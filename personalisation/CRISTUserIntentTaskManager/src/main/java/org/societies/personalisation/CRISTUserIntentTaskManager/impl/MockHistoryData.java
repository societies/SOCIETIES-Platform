package org.societies.personalisation.CRISTUserIntentTaskManager.impl;

import java.util.ArrayList;

import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;


/**
 * This class is responsible to mock a list of history data and store in CtxDB. from time to time?
 * 
 * @author Zhiyong Yu
 * 
 */
public class MockHistoryData {

	
	ArrayList<String> context; //context = ["100","30","22","N/A"]
	String actionValue;
	String situationValue;

	public MockHistoryData(String action, String situation, ArrayList<String> context) {
		if (action == null) {
			this.actionValue = "";
		} else {
			this.actionValue = action;
		}
		
		if (situation == null) {
			this.situationValue = "";
		} else {
			this.situationValue = situation;
		}
		
		if (context == null) {
			this.context = new ArrayList<String>();
		} else {
			this.context = context;
		}
	}

	public ArrayList<String> getContext() {
		return context;
	}

	public void setContext(ArrayList<String> context) {
		this.context = context;
	}

	public String getActionValue() {
		return actionValue;
	}

	public void setActionValue(String actionValue) {
		this.actionValue = actionValue;
	}

	public String getSituationValue() {
		return situationValue;
	}
	
	public void setSituationValue(String situationValue) {
		this.situationValue = situationValue;
	}
	
	public String toString() {
		String string = this.actionValue + " " + this.situationValue + " " + this.context;
		return string;
	}
	
	public static ArrayList<CtxAttributeIdentifier> initializeHistory(ArrayList<String> registeredContext)
	{
		ArrayList<CtxAttributeIdentifier> ctxAttributeIdentifierList = new ArrayList<CtxAttributeIdentifier>();

		ArrayList<MockHistoryData> historyList = new ArrayList<MockHistoryData>();
		
		CRISTCtxBrokerContact ctxBrokerContact = new CRISTCtxBrokerContact();
		
		ArrayList<String> historyAction = new ArrayList<String>();
		ArrayList<String> historySituation = new ArrayList<String>();
		ArrayList<ArrayList<String>> historyContext = new ArrayList<ArrayList<String>>();
		// Assuming that the following sensors are available: light, sound,
		// temperature, gps


		ArrayList<String> historyContextClique = new ArrayList<String>();
		historyAction.add("Turn on MP3 Player");
		historySituation.add("Study Hall");
		historyContextClique.add("100");
		historyContextClique.add("30");
		historyContextClique.add("22");
		historyContextClique.add("N/A");
		historyContext.add(historyContextClique);
		
		historyContextClique = new ArrayList<String>();
		historyAction.add("Turn down volume");
		historySituation.add("Study Hall");
		historyContextClique.add("100");
		historyContextClique.add("30");
		historyContextClique.add("22");
		historyContextClique.add("N/A");
		historyContext.add(historyContextClique);
		
		historyContextClique = new ArrayList<String>();
		historyAction.add("Switch songs");
		historySituation.add("Study Hall");
		historyContextClique.add("100");
		historyContextClique.add("30");
		historyContextClique.add("22");
		historyContextClique.add("N/A");
		historyContext.add(historyContextClique);
		
		historyContextClique = new ArrayList<String>();
		historyAction.add("Turn off MP3 Player");
		historySituation.add("Study Hall");
		historyContextClique.add("100");
		historyContextClique.add("30");
		historyContextClique.add("22");
		historyContextClique.add("N/A");
		historyContext.add(historyContextClique);
		
		historyContextClique = new ArrayList<String>();
		historyAction.add("Start the GPS navigator");
		historySituation.add("Outdoor");
		historyContextClique.add("120");
		historyContextClique.add("60");
		historyContextClique.add("15");
		historyContextClique.add("55,1.33");
		historyContext.add(historyContextClique);
		
		historyContextClique = new ArrayList<String>();
		historyAction.add("Input a location name");
		historySituation.add("Outdoor");
		historyContextClique.add("120");
		historyContextClique.add("60");
		historyContextClique.add("15");
		historyContextClique.add("55,1.33");
		historyContext.add(historyContextClique);
		
		historyContextClique = new ArrayList<String>();
		historyAction.add("Close the GPS navigator");
		historySituation.add("Shopping Mall");
		historyContextClique.add("100");
		historyContextClique.add("80");
		historyContextClique.add("22");
		historyContextClique.add("N/A");
		historyContext.add(historyContextClique);
		
		historyContextClique = new ArrayList<String>();
		historyAction.add("Turn on MP3 Player");
		historySituation.add("Outdoor");
		historyContextClique.add("120");
		historyContextClique.add("60");
		historyContextClique.add("15");
		historyContextClique.add("55,1.33");
		historyContext.add(historyContextClique);
		
		historyContextClique = new ArrayList<String>();
		historyAction.add("Turn up volume");
		historySituation.add("Outdoor");
		historyContextClique.add("120");
		historyContextClique.add("60");
		historyContextClique.add("15");
		historyContextClique.add("55,1.33");
		historyContext.add(historyContextClique);
		
		historyContextClique = new ArrayList<String>();
		historyAction.add("Switch songs");
		historySituation.add("Outdoor");
		historyContextClique.add("120");
		historyContextClique.add("60");
		historyContextClique.add("15");
		historyContextClique.add("55,1.33");
		historyContext.add(historyContextClique);
		
		historyContextClique = new ArrayList<String>();
		historyAction.add("Turn off MP3 Player");
		historySituation.add("Office");
		historyContextClique.add("100");
		historyContextClique.add("30");
		historyContextClique.add("26");
		historyContextClique.add("N/A");
		historyContext.add(historyContextClique);
		
		historyContextClique = new ArrayList<String>();
		historyAction.add("Start the GPS navigator");
		historySituation.add("Outdoor");
		historyContextClique.add("120");
		historyContextClique.add("60");
		historyContextClique.add("15");
		historyContextClique.add("55,1.33");
		historyContext.add(historyContextClique);
		
		historyContextClique = new ArrayList<String>();
		historyAction.add("Input a location name");
		historySituation.add("Outdoor");
		historyContextClique.add("120");
		historyContextClique.add("60");
		historyContextClique.add("15");
		historyContextClique.add("55,1.33");
		historyContext.add(historyContextClique);
		
		historyContextClique = new ArrayList<String>();
		historyAction.add("Close the GPS navigator");
		historySituation.add("Shopping Mall");
		historyContextClique.add("100");
		historyContextClique.add("80");
		historyContextClique.add("22");
		historyContextClique.add("N/A");
		historyContext.add(historyContextClique);
		
		historyContextClique = new ArrayList<String>();
		historyAction.add("Turn on MP3 Player");
		historySituation.add("Study Hall");
		historyContextClique.add("100");
		historyContextClique.add("30");
		historyContextClique.add("22");
		historyContextClique.add("N/A");
		historyContext.add(historyContextClique);
		
		historyContextClique = new ArrayList<String>();
		historyAction.add("Turn down volume");
		historySituation.add("Study Hall");
		historyContextClique.add("100");
		historyContextClique.add("30");
		historyContextClique.add("22");
		historyContextClique.add("N/A");
		historyContext.add(historyContextClique);
		
		historyContextClique = new ArrayList<String>();
		historyAction.add("Switch songs");
		historySituation.add("Study Hall");
		historyContextClique.add("100");
		historyContextClique.add("30");
		historyContextClique.add("22");
		historyContextClique.add("N/A");
		historyContext.add(historyContextClique);
		
		historyContextClique = new ArrayList<String>();
		historyAction.add("Turn off MP3 Player");
		historySituation.add("Study Hall");
		historyContextClique.add("100");
		historyContextClique.add("30");
		historyContextClique.add("22");
		historyContextClique.add("N/A");
		historyContext.add(historyContextClique);
		
		historyContextClique = new ArrayList<String>();
		historyAction.add("Turn on MP3 Player");
		historySituation.add("Study Hall");
		historyContextClique.add("100");
		historyContextClique.add("30");
		historyContextClique.add("22");
		historyContextClique.add("N/A");
		historyContext.add(historyContextClique);
		
		historyContextClique = new ArrayList<String>();
		historyAction.add("Turn down volume");
		historySituation.add("Study Hall");
		historyContextClique.add("100");
		historyContextClique.add("30");
		historyContextClique.add("22");
		historyContextClique.add("N/A");
		historyContext.add(historyContextClique);


		for (int i = 0; i < historyAction.size(); i++) {
			MockHistoryData currentHisData = new MockHistoryData(
					historyAction.get(i), historySituation.get(i),
					historyContext.get(i));
			historyList.add(currentHisData);
			ctxAttributeIdentifierList.add(ctxBrokerContact.createContext("Action", "ActionName", historyAction.get(i)));
			ctxAttributeIdentifierList.add(ctxBrokerContact.createContext("Situation", "SituationName", historySituation.get(i)));
			for (int j = 0; j < historyContext.get(i).size(); j++) 
			{
				ctxAttributeIdentifierList.add(ctxBrokerContact.createContext("UserContext", registeredContext.get(j), historyContext.get(i).get(j)));
			}
			
		}
		
		return ctxAttributeIdentifierList;
		
	}
	
	public static ArrayList<MockHistoryData> retrieveHistoryData(ArrayList<CtxAttributeIdentifier> ctxAttributeIdentifierList)
	{
		ArrayList<MockHistoryData> historyList = new ArrayList<MockHistoryData>();
		
		CRISTCtxBrokerContact ctxBrokerContact = new CRISTCtxBrokerContact();

		for (int i = 0; i < ctxAttributeIdentifierList.size(); i++)
		{
			String actionName = null;
			String situationName = null;
			ArrayList<String> contexts = new ArrayList<String>();

			for (int j = 0; j < 6; j++)//fix construct
			{
				CtxAttributeIdentifier ctxAttributeId = ctxAttributeIdentifierList.get(i+j);
				//CtxAttribute retrievedCtxAttribute = ctxBrokerContact.retrieveContextAttr(ctxAttributeId);
				//String ctxAttrType = retrievedCtxAttribute.getType();
				if (j == 0)// && ctxAttrType.equals("ActionName"))
					actionName = ctxBrokerContact.retrieveContext(ctxAttributeId);
				else if (j == 1)// && ctxAttrType.equals("SituationName"))
					situationName = ctxBrokerContact.retrieveContext(ctxAttributeId);
				else 
					contexts.add(ctxBrokerContact.retrieveContext(ctxAttributeId));

			}

			MockHistoryData currentHisData = new MockHistoryData(
					actionName, situationName, contexts);
			historyList.add(currentHisData);
			i = i+5;
			
		}
		return historyList;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
