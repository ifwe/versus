var express = require('express');
var router = express.Router();
var auth = require('../util/authUtil');
var queueDao = require('../dao/queueDao');
var userDao = require('../dao/userDao');
var conversationDao = require('../dao/conversationDao');
var config = require('../util/config');

router.get('/', function (req, res) {
	conversationDao.find(function (err, rows) {
        res.json(rows);
    });
});

// TODO : add error checking for this
router.get('/getForUser', function (req, res) {
    var fbid = req.query.fbid;
    var token = req.query.token;
    var states = req.query.states;
    var includePending = req.query.includePending;

    conversationDao.getForUser(fbid, token, states, function (err, pairedConversations) {
        var output = [];
        pairedConversations = pairedConversations || [];
        pairedConversations.forEach(convo => output.push(_outputPairedConvo(convo, fbid)));
        
        if (includePending === "true") {
            queueDao.getPendingForUser(fbid, function (err, pendingConversations) {
                pendingConversations = pendingConversations || [];
                pendingConversations.forEach(convo => output.push(_outputPendingConvo(convo)));
                
                res.json(output);
            });
        } else {
            res.json(output);
        }
    });
});

router.get('/getForJudge', function (req, res) {
    var fbid = String(req.query.fbid);
    
    if (!fbid){
        res.status(500).json("did not send fbid!")
    } else{
        conversationDao.getForJudge(fbid, function (err, rows) {
            if (err) {
                res.status(500).json(err);
            } else if (rows.length === 0) {
                res.status(204).json("no convos to judge");
            } else {
                res.json(_outputPairedConvo(rows[0]));
            }
        });
    }
});

router.post('/updateScore', function (req, res) {
    var fbid = req.body.fbid;
    var score_a = req.body.score_a;
    var score_b = req.body.score_b;
    var pubnub_room = req.body.pubnub_room;

    if(!(fbid && (score_b || score_b === 0) && (score_a || score_a === 0) && pubnub_room)) {
        res.status(500).json("missing parameters in body");
    } else {
        conversationDao.updateConversationScore(score_a, score_b, pubnub_room, function (err, rows) {
            if (err) {
                res.status(500).json(err);
            } else {
                conversationDao.updateReviews(fbid, score_a, score_b, pubnub_room, function (err, rows) {
                    if (err) {
                        res.status(500).json(err);
                    } else {
                        userDao.rewardForReview(fbid, function (err, rows) {
                            if (err) {
                                res.status(500).json("failure giving user reward");
                            } else {
                                res.status(200).json("update score success!");
                            }
                        });
                    }
                })
            }
        });
    }
});

let _outputPendingConvo = (convo) => {
    var output = {
        "room_name" : convo.queue_id,
        "topic" : {
            "id" : convo.topic_id,
            "side_a" : convo.side_a,
            "side_b" : convo.side_b
        },
        "category" : {
            "id" : convo.category_id,
            "name" : convo.category_name,
        },
        "user_a" : convo.side === "side_a" ? convo.fbid : null,
        "user_b" : convo.side === "side_b" ? convo.fbid : null,
        "status" : "pending",
        "start_time" : null,
        "end_time" : null
    };

    return output;
}

let _outputPairedConvo = (convo, fbid) => {
    var status = "";
    var result = "";
    var epochStartTime = Date.parse(convo.start_time);

    if (epochStartTime + config.CONVERSATION_TIME_LIMIT_EPOCH > Date.now()) {
        status = "active";
    } else if (epochStartTime + config.CONVERSATION_TIME_LIMIT_EPOCH < Date.now() && convo.score_a + convo.score_b < 25) {
        status = "review";
    } else if (epochStartTime + config.CONVERSATION_TIME_LIMIT_EPOCH < Date.now() && convo.score_a + convo.score_b >= 25) {
        status = "done";

        if (convo.score_a > convo.score_b) {
            if (fbid === convo.user_a) {
                result = "win";
            } else {
                result = "loss";
            }
        } else if (convo.score_a < convo.score_b) {
            if (fbid === convo.user_b) {
                result = "win";
            } else {
                result = "loss";
            }
        } else {
            result = "draw";
        }
    }

    var output = {
        "room_name" : convo.pubnub_room,
        "topic" : {
            "id" : convo.topic_id,
            "side_a" : convo.side_a,
            "side_b" : convo.side_b
        },
        "category" : {
            "id" : convo.category_id,
            "name" : convo.category_name,
        },
        "user_a" : convo.user_a,
        "user_b" : convo.user_b,
        "score_a" : convo.score_a,
        "score_b" : convo.score_b,
        "status" : status,
        "start_time" : epochStartTime,
        "result" : result,
        "end_time" : Date.parse(convo.start_time) + config.CONVERSATION_TIME_LIMIT_EPOCH //epoch time
    };

    return output;
}

module.exports = router;
