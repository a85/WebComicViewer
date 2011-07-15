package com.rickreation.webcomicviewer.models;

public class Comic {
	int id;
	String title;
	int num_strips;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getNum_strips() {
		return num_strips;
	}
	public void setNum_strips(int num_strips) {
		this.num_strips = num_strips;
	}	
}
