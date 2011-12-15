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

import org.societies.api.context.model.CtxAttribute;
import org.societies.api.mock.EntityIdentifier;
import org.societies.api.mock.ServiceResourceIdentifier;
import org.societies.personalisation.CRIST.api.CRISTUserIntentPrediction.ICRISTUserIntentPrediction;
import org.societies.personalisation.CRIST.api.CRISTUserIntentTaskManager.ICRISTUserIntentTaskManager;
import org.societies.personalisation.CRIST.api.model.CRISTUserTaskModelData;
import org.societies.personalisation.CRIST.api.model.ICRISTUserAction;
import org.societies.personalisation.CRIST.api.model.ICRISTUserSituation;
import org.societies.personalisation.CRIST.api.model.ICRISTUserTask;

public class CRISTUserIntentTaskManager implements ICRISTUserIntentTaskManager{

	private ICRISTUserIntentPrediction cristPredictor;
	private CtxAttribute myCtx;
		
	public CRISTUserIntentTaskManager(ICRISTUserIntentPrediction CRISTPredictor){
		this.cristPredictor = CRISTPredictor;
	}
	
	public void initialiseCRISTUserIntentManager(){
		System.out.println("This is the testing class for CRIST UI Predictioin");
		cristPredictor.getCRISTPrediction(myCtx);
	}
	
	public ICRISTUserIntentPrediction getCristPredictor() {
		System.out.println(this.getClass().getName()+"Return CRISTPredictor");
		return cristPredictor;
	}

	public void setPreManager(ICRISTUserIntentPrediction CRISTPredictor) {
		System.out.println(this.getClass().getName()+"GOT CRISTPredictor");
		this.cristPredictor = CRISTPredictor;
	}
	
	/* (non-Javadoc)
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentTaskManager.ICRISTUserIntentTaskManager#addSituationsAndActionsToTask(org.societies.personalisation.CRIST.api.model.ICRISTUserTask, java.util.HashMap, java.util.HashMap)
	 */
	@Override
	public ICRISTUserTask addSituationsAndActionsToTask(ICRISTUserTask arg0,
			HashMap<ICRISTUserAction, Double> arg1,
			HashMap<ICRISTUserSituation, Double> arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentTaskManager.ICRISTUserIntentTaskManager#getAction(java.lang.String)
	 */
	@Override
	public ICRISTUserAction getAction(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentTaskManager.ICRISTUserIntentTaskManager#getActionsByType(java.lang.String, java.lang.String)
	 */
	@Override
	public ArrayList<ICRISTUserAction> getActionsByType(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentTaskManager.ICRISTUserIntentTaskManager#getCurrentIntentAction(org.societies.api.mock.EntityIdentifier, org.societies.api.mock.EntityIdentifier, org.societies.api.mock.ServiceResourceIdentifier)
	 */
	@Override
	public ICRISTUserAction getCurrentIntentAction(EntityIdentifier arg0,
			EntityIdentifier arg1, ServiceResourceIdentifier arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentTaskManager.ICRISTUserIntentTaskManager#getCurrentUserAction()
	 */
	@Override
	public ICRISTUserAction getCurrentUserAction() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentTaskManager.ICRISTUserIntentTaskManager#getCurrentUserSituation()
	 */
	@Override
	public ICRISTUserSituation getCurrentUserSituation() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentTaskManager.ICRISTUserIntentTaskManager#getNextActions(org.societies.personalisation.CRIST.api.model.ICRISTUserAction)
	 */
	@Override
	public HashMap<ICRISTUserAction, Double> getNextActions(
			ICRISTUserAction arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentTaskManager.ICRISTUserIntentTaskManager#getNextTasks(org.societies.personalisation.CRIST.api.model.ICRISTUserTask)
	 */
	@Override
	public HashMap<ICRISTUserTask, Double> getNextTasks(ICRISTUserTask arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentTaskManager.ICRISTUserIntentTaskManager#getTask(java.lang.String)
	 */
	@Override
	public ICRISTUserTask getTask(String arg0) {
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
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentTaskManager.ICRISTUserIntentTaskManager#getTasks(org.societies.personalisation.CRIST.api.model.ICRISTUserAction, org.societies.personalisation.CRIST.api.model.ICRISTUserSituation)
	 */
	@Override
	public ArrayList<ICRISTUserTask> getTasks(ICRISTUserAction arg0,
			ICRISTUserSituation arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentTaskManager.ICRISTUserIntentTaskManager#identifyActionTaskInModel(java.lang.String, java.lang.String, java.util.HashMap)
	 */
	@Override
	public HashMap<ICRISTUserAction, ICRISTUserTask> identifyActionTaskInModel(
			String arg0, String arg1, HashMap<String, Serializable> arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentTaskManager.ICRISTUserIntentTaskManager#identifyActions()
	 */
	@Override
	public ArrayList<ICRISTUserAction> identifyActions() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentTaskManager.ICRISTUserIntentTaskManager#identifySituations()
	 */
	@Override
	public ArrayList<ICRISTUserSituation> identifySituations() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentTaskManager.ICRISTUserIntentTaskManager#identifyTasks()
	 */
	@Override
	public ArrayList<ICRISTUserTask> identifyTasks() {
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
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentTaskManager.ICRISTUserIntentTaskManager#setNextActionLink(org.societies.personalisation.CRIST.api.model.ICRISTUserAction, org.societies.personalisation.CRIST.api.model.ICRISTUserAction, java.lang.Double)
	 */
	@Override
	public void setNextActionLink(ICRISTUserAction arg0, ICRISTUserAction arg1,
			Double arg2) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentTaskManager.ICRISTUserIntentTaskManager#setNextSituationLink(org.societies.personalisation.CRIST.api.model.ICRISTUserSituation, org.societies.personalisation.CRIST.api.model.ICRISTUserSituation, java.lang.Double)
	 */
	@Override
	public void setNextSituationLink(ICRISTUserSituation arg0,
			ICRISTUserSituation arg1, Double arg2) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentTaskManager.ICRISTUserIntentTaskManager#setNextTaskLink(org.societies.personalisation.CRIST.api.model.ICRISTUserTask, org.societies.personalisation.CRIST.api.model.ICRISTUserTask, java.lang.Double)
	 */
	@Override
	public void setNextTaskLink(ICRISTUserTask arg0, ICRISTUserTask arg1,
			Double arg2) {
		// TODO Auto-generated method stub
		
	}

}
