var pgUtil = require('../util/pgUtil');

/**
 * Fetch row from db by id
 *
 * @param {number} id - The queue id
 * @param {pgQueryCallback} callback - The callback on completion
 */
var fetch = (id, callback) => {
	pgUtil.execSql("SELECT * FROM queue WHERE id = $1;", [id], callback);
};

/**
 * Get all rows in queue from db
 *
 * @param {pgQueryCallback} callback - The callback on completion
 */
var find = (callback) => {
	pgUtil.execSql("SELECT * FROM queue;", [], callback);
};

/**
 * Add user to queue
 *
 * @param {string} fbid - The user facebook id
 * @param {number} topic_id - The topic id
 * @param {string} side - The topic side the user is on
 * @param {pgQueryCallback} callback - The callback on completion
 */
var add = (fbid, topic_id, side, callback) => {
	pgUtil.execSql(
		"INSERT INTO queue (fbid, topic_id, side) VALUES ($1, $2, $3) " +
		"RETURNING *;",
		[fbid, topic_id, side],
		callback
	);
};

/**
 * Remove row by id
 *
 * @param {number} id - The queue id
 * @param {pgQueryCallback} callback - The callback on completion
 */
var remove = (id, callback) => {
	pgUtil.execSql(
		"DELETE FROM queue WHERE id = $1;",
		[id],
		callback
	);
};

/**
 * Get pending conversations for user
 *
 * @param {string} fbid - The users fbid
 * @param {pgQueryCallback} callback - The callback on completion
 */
var getPendingForUser = (fbid, callback) => {
	pgUtil.execSql(
		"SELECT queue.id as queue_id, fbid, side, categories.id AS category_id, topic_id, side_a, side_b, name AS category_name " +
		"FROM queue " +
			"INNER JOIN topics ON (queue.topic_id=topics.id) " +
			"INNER JOIN categories ON (categories.id=topics.category) " +
		"WHERE queue.fbid = $1;",
		[fbid],
		callback
	);
};

/**
 * Get users in queue for a topic's side
 *
 * @param {string} fbid - The users fbid
 * @param {number} topic_id - The topic id
 * @param {string} side - The users side
 * @param {pgQueryCallback} callback - The callback on completion
 */
var getUsersByTopicSide = (fbid, topic_id, side, callback) => {
	pgUtil.execSql(
		"SELECT queue.*, topics.side_a, topics.side_b, topics.category, topics.side_a_img_url, topics.side_b_img_url " +
		"FROM queue " +
			"INNER JOIN topics ON (queue.topic_id=topics.id) " +
		"WHERE topic_id = $1 AND side = $2 AND fbid != $3;",
		[topic_id, side, fbid],
		callback
	);
};

// TODO : the below methods should really be in the model
var config = require('../util/config');
var userDao = require('../dao/userDao');
var conversationDao = require('../dao/conversationDao');
var pushNotification = require('../util/pushUtil');

/**
 * Callback for queuing an user
 *
 * @callback queueCallback(err, convo)
 * @param {object} err - Created on error, null otherwise
 * @param {object} convo - Conversation object, null on error
 */

/**
 * Logs user in, updates their energy
 * and inserts them into db if new user
 *
 * @param {string} fbid - The user facebook id
 * @param {string} name - The users name
 * @param {string} token - The users access token
 * @param {number} topic_id - The arguments topic id
 * @param {string} side - The users side
 * @param {queueCallback} callback - The callback on completion
 */
var queueOrMatchUser = (fbid, name, token, topic_id, side, callback) => {
	userDao.login(fbid, name, token, function (err, user) {
		if (err) {
			callback(err, null);
		} else if (user.energy === 0) {
			callback({code : config.ERROR_INSUFFICIENT_ENERGY, message : 'not enough energy'}, null);
		} else {
			var last_energy_update = user.last_energy_update;
			if (user.energy === 5) {
				last_energy_update = Date.now();
			}
			userDao.updateUserEnergy(user.fbid, user.energy - 1, last_energy_update, function (err, rows) {
			    if (err) {
			        callback({code : config.ERROR_DATABASE_UPDATE_ENERGY, message : 'energy update failed'}, null);
			    } else {
			        getUsersByTopicSide(fbid, topic_id, side === "side_a" ? "side_b" : "side_a", function (err, rows) {
			            if (err) {
			                // TODO : give user back energy
			                callback({code : config.ERROR_DATABASE_FIND, message : 'find failed'}, null);
			            } else if (rows.length > 0) {
			                var matchedUser = rows[0];
			                var user_a = side === "side_a" ? fbid : matchedUser.fbid;
			                var user_b = side === "side_a" ? matchedUser.fbid : fbid;
			                var pubnub_room = matchedUser.id;

			                remove(matchedUser.id, function (err, rows) {
			                    if (err) {
			                        // TODO : give user back energy
			                        callback({code : config.ERROR_DATABASE_DELETE, message : 'delete failed'}, null);
			                    } else {
			                        conversationDao.create(topic_id, user_a, user_b, pubnub_room, function (err, rows) {
			                            if (err) {
			                                // TODO : give user back energy and put matched user back in queue
			                                callback({code : config.ERROR_DATABASE_INSERT, message : 'create failed'}, null);
			                            } else {
			                            	var convo = rows[0];
			                                var ret = {
			                                    "room_name" : convo.pubnub_room,
			                                    "topic_id" : convo.topic_id,
			                                    "user_a" : convo.user_a,
			                                    "user_b" : convo.user_b,
			                                    "side_a" : matchedUser.side_a,
			                                    "side_b" : matchedUser.side_b,
			                                    "end_time" : Date.parse(convo.start_time) + config.CONVERSATION_TIME_LIMIT_EPOCH
			                                };

			                                pushNotification.send(matchedUser.fbid, ret);
			                                callback(null, ret);
			                            }
			                        });
			                    }
			                });
			            } else if (rows.length === 0) {
			                var user_a = side === "side_a" ? fbid : null;
			                var user_b = side === "side_a" ? null : fbid;

			                add(fbid, topic_id, side, function (err, rows) {
			                    if (err) {
			                        // TODO : give user back energy
			                        callback({code : config.ERROR_DATABASE_INSERT, message : 'add failed'}, null);
			                    } else {
			                        callback(null, {
			                            "room_name" : rows[0].id,
			                            "topic_id" : topic_id,
			                            "user_a" : user_a,
			                            "user_b" : user_b,
			                            "end_time" : null
			                        });
			                    }
			                });
			            }
			        });
			    }
			});
		}
	});
};

module.exports = {
	fetch : fetch,
	find : find,
	add : add,
	remove : remove,
	getUsersByTopicSide : getUsersByTopicSide,
	getPendingForUser : getPendingForUser,
	queueOrMatchUser : queueOrMatchUser
};
