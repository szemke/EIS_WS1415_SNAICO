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
app.post('/user', function(req, res){
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
app.get('/user/:id', function(req, res){
	console.log("Checking user..." +req.param("id"));
 UserCollection.find({gcmRegId:req.param("id")}).toArray(function(err, result){
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
app.post('/company', function(req, res){
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
* Join Company
*/
app.put('/company/:companycode/staff/:gcmregid', function(req, res){
	console.log('Try to join Company!');
	UserCollection.update({gcmRegId:req.param("gcmregid")}, {$set:{companyCode:req.param("companycode"), companyLeader:"false"}}, function(err, resultUser) {
		if (err){
			console.log(err);
		}else{
			CompanyCollection.update({companyCode:req.param("companycode")}, {$push:{staff: req.param("gcmregid")}}, function(err, resultCompany) {
			if (err){
				console.log(err);
			}else{
				 CompanyCollection.find({companyCode:req.param("companycode")}).toArray(function(err, result){
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
app.delete('/company/:companycode/staff/:gcmregid', function(req, res){
	console.log('Try to leave Company!');
	UserCollection.remove({gcmRegId:req.param("gcmregid")}, function(err, resultUser) {
		if (err){
			console.log(err);
		}else{
			CompanyCollection.update({companyCode:req.param("companycode")}, {$pull:{staff: regid}}, function(err, resultCompany) {
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
* Get All Company member
*/
app.get('/company/:companycode/staff', function(req, res){
	console.log("Search for member..."+req.param("companycode"));
		UserCollection.find({companyCode: req.param("companycode")}).toArray(function(err, resultUser){
			if(err){
				next(err);
			}else{
				var staff = [];
				var objToJson = { };
				for(i = 0; i < resultUser.length; i++){						
					var mitarbeiter = { };
					mitarbeiter.name = resultUser[i].name;
					mitarbeiter.gcmRegId = resultUser[i].gcmRegId;
					staff[i] = mitarbeiter;
				}
				console.log("staff: "+util.inspect(staff));
				objToJson.staff = staff;

				console.log('sending member object...'+util.inspect(objToJson));
				res.writeHead(200, {'Content-Type': 'application/json'});
				res.end(JSON.stringify(objToJson));
			}
		});
});	 


/*
* Get all jobs from company
*/
app.get('/company/:companycode/job', function(req, res){
console.log('Getting Jobs...');
	CompanyCollection.find({companyCode:req.param("companycode")}).toArray(function(err, resultCompany){
		if(err){
			next(err);
		}else{
			console.log('Sending job object...'+util.inspect(resultCompany[0].job));
			res.writeHead(200, {'Content-Type': 'application/json'});
			var objToJson = { };
			objToJson.response = resultCompany[0].job;
			res.end(JSON.stringify(objToJson));	
		}	
	});	 
});

/*
* New Job
*/
app.post('/company/:companycode/job', function(req, res){

	var timestamp = Date.now() / 1000 | 0;
	var jobCode = CryptoJS.SHA256(timestamp+req.body.jobActivity+req.body.jobAddress+req.body.jobStaffMemberGcmRegId);
		
	jobCode = jobCode.toString();
	jobCode = jobCode.substring(0,6);
	
	console.log('New Job'+timestamp);
	CompanyCollection.update(
		{companyCode: req.param("companycode")}, 
			{$push:
				{job:{
						"jobCode":jobCode,
						"jobAddress":req.body.jobAddress,
						"jobActivity":req.body.jobActivity,
						"jobStaffMemberGcmRegId":req.body.jobStaffMemberGcmRegId,
						"jobComment":[
										{
											"jobStaffMemberGcmRegId":req.body.gcmRegId, 
											"Comment":req.body.jobComment
										}
									]
					}
				}
			}, function(err, result) {
		if (err){
			console.log(err);
		}else{
			var message = {
				registration_id: req.body.jobStaffMemberGcmRegId, // required
				collapse_key: jobCode
			};

			gcm.send(message, function(err, messageId){
				if (err) {
					console.log("Something has gone wrong!" +err+req.body.jobStaffMemberGcmRegId);
				} else {
					console.log("Sent with message ID: ", messageId +"to "+req.body.jobStaffMemberGcmRegId);
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
