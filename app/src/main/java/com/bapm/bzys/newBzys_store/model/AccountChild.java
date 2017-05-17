package com.bapm.bzys.newBzys_store.model;

import java.io.Serializable;

public class AccountChild implements Serializable{
	private static final long serialVersionUID = 6611733607616370120L;
	private String id;
	private String name;
	private String phone;
	private String pwd;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	
}
