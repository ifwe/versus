var express = require('express');
var config = require('./util/config');
var app = express();
var pgUtil = require('./util/pgUtil');
var bodyParser = require('body-parser');
app.use(bodyParser.json());       // to support JSON-encoded bodies

// routes ======================================================================
app.use('/users', require('./routes/users'));
app.use('/categories', require('./routes/categories'));
app.use('/conversations', require('./routes/conversations'));
app.use('/queue', require('./routes/queue'));
app.use('/topics', require('./routes/topics'));
app.use('/versus', require('./routes/versus'));

// launch ======================================================================
app.listen(config.PORT, function () {
  	console.log('listening on port ' + config.PORT);
});
