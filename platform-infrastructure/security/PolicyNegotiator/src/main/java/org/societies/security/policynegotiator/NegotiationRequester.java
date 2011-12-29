package org.societies.security.policynegotiator;

import javax.annotation.PostConstruct;

import org.societies.api.internal.security.policynegotiator.INegotiationProviderCallback;
import org.societies.api.internal.security.policynegotiator.INegotiationRequester;
import org.societies.api.security.digsig.ISignatureMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NegotiationRequester implements INegotiationRequester, INegotiationProviderCallback {

	private final String TAG = NegotiationRequester.class.getName();
	
	private ISignatureMgr signatureMgr;
	
	@Autowired
	public NegotiationRequester(ISignatureMgr signatureMgr) {
		this.signatureMgr = signatureMgr;
		System.out.println(TAG + ", " + "NegotiationRequester(signatureMgr)");
	}
	
	@PostConstruct
	public void init() {
		System.out.println(TAG + ", " + "init(): signatureMgr = " + signatureMgr);
	}
	
//	public void setSignatureMgr(ISignatureMgr signatureMgr) {
//		System.out.println(TAG + ", " + "setSignatureMgr()");
//		this.signatureMgr = signatureMgr;
//	}
//	
//	public ISignatureMgr getSignatureMgr() {
//		System.out.println(TAG + ", " + "getSignatureMgr()");
//		return signatureMgr;
//	}

	@Override
	public void onGetPolicyOptions(int sessionId, String sops) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onAcceptPolicyAndGetSla(int sessionId, String policy) {
		// TODO Auto-generated method stub
	}

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

}
