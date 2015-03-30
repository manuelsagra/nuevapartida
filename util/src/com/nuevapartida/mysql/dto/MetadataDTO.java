package com.nuevapartida.mysql.dto;

public class MetadataDTO {
	private long id;
	private long item_id;
	private long type_id;
	private String name;
	private String value;
	private String comment;
	
	public long getItemId() {
		return item_id;
	}
	public void setItemId(long item_id) {
		this.item_id = item_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getTypeId() {
		return type_id;
	}
	public void setTypeId(long type_id) {
		this.type_id = type_id;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
}
