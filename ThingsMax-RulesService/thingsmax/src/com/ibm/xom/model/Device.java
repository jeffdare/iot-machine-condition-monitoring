package com.ibm.xom.model;

public class Device {
	
	private String deviceID = "";
	
	private String deviceType = "";
	
	private String myName = "";
	
	private int temp;
	
	private int humidity;
	
	private int pressure;
	
	private int voltage;
	
	private int rpm;
	
	private String message = "";
	
	
	public Device() {
		super();
		// TODO Auto-generated constructor stub
	}



	public Device(String deviceID, String deviceType, String myName, int temp,
			int humidity, int pressure, int voltage, int rpm, String message) {
		super();
		this.deviceID = deviceID;
		this.deviceType = deviceType;
		this.myName = myName;
		this.temp = temp;
		this.humidity = humidity;
		this.pressure = pressure;
		this.voltage = voltage;
		this.rpm = rpm;
		this.message = message;
	}



	public void setTemp(int temp) {
		this.temp = temp;
	}

	public void setHumidity(int humidity) {
		this.humidity = humidity;
	}

	public void setPressure(int pressure) {
		this.pressure = pressure;
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


	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getTemp() {
		return temp;
	}

	public int getHumidity() {
		return humidity;
	}

	public int getPressure() {
		return pressure;
	}


	public String getMyName() {
		return myName;
	}

	public void setMyName(String myName) {
		this.myName = myName;
	}

	public int getVoltage() {
		return voltage;
	}

	public void setVoltage(int voltage) {
		this.voltage = voltage;
	}

	public int getRpm() {
		return rpm;
	}

	public void setRpm(int rpm) {
		this.rpm = rpm;
	}

	
}
