exports.index = function(req, res){

	res.render('iotAssetMapping', {});

	/*var iotAssetProvider = req.app.get('iotAssetProvider');

	iotAssetProvider.fetchAssetMapping(req, function(error, assetMappings) {
		if (error) {
			res.send(error, 500);
		} else {
			console.log(assetMappings);
			res.render('iotAssetMapping', { title: 'IotAssetMapping Manager', assetMappings:assetMappings });
		}
	}); */
};
