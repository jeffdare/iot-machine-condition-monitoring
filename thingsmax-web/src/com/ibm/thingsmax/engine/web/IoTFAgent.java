/**
 *****************************************************************************
 Copyright (c) 2015 IBM Corporation and other Contributors.
 All rights reserved. 
 Contributors:
 IBM - Initial Contribution
 *****************************************************************************
 * 
 */

package com.ibm.thingsmax.engine.web;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.ibm.iotf.client.app.ApplicationClient;
import com.ibm.iotf.client.app.ApplicationStatus;
import com.ibm.iotf.client.app.Command;
import com.ibm.iotf.client.app.DeviceStatus;
import com.ibm.iotf.client.app.Event;
import com.ibm.iotf.client.app.EventCallback;
import com.ibm.iotf.client.app.StatusCallback;
import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;
import com.ibm.thingsmax.storage.ThingsMaxDeviceDAO;

public class IoTFAgent {
	public Properties options = new Properties();
	public ApplicationClient client = null;
	public BlockingQueue<String> messages = new LinkedBlockingQueue<String>();
	public String deviceType = "iotsample-arduino";
	public String deviceId = "000000000001";
	public String eventType = "status";
	public String formatType = "json";

	public static HashMap<String, JSONObject> deviceMappingCache = new HashMap<>();

	public IoTFAgent(BlockingQueue<String> messages) {
		this.messages = messages;
		options = new Properties();
		options.put("id", "App" + (Math.random() * 10000));
		options.put("auth-method", "apikey");

		String VCAP_SERVICES = System.getenv("VCAP_SERVICES");
		try {
			if (VCAP_SERVICES != null) {
				JSONObject vcap = (JSONObject) JSONObject.parse(VCAP_SERVICES);
				JSONArray iotf = (JSONArray) vcap.get("iotf-service");
				JSONObject iotfInstance = (JSONObject) iotf.get(0);
				JSONObject iotfCredentials = (JSONObject) iotfInstance
						.get("credentials");

				String apiKey = (String) iotfCredentials.get("apiKey");
				String apiToken = (String) iotfCredentials.get("apiToken");

				if (apiKey == null || apiKey.equals("") || apiToken == null
						|| apiToken.equals("")) {
					options.put("auth-key", "some key");
					options.put("auth-token", "some token");
				} else {
					options.put("auth-key", apiKey);
					options.put("auth-token", apiToken);
				}
			}
		} catch (Exception ex) {
			System.out.println("JSON parsing exception while reading VCAPs");
		}

		try {
			client = new ApplicationClient(options);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		client.connect();
		client.setEventCallback(new MyEventCallback(messages));
		client.setStatusCallback(new MyStatusCallback());

		// subscribe to events from all devices
		client.subscribeToDeviceEvents("+", "+", "+", "json", 0);

		Timer timer = new Timer();

		//check every 10 sec for changes in mappings in cloudant DB 
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				ThingsMaxDeviceDAO deviceDAO = new ThingsMaxDeviceDAO();
				String devices = null;
				try {
					devices = deviceDAO.getDevices();

					deviceDAO.closeDBConnector();

					JSONArray deviceJSONArray = JSONArray.parse(devices);
					for (int count = 0; count < deviceJSONArray.size(); count++) {
						JSONObject deviceJSON = (JSONObject) deviceJSONArray
								.get(count);

						// putting the mappings to the cache
						if (!deviceMappingCache.containsKey(deviceJSON
								.get("deviceId"))) {
							deviceMappingCache.put(
									(String) deviceJSON.get("deviceId"),
									deviceJSON);
						}

					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 10000, 10000);
	}

	private class MyEventCallback implements EventCallback {
		private BlockingQueue<String> messages = new LinkedBlockingQueue<String>();

		public MyEventCallback(BlockingQueue<String> messages) {
			this.messages = messages;
		}

		public void processEvent(Event e) {
			System.out.println("Event " + e.getPayload());
			messages.offer(e.getPayload());

			// check if the mapping present in the cache, if present then call
			// the rule, else ignore
			if (deviceMappingCache.containsKey(e.getDeviceId())) {
				System.out.println("Got event from : " + e.getDeviceId());
				invokeRuleEngine(e, deviceMappingCache.get(e.getDeviceId()));
			} else {
				System.out.println("ignoring event from : " + e.getDeviceId());
			}
		}

		public void processCommand(Command cmd) {
			System.out.println("Command " + cmd.getPayload());
			// messages.offer(cmd.getPayload());
		}

	}

	private class MyStatusCallback implements StatusCallback {

		public void processApplicationStatus(ApplicationStatus status) {
		}

		public void processDeviceStatus(DeviceStatus status) {
		}
	}

	public Properties getOptions() {
		return options;
	}

	public void invokeRuleEngine(Event e, JSONObject deviceMapping) {

		try {

			JSONObject device = new JSONObject();
			JSONObject event;

			System.out.println(e.getPayload());
			event = JSONObject.parse(e.getPayload());
			device.put("myName", e.getDeviceId());

			JSONObject eventObject = (JSONObject) event.get("d");

			Set<String> dataPoints = eventObject.keySet();

			// Map the event of the Device to the model in Rule Engine
			// TODO: Currently assuming that both will be same.
			for (String dataPoint : dataPoints) {
				device.put(dataPoint,
						((JSONObject) event.get("d")).get(dataPoint));
			}

			JSONObject asset = new JSONObject();
			//map the asset details to the input for rule engine
			asset.put("assetID", deviceMapping.get("assetId"));
			asset.put("assetType", deviceMapping.get("assetType"));

			JSONObject org = new JSONObject();
			//map the asset org details to the input for rule engine
			org.put("orgID", deviceMapping.get("assetorg"));

			JSONObject payload = new JSONObject();

			payload.put("device", device);
			payload.put("asset", asset);
			payload.put("org", org);

			callURL(payload, deviceMapping);
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

	}

	private void callURL(JSONObject payload, JSONObject deviceMapping) {

		String endpointURI = null;
		String rulesUsername = null;
		String rulesPassword = null;
		// get the rule Engine Username and password
		String VCAP_SERVICES = System.getenv("VCAP_SERVICES");
		try {
			if (VCAP_SERVICES != null) {
				JSONObject vcap = (JSONObject) JSONObject.parse(VCAP_SERVICES);
				JSONArray busRules = (JSONArray) vcap.get("businessrules");
				JSONObject busInstance = (JSONObject) busRules.get(0);
				JSONObject credentials = (JSONObject) busInstance
						.get("credentials");

				rulesUsername = (String) credentials.get("user");
				rulesPassword = (String) credentials.get("password");

			}
		} catch (Exception ex) {
			System.out.println("JSON parsing exception while reading VCAPs");
		}

		String authorization = "Basic "
				+ Base64.encodeBase64String((rulesUsername + ":" + rulesPassword)
						.getBytes());
		String contentType = "application/json";

		JSONArray rulesArray = (JSONArray) deviceMapping.get("rules");

		for (Object rulesetObj : rulesArray) {
			JSONObject ruleset = (JSONObject) rulesetObj;

			endpointURI = (String) ruleset.get("ruleurl");
			CloseableHttpClient client = HttpClients.createDefault();
			try {
				HttpPost httpPost = new HttpPost(endpointURI);
				// Add the basic authentication header
				httpPost.addHeader("Authorization", authorization);
				httpPost.addHeader("Content-Type", contentType);
				httpPost.setEntity(new StringEntity(payload.serialize()));
				CloseableHttpResponse response = client.execute(httpPost);
				try {
					if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
						throw new RuntimeException(
								"An error occured when invoking Rules Service at : "
										+ endpointURI
										+ " please verify you have deployed the xom and ruleapp\n"
										+ response.getStatusLine());
					} else {

						String result = EntityUtils.toString(response
								.getEntity());
						System.out.println("Response: " + result);

						JSONObject resp = JSONObject.parse(result);

						String ruleMessage = (String) ((JSONObject) resp
								.get("device")).get("message");
						// if the message is set on the Device, then the rule
						// was involved
						if (ruleMessage != null && ruleMessage.length() > 0) {
							ExternalActions.callAction(ruleMessage,
									deviceMapping);
						}
					}
				} finally {
					response.close();
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					client.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void setOptions(Properties options) {
		this.options = options;
	}

	public ApplicationClient getClient() {
		return client;
	}

	public void setClient(ApplicationClient client) {
		this.client = client;
	}

	public BlockingQueue<String> getMessages() {
		return messages;
	}

	public void setMessages(BlockingQueue<String> messages) {
		this.messages = messages;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public String getFormatType() {
		return formatType;
	}

	public void setFormatType(String formatType) {
		this.formatType = formatType;
	}

}
