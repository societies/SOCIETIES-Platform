package org.societies.security.policynegotiator;

import org.societies.api.internal.security.policynegotiator.INegotiationProviderCallback;
import org.societies.api.internal.security.policynegotiator.INegotiationRequester;

public class NegotiationRequester implements INegotiationRequester, INegotiationProviderCallback {

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
