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
package org.societies.privacytrust.privacyprotection.privacypreferencemanager.monitoring.accessCtrl;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;

import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.event.CtxChangeEvent;
import org.societies.api.context.event.CtxChangeEventListener;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.util.DataIdentifierUtils;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyAgreementManager;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.AgreementEnvelope;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.PPNegotiationEvent;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.AccessControlPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PrivacyOutcomeConstantsBean;
import org.societies.api.osgi.event.CSSEvent;
import org.societies.api.osgi.event.EventListener;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Decision;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ActionUtils;
import org.societies.api.privacytrust.privacy.util.privacypolicy.DecisionUtils;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ResourceUtils;
import org.societies.api.schema.identity.DataIdentifier;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.privacytrust.privacyprotection.api.IPrivacyDataManagerInternal;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.accesscontrol.AccessControlOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.accesscontrol.AccessControlPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.constants.PrivacyOutcomeConstants;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.AccessControlPreferenceManager;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.PrivacyPreferenceManager;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.monitoring.IMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * @author Eliza
 *
 */
public class AccCtrlMonitor  extends EventListener implements IMonitor{


	private Logger logging = LoggerFactory.getLogger(this.getClass());
	private final AccessControlPreferenceManager accCtrlManager;

	/* this hashtable holds the context identifiers that appear as PreferenceCondition objs as keys
	 * and the list of details of the preferences affected by these PreferenceCondition objs as values 
	 */
	private Hashtable<CtxIdentifier, ArrayList<AccessControlPreferenceDetailsBean>> monitoringTable = new Hashtable<CtxIdentifier, ArrayList<AccessControlPreferenceDetailsBean>>();
	private final IPrivacyDataManagerInternal privDataManager;
	private final IPrivacyAgreementManager agreementMgr;
	private IIdentityManager idMgr;
	private final ICommManager commMgr;
	private final ICtxBroker ctxBroker;
	private final PrivacyPreferenceManager privPrefMgr;
	private IEventMgr eventMgr;



	public AccCtrlMonitor(PrivacyPreferenceManager privPrefMgr) {
		this.privPrefMgr = privPrefMgr;
		this.privDataManager = privPrefMgr.getprivacyDataManagerInternal();
		this.accCtrlManager = privPrefMgr.getAccessControlPreferenceManager();
		this.agreementMgr = privPrefMgr.getAgreementMgr();
		this.commMgr = privPrefMgr.getCommsMgr();
		this.ctxBroker = privPrefMgr.getCtxBroker();
		this.idMgr = privPrefMgr.getIdm();
		eventMgr = privPrefMgr.getEventMgr();

		eventMgr.subscribeInternalEvent(this, new String[]{EventTypes.PRIVACY_POLICY_NEGOTIATION_EVENT}, null);


	}






	public void monitorThisContext(Hashtable<CtxIdentifier, ArrayList<AccessControlPreferenceDetailsBean>> newDetails){

		Enumeration<CtxIdentifier> keys = newDetails.keys();

		while (keys.hasMoreElements()){
			CtxIdentifier nextElement = keys.nextElement();
			if (this.monitoringTable.containsKey(nextElement)){
				this.monitoringTable.get(nextElement).addAll(newDetails.get(nextElement));
			}else{
				privPrefMgr.getContextCache().getContextCacheUpdater().registerForContextEvent((CtxAttributeIdentifier) nextElement, this);
				this.monitoringTable.put(nextElement, newDetails.get(nextElement));
			}
		}

	}




	private void processChangedContext(CtxChangeEvent event) throws PrivacyException, InvalidFormatException, MalformedCtxIdentifierException{
		//JOptionPane.showMessageDialog(null, "Received context event: " + event.getId().getType());

		Enumeration<CtxIdentifier> e = monitoringTable.keys();
		ArrayList<AccessControlPreferenceDetailsBean> arrayList = new ArrayList<AccessControlPreferenceDetailsBean>();
		while (e.hasMoreElements()){
			
			CtxIdentifier nextElement = e.nextElement();
			//JOptionPane.showMessageDialog(null, "inside keys loop: "+ nextElement.getUri() );
			if (nextElement.equals(event.getId())){
				arrayList = monitoringTable.get(nextElement);
				break;
			}
		}

		for (AccessControlPreferenceDetailsBean detail : arrayList){
			//JOptionPane.showMessageDialog(null, "inside for loop1 ");
			AgreementEnvelope agreement = this.agreementMgr.getAgreement(RequestorUtils.toRequestor(detail.getRequestor(), this.idMgr));
			//JOptionPane.showMessageDialog(null, "Retrieved agreement for requestor: "+RequestorUtils.toXmlString(detail.getRequestor())+agreement);
			List<ResponseItem> requestedItems = agreement.getAgreement().getRequestedItems();
			List<Condition> conditions = new ArrayList<Condition>();
			for (ResponseItem respItem : requestedItems){
				RequestItem requestItem = respItem.getRequestItem();
				if (requestItem.getResource().getDataType().equals(event.getId().getType())){
					conditions = requestItem.getConditions();
				}
			}
			AccessControlPreferenceTreeModel privPrefModel = this.accCtrlManager.getAccCtrlPreference(detail);
			//ResponseItem evaluateAccCtrlPreference = this.accCtrlManager.evaluateAccCtrlPreference(detail, conditions);
			IPrivacyOutcome evaluateAccCtrlPreference = this.accCtrlManager.evaluatePreference(privPrefModel.getPref(), conditions);
			AccessControlOutcome outcome = (AccessControlOutcome) evaluateAccCtrlPreference;
			if (evaluateAccCtrlPreference==null){
				List<Action> actions = new ArrayList<Action>();
				actions.add(ActionUtils.toAction(detail.getAction()));
				boolean deletePermission = this.privDataManager.deletePermission(RequestorUtils.toRequestor(detail.getRequestor(), idMgr), ResourceUtils.getDataIdentifier(detail.getResource()), actions);
				//JOptionPane.showMessageDialog(null, "Deleted permission on privDataManager");

				this.logging.debug("Deleted permission on privDataManager for dataUri: "+detail.getResource().getDataIdUri()+" and action: "+detail.getAction()+"requestor: "+detail.getRequestor().getRequestorId());
			}else{
				List<Action> actions = new ArrayList<Action>();
				actions.add(ActionUtils.toAction(detail.getAction()));
				if(outcome.getEffect().equals(PrivacyOutcomeConstantsBean.ALLOW)){
					boolean updatePermission = this.privDataManager.updatePermission(RequestorUtils.toRequestor(detail.getRequestor(), idMgr), ResourceUtils.getDataIdentifier(detail.getResource()), actions, Decision.PERMIT);
					//JOptionPane.showMessageDialog(null, "Updated permission on privDataManager");
					this.logging.debug("Updated permission on privDataManager for dataUri: "+detail.getResource().getDataIdUri()+" and action: "+detail.getAction()+"requestor: "+detail.getRequestor().getRequestorId()+" with decision: "+Decision.PERMIT);	
				}else{
					boolean updatePermission = this.privDataManager.updatePermission(RequestorUtils.toRequestor(detail.getRequestor(), idMgr), ResourceUtils.getDataIdentifier(detail.getResource()), actions, Decision.DENY);
					//JOptionPane.showMessageDialog(null, "Updated permission on privDataManager");
					this.logging.debug("Updated permission on privDataManager for dataUri: "+detail.getResource().getDataIdUri()+" and action: "+detail.getAction()+"requestor: "+detail.getRequestor().getRequestorId()+" with decision: "+Decision.DENY);
				}
				
			}
		}
	}






	@Override
	public void handleInternalEvent(InternalEvent event) {
		
		PPNegotiationEvent ppnEvent = (PPNegotiationEvent) event.geteventInfo();

		List<ResponseItem> requestedItems = ppnEvent.getAgreement().getRequestedItems();
		List<DataIdentifier> resources = this.getDataIdentifiers(requestedItems);
		RequestorBean requestor = ppnEvent.getAgreement().getRequestor();
		try {
			Hashtable<CtxIdentifier, ArrayList<AccessControlPreferenceDetailsBean>> preferenceConditions = this.privPrefMgr.getAccessControlPreferenceManager().getContextConditions(RequestorUtils.toRequestor(requestor, idMgr), resources);
			//JOptionPane.showMessageDialog(null, "Retrieved preferenceConditions: "+preferenceConditions.size());
			this.monitorThisContext(preferenceConditions);
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	private List<DataIdentifier> getDataIdentifiers(List<ResponseItem> requestedItems) {
		List<DataIdentifier> dataIds = new ArrayList<DataIdentifier>();
		
		for (ResponseItem respItem : requestedItems){
			try {
				dataIds.add(ResourceUtils.getDataIdentifier(respItem.getRequestItem().getResource()));
			} catch (MalformedCtxIdentifierException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		return dataIds;
	}


	@Override
	public void handleExternalEvent(CSSEvent event) {
		// TODO Auto-generated method stub

	}


	@Override
	public void onModification(CtxChangeEvent event) {
		try {
			this.processChangedContext(event);
		} catch (MalformedCtxIdentifierException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PrivacyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}






	@Override
	public String getMonitorID() {
		// TODO Auto-generated method stub
		return this.getClass().getName();
	}


}
