package org.societies.personalization.socialprofiler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Timer;

import org.apache.log4j.Logger;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.societies.api.internal.personalisation.ISocialProfiler;
import org.societies.api.internal.sns.ISocialConnector;
import org.societies.api.internal.sns.ISocialData;
import org.societies.personalization.socialprofiler.service.DatabaseConnection;
import org.societies.personalization.socialprofiler.service.GraphManager;
import org.societies.personalization.socialprofiler.service.ProfilerEngine;
import org.societies.personalization.socialprofiler.service.SocialTimerTask;


public class SocialProfiler implements ISocialProfiler {

	private static final Logger 	logger 			= Logger.getLogger(ISocialProfiler.class);
	private Properties 				props 			= new Properties();
	private String 					configFileName	= "config.properties";
	private GraphManager	 		graph;
	private DatabaseConnection  	databaseConnection;

	private ProfilerEngine 			engine;
	
	private Timer 					timer;
	
	
	public SocialProfiler(){
		
	}
	
	private float daysFull = 0;

	@Override
	public void setSocialdata(ISocialData socialData) {
		initializationSocialProfiler(socialData);
	}
	
	
	public ISocialData getSocialdata() {
		return engine.getSocialData();
	}
	
	


	private void initializationSocialProfiler(ISocialData socialdata){
		
		logger.info("Social Profiler Intialized");
		
		this.enableProperties();
		String neoDBPath = props.getProperty("neo4j.dbPath");
		
		if (neoDBPath==null || neoDBPath.isEmpty()){
			logger.fatal("NeoDB path has not been set in config.properties (neo4j.dbPath)");
   		 	return;
		}
		
		// Start and Create (if necessary) the Graph
		EmbeddedGraphDatabase neo	= new EmbeddedGraphDatabase(neoDBPath);
		this.graph					= new GraphManager(neo);
		this.databaseConnection	    = new DatabaseConnection(props);
		
		logger.info("Engine Initialization ...");
		this.engine	= new ProfilerEngine(graph, databaseConnection, socialdata);
		
		// start Scheduler
		scheduleNetworkUpdate();
	}

	
	
	private void enableProperties(){
		try{
			logger.debug("Read keys...");
			
			InputStream inputStream = new FileInputStream(new File(configFileName));
            if (inputStream!=null){
				props.load(inputStream);
	            inputStream.close();
	        }
           
            logger.info("engine activated.");

		}catch(IOException e){
			logger.error("Unable to read property file CAUSE: "+e);
		}
	}
	
	
	 private void scheduleNetworkUpdate() {
		 	
		 
		 	daysFull  = new Integer(props.getProperty("updateFreq.full")).intValue();
			
	    	//int initialDelay = 1000 * 60 * 60 * 24 * daysFull;
			//int period = initialDelay;
			
	    	int initialDelay =0;
			int period		 = 30000; //(int)(1000 * 60 * 60 * 24 * daysFull);
	    	this.timer = new Timer();
			SocialTimerTask task = new SocialTimerTask(this.engine,  this.databaseConnection);
			timer.scheduleAtFixedRate(task, initialDelay, period);
			logger.info("Next network update scheduled in " + daysFull + " days. The procedure will be repeated every " + daysFull + " days.");
	 }


	 
	 


	@Override
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



	@Override
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



	@Override
	public void setUpdateFrequency(float frequency) {
		logger.info("Frequency update changed into :"+frequency + "days");
		this.daysFull=frequency;
		
	}



	@Override
	public float getUpdateFrequency() {
		return daysFull;
	}



	@Override
	public List<ISocialConnector> getListOfLinkedSN() {
		return this.engine.getSocialData().getSocialConnectors();
	}


	

	

}
