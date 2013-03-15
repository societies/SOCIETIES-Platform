/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.context.location.management.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.context.location.management.api.*;
import org.societies.context.location.management.api.impl.CoordinateImpl;
import org.societies.context.location.management.api.impl.TagImpl;
import org.societies.context.location.management.api.impl.UserLocationImpl;
import org.societies.context.location.management.api.impl.ZoneIdImpl;
import org.societies.context.location.management.api.impl.ZoneImpl;
import org.societies.context.location.management.coordinates.convertor.Pix2Geo;
import org.societies.context.location.management.coordinates.convertor.Point;
import org.societies.context.location.management.PZWrapper;
import org.societies.context.location.management.PzPropertiesReader;



public class PZWrapperImpl implements PZWrapper  {

	/** The logging facility. */
	private static final Logger log = LoggerFactory.getLogger(PZWrapperImpl.class);
	
	private String PZ_FULL_ENTITY;
	private String ENTITY_ID;
	
	Pix2Geo pix2GeoConvertor = null;
	private boolean convertToGeo= false;
	
	private String productionQueryURL;
	private String productionAdminURL;
	private boolean locationSystemActive=false;
	
	
	@SuppressWarnings("unused")
	private void init(){
		PzPropertiesReader pzPropertiesReader = PzPropertiesReader.instance();
		PZ_FULL_ENTITY = productionQueryURL +"/"+ pzPropertiesReader.getEntityFullQuery();
		ENTITY_ID = pzPropertiesReader.getEntityId();
		
		/*
		try{
			initialPixel2GeoConvertor();
			convertToGeo = true;
		}catch (Exception e) {
			log.error("can't initial pix2GeoConvertor ; coordinates will be given as in PZ server",e);
		}*/
	}
	
	private void initialPixel2GeoConvertor() throws Exception{
		JSONObject jsonObject = null;
		try{
			if (pix2GeoConvertor == null){
				PzPropertiesReader pzPropertiesReader = PzPropertiesReader.instance();
				String url = productionAdminURL +"/" +pzPropertiesReader.getMapQuery();
				jsonObject = restCallHelperMethod(url);
				pix2GeoConvertor = new Pix2Geo(jsonObject);
				convertToGeo = true;
			}
		}catch (Exception e) {
			log.error("can't initial pix2GeoConvertor ; coordinates will be given as in PZ server; rest call to PZ returned: "+jsonObject,e);
		}	
	}
	
	/**************************************************************/
	/* http://ta-proj02:9082/QueriesGatewayREST/RT/allActiveZones */
	/**************************************************************/
	@Override
	public Collection<IZone> getActiveZones() {
		// TODO Auto-generated method stub
		return null;
	}

	/********************************************************************************/
	/* http://ta-proj02:9082/QueriesGatewayREST/RT/activeEntitiesIdsInZone/{zoneId} */
	/********************************************************************************/
	@Override
	public Set<String> getActiveEntitiesIdsInZone(IZoneId zoneId) {
		// TODO Auto-generated method stub
		return null;
	}

	
	/*******************************************************************************/
	/* http://ta-proj02:9082/QueriesGatewayREST/RT/location/full/entity/{entityId} */
	/*******************************************************************************/
	@Override
	public IUserLocation getEntityFullLocation(String entityId) {
		log.debug("enter: getEntityFullLocation ; entityId ="+entityId);
		
		IUserLocation userLocation = null;
		try{
			initialPixel2GeoConvertor();
			
			String url = PZ_FULL_ENTITY.replaceFirst(ENTITY_ID, entityId);
			
			JSONObject jsonResponse = restCallHelperMethod(url);
			if (jsonResponse != null){
				
				userLocation = toIUserLocation(jsonResponse);
				
				if (userLocation == null){
					log.debug("Json object for '"+ENTITY_ID+"' isn't valid. Entity wasn't found \t Json = "+jsonResponse);
					return userLocation;
					
				}else if (convertToGeo){
					Point point = pix2GeoConvertor.convertPixToGeo(new Point(userLocation.getXCoordinate().getCoordinate(),
														   				     userLocation.getYCoordinate().getCoordinate())
																   );
					
					log.debug("user Location coverted to "+point.toString()+" \t original Json "+jsonResponse);
					userLocation.setXCoordinate(new CoordinateImpl(point.getX()));
					userLocation.setYCoordinate(new CoordinateImpl(point.getY()));
				}
				
			}else{
				log.error("error in method 'getEntityFullLocation' with entity '"+entityId+"' , returned JSON object is NULL" );
			}
		}catch (Exception e) {
			log.error("Exception msg: "+e.getMessage() +" ; cause: "+e.getCause(),e);
			return null;
		}
		
		log.debug("finish: getEntityFullLocation ; entityId ="+entityId);
		return userLocation;
	}
	
	private IUserLocation toIUserLocation(JSONObject jsonObject){
		log.debug("start method 'toIUserLocation' \t Json: "+jsonObject);
		
		IUserLocation userLocation = new UserLocationImpl();
		try{
			//verifies that the JSON object is valid
			if (!isJsonValid(jsonObject)){
				return null;
			}
			
			
			double x = jsonObject.getDouble("x");
			userLocation.setXCoordinate(new CoordinateImpl(x));
			
			double y = jsonObject.getDouble("y");
			userLocation.setYCoordinate(new CoordinateImpl(y));
			
			//double z = ((Double)jsonObject.get("z")).doubleValue();
			//userLocation.setZCoordinate(new CoordinateImpl(z));
			
			JSONArray zonesArray = jsonObject.getJSONArray("zones");
			List<IZone> zonesList = new ArrayList<IZone>();
			IZone zone= null;
			JSONObject currZoneObject;
			for (int i=0; i < zonesArray.length(); i++){
				currZoneObject = (JSONObject) zonesArray.get(i);
				zone = new ZoneImpl();
				
				String value = currZoneObject.getString("personalTag");
				ITag personalTag = new TagImpl(value);
				zone.setPersonalTag(personalTag);
				
				JSONArray jSONArrayTags = currZoneObject.getJSONArray("tags");
				List<ITag> tagCollection = new ArrayList<ITag>();
				for (int j=0; j < jSONArrayTags.length(); j++){
					String str = jSONArrayTags.get(j).toString();
					tagCollection.add(new TagImpl(str));
				}
				zone.setTags(tagCollection);
				
				long zoneId = ((Number)currZoneObject.get("id")).longValue();
				IZoneId zoneIdObj = new ZoneIdImpl();
				zoneIdObj.setId(zoneId);
				zone.setId(zoneIdObj);
				
				zone.setType(currZoneObject.getString("type"));
				zone.setName(currZoneObject.getString("name"));
				zone.setDescription(currZoneObject.getString("description"));
				zonesList.add(zone);
			}
			
			
			userLocation.setZones(zonesList);
			
			log.debug("finish method 'toIUserLocation' \t Json: "+jsonObject);
			
		}catch (Exception e) {
			log.error("Exception was caught: JSON object- "+jsonObject, e);
		}
		return userLocation;
	}
	
	
	/**
	 * Verify that the given JSON is valid location
	 * @param jsonObject
	 * @return
	 */
	private boolean isJsonValid(JSONObject jsonObject) {
		
		JSONArray zonesArray;
		try {
			zonesArray = jsonObject.getJSONArray("zones");
			
			if (jsonObject.get("x") == null ||  jsonObject.get("y") == null ||
				zonesArray.length() == 0 || !hasRealCoordinates(jsonObject) ){
					
					log.debug("Json object isn't valid, no valid location , JSON =  "+jsonObject.toString());	
					return false;
			}
		
		} catch (JSONException e) {
			log.error("JsonException ; Msg: "+e.getMessage() +" ; cause: " +e.getCause());
			return false;
		}
		
		return true;
	}

	private boolean hasRealCoordinates(JSONObject jsonObject){
		boolean value = true;
		try{
			jsonObject.getDouble("x");
			jsonObject.getDouble("y");
		}catch(Exception e){
			value=false;
		}
		return value;
	}
	private JSONObject restCallHelperMethod(String url){
		
		HttpGet httpGetRequest = new HttpGet(url);
		httpGetRequest.addHeader("accept", "application/json");
    	HttpClient httpclient = new DefaultHttpClient();
    	HttpResponse response;
    	int statusCode;
    	String responseString="";
    	JSONObject jsonObject=null;
    	
		try {
			log.debug("in method 'restCallHelperMethod' \t rest call to : " +url);
			
			response = httpclient.execute(httpGetRequest);
			statusCode = response.getStatusLine().getStatusCode();
	    	if (response.getEntity() != null && response.getEntity().getContent() != null){
				BufferedReader bf = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				String temp;
				while ((temp = bf.readLine()) != null) {
					responseString+= temp;	
				}
				
				if (responseString.length() == 0){
					log.warn("warning in method 'restCallHelperMethod' response string is empty ; status code = "+statusCode);
					return null;
				}
				
				jsonObject = new JSONObject(responseString);
	    	}
	    	
		} catch (IOException e) {
			log.error("Exception msg: "+e.getMessage()+"\t cause: "+e.getCause(),e);
		} catch (JSONException e) {
			log.error("Exception msg: "+e.getMessage()+"\t cause: "+e.getCause(),e);
		}
    	return jsonObject;
	}


		public String getProductionQueryURL() {
			return productionQueryURL;
		}
	
	
		public void setProductionQueryURL(String productionQueryURL) {
			this.productionQueryURL = productionQueryURL;
		}
	
	
		public String getProductionAdminURL() {
			return productionAdminURL;
		}
	
	
		public void setProductionAdminURL(String productionAdminURL) {
			this.productionAdminURL = productionAdminURL;
		}
	
	
		public boolean isLocationSystemActive() {
			return locationSystemActive;
		}
	
	
		public void setLocationSystemActive(boolean locationSystemActive) {
			this.locationSystemActive = locationSystemActive;
		}

}
