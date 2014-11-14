var express = require('express');
var mongoDB = require('mongoskin');
var http = require('http');
var util = require('util');
var GCM = require('gcm').GCM;

var apiKey = 'AIzaSyDHAABjgBtL6QysazsAwA7iZGLR8c1P8Y0';
var gcm = new GCM(apiKey);
var db = mongoDB.db('mongodb://localhost:27017/eis_poc01?auto_reconnect=true', {
	safe: true
});
db.bind("eis_poc01");
var PostCollection = db.eis_poc01
var app = express();
var server = http.createServer(app);
app.use(express.json());
app.use(express.urlencoded());
app.use(function(err, req, res, next){
	console.err(error.stack);
	res.end(error.messages);
});	

/*
* Incoming POST
*/
app.post('/post/', function(req, res){
	console.log("Incoming POST");
	PostCollection.insert({date: req.body.date,
							message: req.body.message}, function(err, service){
		if(err){
			console.log(err);
		}else{
			res.writeHead(200, {'Content-Type': 'text/plain'});
			res.end("success");
		}
	});
});

/*
* Get and Push
*/
app.post('/gcm/', function(req, res){
	console.log("Incoming RegID: "+req.body.regid);
	
	var message = {
    registration_id: req.body.regid, // required
    collapse_key: 'Collapse key', 
    'data.key1': 'value1',
    'data.key2': 'value2'
	};

	gcm.send(message, function(err, messageId){
		if (err) {
			console.log("Something has gone wrong!");
		} else {
			console.log("Sent with message ID: ", messageId);
		}
	});
	
	
	res.writeHead(200, {'Content-Type': 'text/plain'});
	res.end("success");
});

/*
* Bind Server on port 3000
*/
server.listen(3000, function() {
    console.log('Listening on port 3000');
});
