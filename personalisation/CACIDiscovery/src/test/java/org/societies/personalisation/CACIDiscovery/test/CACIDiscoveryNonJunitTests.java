package org.societies.personalisation.CACIDiscovery.test;

import java.util.ArrayList;
import java.util.List;

import org.societies.personalisation.CACIDiscovery.impl.CACIDiscovery;
import org.societies.personalisation.CACIDiscovery.impl.UIModelSimilarityEval;
import org.societies.personalisation.CAUI.api.model.UserIntentModelData;


public class CACIDiscoveryNonJunitTests {
	
	CACIDiscovery discovery = null;

	CACIDiscoveryNonJunitTests(){
	
		discovery = new CACIDiscovery();
	
	}
	
	public void testGenerateVariousUserModels() {

		UserIntentModelCreator modelCreator = new UserIntentModelCreator();

		List<UserIntentModelData> uiModelList = new ArrayList<UserIntentModelData>();

		UserHistoryA userA = new UserHistoryA();
		//System.out.println("userA.createContextHistoryAttributesSet() : "+ userA.createContextHistoryAttributesSet());
		UserIntentModelData modelA = modelCreator.createModel(userA.createContextHistoryAttributesSet());
		System.out.println("*********** user model A : "+ modelA.getActionModel());

		UserHistoryB userB = new UserHistoryB();
		UserIntentModelData modelB = modelCreator.createModel(userB.createContextHistoryAttributesSet());
		System.out.println("*********** user model B : "+ modelB.getActionModel());

		UserHistoryC userC = new UserHistoryC();
		UserIntentModelData modelC = modelCreator.createModel(userC.createContextHistoryAttributesSet());
		System.out.println("*********** user model C : "+ modelC.getActionModel());


		uiModelList.add(modelA);
		uiModelList.add(modelB);
		uiModelList.add(modelC);

		discovery.generateNewCommunityModel(uiModelList);
		
		
		//List<UserIntentModelData> translatedModelList = discovery.convertUserToCommModels(uiModelList);


	//	System.out.println("translated " );
	//	printModels(translatedModelList);
	
		/*
		UIModelSimilarityEval evalSim = new UIModelSimilarityEval();
		
		System.out.println("evaluateSimilarity " );
		System.out.println("uiModelList  " + uiModelList.get(0).getActionModel().keySet());
		System.out.println("translatedModelList  " + translatedModelList.get(0).getActionModel().keySet());
		
		Double out = evalSim.evaluateSimilarity(uiModelList.get(0),translatedModelList.get(0));
		
		
		System.out.println("evaluateSimilarity outcome: "+  out);
	*/
		
	}


	private void printModels(List<UserIntentModelData> modelList){

		for(UserIntentModelData uimodel : modelList){
			System.out.println(uimodel.getActionModel());
		}
	}
	
	
	public static void main(String[] args) {
		CACIDiscoveryNonJunitTests cdt = new CACIDiscoveryNonJunitTests();
		cdt.testGenerateVariousUserModels();
	}

	
}
