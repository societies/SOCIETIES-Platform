package org.societies.useragent.dmcomms;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.personalisation.model.IOutcome;
import org.societies.api.internal.useragent.decisionmaking.IDecisionMaker;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.schema.useragent.decisionmaking.DecisionMakingBean;

public class DMCommsClient implements IDecisionMaker, ICommCallback{	
	private static final List<String> NAMESPACES = Collections.unmodifiableList(
			Arrays.asList("http://societies.org/api/schema/useragent/decisionmaking"));
	private static final List<String> PACKAGES = Collections.unmodifiableList(
			Arrays.asList("org.societies.api.schema.useragent.decisionmaking"));

	//PRIVATE VARIABLES
	private ICommManager commManager;
	private IIdentityManager idManager;

	//PROPERTIES
	public ICommManager getCommManager() {
		return commManager;
	}

	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
	}

	public DMCommsClient() {	
	}

	public void InitService() {
		//REGISTER OUR ServiceManager WITH THE XMPP Communication Manager
		try {
			getCommManager().register(this); 
		} catch (CommunicationException e) {
			e.printStackTrace();
		}
		idManager = commManager.getIdManager();
	}

	public static DecisionMakingBean packageBean(List<IOutcome> intents,List<IOutcome> preferences){
		DecisionMakingBean result=new DecisionMakingBean();
		result.setIntentSize(intents.size());
		result.setPreferenceSize(preferences.size());
		result.getIntentServiceIds();
		result.getIntentParameterNames();
		result.getIntentServiceTypes();
		result.getPreferenceServiceIds();
		result.getPreferenceParameterNames();
		result.getPreferenceServiceTypes();
		for(int i=0;i<result.getIntentSize();i++){
			result.getIntentServiceIds().add(intents.get(i).getServiceID());
			result.getIntentServiceTypes().add(intents.get(i).getServiceType());
			result.getIntentParameterNames().add(intents.get(i).getparameterName());
			result.getIntentConfidenceLevel().add(intents.get(i).getConfidenceLevel());
		}
		for(int i=0;i<result.getPreferenceSize();i++){
			result.getPreferenceServiceIds().add(preferences.get(i).getServiceID());
			result.getPreferenceServiceTypes().add(preferences.get(i).getServiceType());
			result.getPreferenceParameterNames().add(preferences.get(i).getparameterName());
			result.getPreferenceConfidenceLevel().add(preferences.get(i).getConfidenceLevel());
		}
		return result;
	}
	
	//asynchronous call
	@Override
	public void makeDecision(List<IOutcome> intents, 
				List<IOutcome> preferences) {
		IIdentity toIdentity = null;
		try {
			toIdentity = idManager.fromJid("XCManager.societies.local");
		} catch (InvalidFormatException e1) {
			e1.printStackTrace();
		}
		Stanza stanza = new Stanza(toIdentity);

		//CREATE MESSAGE BEAN
		DecisionMakingBean dmBean = packageBean(intents,preferences);
	//	dmBean.setMethod(DecisionMakingBean.methodType.makeDecision);
		try {
			//SEND INFORMATION QUERY - RESPONSE WILL BE IN "callback.RecieveMessage()"
			commManager.sendMessage(stanza, dmBean);
		} catch (CommunicationException e) {
			e.printStackTrace();
		};
	}
	
	@Override
	public List<String> getJavaPackages() {
		return PACKAGES;
	}

	@Override
	public List<String> getXMLNamespaces() {
		return NAMESPACES;
	}

	@Override
	public void receiveError(Stanza arg0, XMPPError arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receiveInfo(Stanza arg0, String arg1, XMPPInfo arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receiveItems(Stanza arg0, String arg1, List<String> arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receiveMessage(Stanza arg0, Object arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receiveResult(Stanza arg0, Object arg1) {
		// TODO Auto-generated method stub

	}

}
