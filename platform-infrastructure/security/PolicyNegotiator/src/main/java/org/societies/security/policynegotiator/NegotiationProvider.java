package org.societies.security.policynegotiator;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.societies.api.internal.security.policynegotiator.INegotiationProvider;
import org.societies.api.internal.security.policynegotiator.INegotiationProviderCallback;
import org.societies.api.security.digsig.ISignatureMgr;

@Component
public class NegotiationProvider implements INegotiationProvider {

	private static Logger LOG = LoggerFactory.getLogger(NegotiationProvider.class);
	
	private ISignatureMgr signatureMgr;
	
	@Autowired
	public NegotiationProvider(ISignatureMgr signatureMgr) {
		this.signatureMgr = signatureMgr;
		LOG.debug("NegotiationProvider({})", signatureMgr);
	}
	
	@PostConstruct
	public void init() {
		LOG.debug("init(): signed = {}", signatureMgr.signXml("xml", "id"));
		LOG.debug("init(): signature valid = {}", signatureMgr.verify("xml"));
	}
	
	@Override
	public void getPolicyOptions(INegotiationProviderCallback callback) {
		// TODO Auto-generated method stub
	}

	@Override
	public void acceptPolicyAndGetSla(int sessionId, String signedPolicyOption,
			boolean modified, INegotiationProviderCallback callback) {
		// TODO Auto-generated method stub
	}

	@Override
	public void reject(int sessionId) {
		// TODO Auto-generated method stub
	}
}
