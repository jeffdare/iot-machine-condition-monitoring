var Client = require('node-rest-client').Client;
var client = new Client();

var runtimeUrl = process.env.runtimeapp.replace(/\/?$/, '/');
console.log("Console log" + runtimeUrl);
	   
IotAssetProvider = function(runtimeUrl1) {
	
	this.getMaximo2IotfOrgConfiguration = function (maximoOrg, cb) {	
		var Maximo2IotfOrgConfiguration = require('./maximo2IotfOrgConfiguration.js').Maximo2IotfOrgConfiguration;
		var maximo2IotfOrgConfiguration = new Maximo2IotfOrgConfiguration();
		maximo2IotfOrgConfiguration.getconfigurations(maximoOrg, cb);			
   };
	
  this.fetchAssetMapping = function(req, cb) {       	   
	   var assetid = (req.query != null) ? req.query.assetid : "";
	   var resturl = runtimeUrl+"assetMappings/assets/" + assetid ;
	   console.log('rest url to thingsmax', resturl);		
	   client.get(resturl,
			function(restdata, response) {				
				console.log('rest response', restdata);		
				cb(null, restdata);
			});			   
	};	
	
	this.getIotDevices = function(appkey, apptoken, callback){
		
		    //local testing, set it to true for testing with stubbed local data
			var localtesting = false;
			if(localtesting == true)
				callback(iotdevices);
			else
				getIotDevicesfromIoTF(appkey, apptoken, callback);
	};
	
	this.insertAssetMapping = function(assetMapping, cb) {
		console.log('inserting assetMapping: ' + assetMapping);
		var resturl = runtimeUrl+"assetMappings/assets/"
		var args = {
			data: assetMapping,
			headers:{"Content-Type": "application/json"} 
		};
	    client.post(resturl, args,
			function(restdata, response) {						
					console.log('result of inserting assetMapping: ' + restdata);
					cb(null, restdata);
				});			
	};
	
	this.deleteAssetMapping = function(id, cb) {
       var resturl = runtimeUrl+"assetMappings/assets/" + id;	   
	   console.log('delete request for asset ID', id);		
	   client.delete(resturl,
			function(restdata, response) {				
				console.log('rest response', restdata);		
				cb(null, restdata);
			})
			.on('error',function(err){
					console.log('something went wrong on the request', err.request.options);;			
					cb(err, null);
				});		
	};	
	
	
	this.getMaximoOrganizations = function (maximoCreds, cb ) {	
			   
		process.env.NODE_TLS_REJECT_UNAUTHORIZED = 0;
		var args = {
				connection:{    
	        	secureOptions: require('constants').SSL_OP_NO_TLSv1_2
	        	//ciphers:'ECDHE-RSA-AES256-SHA:AES256-SHA:RC4-SHA:RC4:HIGH:!MD5:!aNULL:!EDH:!AESGCM',
	        	//honorCipherOrder: true
	    		}
			};
		   var resturl = maximoCreds.url+"/rest/mbo/organization?_lid="+maximoCreds.username+"&_lpwd="+maximoCreds.password+"&_format=json&_includecols=ORGID,DESCRIPTION";
		   var client1 = new Client(args);
		   client1.get(resturl,
				function(restdata, response) {	
					console.log(restdata);	
					var maximoOrgs = restdata ;
					//console.log('rest response', JSON.parse(restdata));	
					console.log('rest response ORGANIZATIONMboSet: ', maximoOrgs.ORGANIZATIONMboSet);	
					
					var abridgedmaximoOrganizations = [];
					for (index in maximoOrgs.ORGANIZATIONMboSet.ORGANIZATION) { 
						var maximoOrgTemp = maximoOrgs.ORGANIZATIONMboSet.ORGANIZATION[index];
						var maximoOrg = {};
						maximoOrg.id = (maximoOrgTemp.Attributes.ORGID == undefined) ?  "" : maximoOrgTemp.Attributes.ORGID.content;
						maximoOrg.description = (maximoOrgTemp.Attributes.DESCRIPTION == undefined) ?  "" : maximoOrgTemp.Attributes.DESCRIPTION.content;
						abridgedmaximoOrganizations.push(maximoOrg);
					}		       
					cb(abridgedmaximoOrganizations);							
					//res.send(JSON.stringify(abridgedmaximoOrganizations));					
				}).on('error',function(err){
					console.log('something went wrong on the request', err);
				});		
	};
	
	this.getMaximoDetailAssetListings = function (maximoCreds, maximoOrg, cb) {	
	
		   console.log('maximo org', maximoOrg + " " + JSON.stringify(maximoOrg));   
		   process.env.NODE_TLS_REJECT_UNAUTHORIZED = 0;
		var args = {
				connection:{    
	        	secureOptions: require('constants').SSL_OP_NO_TLSv1_2
	        	//ciphers:'ECDHE-RSA-AES256-SHA:AES256-SHA:RC4-SHA:RC4:HIGH:!MD5:!aNULL:!EDH:!AESGCM',
	        	//honorCipherOrder: true
	    		}
			};                  					           
		   var resturl = maximoCreds.url+"/rest/mbo/asset?_maxItems=30&location='STATION1'&_includecols=ASSETNUM,DESCRIPTION,SITEID,ORGID&_format=json&_lid="+maximoCreds.username+"&_lpwd="+maximoCreds.password+"&ORGID=" + maximoOrg;		   		   
		   var client1 = new Client(args);
		   client1.get(resturl,
				function(restdata, response) {		
					var assetFromMaximo = restdata;
                    //console.log('maximo assets based on org', restdata);                     				
					var abridgedassetlistings = [];
					for (index in assetFromMaximo.ASSETMboSet.ASSET) {
						var maximoAsset = assetFromMaximo.ASSETMboSet.ASSET[index];
						var asset = {};
						asset._id = (maximoAsset.Attributes.ASSETNUM == undefined) ?  "" : maximoAsset.Attributes.ASSETNUM.content;
						asset.assetorg = (maximoAsset.Attributes.ORGID == undefined) ?  "" : maximoAsset.Attributes.ORGID.content;
						asset.assettype = (maximoAsset.Attributes.DESCRIPTION == undefined) ?  "" : maximoAsset.Attributes.DESCRIPTION.content;
						asset.siteid = (maximoAsset.Attributes.SITEID == undefined) ?  "" : maximoAsset.Attributes.SITEID.content;
						abridgedassetlistings.push(asset);
					}		       
					cb(abridgedassetlistings);													
				}).on('error',function(err){
					console.log('something went wrong on the request', err.request.options);;			
				});		
	};
	
	this.retrieveTriggeredRuleActions = function(cloudantCreds, deviceID, callback){
	   
	   console.log('retrieveTriggeredRuleActions', deviceID);									
	   var RESTClient = require('node-rest-client').Client;

	   // configure basic http auth for every request	   
	    var invokedrules_cloudantdb_url = "https://"+cloudantCreds.url+"/invokedrules/_find";
	   
	   // configure basic http auth for every request
	   var options_auth={user:cloudantCreds.username,
						 password:cloudantCreds.password};	
	   var selector;


		   selector = {
							"selector": {
								"deviceID": deviceID
								}
						};

		console.log(selector.selector);
		
	   var rulesrestclient = new RESTClient(options_auth);	   	   
		var args = {
			data: selector,
			headers:{"Content-Type": "application/json"} 
		};		
				
	    rulesrestclient.post(invokedrules_cloudantdb_url, args,							
				function(restdata, response) {				
					console.log('response', JSON.parse(restdata).docs);									
					callback(JSON.parse(restdata));
					
				})
				.on('error',function(err){
					console.log('something went wrong on the request', err.request.options);;			
				});
	};

	
	var getIotDevicesfromIoTF = function(appkey, apptoken, callback){		  
	   // configure basic http auth for every request
	   var options_auth={user:appkey,password:apptoken};
	   console.log(options_auth);
	   var IotfClient = require('node-rest-client').Client;
	   var iotfclient = new IotfClient(options_auth);
	  
	   console.log ("org", appkey.substring(2,8));
	   var resturl = "https://internetofthings.ibmcloud.com/api/v0001/organizations/" + appkey.substring(2,8) + "/devices";
	   iotfclient.get(resturl,
				function(restdata, response) {
					console.log("response :: ", response.options);
					console.log("iotdevices :: ", restdata);
					callback(restdata);					
				}).on('error',function(err){
					console.log('something went wrong on the request', err.request.options);			
				});
	};
	
	var getMaximo2IotfOrgConfiguration = function (maximoOrg, cb) {	
		var Maximo2IotfOrgConfiguration = require('./maximo2IotfOrgConfiguration.js').Maximo2IotfOrgConfiguration;
		var maximo2IotfOrgConfiguration = new Maximo2IotfOrgConfiguration();
		maximo2IotfOrgConfiguration.getconfigurations(maximOrg, cb);			
	};
	
	        				
	var getStubMaximoDetailAssetListings = function (cb) {	
		var TestStubForMaximo = require('./assets.js').TestStubForMaximo;
		var testStubForMaximo = new TestStubForMaximo();
		
		testStubForMaximo.getMaximoDetailAssetListings(function(detailassets) { 
			var abridgedassetlistings = [];
			for (index in detailassets.ASSETMboSet.ASSET) {
				var maximoAsset = detailassets.ASSETMboSet.ASSET[index];
				var asset = {};
				asset._id = (maximoAsset.Attributes.ASSETNUM == undefined) ?  "" : maximoAsset.Attributes.ASSETNUM.content;
				asset.assetorg = (maximoAsset.Attributes.ORGID == undefined) ?  "" : maximoAsset.Attributes.ORGID.content;
				asset.assettype = (maximoAsset.Attributes.DESCRIPTION == undefined) ?  "" : maximoAsset.Attributes.DESCRIPTION.content;
				abridgedassetlistings.push(asset);
			}		       
			cb(abridgedassetlistings);	
		});
	};
};

exports.IotAssetProvider = IotAssetProvider;
