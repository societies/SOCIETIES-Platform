/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
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
package org.societies.personalisation.UserPreferenceManagement.impl.merging;



import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.personalisation.model.IOutcome;
import org.societies.api.internal.personalisation.model.PreferenceDetails;
import org.societies.api.internal.servicelifecycle.ServiceModelUtils;
import org.societies.api.internal.useragent.monitoring.UIMEvent;
import org.societies.api.osgi.event.CSSEvent;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.personalisation.UserPreferenceManagement.impl.UserPreferenceManagement;
import org.societies.personalisation.UserPreferenceManagement.impl.monitoring.UserPreferenceConditionMonitor;
import org.societies.personalisation.preference.api.UserPreferenceLearning.IC45Learning;
import org.societies.personalisation.preference.api.model.IC45Consumer;
import org.societies.personalisation.preference.api.model.IC45Output;
import org.societies.personalisation.preference.api.model.IPreference;
import org.societies.personalisation.preference.api.model.IPreferenceTreeModel;
import org.societies.api.osgi.event.EventListener;

public class MergingManager implements IC45Consumer{


	private Logger logging = LoggerFactory.getLogger(this.getClass());
	private Hashtable<IIdentity,Hashtable<IAction, Integer>> counters = new Hashtable<IIdentity,Hashtable<IAction, Integer>>();;

	private UserPreferenceManagement prefImpl;

	private UserPreferenceConditionMonitor pcm;


	private IC45Learning c45Learning;
	
	public MergingManager(IC45Learning c45Learning, UserPreferenceManagement prefImpl, UserPreferenceConditionMonitor pcm){
		this.c45Learning = c45Learning;
		this.prefImpl = prefImpl;
		this.pcm = pcm;
	}



	public void explicitlyTriggerLearning(Date date){
		this.c45Learning.runC45Learning(this, date);
	}

	public void explicitlyTriggerLearning(Date date, ServiceResourceIdentifier serviceId, IAction action){
		this.c45Learning.runC45Learning(this, date, serviceId, action.getparameterName());


	}

	public void explicitlyTriggerLearning(IIdentity userId, Date date, ServiceResourceIdentifier serviceId, IAction action){
		this.c45Learning.runC45Learning(this, date, userId, serviceId, action.getparameterName());
	}
	/*
	 * (non-Javadoc)
	 * @see org.societies.personalisation.preference.api.model.IC45Consumer#handleC45Output(java.util.List)
	 */
	@Override
	public void handleC45Output(List<IC45Output> list) {
		try{
			if(this.logging.isDebugEnabled()){
				logging.debug(this.getClass().getName()+ " received C45Output! size of list:" +list.size());
			}
			for (IC45Output output : list){
				
				IIdentity identity = output.getOwner();
				if(this.logging.isDebugEnabled()){
					this.logging.debug("Processing output for user: "+identity.getIdentifier());
				}
				ServiceResourceIdentifier serviceID = output.getServiceId();
				String serviceType = output.getServiceType();
				List<IPreferenceTreeModel> treeList = output.getTreeList();
				PreferenceMerger prefMerger = new PreferenceMerger(pcm.getUserFeedbackMgr());
				if(this.logging.isDebugEnabled()){
					logging.debug("Trees in C45output: "+treeList.size());
				}
				for (IPreferenceTreeModel tree : treeList){
					if(this.logging.isDebugEnabled()){
						logging.debug(tree.toString());
					}
					String prefName = tree.getPreferenceDetails().getPreferenceName();
					if (prefName==null){
						prefName = this.getPreferenceName(tree);
					}
					IPreference existingPreference = this.getPreferenceFromPM(identity, serviceType, serviceID, prefName);
					if(this.logging.isDebugEnabled()){
						this.logging.debug("Received preference from PM");
					}
					if (existingPreference==null){
						
						if(this.logging.isDebugEnabled()){
							logging.debug("STORING NEW PREFERENCE");
						}
						PreferenceDetails detail = new PreferenceDetails(output.getServiceType(), serviceID, prefName);
						this.prefImpl.storePreference(identity, detail, tree.getRootPreference());
						this.pcm.processPreferenceChanged(identity, output.getServiceId(), output.getServiceType(), prefName);
						
						return;
					}
					IPreference mergedTree = prefMerger.mergeTrees(existingPreference, (IPreference) tree.getRootPreference(), "");
					if (mergedTree == null){
						if(this.logging.isDebugEnabled()){
							logging.debug(this.getClass().getName()+ " CONFLICT!!!! RUNNING C45 AGAIN!!!!");
						}
						this.c45Learning.runC45Learning(new C45ConflictConsumer(identity, serviceType, serviceID, prefName, this.prefImpl), null, identity, serviceID, prefName);
					}else{
						if(this.logging.isDebugEnabled()){
							logging.debug(this.getClass().getName()+" MERGED PREFERENCE: \n"+mergedTree.toTreeString());
						}
						PreferenceDetails detail = new PreferenceDetails(output.getServiceType(), serviceID, prefName);
						this.prefImpl.storePreference(identity, detail, mergedTree);
						
						this.pcm.processPreferenceChanged(identity, output.getServiceId(), output.getServiceType(), prefName);
					}
				}


			}

		}
		catch (Exception e){
			e.printStackTrace();
			if(this.logging.isDebugEnabled()){
				logging.debug(e.toString());
			}
		}

	}

	private String getPreferenceName(IPreferenceTreeModel iptm){
		IPreference p = iptm.getRootPreference();

		Enumeration<IPreference> e = p.preorderEnumeration();

		while (e.hasMoreElements()){
			IPreference temp = e.nextElement();
			if (temp.getUserObject() instanceof IOutcome){
				IOutcome o = temp.getOutcome();
				return o.getparameterName();
			}
		}

		return "NO-PREFERENCE-NAME";
	}
	private IPreference getPreferenceFromPM(IIdentity id, String serviceType, ServiceResourceIdentifier serviceID, String parameterName){
		IPreferenceTreeModel model =this.prefImpl.getModel(id, serviceType, serviceID, parameterName); 
		if (model==null){
			return null;
		}
		return model.getRootPreference();
	}


	public void processActionReceived(IIdentity userId, IAction action){
		if (this.counters.containsKey(userId)){
			if(this.logging.isDebugEnabled()){
				logging.debug("hashtable for identity: "+userId.toString()+" exists");
			}
			Hashtable<IAction, Integer> tempTable = counters.get(userId);
			Enumeration<IAction> e = tempTable.keys();
			if(this.logging.isDebugEnabled()){
				logging.debug(" Processing Action with serviceID: "+action.getServiceID()+ " and identity: "+userId.toString());
			}
			boolean actionExists = false;
			while (e.hasMoreElements()){
				IAction tempAction = e.nextElement();
				if (tempAction.getServiceID().equals(action.getServiceID()) && tempAction.getparameterName().equals(action.getparameterName())){
					if(this.logging.isDebugEnabled()){
						logging.debug(this.getClass().getName()+"found inner hashtable for action: "+action.toString());
					}
					actionExists = true;

					int counter = tempTable.get(tempAction);
					if(this.logging.isDebugEnabled()){
						this.logging.debug("Counter for service:"+ServiceModelUtils.serviceResourceIdentifierToString(action.getServiceID())+" parameter: "+action.getparameterName()+" is "+counter);
					}
					if (counter>=2){
						if(this.logging.isDebugEnabled()){
							this.logging.debug("Counter reached 2, requesting learning and resetting counter");
						}
						tempTable.put(tempAction, new Integer(0)); //reset counter
						IPreferenceTreeModel iptm = this.prefImpl.getModel(userId, action.getServiceType(), action.getServiceID(), action.getparameterName());
						if (iptm==null){
							if(this.logging.isDebugEnabled()){
								logging.debug(this.getClass().getName()+" runC45Learning!");
							}
							
							this.c45Learning.runC45Learning(this, null, userId,  action.getServiceID(), action.getparameterName());
						}else{
							if(this.logging.isDebugEnabled()){
								logging.debug(this.getClass().getName()+" runC45Learning!");
							}
							Date d = iptm.getLastModifiedDate();
							this.c45Learning.runC45Learning(this, d, userId, action.getServiceID(), action.getparameterName());

						}
					}else{
						if(this.logging.isDebugEnabled()){
							logging.debug(this.getClass().getName()+" incrementing counter for action: "+action.toString());
						}
						counter ++;
						if(this.logging.isDebugEnabled()){
							this.logging.debug("Counter for :"+action.toString()+" is "+counter);
						}
						tempTable.put(tempAction, new Integer(counter));
					}
				}
			}
			if (!actionExists){
				tempTable.put(action, new Integer(0));
				
			}
		}else{
			if(this.logging.isDebugEnabled()){
				logging.debug(this.getClass().getName()+" adding hashtable for identity: "+userId.toString());
			}
			Hashtable<IAction, Integer> table = new Hashtable<IAction, Integer>();
			table.put(action, new Integer(0));
			this.counters.put(userId, table);
		}
	}

	/*
	 * REPLACE with direct call to PCM
	 */
/*	private  void sendEvent(IIdentity userId, ServiceResourceIdentifier serviceID, String serviceType, String prefName){
		EventSender evSender = new EventSender(this.eventMgr);
		evSender.sendEvent(userId, serviceID, serviceType, prefName);
	}*/




	/*
	 * (non-Javadoc)
	 * @see org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
	 
	@Override
	public void onApplicationEvent(InternalEvent event) {
		if (event.getEventNode().equals("UIM_EVENT")){
			UIMEvent uimEvent = (UIMEvent) event.getEventInfo();
			this.processActionReceived(uimEvent.getAction(), uimEvent.getUserId());
		}
		
	}
	
	*/


}
