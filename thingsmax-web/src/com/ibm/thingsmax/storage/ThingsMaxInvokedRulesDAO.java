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
import com.ibm.thingsmax.bean.InvokedRule;
import com.ibm.thingsmax.bean.ThingsAction;

public class ThingsMaxInvokedRulesDAO {

	HttpClient httpClient = null;

	// set default db connection credentials
	String databaseHost = "user.cloudant.com";
	int port = 443;
	String databaseName = "invokedrules";
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

		httpClient = new StdHttpClient.Builder().host(host).port(port)
				.username(username).password(password).enableSSL(true)
				.relaxedSSLSettings(true).build();

		dbInstance = new StdCouchDbInstance(httpClient);

		CouchDbConnector dbConnector = new StdCouchDbConnector(dbName,
				dbInstance);
		dbConnector.createDatabaseIfNotExists();

		return dbConnector;
	}

	
	/** create an invoked Rule entry
	 * @param action, as specified by the JSON input from user
	 * @return ThingsAction object
	 * @throws Exception
	 */
	public InvokedRule createAction(InvokedRule rule) throws Exception {
		
		CouchDbConnector dbConnector = createDbConnector();
		//create a new document
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("deviceID", rule.getDeviceID());
		data.put("deviceType", rule.getDeviceType());
		data.put("assetID", rule.getAssetID());
		data.put("assetType", rule.getAssetType());
		data.put("decisionID", rule.getDecisionID());
		data.put("message", rule.getMessage());
		
		dbConnector.create(data);
		return rule;
	}
	
	
}
