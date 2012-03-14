package org.societies.security.policynegotiator;

import java.util.concurrent.Future;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.societies.api.internal.security.policynegotiator.INegotiationProvider;
import org.societies.api.internal.security.policynegotiator.INegotiationProviderRemote;
import org.societies.api.internal.security.policynegotiator.Sla;
import org.societies.api.security.digsig.ISignatureMgr;

@Component
public class NegotiationProvider implements INegotiationProvider {

	private static Logger LOG = LoggerFactory.getLogger(NegotiationProvider.class);
	
	private ISignatureMgr signatureMgr;
	private INegotiationProviderRemote groupMgr;
	
	@Autowired
	public NegotiationProvider(ISignatureMgr signatureMgr, INegotiationProviderRemote groupMgr) {
		this.signatureMgr = signatureMgr;
		LOG.debug("NegotiationProvider({})", signatureMgr);

		this.groupMgr = groupMgr;
		LOG.debug("init(): group manager = {}", groupMgr);
	}
	
	@PostConstruct
	public void init() {
		
		LOG.debug("init(): signed = {}", signatureMgr.signXml("xml", "id"));
		LOG.debug("init(): signature valid = {}", signatureMgr.verify("xml"));
		
		LOG.debug("init(): group manager = {}", groupMgr);
		//groupMgr.reject(0);
	}
	
	@Override
	public Future<Sla> getPolicyOptions() {
		return null;
	}

	@Override
	public Future<Sla> acceptPolicyAndGetSla(int sessionId, String signedPolicyOption,
			boolean modified) {
		
		return null;
	}

	@Override
	public void reject(int sessionId) {

		LOG.debug("reject({})", sessionId);
	}
}
