package org.societies.platform.socialdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.societies.api.internal.sns.ISocialConnector;
import org.societies.api.internal.sns.ISocialData;

public class SocialData implements ISocialData {

    HashMap<String, ISocialConnector> connectors = new HashMap<String, ISocialConnector>();
    
    ArrayList<Object> 				socialPersonList;
    ArrayList<Object>	 			socialActivityList;
    ArrayList<Object>				socialGroupList;
    ArrayList<Object>				socialProfiles;
    
    long lastUpate;
    
    
    public SocialData(){
    		
    	socialPersonList 		= new ArrayList<Object>();
    	socialActivityList		= new ArrayList<Object>();
    	socialGroupList			= new ArrayList<Object>();
    	socialProfiles			= new ArrayList<Object>();
    	
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
		return socialPersonList;
	}

	@Override
	public List<Object> getSocialActivity() {
		return socialActivityList;
	}

	@Override
	public List<Object> getSocialGroups() {
		return socialGroupList;
	}
	
	@Override
	public List<Object> getSocialProfiles() {
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
