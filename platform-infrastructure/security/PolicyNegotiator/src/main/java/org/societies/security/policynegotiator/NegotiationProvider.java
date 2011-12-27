package org.societies.security.policynegotiator;

import org.societies.api.internal.security.policynegotiator.INegotiationProvider;
import org.societies.api.internal.security.policynegotiator.INegotiationProviderCallback;
import org.societies.api.security.sign.ISign;

public class NegotiationProvider implements INegotiationProvider {

	private final String TAG = NegotiationProvider.class.getName();
	
	private ISign sign;
	
	public NegotiationProvider() {
		System.out.println(TAG + ", " + "NegotiationProvider()");
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
