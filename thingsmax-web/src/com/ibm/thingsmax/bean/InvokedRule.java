/**
 *****************************************************************************
 Copyright (c) 2014 IBM Corporation and other Contributors.
 All rights reserved. 
 Contributors:
 IBM - Initial Contribution
 *****************************************************************************
 * 
 */

package com.ibm.thingsmax.bean;

import java.io.Serializable;

public class InvokedRule implements Serializable {

	private static final long serialVersionUID = -4289486454276750989L;

	private String deviceID;
	private String deviceType;

	private String assetID;
	private String assetType;
	
	private String message;
	
	private String decisionID;
	

	public InvokedRule() {
	}

	public InvokedRule(String assetID, String assetType, String deviceID, String deviceType,
			String decisionID, String message) {
		this.assetID = assetID;
		this.assetType = assetType;
		this.deviceID = deviceID;
		this.deviceType = deviceType;
		this.decisionID = decisionID;
		this.message = message;

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


	public String getDecisionID() {
		return decisionID;
	}

	public void setDecisionID(String decisionID) {
		this.decisionID = decisionID;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}