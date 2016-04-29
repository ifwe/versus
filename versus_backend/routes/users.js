var express = require('express');
var router = express.Router();
var userDao = require('../dao/userDao');
var config = require('../util/config');

router.post('/login', function (req, res) {
    var fbid = req.body.fbid;
    var name = req.body.name;
    var token = req.body.token;

    userDao.login(fbid, name, token, function (err, user) {
        if (err) {
            config.outputError(err, res);
        } else {
            res.json(user);
        }
    });
});

router.get('/', function (req, res) {
	userDao.find(function (err, rows) {
        if (err) {
            config.outputError(err, res);
        } else {
            res.json(rows);
        }
    });
});

router.get('/getUser', function (req, res) {
    var fbid = req.query.fbid;
    var name = req.query.name;
    var token = req.query.token;

    userDao.login(fbid, name, token, function (err, user) {
        if (err) {
            config.outputError(err, res);
        } else {
            res.json(user);
        }
    });
});

module.exports = router;
