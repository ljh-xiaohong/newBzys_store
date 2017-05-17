package com.bapm.bzys.newBzys_store.network.bean;

import org.json.JSONArray;
import org.json.JSONObject;

public class Result {
	
	private int requestCode;
	private int status;
	private String msg;
	private JSONObject resultJson;
	private JSONArray resultArray;
	
	public int getRequestCode() {
		return requestCode;
	}
	public void setRequestCode(int requestCode) {
		this.requestCode = requestCode;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public JSONObject getResultJson() {
		return resultJson;
	}
	public void setResultJson(JSONObject resultJson) {
		this.resultJson = resultJson;
	}
	public JSONArray getResultArray() {
		return resultArray;
	}
	public void setResultArray(JSONArray resultArray) {
		this.resultArray = resultArray;
	}
	
}
