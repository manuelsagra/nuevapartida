package com.nuevapartida.beans;

import java.util.List;

public class Version implements Comparable<Version> {
	String title;
	List<String> alt;
	String system;
	List<String> genre;
	String url;
	String date;
	List<String> developer;
	List<Edition> editions;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public List<String> getAlt() {
		return alt;
	}
	public void setAlt(List<String> alt) {
		this.alt = alt;
	}
	public String getSystem() {
		return system;
	}
	public void setSystem(String system) {
		this.system = system;
	}
	public List<String> getGenre() {
		return genre;
	}
	public void setGenre(List<String> genre) {
		this.genre = genre;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public List<String> getDeveloper() {
		return developer;
	}
	public void setDeveloper(List<String> developer) {
		this.developer = developer;
	}
	public List<Edition> getEditions() {
		return editions;
	}
	public void setEditions(List<Edition> editions) {
		this.editions = editions;
	}
	public String toString() {
		String txt = (title != null ? "Título: " + title + "\n" : "") +
				(system != null ? "Sistema: " + system + "\n" : "") +
				(alt != null ? "Título alternativo: " + alt + "\n" : "") +
				(date != null ? "Fecha: " + date + "\n" : "") + 
				(genre != null ? "Género: " + genre + "\n" : "") + 
				(developer != null ? "Desarrollado por: " + developer + "\n" : "");		
		if (editions != null) {
			txt += "Ediciones:\n";
			for (Edition e : editions) {
				txt += "-------------------\n" + e.toString();
			}		
		}
		return txt + "\n";
	}
	public String getImageURL() {
		return (system != null ? system.toUpperCase() : "SS") + "-" + (title != null ? title : "Título Desconocido");
	}
	
	@Override
	public int compareTo(Version v) {
		return title.compareTo(v.getTitle());
	}
}
