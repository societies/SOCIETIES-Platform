package org.societies.platform.socialdata.converters;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.shindig.protocol.model.ExtendableBean;
import org.apache.shindig.protocol.model.ExtendableBeanImpl;
import org.apache.shindig.social.core.model.ActivityEntryImpl;
import org.apache.shindig.social.core.model.ActivityObjectImpl;
import org.apache.shindig.social.opensocial.model.ActivityEntry;
import org.apache.shindig.social.opensocial.model.ActivityObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ActivityConverterFromFoursquare implements ActivityConverter {

	
	final String LOCATION = "location";
	final String POSITION = "position";
	
	@Override
	public List<ActivityEntry> load(String data) {
		ArrayList<ActivityEntry> activities = new ArrayList<ActivityEntry>();
		JSONObject response;
		JSONArray elements;

//		ActivityObject providerObj = new ActivityObjectImpl();
//		providerObj.setContent("foursquare");
//		providerObj.setUrl("www.foursquare.com");
//		providerObj.setId("foursquare");
//		providerObj.setDisplayName("Foursquare");

		try {
			response = new JSONObject(data);
			// System.out.println(response);
			JSONObject jdata = new JSONObject(data);
			if (response.has("response")) {
				JSONObject checkins = (JSONObject) response.get("response");
				// System.out.println(user);
				if (checkins.has("checkins")) {
					jdata = (JSONObject) checkins.get("checkins");

					if (jdata.get("items") != null) {
						elements = jdata.getJSONArray("items");

						for (int i = 0; i < elements.length(); i++) {
							JSONObject elm = elements.getJSONObject(i);
							ActivityEntry entry = new ActivityEntryImpl();
							entry.setId("foursqure:"+elm.getString("id"));

							Date createdTime = new Date(
									elm.getLong("createdAt")*1000);
							JSONObject venue = elm.getJSONObject("venue");
							JSONObject location = venue
									.getJSONObject("location");

							SimpleDateFormat publishedDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
							entry.setPublished(publishedDate.format(createdTime));
							
							String venueName = getVenueName(venue);
							String locationString = getLocation(location);
							String gps = getGPS(location);
							String category = getCategory(venue);

							if (elm.has("shout"))
								entry.setTitle(elm.getString("shout"));
//							entry.setContent(venueName + "(" + category + ")"
//									+ "; " + locationString + "; " + gps + "; "
//									+ createdTime.toString());
							
							ActivityObject checkin = new ActivityObjectImpl();
							checkin.setDisplayName(venueName);
							checkin.setObjectType("place");
							checkin.setContent(category);
							ExtendableBean loc = new ExtendableBeanImpl();
							loc.put(LOCATION, locationString);
							loc.put(POSITION, gps);
							
							entry.setExtensions(loc);
							entry.setObject(checkin);
							
							ActivityObject aobj = new ActivityObjectImpl();
							aobj.setDisplayName("I");
							entry.setActor(aobj);
							entry.setContent(checkin.getDisplayName()+","+loc.get("location"));
							
							entry.setVerb("checkin");
							activities.add(entry);
						}
					}

				}
			}

		}

		catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return activities;
	}

	/**
	 * @param venue
	 * @return
	 */
	private String getVenueName(JSONObject venue) {
		String venueName = null;
		try {
			venueName = venue.getString("name");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return venueName.toString();
	}

	private String getLocation(JSONObject location) {

		StringBuilder locationString = new StringBuilder();
		try {
			if (location.has("city"))
				locationString.append(location.getString("city"));
			if (location.has("state"))
				locationString.append(location.getString("state"));
			if (location.has("country"))
				locationString.append(location.getString("country"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return locationString.toString();
	}

	private String getGPS(JSONObject location) {
		StringBuilder gps = new StringBuilder();
		try {
			gps.append(location.getString("lat"));
			gps.append(",");
			gps.append(location.getString("lng"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return gps.toString();
	}

	private String getCategory(JSONObject venue) {
		String category = null;
		try {
			category = venue.getJSONArray("categories").getJSONObject(0)
					.getString("name");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return category.toString();
	}
}
