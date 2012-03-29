package org.societies.personalization.socialprofiler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.Timer;

import org.apache.log4j.Logger;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.societies.api.internal.personalisation.ISocialProfiler;
import org.societies.api.internal.sns.ISocialConnector;
import org.societies.api.internal.sns.ISocialData;
import org.societies.personalization.socialprofiler.service.DatabaseConnectionImpl;
import org.societies.personalization.socialprofiler.service.EngineImpl;
import org.societies.personalization.socialprofiler.service.ServiceImpl;
import org.societies.personalization.socialprofiler.service.SocialTimerTask;


public class SocialProfiler implements ISocialProfiler {

	private static final Logger 	logger 			= Logger.getLogger(ISocialProfiler.class);
	private Properties 				props 			= new Properties();
	private String 					configFileName	= "config.properties";
	private EmbeddedGraphDatabase 	neo;
	private ServiceImpl	 			service;
	private DatabaseConnectionImpl  databaseConnection;

	private EngineImpl 				engine;
	private ISocialData				socialdata;
	
	private boolean 				watingForFirstConnector=true;
	
	
	private float daysFull = 0;

	
	public ISocialData getSocialdata() {
		return socialdata;
	}


	public void setSocialdata(ISocialData socialdata) {
		this.socialdata = socialdata;
		initializationSocialProfiler();
	}




	public void initializationSocialProfiler(){
		
		logger.info("Social Profiler Intialized");
		
		this.enableProperties();
		String neoDBPath = props.getProperty("neo4j.dbPath");
		
		if (neoDBPath==null || neoDBPath.isEmpty()){
			logger.fatal("NeoDB path has not been set in config.properties (neo4j.dbPath)");
   		 	return;
		}
		
		// Start and Create (if necessary) the Graph
		this.neo 					= new EmbeddedGraphDatabase(neoDBPath);
		this.service				= new ServiceImpl(neo);
		this.databaseConnection	    = new DatabaseConnectionImpl();
		
		
		logger.info("Engine Initialization ...");
		this.engine	= new EngineImpl(service, databaseConnection);
		
		
		
		
		
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
			int period		 = (int)(1000 * 60 * 60 * 24 * daysFull);
	    	Timer timer = new Timer();
			SocialTimerTask task = new SocialTimerTask(this.engine,  this.databaseConnection);
			timer.scheduleAtFixedRate(task, initialDelay, period);
			logger.info("Next network update scheduled in " + daysFull + " days. The procedure will be repeated every " + daysFull + " days.");
		}


	 
	 


	@Override
	public void addSocialNetwork(ISocialConnector connector) {
		
		
		try {
			this.socialdata.addSocialConnector(connector);
			logger.info("Add new connector "+connector.getConnectorName() + "with id:"+connector.getID());
			
			if (watingForFirstConnector){
				
					watingForFirstConnector	=false;
					initializationSocialProfiler();
			
					// network update timeout setup
					this.scheduleNetworkUpdate();
			}
	 	    
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}



	@Override
	public void removeSocialNetwork(ISocialConnector connector) {
	
		try {
			
			this.socialdata.removeSocialConnector(connector);
			logger.info("Removed connector "+connector.getConnectorName() + "with id:"+connector.getID());
			
			if (this.socialdata.getSocialConnectors().size()==0){
				logger.info("No connector Available -- Stop component task");
				watingForFirstConnector = true;
				neo.shutdown();
				databaseConnection.closeMysql();
				logger.info("Engine stopped - DB closed");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
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
		return this.socialdata.getSocialConnectors();
	}

}
