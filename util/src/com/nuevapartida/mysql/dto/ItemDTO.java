package com.nuevapartida.mysql.dto;

import java.util.Date;

public class ItemDTO implements Comparable<ItemDTO> {
	private long id;
	private long parent_id;
	private long type_id;
	private String name;
	private String altname;
	private String shortname;
	private String date;
	private Date modified;
	private String content;
	private String excerpt;
	private String status;
	
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAltname() {
		return altname;
	}
	public void setAltname(String altname) {
		this.altname = altname;
	}
	public String getShortname() {
		return shortname;
	}
	public void setShortname(String shortname) {
		this.shortname = shortname;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public Date getModified() {
		return modified;
	}
	public void setModified(Date modified) {
		this.modified = modified;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getExcerpt() {
		return excerpt;
	}
	public void setExcerpt(String excerpt) {
		this.excerpt = excerpt;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public long getParentId() {
		return parent_id;
	}
	public void setParentId(long parent_id) {
		this.parent_id = parent_id;
	}
	
	@Override
	public int compareTo(ItemDTO o) {
		return name.compareTo(o.getName());
	}
}
