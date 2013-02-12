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
package org.societies.privacytrust.privacyprotection.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxIdentifierFactory;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyDataManager;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyPolicyManager;
import org.societies.api.internal.privacytrust.privacyprotection.model.listener.IPrivacyDataManagerListener;
import org.societies.api.internal.privacytrust.privacyprotection.model.listener.IPrivacyPolicyManagerListener;
import org.societies.api.internal.privacytrust.privacyprotection.remote.IPrivacyDataManagerRemote;
import org.societies.api.internal.privacytrust.privacyprotection.remote.IPrivacyPolicyManagerRemote;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.privacytrust.privacy.model.privacypolicy.RequestPolicy;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Resource;
import org.societies.api.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.ActionConstants;

/**
 * @author Olivier Maridat (Trialog)
 *
 */
public class Test implements IPrivacyDataManagerListener, IPrivacyPolicyManagerListener {
	private static Logger LOG = LoggerFactory.getLogger(Test.class.getSimpleName());

	private IPrivacyDataManager privacyDataManager;
	private IPrivacyDataManagerRemote privacyDataManagerRemote;
	private IPrivacyPolicyManager privacyPolicyManager;
	private IPrivacyPolicyManagerRemote privacyPolicyManagerRemote;
	private ICommManager commManager;
	private ICtxBroker ctxBroker;
	private CtxEntity person;
	private CtxAttribute symLocAttribute;
	private CtxAttribute statusAttribute;

	private IIdentity ownerId;

	public void start() {
		// -- Privacy Policy Manager usage
		try {
			if (null == privacyPolicyManager) {
				throw new Exception("privacyPolicyManager NULL");
			}
			if (null == commManager) {
				throw new Exception("CommManager NULL");
			}
			if (null == commManager.getIdManager()) {
				throw new Exception("IdManager NULL");
			}
			this.getPersonEntity();
			this.getStatusAttribute();
			this.getSymLocAttribute();
			IIdentity requestorId = commManager.getIdManager().fromJid("orange@societies.local");
			Requestor requestor = new Requestor(requestorId);
			List<RequestItem> requestItems = new ArrayList<RequestItem>();
			Resource resource = new Resource(statusAttribute.getId());
			List<Action> actions = new ArrayList<Action>();
			actions.add(new Action(ActionConstants.READ));
			RequestItem requestItem = new RequestItem(resource, actions, new ArrayList<Condition>());
			requestItems.add(requestItem);
			RequestPolicy privacyPolicy = new RequestPolicy(requestItems);
			privacyPolicy.setRequestor(requestor);
			privacyPolicy = privacyPolicyManager.updatePrivacyPolicy(privacyPolicy);
			LOG.info("************* [Test Resullt] Privacy policy updated? "+(null != privacyPolicy));
			if (null != privacyPolicy) {
				LOG.info(privacyPolicy.toXMLString());
			}
		} catch (Exception e) {
			LOG.error("************* [Tests PrivacyPolicyManager] Error Exception: "+e.getMessage()+"\n", e);
		}


		// -- Privacy Data Manager usage
		try {
			if (null == privacyDataManager) {
				throw new Exception("privacyDataManager NULL");
			}
			if (null == commManager) {
				throw new Exception("CommManager NULL");
			}
			if (null == commManager.getIdManager()) {
				throw new Exception("IdManager NULL");
			}
			IIdentity requestorId = commManager.getIdManager().fromJid("orange@societies.local");
			Requestor requestor = new Requestor(requestorId);
			Action action = new Action(ActionConstants.READ);
			ResponseItem permission = privacyDataManager.checkPermission(requestor, ownerId, this.symLocAttribute.getId(), action);
			LOG.info("************* [Test Resullt] Permission checked? "+(null != permission));
			if (null != permission) {
				LOG.info(permission.toString());
			}
		} catch (Exception e) {
			LOG.error("************* [Tests PrivacyDataManager] Error Exception: "+e.getMessage()+"\n", e);
		}

		// -- Privacy Data Manager Remote Usage
		try {
			if (null == privacyDataManagerRemote) {
				throw new Exception("privacyDataManagerRemote NULL");
			}
			if (null == commManager) {
				throw new Exception("CommManager NULL");
			}
			if (null == commManager.getIdManager()) {
				throw new Exception("IdManager NULL");
			}
			IIdentity requestorId = commManager.getIdManager().fromJid("orange@societies.local");
			Requestor requestor = new Requestor(requestorId);
			Action action = new Action(ActionConstants.READ);
			privacyDataManagerRemote.checkPermission(requestor, ownerId, this.statusAttribute.getId(), action, this);
			LOG.info("************* Permission check remote: launched");
		} catch (Exception e) {
			LOG.error("************* [Tests PrivacyDataManagerRemote] Error Exception: "+e.getMessage()+"\n", e);
		}

		// -- Privacy Policy Manager Remote Usage
		try {
			if (null == privacyPolicyManagerRemote) {
				throw new Exception("privacyPolicyManagerRemote NULL");
			}
			if (null == commManager) {
				throw new Exception("CommManager NULL");
			}
			if (null == commManager.getIdManager()) {
				throw new Exception("IdManager NULL");
			}
			IIdentity requestorId = commManager.getIdManager().fromJid("orange@societies.local");
			Requestor requestor = new Requestor(requestorId);
			privacyPolicyManagerRemote.getPrivacyPolicy(requestor, ownerId, this);
			LOG.info("************* Get privacy policy remote: launched");
		} catch (Exception e) {
			LOG.error("************* [Tests PrivacyDataManagerRemote] Error Exception: "+e.getMessage()+"\n", e);
		}
	}

	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
		LOG.info("************* commManager injected");
	}
	public void setPrivacyDataManager(IPrivacyDataManager privacyDataManager) {
		this.privacyDataManager = privacyDataManager;
		LOG.info("************* privacyDataManager injected");
	}
	public void setPrivacyDataManagerRemote(
			IPrivacyDataManagerRemote privacyDataManagerRemote) {
		this.privacyDataManagerRemote = privacyDataManagerRemote;
		LOG.info("************* privacyDataManagerREMOTE injected");
	}
	public void setPrivacyPolicyManager(IPrivacyPolicyManager privacyPolicyManager) {
		this.privacyPolicyManager = privacyPolicyManager;
		LOG.info("************* privacyPolicyManager injected");
	}



	/* (non-Javadoc)
	 * @see org.societies.api.internal.privacytrust.privacyprotection.model.listener.IPrivacyDataManagerListener#onAccessControlChecked(org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.ResponseItem)
	 */
	@Override
	public void onAccessControlChecked(ResponseItem permission) {
		LOG.info("************* onAccessControlChecked "+permission.toXMLString());
	}
	/* (non-Javadoc)
	 * @see org.societies.api.internal.privacytrust.privacyprotection.model.listener.IPrivacyDataManagerListener#onAccessControlCancelled(java.lang.String)
	 */
	@Override
	public void onAccessControlCancelled(String msg) {
		LOG.info("************* onAccessControlCancelled "+msg);
	}
	/* (non-Javadoc)
	 * @see org.societies.api.internal.privacytrust.privacyprotection.model.listener.IPrivacyDataManagerListener#onAccessControlAborted(java.lang.String, java.lang.Exception)
	 */
	@Override
	public void onAccessControlAborted(String msg, Exception e) {
		LOG.info("************* onAccessControlAborted "+msg, e);
	}

	private void getPersonEntity() throws Exception{
		Future<IndividualCtxEntity> futurePerson = this.getCtxBroker().retrieveCssOperator();
		person = futurePerson.get();
		if (null == person){
			throw new Exception("Person CtxEntity is null");
		}
		this.ownerId = commManager.getIdManager().fromJid(person.getId().getOwnerId());
	}

	private void getSymLocAttribute(){
		try {

			Future<List<CtxIdentifier>> futureAttrs = this.getCtxBroker().lookup(CtxModelType.ATTRIBUTE, CtxAttributeTypes.LOCATION_SYMBOLIC);
			List<CtxIdentifier> attrs = futureAttrs.get();
			if (attrs.size() == 0){
				symLocAttribute = this.getCtxBroker().createAttribute(person.getId(), CtxAttributeTypes.LOCATION_SYMBOLIC).get();
			}else{
				symLocAttribute = (CtxAttribute) this.getCtxBroker().retrieve(attrs.get(0)).get();
			}
			if (symLocAttribute==null){
				LOG.debug(CtxAttributeTypes.LOCATION_SYMBOLIC+" CtxAttribute is null");
			}else{
				LOG.debug(CtxAttributeTypes.LOCATION_SYMBOLIC+" CtxAttribute - NOT NULL");
			}

		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(Exception e){
			e.printStackTrace();
			this.LOG.debug("EXCEPTION!");
		}

	}


	private void getStatusAttribute(){
		try {
			Future<List<CtxIdentifier>> futureAttrs = this.getCtxBroker().lookup(CtxModelType.ATTRIBUTE, CtxAttributeTypes.STATUS);
			List<CtxIdentifier> attrs = futureAttrs.get();
			if (attrs.size() == 0){
				statusAttribute = this.getCtxBroker().createAttribute(person.getId(), CtxAttributeTypes.STATUS).get();
			}else{
				statusAttribute = (CtxAttribute) this.getCtxBroker().retrieve(attrs.get(0)).get();
			}

			if (statusAttribute==null){
				LOG.debug(CtxAttributeTypes.STATUS+" CtxAttribute is null");
			}else{
				LOG.debug(CtxAttributeTypes.STATUS+" CtxAttribute - NOT NULL");
			}
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @return the ctxBroker
	 */
	public ICtxBroker getCtxBroker() {
		return ctxBroker;
	}

	/**
	 * @param ctxBroker the ctxBroker to set
	 */
	public void setCtxBroker(ICtxBroker ctxBroker) {
		this.ctxBroker = ctxBroker;
	}

	/**
	 * @param privacyPolicyManagerRemote the privacyPolicyManagerRemote to set
	 */
	public void setPrivacyPolicyManagerRemote(
			IPrivacyPolicyManagerRemote privacyPolicyManagerRemote) {
		this.privacyPolicyManagerRemote = privacyPolicyManagerRemote;
	}

	/* (non-Javadoc)
	 * @see org.societies.api.internal.privacytrust.privacyprotection.model.listener.IPrivacyPolicyManagerListener#onPrivacyPolicyRetrieved(org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RequestPolicy)
	 */
	@Override
	public void onPrivacyPolicyRetrieved(RequestPolicy privacyPolicy) {
		LOG.info("************* onPrivacyPolicyRetrieved "+(null != privacyPolicy));
		LOG.info("*************"+privacyPolicy.toString());
	}

	/* (non-Javadoc)
	 * @see org.societies.api.internal.privacytrust.privacyprotection.model.listener.IPrivacyPolicyManagerListener#onOperationSucceed(java.lang.String)
	 */
	@Override
	public void onOperationSucceed(String msg) {
		LOG.info("************* onOperationSucceed "+msg);
	}

	/* (non-Javadoc)
	 * @see org.societies.api.internal.privacytrust.privacyprotection.model.listener.IPrivacyPolicyManagerListener#onOperationCancelled(java.lang.String)
	 */
	@Override
	public void onOperationCancelled(String msg) {
		LOG.info("************* onOperaonOperationCancelledtionAborted "+msg);
	}

	/* (non-Javadoc)
	 * @see org.societies.api.internal.privacytrust.privacyprotection.model.listener.IPrivacyPolicyManagerListener#onOperationAborted(java.lang.String, java.lang.Exception)
	 */
	@Override
	public void onOperationAborted(String msg, Exception e) {
		LOG.info("************* onOperationAborted "+msg, e);
	}	



}
