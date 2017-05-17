package com.bapm.bzys.newBzys_store.model;

import java.io.Serializable;

public class GoodsType implements Serializable{
	private static final long serialVersionUID = 1L;
	private String id;
	private String name;
	private String level;
	private String count;
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
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getCount() {
		return count;
	}
	public void setCount(String count) {
		this.count = count;
	}
}
