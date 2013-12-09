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

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxEntityTypes;
import org.societies.api.context.model.CtxHistoryAttribute;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.api.internal.logging.IPerformanceMessage;
import org.societies.api.internal.logging.PerformanceMessage;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.personalisation.CRIST.api.CRISTUserIntentDiscovery.ICRISTUserIntentDiscovery;
import org.societies.personalisation.CRIST.api.CRISTUserIntentTaskManager.ICRISTUserIntentTaskManager;
import org.societies.personalisation.CRIST.api.model.CRISTUserAction;
import org.societies.personalisation.CRIST.api.model.CRISTUserSituation;
import org.societies.personalisation.CRIST.api.model.CRISTUserTask;
import org.societies.personalisation.CRIST.api.model.CRISTUserTaskModelData;
import org.societies.personalisation.CRISTUserIntentDiscovery.impl.CRISTHistoryData;
import org.societies.personalisation.CRISTUserIntentDiscovery.impl.CRISTUserIntentDiscovery;
import org.societies.personalisation.common.api.management.IInternalPersonalisationManager;
import org.societies.personalisation.common.api.model.PersonalisationTypes;


public class CRISTUserIntentTaskManager implements ICRISTUserIntentTaskManager {

    private static Logger PERF_LOG = LoggerFactory.getLogger("PerformanceMessage"); 
    // to define a dedicated Logger
    IPerformanceMessage m;

	
	
	private static final Logger LOG = LoggerFactory.getLogger(CRISTUserIntentTaskManager.class);
	public static final int UPDATE_TRIGGER_THRESHOLD = 5;
	public static final int MAX_HISTORY_LENGTH = 1000;
	public static final double COMMUNITY_MODEL_WEIGHT = 0.5; // how much the community influence the individual, from 0 to 1
	public static HashMap<String, CtxAttributeIdentifier> REGISTERED_CONTEXTS = null;

	private ArrayList<CRISTHistoryData> historyList = new ArrayList<CRISTHistoryData>();
	private LinkedHashMap<String, Integer> intentModel = null;// initialized in discovery

	private HashMap<IIdentity, CRISTUserAction> currentUserActionMap = new HashMap<IIdentity, CRISTUserAction>();
	private HashMap<IIdentity, CRISTUserSituation> currentUserSituationMap = new HashMap<IIdentity, CRISTUserSituation>();
	private ICRISTUserIntentDiscovery cristDiscovery;
	private ICtxBroker ctxBroker;
	private IInternalPersonalisationManager persoMgr;

	public CRISTUserIntentTaskManager() {
	//	LOG.info("Hello! I'm the CRIST User Intent Manager!");
	}
	
	public IInternalPersonalisationManager getPersoMgr() {
		return persoMgr;
	}

	public void setPersoMgr(IInternalPersonalisationManager persoMgr) {
		this.persoMgr = persoMgr;
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

	public void initialiseCRISTUserIntentManager() {

		if (ctxBroker == null) {
			LOG.error(this.getClass().getName() + "CtxBroker is null");
		}
		if (this.persoMgr == null) {
			LOG.error(this.getClass().getName() + "PersoMgr is null");
		} 

		LOG.info("Yo!! I'm a brand new service and my interface is: "
				+ this.getClass().getName());

		/*
		 * debug CRISTCtxBrokerContact.initializeHistory(registeredContext,
		 * ctxBrokerContact);
		 * 
		 * historyList =
		 * CRISTCtxBrokerContact.retrieveHistoryData(ctxBrokerContact);
		 * 
		 * for (CRISTHistoryData historyData : historyList) {
		 * LOG.info("retrieveHistoryData ----- " + historyData.toString()); }
		 */
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentTaskManager.
	 * ICRISTUserIntentTaskManager
	 * #addSituationsAndActionsToTask(org.societies.personalisation
	 * .CRIST.api.model.CRISTUserTask, java.util.HashMap, java.util.HashMap)
	 */
	@Override
	public CRISTUserTask addSituationsAndActionsToTask(CRISTUserTask arg0,
			HashMap<CRISTUserAction, Double> arg1,
			HashMap<CRISTUserSituation, Double> arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentTaskManager.
	 * ICRISTUserIntentTaskManager#getAction(java.lang.String)
	 */
	@Override
	public CRISTUserAction getAction(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentTaskManager.
	 * ICRISTUserIntentTaskManager#getActionsByType(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public ArrayList<CRISTUserAction> getActionsByType(String arg0, String arg1) {
		// TODO Auto-generated method stub

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentTaskManager.
	 * ICRISTUserIntentTaskManager
	 * #getCurrentIntentAction(org.societies.api.comm.xmpp.datatypes.IIdentity,
	 * org.societies.api.comm.xmpp.datatypes.IIdentity,
	 * org.societies.api.servicelifecycle.model.ServiceResourceIdentifier)
	 */
	@Override
	public CRISTUserAction getCurrentIntentAction(IIdentity arg0,
			IIdentity arg1, ServiceResourceIdentifier arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentTaskManager.
	 * ICRISTUserIntentTaskManager#getCurrentUserAction()
	 */
	@Override
	public CRISTUserAction getCurrentUserAction(IIdentity entityID) {
		// TODO Auto-generated method stub
		return this.currentUserActionMap.get(entityID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentTaskManager.
	 * ICRISTUserIntentTaskManager#getCurrentUserSituation()
	 */
	@Override
	public CRISTUserSituation getCurrentUserSituation(IIdentity entityID) {
		// TODO Auto-generated method stub
		return this.currentUserSituationMap.get(entityID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentTaskManager.
	 * ICRISTUserIntentTaskManager
	 * #getCurrentUserContext(org.societies.api.identity.IIdentity)
	 */
	@Override
	public ArrayList<String> getCurrentUserContext(IIdentity entityID) {
		// TODO Auto-generated method stub
		return null;
		// return this.currentUserContextMap.get(entityID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentTaskManager.
	 * ICRISTUserIntentTaskManager
	 * #updateUserSituation(org.societies.api.identity.IIdentity,
	 * org.societies.api.context.model.CtxAttribute)
	 */
	@Override
	public void updateUserSituation(IIdentity entityID,
			CtxAttribute ctxAttribute) {

		CRISTUserSituation currentUserSituation = inferUserSituation(entityID);
		if (currentUserSituation != null) {
			this.currentUserSituationMap.put(entityID, currentUserSituation);
		}
	}

	//will be used in inferUserSituation method, ensure not return null
	private String retrieveCurrentUserContext(CtxAttributeIdentifier ctxAttrId) {

		try {
			Future<CtxModelObject> ctxAttributeRetrievedStringFuture = this.ctxBroker
					.retrieve(ctxAttrId);
			CtxAttribute retrievedCtxAttribute = (CtxAttribute) ctxAttributeRetrievedStringFuture
					.get();
			return getValue(retrievedCtxAttribute);
			

		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (CtxException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	private String getValue(CtxAttribute attribute){

		String result = "";

		if (attribute.getStringValue()!=null) {
			result = attribute.getStringValue();
			return result;             			
		}
		else if(attribute.getIntegerValue()!=null) {
			Integer valueInt = attribute.getIntegerValue();
			result = valueInt.toString();
			return result; 
		} else if (attribute.getDoubleValue()!=null) {
			Double valueDouble = attribute.getDoubleValue();
			result = valueDouble.toString();  			
			return result; 
		} 
		return result; 
	}

	//ensure no return null
	//may have bug about convert Set to List
	private CRISTUserAction retrieveCurrentUserAction() {
		try {
			List<CtxAttributeIdentifier> listOfEscortingAttributeIds = new ArrayList<CtxAttributeIdentifier>();

			if (ctxBroker
					.retrieveHistoryTuples(CtxAttributeTypes.LAST_ACTION,
							listOfEscortingAttributeIds, null, null) == null) {
				return null;
			}
			
			Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> ctxHocTuples = ctxBroker
					.retrieveHistoryTuples(CtxAttributeTypes.LAST_ACTION,
							listOfEscortingAttributeIds, null, null).get();
			if (ctxHocTuples == null || ctxHocTuples.size() == 0) {
			    return null;
			   }

			List<CtxHistoryAttribute> primaryHocAttrs = new ArrayList<CtxHistoryAttribute>(ctxHocTuples.keySet());
			CtxHistoryAttribute primaryHocAttr = primaryHocAttrs.get(primaryHocAttrs.size()-1);//the last one is the current action

			IAction retrievedAction = (IAction) SerialisationHelper
					.deserialise(primaryHocAttr.getBinaryValue(), this
							.getClass().getClassLoader());
			return new CRISTUserAction(retrievedAction);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	private CtxAttributeIdentifier registerContextUpdate(IIdentity entityID, String ctxAttributeTypeString)
	{
		CtxEntity operator;
		try {
			operator = this.ctxBroker.retrieveIndividualEntity(entityID).get();
			//operator = this.ctxBroker.retrieveCssOperator().get();
			Set<CtxAttribute> attrSet = operator
					.getAttributes(ctxAttributeTypeString);
			List<CtxAttribute> attrList = new ArrayList<CtxAttribute>(attrSet);
			if (attrList.size() > 0) {
				CtxAttribute attr = attrList.get(0);// the first one
				//System.out.println("persoMgr="+persoMgr);
				this.persoMgr.registerForContextUpdate(entityID, PersonalisationTypes.CRISTIntent, attr.getId());
				return attr.getId();
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (CtxException e) {
			e.printStackTrace();
		}
		if (LOG.isDebugEnabled())LOG.debug("registerContextUpdate failed: " + ctxAttributeTypeString);
		return null;

	}

	/* ensure no return null. How? Add a "OTHER" situation, if any context is null, it is in "OTHER"
	 * @param entityID
	 * @return
	 */
	private CRISTUserSituation inferUserSituation(IIdentity entityID) {

		if (REGISTERED_CONTEXTS == null)
		{
			REGISTERED_CONTEXTS = new HashMap<String, CtxAttributeIdentifier>();
			CtxAttributeIdentifier ctxAttrId = registerContextUpdate(entityID, CtxAttributeTypes.STATUS);
			if (ctxAttrId != null) {
				//System.out.println("test registered 1");
				REGISTERED_CONTEXTS.put(CtxAttributeTypes.STATUS, ctxAttrId);
			}
			ctxAttrId = registerContextUpdate(entityID, CtxAttributeTypes.LOCATION_SYMBOLIC);
			if (ctxAttrId != null) {
				REGISTERED_CONTEXTS.put(CtxAttributeTypes.LOCATION_SYMBOLIC, ctxAttrId);
			}
		}
		
		CRISTUserSituation situation = new CRISTUserSituation();
		//System.out.println("test0");
		if (REGISTERED_CONTEXTS.get(CtxAttributeTypes.STATUS) != null) {
			String currentStatusString = retrieveCurrentUserContext(REGISTERED_CONTEXTS.get(CtxAttributeTypes.STATUS));
			//System.out.println("test1");
			if (!currentStatusString.equals("")) {
				//System.out.println("test2");
				if (REGISTERED_CONTEXTS.get(CtxAttributeTypes.LOCATION_SYMBOLIC) != null) {
					String currentLocationString = retrieveCurrentUserContext(REGISTERED_CONTEXTS.get(CtxAttributeTypes.LOCATION_SYMBOLIC));
					//System.out.println("test3");
					if (!currentLocationString.equals("")) {
						situation.setSituationID(currentStatusString+"_"+currentLocationString);
						//System.out.println("test4");
					}
					else {
						situation.setSituationID(currentStatusString);
					}
				}
				else {
					situation.setSituationID(currentStatusString);
				}
			}
			else {
				if (REGISTERED_CONTEXTS.get(CtxAttributeTypes.LOCATION_SYMBOLIC) != null) {
					String currentLocationString = retrieveCurrentUserContext(REGISTERED_CONTEXTS.get(CtxAttributeTypes.LOCATION_SYMBOLIC));
					if (!currentLocationString.equals("")) {
						situation.setSituationID(currentLocationString);
					}
				}
			}
		}
		else {
			if (REGISTERED_CONTEXTS.get(CtxAttributeTypes.LOCATION_SYMBOLIC) != null) {
				String currentLocationString = retrieveCurrentUserContext(REGISTERED_CONTEXTS.get(CtxAttributeTypes.LOCATION_SYMBOLIC));
				if (!currentLocationString.equals("")) {
					situation.setSituationID(currentLocationString);
				}
			}
		}
		
		if (situation.getSituationID() == null){
			situation.setSituationID("OTHER");
		}
			
		return situation;
	}
	
	public int getMaxIndex(double[] array) {
		
		int maxIndex = 0;
		double a = array[0];
		for(int i=1; i<array.length; i++) {
		    if(a < array[i]) {
		             a = array[i];
		             maxIndex = i;
		        }
		}
		return maxIndex;
		
	}
	
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentTaskManager.
	 * ICRISTUserIntentTaskManager
	 * #predictUserIntent(org.societies.api.identity.IIdentity,
	 * org.societies.api.context.model.CtxAttribute)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<CRISTUserAction> predictUserIntent(IIdentity entityID,
			CtxAttribute ctxAttribute) {
		
		// null paras are already handled in Prediction class
		// update the current situation for current context list in DB. here do not store to DB, ua do.
		CRISTUserSituation currentUserSituation  = inferUserSituation(entityID);

		if (currentUserSituation != null) {
			this.currentUserSituationMap.put(entityID, currentUserSituation);

			CRISTUserAction userAction  = getCurrentUserAction(entityID);
			if (userAction == null)	{
				userAction = retrieveCurrentUserAction();
			}
			if (userAction == null)	{
				return new ArrayList<CRISTUserAction>();
			}
			else {
				this.currentUserActionMap.put(entityID, userAction);
			}
			if (this.intentModel == null) {
				// create intent model
				if (cristDiscovery == null) {
					if (LOG.isDebugEnabled())LOG.debug("The CRIST Discovery is NULL. ");
					return new ArrayList<CRISTUserAction>();
				}
				// this.cristDiscovery.enableCRISTUIDiscovery(true);
				
				
				
				
				
				
				
				
				
				
				
				//@@@
			    // Logging for test
			    m = new PerformanceMessage();
			    m.setTestContext("Personalisation.CRISTUserIntent.HistoryList.Size");
			    m.setSourceComponent(this.getClass()+"");
		        m.setOperationType("IntentModelLearningFromActionHistory");//?
		        m.setD82TestTableName("S18");
		        m.setPerformanceType(IPerformanceMessage.Quanitative);
				m.setPerformanceNameValue("Size=" + historyList.size());
				//LOG.info("#2#This is a test log for PerformanceMessage S18!");
		        PERF_LOG.trace(m.toString());
				
		        m = new PerformanceMessage();
				m.setTestContext("Personalisation.CRISTUserIntent.ModelLearning.Delay");
			    m.setSourceComponent(this.getClass()+"");
		        m.setOperationType("IntentModelLearningFromActionHistory");//?
		        m.setD82TestTableName("S18");
		        
		        m.setPerformanceType(IPerformanceMessage.Delay);
		        long startTime = System.currentTimeMillis();
	        
		        
		        
				
				//@@@ tested method
				this.intentModel = this.cristDiscovery
							.generateNewCRISTUIModel(this.historyList);
				
		        
				m.setPerformanceNameValue("Delay=" + (System.currentTimeMillis()-startTime));
		        PERF_LOG.trace(m.toString());
				//Logging end
		        //@@@
				
				
				
				
				
				
				
			}
			return getNextActions(entityID, userAction, currentUserSituation);
		}
		else {
			if (LOG.isDebugEnabled())LOG.debug("currentUserSituation is null.");
		}

		// When currentUserSituation == null
		return new ArrayList<CRISTUserAction>();
		
	
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentTaskManager.
	 * ICRISTUserIntentTaskManager
	 * #predictUserIntent(org.societies.api.identity.IIdentity,
	 * org.societies.personalisation.CRIST.api.model.CRISTUserAction)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<CRISTUserAction> predictUserIntent(IIdentity entityID,
			CRISTUserAction userAction) {
		
		// null paras are already handled in Prediction class
		/*
		 * if (entityID == null) {
		 * LOG.error("The entityID is null, getCRISTPrediction can not run.");
		 * return new ArrayList<CRISTUserAction>(); } if (userAction == null) {
		 * LOG.error("The action is null, getCRISTPrediction can not run.");
		 * return new ArrayList<CRISTUserAction>(); }
		 */

		// Update the given user's current action,

		// put method is already has the function of replace
		/*
		 * if (this.currentUserActionMap.containsKey(entityID)) {
		 * this.currentUserActionMap.remove(entityID); }
		 */
		this.currentUserActionMap.put(entityID, userAction);

		// update the current situation for current context list in DB
		CRISTUserSituation currentUserSituation = inferUserSituation(entityID);

		if (currentUserSituation != null) {
			this.currentUserSituationMap.put(entityID, currentUserSituation);
			// update local historyList. how about ctxDB? here do not store to DB, ua do.
			this.historyList.add(new CRISTHistoryData(userAction,
					currentUserSituation));
			if (this.intentModel == null
					|| historyList.size() % UPDATE_TRIGGER_THRESHOLD == 0) {
				// update intent model
				if (cristDiscovery == null) {
					if (LOG.isDebugEnabled())LOG.debug("The CRIST Discovery is NULL. ");
					return new ArrayList<CRISTUserAction>();
				}
				// this.cristDiscovery.enableCRISTUIDiscovery(true);
				// control the max length of history list:
				if (historyList.size() > MAX_HISTORY_LENGTH + UPDATE_TRIGGER_THRESHOLD){
					historyList.subList(0, UPDATE_TRIGGER_THRESHOLD).clear();
				}
				
				
				
				
				
				//@@@
			    // Logging for test
			    m = new PerformanceMessage();
			    m.setTestContext("Personalisation.CRISTUserIntent.HistoryList.Size");
			    m.setSourceComponent(this.getClass()+"");
		        m.setOperationType("IntentModelLearningFromActionHistory");//?
		        m.setD82TestTableName("S18");
		        m.setPerformanceType(IPerformanceMessage.Quanitative);
				m.setPerformanceNameValue("Size=" + historyList.size());
				//LOG.info("#1#This is a test log for PerformanceMessage S18!");
		        PERF_LOG.trace(m.toString());
				
		        m = new PerformanceMessage();
				m.setTestContext("Personalisation.CRISTUserIntent.ModelLearning.Delay");
			    m.setSourceComponent(this.getClass()+"");
		        m.setOperationType("IntentModelLearningFromActionHistory");//?
		        m.setD82TestTableName("S18");
		        
		        m.setPerformanceType(IPerformanceMessage.Delay);
		        long startTime = System.currentTimeMillis();
	        
		        
		        
				
				//@@@ tested method
				this.intentModel = this.cristDiscovery
						.generateNewCRISTUIModel(this.historyList);// ensure not
																	// return
																	// null
		        
				m.setPerformanceNameValue("Delay=" + (System.currentTimeMillis()-startTime));
		        PERF_LOG.trace(m.toString());
				//Logging end
		        //@@@
				
				
			}

			return getNextActions(entityID, userAction, currentUserSituation);
		} else {
			if (LOG.isDebugEnabled())LOG.debug("currentUserSituation is null.");
		}

		// When currentUserSituation == null
		return new ArrayList<CRISTUserAction>();
	}

	/*
	 * (non-Javadoc)
	 * able to return null
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentTaskManager.
	 * ICRISTUserIntentTaskManager
	 * #getCurrentUserIntent(org.societies.api.identity.IIdentity,
	 * org.societies.
	 * api.schema.servicelifecycle.model.ServiceResourceIdentifier,
	 * java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public CRISTUserAction getCurrentUserIntent(IIdentity entityID,
			ServiceResourceIdentifier serviceID, String parameterName) {

		CRISTUserAction predictedUserAction = null;
		
		CRISTUserAction currentUserAction = null;
		CRISTUserSituation currentUserSituation = null;

		//why not get from historylist? how to keep historylist updated?
		currentUserAction = this.currentUserActionMap.get(entityID);
		currentUserSituation = this.currentUserSituationMap.get(entityID);
		if (currentUserAction == null) {
			currentUserAction = retrieveCurrentUserAction();
			if (currentUserAction == null) {
				if (LOG.isDebugEnabled())LOG.debug("currentUserAction is null, and no history action.");
				return null;
			}
			currentUserActionMap.put(entityID, currentUserAction);
		}
		if (currentUserSituation == null) {
			currentUserSituation = inferUserSituation(entityID);
			currentUserSituationMap.put(entityID, currentUserSituation);
		}
		if (this.intentModel == null) {
			// create intent model
			if (cristDiscovery == null) {
				if (LOG.isDebugEnabled())LOG.debug("The CRIST Discovery is NULL. ");
				return new CRISTUserAction();
			}
			// this.cristDiscovery.enableCRISTUIDiscovery(true);
			
			
			
			
			
			
			
			
			//@@@
		    // Logging for test
		    m = new PerformanceMessage();
		    m.setTestContext("Personalisation.CRISTUserIntent.HistoryList.Size");
		    m.setSourceComponent(this.getClass()+"");
	        m.setOperationType("IntentModelLearningFromActionHistory");//?
	        m.setD82TestTableName("S18");
	        m.setPerformanceType(IPerformanceMessage.Quanitative);
			m.setPerformanceNameValue("Size=" + historyList.size());
			//LOG.info("#3#This is a test log for PerformanceMessage S18!");
	        PERF_LOG.trace(m.toString());
			
	        m = new PerformanceMessage();
			m.setTestContext("Personalisation.CRISTUserIntent.ModelLearning.Delay");
		    m.setSourceComponent(this.getClass()+"");
	        m.setOperationType("IntentModelLearningFromActionHistory");//?
	        m.setD82TestTableName("S18");
	        
	        m.setPerformanceType(IPerformanceMessage.Delay);
	        long startTime = System.currentTimeMillis();
        
	        
	        
			
			//@@@ tested method
			this.intentModel = this.cristDiscovery
						.generateNewCRISTUIModel(this.historyList);
	        
			m.setPerformanceNameValue("Delay=" + (System.currentTimeMillis()-startTime));
	        PERF_LOG.trace(m.toString());
			//Logging end
	        //@@@
			
			
			
			
			
			
		}

		if (currentUserAction != null && currentUserSituation != null && intentModel != null) {
			ArrayList<CRISTUserAction> results = getNextActions(entityID, currentUserAction,
					currentUserSituation);

			ServiceResourceIdentifier currentServiceID = null;
			String currentParameterName = null;
			for (int i = 0; i < results.size(); i++) {
				currentServiceID = results.get(i).getServiceID();
				currentParameterName = results.get(i).getparameterName();
				if (currentServiceID.equals(serviceID)
						&& currentParameterName.equalsIgnoreCase(parameterName)) {
					predictedUserAction = results.get(i);
					break;
				}
			}
		}

		return predictedUserAction;
	}

	/*
	 * (non-Javadoc)
	 * ensure not return null
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentTaskManager.
	 * ICRISTUserIntentTaskManager
	 * #getNextActions(org.societies.personalisation.CRIST
	 * .api.model.CRISTUserAction)
	 */
	@Override
	public ArrayList<CRISTUserAction> getNextActions(IIdentity entityID, // this
																			// para
																			// isn't
																			// used
			CRISTUserAction currentAction, CRISTUserSituation currentSituation) {

		// null paras are handled in predictUserIntent method
		if (currentAction == null || currentSituation == null) {
			if (LOG.isDebugEnabled())LOG.debug("action or situation is null.");
			return new ArrayList<CRISTUserAction>();
		}


		

		String actionID = currentAction.getActionID();// ensure do not return
														// null

		ArrayList<CRISTUserAction> predictedAction = new ArrayList<CRISTUserAction>();

		HashMap<String, Integer> predictionResult = new HashMap<String, Integer>();

		String currentPrediction = "";
		Integer totalScore = 0;

		/*
		 * update intent model locally, and do not store in DB. if
		 * (this.intentModel == null) { // TODO: Retrieve intent model from
		 * CtxBroker
		 * LOG.info("Trying to retrieve the user's intent model from CtxBroker..."
		 * ); // this.ctxBroker.retrieveCRISTUIModel(entityID);
		 * 
		 * if (this.intentModel == null) { // In case there is no intent model
		 * on the CtxBroker, generate a // new model // TODO: Retrieve the
		 * user's history data from CtxBroker // this.historyList =
		 * this.ctxBroker.retrieveHistory(entiryID); ArrayList<CRISTHistoryData>
		 * historyData = this.historyList;
		 * 
		 * this.cristDiscovery.enableCRISTUIDiscovery(true); this.intentModel =
		 * this.cristDiscovery .generateNewCRISTUIModel(historyData); // TODO:
		 * Upload the new intentModel to the CtxBroker } }
		 */

		// Get the next actions
		LinkedHashMap<String, Integer> candidateAction = new LinkedHashMap<String, Integer>();
		if (currentSituation != null
				&& currentSituation.toString().length() > 0) {
			// In case the user's current situation is available
			String situationValue = currentSituation.getSituationID();
			String currentBehavior = actionID + "@" + situationValue;
			Set<String> modelKeys = this.intentModel.keySet();
			Object[] keyArray = modelKeys.toArray();
			for (int i = 0; i < keyArray.length; i++) {
				if (keyArray[i].toString().startsWith(currentBehavior)) {
					String oneCandidate = keyArray[i].toString().replace(
							currentBehavior, "");
					Integer oneScore = this.intentModel.get(keyArray[i]);
					candidateAction.put(oneCandidate, oneScore);
				}
			}
		} else {
			// In case the user's current situation is not available, consider
			// all situations
			String currentBehavior = actionID;
			Set<String> modelKeys = this.intentModel.keySet();
			Object[] keyArray = modelKeys.toArray();
			for (int i = 0; i < keyArray.length; i++) {
				if (keyArray[i].toString().startsWith(currentBehavior)) {
					String oneCandidate = keyArray[i].toString().replace(
							currentBehavior, "");
					oneCandidate = oneCandidate.substring(
							oneCandidate.indexOf('#'), oneCandidate.length());
					Integer oneScore = this.intentModel.get(keyArray[i]);
					candidateAction.put(oneCandidate, oneScore);
				}
			}
		}

		// In case the predicted user intent action is not null
		if (candidateAction.size() > 0) {
			Set<String> candidateKeys = candidateAction.keySet();
			Object[] candidateArray = candidateKeys.toArray();

			for (int i = 0; i < CRISTUserIntentDiscovery.MAX_PREDICTION_STEP; i++) {
				LinkedHashMap<String, Integer> currentCandidate = new LinkedHashMap<String, Integer>();
				for (int j = 0; j < candidateArray.length; j++) {
					if (candidateArray[j].toString().startsWith(
							currentPrediction)) {
						String[] candidates = candidateArray[j].toString()
								.substring(1).split("#");
						Integer historyScore = candidateAction
								.get(candidateArray[j].toString());
						if (candidates.length > i) {
							if (currentCandidate.containsKey(candidates[i])) {
								Integer currentScore = currentCandidate
										.get(candidates[i]);
								currentCandidate.put(candidates[i],
										currentScore + historyScore);
							} else {
								currentCandidate.put(candidates[i],
										historyScore);
							}
						}
					}
				}

				if (currentCandidate.size() > 0) {
					Set<String> currentCandidateKeys = currentCandidate
							.keySet();
					Object[] currentCandidateArray = currentCandidateKeys
							.toArray();
					Integer[] currentCandidateScore = new Integer[currentCandidate
							.size()];
					for (int j = 0; j < currentCandidate.size(); j++) {
						currentCandidateScore[j] = currentCandidate
								.get(currentCandidateArray[j]);
					}
					int maxScore = 0;
					int maxIndex = 0;
					for (int j = 0; j < currentCandidateScore.length; j++) {
						if (currentCandidateScore[j] > maxScore) {
							maxScore = currentCandidateScore[j];
							maxIndex = j;
						}
					}

					predictionResult.put(
							currentCandidateArray[maxIndex].toString(), maxScore);
					totalScore += maxScore;
					currentPrediction = currentPrediction + "#"
							+ currentCandidateArray[maxIndex];
				}
			}
		}

		if (predictionResult.size() > 0) {
			String[] predictionResultArray = currentPrediction.substring(1)
					.split("#");
			for (int i = 0; i < predictionResult.size(); i++) {
				CRISTUserAction oneAction = new CRISTUserAction();
				oneAction.setActionID(predictionResultArray[i]);
				// Double oneScore =
				// ((double)predictionResult.get(predictionResultArray[i]))/((double)totalScore);
				int confidenceLevel = predictionResult
						.get(predictionResultArray[i]);
				oneAction.setConfidenceLevel(confidenceLevel);
				predictedAction.add(oneAction);
			}
		}

		return predictedAction;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentTaskManager.
	 * ICRISTUserIntentTaskManager
	 * #getNextTasks(org.societies.personalisation.CRIST
	 * .api.model.CRISTUserTask)
	 */
	@Override
	public ArrayList<CRISTUserTask> getNextTasks(CRISTUserTask arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentTaskManager.
	 * ICRISTUserIntentTaskManager#getTask(java.lang.String)
	 */
	@Override
	public CRISTUserTask getTask(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentTaskManager.
	 * ICRISTUserIntentTaskManager#getTaskModelData()
	 */
	@Override
	public CRISTUserTaskModelData getTaskModelData() {
		// TODO Auto-generated method stub

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentTaskManager.
	 * ICRISTUserIntentTaskManager
	 * #getTasks(org.societies.personalisation.CRIST.api.model.CRISTUserAction,
	 * org.societies.personalisation.CRIST.api.model.CRISTUserSituation)
	 */
	@Override
	public ArrayList<CRISTUserTask> getTasks(CRISTUserAction arg0,
			CRISTUserSituation arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentTaskManager.
	 * ICRISTUserIntentTaskManager#identifyActionTaskInModel(java.lang.String,
	 * java.lang.String, java.util.HashMap)
	 */
	@Override
	public HashMap<CRISTUserAction, CRISTUserTask> identifyActionTaskInModel(
			String arg0, String arg1, HashMap<String, Serializable> arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentTaskManager.
	 * ICRISTUserIntentTaskManager#identifyActions()
	 */
	@Override
	public ArrayList<CRISTUserAction> identifyActions() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentTaskManager.
	 * ICRISTUserIntentTaskManager#identifySituations()
	 */
	@Override
	public ArrayList<CRISTUserSituation> identifySituations() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentTaskManager.
	 * ICRISTUserIntentTaskManager#identifyTasks()
	 */
	@Override
	public ArrayList<CRISTUserTask> identifyTasks() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentTaskManager.
	 * ICRISTUserIntentTaskManager#resetTaskModelData()
	 */
	@Override
	public void resetTaskModelData() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentTaskManager.
	 * ICRISTUserIntentTaskManager
	 * #setNextActionLink(org.societies.personalisation
	 * .CRIST.api.model.CRISTUserAction,
	 * org.societies.personalisation.CRIST.api.model.CRISTUserAction,
	 * java.lang.Double)
	 */
	@Override
	public void setNextActionLink(CRISTUserAction arg0, CRISTUserAction arg1,
			Double arg2) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentTaskManager.
	 * ICRISTUserIntentTaskManager
	 * #setNextSituationLink(org.societies.personalisation
	 * .CRIST.api.model.CRISTUserSituation,
	 * org.societies.personalisation.CRIST.api.model.CRISTUserSituation,
	 * java.lang.Double)
	 */
	@Override
	public void setNextSituationLink(CRISTUserSituation arg0,
			CRISTUserSituation arg1, Double arg2) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentTaskManager.
	 * ICRISTUserIntentTaskManager
	 * #setNextTaskLink(org.societies.personalisation.
	 * CRIST.api.model.CRISTUserTask,
	 * org.societies.personalisation.CRIST.api.model.CRISTUserTask,
	 * java.lang.Double)
	 */
	@Override
	public void setNextTaskLink(CRISTUserTask arg0, CRISTUserTask arg1,
			Double arg2) {
		// TODO Auto-generated method stub

	}
	
	public void displayHistoryList() {
		if (LOG.isDebugEnabled())LOG.debug("This is the historyList: ");
		for (int i = 0; i < historyList.size(); i++) {
			System.out.println(historyList.get(i).toString());
		}
	}
	
	public void displayIntentModel() {
		//System.out.println("This is the intentModel.toString(): " + intentModel.toString()); //nearly the same as below
		System.out.println(getCRISTUIModel(intentModel.toString()).toString());
		if (LOG.isDebugEnabled())LOG.debug("This is the intentModel: ");
		Set<String> modelKeys = intentModel.keySet();
		Object[] keyArray = modelKeys.toArray();
		if (keyArray.length == 0)
		{
			if (LOG.isDebugEnabled())LOG.debug("intentModel is not null, but its length is zero.");
		}
		for (int i = 0; i < keyArray.length; i++) {
			System.out.println(keyArray[i].toString() + "=" + intentModel.get(keyArray[i]));
		}
	}

	
	
	
	
	
	//---------------------- for community model -------------------------
	

	
	// create a community CRIST model, store in the contextDB, and return the communityCristModelCtxAttrId
	// may return null
	public CtxAttributeIdentifier createCommunityCRISTModel(IIdentity cisId) {
		try {
			CtxEntityIdentifier communityCtxEntityId;
			if (this.ctxBroker.retrieveCommunityEntityId(cisId) != null) {
				communityCtxEntityId = this.ctxBroker.retrieveCommunityEntityId(cisId).get(); 
			}
			// what if no ctxEntity of this cisId in the contextDB
			else {
				if (LOG.isDebugEnabled())LOG.debug("ctxBroker.createEntity(cisId, CtxEntityTypes.COMMUNITY): " + ctxBroker.createEntity(cisId, CtxEntityTypes.COMMUNITY)); //null
				communityCtxEntityId = ctxBroker.createEntity(cisId, CtxEntityTypes.COMMUNITY).get().getId();
			}
			
			CtxAttribute communityCristModelCtxAttr = this.ctxBroker.createAttribute(communityCtxEntityId, CtxAttributeTypes.CRIST_MODEL).get();
			// what if already a CRIST_MODEL Attribute? 
			
			// fill with local IndividualCRISTModel, because "The CSS who creates the CIS will be added as a member to the CIS and with the owner role."
			communityCristModelCtxAttr.setStringValue(intentModel.toString());
			communityCristModelCtxAttr.setValueType(CtxAttributeValueType.STRING);
			
			
			CtxAttributeIdentifier communityCristModelCtxAttrId = communityCristModelCtxAttr.getId();
			return communityCristModelCtxAttrId;

		
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (CtxException e) {
			e.printStackTrace();
		}
		return null;

	}
	
	// update community CRIST model and store in the contextDB
	// may return null
	public CtxAttributeIdentifier updateCommunityCRISTModel(IIdentity cisId) {
		

		try {
			
			// retrieve CommunityCRISTModel 
			List<CtxIdentifier> attrIds = this.ctxBroker.lookup(cisId, CtxModelType.ATTRIBUTE, CtxAttributeTypes.CRIST_MODEL).get(); // can this method lookup communityCristModelCtxAttrId?
			// what if attrIds == null? return createCommunityCRISTModel(cisId)
			CtxAttributeIdentifier communityCristModelCtxAttrId = (CtxAttributeIdentifier) attrIds.get(0); // if more than one CRIST_MODEL, only get the first one.
	
			CtxAttribute communityCristModelCtxAttr = (CtxAttribute) this.ctxBroker.retrieve(communityCristModelCtxAttrId).get();
			
			LinkedHashMap<String, Integer> communityCRISTModel = retrieveCommunityCRISTModel(communityCristModelCtxAttrId);
			
			// merge CommunityCRISTModel and local IndividualCRISTModel
			for (String currentKey : intentModel.keySet()) {
				Integer individualScore = this.intentModel.get(currentKey);
				if (communityCRISTModel.containsKey(currentKey)) {
					communityCRISTModel.put(currentKey, individualScore + communityCRISTModel.get(currentKey));
				} else {
					communityCRISTModel.put(currentKey, individualScore);
				}
			}

			
			// store to the contextDB
			communityCristModelCtxAttr.setStringValue(communityCRISTModel.toString());
			communityCristModelCtxAttrId = (CtxAttributeIdentifier) this.ctxBroker.update(communityCristModelCtxAttr).get().getId();

			return communityCristModelCtxAttrId;
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		return null;



	}
	
	// merge Community CRIST Model with local Individual CRIST Model to a new Individual model, under the permission from the user.
	public void updateIndividualCRISTModelWithCommunity(IIdentity cisId) {
		
		try {
			
			// retrieve CommunityCRISTModel 
			List<CtxIdentifier> attrIds = this.ctxBroker.lookup(cisId, CtxModelType.ATTRIBUTE, CtxAttributeTypes.CRIST_MODEL).get(); // can this method lookup communityCristModelCtxAttrId?
			// what if attrIds == null? return createCommunityCRISTModel(cisId)
			CtxAttributeIdentifier communityCristModelCtxAttrId = (CtxAttributeIdentifier) attrIds.get(0); // if more than one CRIST_MODEL, only get the first one.
	
			LinkedHashMap<String, Integer> communityCRISTModel = retrieveCommunityCRISTModel(communityCristModelCtxAttrId);
			
			// merge CommunityCRISTModel and local IndividualCRISTModel to a new local IndividualCRISTModel
			for (String currentKey : communityCRISTModel.keySet()) {
				Integer communityScore = communityCRISTModel.get(currentKey);
				if (this.intentModel.containsKey(currentKey)) {
					this.intentModel.put(currentKey, (int)Math.ceil(communityScore * COMMUNITY_MODEL_WEIGHT) + this.intentModel.get(currentKey));
				} else {
					this.intentModel.put(currentKey, (int)Math.ceil(communityScore * COMMUNITY_MODEL_WEIGHT));
				}

			}


			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
	
	
	// retrieve community CRIST model from the contextDB
	// may return null
	//ctxAttribute or ctxAttributeIdentifier is unique, related with its CtxEntity, not like ctxAttributeType, which is not unique.
	public LinkedHashMap<String, Integer> retrieveCommunityCRISTModel(CtxAttributeIdentifier communityCristModelCtxAttrId) {
		
		try {
			Future<CtxModelObject> ctxAttributeRetrievedStringFuture = this.ctxBroker
					.retrieve(communityCristModelCtxAttrId);
			CtxAttribute retrievedCtxAttribute = (CtxAttribute) ctxAttributeRetrievedStringFuture
					.get();
			return getCRISTUIModel(getValue(retrievedCtxAttribute));
			

		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (CtxException e) {
			e.printStackTrace();
		}
		return null;

		
	}
	
	private LinkedHashMap<String, Integer> getCRISTUIModel(String UIModelString){
		LinkedHashMap<String, Integer> UIModel = new LinkedHashMap<String, Integer>();
		// UIModelString = {STARTING_ACTION@RoomA#http://testService_MyTV: TVService: channel: 1=1, http://testService_MyTV: TVService: channel: 1@RoomB#http://testService_MyTV: TVService: channel: 2=5, http://testService_MyTV: TVService: channel: 2@RoomA#http://testService_MyTV: TVService: channel: 1=4, http://testService_MyTV: TVService: channel: 2@RoomA#http://testService_MyTV: TVService: channel: 2=1, http://testService_MyTV: TVService: channel: 2@RoomB#http://testService_MyTV: TVService: channel: 1=5, http://testService_MyTV: TVService: channel: 1@RoomA#http://testService_MyTV: TVService: channel: 2=4}

		String UIModelString_clean = UIModelString.substring(1, UIModelString.length()-1); // no {...}
		String[] itemString = UIModelString_clean.split(", ");
		for (int i = 0; i < itemString.length; i++) {
			// itemString[0] = STARTING_ACTION@RoomA#http://testService_MyTV: TVService: channel: 1=1
			int indexOfEqual = itemString[i].indexOf('=');
			UIModel.put(itemString[i].substring(0, indexOfEqual), Integer.parseInt(itemString[i].substring(indexOfEqual+1)));
		}
	
		
		return UIModel;
		
	}
	
	
	
	
	

}
