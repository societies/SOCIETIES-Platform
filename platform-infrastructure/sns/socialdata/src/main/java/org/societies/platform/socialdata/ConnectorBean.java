package org.societies.platform.socialdata;

import java.io.Serializable;

public class ConnectorBean implements Serializable{

	String identity;
	String snName;
	String token;
	long exipres=0;
	String id;
	
	public String getIdentity() {
		return identity;
	}
	public void setIdentity(String identity) {
		this.identity = identity;
	}
	public String getSnName() {
		return snName;
	}
	public void setSnName(String snName) {
		this.snName = snName;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public long getExipres() {
		return exipres;
	}
	public void setExipres(long exipres) {
		this.exipres = exipres;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String toString(){
		return "identity="+identity +
				", id="+id + 
				" ,snName="+snName + 
				" ,token="+token+
				" ,expires="+exipres;
				
	}
	
	

}
