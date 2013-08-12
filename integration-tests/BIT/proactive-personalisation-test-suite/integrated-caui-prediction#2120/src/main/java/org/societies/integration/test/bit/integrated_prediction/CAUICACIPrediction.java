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
package org.societies.integration.test.bit.integrated_prediction;

import static org.junit.Assert.*;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;

import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.api.personalisation.model.Action;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.personalisation.CAUI.api.model.IUserIntentAction;
import org.societies.personalisation.CAUI.api.model.UserIntentModelData;


public class CAUICACIPrediction {

	private static Logger LOG = LoggerFactory.getLogger(TestCase2120.class);

	private static final String SERVICE_ID_SUFFIX = ".societies.org";
	private static final String SERVICE_SRI = "css://requestor.societies.org/HelloWorld";
	private static final String SERVICE_TYPE = "radio_service";

	private ServiceResourceIdentifier serviceSri;

	//private IIdentity cssOwnerId;

	boolean modelExist = false;

	@Before
	public void setUp() throws Exception {

		createCAUIModel();
		//createCACIModel();
	}



	public void createCAUIModel() throws URISyntaxException{

		this.serviceSri = new ServiceResourceIdentifier();
		this.serviceSri.setServiceInstanceIdentifier(SERVICE_SRI);
		this.serviceSri.setIdentifier(new URI(SERVICE_SRI));


		HashMap<String,Serializable> context = new HashMap<String,Serializable>();
		context.put(CtxAttributeTypes.LOCATION_SYMBOLIC, "home");
		context.put(CtxAttributeTypes.STATUS, "free");

		UserIntentModelData modelData = TestCase2120.cauiTaskManager.createModel();

		IUserIntentAction userActionOn = TestCase2120.cauiTaskManager.createAction(this.serviceSri ,SERVICE_TYPE,"radio","on");
		userActionOn.setActionContext(context);

		IUserIntentAction userActionSetVol = TestCase2120.cauiTaskManager.createAction(this.serviceSri ,SERVICE_TYPE,"SetVolume","medium");
		userActionSetVol.setActionContext(context);

		IUserIntentAction userActionSetChannel = TestCase2120.cauiTaskManager.createAction(this.serviceSri ,SERVICE_TYPE,"SetChannel","radio1");
		userActionSetChannel.setActionContext(context);

		IUserIntentAction userActionOff = TestCase2120.cauiTaskManager.createAction(this.serviceSri ,SERVICE_TYPE,"radio","off");
		userActionOff.setActionContext(context);

		//On --> setVol 0.5
		TestCase2120.cauiTaskManager.setActionLink(userActionOn, userActionSetVol, 0.3d);	
		TestCase2120.cauiTaskManager.setActionLink(userActionOn, userActionSetChannel, 0.7d);
		TestCase2120.cauiTaskManager.setActionLink(userActionSetVol, userActionOff, 1.0d);	
		TestCase2120.cauiTaskManager.setActionLink(userActionSetChannel, userActionOff, 1.0d);	

		modelData  = TestCase2120.cauiTaskManager.retrieveModel();
		storeModelCtxDB(modelData);

		LOG.debug("CAUI modelData ::"+modelData.getActionModel());
		
		/*
		target.put(userActionSetVol, 0.5d);
		model.put(userActionOn,target);
		target.clear();

		//On --> setChannel 0.5
		target.put(userActionSetChannel,0.5d);
		model.put(userActionOn,target);
		target.clear();

		// SetVol--> off 1.0
		// SetChannel --> off 1.0
		target.put(userActionOff,1.0d);
		model.put(userActionSetVol,target);
		model.put(userActionSetChannel,target);
		target.clear();

		UserIntentModelData intentModel =  
		 */
	}




	@Test
	public void TestPerformOnDemandPrediction() {

		try {	
			LOG.info("TestPerformOnDemandPrediction : waiting 9000 for model creation ");
			Thread.sleep(5000);

			IIdentity cssOwnerId = getOwnerId();

			// this action simulates an action performed by the user 
			IAction actionRadio1 = new Action(this.serviceSri ,SERVICE_TYPE,"radio","on");

			List<IUserIntentAction> actionList = TestCase2120.cauiPrediction.getPrediction(cssOwnerId, actionRadio1).get();
			LOG.info("List of predicted actions :  "+  actionList );

			boolean setChannelActFlag = false;
			boolean setVolumeFlag = false;

			if(actionList.size()>0){

				for(IUserIntentAction predictedAction: actionList){
					String parName = predictedAction.getparameterName();
					String value = predictedAction.getvalue();	

					if(parName.equals("SetVolume") ) setVolumeFlag = true;
					if(parName.equals("SetChannel") ) setChannelActFlag = true;
					LOG.info("CAUI PREDICTION perform prediction :"+ predictedAction +" conf level: "+predictedAction.getConfidenceLevel());
				}
			

				for(IUserIntentAction predictedAction: actionList){
					HashMap<String, Serializable> context = predictedAction.getActionContext();
					if(context != null){
						LOG.info("predicted action cotnext :"+ context);	

					} else {
						LOG.info("predicted action cotnext is null");
					}	


					if(context.get(CtxAttributeTypes.LOCATION_SYMBOLIC)!= null){
						String location = (String) context.get(CtxAttributeTypes.LOCATION_SYMBOLIC);
						LOG.info("String context location value :"+ location);
						Assert.assertEquals("home", location);
					}

					if(context.get(CtxAttributeTypes.STATUS)!= null){
						String status = (String) context.get(CtxAttributeTypes.STATUS);
						LOG.info("String context status value :"+ status);
						Assert.assertEquals("free", status);
					}

				}

			}
			
			assertTrue(setChannelActFlag);
			assertTrue(setVolumeFlag);
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	}




	private CtxAttribute setContext(String type, Serializable value){

		IIdentity cssOwnerId = getOwnerId();
		CtxAttribute attr = null; 
		try {
			IndividualCtxEntity operator = TestCase2120.ctxBroker.retrieveIndividualEntity(cssOwnerId).get();
			Set<CtxAttribute> ctxAttrSet = operator.getAttributes(type);
			if(ctxAttrSet.size()>0 ){
				ArrayList<CtxAttribute> ctxAttrList = new ArrayList<CtxAttribute>(ctxAttrSet);	
				attr = ctxAttrList.get(0);
				attr = TestCase2120.ctxBroker.updateAttribute(attr.getId(), value).get();
			} else {
				attr = TestCase2120.ctxBroker.createAttribute(operator.getId(), type).get();
				attr = TestCase2120.ctxBroker.updateAttribute(attr.getId(),value).get();
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
		LOG.info("ctxAttr of type: "+attr.getType()+" set to value: "+attr.getStringValue());

		return attr;
	}



	private void printOperatorAttr() {

		IIdentity cssOwnerId = getOwnerId();
		try {
			final INetworkNode cssNodeId = TestCase2120.commMgr.getIdManager().getThisNetworkNode();
			//LOG.info("*** cssNodeId = " + cssNodeId);
			final String cssOwnerStr = cssNodeId.getBareJid();

			cssOwnerId = TestCase2120.commMgr.getIdManager().fromJid(cssOwnerStr);
			IndividualCtxEntity operator = TestCase2120.ctxBroker.retrieveIndividualEntity(cssOwnerId).get();

			System.out.println("operator: "+operator);
			Set<CtxAttribute> attrSet = operator.getAttributes();
			//System.out.println("operator attrs : "+attrSet);
			for(CtxAttribute attrs: attrSet){
				System.out.println("attr type: "+attrs.getType());
				if(attrs.getStringValue() != null) System.out.println(" value "+attrs.getStringValue());
			}
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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


	private IIdentity getOwnerId(){

		IIdentity cssOwnerId = null;
		try {
			final INetworkNode cssNodeId = TestCase2120.commMgr.getIdManager().getThisNetworkNode();
			//LOG.info("*** cssNodeId = " + cssNodeId);
			final String cssOwnerStr = cssNodeId.getBareJid();
			cssOwnerId = TestCase2120.commMgr.getIdManager().fromJid(cssOwnerStr);
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return cssOwnerId;
	}


	private CtxAttribute storeModelCtxDB(UserIntentModelData modelData){

		CtxAttribute ctxAttrCAUIModel = null;
		try {
			byte[] binaryModel = SerialisationHelper.serialise(modelData);

			//CtxEntity operator = ctxBroker.retrieveCssOperator().get();

			final INetworkNode cssNodeId = TestCase2120.getCommMgr().getIdManager().getThisNetworkNode();

			final String cssOwnerStr = cssNodeId.getBareJid();
			IIdentity cssOwnerId = TestCase2120.getCommMgr().getIdManager().fromJid(cssOwnerStr);

			IndividualCtxEntity operator = TestCase2120.getCtxBroker().retrieveIndividualEntity(cssOwnerId).get();
			List<CtxIdentifier> cauiModelAttrList = TestCase2120.getCtxBroker().lookup(operator.getId(),CtxModelType.ATTRIBUTE ,CtxAttributeTypes.CAUI_MODEL).get();

			if(!cauiModelAttrList.isEmpty()){
				CtxAttributeIdentifier attrId = (CtxAttributeIdentifier) cauiModelAttrList.get(0);
				ctxAttrCAUIModel = (CtxAttribute) TestCase2120.getCtxBroker().retrieve(attrId).get();
			}

			//ctxAttrCAUIModel = lookupAttrHelp(CtxAttributeTypes.CAUI_MODEL);
			if(ctxAttrCAUIModel != null){

				ctxAttrCAUIModel = TestCase2120.getCtxBroker().updateAttribute(ctxAttrCAUIModel.getId(), binaryModel).get();
			} else {
				ctxAttrCAUIModel = TestCase2120.getCtxBroker().createAttribute(operator.getId(),CtxAttributeTypes.CAUI_MODEL).get();
				ctxAttrCAUIModel = TestCase2120.getCtxBroker().updateAttribute(ctxAttrCAUIModel.getId(), binaryModel).get();
			}

		} catch (Exception e) {
			LOG.error("Exception while storing CAUI model in context DB" + e.getLocalizedMessage());
			e.printStackTrace();
		} 
		return ctxAttrCAUIModel;
	}
}