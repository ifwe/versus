var express = require('express');
var router = express.Router();
var categoryDao = require('../dao/categoryDao');

router.get('/', function (req, res) {
	categoryDao.find(function (err, rows) {
		if (err) {
			res.status(500).json("find error");
		} else if (rows.length === 0) {
        	res.status(204).json("No categories found");
        } else {
			res.json(rows);
        }
    });
});

module.exports = router;
