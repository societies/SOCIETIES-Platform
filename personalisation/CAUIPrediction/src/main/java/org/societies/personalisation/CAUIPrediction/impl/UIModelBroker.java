package org.societies.personalisation.CAUIPrediction.impl;

import java.util.ArrayList;
import java.util.List;

import org.societies.api.identity.IIdentity;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.personalisation.CAUI.api.CAUITaskManager.ICAUITaskManager;
import org.societies.personalisation.CAUI.api.model.IUserIntentAction;
import org.societies.personalisation.CAUI.api.model.IUserIntentTask;
import org.societies.personalisation.CAUI.api.model.UserIntentModelData;

public class UIModelBroker {
	
	
	private ICtxBroker ctxBroker;
	private ICAUITaskManager modelManager;
	
	
	UIModelBroker(ICtxBroker ctxBroker, ICAUITaskManager cauiTaskManager){
		this.ctxBroker = ctxBroker; 
		this.modelManager = cauiTaskManager;
	}

	public void setActiveModel(IIdentity requestor){
		// retrieve model from Context DB
		// set model as active in CauiTaskManager
		// until then create and use a fake model
		
	}

	

}
