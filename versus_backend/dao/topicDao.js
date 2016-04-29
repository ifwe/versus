var pgUtil = require('../util/pgUtil');

/**
 * Gets most appropriate topic for category
 *
 * @param {number} id - The category id
 * @param {queueCallback} callback - The callback on completion
 */
var fetchBestForCategory = (categoryId, fbid, callback) => {
	var query = "SELECT topics.* " + 
				"FROM topics " +
					"INNER JOIN queue ON topics.id=queue.topic_id " +
				"WHERE topics.category=$1 AND queue.fbid!=$2 " +
				"ORDER BY queue.entry_time " +
				"LIMIT 1;";
	pgUtil.execSql(query, [categoryId, fbid], callback);
};

/**
 * Gets all topics for category
 *
 * @param {number} id - The category id
 * @param {queueCallback} callback - The callback on completion
 */
var fetchAllForCategory = (id, callback) => {
	pgUtil.execSql("SELECT * FROM topics WHERE category = $1", [id], callback);
};

/**
 * Get all rows in queue from db
 *
 * @param {pgQueryCallback} callback - The callback on completion
 */
var find = (callback) => {
	pgUtil.execSql("SELECT * FROM topics", [], callback);
};

module.exports = {
	find : find,
	fetchBestForCategory : fetchBestForCategory,
	fetchAllForCategory : fetchAllForCategory
};