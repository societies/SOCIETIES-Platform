package org.societies.platform.socialdata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.societies.api.internal.schema.sns.socialdata.ConnectorBean;
import org.societies.api.internal.sns.ISocialConnector;
import org.societies.api.internal.sns.ISocialData;
import org.societies.api.schema.sns.socialdata.model.SocialNetwork;

import com.restfb.json.JsonObject;

public class SocialNetworkUtils {

    /**
     * Translate a String into a SocialNetwotkName
     * 
     * @param name
     *            String
     * @return {@link SocialNetwork}
     */
    public static SocialNetwork getSocialNetowkName(String name) {
	if (SocialNetwork.FACEBOOK.value().equalsIgnoreCase(name))
	    return SocialNetwork.FACEBOOK;
	if (SocialNetwork.TWITTER.value().equalsIgnoreCase(name))
	    return SocialNetwork.TWITTER;
	if (SocialNetwork.FOURSQUARE.value().equalsIgnoreCase(name))
	    return SocialNetwork.FOURSQUARE;
	if (SocialNetwork.LINKEDIN.value().equalsIgnoreCase(name))
	    return SocialNetwork.LINKEDIN;
	if (SocialNetwork.GOOGLEPLUS.value().equalsIgnoreCase(name))
	    return SocialNetwork.GOOGLEPLUS;
	return null;
    }

    public static String genJsonPostMessage(Map<String, ?> map) {
	String type = map.get(ISocialData.POST_TYPE).toString();
	JsonObject result = new JsonObject(type);
	if (type.equals(ISocialData.CHECKIN)) {

	    // Example:
	    // String value="{ \"checkin\": {"+
	    // "\"lat\": \"45.473272\","+
	    // "\"lon\": \"9.187519\","+
	    // "\"message\": \"Milano City\","+
	    // "\"place\": 1234}"+
	    // "}";

	    if (map.containsKey(ISocialData.POST_DESCR))
		result.put(ISocialData.POST_DESCR,
			map.get(ISocialData.POST_DESCR).toString());
	    result.put(ISocialData.POST_LAT, map.get(ISocialData.POST_LAT)
		    .toString());
	    result.put(ISocialData.POST_LON, map.get(ISocialData.POST_LON)
		    .toString());
	    if (map.containsKey(ISocialData.POST_MESSAGE))
		result.put(ISocialData.POST_MESSAGE,
			map.get(ISocialData.POST_MESSAGE).toString());
	    if (map.containsKey(ISocialData.POST_PLACE))
		result.put(ISocialData.POST_PLACE,
			map.get(ISocialData.POST_PLACE).toString());
	} else {

	    // Example:
	    // String value="{ \"event\": {"+
	    // "\"name\": \"Party\","+
	    // "\"from\": \"2013-08-12 10:45\","+
	    // "\"to\": \"2013-08-12 18:45\","+
	    // "\"location\": \"HOME\","+
	    // "\"description\": \"My Birthday Party\"}"+
	    // "}";

	    if (map.containsKey(ISocialData.POST_NAME))
		result.put(ISocialData.POST_NAME, map
			.get(ISocialData.POST_NAME).toString());
	    result.put(ISocialData.POST_FROM, map.get(ISocialData.POST_FROM)
		    .toString());
	    result.put(ISocialData.POST_TO, map.get(ISocialData.POST_TO)
		    .toString());
	    if (map.containsKey(ISocialData.POST_DESCR))
		result.put(ISocialData.POST_DESCR,
			map.get(ISocialData.POST_DESCR).toString());
	    if (map.containsKey(ISocialData.POST_LOCATION))
		result.put(ISocialData.POST_LOCATION,
			map.get(ISocialData.POST_LOCATION).toString());
	    if (map.containsKey(ISocialData.POST_PLACE))
		result.put(ISocialData.POST_PLACE,
			map.get(ISocialData.POST_PLACE).toString());
	}

	return result.toString(1);
    }
    
   
    
    public static ISocialConnector convertBeanToSocialConnector(ConnectorBean connectorBean) {
	return SocialConnectorDTO.createFromBean(connectorBean);
    }
    
   
    public static ConnectorBean convertSocialConnectorToBean(ISocialConnector socialConnector) {
	
	ConnectorBean connectorBean = new ConnectorBean();
	connectorBean.setId(socialConnector.getID());
	connectorBean.setName(socialConnector.getConnectorName());
	connectorBean.setExpires(socialConnector.getTokenExpiration());
	
	return connectorBean;
    }
    
    
    public static List<ISocialConnector> getConnectorsByName(SocialNetwork name, Collection<ISocialConnector> connectors){
	
	    Iterator <ISocialConnector> it = connectors.iterator();
	    ArrayList<ISocialConnector> results = new ArrayList<ISocialConnector>();
		
	    for (ISocialConnector conn : connectors){
	   	if (conn.getSocialNetwork().equals(name)){
			results.add(conn);
		}
	     }
	     return results;
    }
}
