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

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Asset implements Serializable {

	private static final long serialVersionUID = -4289486454276750990L;

	private String assetID;
	private String assetType;
	private String assetOrg;
	private String siteID;

	private String deviceID;
	private String deviceType;
	private String deviceOrg;
	private String apptoken;
	private String appkey;

	private ArrayList devices;
	private ArrayList rules;

	public Asset() {
	}

	public Asset(String assetID, String assetType, String assetOrg,
			String siteid, ArrayList devices, String deviceID,
			String deviceType, String deviceOrg, String apptoken, String appkey) {
		this.assetID = assetID;
		this.assetType = assetType;
		this.assetOrg = assetOrg;
		this.siteID = siteid;
		
		System.out.println("siteID === "+siteID);

		this.deviceID = deviceID;
		this.deviceType = deviceType;
		this.deviceOrg = deviceOrg;
		this.apptoken = apptoken;
		this.appkey = appkey;
	}

	// // asset details
	@JsonProperty("siteid")
	public String getsiteID() {
		System.out.println("siteID === "+siteID);
		return siteID;
	}

	public void setsiteID(String siteid) {
		this.siteID = siteid;
		
		System.out.println("siteID === "+siteID);
	}

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

	// // device details
	public ArrayList getDevices() {
		return devices;
	}

	public void setDevices(ArrayList devices) {
		this.devices = devices;
	}

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

	public String getappKey() {
		return appkey;
	}

	public void setappKey(String appkey) {
		this.appkey = appkey;
	}

	public String getappToken() {
		return apptoken;
	}

	public void setappToken(String apptoken) {
		this.apptoken = apptoken;
	}
}