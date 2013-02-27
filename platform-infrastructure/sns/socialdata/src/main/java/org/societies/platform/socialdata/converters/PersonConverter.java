package org.societies.platform.socialdata.converters;

import org.json.JSONException;
import org.json.JSONObject;



 abstract class PersonConverter implements IPersonConverter{

	String rawData;
	
	
	public String toString(){
		
		String error ="";
		try {
			JSONObject jObj = new JSONObject(rawData);
			return jObj.toString(1);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			error= e.getMessage();
			e.printStackTrace();
		}
		
		return "{\"error\": \""+ error + "\"}";
	}
	
	
}
