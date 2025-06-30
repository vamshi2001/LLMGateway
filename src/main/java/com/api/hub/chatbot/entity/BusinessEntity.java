package com.api.hub.chatbot.entity;

public class BusinessEntity {

	public Object get_id() {
		return _id;
	}
	public void set_id(Object _id) {
		this._id = _id;
	}
	public Object getEmbedding() {
		return embedding;
	}
	public void setEmbedding(Object embedding) {
		this.embedding = embedding;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	Object _id;
	Object embedding;
	String title;
	String category;
	String description;
	
	public String toString() {
		return title+":"+description;
	}
}
