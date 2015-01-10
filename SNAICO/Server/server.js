var express = require('express');
var mongoDB = require('mongoskin');
var http = require('http');
var util = require('util');
var GCM = require('gcm').GCM;
var CryptoJS = require("crypto-js");

var apiKey = 'AIzaSyDHAABjgBtL6QysazsAwA7iZGLR8c1P8Y0';
var gcm = new GCM(apiKey);
var db = mongoDB.db('mongodb://localhost:27017/eis1415?auto_reconnect=true', {
	safe: true
});
db.bind("user");
var UserCollection = db.user;
db.bind("company");
var CompanyCollection = db.company;
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
* New User
*/
app.post('/user/new', function(req, res){
	console.log("Incoming POST");
	
	UserCollection.insert({gcmRegId: req.body.gcmRegId,
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
* Check User
*/
app.post('/user/chk', function(req, res){
	console.log("Checking user...");
	var regid = req.body.gcmRegId.toString();
 UserCollection.find({gcmRegId:regid}).toArray(function(err, result){
		if(err){
			next(err);
		}else{ 
			if(result.length > 0){
				console.log("I know him");
				res.writeHead(200, {'Content-Type': 'application/json'});
				var objToJson = { };
				objToJson.response = result;
				res.end(JSON.stringify(objToJson));
			}else{
				console.log("I've never seen");
				res.writeHead(200, {'Content-Type': 'application/json'});
				var objToJson = { };
				objToJson.response = "unknown";
				res.end(JSON.stringify(objToJson));
			}	
 		}
	});	 
});

/*
* Create new Company
*/
app.post('/company/new', function(req, res){
	var companyCode = CryptoJS.SHA256(req.body.companyName+req.body.gcmRegId);
	var regid = req.body.gcmRegId.toString();
	
	companyCode = companyCode.toString();
	companyCode = companyCode.substring(0,6);
	
	CompanyCollection.insert({companyName: req.body.companyName,
							gcmRegidOwner:req.body.gcmRegId,
							companyCode:companyCode}, function(err, service){
		if(err){
			console.log(err);
		}else{
			console.log('Company created!');
			UserCollection.update({gcmRegId:regid}, {$set:{companyCode:companyCode, companyLeader:"true", name: req.body.name}}, function(err, result) {
				if (err){
					console.log(err);
				}else{
					console.log('User updated!');
					res.writeHead(200, {'Content-Type': 'application/json'});
					var objToJson = { };
					objToJson.response = companyCode;
					res.end(JSON.stringify(objToJson));
				}
			});
		}
	});
});

/*
* Join new Company
*/
app.post('/company/join', function(req, res){
	console.log('Try to join Company!');
	var regid = req.body.gcmRegId.toString();
	
	UserCollection.update({gcmRegId:regid}, {$set:{companyCode:req.body.companyCode, companyLeader:"false", name: req.body.name}}, function(err, resultUser) {
		if (err){
			console.log(err);
		}else{
			CompanyCollection.update({companyCode:req.body.companyCode}, {$push:{staff: regid}}, function(err, resultCompany) {
			if (err){
				console.log(err);
			}else{
				 CompanyCollection.find({companyCode:req.body.companyCode}).toArray(function(err, result){
					if(err){
						next(err);
					}else{
						console.log('Company joined!');
						res.writeHead(200, {'Content-Type': 'application/json'});
						var objToJson = { };
						objToJson.response = result[0].companyName;
						res.end(JSON.stringify(objToJson));
						}
					});
				}
			});
		}
	});
});

/*
* Leave Company
*/
app.post('/company/leave', function(req, res){
	console.log('Try to leave Company!');
	var regid = req.body.gcmRegId.toString();
	
	UserCollection.remove({gcmRegId:regid}, function(err, resultUser) {
		if (err){
			console.log(err);
		}else{
			CompanyCollection.update({staff:regid}, {$pull:{staff: regid}}, function(err, resultCompany) {
			if (err){
				console.log(err);
			}else{
					console.log('Company leaved!');
					res.writeHead(200, {'Content-Type': 'application/json'});
					var objToJson = { };
					objToJson.response = "success";
					res.end(JSON.stringify(objToJson));
				}
			});
		}
	});
});



/*
* Bind Server on port 3000
*/
server.listen(3000, function() {
    console.log('Listening on port 3000');
});
