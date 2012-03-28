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

package org.societies.personalisation.CRISTUserIntentTaskManager.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.societies.api.context.model.CtxAttribute;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.personalisation.CRIST.api.CRISTUserIntentDiscovery.ICRISTUserIntentDiscovery;
import org.societies.personalisation.CRIST.api.CRISTUserIntentPrediction.ICRISTUserIntentPrediction;
import org.societies.personalisation.CRIST.api.CRISTUserIntentTaskManager.ICRISTUserIntentTaskManager;
import org.societies.personalisation.CRIST.api.model.CRISTUserAction;
import org.societies.personalisation.CRIST.api.model.CRISTUserSituation;
import org.societies.personalisation.CRIST.api.model.CRISTUserTask;
import org.societies.personalisation.CRIST.api.model.CRISTUserTaskModelData;
import org.societies.personalisation.common.api.management.IPersonalisationInternalCallback;

public class CRISTUserIntentTaskManager implements ICRISTUserIntentTaskManager{

	private LinkedHashMap<String,Integer> intentModel = null; 
	
	private ICRISTUserIntentPrediction cristPrediction;
	private ICRISTUserIntentDiscovery cristDiscovery;
	private ICtxBroker ctxBroker;
	
	private CtxAttribute myCtx;
	private IIdentity myID;
	private IPersonalisationInternalCallback myCallback;
		
	public CRISTUserIntentTaskManager(){
		System.out.println("Hello! I'm the CRIST User Intent Manager!");
	}
	
	public ICRISTUserIntentPrediction getCristPrediction() {
		return cristPrediction;
	}

	public void setCristPrediction(ICRISTUserIntentPrediction cristPrediction) {
		this.cristPrediction = cristPrediction;
	}

	public ICRISTUserIntentDiscovery getCristDiscovery() {
		return cristDiscovery;
	}

	public void setCristDiscovery(ICRISTUserIntentDiscovery cristDiscovery) {
		this.cristDiscovery = cristDiscovery;
	}

	public ICtxBroker getCtxBroker() {
		return ctxBroker;
	}

	public void setCtxBroker(ICtxBroker ctxBroker) {
		this.ctxBroker = ctxBroker;
	}

	public CRISTUserIntentTaskManager(ICRISTUserIntentPrediction CRISTPredictor){
		System.out.println("This is the testing class for CRIST Model!");
		this.setCristPrediction(CRISTPredictor);
	}
	
	public void initialiseCRISTUserIntentManager(){
				
		if (this.getCristPrediction() == null) {
			System.out
					.println(this.getClass().getName() + "CRIST UI Predictor is null");
		} else {
			System.out.println(this.getClass().getName()
					+ "CRIST UI Predictor is NOT null");
		}

		System.out.println("Yo!! I'm a brand new service and my interface is: "
				+ this.getClass().getName());
		try{
			this.getCristPrediction().getCRISTPrediction(myID, myCtx, myCallback);
			System.out.println("CRIST Tester got the CRIST Prediction Result");
		}catch(Exception e){
			System.err.println("Exception when trying to get the CRIST Prediction Result");
			System.err.println(e.toString());
		}
	}
	
	public ICRISTUserIntentPrediction getCristPredictor() {
		System.out.println(this.getClass().getName()+" Return CRISTPredictor");
		return getCristPrediction();
	}

	public void setCristPredictor(ICRISTUserIntentPrediction CRISTPredictor) {
		System.out.println(this.getClass().getName()+" GOT CRISTPredictor");
		this.setCristPrediction(CRISTPredictor);
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentTaskManager.ICRISTUserIntentTaskManager#addSituationsAndActionsToTask(org.societies.personalisation.CRIST.api.model.CRISTUserTask, java.util.HashMap, java.util.HashMap)
	 */
	@Override
	public CRISTUserTask addSituationsAndActionsToTask(CRISTUserTask arg0,
			HashMap<CRISTUserAction, Double> arg1,
			HashMap<CRISTUserSituation, Double> arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentTaskManager.ICRISTUserIntentTaskManager#getAction(java.lang.String)
	 */
	@Override
	public CRISTUserAction getAction(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentTaskManager.ICRISTUserIntentTaskManager#getActionsByType(java.lang.String, java.lang.String)
	 */
	@Override
	public ArrayList<CRISTUserAction> getActionsByType(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentTaskManager.ICRISTUserIntentTaskManager#getCurrentIntentAction(org.societies.api.comm.xmpp.datatypes.IIdentity, org.societies.api.comm.xmpp.datatypes.IIdentity, org.societies.api.servicelifecycle.model.ServiceResourceIdentifier)
	 */
	@Override
	public CRISTUserAction getCurrentIntentAction(IIdentity arg0, IIdentity arg1,
			ServiceResourceIdentifier arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentTaskManager.ICRISTUserIntentTaskManager#getCurrentUserAction()
	 */
	@Override
	public CRISTUserAction getCurrentUserAction() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentTaskManager.ICRISTUserIntentTaskManager#getCurrentUserSituation()
	 */
	@Override
	public CRISTUserSituation getCurrentUserSituation() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentTaskManager.ICRISTUserIntentTaskManager#getNextActions(org.societies.personalisation.CRIST.api.model.CRISTUserAction)
	 */
	@Override
	public HashMap<CRISTUserAction, Double> getNextActions(CRISTUserAction currentAction) {
		// TODO Auto-generated method stub
		String actionValue = currentAction.getActionID();
		CRISTUserSituation currentSituation = getCurrentUserSituation();
		String situationValue = currentSituation.getSituationID();
		
		if (this.intentModel == null){
			// TODO: Retrieve intent model from CtxBroker
			System.out.println("Trying to retrieve the user's intent model from CtxBroker...");
			// this.ctxBroker.retrieveHistory();
			
			if (this.intentModel == null){
				// In case there is no intent model on the CtxBroker, generate a new model
				// TODO: Retrieve the user's history data from CtxBroker
				// this.ctxBroker.retrieveHistory();
				ArrayList<MockHistoryData> historyData = null;
				
				this.cristDiscovery.enableCRISTUIDiscovery(true);
				this.intentModel = this.cristDiscovery.generateNewCRISTUIModel(historyData);
				// TODO: Upload the new intentModel to the CtxBroker				
			}
		}
		
		if (this.intentModel != null){
			// TODO: Get the next actions
			
		}
		
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentTaskManager.ICRISTUserIntentTaskManager#getNextTasks(org.societies.personalisation.CRIST.api.model.CRISTUserTask)
	 */
	@Override
	public HashMap<CRISTUserTask, Double> getNextTasks(CRISTUserTask arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentTaskManager.ICRISTUserIntentTaskManager#getTask(java.lang.String)
	 */
	@Override
	public CRISTUserTask getTask(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentTaskManager.ICRISTUserIntentTaskManager#getTaskModelData()
	 */
	@Override
	public CRISTUserTaskModelData getTaskModelData() {
		// TODO Auto-generated method stub
		
		
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentTaskManager.ICRISTUserIntentTaskManager#getTasks(org.societies.personalisation.CRIST.api.model.CRISTUserAction, org.societies.personalisation.CRIST.api.model.CRISTUserSituation)
	 */
	@Override
	public ArrayList<CRISTUserTask> getTasks(CRISTUserAction arg0,
			CRISTUserSituation arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentTaskManager.ICRISTUserIntentTaskManager#identifyActionTaskInModel(java.lang.String, java.lang.String, java.util.HashMap)
	 */
	@Override	
	public HashMap<CRISTUserAction, CRISTUserTask> identifyActionTaskInModel(
			String arg0, String arg1, HashMap<String, Serializable> arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentTaskManager.ICRISTUserIntentTaskManager#identifyActions()
	 */
	@Override
	public ArrayList<CRISTUserAction> identifyActions() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentTaskManager.ICRISTUserIntentTaskManager#identifySituations()
	 */
	@Override
	public ArrayList<CRISTUserSituation> identifySituations() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentTaskManager.ICRISTUserIntentTaskManager#identifyTasks()
	 */
	@Override
	public ArrayList<CRISTUserTask> identifyTasks() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentTaskManager.ICRISTUserIntentTaskManager#resetTaskModelData()
	 */
	@Override
	public void resetTaskModelData() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentTaskManager.ICRISTUserIntentTaskManager#setNextActionLink(org.societies.personalisation.CRIST.api.model.CRISTUserAction, org.societies.personalisation.CRIST.api.model.CRISTUserAction, java.lang.Double)
	 */
	@Override
	public void setNextActionLink(CRISTUserAction arg0, CRISTUserAction arg1,
			Double arg2) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentTaskManager.ICRISTUserIntentTaskManager#setNextSituationLink(org.societies.personalisation.CRIST.api.model.CRISTUserSituation, org.societies.personalisation.CRIST.api.model.CRISTUserSituation, java.lang.Double)
	 */
	@Override
	public void setNextSituationLink(CRISTUserSituation arg0,
			CRISTUserSituation arg1, Double arg2) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentTaskManager.ICRISTUserIntentTaskManager#setNextTaskLink(org.societies.personalisation.CRIST.api.model.CRISTUserTask, org.societies.personalisation.CRIST.api.model.CRISTUserTask, java.lang.Double)
	 */
	@Override
	public void setNextTaskLink(CRISTUserTask arg0, CRISTUserTask arg1,
			Double arg2) {
		// TODO Auto-generated method stub
		
	}
}
