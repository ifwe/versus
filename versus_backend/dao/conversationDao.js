var pgUtil = require('../util/pgUtil');

/**
 * Fetch row from db by id
 *
 * @param {number} id - The queue id
 * @param {pgQueryCallback} callback - The callback on completion
 */
var fetch = (id, callback) => {
	pgUtil.execSql("SELECT * FROM conversations WHERE id = $1;", [id], callback);
};

/**
 * Get all rows in queue from db
 *
 * @param {pgQueryCallback} callback - The callback on completion
 */
var find = (callback) => {
	pgUtil.execSql("SELECT * FROM conversations;", [], callback);
};

/**
 * Gets conversations for user by state
 *
 * @param {string} fbid - The user facebook id
 * @param {string} token - The users access token
 * @param {array} states - The conversations states
 * @param {pgQueryCallback} callback - The callback on completion
 */
var getForUser = (fbid, token, states, callback) => {
	var statusQuery = "";

	states.forEach(function (state) {
		if (statusQuery !== "") {
			statusQuery += " OR ";
		}

		switch (state) {
			case "active":
				statusQuery += "(conversations.start_time + interval '2 hour' > current_timestamp)";
				break;
			case "review":
				statusQuery += "(conversations.start_time + interval '2 hour' < current_timestamp AND conversations.score_a + conversations.score_b < 25)";
				break;
			case "done":
				statusQuery += "(conversations.score_a + conversations.score_b >= 25)";
				break;
		}
	});

	pgUtil.execSql(
		"SELECT topic_id, user_a, user_b, side_a, side_b, score_a, score_b, categories.id AS category_id, name as category_name, pubnub_room, start_time " +
		"FROM conversations " +
			"INNER JOIN topics ON (conversations.topic_id=topics.id) " +
			"INNER JOIN categories ON (categories.id=topics.category) " +
		"WHERE (conversations.user_a = $1 OR conversations.user_b = $1) AND (" + statusQuery + ");",
		[fbid],
		callback
	);
};

/**
 * Gets a conversation to judge
 *
 * @param {pgQueryCallback} callback - The callback on completion
 */
var getForJudge = (fbid, callback) => {
	pgUtil.execSql(
		"SELECT topic_id, user_a, user_b, side_a, side_b, conversations.score_a, conversations.score_b, categories.id AS category_id, name as category_name, conversations.pubnub_room, start_time " +
		"FROM conversations " +
			"INNER JOIN topics ON (conversations.topic_id=topics.id) " +
			"INNER JOIN categories ON (categories.id=topics.category) " +
		"WHERE conversations.pubnub_room NOT IN " +
				"(SELECT pubnub_room " +
					"FROM reviews " +
				"WHERE reviews.fbid = $1) " +
			"AND (conversations.start_time + interval '2 hour' < current_timestamp) " +
			"AND (conversations.score_a + conversations.score_b < 25) " +
			"AND (conversations.user_a != $1) AND (conversations.user_b != $1) " +
		"LIMIT 1;",
		[fbid],
		callback
	);
};

/**
 * Creates a conversation
 *
 * @param {number} topic_id - The topic id
 * @param {string} user_a - The user on side a
 * @param {string} user_b - The user on side b
 * @param {number} pubnub_room - The sub/pub channel for pubnub
 * @param {pgQueryCallback} callback - The callback on completion
 */
var create = (topic_id, user_a, user_b, pubnub_room, callback) => {
	pgUtil.execSql(
		"INSERT INTO conversations (topic_id, user_a, user_b, pubnub_room) VALUES ($1, $2, $3, $4) " +
		"RETURNING *;",
		[topic_id, user_a, user_b, pubnub_room],
		callback
	);
};

var updateConversationScore = (score_a, score_b, pubnub_room, callback) => {
	pgUtil.execSql(
		"UPDATE conversations SET score_a=score_a+$1, score_b=score_b+$2 WHERE pubnub_room=$3;",
		[score_a, score_b, pubnub_room],
		callback
	);
};

var updateReviews = (fbid, score_a, score_b, pubnub_room, callback) => {
	pubnub_room = String(pubnub_room);
	pgUtil.execSql(
		"INSERT INTO reviews(fbid, score_a, score_b, pubnub_room) VALUES ($1, $2, $3, $4);",
		[fbid, score_a, score_b, pubnub_room],
		callback
	);
};

module.exports = {
	fetch : fetch,
	find : find,
	create : create,
	getForUser : getForUser,
	getForJudge : getForJudge,
	updateConversationScore : updateConversationScore,
	updateReviews: updateReviews
};
