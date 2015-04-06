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

public class ThingsAction implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8158752427941281199L;

	private String thingsActionUserId;
	
	private String thingsActionPassword;
	
	private String thingsActionURL;

	private String actionId;
	
	public ThingsAction() {
		
	}

	public ThingsAction(String actionId, String thingsActionUserId, String thingsActionPassword, String thingsActionURL) {
		this.actionId = actionId;
		this.thingsActionUserId = thingsActionUserId;
		this.thingsActionPassword = thingsActionPassword;
		this.thingsActionURL = thingsActionURL;
	}
	
	public String getThingsActionUserId() {
		return thingsActionUserId;
	}

	public void setThingsActionUserId(String maximoUserId) {
		this.thingsActionUserId = maximoUserId;
	}

	public String getThingsActionPassword() {
		return thingsActionPassword;
	}

	public void setThingsActionPassword(String maximoPassword) {
		this.thingsActionPassword = maximoPassword;
	}

	public String getThingsActionURL() {
		return thingsActionURL;
	}

	public void setThingsActionURL(String maximoURL) {
		this.thingsActionURL = maximoURL;
	}

	public String getActionId() {
		return actionId;
	}

	public void setActionId(String actionId) {
		this.actionId = actionId;
	}
	
	
}
