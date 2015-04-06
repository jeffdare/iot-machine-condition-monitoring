package com.ibm.xom.model;

public class Asset {
	
	private String assetID;
	
	private String assetType;
	
	

	public Asset() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Asset(String assetID, String assetType) {
		super();
		this.assetID = assetID;
		this.assetType = assetType;
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
	
	

}
