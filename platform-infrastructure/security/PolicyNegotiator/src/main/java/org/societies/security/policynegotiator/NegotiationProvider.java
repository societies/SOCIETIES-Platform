package org.societies.security.policynegotiator;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.societies.api.internal.security.policynegotiator.INegotiationProvider;
import org.societies.api.internal.security.policynegotiator.INegotiationProviderCallback;
import org.societies.api.security.digsig.ISignatureMgr;

@Component
public class NegotiationProvider implements INegotiationProvider {

	private final String TAG = NegotiationProvider.class.getName();
	
	private ISignatureMgr signatureMgr;
	
	@Autowired
	public NegotiationProvider(ISignatureMgr signatureMgr) {
		this.signatureMgr = signatureMgr;
		System.out.println(TAG + ", " + "NegotiationProvider(signatureMgr)");
	}
	
	@PostConstruct
	public void init() {
		System.out.println(TAG + ", " + "init(): signature = " + signatureMgr.signXml("xml", "id"));
		System.out.println(TAG + ", " + "init(): signature = " + signatureMgr.verify("xml"));
	}
	
//	public void setSignatureMgr(ISignatureMgr signatureMgr) {
//		System.out.println(TAG + ", " + "setSign()");
//		this.signatureMgr = signatureMgr;
//	}
//	
//	public ISignatureMgr getSignatureMgr() {
//		System.out.println(TAG + ", " + "getSignatureMgr()");
//		return signatureMgr;
//	}

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
