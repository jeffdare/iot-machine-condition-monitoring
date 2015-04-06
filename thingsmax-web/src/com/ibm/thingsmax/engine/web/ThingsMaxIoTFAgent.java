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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Properties;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.ibm.iotf.client.app.ApplicationClient;
import com.ibm.iotf.client.app.ApplicationStatus;
import com.ibm.iotf.client.app.Command;
import com.ibm.iotf.client.app.CommandCallback;
import com.ibm.iotf.client.app.DeviceStatus;
import com.ibm.iotf.client.app.Event;
import com.ibm.iotf.client.app.EventCallback;
import com.ibm.iotf.client.app.StatusCallback;
import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;
import com.ibm.thingsmax.storage.ThingsMaxDeviceDAO;

public class ThingsMaxIoTFAgent {

	private Properties options = new Properties();
	private String deviceType = "iotsample-arduino";	
	private String deviceId = "00aabbccde03";
	private String eventType = "status";
	private String formatType = "json";
	protected ApplicationClient client = null;
	
	
	
	public ThingsMaxIoTFAgent(String id, String apiAuthKey, String apiAuthToken) {
		options = new Properties();
		options.put("id", id);
		options.put("auth-method", "apikey");

		options.put("auth-key", apiAuthKey);
		options.put("auth-token", apiAuthToken);

		try {
			client = new ApplicationClient(options);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	

	public ThingsMaxIoTFAgent() {
		options = new Properties();
		options.put("id", String.valueOf(new Date().getTime()));
		options.put("auth-method", "apikey");


		String VCAP_SERVICES = System.getenv("VCAP_SERVICES");
		try {
			if (VCAP_SERVICES != null) {
				JSONObject vcap = (JSONObject) JSONObject.parse(VCAP_SERVICES);
				JSONArray iotf = (JSONArray) vcap.get("iotf-service");
				JSONObject iotfInstance = (JSONObject) iotf.get(0);
				JSONObject iotfCredentials = (JSONObject) iotfInstance.get("credentials");

				String apiKey = (String)iotfCredentials.get("apiKey");
				String apiToken = (String)iotfCredentials.get("apiToken");
				
				if(apiKey == null || apiKey.equals("") || apiToken == null || apiToken.equals("") ) {
					options.put("auth-key", "a-uguhsp-ywwgvqzatf");
					options.put("auth-token", "LRWbgadqiPV0x2PbKX");
				} else {
					options.put("auth-key", apiKey);
					options.put("auth-token", apiToken);					
				}
			} else {
				options.put("auth-key", "a-uguhsp-ywwgvqzatf");
				options.put("auth-token", "LRWbgadqiPV0x2PbKX");
			}
		} catch(Exception ex) {
			System.out.println("Unable to parse the VCAP messages...");
		}
	
		
		try {
			client = new ApplicationClient(options);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

	public void subscribe() {
	
		client.connect();
		client.setEventCallback(new MyEventCallback());
//		client.setStatusCallback(new MyStatusCallback());
//		client.subscribeToDeviceStatus();
//		client.subscribeToDeviceCommands("iotsample-arduino", "00aabbccde03", "blink", "json", 0);
	//			client.subscribeToDeviceEvents("iotsample-arduino", "00aabbccde03", "status", "nonjson", 0);
		client.subscribeToDeviceEvents(getDeviceType(), getDeviceId(), getEventType(), getFormatType(), 0);
			
/*			
		while (true) {
			Thread.sleep(1000);
		}
*/			
	}

	private class MyEventCallback implements EventCallback {

		@Override
		public void processEvent(Event e) {
			System.out.println("Event " + e.getPayload());
		}

		@Override
		public void processCommand(Command cmd) {
			System.out.println("Command " + cmd.getPayload());			
		}
	}
	
	private class MyStatusCallback implements StatusCallback {

		@Override
		public void processApplicationStatus(ApplicationStatus status) {
			System.out.println("Application Status = " + status.getPayload());
		}

		@Override
		public void processDeviceStatus(DeviceStatus status) {
			System.out.println("Device Status = " + status.getPayload());
		}
	}
	
	public static void main(String[] args) {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("Enter an id for creating MQTT client id(or blank for ThingsMax to generate one) ");
		String id = null, apiAuthKey = null, apiAuthToken = null, deviceType = null, 
				deviceId = null, eventType = null, formatType = null;
		try {
			id = br.readLine().trim();
			if(id == null || id.equals("")) {
				id = String.valueOf(new Date().getTime());
				System.out.println("ThingsMax has generated an id = " + id);
			}
			System.out.print("Enter the API Auth key = ");
			apiAuthKey = br.readLine().trim();
			System.out.print("Enter the API Auth token = ");
			apiAuthToken = br.readLine().trim();

			System.out.print("Enter the Device Type = ");
			deviceType = br.readLine().trim();
			System.out.print("Enter the Device Id = ");
			deviceId = br.readLine().trim();

			System.out.print("Enter the Event Type = ");
			eventType = br.readLine().trim();
			System.out.print("Enter the Format Type(or blank for json) = ");
			formatType = br.readLine().trim();
			if(formatType == null || formatType.equals("")) {
				formatType = "json";
				System.out.println("ThingsMax has defaulted to \"" + formatType + "\" for format\n");
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ThingsMaxIoTFAgent tc = new ThingsMaxIoTFAgent(id, apiAuthKey, apiAuthToken);
		
		//Get list of devices from cloudant

		
		//Iteratate through all the devices and subscribe to each of them

		tc.setDeviceId(deviceId);
		tc.setDeviceType(deviceType);
		tc.setEventType(eventType);
		tc.setFormatType(formatType);
		
		
		tc.subscribe();
		
		System.out.println("Connected successfully - Your device ID is " + tc.client.getAppId());
		System.out.println(" * Organization: " + tc.client.getOrgId() + " (" + tc.client.getAuthToken() + ")");			
	}
}
