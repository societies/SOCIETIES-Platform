package org.societies.personalization.socialprofiler.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.Timer;

import org.apache.log4j.Logger;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.societies.api.context.broker.ICtxBroker;
import org.societies.api.internal.sns.ISocialConnector;
import org.societies.personalization.socialprofiler.SocialProfiler;
import org.societies.personalization.socialprofiler.service.DatabaseConnectionImpl;
import org.societies.personalization.socialprofiler.service.EngineImpl;
import org.societies.personalization.socialprofiler.service.ServiceImpl;
import org.societies.personalization.socialprofiler.service.SocialTimerTask;
import org.societies.platform.FacebookConn.impl.FacebookConnectorImpl;

public class SocialProfilerImpl implements SocialProfiler {

	private static final Logger 	logger 			= Logger.getLogger(SocialProfiler.class);
	private Properties 				props 			= new Properties();
	private String 					configFileName	= "config.properties";
	private EmbeddedGraphDatabase 	neo;
	private ServiceImpl	 			service;
	private DatabaseConnectionImpl  databaseConnection;

	private EngineImpl 				engine;

	
	
	
	private ICtxBroker ctxBroker;
	private float daysFull = 0;

	public SocialProfilerImpl(){
		
		initializationSocialProfiler();
		
		// network update timeout setup
 	    this.scheduleNetworkUpdate();
 	    
 	    // JUST FOR NOW
 	    if (neo!=null)   neo.shutdown();
		
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
		this.engine	=new EngineImpl(service, databaseConnection);
		
		///////////////////////////////////////////////////
		//
		// Add SOCIAL CONNECTOR -- FACEBOOK
		//
		///////////////////////////////////////////////////
		String access_token = "AAAFPIhZAkC90BAO1fmJZAxs754pEGhaGQesb9haktJ8JgDOnxsSUo2A9PJOnQa34b2mRUuRXRUS1mhUIJG8RMetAUwZBZBkOBFfo7G0h6wZDZD";
		String identity		= "";
		ISocialConnector connector =  new FacebookConnectorImpl(access_token, identity);
		
		// ENGINE
		this.engine.linkSocialNetwork(connector);
		
	}

	
	
	public ICtxBroker getCtxBroker() {
		return ctxBroker;
	}


	public void setCtxBroker(ICtxBroker ctxBroker) {
		this.ctxBroker = ctxBroker;
	}
	
	private void enableProperties(){
		try{
			logger.debug("Read keys...");
			
			InputStream inputStream = new FileInputStream(new File(configFileName));
            if (inputStream!=null){
				props.load(inputStream);
	            inputStream.close();
	        }
           
            

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
		this.engine.linkSocialNetwork(connector);
		logger.info("Add new connector "+connector.getConnectorName() + "with id:"+connector.getID());
	}



	@Override
	public void removeSocialNetwork(ISocialConnector connector) {
	
		this.engine.unlinkSocialNetwork(connector);
		logger.info("Removed connector "+connector.getConnectorName() + "with id:"+connector.getID());
		
	}



	@Override
	public void setUpdateFrequency(float frequency) {
		this.daysFull=frequency;
		
	}



	@Override
	public float getUpdateFrequency() {
		return daysFull;
	}



	@Override
	public List<ISocialConnector> getListOfLinkedSN() {
		return this.engine.getSNConnectors();
	}

}
