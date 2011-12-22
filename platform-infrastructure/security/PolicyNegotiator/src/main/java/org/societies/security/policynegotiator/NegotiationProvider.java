package org.societies.security.policynegotiator;

import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.ResponsePolicy;
import org.societies.api.internal.security.policynegotiator.INegotiationProvider;
import org.societies.api.internal.security.policynegotiator.INegotiationRequesterCallback;
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
	public void getPolicyOptions(INegotiationRequesterCallback callback) {
		// TODO Auto-generated method stub

	}

	@Override
	public void acceptPolicy(String signedPolicyOption,
			INegotiationRequesterCallback callback) {
		// TODO Auto-generated method stub

	}

	@Override
	public void negotiatePolicy(int policyOptionId,
			ResponsePolicy modifiedPolicy,
			INegotiationRequesterCallback callback) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reject() {
		// TODO Auto-generated method stub

	}

}
