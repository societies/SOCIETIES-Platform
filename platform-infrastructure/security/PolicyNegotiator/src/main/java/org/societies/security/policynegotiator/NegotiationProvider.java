package org.societies.security.policynegotiator;

import java.util.Random;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.societies.api.internal.security.policynegotiator.INegotiationProvider;
import org.societies.api.internal.security.policynegotiator.INegotiationProviderRemote;
import org.societies.api.schema.security.policynegotiator.SlaBean;
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
		
		LOG.debug("init(): signed = {}", signatureMgr.signXml("xml", "xmlNodeId", "identity"));
		LOG.debug("init(): signature valid = {}", signatureMgr.verify("xml"));
		
		LOG.debug("init(): group manager = {}", groupMgr);
		//groupMgr.reject(0);
	}
	
	@Override
	public Future<SlaBean> getPolicyOptions() {
		
		SlaBean sla = new SlaBean();
		Random rnd = new Random();
		int sessionId = rnd.nextInt();
		
		sla.setSessionId(sessionId);
		// TODO: store session ID
		sla.setSla("<a/>");  // FIXME
		
		return new AsyncResult<SlaBean>(sla);
	}

	@Override
	public Future<SlaBean> acceptPolicyAndGetSla(int sessionId, String signedPolicyOption,
			boolean modified) {
		
		SlaBean sla = new SlaBean();
		String finalSla;
		
		sla.setSessionId(sessionId);
		finalSla = signedPolicyOption;  //TODO: add provider's signature
		
		if (!signatureMgr.verify(signedPolicyOption)) {
			LOG.info("acceptPolicyAndGetSla({}): invalid signature", sessionId);
			//sla.setError();
		}

		sla.setSla(finalSla);
		return new AsyncResult<SlaBean>(sla);
	}

	@Override
	public void reject(int sessionId) {

		LOG.debug("reject({})", sessionId);
	}
}
