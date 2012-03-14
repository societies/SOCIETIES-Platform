package org.societies.security.policynegotiator;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.security.policynegotiator.INegotiationProviderCallback;
import org.societies.api.internal.security.policynegotiator.INegotiationRequester;
import org.societies.api.security.digsig.ISignatureMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NegotiationRequester implements INegotiationRequester, INegotiationProviderCallback {

	private static Logger LOG = LoggerFactory.getLogger(NegotiationRequester.class);
	
	private ISignatureMgr signatureMgr;
	
	@Autowired
	public NegotiationRequester(ISignatureMgr signatureMgr) {
		this.signatureMgr = signatureMgr;
		LOG.debug("NegotiationRequester({})", signatureMgr);
	}
	
	@PostConstruct
	public void init() {
		LOG.debug("init(): signed = {}", signatureMgr.signXml("xml", "id"));
		LOG.debug("init(): signature valid = {}", signatureMgr.verify("xml"));
	}
	
//	@Override
//	public void onGetPolicyOptions(int sessionId, String sops) {
//		// TODO Auto-generated method stub
//	}

//	@Override
//	public void onAcceptPolicyAndGetSla(int sessionId, String policy) {
//		// TODO Auto-generated method stub
//	}

	@Override
	public void acceptUnmodifiedPolicy(int sessionId,
			String selectedPolicyOptionId) {
		// TODO Auto-generated method stub
	}

	@Override
	public void reject(int sessionId) {
		// TODO Auto-generated method stub
	}

	@Override
	public void acceptModifiedPolicy(int sessionId, Object agreement) {
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see org.societies.api.internal.security.policynegotiator.INegotiationProviderCallback
	 * #receiveExamplesResult(java.lang.Object)
	 */
	@Override
	public void receiveResult(Object returnValue) {
	}

}
