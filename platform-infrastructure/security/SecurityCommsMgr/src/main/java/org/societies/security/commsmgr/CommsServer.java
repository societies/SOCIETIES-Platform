package org.societies.security.commsmgr;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.interfaces.IFeatureServer;
import org.societies.api.internal.security.policynegotiator.INegotiationProvider;
import org.societies.api.internal.security.policynegotiator.INegotiationRequester;
import org.societies.api.schema.security.policynegotiator.ProviderBean;
import org.societies.security.policynegotiator.NegotiationProvider;
import org.societies.security.policynegotiator.NegotiationRequester;

@Component
public class CommsServer implements IFeatureServer {

	private static Logger LOG = LoggerFactory.getLogger(CommsServer.class);

	private static final List<String> NAMESPACES = Collections.unmodifiableList(
			  Arrays.asList("http://societies.org/api/schema/security/policynegotiator"
					  ));
	private static final List<String> PACKAGES = Collections.unmodifiableList(
			  Arrays.asList("org.societies.api.schema.security.policynegotiator"
					  ));
	
	private ICommManager commManager;
	private INegotiationProvider negotiationProvider;
	private INegotiationRequester negotiationRequester;
	
	@Autowired
	public CommsServer(ICommManager commManager,
			INegotiationRequester negotiationRequester,
			INegotiationProvider negotiationProvider) {
		
		this.commManager = commManager;
		this.negotiationRequester = negotiationRequester;
		this.negotiationProvider = negotiationProvider;
		
		LOG.debug("CommsServer({})", commManager + ", " + negotiationRequester + ", " + negotiationProvider);
	}
	
	@PostConstruct
	public void init() {
		
		LOG.debug("init(): commManager = {}", commManager.toString());
		
		try {
			commManager.register(this);
			LOG.debug("init(): commManager registered");
		} catch (CommunicationException e) {
			LOG.error("init(): ", e);
		}
	}

	/* (non-Javadoc)
	 * @see org.societies.api.comm.xmpp.interfaces.IFeatureServer#getJavaPackages()
	 */
	@Override
	public List<String> getJavaPackages() {
		
		LOG.debug("getJavaPackages()");
		
		return PACKAGES;
	}

	/* (non-Javadoc)
	 * @see org.societies.api.comm.xmpp.interfaces.IFeatureServer#getQuery(
	 * org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.Object)
	 */
	@Override
	public Object getQuery(Stanza stanza, Object messageBean) throws XMPPError {

		LOG.debug("getQuery()");
		
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.api.comm.xmpp.interfaces.IFeatureServer#getXMLNamespaces()
	 */
	@Override
	public List<String> getXMLNamespaces() {
		
		LOG.debug("getXMLNamespaces()");
		
		return NAMESPACES;
	}

	/* (non-Javadoc)
	 * @see org.societies.api.comm.xmpp.interfaces.IFeatureServer#receiveMessage(
	 * org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.Object)
	 */
	@Override
	public void receiveMessage(Stanza stanza, Object messageBean) {

		LOG.debug("receiveMessage({}, {})", stanza, messageBean);
		
		if (messageBean.getClass().equals(NegotiationProvider.class)) {
			
			// Method parameters
			ProviderBean providerBean = (ProviderBean) messageBean;
			int sessionId = providerBean.getSessionId();
			String signedPolicyOption = providerBean.getSignedPolicyOption();
			boolean isModified = providerBean.isModified();
			
			LOG.debug("receiveMessage(): NegotiationProvider. Params: " + isModified + ", " +
					sessionId + ", " + signedPolicyOption);
			
			switch (providerBean.getMethod()) {
			case GET_POLICY_OPTIONS:
				negotiationProvider.getPolicyOptions();
				break;
			case ACCEPT_POLICY_AND_GET_SLA:
				negotiationProvider.acceptPolicyAndGetSla(sessionId, signedPolicyOption, isModified);
				break;
			case REJECT:
				negotiationProvider.reject(sessionId);
				break;
			}
		}
		else if (messageBean.getClass().equals(NegotiationRequester.class)) {
			LOG.debug("receiveMessage(): NegotiationRequester");
		}
	}

	/* (non-Javadoc)
	 * @see org.societies.api.comm.xmpp.interfaces.IFeatureServer#setQuery(
	 * org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.Object)
	 */
	@Override
	public Object setQuery(Stanza stanza, Object messageBean) throws XMPPError {
		
		LOG.debug("setQuery()");
		
		return null;
	}
}
