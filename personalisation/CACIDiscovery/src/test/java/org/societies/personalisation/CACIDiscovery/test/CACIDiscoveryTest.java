package org.societies.personalisation.CACIDiscovery.test;


import static org.junit.Assert.*;

import static org.mockito.Mockito.*;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
		

	private static final String SERVICE_SRI1 = "css://requestor1.societies.org/HelloWorld";
	private static final String SERVICE_TYPE = "radio_service";
	private static ServiceResourceIdentifier serviceSri1;

	private static final String SERVICE_SRI2 = "css://requestor2.societies.org/HelloWorld";
	private static final String SERVICE_TYPE2 = "nav_service";
	private static ServiceResourceIdentifier serviceSri2;
	
	
	
	static UserIntentModelData modelA =null;
	static UserIntentModelData modelB =null;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		serviceSri1 = new ServiceResourceIdentifier();
		serviceSri1.setServiceInstanceIdentifier(SERVICE_SRI1);
		serviceSri1.setIdentifier(new URI(SERVICE_SRI1));
		
		serviceSri2 = new ServiceResourceIdentifier();
		serviceSri2.setServiceInstanceIdentifier(SERVICE_SRI2);
		serviceSri2.setIdentifier(new URI(SERVICE_SRI2));
		
		
		
		discovery = new CACIDiscovery();
		cauiTaskManager = discovery.getCauiTaskManager();
		
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		modelA = createCAUIModelA();
		modelB =  createCAUIModelB();
		
	
	
	}

	@After
	public void tearDown() throws Exception {
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

		IUserIntentAction userActionOn1 = cauiTaskManager.createAction(serviceSri1 ,SERVICE_TYPE,"radio","on");
		userActionOn1.setActionContext(context1);

		IUserIntentAction userActionSetVol = cauiTaskManager.createAction(serviceSri1 ,SERVICE_TYPE,"SetVolume","medium");
		userActionSetVol.setActionContext(context1);

		IUserIntentAction userActionSetChannel = cauiTaskManager.createAction(serviceSri1 ,SERVICE_TYPE,"SetChannel","radio1");
		userActionSetChannel.setActionContext(context1);

		IUserIntentAction userActionOff1 = cauiTaskManager.createAction(serviceSri1 ,SERVICE_TYPE,"radio","off");
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


		HashMap<String,Serializable> context1 = new HashMap<String,Serializable>();
		context1.put(CtxAttributeTypes.LOCATION_SYMBOLIC, "office");
		context1.put(CtxAttributeTypes.HOUR_OF_DAY, 2);
		context1.put(CtxAttributeTypes.DAY_OF_WEEK, "tuesday");

		HashMap<String,Serializable> context2 = new HashMap<String,Serializable>();
		context2.put(CtxAttributeTypes.LOCATION_SYMBOLIC, "home");
		context2.put(CtxAttributeTypes.HOUR_OF_DAY, 1);
		context2.put(CtxAttributeTypes.DAY_OF_WEEK, "monday");
		
		UserIntentModelData modelData = cauiTaskManager.createModel();

		IUserIntentAction userActionOn1 = cauiTaskManager.createAction(serviceSri1 ,SERVICE_TYPE,"radio","on");
		userActionOn1.setActionContext(context1);

		//IUserIntentAction userActionOff1 = cauiTaskManager.createAction(serviceSri ,SERVICE_TYPE,"radio","off");
		IUserIntentAction userActionSetMedium = cauiTaskManager.createAction(serviceSri1 ,SERVICE_TYPE,"SetVolume","medium");
		userActionSetMedium.setActionContext(context1);

		//On --> setVol 0.5
		cauiTaskManager.setActionLink(userActionOn1, userActionSetMedium, 1.0d);	

		modelData  = cauiTaskManager.retrieveModel();
		System.out.println("B CAUI modelData ::"+modelData.getActionModel());
		//printModel(modelData);

		return modelData;
	}
	
	@Ignore	
	@Test
	public void testMergeTargetMaps () {
		
		// {(a,0.4),(b,0.6)} + {(c,0.4),(b,0.6)} = {( b,0.6),(c,0.2),(a,0.2)}
			
		IUserIntentAction userActionA = cauiTaskManager.createAction(serviceSri1 ,SERVICE_TYPE,"A","A");
		IUserIntentAction userActionB = cauiTaskManager.createAction(serviceSri1 ,SERVICE_TYPE,"B","B");
		IUserIntentAction userActionC1 = cauiTaskManager.createAction(serviceSri1 ,SERVICE_TYPE,"C","C");
		
		HashMap<IUserIntentAction,Double> targetMapA = new HashMap<IUserIntentAction,Double>();
		targetMapA.put(userActionA, 0.4);
		targetMapA.put(userActionB, 0.4);
		targetMapA.put(userActionC1, 0.2);
		System.out.println("targetMapA "+targetMapA);
		
		
		HashMap<IUserIntentAction,Double> targetMapB = new HashMap<IUserIntentAction,Double>();
		IUserIntentAction userActionC2 = cauiTaskManager.createAction(serviceSri1 ,SERVICE_TYPE,"C","C");
		IUserIntentAction userActionB2 = cauiTaskManager.createAction(serviceSri1 ,SERVICE_TYPE2,"B","B");
		IUserIntentAction userActionD = cauiTaskManager.createAction(serviceSri1 ,SERVICE_TYPE,"D","D");
		targetMapB.put(userActionC2, 0.2);
		targetMapB.put(userActionB2, 0.8);
		targetMapA.put(userActionD, 0.2);
		System.out.println("targetMapB "+targetMapB);
			
		HashMap<IUserIntentAction,Double>  out = discovery.mergeTargetMaps(targetMapA, targetMapB);
		System.out.println("out: "+out);
		
		assertEquals(0.2, out.get(userActionA));
		assertEquals(0.1, out.get(userActionD));
		assertEquals(0.2, out.get(userActionC2));
		assertEquals(0.2, out.get(userActionC1));
		// {css://requestor1.societies.org/HelloWorld#D=D/11=0.1, 
		// css://requestor1.societies.org/HelloWorld#C=C/8=0.2, 
		// css://requestor1.societies.org/HelloWorld#B=B/7=0.6000000000000001, 
		// css://requestor1.societies.org/HelloWorld#A=A/6=0.2}
	}
	
	@Ignore
	@Test
	public void testAreSimilarActions() {
		
		System.out.println(" testareSimilarActions" );
		IUserIntentAction userActionOn1 = cauiTaskManager.createAction(serviceSri1 ,SERVICE_TYPE,"radio","on");
		IUserIntentAction userActionOn2 = cauiTaskManager.createAction(serviceSri2 ,SERVICE_TYPE,"radio","on");
		IUserIntentAction userActionOn3 = cauiTaskManager.createAction(serviceSri2 ,SERVICE_TYPE,"radio","off");
		IUserIntentAction userActionOn4 = cauiTaskManager.createAction(serviceSri2 ,SERVICE_TYPE2,"radio","on");
		
		System.out.println(" testareSimilarActions :  "+discovery.areSimilarActions(userActionOn1, userActionOn2));
		assertEquals(true,discovery.areSimilarActions(userActionOn1, userActionOn2) );
		assertEquals(false,discovery.areSimilarActions(userActionOn1, userActionOn3) );
		assertEquals(false,discovery.areSimilarActions(userActionOn1, userActionOn4) );
	}
	
	@Ignore
	@Test
	public void testCreateTranslationMap() {
		
		System.out.println("--- testCreateTranslationMap ---");
		
		List<UserIntentModelData> uiModelList = new ArrayList<UserIntentModelData>();
		uiModelList.add(modelA);
		uiModelList.add(modelB);
	
		//System.out.println("modelA"+ modelA.getActionModel().keySet());
		//System.out.println("modelB"+ modelB.getActionModel().keySet());
		
		Map<IUserIntentAction, List<IUserIntentAction>> transMap = discovery.createTranslationMap(uiModelList);
		assertEquals(4 , transMap.keySet().size());
		printTranlationMap(transMap);
		System.out.println("--- end of  testCreateTranslationMap --- ");
	}
	@Ignore
	@Test
	public void testConvertUserToCommModels() {
		
		System.out.println("--- testConvertUserToCommModels ---");
		
		List<UserIntentModelData> uiModelList = new ArrayList<UserIntentModelData>();
		uiModelList.add(modelA);
		uiModelList.add(modelB);
		System.out.println("modelA");
		printModel(modelA);
		System.out.println("modelB");
		printModel(modelB);
		
		Map<IUserIntentAction, List<IUserIntentAction>> transMap = discovery.createTranslationMap(uiModelList);
		printTranlationMap(transMap);
		
		List<UserIntentModelData> convertedUIModelList =  discovery.convertUserToCommModels(uiModelList, transMap);
		
		UserIntentModelData conModelA = convertedUIModelList.get(0);
		UserIntentModelData conModelB = convertedUIModelList.get(1);
		
		
		System.out.println("conModelA "+conModelA.getActionModel());
		printModel(conModelA);
		assertEquals(4, conModelA.getActionModel().size());
		
		System.out.println("conModelB "+conModelB.getActionModel());
		printModel(conModelB);
		assertEquals(2, conModelB.getActionModel().size());
		System.out.println("--- end of testConvertUserToCommModels ---");
	}
	
	
	
	void printTranlationMap(Map<IUserIntentAction, List<IUserIntentAction>> tranlationMap){
		
		
		System.out.println("printing Tranlation Map");
		for(IUserIntentAction action : tranlationMap.keySet()){
			System.out.println("com act: "+action +" /user act->"+tranlationMap.get(action));
		}	
	}
	
	
	
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
	
	private static void printModel(UserIntentModelData model){

		HashMap<IUserIntentAction, HashMap<IUserIntentAction, Double>> allActions = new HashMap<IUserIntentAction, HashMap<IUserIntentAction, Double>>();
		allActions = model.getActionModel();
		System.out.println("------------printModel start ---------------");
		for(IUserIntentAction action : allActions.keySet()){
			System.out.println("source:"+action.getparameterName()+"/"+action.getvalue() /*+" ctx:"+ action.getActionContext()*/);

			if(allActions.get(action)!=null){
				
			
			HashMap<IUserIntentAction, Double> targetActions = allActions.get(action);
			for(IUserIntentAction actionTarget : targetActions.keySet()){
				System.out.println("--> target:"+actionTarget.getparameterName()+"/"+actionTarget.getvalue() /*+" ctx:"+ actionTarget.getActionContext()*/);	
			}
			}
		}
		System.out.println("------------printModel end ---------------");
	}
}