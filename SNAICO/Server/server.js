var express = require('express');
var mongoDB = require('mongoskin');
var http = require('http');
var util = require('util');
var GCM = require('gcm').GCM;

var apiKey = 'AIzaSyDHAABjgBtL6QysazsAwA7iZGLR8c1P8Y0';
var gcm = new GCM(apiKey);
var db = mongoDB.db('mongodb://localhost:27017/eis1415?auto_reconnect=true', {
	safe: true
});
db.bind("eis1415");
var UserCollection = db.eis1415
var app = express();
var server = http.createServer(app);
var BSON = mongoDB.BSONPure;
app.use(express.json());
app.use(express.urlencoded());
app.use(function(err, req, res, next){
	console.err(error.stack);
	res.end(error.messages);
});	

/*
* Incoming POST
*/
app.post('/user/new', function(req, res){
	console.log("Incoming POST");
	
	UserCollection.insert({gcmRegid: req.body.gcmRegId,
							date: req.body.date}, function(err, service){
		if(err){
			console.log(err);
		}else{
			console.log("Welcome");
			res.writeHead(200, {'Content-Type': 'application/json'});
			var objToJson = { };
			objToJson.response = "success";
			res.end(JSON.stringify(objToJson));
		}
	});
});

/*
* ChkUser
*/
app.post('/user/chk', function(req, res){
	console.log("Checking user...");
	var regid = req.body.gcmRegId.toString();
 UserCollection.find({gcmRegid:regid}).toArray(function(err, result){
		if(err){
			next(err);
		}else{ 
			if(result.length > 0){
				console.log("I know him");
				res.writeHead(200, {'Content-Type': 'application/json'});
				var objToJson = { };
				objToJson.response = "known";
				res.end(JSON.stringify(objToJson));
			}else{
				console.log("I've never seen");
				res.writeHead(200, {'Content-Type': 'application/json'});
				var objToJson = { };
				objToJson.response = "unknown";
				console.log(JSON.stringify(objToJson));
				res.end(JSON.stringify(objToJson));
			}	
 		}
	});	 
});

/*
* Bind Server on port 3000
*/
server.listen(3000, function() {
    console.log('Listening on port 3000');
});
