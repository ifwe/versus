var express = require('express');
var router = express.Router();
var queueDao = require('../dao/queueDao');
var config = require('../util/config');

router.post('/addOrMatch', function (req, res) {
    var fbid = req.body.fbid;
    var name = req.body.name || "name not provided";
    var topic_id = req.body.topic_id;
    var side = req.body.side;
    var token = req.body.token;

    queueDao.queueOrMatchUser(fbid, name, token, topic_id, side, function (err, convo) {
        if (err) {
            config.outputError(err, res);
        } else {
            res.json(convo);
        }
    });
});

router.get('/', function (req, res) {
	queueDao.find(function (err, rows) {
        res.send(rows);
    });
});

module.exports = router;
