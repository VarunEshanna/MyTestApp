package com.adobe.sfdc.pojo;

import java.util.ArrayList;
import java.util.List;

public class TestData {

	private String userQuery;
	private List<UserData> userData;
	private String intentName;
	private Boolean luisCallRequired;
	private Boolean finalResponse;
	private String finalResponseText;
	private String finalResponseType;
	
	public TestData() {
		super();
	}
	
	public String getUserQuery() {
		return userQuery;
	}
	public void setUserQuery(String userQuery) {
		this.userQuery = userQuery;
	}
	public String getIntentName() {
		return intentName;
	}
	public void setIntentName(String intentName) {
		this.intentName = intentName;
	}
	public Boolean getLuisCallRequired() {
		return luisCallRequired;
	}
	public void setLuisCallRequired(Boolean luisCallRequired) {
		this.luisCallRequired = luisCallRequired;
	}
	public String getFinalResponseText() {
		return finalResponseText;
	}
	public void setFinalResponseText(String finalResponseText) {
		this.finalResponseText = finalResponseText;
	}
	public String getFinalResponseType() {
		return finalResponseType;
	}
	public void setFinalResponseType(String finalResponseType) {
		this.finalResponseType = finalResponseType;
	}
	public List<UserData> getUserData() {
		if(userData == null){
			userData = new ArrayList<UserData>();
		}
		return userData;
	}
	public void setUserData(List<UserData> userData) {
		this.userData = userData;
	}

	public Boolean getFinalResponse() {
		return finalResponse;
	}

	public void setFinalResponse(Boolean finalResponse) {
		this.finalResponse = finalResponse;
	}

	@Override
	public String toString() {
		return "TestData [userQuery=" + userQuery + ", userData=" + userData + ", intentName=" + intentName
				+ ", luisCallRequired=" + luisCallRequired + ", finalResponse=" + finalResponse + ", finalResponseText="
				+ finalResponseText + ", finalResponseType=" + finalResponseType + "]";
	}
	
}
