package com.adobe.sfdc.pojo;

public class UserData {
	
	
	public UserData() {
		super();
	}
	
	private String entityName;
	private String userText;
	private String responseText;
	private String userDataType;
	
	public String getEntityName() {
		return entityName;
	}
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}
	public String getUserText() {
		return userText;
	}
	public void setUserText(String userText) {
		this.userText = userText;
	}
	public String getResponseText() {
		return responseText;
	}
	public void setResponseText(String responseText) {
		this.responseText = responseText;
	}
	public String getUserDataType() {
		return userDataType;
	}
	public void setUserDataType(String userDataType) {
		this.userDataType = userDataType;
	}
	@Override
	public String toString() {
		return "UserData [entityName=" + entityName + ", userText=" + userText + ", responseText=" + responseText
				+ ", userDataType=" + userDataType + "]";
	}
}
