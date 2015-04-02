var express = require('express')
  , routes = require('./routes')
  , http = require('http')
  , path = require('path');

var app = express();

// all environments
// all environments
var port = (process.env.VCAP_APP_PORT || 3000);
var host = (process.env.VCAP_APP_HOST || 'localhost');
app.set('port', port);
app.set('views', __dirname + '/views');
app.set('view engine', 'ejs');
app.use(express.favicon());
app.use(express.logger('dev'));
app.use(express.bodyParser());
app.use(express.methodOverride());
app.use(app.router);
app.use(require('stylus').middleware(__dirname + '/public'));
app.use(express.static(path.join(__dirname, 'public')));

// development only
if ('development' == app.get('env')) {
  app.use(express.errorHandler());
}

var cloudantCreds = {
	"username" : null,
	"password" : null,
	"url" : null
} ;

var maximoCreds = {
	"username" : null,
	"password" : null,
	"url" : null
};

var iotCreds = {
	"apiKey" : null,
	"apiToken" : null,
	"url" : null
};

	//get the URL from Userdefined properties & add the '/' if not present
if(!process.env.runtimeapp) {
		console.log("The runtimeapp variable is not set. Please set it in the USER-DEFINED property.");
		process.exit(1);
}
var runtimeUrl = process.env.runtimeapp.replace(/\/?$/, '/');

var IotAssetManager = require('./iotAssetManager').IotAssetManager;
var iotAssetManagerService = new IotAssetManager(app);

var IotAssetProvider = require('./iotAssetProvider').IotAssetProvider;
var iotAssetProvider = new IotAssetProvider(runtimeUrl);
app.set('iotAssetProvider', iotAssetProvider);


app.get('/iotfOrgs',function(req,res){
    iotAssetProvider.getMaximo2IotfOrgConfiguration(req.query.maximoOrg, 
					function(iotfOrgs) {
						console.log("iotfOrgs", iotfOrgs);
						res.send(iotfOrgs);	
					});
});

app.get('/iotf',function(req,res){
	iotAssetProvider.getIotDevices(iotCreds.apiKey, iotCreds.apiToken, function(iotdevices) { 
				res.send(iotdevices);	
	});	   
});

app.get('/triggeredactions',function(req,res){  

		   iotAssetProvider.retrieveTriggeredRuleActions(cloudantCreds, req.query.deviceID, function(triggeredRuleActions) { 
					res.send(triggeredRuleActions);	
			});
		

});

 
app.get('/maximo',function(req,res){	  
       console.log("maximo asset request", req.query.maximoOrganization);
	   iotAssetProvider.getMaximoDetailAssetListings(maximoCreds, req.query.maximoOrganization, function(assets) { 
				res.send(assets);	
		});	   
});
	   
app.get('/maximoOrganizations',function(req,res){	  
	   iotAssetProvider.getMaximoOrganizations(maximoCreds, function(maximoOrganizations) { 
				res.send(maximoOrganizations);	
		});	   
});
	   
	   
app.get('/', routes.index);	 

function init() {

	console.log("init called. Fetching details of Cloudant and Maximo");

	if(process.env.VCAP_SERVICES) {

		var env = JSON.parse(process.env.VCAP_SERVICES);
		console.log("VCAP services found, Fetching creds");

		for (var service in env) {
			
			if(service == "cloudantNoSQLDB") {
				cloudantCreds.url = env[service][0]['credentials'].host;
				cloudantCreds.username = env[service][0]['credentials'].username;
				cloudantCreds.password = env[service][0]['credentials'].password;
			}
			if(service == "iotf-service") {
				iotCreds.url = env[service][0]['credentials'].base_uri;
				iotCreds.apiKey = env[service][0]['credentials'].apiKey;
				iotCreds.apiToken = env[service][0]['credentials'].apiToken;
			}
		}
	}
	//get the maximo creds from the Cloudant.

	var client = require('node-rest-client').Client;

	var maximoEntry = "/thingsmaxactionmappings/maximo";
	var cloudantUrl = "https://"+cloudantCreds.url + maximoEntry;
	

	var args = {
			headers:{"Content-Type": "application/json"} 
		};


	var options_auth={user:cloudantCreds.username,
						 password:cloudantCreds.password};

	var restclient = new client(options_auth);

	restclient.get(cloudantUrl, args,							
				function(restdata, response) {
					console.log("Got response for Maximo Creds");				
					var actionCall = JSON.parse(restdata);

					maximoCreds.url = actionCall.thingsActionURL;
					maximoCreds.username = actionCall.thingsActionUserId;
					maximoCreds.password = actionCall.thingsActionPassword;

					console.log(JSON.stringify(maximoCreds));

				})
				.on('error',function(err){
					console.log('something went wrong on the request', err.request.options);;			
				});
};

init();

http.createServer(app).listen(app.get('port'), function(){
  console.log('Express server listening on port ' + app.get('port'));
});