var pg = require('pg');
var config = require('./config');
const CONNECTION_STRING = "postgres://" + config.DB_USER + ":" + config.DB_PASSWORD + "@" + config.DB_HOST + ":" + config.DB_PORT + "/" + config.DB_NAME;

/**
 * Callback for login validation.
 *
 * @callback pgQueryCallback(err, rows)
 * @param {object} err - Created on error, null otherwise
 * @param {object} rows - Database result rows, null on error
 */

/**
 * Executes a sql query
 *
 * @param {string} query - The query string
 * @param {array} params - The query parameters
 * @param {pgQueryCallback} callback - The callback on completion
 */
var execSql = (query, params, callback) => {
	pg.connect(CONNECTION_STRING, function(err, client, done) {
		if (err) {
		    console.log('Failed to connect to postgres db');
		    callback(err, null);
		}

		client.query(query, params, function(error, result) {
	        done();
	        if (error) {
	        	callback(error, null);
	        } else {
	        	callback(null, result.rows);
	        }
		});
	});
};

module.exports = {
	execSql : execSql
};