var config = require('../util/config');
var pgUtil = require('../util/pgUtil');
var auth = require('../util/authUtil');

/**
 * Fetch user from db by fbid
 *
 * @param {string} fbid - The user facebook id
 * @param {pgQueryCallback} callback - The callback on completion
 */
var fetch = (fbid, callback) => {
	pgUtil.execSql("SELECT * FROM users WHERE fbid = $1;", [fbid], callback);
};

/**
 * Get all users from db
 *
 * @param {pgQueryCallback} callback - The callback on completion
 */
var find = (callback) => {
	pgUtil.execSql("SELECT * FROM users;", [], callback);
};

/**
 * Create user row in db
 *
 * @param {string} fbid - The user facebook id
 * @param {string} name - The users name
 * @param {pgQueryCallback} callback - The callback on completion
 */
var create = (fbid, name, callback) => {
	pgUtil.execSql(
		"INSERT INTO users (fbid, name, energy, last_energy_update) VALUES ($1, $2, 5, $3) " +
		"RETURNING *;",
		[fbid, name, Date.now()],
		callback
	);
};

/**
 * Updates the users energy
 *
 * @param {string} fbid - The user facebook id
 * @param {number} energy - The users new energy
 * @param {epoch} lastEnergyUpdate - The end time of the last update window
 * @param {pgQueryCallback} callback - The callback on completion
 */
var updateUserEnergy = (fbid, energy, lastEnergyUpdate, callback) => {
	pgUtil.execSql(
		"UPDATE users " +
		"SET energy=$2, last_energy_update=$3 " +
		"WHERE fbid=$1 " +
		"RETURNING *;",
		[fbid, energy, lastEnergyUpdate],
		callback
	);
};

// TODO : the below methods should really be in the model

/**
 * Callback for user objects
 *
 * @callback userCallback(err, user)
 * @param {object} err - Created on error, null otherwise
 * @param {object} user - User object, null on error
 */

/**
 * Logs user in, updates their energy
 * and inserts them into db if new user
 *
 * @param {string} fbid - The user facebook id
 * @param {string} name - The users name
 * @param {string} token - The users access token
 * @param {userCallback} callback - The callback on completion
 */
var login = (fbid, name, token, callback) => {
	auth.validateLogin(fbid, token, function (success) {
        if (success) {
            fetch(fbid, function (err, rows) {
                if (err) {
                    callback({code : config.ERROR_DATABASE_SELECT, message : 'fetch failed'}, null);
                } else if (rows.length > 0) { // existing user
                    syncEnergyAndFetch(rows[0], function (err, rows) {
                        if (err) {
                            callback(err, null);
                        } else {
                        	var user = rows[0];
                        	var nextUpdate = parseInt(user.last_energy_update) + config.ENERGY_REFRESH_INTERVAL;
                        	user.next_energy_update = user.energy < 5 ? nextUpdate.toString() : "0";

                            callback(null, user);
                        }
                    });
                } else { // new user
                    create(fbid, name, function (err, rows) {
                        if (err) {
                            callback({code : config.ERROR_DATABASE_INSERT, message : 'create failed'}, null);
                        } else {
                        	rows[0].next_energy_update = "0";
                            callback(null, rows[0]);
                        }
                    });
                }
            });
        } else {
        	callback({code : config.ERROR_INVALID_CREDENTIALS, message : 'invalid fbid or token'}, null);
        }
    });
};

/**
 * Syncs the users energy
 *
 * @param {string} fbid - The user facebook id
 * @param {pgQueryCallback} callback - The callback on completion
 */
var syncEnergyAndFetch = (user, callback) => {
	var gainedEnergy = Math.floor((Date.now() - user.last_energy_update) / config.ENERGY_REFRESH_INTERVAL);

	if (user.energy >= 5 || gainedEnergy === 0) {
		callback(null, [user]);
	} else {
	    var energy = user.energy + gainedEnergy;
		var lastEnergyUpdate = Date.now() - ((Date.now() - user.last_energy_update) % config.ENERGY_REFRESH_INTERVAL);

		updateUserEnergy(user.fbid, energy, lastEnergyUpdate, function (err, rows) {
			if (err) {
				callback({code : config.ERROR_DATABASE_UPDATE_ENERGY, message : 'energy update failed'}, null);
			} else {
				callback(null, rows);
			}
		});
	}
};

var rewardForReview = (fbid, callback) => {
	fetch(fbid, function (err, rows) {
	    if (err) {
	        callback({code : config.ERROR_DATABASE_SELECT, message : 'fetch failed'}, null);
	    } else { // existing user
	        var user = rows[0];

	        updateUserEnergy(user.fbid, user.energy + 1, user.last_energy_update, function (err, rows) {
        		if (err) {
        			callback({code : config.ERROR_DATABASE_UPDATE_ENERGY, message : 'energy update failed'}, null);
        		} else {
        			callback(null, rows[0]);
        		}
        	});
        }
	});
};

module.exports = {
	fetch : fetch,
	find : find,
	create : create,
	updateUserEnergy : updateUserEnergy,
	syncEnergyAndFetch : syncEnergyAndFetch,
	login : login,
	rewardForReview : rewardForReview
};
