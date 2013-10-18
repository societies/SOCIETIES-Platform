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

package org.societies.integration.test.bit.ctxRetrieve;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.broker.ICtxBroker;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.RequestorService;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;

/**
 * Describe your class here...
 *
 * @author Eliza
 *
 */
public class HelloWorld implements IHelloWorld{

	private ServiceResourceIdentifier myServiceID;

	private Logger logging = LoggerFactory.getLogger(this.getClass());

	private IIdentity userIdentity;
	private IIdentity serviceIdentity;
	
	private ICommManager commsMgr;
	private IIdentityManager idMgr;
	
	private RequestorService me;
	
	private ICtxBroker ctxBroker;

	private CtxAttribute nameCtxAttribute;
	private List<CtxModelObject> objects;

	private List<CtxAttribute> retrievedAttributes;
	
	public HelloWorld(){
		this.retrievedAttributes = new ArrayList<CtxAttribute>();
	}

	
	public void initialiseHelloWorld(){
		objects = new ArrayList<CtxModelObject>();
		myServiceID = new ServiceResourceIdentifier();
		myServiceID.setServiceInstanceIdentifier("css://eliza@societies.org/HelloEarth");
		try {
			myServiceID.setIdentifier(new URI("css://eliza@societies.org/HelloEarth"));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		me = new RequestorService(serviceIdentity, myServiceID);
	}
	@Override
	public void displayUserLocation() {
	
		String str = "";
		for (CtxModelObject ctxAttr : this.objects){
			str = str.concat("Got access to: "+ctxAttr.toString()+" : "+ctxAttr.getId().toUriString()+"\n");
		}
		
		JOptionPane.showMessageDialog(null, str);
	}

	/**
	 * @return the commManager
	 */
	public ICommManager getCommsMgr() {
		return commsMgr;
	}

	/**
	 * @param commManager the commManager to set
	 */
	public void setCommsMgr(ICommManager commManager) {
		this.commsMgr = commManager;
		this.idMgr = commManager.getIdManager();
		this.userIdentity = this.idMgr.getThisNetworkNode();
		try {
			this.serviceIdentity = this.idMgr.fromJid("xcmanager.societies.local.macs.hw.ac.uk");
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public List<CtxAttribute> retrieveCtxAttribute(List<String> types){
	
		try {
			List<CtxIdentifier> lookupResults = new ArrayList<CtxIdentifier>();
			for (String x : types)
			{
				Future<List<CtxIdentifier>> flookupResults = this.ctxBroker.lookup(me, userIdentity, CtxModelType.ATTRIBUTE, x);
				lookupResults.addAll(flookupResults.get());
			}
			JOptionPane.showMessageDialog(null, "Retrieved: "+lookupResults.size()+" results from CtxBroker");
			if (lookupResults.size()==0){
				return null;
			}else{
				Future<List<CtxModelObject>> retrieveResults = this.ctxBroker.retrieve(me, lookupResults);
				this.objects = 	retrieveResults.get();

				
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
			JOptionPane.showMessageDialog(null, "Retrieve not allowed on this resource!");
		}
		
		return null;
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
	
}
