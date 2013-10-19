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
package org.societies.personalisation.CommunityPreferenceManagement.impl.comms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.management.ICis;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.personalisation.model.PreferenceDetails;
import org.societies.api.internal.schema.personalisation.comms.CommunityPersonalisationCommsClientBean;
import org.societies.api.internal.schema.personalisation.comms.CommunityPersonalisationMethodType;
import org.societies.api.internal.schema.personalisation.comms.CommunityPersonalisationResultBean;
import org.societies.api.internal.schema.personalisation.model.PreferenceDetailsBean;
import org.societies.api.internal.schema.personalisation.model.PreferenceTreeModelBean;
import org.societies.personalisation.preference.api.CommunityPreferenceManagement.ICommunityPreferenceManager;
import org.societies.personalisation.preference.api.model.IPreferenceTreeModel;
import org.societies.personalisation.preference.api.model.util.PreferenceUtils;

/**
 * @author Eliza
 *
 */
public class CommunityPreferenceManagementClient implements ICommunityPreferenceManager, ICommCallback{

	private static Logger LOG = LoggerFactory.getLogger(CommunityPreferenceManagementClient.class);

	private static final List<String> NAMESPACES = Collections.unmodifiableList(
			  Arrays.asList("http://societies.org/api/schema/personalisation/model",
				  		"http://societies.org/api/schema/personalisation/mgmt",
				  		"http://societies.org/api/schema/identity",
				  		"http://societies.org/api/schema/servicelifecycle/model",
				  		"http://societies.org/api/internal/schema/personalisation/model",
				  		"http://societies.org/api/internal/schema/personalisation/comms"));
	private static final List<String> PACKAGES = Collections.unmodifiableList(
		  Arrays.asList("org.societies.api.schema.personalisation.model",
						"org.societies.api.schema.personalisation.mgmt",
						"org.societies.api.schema.identity",
						"org.societies.api.schema.servicelifecycle.model",
						"org.societies.api.internal.schema.personalisation.model",
						"org.societies.api.internal.schema.personalisation.comms"));
	
	private ICommManager commManager;
	private ICisManager cisManager;
	
	private Hashtable<String, CommunityPersonalisationResultBean> results = new Hashtable<String, CommunityPersonalisationResultBean>();
	
	public void InitService(){
		//REGISTER OUR ServiceManager WITH THE XMPP Communication Manager
		try {
			commManager.register(this); 
		} catch (CommunicationException e) {
			e.printStackTrace();
		}
		
	}
	


	@Override
	public List<PreferenceDetails> getCommunityPreferenceDetails(IIdentity cisId) {
		ICis cis = cisManager.getCis(cisId.getBareJid());
		String ownerId = cis.getOwnerId();
		IIdentity toIdentity;
		try {
			toIdentity = this.commManager.getIdManager().fromFullJid(ownerId);
			Stanza stanza = new Stanza(toIdentity);
			
			CommunityPersonalisationCommsClientBean bean = new CommunityPersonalisationCommsClientBean();
			bean.setCisId(cisId.getBareJid());
			bean.setMethodType(CommunityPersonalisationMethodType.GET_COMMUNITY_PREFERENCE_DETAILS);
			
			String requestID = UUID.randomUUID().toString();
			bean.setRequestID(requestID);
			this.commManager.sendIQGet(stanza, bean, this);
			
			while (!this.results.containsKey(requestID)){
				synchronized (this.results) {
					this.results.wait();
				}
			}
			
			CommunityPersonalisationResultBean resultBean = this.results.get(requestID);
			ArrayList<PreferenceDetails> details = new ArrayList<PreferenceDetails>();
			
			for (PreferenceDetailsBean detailBean : resultBean.getDetails()){
				details.add(PreferenceUtils.toPreferenceDetails(detailBean));
			}
			return details;
			
			
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.LOG.equals("Error - returning empty list of community preference models");

		return new ArrayList<PreferenceDetails>();
		
		
	}

	@Override
	public List<IPreferenceTreeModel> getAllCommunityPreferences(IIdentity cisId) {
		ICis cis = cisManager.getCis(cisId.getBareJid());
		String ownerId = cis.getOwnerId();
		IIdentity toIdentity;
		try {
			toIdentity = this.commManager.getIdManager().fromFullJid(ownerId);
			Stanza stanza = new Stanza(toIdentity);
			
			CommunityPersonalisationCommsClientBean bean = new CommunityPersonalisationCommsClientBean();
			bean.setCisId(cisId.getBareJid());
			bean.setMethodType(CommunityPersonalisationMethodType.GET_ALL_COMMUNITY_PREFERENCES);
			String requestID = UUID.randomUUID().toString();
			bean.setRequestID(requestID);
			this.commManager.sendIQGet(stanza, bean, this);
			
			while (!this.results.containsKey(requestID)){
				synchronized (this.results) {
					this.results.wait();
				}
			}
			
			CommunityPersonalisationResultBean resultBean = this.results.get(requestID);
			ArrayList<IPreferenceTreeModel> models = new ArrayList<IPreferenceTreeModel>();
			
			for (PreferenceTreeModelBean modelBean : resultBean.getModels()){
				models.add(PreferenceUtils.toPreferenceTreeModel(modelBean));
			}
			
			return models;
			
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.LOG.equals("Error - returning empty list of community preference details");

		return new ArrayList<IPreferenceTreeModel>();
		
	}

	@Override
	public void uploadUserPreferences(IIdentity cisId,
			List<IPreferenceTreeModel> preferences) {
		ICis cis = cisManager.getCis(cisId.getBareJid());
		String ownerId = cis.getOwnerId();
		IIdentity toIdentity;
		try {
			toIdentity = this.commManager.getIdManager().fromFullJid(ownerId);
			Stanza stanza = new Stanza(toIdentity);
			
			CommunityPersonalisationCommsClientBean bean = new CommunityPersonalisationCommsClientBean();
			bean.setCisId(cisId.getBareJid());
			ArrayList<PreferenceTreeModelBean> modelBeans = new ArrayList<PreferenceTreeModelBean>();
			for (IPreferenceTreeModel model : preferences){
				modelBeans.add(PreferenceUtils.toPreferenceTreeModelBean(model));
			}
			bean.setModels(modelBeans);
			bean.setMethodType(CommunityPersonalisationMethodType.UPLOAD_USER_PREFERENCES);
			String requestID = UUID.randomUUID().toString();
			bean.setRequestID(requestID);
			this.commManager.sendMessage(stanza, bean);
			return;
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

		this.LOG.equals("Error - uploading user preferences to community");

		
		
	}

	@Override
	public List<IPreferenceTreeModel> getCommunityPreferences(IIdentity cisId,
			List<PreferenceDetails> details) {
		ICis cis = cisManager.getCis(cisId.getBareJid());
		String ownerId = cis.getOwnerId();
		IIdentity toIdentity;
		try {
			toIdentity = this.commManager.getIdManager().fromFullJid(ownerId);
			Stanza stanza = new Stanza(toIdentity);
			
			CommunityPersonalisationCommsClientBean bean = new CommunityPersonalisationCommsClientBean();
			bean.setCisId(cisId.getBareJid());
			bean.setMethodType(CommunityPersonalisationMethodType.GET_COMMUNITY_PREFERENCES);
			
			List<PreferenceDetailsBean> detailBeans = new ArrayList<PreferenceDetailsBean>();
			for (PreferenceDetails detail: details){
				detailBeans.add(PreferenceUtils.toPreferenceDetailsBean(detail));
			}
			
			bean.setDetails(detailBeans);
			String requestID = UUID.randomUUID().toString();
			bean.setRequestID(requestID);
			this.commManager.sendIQGet(stanza, bean, this);
			
			while (!this.results.containsKey(requestID)){
				synchronized (this.results) {
					this.results.wait();
				}
			}
			
			CommunityPersonalisationResultBean resultBean = this.results.get(requestID);
			ArrayList<IPreferenceTreeModel> models = new ArrayList<IPreferenceTreeModel>();
			
			for (PreferenceTreeModelBean modelBean : resultBean.getModels()){
				models.add(PreferenceUtils.toPreferenceTreeModel(modelBean));
			}
			
			return models;
			
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.LOG.equals("Error - returning empty list of community preference details");

		return new ArrayList<IPreferenceTreeModel>();
	}

	
	public ICommManager getCommManager() {
		return commManager;
	}

	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
	}

	
	
	/**
	 * ICommManager methods
	 */
	@Override
	public List<String> getJavaPackages() {
		// TODO Auto-generated method stub
		return PACKAGES;
	}

	@Override
	public List<String> getXMLNamespaces() {
		// TODO Auto-generated method stub
		return NAMESPACES;
	}

	@Override
	public void receiveError(Stanza arg0, XMPPError arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveInfo(Stanza arg0, String arg1, XMPPInfo arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveItems(Stanza arg0, String arg1, List<String> arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveMessage(Stanza arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveResult(Stanza stanza, Object obj) {
		if (obj instanceof CommunityPersonalisationResultBean){
			CommunityPersonalisationResultBean bean = (CommunityPersonalisationResultBean) obj;
			this.results.put(bean.getRequestID(), bean);
			
		}
		
	}

	public ICisManager getCisManager() {
		return cisManager;
	}

	public void setCisManager(ICisManager cisManager) {
		this.cisManager = cisManager;
	}


}
