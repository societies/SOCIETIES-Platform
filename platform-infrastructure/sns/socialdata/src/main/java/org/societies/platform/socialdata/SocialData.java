package org.societies.platform.socialdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.shindig.social.opensocial.model.Group;
import org.apache.shindig.social.opensocial.model.Person;
import org.societies.api.internal.sns.ISocialConnector;
import org.societies.api.internal.sns.ISocialData;
import org.societies.platform.socialdata.converters.ActivityConverter;
import org.societies.platform.socialdata.converters.ActivityConveterFactory;
import org.societies.platform.socialdata.converters.FriendsConverter;
import org.societies.platform.socialdata.converters.FriendsConveterFactory;
import org.societies.platform.socialdata.converters.GroupConverter;
import org.societies.platform.socialdata.converters.GroupConveterFactory;
import org.societies.platform.socialdata.converters.PersonConverter;
import org.societies.platform.socialdata.converters.PersonConverterFactory;

public class SocialData implements ISocialData {

    HashMap<String, ISocialConnector> connectors = new HashMap<String, ISocialConnector>();
    
    Map<String, Object> 			socialFriends;
    Map<String, Object>				socialGroups;
    Map<String, Object>				socialProfiles;
    
    Map<String, Object>	 			socialActivities;
    
    long lastUpate;
    
    
    public SocialData(){
    		
    	socialFriends 			= new HashMap<String, Object>();
    	socialGroups			= new HashMap<String, Object>();
    	socialProfiles			= new HashMap<String, Object>();
    	
    	socialActivities		= new HashMap<String, Object>();
    	
    }
    

	@Override
	public void addSocialConnector(ISocialConnector socialConnector) throws Exception {
		if (connectors.containsKey(socialConnector.getID())){
			throw new Exception("this connetor already exists");
		}
		connectors.put(socialConnector.getID(), socialConnector);
		log("Add connector "+socialConnector.getID());
	}
	
	

	@Override
	public void removeSocialConnector(String connectorId) throws Exception{
		
		if (connectors.containsKey(connectorId)){
			connectors.remove(connectorId);
		}
		else throw new Exception("This connector not found");
		
	}
	
	

	@Override
	public List<ISocialConnector> getSocialConnectors() {
		Iterator<ISocialConnector>it = connectors.values().iterator();
		List <ISocialConnector> list = new ArrayList<ISocialConnector>();
		while (it.hasNext()){
			list.add(it.next());
		}
		return list;
	}

	@Override
	public List<Object> getSocialPeople() {
		return new ArrayList(socialFriends.values());
	}

	@Override
	public List<Object> getSocialActivity() {
		return new ArrayList(socialActivities.values());
	}

	@Override
	public List<Object> getSocialGroups() {
		return new ArrayList(socialGroups.values());
	}
	
	@Override
	public List<Object> getSocialProfiles() {
		return new ArrayList(socialProfiles.values());
	}
	

	@Override
	public void updateSocialData() {

		Iterator<ISocialConnector>it = connectors.values().iterator();
		socialActivities = new HashMap<String, Object>();  // reset old Activities
		
		while (it.hasNext()){
			ISocialConnector connector = it.next();
		    
			updateProfile(connector);
			updateFriends(connector);
			updateGroups(connector);
			getActivities(connector);
			
			/// UPDATE ALL DATA
			
			
		}
		
		lastUpate = System.currentTimeMillis();
		
		
	}
	
	

	private void updateGroups(ISocialConnector connector) {
		GroupConverter parser = GroupConveterFactory.getPersonConverter(connector);
		List<Group> groups = parser.load(connector.getUserGroups());
		Iterator<Group> it = groups.iterator();
		while (it.hasNext()){
			Group g = it.next();
			if (socialGroups.containsKey(g.getId())){
				socialGroups.remove(g.getId().getGroupId());
				// Send notification of UPDATE?
			}	
			else {
				// Send Notitication of NEW PROFILE?
			}
			socialGroups.put(g.getId().getGroupId(), g);
		}
		
	}


	private void getActivities(ISocialConnector connector) {
		
		ActivityConverter parser = ActivityConveterFactory.getActivityConverter(connector);
		
		socialActivities.put(connector.getID(), parser.load(connector.getUserActivities()));
	}


	private void updateFriends(ISocialConnector connector) {
		FriendsConverter parser = FriendsConveterFactory.getPersonConverter(connector);
		List<Person> friends = parser.load(connector.getUserProfile());
		Iterator<Person> it = friends.iterator();
		
		while (it.hasNext()){
			Person friend = it.next();
			if (socialFriends.containsKey(friend.getId())){
				socialFriends.remove(friend.getId());
				// Send notification of UPDATE?
			}	
			else {
				// Send Notitication of NEW PROFILE?
			}
			socialFriends.put(friend.getId(), friend);
		}
	}


	private void updateProfile(ISocialConnector connector) {
			PersonConverter parser = PersonConverterFactory.getPersonConverter(connector);
			Person profile = parser.load(connector.getUserProfile());
			if (socialProfiles.containsKey(profile.getId())){
				socialProfiles.remove(profile.getId());
				// Send notification of UPDATE?
			}	
			else {
				// Send Notitication of NEW PROFILE?
			}
			socialProfiles.put(profile.getId(), profile);
	}


	@Override
	public long getLastUpdate() {
		return lastUpate;
	}
    
    
    private void log(String out){
    	System.out.println("SocialData - " +out);
    }

	@Override
	public boolean isAvailable(ISocialConnector connector) {
		
		if (connector==null) return false;
		
		return (connectors.containsKey(connector.getID()));
	}


	@Override
	public void removeSocialConnector(ISocialConnector connector)
			throws Exception {
		

		if (connector==null) throw new Exception("Connector is null");
		
		if (connectors.containsKey(connector.getID())) 
			connectors.remove(connector.getID());
		else throw new Exception("Connector not found");
		
	}
 

}
