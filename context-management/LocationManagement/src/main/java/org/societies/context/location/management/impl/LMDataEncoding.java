package org.societies.context.location.management.impl;

import org.societies.context.api.user.location.ITag;
import org.societies.context.api.user.location.IUserLocation;
import org.societies.context.api.user.location.IZone;

/*
 * 
 *	LOCATION_SYMBOLIC ->   (zoneId_X, Zone "name"),(zoneId_Y, Zone "name")
 *	LOCATION_PUBLIC_TAGS -> (zoneId_X, Zone tag1) ,(zoneId_X, Zone tag2) ,(zoneId_Y, Zone tag3) 
 *	LOCATION_PERSONAL_TAGS ->  (zoneId_X, Zone tag1), (zoneId_Y, Zone tag2)
 *	LOCATION_TYPE -> (zoneId_X, type),(zoneId_y, type)
 *	LOCATION_ID -> zoneId_X,zoneId_Y
 * 
 */

public class LMDataEncoding {
	
	String encodeLocationSymbolic;
	
	public static String encodeLocationSymbolic(IUserLocation userLocation){
		String symbolicLocationString = "";
		for (IZone zone : userLocation.getZones()){
			symbolicLocationString += "("+zone.getId().getId()+"," + zone.getName()+")";
		}
		return symbolicLocationString;
	}
	
	public static String encodeCoordinates(IUserLocation userLocation){
		
		return userLocation.getXCoordinate().getCoordinate()+","+userLocation.getYCoordinate().getCoordinate();
		
	}
	
	
	public static String encodePersonalTags(IUserLocation userLocation){
		String str = "";
		for (IZone zone : userLocation.getZones()){
			str += "("+zone.getId().getId()+"," + zone.getPersonalTag().getTag()+")";
		}
		return str;
	}
	 
	public static String encodePublicTags(IUserLocation userLocation){
		String str = "";
		for (IZone zone : userLocation.getZones()){
			for (ITag tag : zone.getTags()){
				str += "("+zone.getId().getId()+"," + tag.getTag()+")";
			}
		}
		return str;
	}
	
	public static String encodeZoneType(IUserLocation userLocation){
		String str = "";
		for (IZone zone : userLocation.getZones()){
			str += "("+zone.getId().getId()+"," + zone.getType()+")";
		}
		return str;
	}
	
	public static String encodeZones(IUserLocation userLocation){
		String str = "";
		for (IZone zone : userLocation.getZones()){
			if (!"root-zone".equalsIgnoreCase(zone.getType())){
				str += zone.getId().getId() + ",";
			}
		}
		
		if (str.endsWith(",")){
			str = str.substring(0,str.length()-1);
		}
		return str;
	}

	public static String encodeParentZones(IUserLocation userLocation){
		String str = "";
		for (IZone zone : userLocation.getZones()){
			if ("root-zone".equalsIgnoreCase(zone.getType())){
				str += zone.getId().getId() + ",";
			}
		}
		
		if (str.endsWith(",")){
			str = str.substring(0,str.length()-1);
		}
		return str;
	}

}
