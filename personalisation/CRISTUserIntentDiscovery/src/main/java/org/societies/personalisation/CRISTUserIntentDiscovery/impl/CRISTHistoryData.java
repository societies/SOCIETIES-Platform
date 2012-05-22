package org.societies.personalisation.CRISTUserIntentDiscovery.impl;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxHistoryAttribute;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.personalisation.model.Action;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;


/**
 * This class is a local data structure which can store to and retrieve from Ctx DB
 * 
 * @author Zhiyong Yu
 * 
 */
public class CRISTHistoryData {
	
	private static final Logger LOG = LoggerFactory.getLogger(CRISTHistoryData.class);
	
	public static ServiceResourceIdentifier serviceId_music;
	public static ServiceResourceIdentifier serviceId_checkin;
	
	private ArrayList<String> context = new ArrayList<String>(); //context = ["100","30","22","N/A"]
	private IAction action = null;
	private String situationValue = null;
	

	public CRISTHistoryData(IAction action, String situation, ArrayList<String> context) {
			
		this.action = action;
		this.situationValue = situation;
		this.context = context;
		LOG.info("New CRISTHistoryData: " + this.toString());
	}

	public ArrayList<String> getContext() {
		return context;
	}

	public void setContext(ArrayList<String> context) {
		this.context = context;
	}
	
	public String getActionValue() {
		return action.getparameterName()+ " " + action.getvalue();
	}

	public IAction getAction() {
		return action;
	}

	public void setActionValue(IAction action) {
		this.action = action;
	}

	public String getSituationValue() {
		return situationValue;
	}
	
	public void setSituationValue(String situationValue) {
		this.situationValue = situationValue;
	}
	
	public String toString() {
		String string = getActionValue() + " " + this.situationValue + " " + this.context;
		return string;
	}
	
	public static ServiceResourceIdentifier getServiceId_music()
	{
		if (serviceId_music != null)
		{
			return serviceId_music;
		}
		serviceId_music = new ServiceResourceIdentifier();
		try {
			serviceId_music.setIdentifier(new URI("http://testService_music"));
			return serviceId_music;
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static ServiceResourceIdentifier getServiceId_checkin()
	{
		if (serviceId_checkin != null)
		{
			return serviceId_checkin;
		}
		serviceId_checkin = new ServiceResourceIdentifier();
		try {
			serviceId_checkin.setIdentifier(new URI("http://testService_checkin"));
			return serviceId_checkin;
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static ArrayList<String> getRegisteredContext()
	{
		ArrayList<String> registeredContext = new ArrayList<String>();
		registeredContext.add("Light");
		registeredContext.add("Sound");
		registeredContext.add("Temperature");
		registeredContext.add("GPS");
		return registeredContext;

	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
