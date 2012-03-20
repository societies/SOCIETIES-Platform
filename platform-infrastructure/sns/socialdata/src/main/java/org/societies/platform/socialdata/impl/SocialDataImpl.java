package org.societies.platform.socialdata.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.shindig.social.opensocial.model.ActivityEntry;
import org.apache.shindig.social.opensocial.model.Group;
import org.apache.shindig.social.opensocial.model.Person;
import org.societies.api.internal.sns.ISocialConnector;
import org.societies.platform.socialdata.SocialData;

public class SocialDataImpl implements SocialData {

    HashMap<String, ISocialConnector> connectors = new HashMap<String, ISocialConnector>();
    
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
	public void addSocialConnector(ISocialConnector socialConnector) throws Exception {
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
	public List<ISocialConnector> getSocialConnectors() {
		Iterator<ISocialConnector>it = connectors.values().iterator();
		List <ISocialConnector> list = new ArrayList<ISocialConnector>();
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

		Iterator<ISocialConnector>it = connectors.values().iterator();
		List <ISocialConnector> list = new ArrayList<ISocialConnector>();
		while (it.hasNext()){
			ISocialConnector connector = it.next();
		    
			updateProfile(connector.getUserProfile());
			updateFriends(connector.getUserFriends());
			updateGroups(connector.getUserGroups());
			getActivities(connector.getUserActivities());
			
			/// UPDATE ALL DATA
			
			
		}
		
		lastUpate = System.currentTimeMillis();
		
		
	}
	
	

	private void updateGroups(String userGroups) {
		// TODO Auto-generated method stub
		
	}


	private void getActivities(String userActivities) {
		// TODO Auto-generated method stub
		
	}


	private void updateFriends(String userFriends) {
		// TODO Auto-generated method stub
		
	}


	private void updateProfile(String userProfile) {
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
	public boolean isAvailable(ISocialConnector connector) {
		
		if (connector==null) return false;
		
		return (connectors.containsKey(connector.getID()));
	}
 

}
