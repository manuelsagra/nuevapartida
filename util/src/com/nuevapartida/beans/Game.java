package com.nuevapartida.beans;

import java.util.ArrayList;
import java.util.List;

public class Game implements Comparable<Game> {
	String title;
	List<Version> versions = new ArrayList<Version>();
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public List<Version> getVersions() {
		return versions;
	}
	public void setVersions(List<Version> versions) {
		this.versions = versions;
	}
	
	@Override
	public int compareTo(Game g) {
		return title.compareTo(g.getTitle());
	}
}
