package org.societies.personalisation.CACIDiscovery.test;


import static org.junit.Assert.*;

import static org.mockito.Mockito.*;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.personalisation.CACIDiscovery.impl.CACIDiscovery;
import org.societies.personalisation.CACIDiscovery.impl.UIModelSimilarityEval;
import org.societies.personalisation.CAUI.api.CAUITaskManager.ICAUITaskManager;
import org.societies.personalisation.CAUI.api.model.IUserIntentAction;
import org.societies.personalisation.CAUI.api.model.UserIntentModelData;

public class CACIDiscoveryTest {

	public static final Logger LOG = LoggerFactory.getLogger(CACIDiscoveryTest.class);
	
	static CACIDiscovery discovery = null;
	static ICAUITaskManager cauiTaskManager = null;
		
	private static final String SERVICE_SRI = "css://requestor.societies.org/HelloWorld";
	private static final String SERVICE_TYPE = "radio_service";

	private static ServiceResourceIdentifier serviceSri;
	
	
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		serviceSri = new ServiceResourceIdentifier();
		serviceSri.setServiceInstanceIdentifier(SERVICE_SRI);
		serviceSri.setIdentifier(new URI(SERVICE_SRI));
		
		//discovery = new CACIDiscovery();
		//cauiTaskManager = discovery.getCauiTaskManager();
		//createCAUIModelA();
		//createCAUIModelB();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {

		
	
	
	}

	@After
	public void tearDown() throws Exception {
	}


	@Test
	public void testGenerateVariousUserModels() {
	}

	
	
	public static UserIntentModelData createCAUIModelA() {

		LOG.info("createCAUIModel");

		HashMap<String,Serializable> context1 = new HashMap<String,Serializable>();
		context1.put(CtxAttributeTypes.LOCATION_SYMBOLIC, "home");
		context1.put(CtxAttributeTypes.STATUS, "free");

		HashMap<String,Serializable> context2 = new HashMap<String,Serializable>();
		context2.put(CtxAttributeTypes.LOCATION_SYMBOLIC, "office");
		context2.put(CtxAttributeTypes.STATUS, "free");


		UserIntentModelData modelData = cauiTaskManager.createModel();

		IUserIntentAction userActionOn1 = cauiTaskManager.createAction(serviceSri ,SERVICE_TYPE,"radio","on");
		userActionOn1.setActionContext(context1);

		IUserIntentAction userActionSetVol = cauiTaskManager.createAction(serviceSri ,SERVICE_TYPE,"SetVolume","medium");
		userActionSetVol.setActionContext(context1);

		IUserIntentAction userActionSetChannel = cauiTaskManager.createAction(serviceSri ,SERVICE_TYPE,"SetChannel","radio1");
		userActionSetChannel.setActionContext(context1);

		IUserIntentAction userActionOff1 = cauiTaskManager.createAction(serviceSri ,SERVICE_TYPE,"radio","off");
		userActionOff1.setActionContext(context1);



		//On --> setVol 0.5
		cauiTaskManager.setActionLink(userActionOn1, userActionSetVol, 0.3d);	
		cauiTaskManager.setActionLink(userActionOn1, userActionSetChannel, 0.7d);
		cauiTaskManager.setActionLink(userActionSetVol, userActionOff1, 1.0d);	
		cauiTaskManager.setActionLink(userActionSetChannel, userActionOff1, 1.0d);	

		modelData  = cauiTaskManager.retrieveModel();
		
		System.out.println("A CAUI modelData ::" +cauiTaskManager.retrieveModel().getActionModel());
		LOG.info("A CAUI modelData ::"+modelData.getActionModel());
		
		return modelData;
	}
	
	
	public static UserIntentModelData createCAUIModelB(){

		LOG.info("createCAUIModel");

		HashMap<String,Serializable> context1 = new HashMap<String,Serializable>();
		context1.put(CtxAttributeTypes.LOCATION_SYMBOLIC, "home");
		context1.put(CtxAttributeTypes.STATUS, "free");

		HashMap<String,Serializable> context2 = new HashMap<String,Serializable>();
		context2.put(CtxAttributeTypes.LOCATION_SYMBOLIC, "office");
		context2.put(CtxAttributeTypes.STATUS, "free");


		UserIntentModelData modelData = cauiTaskManager.createModel();

		IUserIntentAction userActionOn1 = cauiTaskManager.createAction(serviceSri ,SERVICE_TYPE,"radio","on");
		userActionOn1.setActionContext(context1);

		
		IUserIntentAction userActionOff1 = cauiTaskManager.createAction(serviceSri ,SERVICE_TYPE,"radio","off");
		userActionOff1.setActionContext(context1);



		//On --> setVol 0.5
		cauiTaskManager.setActionLink(userActionOn1, userActionOff1, 1.0d);	
		
		modelData  = cauiTaskManager.retrieveModel();
		
		System.out.println("B CAUI modelData ::" +cauiTaskManager.retrieveModel().getActionModel());
		LOG.info("CAUI modelData ::"+modelData.getActionModel());
	
		return modelData;
	}
	
	
	
	
	/*
	public UserIntentModelData createModelA(){
		
		UserIntentModelData cauiModelA = new  UserIntentModelData();
		
		HashMap<IUserIntentAction, HashMap<IUserIntentAction,Double>> actionModelA = new HashMap<IUserIntentAction, HashMap<IUserIntentAction,Double>>();
		HashMap<IUserIntentAction,Double> actionModeltarget = new HashMap<IUserIntentAction,Double>(); 
		
		actionModelA.put(mockUserAction1A, null);
		actionModelA.put(mockUserAction1B, null);
		actionModelA.put(mockUserAction1C, null);
		actionModelA.put(mockUserAction1D, null);
		
		// {A-->B,C} {B-->D}, {C-->D} 
		actionModeltarget.put(mockUserAction1B, 0.5);
		actionModeltarget.put(mockUserAction1C, 0.5);
		actionModelA.put(mockUserAction1A,actionModeltarget);
			
		HashMap<IUserIntentAction,Double> actionModeltargetX = new HashMap<IUserIntentAction,Double>(); 
		actionModeltargetX.put(mockUserAction1D,1.0);
		
		actionModelA.put(mockUserAction1B,actionModeltargetX);
		actionModelA.put(mockUserAction1C,actionModeltargetX);
		
		cauiModelA.setActionModel(actionModelA);
		assertEquals(4, cauiModelA.getActionModel().size());
		assertEquals("setRadio", mockUserAction1A.getparameterName());
		assertEquals("on", mockUserAction1A.getvalue());
			
		System.out.println("mockUserAction1A:getServiceID: " +mockUserAction1A.getServiceID());
		System.out.println("CAUI MODEL A:: " +cauiModelA.getActionModel());

		return cauiModelA;
	}
	
	
	public UserIntentModelData createModelB(){
		
		UserIntentModelData cauiModelB = new  UserIntentModelData();
		
		HashMap<IUserIntentAction, HashMap<IUserIntentAction,Double>> actionModelB = new HashMap<IUserIntentAction, HashMap<IUserIntentAction,Double>>();
		HashMap<IUserIntentAction,Double> actionModeltarget = new HashMap<IUserIntentAction,Double>(); 
		
		actionModelB.put(mockUserAction2A, null);
		actionModelB.put(mockUserAction2B, null);
		actionModelB.put(mockUserAction2C, null);
		// {A-->B} {B-->C} 
		actionModeltarget.put(mockUserAction2B, 0.5);
		
		actionModelB.put(mockUserAction2A,actionModeltarget);
		
		actionModeltarget.clear();
		actionModeltarget.put(mockUserAction2C,1.0);
		actionModelB.put(mockUserAction2B,actionModeltarget);
		
		cauiModelB.setActionModel(actionModelB);
		
		assertEquals(3, cauiModelB.getActionModel().size());
		assertEquals("setVolume", mockUserAction2B.getparameterName());
		assertEquals("medium", mockUserAction2B.getvalue());
		
		System.out.println("CAUI MODEL B:: " +cauiModelB.getActionModel());
	
		return cauiModelB;
	}
	*/
	
	@Ignore
	@Test
	public void mergeTargetMaps(){
		
		UserIntentModelData communityModelA = new UserIntentModelData();
		
		
		UserIntentModelData cauiModelA = createCAUIModelA();
		UserIntentModelData cauiModelB = createCAUIModelB();
		
		List<UserIntentModelData> userModelList = new ArrayList<UserIntentModelData>();
		userModelList.add(cauiModelB);
		userModelList.add(cauiModelA);

		//UserIntentModelData merged = discovery.mergeModels(userModelList);
		//System.out.println("merged:"+ merged);
		
		
		
		//HashMap<IUserIntentAction,Double> merged = discovery.mergeTargetMaps(mapAnew, mapBexisting);
		
		
			
	}
	
	/*
	public static void createCAUIModel() throws URISyntaxException, InterruptedException{

		LOG.info("createCAUIModel");

		HashMap<String,Serializable> context1 = new HashMap<String,Serializable>();
		context1.put(CtxAttributeTypes.LOCATION_SYMBOLIC, "home");
		context1.put(CtxAttributeTypes.STATUS, "free");

		HashMap<String,Serializable> context2 = new HashMap<String,Serializable>();
		context2.put(CtxAttributeTypes.LOCATION_SYMBOLIC, "office");
		context2.put(CtxAttributeTypes.STATUS, "free");


		UserIntentModelData modelData = .cauiTaskManager.createModel();

		IUserIntentAction userActionOn1 = TestCase2120.cauiTaskManager.createAction(serviceSri ,SERVICE_TYPE,"radio","on");
		userActionOn1.setActionContext(context1);

		IUserIntentAction userActionSetVol = TestCase2120.cauiTaskManager.createAction(serviceSri ,SERVICE_TYPE,"SetVolume","medium");
		userActionSetVol.setActionContext(context1);

		IUserIntentAction userActionSetChannel = TestCase2120.cauiTaskManager.createAction(serviceSri ,SERVICE_TYPE,"SetChannel","radio1");
		userActionSetChannel.setActionContext(context1);

		IUserIntentAction userActionOff1 = TestCase2120.cauiTaskManager.createAction(serviceSri ,SERVICE_TYPE,"radio","off");
		userActionOff1.setActionContext(context1);



		//On --> setVol 0.5
		TestCase2120.cauiTaskManager.setActionLink(userActionOn1, userActionSetVol, 0.3d);	
		TestCase2120.cauiTaskManager.setActionLink(userActionOn1, userActionSetChannel, 0.7d);
		TestCase2120.cauiTaskManager.setActionLink(userActionSetVol, userActionOff1, 1.0d);	
		TestCase2120.cauiTaskManager.setActionLink(userActionSetChannel, userActionOff1, 1.0d);	

		modelData  = TestCase2120.cauiTaskManager.retrieveModel();
		storeModelCtxDB(modelData,CtxAttributeTypes.CAUI_MODEL);

		LOG.info("CAUI modelData ::"+modelData.getActionModel());
		Thread.sleep(5000);

	}
	*/
}