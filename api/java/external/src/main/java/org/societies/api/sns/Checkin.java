package org.societies.api.sns;

public class Checkin {

	// String value="{ \"checkin\": {"+
	// "\"lat\": \"53.345149444145\","+
	// "\"lon\": \"-6.2539714878708\","+
	// "\"message\": \"Trinity Capital Hotel Dublin\","+
	// "\"place\": 171512719546772}"+
	// "}";

	long latitude;
	long longitude;
	String message;
	long placeId;

	public long getLatitude() {
		return latitude;
	}

	public void setLatitude(long latitude) {
		this.latitude = latitude;
	}

	public long getLongitude() {
		return longitude;
	}

	public void setLongitude(long longitude) {
		this.longitude = longitude;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public long getPlaceId() {
		return placeId;
	}

	public void setPlaceId(long placeId) {
		this.placeId = placeId;
	}

}
