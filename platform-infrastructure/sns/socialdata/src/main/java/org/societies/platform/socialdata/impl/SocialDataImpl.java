package org.societies.platform.socialdata.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.shindig.social.opensocial.model.ActivityEntry;
import org.apache.shindig.social.opensocial.model.Group;
import org.apache.shindig.social.opensocial.model.Person;
import org.json.JSONObject;
import org.societies.platform.FacebookConn.SocialConnector;
import org.societies.platform.socialdata.SocialData;

public class SocialDataImpl implements SocialData {

    HashMap<String, SocialConnector> connectors = new HashMap<String, SocialConnector>();
    
    ArrayList<Person> 				socialPersonList;
    ArrayList<ActivityEntry> 		socialActivityList;
    ArrayList<Group>				socialGroupList;
    ArrayList<Person>				socialProfiles;
    
    
    
    long lastUpate;
    
    
    public SocialDataImpl(){
    		
    	socialPersonList 		= new ArrayList<Person>();
    	socialActivityList		= new ArrayList<ActivityEntry>();
    	socialGroupList			= new ArrayList<Group>();
    	socialProfiles			= new ArrayList<Person>();
    	
    }
    

	@Override
	public void addSocialConnector(SocialConnector socialConnector) throws Exception {
		if (connectors.containsKey(socialConnector.getID())){
			throw new Exception("this connetor lready exists");
		}
		connectors.put(socialConnector.getID(), socialConnector);
		log("Add connector "+socialConnector.getID());
	}

	@Override
	public void removeSocialConnector(String connectorId) throws Exception{
		if (connectors.containsKey(connectorId)){
			connectors.remove(connectorId);
		}
		else throw new Exception("This connector is not available");
		
	}

	@Override
	public List<SocialConnector> getSocialConnectors() {
		Iterator<SocialConnector>it = connectors.values().iterator();
		List <SocialConnector> list = new ArrayList<SocialConnector>();
		while (it.hasNext()){
			list.add(it.next());
		}
		return list;
	}

	@Override
	public List<Person> getSocialPeople() {
		return socialPersonList;
	}

	@Override
	public List<ActivityEntry> getSocialActivity() {
		return socialActivityList;
	}

	@Override
	public List<Group> getSocialGroups() {
		return socialGroupList;
	}
	
	@Override
	public List<Person> getSocialProfiles() {
		return socialProfiles;
	}
	

	@Override
	public void updateSocialData() {

		Iterator<SocialConnector>it = connectors.values().iterator();
		List <SocialConnector> list = new ArrayList<SocialConnector>();
		while (it.hasNext()){
			SocialConnector connector = it.next();
		    
			updateProfile(connector.getUserProfile());
			updateFriends(connector.getUserFriends());
			updateGroups(connector.getUserGroups());
			getActivities(connector.getUserActivities());
			
			/// UPDATE ALL DATA
			
			
		}
		
		lastUpate = System.currentTimeMillis();
		
		
	}
	
	

	private void updateGroups(JSONObject userGroups) {
		// TODO Auto-generated method stub
		
	}


	private void getActivities(JSONObject userActivities) {
		// TODO Auto-generated method stub
		
	}


	private void updateFriends(JSONObject userFriends) {
		// TODO Auto-generated method stub
		
	}


	private void updateProfile(JSONObject userProfile) {
		 // UPDATE Social profile (per SN)
		
	}


	@Override
	public long getLastUpdate() {
		return lastUpate;
	}
    
    
    private void log(String out){
    	System.out.println("SocialData - " +out);
    }

	@Override
	public boolean isAvailable(SocialConnector connector) {
		
		if (connector==null) return false;
		
		return (connectors.containsKey(connector.getID()));
	}
 

}
