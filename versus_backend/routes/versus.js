var express = require('express');
var router = express.Router();
var versionDao = require('../dao/versionDao');

router.get('/', function (req, res) {
    res.status(200).json("connected");
});

router.get('/version', function (req, res) {
	var current = req.query.version;

	if (current) {
		versionDao.check(current, function(err, version) {
			if (err) {
				res.status(500).json(err);
			} else if (!version) {
				res.status(404).json("version not found");
			} else {
				res.status(200).json(version);
			}
		});
	} else {
		versionDao.fetch(function(err, rows) {
			if (err) {
				res.status(500).json(err);
			} else if (rows.length == 0){
				res.status(404).json("no versions found");
			} else {
				res.status(200).json(rows);
			}
		});
	}
});

module.exports = router;
