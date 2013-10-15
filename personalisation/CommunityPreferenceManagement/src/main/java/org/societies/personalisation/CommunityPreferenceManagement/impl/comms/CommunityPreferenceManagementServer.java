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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.interfaces.IFeatureServer;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.personalisation.model.PreferenceDetails;
import org.societies.api.internal.schema.personalisation.comms.CommunityPersonalisationCommsClientBean;
import org.societies.api.internal.schema.personalisation.comms.CommunityPersonalisationMethodType;
import org.societies.api.internal.schema.personalisation.comms.CommunityPersonalisationResultBean;
import org.societies.api.internal.schema.personalisation.model.PreferenceDetailsBean;
import org.societies.api.internal.schema.personalisation.model.PreferenceTreeModelBean;
import org.societies.personalisation.CommunityPreferenceManagement.impl.CommunityPreferenceManagement;
import org.societies.personalisation.preference.api.model.IPreferenceTreeModel;
import org.societies.personalisation.preference.api.model.util.PreferenceUtils;

/**
 * @author Eliza
 *
 */
public class CommunityPreferenceManagementServer implements IFeatureServer{

	private static Logger LOG = LoggerFactory.getLogger(CommunityPreferenceManagementServer.class);
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

	private CommunityPreferenceManagement communityPreferenceManager;
	private ICommManager commsManager;
	
	public void InitService(){
		//REGISTER OUR ServiceManager WITH THE XMPP Communication Manager
		try {
			getCommsManager().register(this); 
		} catch (CommunicationException e) {
			e.printStackTrace();
		}
		
	}
	
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
	public Object getQuery(Stanza stanza, Object obj) throws XMPPError {
		if (obj instanceof CommunityPersonalisationCommsClientBean){
			CommunityPersonalisationCommsClientBean bean = (CommunityPersonalisationCommsClientBean) obj;

			if (bean.getMethodType().equals(CommunityPersonalisationMethodType.GET_COMMUNITY_PREFERENCE_DETAILS)){
				try {
					IIdentity cisId = this.getCommsManager().getIdManager().fromJid(bean.getCisId());
					List<PreferenceDetails> communityPreferenceDetails = this.getCommunityPreferenceManager().getCommunityPreferenceDetails(cisId);
					ArrayList<PreferenceDetailsBean> beans = new ArrayList<PreferenceDetailsBean>();
					for (PreferenceDetails detail: communityPreferenceDetails){
						beans.add(PreferenceUtils.toPreferenceDetailsBean(detail));
					}
					CommunityPersonalisationResultBean resultBean = new CommunityPersonalisationResultBean();
					resultBean.setDetails(beans);
					return resultBean;
				} catch (InvalidFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}else if (bean.getMethodType().equals(CommunityPersonalisationMethodType.GET_ALL_COMMUNITY_PREFERENCES)){
				try {
					IIdentity cisId = this.getCommsManager().getIdManager().fromJid(bean.getCisId());

						List<IPreferenceTreeModel> communityPreferences = this.getCommunityPreferenceManager().getAllCommunityPreferences(cisId);
						List<PreferenceTreeModelBean> beans = new ArrayList<PreferenceTreeModelBean>();
						for (IPreferenceTreeModel model : communityPreferences){
							beans.add(PreferenceUtils.toPreferenceTreeModelBean(model));
						}
						CommunityPersonalisationResultBean resultBean = new CommunityPersonalisationResultBean();
						resultBean.setModels(beans);
						return resultBean;


				} catch (InvalidFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}else if (bean.getMethodType().equals(CommunityPersonalisationMethodType.GET_COMMUNITY_PREFERENCES)){
				IIdentity cisId;
				try {
					cisId = this.getCommsManager().getIdManager().fromJid(bean.getCisId());
					List<PreferenceDetails> details = new ArrayList<PreferenceDetails>();
					for (PreferenceDetailsBean detailBean : bean.getDetails()){
						details.add(PreferenceUtils.toPreferenceDetails(detailBean));
					}
					List<IPreferenceTreeModel> communityPreferences = this.getCommunityPreferenceManager().getCommunityPreferences(cisId, details);
					List<PreferenceTreeModelBean> beans = new ArrayList<PreferenceTreeModelBean>();
					for (IPreferenceTreeModel model : communityPreferences){
						beans.add(PreferenceUtils.toPreferenceTreeModelBean(model));
					}
					CommunityPersonalisationResultBean resultBean = new CommunityPersonalisationResultBean();
					resultBean.setModels(beans);
					return resultBean;
				} catch (InvalidFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			else{
				if (LOG.isDebugEnabled()){
					this.LOG.debug("Error - bean method type not recognised, returning null");
				}
				return null;
			}

		}
		if (LOG.isDebugEnabled()){
			this.LOG.debug("Error - bean type not recognised, returning null");
		}
		return null;


	}



	@Override
	public void receiveMessage(Stanza stanza, Object obj) {
		if (obj instanceof CommunityPersonalisationCommsClientBean){
			CommunityPersonalisationCommsClientBean bean = (CommunityPersonalisationCommsClientBean) obj;
			if (bean.getMethodType().equals(CommunityPersonalisationMethodType.UPLOAD_USER_PREFERENCES)){
				IIdentity cisId;
				try {
					cisId = getCommsManager().getIdManager().fromJid(bean.getCisId());
					List<PreferenceTreeModelBean> modelBeans = bean.getModels();
					List<IPreferenceTreeModel> models = new ArrayList<IPreferenceTreeModel>();
					for (PreferenceTreeModelBean modelBean : modelBeans){
						models.add(PreferenceUtils.toPreferenceTreeModel(modelBean));
					}
					this.getCommunityPreferenceManager().uploadUserPreferences(cisId, models);
				} catch (InvalidFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

	}

	@Override
	public Object setQuery(Stanza arg0, Object arg1) throws XMPPError {
		// TODO Auto-generated method stub
		return null;
	}

	public ICommManager getCommsManager() {
		return commsManager;
	}

	public void setCommsManager(ICommManager commsManager) {
		this.commsManager = commsManager;
	}

	public CommunityPreferenceManagement getCommunityPreferenceManager() {
		return communityPreferenceManager;
	}

	public void setCommunityPreferenceManager(CommunityPreferenceManagement communityPreferenceManager) {
		this.communityPreferenceManager = communityPreferenceManager;
	}

}
