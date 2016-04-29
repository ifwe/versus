var express = require('express');
var router = express.Router();
var topicDao = require('../dao/topicDao');

router.get('/getForNewConversation', function (req, res) {
    if (!req.query.category) {
        res.status(500).json("You did not provide the category!");
    } else {
        var categoryId = req.query.category;
        var fbid = req.query.fbid;

        topicDao.fetchBestForCategory(categoryId, fbid, function (err, rows) {
            if (err) {
                res.status(500).json("fetch best category failed");
            } else if (rows.length > 0) {
                res.json(rows[0]);
            } else {
                topicDao.fetchAllForCategory(categoryId, function (err, rows) {
                    if (err) {
                        res.status(500).json("fetch all for category failed");
                    } else {
                        var i = Math.floor(Math.random() * rows.length);
                        res.json(rows[i]);
                    }
                });
            }
        });

    }
});

router.get('/all', function (req, res) {
    topicDao.find(function (err, rows) {
        if (err) {
            res.status(418).json("failed to update");
        } else {
            res.json(rows);
        }
    });
});

module.exports = router;