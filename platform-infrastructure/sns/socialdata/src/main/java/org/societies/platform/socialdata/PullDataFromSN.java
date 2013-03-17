package org.societies.platform.socialdata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shindig.social.opensocial.model.Group;
import org.apache.shindig.social.opensocial.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.sns.ISocialConnector;
import org.societies.api.sns.SocialDataState;
import org.societies.platform.socialdata.converters.ActivityConverter;
import org.societies.platform.socialdata.converters.ActivityConveterFactory;
import org.societies.platform.socialdata.converters.FriendsConverter;
import org.societies.platform.socialdata.converters.FriendsConveterFactory;
import org.societies.platform.socialdata.converters.GroupConverter;
import org.societies.platform.socialdata.converters.GroupConveterFactory;
import org.societies.platform.socialdata.converters.PersonConverter;
import org.societies.platform.socialdata.converters.PersonConverterFactory;
import org.springframework.scheduling.annotation.Async;

public class PullDataFromSN {

    private static final Logger logger = LoggerFactory.getLogger(PullDataFromSN.class);
    SocialData sd;
    
    public PullDataFromSN(SocialData socialData){
	this.sd = socialData;
    }
    
   
    @Async
    public void execute() {
	
	sd.setState(SocialDataState.DOWNLOADING_FROM_SN);
	
	HashMap<String, Object> allFriends 	= new HashMap<String, Object>();
	HashMap<String, Object> allGroups 	= new HashMap<String, Object>();
	HashMap<String, Object> allProfiles 	= new HashMap<String, Object>();
	HashMap<String, Object> allFeeds 	= new HashMap<String, Object>();
	
	logger.debug("Processing connectors....");
	for (ISocialConnector connector : sd.connectors.values()){
		
		logger.debug("Update " + connector.getConnectorName()  + " data...");
		
		
		// add a Feed for each connector
		allFeeds.put(connector.getID(),     getActivities(connector));
		
		
		// Add a new profile for each connector....
		Person profile = updateProfile(connector);
		allProfiles.put(profile.getId(), profile);
		
		
		allGroups.putAll (updateGroups(connector));
		allFriends.putAll(updateFriends(connector));
		
		
	}

	
	// clone data...
	sd.socialActivities = (Map<String, Object>) allFeeds.clone();
	sd.socialFriends    = (Map<String, Object>) allFriends.clone();
	sd.socialProfiles   = (Map<String, Object>) allProfiles.clone();
	sd.socialGroups	    = (Map<String, Object>) allGroups.clone();
	
	sd.updateContextBroker();
	logger.debug("Update Thread completed");
    
    }
    
    
    /**
     * Read and parse ActivityFeed from connector
     * 
     * @param {@link {@link ISocialConnector}
     * @return Map<ConnectorID, Feed>
     */
    private List<?> getActivities(ISocialConnector connector) {
	
	logger.debug("Read " + connector.getSocialNetwork() + " feed ... ");
	ActivityConverter parser = ActivityConveterFactory.getConverter(connector);
	return parser.load(connector.getUserActivities());
	
    }
    
    
    /**
     * Read Profile data
     * @param  {@link ISocialConnector}
     * @return {@link Person}
     */
    private Person updateProfile(ISocialConnector connector) {

	logger.debug("Update " + connector.getSocialNetwork() + " Profile ... ");

	PersonConverter parser = PersonConverterFactory.getConverter(connector);
	return  parser.load(connector.getUserProfile());
    }
    
    /**
     * Read Groups data
     * @param  {@link ISocialConnector}
     * @return List{@link Group}
     */
   
    private Map<String, ?> updateGroups(ISocialConnector connector) {
	logger.debug("Read  " + connector.getSocialNetwork() + " groups ... ");
	GroupConverter parser = GroupConveterFactory.getConverter(connector);
	List<Group> groups = parser.load(connector.getUserGroups());
	Map<String, Object> gMap= new HashMap<String, Object>();
	for(Group g: groups){
	    gMap.put(g.getId().getGroupId(), g);
	}
	
	return gMap;

    }
    
    /**
     * Read Friends data
     * @param {@link ISocialConnector}
     * @return List < {@link Person }
     */
    private Map<String, ?> updateFriends(ISocialConnector connector) {
	logger.debug("Read " + connector.getSocialNetwork() + " friends ... ");
	FriendsConverter parser = FriendsConveterFactory.getConverter(connector);
	List<Person> friends = parser.load(connector.getUserFriends());
	Map<String, Object> fMap = new HashMap<String, Object>();
	for(Person f: friends) fMap.put(f.getId(),f);
	return fMap;

    }

    
    

}
