package org.societies.personalisation.socialprofiler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Timer;

import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.sns.ISocialConnector;
import org.societies.api.internal.sns.ISocialData;
import org.societies.personalisation.socialprofiler.service.DatabaseConnection;
import org.societies.personalisation.socialprofiler.service.GraphManager;
import org.societies.personalisation.socialprofiler.service.ProfilerEngine;
import org.societies.personalisation.socialprofiler.service.SocialTimerTask;


public class SocialProfiler  { //implements ISocialProfiler

	private static final Logger logger = LoggerFactory.getLogger(SocialProfiler.class);
	private Properties 				props 			= new Properties();
	private String 					configFileName	= "config.properties";
	private GraphManager	 		graph;
	private DatabaseConnection  	databaseConnection;
	private ISocialData 			socialdata;
	
	
	public ISocialData getSocialdata() {
		return socialdata;
	}


	public void setSocialdata(ISocialData socialdata) {
		this.socialdata = socialdata;
	}

	private ProfilerEngine 			engine;
	
	private Timer 					timer;
	
	private float daysFull = 0;
	
	


	public void initService(){
		
		logger.info("Social Profiler Initialized");
		
		this.enableProperties();
		
		
		
		String neoDBPath = props.getProperty("neo4j.dbPath");
		
		if (neoDBPath==null || neoDBPath.isEmpty()){
			logger.error("NeoDB path has not been set in config.properties (neo4j.dbPath)");
   		 	return;
		}
		
		// Start and Create (if necessary) the Graph
		EmbeddedGraphDatabase neo	= new EmbeddedGraphDatabase(neoDBPath);
		this.graph					= new GraphManager(neo);
		this.databaseConnection	    = new DatabaseConnection(props);
		
		logger.info("Engine Initialization ...");
//		this.engine	= new ProfilerEngine(graph, databaseConnection, getSocialdata());
		this.engine	= new ProfilerEngine(graph, getSocialdata());
		
		// start Scheduler
		scheduleNetworkUpdate();
	}

	
	
	private void enableProperties(){
//		try{
			logger.debug("Read keys...");
			
//			InputStream inputStream = new FileInputStream(new File(configFileName));
//            if (inputStream!=null){
//				props.load(inputStream);
//	            inputStream.close();
//	        }
			
			ResourceBundle rb = ResourceBundle.getBundle("config");
			Enumeration <String> keys = rb.getKeys();
			while (keys.hasMoreElements()) {
				String key = keys.nextElement();
				String value = rb.getString(key);
				logger.info(key + ": " + value);
				props.put(key, value);
				
			}
           
            logger.info("engine activated.");

//		}catch(IOException e){
//			logger.error("Unable to read property file CAUSE: "+e);
//		}
	}
	
	
	 private void scheduleNetworkUpdate() {
		 	
		 
		 	daysFull  = new Integer(props.getProperty("updateFreq.full")).intValue();
			
	    	//int initialDelay = 1000 * 60 * 60 * 24 * daysFull;
			//int period = initialDelay;
			
	    	int initialDelay =0;
			int period		 = (int)(1000 * 60 * 60 * 24 * daysFull); //30000
	    	this.timer = new Timer();
			SocialTimerTask task = new SocialTimerTask(this.engine);
			timer.scheduleAtFixedRate(task, initialDelay, period);
			logger.info("Next network update scheduled in " + daysFull + " days. The procedure will be repeated every " + daysFull + " days.");
	 }


	 
	 


//	@Override
	public void addSocialNetwork(List<ISocialConnector> connectors) {
		
		
		try {
			Iterator<ISocialConnector>it = connectors.iterator();
			while (it.hasNext()){
				
				ISocialConnector connector = it.next();
				this.engine.getSocialData().addSocialConnector(connector);
				logger.info("Add new connector "+connector.getConnectorName() + "with id:"+connector.getID());
			
			}
			
			engine.UpdateNetwork(ProfilerEngine.UPDATE_EVERYTHING);
	 	    
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}



//	@Override
	public void removeSocialNetwork(List<ISocialConnector> connectors) {
	
		try {
			Iterator<ISocialConnector>it = connectors.iterator();
			while (it.hasNext()){
			
				ISocialConnector connector = it.next();
				this.engine.getSocialData().removeSocialConnector(connector);
				logger.info("Removed connector "+connector.getConnectorName() + "with id:"+connector.getID());
			
			}
			
			if (this.engine.getSocialData().getSocialConnectors().size()==0)
				shutdown();
			else 
				engine.UpdateNetwork(200);
			
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	public void shutdown(){
		logger.info("No connector Available -- Stop component task");
		timer.cancel();
		graph.shutdown();
		databaseConnection.closeMysql();
		logger.info("Engine stopped - DB closed");
	}



//	@Override
	public void setUpdateFrequency(float frequency) {
		logger.info("Frequency update changed into :"+frequency + "days");
		this.daysFull=frequency;
		
	}



//	@Override
	public float getUpdateFrequency() {
		return daysFull;
	}



//	@Override
	public List<ISocialConnector> getListOfLinkedSN() {
		return this.engine.getSocialData().getSocialConnectors();
	}	

	public GraphManager getGraph() {
		return this.graph;
	}
	
	public ArrayList<String> getPredominantProfileForUser(String personId,int option){
		ArrayList <Integer> user_number_actions=getUserNumberActionsForProfiles(personId,option);
		ArrayList<String> result=graph.getPredominantProfileForUser(personId,user_number_actions);
		return result;
	}
	
	private ArrayList <Integer> getUserNumberActionsForProfiles(String personId,int option){
		ArrayList <Integer> user_number_actions=new ArrayList <Integer> ();
		//current date
		java.util.Date today = new java.util.Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
	    int current_week = 0;
		try {
			current_week = databaseConnection.calculateWeek(sdf.format(today));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    logger.debug("current week is "+current_week);
	    //update week
	    switch (option){
	    	case ProfilerEngine.EVERYTHING : {
	    		current_week=0;
	    		break;
	    	}
	    	case ProfilerEngine.LAST_WEEK : {
	    		current_week--;
	    		break;
	    	}
	    	case ProfilerEngine.LAST_2_WEEKS : {
	    		current_week=current_week-2;
	    		break;
	    	}
	    	case ProfilerEngine.LAST_MONTH : {
	    		current_week=current_week-5;
	    		break;
	    	}
	    	case ProfilerEngine.LAST_2_MONTHS : {
	    		current_week=current_week-9;
	    		break;
	    	}
	    	case ProfilerEngine.LAST_3_MONTHS : {
	    		current_week=current_week-14;
	    		break;
	    	}
	    	case ProfilerEngine.LAST_6_MONTHS : {
	    		current_week=current_week-28;
	    		break;
	    	}
	    	case ProfilerEngine.LAST_YEAR : {
	    		current_week=current_week-56;
	    		break;
	    	}
	    }
	    if (current_week<0){
	    	current_week=0;	
	    }
	    logger.debug("current_week "+current_week);
	    if (current_week==0){  //if week=0 then no need to use mysql queries , can use directly the information from graph
	    	user_number_actions.add(0); //narcissism
	    	user_number_actions.add(0); //photo
	    	user_number_actions.add(0); //super 
	    	user_number_actions.add(0); //quiz
	    	user_number_actions.add(0); //surf
	    }else{
	    	user_number_actions.add(databaseConnection.getNumberOfActionInPast(current_week, ProfilerEngine.NARCISSISM_PROFILE, personId));
	    	user_number_actions.add(databaseConnection.getNumberOfActionInPast(current_week, ProfilerEngine.PHOTO_PROFILE, personId));
	    	user_number_actions.add(databaseConnection.getNumberOfActionInPast(current_week, ProfilerEngine.SUPERACTIVE_PROFILE, personId));
	    	user_number_actions.add(databaseConnection.getNumberOfActionInPast(current_week, ProfilerEngine.QUIZ_PROFILE, personId));
	    	user_number_actions.add(databaseConnection.getNumberOfActionInPast(current_week, ProfilerEngine.SURF_PROFILE, personId));
	    	
	    	logger.debug(""+databaseConnection.getNumberOfActionInPast(current_week, ProfilerEngine.NARCISSISM_PROFILE, personId));
	    	logger.debug(""+databaseConnection.getNumberOfActionInPast(current_week, ProfilerEngine.PHOTO_PROFILE, personId));
	    	logger.debug(""+databaseConnection.getNumberOfActionInPast(current_week, ProfilerEngine.SUPERACTIVE_PROFILE, personId));
	    	logger.debug(""+databaseConnection.getNumberOfActionInPast(current_week, ProfilerEngine.QUIZ_PROFILE, personId));
	    	logger.debug(""+databaseConnection.getNumberOfActionInPast(current_week, ProfilerEngine.SURF_PROFILE, personId));
	    }
	    return user_number_actions;
	}

}
