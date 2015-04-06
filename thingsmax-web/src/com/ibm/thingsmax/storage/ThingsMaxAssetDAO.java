/**
 *****************************************************************************
 Copyright (c) 2015 IBM Corporation and other Contributors.
 All rights reserved. 
 Contributors:
 IBM - Initial Contribution
 *****************************************************************************
 * 
 */

package com.ibm.thingsmax.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.DocumentNotFoundException;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;
import com.ibm.thingsmax.bean.Asset;
import com.ibm.thingsmax.engine.web.IoTFAgent;

public class ThingsMaxAssetDAO {

	HttpClient httpClient = null;

	// set default db connection credentials
	String databaseHost = "user.cloudant.com";
	int port = 443;
	String databaseName = "thingsmaxassetcentricmappings";
	String user = "user";
	String password = "password";

	/**
	 * create a CouchDbConnector
	 * 
	 * @return
	 * @throws Exception
	 */
	private CouchDbConnector createDbConnector() throws Exception {
		// VCAP_SERVICES is a system environment variable
		// Parse it to obtain the for NoSQL DB connection info
		String VCAP_SERVICES = System.getenv("VCAP_SERVICES");
		String serviceName = null;

		if (VCAP_SERVICES != null) {
			// parse the VCAP JSON structure
			JSONObject obj = JSONObject.parse(VCAP_SERVICES);
			String dbKey = null;
			Set<String> keys = obj.keySet();
			// Look for the VCAP key that holds the cloudant no sql db
			// information
			for (String eachkey : keys) {
				if (eachkey.contains("cloudantNoSQLDB")) {
					dbKey = eachkey;
					break;
				}
			}
			if (dbKey == null) {
				System.out
						.println("Could not find cloudantNoSQLDB key in VCAP_SERVICES env variable ");
				return null;
			}

			JSONArray list = (JSONArray) obj.get(dbKey);
			obj = (JSONObject) list.get(0);
			serviceName = (String) obj.get("name");
			System.out.println("Service Name - " + serviceName);

			obj = (JSONObject) obj.get("credentials");

			databaseHost = (String) obj.get("host");
			port = ((Long) obj.get("port")).intValue();
			user = (String) obj.get("username");
			password = (String) obj.get("password");
			// url is not being used
			// url = (String) obj.get("url");
		} else {
			System.out
					.println("VCAP_SERVICES not found, using hard-coded defaults");
		}

		return getDBConnector(databaseHost, port, user, password, databaseName,
				serviceName);
	}

	/**
	 * close the db connection
	 * 
	 */
	public void closeDBConnector() {
		if (httpClient != null)
			httpClient.shutdown();
	}

	/**
	 * get an instance of CouchDbConnector
	 * 
	 * @param host
	 * @param port
	 * @param username
	 * @param password
	 * @param dbName
	 * @param serviceName
	 * @return
	 */
	public CouchDbConnector getDBConnector(String host, int port,
			String username, String password, String dbName, String serviceName) {

		CouchDbInstance dbInstance = null;

		System.out.println("Creating couch db instance...");
		httpClient = new StdHttpClient.Builder().host(host).port(port)
				.username(username).password(password).enableSSL(true)
				.relaxedSSLSettings(true).build();

		dbInstance = new StdCouchDbInstance(httpClient);

		CouchDbConnector dbConnector = new StdCouchDbConnector(dbName,
				dbInstance);
		dbConnector.createDatabaseIfNotExists();

		return dbConnector;
	}

	/**
	 * get asset details for specified asset
	 * 
	 * @param assetID
	 * @return
	 * @throws Exception
	 */
	public String getAssetDetails(String assetID) throws Exception {
		CouchDbConnector dbConnector = createDbConnector();
		Gson gson = new Gson();
		JsonElement EachAsset = null;
		// get the document object by providing doc id
		HashMap<String, Object> obj = dbConnector.get(HashMap.class, assetID);

		// use google gson api to convert java object to json
		EachAsset = gson.toJsonTree(obj);
		return EachAsset.toString();
	}

	/**
	 * get all assets
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getAssets() throws Exception {

		CouchDbConnector dbConnector = createDbConnector();
		HashMap<String, Object> obj = null;
		Gson gson = new Gson();
		JsonElement EachAsset = null;
		JsonArray AllAssets = new JsonArray();

		try {
			// get all the document IDs present in database
			List<String> docIds = dbConnector.getAllDocIds();

			for (String docId : docIds) {
				// get the document object by providing doc id
				obj = dbConnector.get(HashMap.class, docId);
				EachAsset = gson.toJsonTree(obj);
				// add each asset to a collection
				AllAssets.add(EachAsset);
			}
		} catch (DocumentNotFoundException dnfe) {
			System.out.println("Exception thrown : " + dnfe.getMessage());
			throw dnfe;
		}

		// close the connection manager
		closeDBConnector();
		return AllAssets.toString();
	}

	/**
	 * create an asset
	 * 
	 * @param asset
	 *            , as specified by the JSON input from user
	 * @return Asset object
	 * @throws Exception
	 */
	public Asset createAsset(Asset asset) throws Exception {

		/*
		 * DEVICE creation starts
		 * now we start creating device records
		 * create the devices as separate documents 
		 * in a different Cloudant DB
		 */

		CouchDbConnector dbConnector = null;
		
		ArrayList arlDevicesFull = asset.getDevices();
		int noofdevices = arlDevicesFull.size();

		LinkedHashMap<String, Object> map = null;
		String sDeviceId = null;
		String sAppKey = null;
		String sAppToken = null;
		ArrayList arlRules = null;

		for (int i = 0; i < noofdevices; i++) {
			// each device exists as a map inside the arraylist
			map = (LinkedHashMap<String, Object>) arlDevicesFull.get(i);
			sDeviceId = (String)map.get("deviceId");
			sAppKey = (String)map.get("appkey");
			sAppToken = (String)map.get("apptoken");
			
			// add assetid to the device record. this helps to maintain link b/w
			// device & asset
			map.put("assetId", (String)asset.getAssetID());
			map.put("assetType", (String)asset.getAssetType());
			map.put("assetorg", (String)asset.getAssetOrg());
			map.put("siteid", (String)asset.getsiteID());
			map.put("_id", sDeviceId);
			map.put("appkey", sAppKey);
			map.put("apptoken", sAppToken);
			
			arlRules = (ArrayList) map.get("rules");
			map.put("rules", arlRules);
			
			databaseName = "thingsmaxdevicecentricmappings";
			dbConnector = createDbConnector();
			dbConnector.create(map);


			/*
			 * RULE CREATION starts
			 */
			int noofrules = arlRules.size();
			
			for (int j = 0; j < noofrules; j++) {
				// each rule exists as a map inside the arraylist
				map = (LinkedHashMap<String, Object>) arlRules.get(j);
				//String ruleId = (String)map.get("ruleId");
				// add assetid & deviceId to the rule record. this helps to maintain link b/w
				// device, asset & rule
				map.put("assetId", (String)asset.getAssetID());
				map.put("deviceId", sDeviceId);
				map.put("_id", sDeviceId+"_Rule"+j);
				map.put("ruleId", sDeviceId+"_Rule"+j);
				
				databaseName = "thingsmaxrulecentricmappings";
				dbConnector = createDbConnector();
				dbConnector.create(map);
			} // end loop for each rule within a specific device

		} // end loop for each device
		
		
		/*
		 * ASSET CREATION starts
		 * create this as a single document
		 * this contains devices info within it
		 * 
		 */
		databaseName = "thingsmaxassetcentricmappings";
		dbConnector = createDbConnector();
		// create a new document
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("_id", asset.getAssetID());
		data.put("assettype", asset.getAssetType());
		data.put("assetorg", asset.getAssetOrg());
		data.put("siteid", asset.getsiteID());

		// list of devices will be stored as arraylist
		ArrayList arlDevices = arlDevicesFull;

								// iterate the arraylist to get the map for each device
								// and remove everything except deviceId and deviceType
								/*LinkedHashMap<String, String> devicemap = null;
								ArrayList arlFilteredDevices = new ArrayList();
								Iterator<Map.Entry<String, String>> itrDevice = null;
								Map.Entry<String, String> deviceMap = null;
								Map.Entry<String, String> entry = null;
						
								// iterate list of devices to get each device Map
								for (Iterator iterator = arlDevices.iterator(); iterator.hasNext();) {
									devicemap = (LinkedHashMap<String, String>) iterator.next();
						
									// iterate the Map containing device details
									for (itrDevice = devicemap.entrySet().iterator(); itrDevice
											.hasNext();) {
										entry = itrDevice.next();
										if (!entry.getKey().equals("deviceId")
												|| !entry.getKey().equals("deviceType")) {
											itrDevice.remove();
										}
									}
									arlFilteredDevices.add(devicemap);
								}
						
								data.put("devices", arlFilteredDevices);*/
		data.put("devices", arlDevices);
		dbConnector.create(data);

		// return the Asset record
		return asset;
	}

	/**
	 * delete an Asset record
	 * 
	 * @param assetID
	 * @throws Exception
	 */
	public void delete(String assetID) throws Exception {
		CouchDbConnector dbConnector = createDbConnector();
		dbConnector.delete(dbConnector.get(HashMap.class, assetID));
	}

}