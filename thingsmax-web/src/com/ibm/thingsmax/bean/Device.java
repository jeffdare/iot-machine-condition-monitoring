/**
 *****************************************************************************
 Copyright (c) 2015 IBM Corporation and other Contributors.
 All rights reserved. 
 Contributors:
 IBM - Initial Contribution
 *****************************************************************************
 * 
 */

package com.ibm.thingsmax.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class Device implements Serializable {

	private static final long serialVersionUID = -4289486454276750989L;

	private String deviceID;
	private String deviceType;
	private String deviceOrg;
	private String deviceFormat;
	private String deviceBrokerHost;
	private String deviceBrokerPort;
	private String deviceEventType;

	private String assetID;
	private String assetType;
	private String assetOrg;
	private String siteid;

	private ArrayList rules;

	public Device() {
	}

	public Device(String assetID, String assetType, String assetOrg,
			ArrayList rules, String deviceID, String deviceType,
			String deviceOrg, String deviceFormat, String deviceBrokerHost,
			String deviceBrokerPort, String deviceEventType) {
		this.assetID = assetID;
		this.assetType = assetType;
		this.assetOrg = assetOrg;

		this.deviceID = deviceID;
		this.deviceType = deviceType;
		this.deviceOrg = deviceOrg;
		this.deviceFormat = deviceFormat;
		this.deviceBrokerHost = deviceBrokerHost;
		this.deviceBrokerPort = deviceBrokerPort;
		this.deviceEventType = deviceEventType;
	}
	
	/// device details
	public String getDeviceID() {
		return deviceID;
	}

	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}
	
	public String getDeviceOrg() {
		return deviceOrg;
	}

	public void setDeviceOrg(String deviceOrg) {
		this.deviceOrg = deviceOrg;
	}

	public String getDeviceFormat() {
		return deviceFormat;
	}

	public void setDeviceFormat(String deviceFormat) {
		this.deviceFormat = deviceFormat;
	}
	
	public String getDeviceBrokerHost() {
		return deviceBrokerHost;
	}

	public void setDeviceBrokerHost(String deviceBrokerHost) {
		this.deviceBrokerHost = deviceBrokerHost;
	}
	
	public String getDeviceBrokerPort() {
		return deviceBrokerPort;
	}

	public void setDeviceBrokerPort(String deviceBrokerPort) {
		this.deviceBrokerPort = deviceBrokerPort;
	}
	
	public String getDeviceEventType() {
		return deviceEventType;
	}

	public void setDeviceEventType(String deviceEventType) {
		this.deviceEventType = deviceEventType;
	}

	
	/// asset details
	public String getAssetID() {
		return assetID;
	}

	public void setAssetID(String assetID) {
		this.assetID = assetID;
	}

	public String getAssetType() {
		return assetType;
	}

	public void setAssetType(String assetType) {
		this.assetType = assetType;
	}

	public String getAssetOrg() {
		return assetOrg;
	}

	public void setAssetOrg(String assetOrg) {
		this.assetOrg = assetOrg;
	}
	
	
	/// rule details
	public ArrayList getRules() {
		return rules;
	}

	public void setRules(ArrayList rules) {
		this.rules = rules;
	}
}