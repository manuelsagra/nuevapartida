package com.nuevapartida.beans;

import java.util.List;

public class Edition implements Comparable<Edition> {
	String title;
	List<String> region;
	String system;
	List<String> publisher;
	String productId;
	String barcode;
	String date;
	String rating;
	String cover;
	String comment;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public List<String> getRegion() {
		return region;
	}
	public void setRegion(List<String> region) {
		this.region = region;
	}
	public String getSystem() {
		return system;
	}
	public void setSystem(String system) {
		this.system = system;
	}
	public List<String> getPublisher() {
		return publisher;
	}
	public void setPublisher(List<String> publisher) {
		this.publisher = publisher;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getBarcode() {
		return barcode;
	}
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getRating() {
		return rating;
	}
	public void setRating(String rating) {
		this.rating = rating;
	}
	public String getCover() {
		return cover;
	}
	public void setCover(String cover) {
		this.cover = cover;
	}
	public String getCoverURL() {
		return (system != null ? system.toUpperCase() : "SS") + "-" + (region != null ? region.get(0).toUpperCase() : "RR") + "-" + (title != null ? title : "Título Desconocido") + "-" + (productId != null ? productId : "ID");
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String toString() {
		String txt = (title != null ? "Título: " + title + "\n" : "") + 
				(cover != null ? "Carátula: " + cover + "\n" : "") + 
				(region != null ? "Región: " + region + "\n" : "") +
				(publisher != null ? "Publicado por: " + publisher + "\n" : "") + 
				(productId != null ? "ID: " + productId + "\n" : "") + 
				(barcode != null ? "Código de barras: " + barcode + "\n" : "") + 
				(date != null ? "Fecha: " + date + "\n" : "") + 
				(rating != null ? "Clasificación de edad: " + rating + "\n" : "") +
				(comment != null ? "Comentario: " + comment + "\n" : "");
		return txt;
	}
	
	@Override
	public int compareTo(Edition e) {
		return title.compareTo(e.getTitle());
	}
}
