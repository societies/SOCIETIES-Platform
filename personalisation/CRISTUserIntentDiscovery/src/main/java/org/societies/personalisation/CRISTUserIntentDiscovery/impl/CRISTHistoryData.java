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
import org.societies.personalisation.CRIST.api.model.CRISTUserAction;
import org.societies.personalisation.CRIST.api.model.CRISTUserSituation;


/**
 * This class is a local data structure which can store to and retrieve from Ctx DB
 * 
 * @author Zhiyong Yu
 * 
 */
public class CRISTHistoryData {
	
	private static final Logger LOG = LoggerFactory.getLogger(CRISTHistoryData.class);
	
	private IAction action = null;
	private CRISTUserSituation situation = null;
	

	public CRISTHistoryData(IAction action, CRISTUserSituation situation) {
			
		this.action = action;
		this.situation = situation;
		LOG.info("New CRISTHistoryData: " + ((CRISTUserAction) this.action).getActionID() + ", " + this.situation.getSituationID());
	}

	public IAction getAction() {
		return action;
	}

	public void setAction(IAction action) {
		this.action = action;
	}

	public CRISTUserSituation getSituation() {
		return situation;
	}
	
	public void setSituation(CRISTUserSituation situation) {
		this.situation = situation;
	}
	
	public String toString() {
		return ((CRISTUserAction) this.action).getActionID() + ", " + this.situation.getSituationID();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
