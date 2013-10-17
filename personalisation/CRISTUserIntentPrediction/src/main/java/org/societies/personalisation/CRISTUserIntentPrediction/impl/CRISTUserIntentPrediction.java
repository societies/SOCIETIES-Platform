/**
 * Copyright (c) 2011, SOCIETIES Consortium
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.societies.personalisation.CRISTUserIntentPrediction.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.logging.IPerformanceMessage;
import org.societies.api.internal.logging.PerformanceMessage;
import org.societies.api.internal.personalisation.model.FeedbackEvent;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.personalisation.CRIST.api.CRISTUserIntentPrediction.ICRISTUserIntentPrediction;
import org.societies.personalisation.CRIST.api.CRISTUserIntentTaskManager.ICRISTUserIntentTaskManager;
import org.societies.personalisation.CRIST.api.model.CRISTUserAction;
import org.societies.personalisation.common.api.management.IInternalPersonalisationManager;
import org.springframework.scheduling.annotation.AsyncResult;
// import javax.annotation.PostConstruct;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Component;
// import org.societies.api.personalisation.model.IAction;

// @Component
public class CRISTUserIntentPrediction implements ICRISTUserIntentPrediction {

    private static Logger PERF_LOG = LoggerFactory.getLogger("PerformanceMessage"); 
    // to define a dedicated Logger
    IPerformanceMessage m;
	
	
	private static Logger LOG = LoggerFactory.getLogger(CRISTUserIntentPrediction.class);

	private ICtxBroker ctxBroker;
	private ICRISTUserIntentTaskManager cristTaskManager;
	private IInternalPersonalisationManager persoMgr;
	private Boolean enablePrediction = true; 
	/*
	private IIdentity myId;
	private CtxAttributeIdentifier myCtxId;
	private ServiceResourceIdentifier serviceId;
	private ICRISTUserAction cristOutcome = null; 	
	*/
	
	public CRISTUserIntentPrediction() {
		//LOG.info("Hello! I'm the CRIST User Intent Prediction!");
	}


	public ICtxBroker getCtxBroker() {
		return ctxBroker;
	}

	public void setCtxBroker(ICtxBroker ctxBroker) {
		this.ctxBroker = ctxBroker;
	}
	
	public ICRISTUserIntentTaskManager getCristTaskManager(){
		return cristTaskManager;
	}
	
	public void setCristTaskManager(ICRISTUserIntentTaskManager cristTaskManager){
		this.cristTaskManager = cristTaskManager;
	}
	
	/**
	 * @return the persoMgr
	 */
	public IInternalPersonalisationManager getPersoMgr() {
		return persoMgr;
	}


	/**
	 * @param persoMgr the persoMgr to set
	 */
	public void setPersoMgr(IInternalPersonalisationManager persoMgr) {
		this.persoMgr = persoMgr;
	}


	public void initialiseCRISTPrediction() {

		if (this.ctxBroker == null) {
			LOG.error(this.getClass().getName() + "CtxBroker is null");
		} 
		if (this.cristTaskManager == null){
			LOG.error("The CRIST Taks Manager is NULL. ");
		} 
		

		LOG.info("Yo!! I'm a brand new service and my interface is: "
				+ this.getClass().getName());
		

	}
	
	/* (non-Javadoc)
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentPrediction.ICRISTUserIntentPrediction#enableCRISTPrediction(boolean)
	 */
	@Override
	public void enableCRISTPrediction(boolean bool) {
		// TODO Auto-generated method stub
		this.enablePrediction = bool;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentPrediction.ICRISTUserIntentPrediction#sendFeedback(org.societies.api.internal.personalisation.model.FeedbackEvent)
	 */
	@Override
	public void sendFeedback(FeedbackEvent feedbackEvent) {
		// TODO Auto-generated method stub
		///
		
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentPrediction.ICRISTUserIntentPrediction#updateReceived(org.societies.api.context.model.CtxModelObject)
	 */
	@Override
	public void updateReceived(CtxModelObject ctxModelObj) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc) External 1/3 
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentPrediction.ICRISTUserIntentPrediction#getCRISTPrediction(org.societies.api.identity.IIdentity, org.societies.api.context.model.CtxAttribute)
	 */
	@Override
	public Future<List<CRISTUserAction>> getCRISTPrediction(IIdentity entityID,
			CtxAttribute ctxAttribute) {
		
		if (enablePrediction != true) {
			LOG.error("The enablePrediction is false, getCRISTPrediction can not run.");
			return new AsyncResult<List<CRISTUserAction>>(new ArrayList<CRISTUserAction>());
		}
		
		//handle null parameters
		if (entityID == null)
		{
			LOG.error("The entityID is null, getCRISTPrediction can not run.");
			return new AsyncResult<List<CRISTUserAction>>(new ArrayList<CRISTUserAction>());
		}
		if (ctxAttribute == null)
		{
			LOG.error("The ctxAttribute is null, getCRISTPrediction can not run.");
			return new AsyncResult<List<CRISTUserAction>>(new ArrayList<CRISTUserAction>());
		}
		
		
		if (this.cristTaskManager == null)
		{
			//LOG.info("The CRIST Taks Manager is NULL. ");
/*			cristTaskManager = new CRISTUserIntentTaskManager();
			((CRISTUserIntentTaskManager) cristTaskManager).initialiseCRISTUserIntentManager();*/
			return new AsyncResult<List<CRISTUserAction>>(new ArrayList<CRISTUserAction>());			
		}

		
		
		
		//@@@
	    // Logging for test
		// the size can be get from the most recent performance record
		//m.setPerformanceNameValue("Size=" + cristTaskManager.historyList.size());

		
        m = new PerformanceMessage();
	    m.setSourceComponent(this.getClass()+"");
	    m.setD82TestTableName("S18");
	    m.setTestContext("Personalisation.CRISTUserIntent.IntentPrediction.Delay");
	    m.setOperationType("IntentPredictionFromIntentModel");//?
	    m.setPerformanceType(IPerformanceMessage.Delay);
        //LOG.info("#4#This is a test log for PerformanceMessage S18!");
        long startTime = System.currentTimeMillis();
    
		//@@@ tested method
		List<CRISTUserAction> results = this.cristTaskManager.predictUserIntent(entityID, ctxAttribute);
        
		m.setPerformanceNameValue("Delay=" + (System.currentTimeMillis()-startTime)); //"Delay="
        PERF_LOG.trace(m.toString());
		//Logging end
        //@@@
		
		
		return new AsyncResult<List<CRISTUserAction>>(results);
	}

	/* (non-Javadoc) External 2/3 
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentPrediction.ICRISTUserIntentPrediction#getCRISTPrediction(org.societies.api.identity.IIdentity, org.societies.api.personalisation.model.IAction)
	 */
	@Override
	public Future<List<CRISTUserAction>> getCRISTPrediction(IIdentity entityID,	IAction action) {
		
		if (enablePrediction != true) {
			LOG.error("The enablePrediction is false, getCRISTPrediction can not run.");
			return new AsyncResult<List<CRISTUserAction>>(new ArrayList<CRISTUserAction>());
		}
		
		//handle null parameters
		if (entityID == null)
		{
			LOG.error("The entityID is null, getCRISTPrediction can not run.");
			return new AsyncResult<List<CRISTUserAction>>(new ArrayList<CRISTUserAction>());
		}
		if (action == null)
		{
			LOG.error("The action is null, getCRISTPrediction can not run.");
			return new AsyncResult<List<CRISTUserAction>>(new ArrayList<CRISTUserAction>());
		}
		
		if (this.cristTaskManager == null)
		{
		//	LOG.info("The CRIST Taks Manager is NULL. ");
/*			cristTaskManager = new CRISTUserIntentTaskManager();
			((CRISTUserIntentTaskManager) cristTaskManager).initialiseCRISTUserIntentManager();*/
			return new AsyncResult<List<CRISTUserAction>>(new ArrayList<CRISTUserAction>());			
		}

		
		
		
		//@@@
	    // Logging for test
		// the size can be get from the most recent performance record
		//m.setPerformanceNameValue("Size=" + cristTaskManager.historyList.size());

		
        m = new PerformanceMessage();
		m.setTestContext("Personalisation.CRISTUserIntent.IntentPrediction.Delay");
	    m.setSourceComponent(this.getClass()+"");
        m.setOperationType("IntentPredictionFromIntentModel");//?
        m.setD82TestTableName("S18");
        
        m.setPerformanceType(IPerformanceMessage.Delay);
        long startTime = System.currentTimeMillis();
    
        
        
		
		//@@@ tested method
		List<CRISTUserAction> results = this.cristTaskManager.predictUserIntent(entityID, new CRISTUserAction(action));
		
		m.setPerformanceNameValue("Delay=" + (System.currentTimeMillis()-startTime));
        PERF_LOG.trace(m.toString());
		//Logging end
        //@@@
		
		
		return new AsyncResult<List<CRISTUserAction>>(results);
	}

	/* (non-Javadoc) External 3/3 
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentPrediction.ICRISTUserIntentPrediction#getCurrentUserIntentAction(org.societies.api.identity.IIdentity, org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier)
	 * 
	 * parameterName: the name of the outcome (e.g., volume, font, backgroundColour etc.)
	 */
	@Override
	public Future<CRISTUserAction> getCurrentUserIntentAction(
			IIdentity ownerID, ServiceResourceIdentifier serviceID, String parameterName) { 
		
		if (enablePrediction != true) {
			LOG.error("The enablePrediction is false, getCRISTPrediction can not run.");
			return new AsyncResult<CRISTUserAction>(null);
		}
		
		//handle null parameters
		if (ownerID == null)
		{
			LOG.error("The ownerID is null, getCurrentUserIntentAction can not run.");
			return new AsyncResult<CRISTUserAction>(null);			
		}
		if (serviceID == null)
		{
			LOG.error("The serviceID is null, getCurrentUserIntentAction can not run.");
			return new AsyncResult<CRISTUserAction>(null);			
		}
		if (parameterName == null)
		{
			LOG.error("The parameterName is null, getCurrentUserIntentAction can not run.");
			return new AsyncResult<CRISTUserAction>(null);			
		}
		
		
		if (this.cristTaskManager == null)
		{
		//	LOG.info("The CRIST Taks Manager is NULL. Initiating a new mananger...");
/*			cristTaskManager = new CRISTUserIntentTaskManager();
			((CRISTUserIntentTaskManager) cristTaskManager).initialiseCRISTUserIntentManager();*/
			return new AsyncResult<CRISTUserAction>(null);			
		}
		
		
		
		//@@@
	    // Logging for test
		// the size can be get from the most recent performance record
		//m.setPerformanceNameValue("Size=" + cristTaskManager.historyList.size());

		
        m = new PerformanceMessage();
		m.setTestContext("Personalisation.CRISTUserIntent.IntentPrediction.Delay");
	    m.setSourceComponent(this.getClass()+"");
        m.setOperationType("IntentPredictionFromIntentModel");//?
        m.setD82TestTableName("S18");
        
        m.setPerformanceType(IPerformanceMessage.Delay);
        long startTime = System.currentTimeMillis();
    
        
        
		
		//@@@ tested method
		CRISTUserAction result = this.cristTaskManager.getCurrentUserIntent(ownerID, serviceID, parameterName);
		
		m.setPerformanceNameValue("Delay=" + (System.currentTimeMillis()-startTime));
        PERF_LOG.trace(m.toString());
		//Logging end
        //@@@
		
		return new AsyncResult<CRISTUserAction>(result);
	}
}
