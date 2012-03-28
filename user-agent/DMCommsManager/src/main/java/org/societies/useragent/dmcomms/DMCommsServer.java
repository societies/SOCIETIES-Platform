package org.societies.useragent.dmcomms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.interfaces.IFeatureServer;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.internal.personalisation.model.IOutcome;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.schema.useragent.decisionmaking.DecisionMakingBean;
import org.societies.useragent.decisionmaking.DecisionMaker;

public class DMCommsServer implements IFeatureServer {

	private static final List<String> NAMESPACES = Collections
			.unmodifiableList(Arrays
					.asList("http://societies.org/api/schema/useragent/decisionmaking"));
	private static final List<String> PACKAGES = Collections
			.unmodifiableList(Arrays
					.asList("org.societies.api.schema.useragent.decisionmaking"));

	// PRIVATE VARIABLES
	private ICommManager commManager;
	private DecisionMaker dmaker;
	private IIdentityManager idManager;

	// PROPERTIES
	public ICommManager getCommManager() {
		return commManager;
	}

	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
	}

	public DecisionMaker getDmaker() {
		return dmaker;
	}

	public void setDmaker(DecisionMaker dmaker) {
		this.dmaker = dmaker;
	}

	// METHODS
	public DMCommsServer() {
	}

	public void InitService() {
		// REGISTER OUR CommsManager WITH THE XMPP Communication Manager
		try {
			getCommManager().register(this);
		} catch (CommunicationException e) {
			e.printStackTrace();
		}
		idManager = commManager.getIdManager();
	}

	public List<String> getJavaPackages() {
		return PACKAGES;
	}

	public List<String> getXMLNamespaces() {
		return NAMESPACES;
	}

	public void receiveMessage(Stanza stanza, Object payload) {
		// CHECK WHICH END BUNDLE TO BE CALLED THAT I MANAGE
		if (payload instanceof DecisionMakingBean) {
			this.receiveMessage(stanza, (DecisionMakingBean) payload);
		}
	}

	public void receiveMessage(Stanza stanza, DecisionMakingBean payload) {
		// ---- UAM Bundle ---
		DecisionMakingBean dmakingBean = (DecisionMakingBean) payload;
		try {
			// IIdentity owner = idManager.fromJid(monitorBean.getIdentity());
			// ServiceResourceIdentifier serviceId = monitorBean
			// .getServiceResourceIdentifier();
			// String serviceType = monitorBean.getServiceType();
			// String parameterName = monitorBean.getParameterName();
			// String value = monitorBean.getValue();
			// IAction action = new Action(serviceId, serviceType,
			// parameterName, value);
			// uam.monitor(owner, action);
			// break;
			List<IOutcome> intents = new ArrayList<IOutcome>();
			List<IOutcome> preferences = new ArrayList<IOutcome>();
			List<ServiceResourceIdentifier> intentServiceIds = dmakingBean
					.getIntentServiceIds();
			List<String> intentServiceTypes = dmakingBean
					.getIntentServiceTypes();
			List<String> intentParameterNames = dmakingBean
					.getIntentParameterNames();
			List<ServiceResourceIdentifier> preferenceServiceIds = dmakingBean
					.getPreferenceServiceIds();
			List<String> preferenceServiceTypes = dmakingBean
					.getPreferenceServiceTypes();
			List<String> preferenceParameterNames = dmakingBean
					.getPreferenceParameterNames();
			List<Integer> intentConfidenceLevels=dmakingBean
					.getIntentConfidenceLevel();
			List<Integer> preferenceConfidenceLevels=dmakingBean
					.getPreferenceConfidenceLevel();
			List<String> intentValues = dmakingBean
					.getIntentValues();
			List<String> preferenceValues = dmakingBean
					.getPreferenceValues();
			int intentSize=dmakingBean.getIntentSize();
			int preferenceSize=dmakingBean.getPreferenceSize();
			for(int i=0;i<intentSize;i++){
				IOutcome intent=new IOutcomeDO(
						intentServiceIds.get(i),
						intentServiceTypes.get(i),
						intentParameterNames.get(i),
						intentValues.get(i),
						intentConfidenceLevels.get(i));
				intents.add(intent);
			}
			for(int i=0;i<preferenceSize;i++){
				IOutcome preference=new IOutcomeDO(
						preferenceServiceIds.get(i),
						preferenceServiceTypes.get(i),
						preferenceParameterNames.get(i),
						preferenceValues.get(i),
						preferenceConfidenceLevels.get(i));
				preferences.add(preference);
			}
			this.dmaker.makeDecision(intents, preferences);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Object getQuery(Stanza arg0, Object arg1) throws XMPPError {
		// PUT FUNCTIONALITY HERE FOR IF THERE IS A RETURN TYPE
		return null;
	}

	public Object setQuery(Stanza arg0, Object arg1) throws XMPPError {
		return null;

	}

}
