package com.bapm.bzys.newBzys_store.model;

import java.io.Serializable;

public class OrderType implements Serializable {
	private String typeNo;
	private String typeName;
	public OrderType(String typeNO,String typeName){
		this.typeNo = typeNO;
		this.typeName = typeName;
	}
	public String getTypeNo() {
		return typeNo;
	}
	public void setTypeNo(String typeNo) {
		this.typeNo = typeNo;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	
}
