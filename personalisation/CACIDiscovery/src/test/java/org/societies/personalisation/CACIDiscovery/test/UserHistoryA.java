package org.societies.personalisation.CACIDiscovery.test;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxHistoryAttribute;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.personalisation.model.Action;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;

//remove after testing
import org.societies.personalisation.CAUIDiscovery.impl.CAUIDiscovery;


public class UserHistoryA {

	private static Long nextValue = 0L;
	Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> mapHocData = new LinkedHashMap<CtxHistoryAttribute, List<CtxHistoryAttribute>>();
	private CtxEntity operator = null;
	
	CAUIDiscovery discover = null;
	
	
	UserHistoryA(){
		
		System.out.println("creating user intent model for user A");
		
		operator = createOperator();
		discover = new  CAUIDiscovery();
	}
		
	public Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> createContextHistoryAttributesSet(){

		//create actions
		//IIdentity identity = new MockIdentity(IdentityType.CSS, "user", "societies.org");
		ServiceResourceIdentifier serviceId1 = new ServiceResourceIdentifier();
		ServiceResourceIdentifier serviceId2 = new ServiceResourceIdentifier();
		try {
			serviceId1.setIdentifier(new URI("http://testService1"));
			serviceId2.setIdentifier(new URI("http://testService2"));
			serviceId1.setServiceInstanceIdentifier("http://testService1");
			serviceId2.setServiceInstanceIdentifier("http://testService2");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		//create actions
		IAction action1 = new Action(serviceId1, "testService", "volume", "high");
		IAction action2 = new Action(serviceId2, "testService", "volume", "low");
		IAction action3 = new Action(serviceId1, "testService", "volume", "mute");
		IAction actionX = new Action(serviceId1, "testService", "XXXX", "XXXX");
		IAction action4 = new Action(serviceId2, "testService", "colour", "blue");
		IAction action5 = new Action(serviceId2, "testService", "colour", "green");
		IAction actionY = new Action(serviceId1, "testService", "YYYY", "YYYY");
		//System.out.println ("action service ID "+actionY.getServiceID().getServiceInstanceIdentifier());
		for (int i=0; i<4; i++){

			monitorAction(action1,"country","free",10);
			monitorAction(action2,"office","busy",15);
			monitorAction(action3,"park","away",25);
			
			monitorAction(actionX,"park","away",25);
			monitorAction(actionY,"park","away",25);
		
			monitorAction(action4,"park","away",25);
			monitorAction(action5,"park","away",25);
			
			monitorAction(actionY,"park","away",25);
			monitorAction(actionX,"park","away",25);

		}
	
	return this.mapHocData;
	}
	
	//******************************************************************
	// helper methods
	//******************************************************************
	
	private void monitorAction(IAction action, String location, String status, Integer temperature){

		CtxHistoryAttribute mockPrimaryHocActionAttrX = createMockHocActionAttr(action);
		List<CtxHistoryAttribute> escortingCtxDataX = new ArrayList<CtxHistoryAttribute>();
		CtxHistoryAttribute attrLocationX = createMockHocAttr(CtxAttributeTypes.LOCATION_SYMBOLIC,location);
		CtxHistoryAttribute attrStatusX = createMockHocAttr(CtxAttributeTypes.STATUS,status);
		CtxHistoryAttribute attrTemperatureX = createMockHocAttr(CtxAttributeTypes.TEMPERATURE,temperature);
		escortingCtxDataX.add(attrLocationX);
		escortingCtxDataX.add(attrStatusX);
		escortingCtxDataX.add(attrTemperatureX);
		this.mapHocData.put(mockPrimaryHocActionAttrX, escortingCtxDataX);

	}

	
	private CtxHistoryAttribute createMockHocAttr(String ctxAttrType, Serializable ctxAttrValue){

		CtxAttributeIdentifier ctxAttrID = new CtxAttributeIdentifier(operator.getId(),ctxAttrType.toString(),getNextValue());
		CtxAttribute ctxAttr = new CtxAttribute(ctxAttrID);

		if(ctxAttrValue instanceof String){
			ctxAttr.setStringValue(ctxAttrValue.toString());
		} else if (ctxAttrValue instanceof Double){
			ctxAttr.setDoubleValue((Double)ctxAttrValue);
		}else if (ctxAttrValue instanceof Integer){
			ctxAttr.setIntegerValue((Integer)ctxAttrValue);
		}
		CtxHistoryAttribute ctxHocAttr = new CtxHistoryAttribute(ctxAttr,getNextValue());
		return ctxHocAttr;
	}

	private CtxHistoryAttribute createMockHocActionAttr(IAction action){
		CtxHistoryAttribute ctxHocAttr = null;
		try {
			CtxAttributeIdentifier ctxAttrID = new CtxAttributeIdentifier(operator.getId(),CtxAttributeTypes.LAST_ACTION,getNextValue());
			CtxAttribute ctxAttr = new CtxAttribute(ctxAttrID);
			ctxAttr.setBinaryValue(SerialisationHelper.serialise(action));
			ctxHocAttr = new CtxHistoryAttribute(ctxAttr,getNextValue());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ctxHocAttr;
	}
	
	private CtxEntity createOperator(){
		CtxEntityIdentifier ctxEntId = new CtxEntityIdentifier("operatorID","person",getNextValue());
		CtxEntity ctxEntity = new CtxEntity(ctxEntId);
		setOperatorEntity(ctxEntity);
		return ctxEntity;
	}
	
	private void setOperatorEntity(CtxEntity entity){
		operator = entity;
	}
	
	public static Long getNextValue() {
		return nextValue++;
	}

}