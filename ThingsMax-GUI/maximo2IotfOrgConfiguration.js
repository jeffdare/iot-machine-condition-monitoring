Maximo2IotfOrgConfiguration = function() {
	this.getconfigurations = function(maximoOrg, cb) {
		cb(getIoftOrgs(maximoOrg));
	}	
}
exports.Maximo2IotfOrgConfiguration = Maximo2IotfOrgConfiguration;

var getIoftOrgs = function (maximoOrg, cb) {
	var foundMatch = false;
	var iotfOrgs;
	for (i in maximoOrg2IofOrgs)
	{		
		//console.log("In if block, iotfOrgs", maximoOrg2IofOrgs[i].iotfOrgs);
		if( maximoOrg2IofOrgs[i].maximoOrg == maximoOrg) {			
			foundMatch = true;						
			console.log("In if block, iotfOrgs", maximoOrg2IofOrgs[i].iotfOrgs);
			return maximoOrg2IofOrgs[i].iotfOrgs;
		}
	}
	return undefined;
};

var maximoOrg2IofOrgs = [   { 	maximoOrg:     "ORG1",
								iotfOrgs:	[
											   {
													"org": "uguhsp",
													"name": "Electrical",
													"appkey": "a-uguhsp-bdmvjn9zxb",
													"apptoken": "dxLYxMsFZDfQkekUm8"												   
												}
		
											]
							}/*,
							{ 
							    maximoOrg: "EAGLESA" ,
								iotfOrgs:	[
												{
													"org": "uguhsp",
													"name": "Plumbing",
													"appkey": "a-uguhsp-t54ipe1bk7",
													"apptoken": "y1Viw)kxmbXeLXMo!m"
												}
											]
							}*/
						];												