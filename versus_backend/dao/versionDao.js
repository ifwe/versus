var pgUtil = require('../util/pgUtil');

/**
 * Fetch latest version from db 
 *
 * @param {pgQueryCallback} callback - The callback on completion
 */
var fetch = (callback) => {
	var query = "SELECT * FROM versions ORDER BY code DESC LIMIT 1";
	pgUtil.execSql(query, [], callback);
};

var check = (version, callback) => {
	var query = "SELECT * FROM versions WHERE code > $1 ORDER BY code DESC";
	pgUtil.execSql(query, [version], function(err, rows) {
		if (err) {
			callback(err, null);
		} else if (rows.length == 0) {
			callback(err, {code:version,upgrade_required:false});
		} else {
			var upgradeRequired = false;
			for (var i = 0; i < rows.length; i++) {
				if (rows[i].upgrade_required) {
					upgradeRequired = true;
					break;
				}
			}
			rows[0].upgrade_required = upgradeRequired;
			callback(err, rows[0]);
		}
	});
}

module.exports = {
	fetch : fetch,
	check : check
};
