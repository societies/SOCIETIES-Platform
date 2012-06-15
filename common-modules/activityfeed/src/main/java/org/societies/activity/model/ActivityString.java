package org.societies.activity.model;

public class ActivityString {
	private String data;
	public ActivityString(String data){
		this.data = data;
	}
	public Boolean contains(String s){
		return data.contains(s);
	}
	public Boolean equals(String s){
		return data.equals(s);
	}
	public Boolean startsWith(String s){
		return data.startsWith(s);
	}
	public Boolean presents(String s){
		return data != null;
	}
	public Boolean isNull(String s){
		return data == null;
	}
	public String getString(){
		return data;
	}
	public String toString(){
		return data;
	}
}
