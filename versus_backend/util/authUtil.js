var request = require('request');
const FB_VALIDATE_URL = 'https://graph.facebook.com/me?access_token=';

/**
 * Callback for login validation.
 *
 * @callback loginCallback
 * @param {boolean} success - If the login was successful.
 */

/**
 * Validates the user
 *
 * @param {string} fbid - The user facebook id 
 * @param {string} token - Facebook access token
 * @param {loginCallback} callback - The callback on completion
 */
var validateLogin = (fbid, token, callback) => {
	request.get(FB_VALIDATE_URL + token, function (err, response, body) {
	    if (err || response.statusCode !== 200 || JSON.parse(body).id !== fbid) {
	        callback(false);
	    } else {
	    	callback(true);
	    }
	});
};

module.exports = {
	validateLogin : validateLogin
};