package org.societies.personalisation.socialprofiler.service;

import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SocialTimerTask extends TimerTask {

	private static final Logger logger = LoggerFactory.getLogger(SocialTimerTask.class);
	private ProfilerEngine				engine;
	

	public SocialTimerTask(ProfilerEngine engine) {
		
		super();
		this.engine = engine;
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
