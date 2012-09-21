package org.societies.example;

import java.util.Arrays;

public class Data {
	private int id;
	private int type;
	private String[] data;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String[] getData() {
		return data;
	}
	public void setData(String[] data) {
		this.data = data;
	}
	@Override
	public String toString() {
		return "Data [id=" + id + ", type=" + type + ", "
				+ (data != null ? "data=" + Arrays.toString(data) : "") + "]";
	}
	
	
}
