package com.nuevapartida.mysql.dto;

public class ItemTagDTO {
	private long item_id;
	private long tag_id;
	
	public long getItemId() {
		return item_id;
	}
	public void setItemId(long item_id) {
		this.item_id = item_id;
	}
	public long getTagId() {
		return tag_id;
	}
	public void setTagId(long tag_id) {
		this.tag_id = tag_id;
	}
}
