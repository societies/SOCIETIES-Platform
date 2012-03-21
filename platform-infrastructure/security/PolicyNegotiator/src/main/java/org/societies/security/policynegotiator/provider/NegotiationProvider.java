package org.societies.security.policynegotiator.provider;

import java.util.Random;
import java.util.concurrent.Future;

import org.springframework.scheduling.annotation.AsyncResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.societies.api.internal.security.policynegotiator.INegotiationProvider;
import org.societies.api.internal.security.policynegotiator.INegotiationProviderRemote;
import org.societies.api.schema.security.policynegotiator.SlaBean;
import org.societies.api.security.digsig.ISignatureMgr;

//@Component
public class NegotiationProvider implements INegotiationProvider {

	private static Logger LOG = LoggerFactory.getLogger(NegotiationProvider.class);
	
	private ISignatureMgr signatureMgr;
	private INegotiationProviderRemote groupMgr;
	
//	@Autowired
//	public NegotiationProvider(ISignatureMgr signatureMgr) {
//		
//		this.signatureMgr = signatureMgr;
//
//		LOG.info("NegotiationProvider({})", signatureMgr.toString());
//	}

	public NegotiationProvider() {
		LOG.info("NegotiationProvider()");
	}
	
//	@PostConstruct
	public void init() {
		
		LOG.debug("init(): signed = {}", signatureMgr.signXml("xml", "xmlNodeId", "identity"));
		LOG.debug("init(): signature valid = {}", signatureMgr.verify("xml"));
		
		LOG.debug("init(): group manager = {}", groupMgr.toString());
		groupMgr.reject(0);
	}
	
	// Getters and setters for beans
	public INegotiationProviderRemote getGroupMgr() {
		return groupMgr;
	}
	public void setGroupMgr(INegotiationProviderRemote groupMgr) {
		this.groupMgr = groupMgr;
	}
	public ISignatureMgr getSignatureMgr() {
		return signatureMgr;
	}
	public void setSignatureMgr(ISignatureMgr signatureMgr) {
		this.signatureMgr = signatureMgr;
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
