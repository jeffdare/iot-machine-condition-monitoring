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
import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;
import com.ibm.thingsmax.bean.Device;

public class ThingsMaxDeviceDAO {

	HttpClient httpClient = null;

	// set default db connection credentials
	String databaseHost = "user.cloudant.com";
	int port = 443;
	String databaseName = "thingsmaxdevicecentricmappings";
	String user = "user";
	String password = "password";

	/** create a CouchDbConnector
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

	/** close the db connection
	 * 
	 */
	public void closeDBConnector() {
		if (httpClient != null)
			httpClient.shutdown();
	}

	/** get an instance of CouchDbConnector
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

	
	/** delete a record
	 * @param assetID
	 * @throws Exception
	 */
	public void delete(String assetID) throws Exception {
		CouchDbConnector dbConnector = createDbConnector();
		dbConnector.delete(dbConnector.get(HashMap.class, assetID));
	}

	
	/** get device details for specified asset
	 * @param deviceID
	 * @return
	 * @throws Exception
	 */
	public String getDeviceDetails(String deviceID) throws Exception {
		CouchDbConnector dbConnector = createDbConnector();
		Gson gson = new Gson();
		JsonElement EachDevice = null;
		// get the document object by providing doc id
		HashMap<String, Object> obj = dbConnector.get(HashMap.class, deviceID);
		
		// use google gson api to convert java object to json
		EachDevice = gson.toJsonTree(obj);
		return EachDevice.toString();
	}

	
	
	/** get all devices
	 * @return
	 * @throws Exception
	 */
	public String getDevices() throws Exception {

		CouchDbConnector dbConnector = createDbConnector();
		HashMap<String, Object> obj = null;
		Gson gson = new Gson();
		JsonElement EachDevice = null;
		JsonArray AllDevices = new JsonArray();

		try {
			// get all the document IDs present in database
			List<String> docIds = dbConnector.getAllDocIds();

			for (String docId : docIds) {
				// get the document object by providing doc id
				obj = dbConnector.get(HashMap.class, docId);
				EachDevice = gson.toJsonTree(obj);
				// add each asset to a collection
				AllDevices.add(EachDevice);
			}
		} catch (DocumentNotFoundException dnfe) {
			System.out.println("Exception thrown : " + dnfe.getMessage());
			throw dnfe;
		}

		// close the connection manager
		closeDBConnector();
		return AllDevices.toString();
	}

	
 
	/** create a device
	 * @param asset, as specified by the JSON input from user
	 * @return Asset object
	 * @throws Exception
	 */
	public Device createDevice(Device device) throws Exception {
		
		CouchDbConnector dbConnector = createDbConnector();
		//create a new document
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("_id", device.getDeviceID());
		data.put("devicetype", device.getDeviceType());
		data.put("deviceorg", device.getDeviceOrg());
		data.put("deviceformat", device.getDeviceFormat());
		data.put("devicebrokerhost", device.getDeviceBrokerHost());
		data.put("devicebrokerport", device.getDeviceBrokerPort());
		data.put("deviceeventtype", device.getDeviceEventType());
		
		data.put("assetid", device.getAssetID());
		data.put("assettype", device.getAssetType());
		data.put("assetorg", device.getAssetOrg());
		
		ArrayList arlRules = new ArrayList();
		// list of rules will be stored as arraylist 
		arlRules = device.getRules();
		
		data.put("rules", arlRules);
		dbConnector.create(data);
		return device;
	}
	
}