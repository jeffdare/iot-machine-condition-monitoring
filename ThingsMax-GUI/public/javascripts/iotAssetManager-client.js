
var app = angular.module('iotAssetManagerApp', ['ngRoute', 'ngSanitize', 'ui.select']);

/**
 * AngularJS default filter with the following expression:
 * "assetmapping in assetmappings | filter: {assetID: $select.search, assetType: $select.search}"
 * performs a AND between 'assetID: $select.search' and 'assetType: $select.search'.
 * We want to perform a OR.
 */

 app.filter('propsFilter', function() {
  return function(items, props) {
    var out = [];

    if (angular.isArray(items)) {
      items.forEach(function(item) {
        var itemMatches = false;

        var keys = Object.keys(props);
        for (var i = 0; i < keys.length; i++) {
          var prop = keys[i];
          var text = props[prop].toLowerCase();
          if (item[prop].toString().toLowerCase().indexOf(text) !== -1) {
            itemMatches = true;
            break;
          }
        }

        if (itemMatches) {
          out.push(item);
        }
      });
    } else {
      // Let the output be the input untouched
      out = items;
    }

    return out;
  }
});

app.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/', {
        controller: 'iotAssetManagerController',
        templateUrl: '/'
    })
    .otherwise({ redirectTo: '/' });
}]);	


// Angular Service that does all the calls to the GUI Backbackend
angular.module('iotAssetManagerApp').factory('dataFactory', ['$http', function($http) {
    var dataFactory = {};
	
	
	dataFactory.getIotDevices = function (iotfOrgSelected) {
		    return $http.get('/iotf?appkey=' + iotfOrgSelected.appkey + '&apptoken=' + iotfOrgSelected.apptoken);
	};
	
	dataFactory.getMaximoOrganizations = function () {	
	
        return $http.get('/maximoOrganizations');
    };
	
	dataFactory.getIOTFOrganizations = function (selectedMaximoOrg) {			
	console.log("data factory maximoorgSelected",  selectedMaximoOrg); 		
        return $http.get('/iotfOrgs/?maximoOrg=' + selectedMaximoOrg);
    };	
	
	dataFactory.getMaximoAssets = function (orgSelected) {
        console.log("data factory orgSelected",  orgSelected); 	
        return $http.get('/maximo/?maximoOrganization=' + orgSelected);
    };
	
	dataFactory.getTriggeredRuleActions = function (deviceID) {				 
        return $http.get('/triggeredactions/?deviceID=' + deviceID);
    };
    
	dataFactory.getIotAssetMappings = function (assetid) {				 
        return $http.get('/thingsmax/?assetid=' + assetid);
    };
	
	dataFactory.insertIotAssetMapping = function (assetMapping) {
        return $http.post('/thingsmax', assetMapping);
    };
	
	dataFactory.deleteIotAssetMapping = function (id) {		
        return $http.delete('/thingsmax/?assetid=' + id);
    };
    return dataFactory;
}]);

// Main module that handles the changes to the Model($scope)
// in the MVC part of the GUI front end.

angular.module('iotAssetManagerApp')
  .controller('iotAssetManagerController', ['$http',
    '$scope', 'dataFactory', function ($http, $scope, dataFactory) {

	$scope.devices;
    $scope.assetMappings;
	$scope.flatAssetDeviceTable = [];
	$scope.assetdevice = {};
	$scope.device = {};
	$scope.triggeredactions = [];
	$scope.isCollapsed = true;
	
	$scope.disabled = undefined;
	$scope.incomplete = true;
	  
	$scope.maximoassets = [];
		
	//getIotDevices();
	//getMaximoAssets ();
	
	$scope.org = {};
	$scope.organizations = [{
			"id": "uguhsp",
			"name": "My Organization",
			"created": "2014-07-04T21:36:05Z",
			"updated": "2014-07-04T21:36:05Z"
		}];
	
	
	$scope.maximoOrg = {};
	$scope.iotfOrgs = [];
	$scope.iotfOrg = {};
	
	
	$scope.maximoOrganizations = [];
	
	getMaximoOrganizations ();

	// getMaximoAssets("ORG1");
	//changeOnMaximoOrgSelected("ORG1");
	/*var obj = {id : "ORG1",description:"Organization 1"};

	$scope.maximoOrganizations[0] = obj;
	$scope.maximoOrg.selected = obj;

  var maximoobj = {_id :"CAR1",

assetorg : 	"ORG1",
	
assettype :
	"Rail Car 1", 
	siteid : 
		"SITE1" };

		$scope.maximoassets[0] = maximoobj;
		$scope.assetdevice.selected = maximoobj;*/


	//angular.element('#myselector').trigger('click')

	function getMaximoOrganizations () {
		dataFactory.getMaximoOrganizations()
            .success(function (maximoOrganizations) {				
                $scope.maximoOrganizations = maximoOrganizations;						
            })
            .error(function (error) {
                alert('Unable to load device data: ' + error.message);				
            });
	};	
	
	$scope.changeOnMaximoOrgSelected = function(maximoOrgSelected) { 						
		console.log("orgSelected", maximoOrgSelected);
		getMaximoAssets (maximoOrgSelected);
		dataFactory.getIOTFOrganizations(maximoOrgSelected)
		            .success(function (iotforgs) {		
					console.log("getIOTFOrganizations  " + JSON.stringify(iotforgs[0]));
					$scope.iotfOrg = iotforgs[0];						
            })
			            .error(function (error) {
						alert('Unable to load maximo asset data: ' + error.message);				
            });
	};
	

	function getMaximoAssets (orgSelected) {
		dataFactory.getMaximoAssets(orgSelected)
            .success(function (assets) {				
                $scope.maximoassets = assets;						
            })
            .error(function (error) {
                alert('Unable to load maximo asset data: ' + error.message);				
            });
	};

	$scope.changeOnIotfOrgSelection = function(iotfOrgSelected) { 						
		console.log("iotforgSelected", iotfOrgSelected);
		getIotDevices (iotfOrgSelected);
	};	
	
	function getIotDevices(iotfOrgSelected) {
		dataFactory.getIotDevices(iotfOrgSelected)
            .success(function (devices) {
	           $scope.devices = devices;						
				console.log("iot devices", $scope.devices);
            })
            .error(function (error) {
                alert('Unable to load device data: ' + error.message);				
            });
	};
	
	function getAssetMappedDevices(assetid) {
		$scope.flatAssetDeviceTable = [];
		dataFactory.getIotAssetMappings(assetid)
            .success(function (assetMapping) {		
                $scope.assetMappings = assetMapping;							
				//console.log("asset Mapping", $scope.assetMappings);
				// flatten the asset device structure for easy representation in UI				
				for (var j in assetMapping.devices) {
						var mappedDevice = {};
						mappedDevice._id = assetMapping._id;
						mappedDevice.assetorg = assetMapping.assetorg;
						mappedDevice.assettype = assetMapping.assettype;					
						mappedDevice.dorg = assetMapping.devices[j].dorg;
						mappedDevice.deviceId = assetMapping.devices[j].deviceId;
						mappedDevice.deviceType = assetMapping.devices[j].deviceType;
						mappedDevice.rules = assetMapping.devices[j].rules;
						$scope.flatAssetDeviceTable.push(mappedDevice);				
				}				
				if ($scope.flatAssetDeviceTable != undefined )
					console.log("asset Mapping length", $scope.flatAssetDeviceTable.length);
				if ($scope.flatAssetDeviceTable != undefined 
				    && $scope.flatAssetDeviceTable.length == 0) {
					   $scope.showNoMappingMsg = true;		
					   console.log("show no asset Mapping set to true: ", $scope.flatAssetDeviceTable.length);
					}
				else
					   $scope.showNoMappingMsg = false;						
            })
            .error(function (error) {
                alert('Unable to load assetMapping data: ' + error.message);				
            });
	}
					
	$scope.createNewAssetMapping = function () {		
		var assetMapping = angular.copy($scope.newAssetMapping);
		//alert('Inserting assetmapping  ' + JSON.stringify(assetMapping));
        dataFactory.insertIotAssetMapping(assetMapping)
            .success(function () {
                window.location.href = "/";
            }).
            error(function(error) {
                alert('Unable to insert assetMapping: ' + error);
            });
    };
	
	$scope.deleteIotAssetMapping = function(assetid) {
		if(confirm('Do you really want to delete assetmapping with id  ' + assetid) ) {
		dataFactory.deleteIotAssetMapping(assetid)
			.success(function () {
				window.location.href = "/";
			}).
			error(function(error) {
				alert('Unable to delete assetMapping: ' + error);
			});
		}
	};
	
	$scope.view_tab = 'maximoassets';	
	$scope.changeTab = function(tab) { 			
			if(tab != undefined)		
				$scope.view_tab = tab; 				
			if(tab == 'newdevicemappings')	{
				getIotDevices($scope.iotfOrg);
				$scope.device.selected = undefined;						
			
			}
			if(tab == 'triggeredruleactions')		
				$scope.getTriggeredActions($scope.device.deviceId);
	};				 
	
	$scope.getTriggeredActions = function (deviceID) {		
		console.log("getTriggeredActions", deviceID);
		dataFactory.getTriggeredRuleActions(deviceID)
            .success(function (triggeredactions) {		
				console.log("getTriggeredActions success", triggeredactions.docs);
               $scope.triggeredactions = triggeredactions.docs;					
			   console.log("getTriggeredActions success", $scope.triggeredactions);
            })
            .error(function (error) {
                alert('Unable to get triggered rule actions: ' + error.message);				
            });
    };
	$scope.selectedDeviceForTriggeredActions = function(device) {	
					$scope.device = device;	
					console.log("selectedDeviceForTriggeredActions", device);
					$scope.changeTab('triggeredruleactions');
	};
	
	$scope.showNoMappingMsg = false;
	$scope.changeTabMaximoDevices = function(assetSelected) { 			
			if(assetSelected != undefined && assetSelected._id != "") {											
				getAssetMappedDevices(assetSelected._id);				
				$scope.resetAssetMapping();
				closeAll();	
			} else  // prevents empty selection from changing the pane
				$scope.assetdevice.selected = undefined; 	
					
	};


	
	$scope.newDeviceMapping = { deviceId : "",
							   deviceType : "",
							   rules: []};		

    $scope.resetAssetMapping = function() {
			$scope.newAssetMapping = angular.copy($scope.emptyAssetMapping);
	};
		
	$scope.emptyAssetMapping = {
						'assetID' : "",
						'assetType' : "",
						'assetOrg' : "",
						'siteID' : "",
						'devices' : []}
	$scope.newAssetMapping;
	$scope.resetAssetMapping();
	
	$scope.newDeviceRule = { ruleName : "",
							 ruleurl : ""};	
	
	var orgselected = { appkey: "a-uguhsp-bdmvjn9zxb",
	                    apptoken: "dxLYxMsFZDfQkekUm8"
					   };
	$scope.onDeviceSelection = function(assetselected, deviceselected) {		
		var copyOfDevice =  angular.copy(deviceselected);		
		$scope.newDeviceMapping.deviceId = copyOfDevice.id;
		$scope.newDeviceMapping.deviceType = copyOfDevice.type;
		$scope.newDeviceMapping.appId = copyOfDevice.appId;		
		$scope.newDeviceMapping.appkey = orgselected.appkey;
		$scope.newDeviceMapping.apptoken = orgselected.apptoken;
		$scope.newDeviceMapping.brokerHost = copyOfDevice.brokerHost;
		$scope.newDeviceMapping.brokerPort = copyOfDevice.brokerPort;
		$scope.newDeviceMapping.eventType = copyOfDevice.eventType;
		$scope.newDeviceMapping.format = copyOfDevice.format;		
		//$scope.newDeviceMapping.dorg = copyOfDevice.uuid.split(':')[1];
		
		$scope.addDeviceMapping(assetselected, $scope.newDeviceMapping);
		console.log("newDeviceMapping", $scope.newDeviceMapping);
		console.log("asset selected", assetselected);
		closeOthers();
	};
	
	$scope.addDeviceMapping = function(assetselected, deviceMapping) { 			
		var d = angular.copy(deviceMapping);
		var duplicate = false;
		
		$scope.newAssetMapping.assetID = assetselected._id;
		$scope.newAssetMapping.assetType = assetselected.assettype;
		$scope.newAssetMapping.assetOrg = assetselected.assetorg;		
		$scope.newAssetMapping.siteID = assetselected.siteid;		
		 		 
		 // Before adding a device, check if the device already exists in the newAssetMapping devices
		if( $scope.newAssetMapping.devices.length !=0 ) {
		    for ( i in $scope.newAssetMapping.devices){				
				if (($scope.newAssetMapping.devices[i].deviceId.indexOf(d.deviceId) > -1)) {
					duplicate = true;
					break;
				}
			}
		}
		// Inserts only if the deviceid doesn't exist in the newAssetMapping devices
		if(duplicate == false) {
			$scope.newAssetMapping.devices.push(d); 									
			closeOthers();
		}
		//$scope.device.selected = undefined;
		console.log("newAssetMapping", JSON.stringify($scope.newAssetMapping));
			
	};		
    
	$scope.addDeviceRule = function(deviceMapping, deviceRule) { 	
		
		 var dr = angular.copy(deviceRule);
		 var duplicate = false;
		if( deviceMapping.rules.length !=0 ) {
		    for ( i in deviceMapping.rules){	
				if ((deviceMapping.rules[i].ruleName.indexOf(dr.ruleName) > -1)) {
					duplicate = true;
					break;
				}
			}
		}
		// Inserts only if the rulename doesn't exist in the newAssetMapping devices
		if(duplicate == false) {
			deviceMapping.rules.push(dr); 										
		}
		$scope.newDeviceRule = undefined;	
	};

    $scope.enable = function() {
		$scope.disabled = false;
	};
  
	$scope.clearAssetSelection = function() {
		$scope.assetdevice.selected = undefined;    
	};
	
	$scope.clearDeviceSelection = function() {
		$scope.device.selected = undefined;    
	};
	
	$scope.mapBtnEnable = function(assetSelected, deviceSelected) {
		return !assetSelected && !deviceSelected;
	};		

	$scope.init = function () {
		var obj = {id : "ORG1",description:"Organization 1"};

		$scope.maximoOrg.selected = obj;
		var maximoobj = {_id :"W-BEARING1",

					assetorg : 	"ORG1",
						
					assettype :
						"Rail Car 1 - Axle1 - Wheel Bearing 1", 
						siteid : 
							"SITE1" };

		$scope.assetdevice.selected = maximoobj;

		$scope.changeOnMaximoOrgSelected("ORG1");
		$scope.changeTabMaximoDevices({_id : "W-BEARING1"});
		//$scope.changeTab('mappeddevices');
	};

	//$scope.init();
}]);
