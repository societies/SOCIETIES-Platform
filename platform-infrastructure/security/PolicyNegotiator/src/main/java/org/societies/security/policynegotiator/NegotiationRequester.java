package org.societies.security.policynegotiator;

import org.societies.api.internal.security.policynegotiator.INegotiationProviderCallback;
import org.societies.api.internal.security.policynegotiator.INegotiationRequester;
import org.societies.api.security.sign.ISign;

public class NegotiationRequester implements INegotiationRequester, INegotiationProviderCallback {

	private final String TAG = NegotiationRequester.class.getName();
	
	private ISign sign;
	
	public NegotiationRequester() {
		System.out.println(TAG + ", " + "NegotiationRequester()");
	}
	
	public void setSign(ISign sign) {
		System.out.println(TAG + ", " + "setSign()");
		this.sign = sign;
	}
	
	public ISign getSign() {
		System.out.println(TAG + ", " + "getSign()");
		return sign;
	}

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
