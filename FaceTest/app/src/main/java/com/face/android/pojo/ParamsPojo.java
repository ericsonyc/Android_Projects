package com.face.android.pojo;

public class ParamsPojo {

	private String paramsName;
	private String paramsValue;
	
	public ParamsPojo(){}

	public ParamsPojo(String paramsName, String paramsValue) {
		super();
		this.paramsName = paramsName;
		this.paramsValue = paramsValue;
	}

	public String getParamsName() {
		return paramsName;
	}

	public void setParamsName(String paramsName) {
		this.paramsName = paramsName;
	}

	public String getParamsValue() {
		return paramsValue;
	}

	public void setParamsValue(String paramsValue) {
		this.paramsValue = paramsValue;
	}
	
}
