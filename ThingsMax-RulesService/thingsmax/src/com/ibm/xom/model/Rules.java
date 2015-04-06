package com.ibm.xom.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Rules {

	private String ruleName;
	
	private String ruleURL;
	
	private String ruleExecutionTimeStamp;

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public String getRuleURL() {
		ruleURL = "https://brsv2-85f4a9e4.stage1.ng.bluemix.net/DecisionService/rest/v1/thingsmaxRuleApp/1.0/thingsmaxRuleProject/1.0";
		return ruleURL;
	}

	public void setRuleURL(String ruleURL) {
		this.ruleURL = ruleURL;
	}

	public String getRuleExecutionTimeStamp() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String strDate = dateFormat.format(new Date());
		return strDate;
	}

	public void setRuleExecutionTimeStamp(String ruleExecutionTimeStamp) {
		this.ruleExecutionTimeStamp = ruleExecutionTimeStamp;
	}

	
	
	
}
