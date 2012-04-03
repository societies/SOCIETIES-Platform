package org.societies.personalization.socialprofiler.service;

import java.util.TimerTask;

import org.apache.log4j.Logger;

public class SocialTimerTask extends TimerTask {

	private static final Logger 		logger = Logger.getLogger(SocialTimerTask.class);
	private DatabaseConnection		databaseConnection;
	private ProfilerEngine				engine;
	

	public SocialTimerTask(ProfilerEngine engine, DatabaseConnection databaseConnection) {
		
		super();
		this.engine = engine;
		this.databaseConnection = databaseConnection;
	}


	@Override
	public void run() {
			logger.info("Starting full network update...");
		   if (!databaseConnection.connectMysql()){
			   logger.error("Cannot update network due to database connection problems.");
			   return;
		}
		
		this.engine.setDatabaseConnection(databaseConnection);
		this.updateNetwork();
		databaseConnection.closeMysql();
		logger.info("Network update routine terminated.");
	}
	
	
	private void updateNetwork() {
			logger.info("Update Network ....");
	   		engine.UpdateNetwork(ProfilerEngine.UPDATE_EVERYTHING);
//	   		engine.generateUniformProfilePercentagesUsingBayesianSistem(); // this creates a bayesian system overall
//	   		engine.updateCentralityParameters();
	}
	
	
	

}
