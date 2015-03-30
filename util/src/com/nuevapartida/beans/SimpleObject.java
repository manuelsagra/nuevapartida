package com.nuevapartida.beans;

import java.util.List;

public class SimpleObject {
	String name;
	String code;
	List<SimpleObject> children;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public List<SimpleObject> getChildren() {
		return children;
	}
	public void setChildren(List<SimpleObject> children) {
		this.children = children;
	}
}
