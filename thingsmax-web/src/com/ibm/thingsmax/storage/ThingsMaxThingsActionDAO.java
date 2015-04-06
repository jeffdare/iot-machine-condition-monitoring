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
import com.ibm.thingsmax.bean.ThingsAction;

public class ThingsMaxThingsActionDAO {

	HttpClient httpClient = null;

	// set default db connection credentials
	String databaseHost = "user.cloudant.com";
	int port = 443;
	String databaseName = "thingsmaxactionmappings";
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
	 * @param actionId
	 * @throws Exception
	 */
	public void delete(String actionId) throws Exception {
		CouchDbConnector dbConnector = createDbConnector();
		dbConnector.delete(dbConnector.get(HashMap.class, actionId));
	}

	
	/** get action details for specified action
	 * @param actionId
	 * @return
	 * @throws Exception
	 */
	public String getThingsActionDetails(String actionId) throws Exception {
		CouchDbConnector dbConnector = createDbConnector();
		Gson gson = new Gson();
		JsonElement eachAction = null;
		// get the document object by providing doc id
		HashMap<String, Object> obj = dbConnector.get(HashMap.class, actionId);
		
		// use google gson api to convert java object to json
		eachAction = gson.toJsonTree(obj);
		return eachAction.toString();
	}

	
	/** get all actions
	 * @return
	 * @throws Exception
	 */
	public String getThingsAction() throws Exception {

		CouchDbConnector dbConnector = createDbConnector();
		HashMap<String, Object> obj = null;
		Gson gson = new Gson();
		JsonElement eachAction = null;
		JsonArray allActions = new JsonArray();

		try {
			// get all the document IDs present in database
			List<String> docIds = dbConnector.getAllDocIds();

			for (String docId : docIds) {
				// get the document object by providing doc id
				obj = dbConnector.get(HashMap.class, docId);
				eachAction = gson.toJsonTree(obj);
				// add each action to a collection
				allActions.add(eachAction);
			}
		} catch (DocumentNotFoundException dnfe) {
			System.out.println("Exception thrown : " + dnfe.getMessage());
			throw dnfe;
		}

		// close the connection manager
		closeDBConnector();
		return allActions.toString();
	}

	/** create an action
	 * @param action, as specified by the JSON input from user
	 * @return ThingsAction object
	 * @throws Exception
	 */
	public ThingsAction createAction(ThingsAction thingsAction) throws Exception {
		
		CouchDbConnector dbConnector = createDbConnector();
		//create a new document
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("_id", thingsAction.getActionId());
		data.put("actionId", thingsAction.getActionId());
		data.put("thingsActionUserId", thingsAction.getThingsActionUserId());
		data.put("thingsActionPassword", thingsAction.getThingsActionPassword());
		data.put("thingsActionURL", thingsAction.getThingsActionURL());
		
		dbConnector.create(data);
		return thingsAction;
	}
	
	
}
