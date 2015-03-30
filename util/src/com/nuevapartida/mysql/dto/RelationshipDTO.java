package com.nuevapartida.mysql.dto;

public class RelationshipDTO {
	private long id;
	private long parent_id;
	private long child_id;
	private long type_id;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}	
	public long getParentId() {
		return parent_id;
	}
	public void setParentId(long parent_id) {
		this.parent_id = parent_id;
	}
	public long getChildId() {
		return child_id;
	}
	public void setChildId(long child_id) {
		this.child_id = child_id;
	}
	public long getTypeId() {
		return type_id;
	}
	public void setTypeId(long type_id) {
		this.type_id = type_id;
	}
}
