var pgUtil = require('../util/pgUtil');

/**
 * Get all rows in queue from db
 *
 * @param {pgQueryCallback} callback - The callback on completion
 */
var find = (callback) => {
	var query = "SELECT * FROM categories";
	pgUtil.execSql(query, [], callback);
};

module.exports = {
	find : find
};