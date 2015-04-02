IotAssetManager = function(app) {
	var IotAssetProvider = require('./iotAssetProvider').IotAssetProvider;
	var iotAssetProvider = new IotAssetProvider();
	app.set('iotAssetProvider', iotAssetProvider);
	
	app.get('/thingsmax', function(req, res) {	
		iotAssetProvider.fetchAssetMapping(req, function(error, assetMappings) {						
			if (error) {
				res.send(error, 404);
			} else {
				res.send(assetMappings);
			}
			
		});
	});
	
	app.post('/thingsmax', function(req, res) {
		console.log('posting assetMapping');
		//console.log(req.body);
		console.log(JSON.stringify(req.body));
		iotAssetProvider.insertAssetMapping(req.body, function(error, assetMapping) {
			if (error) {
				res.send(error, 500);
			} else {
				res.send(assetMapping);
			}
		});
	});
	
	app.delete('/thingsmax', function(req, res) {		
		
		console.log('delete request for asset ID', req.query.assetid);		
		iotAssetProvider.deleteAssetMapping(req.query.assetid, function(error, assetMapping) {						
			if (error) {
				res.send(error, 404);
			} else {
				res.send('');
			}
		});
	});
};

exports.IotAssetManager = IotAssetManager;
