package org.societies.personalisation.socialprofiler.service;

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
		//this.engine.setDatabaseConnection(databaseConnection);
		updateNetwork();
		//databaseConnection.closeMysql();
		logger.info("Network update routine terminated.");
	}
	
	
	private void updateNetwork() {
			logger.info("Update Network ....");
	   		engine.UpdateNetwork(ProfilerEngine.UPDATE_EVERYTHING);
	   		engine.generateUniformProfilePercentagesUsingBayesianSystem(); // this creates a bayesian system overall
//	   		engine.updateCentralityParameters();
	}
	
	
	

}
