package org.societies.security.policynegotiator;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.societies.api.internal.security.policynegotiator.INegotiationProvider;
import org.societies.api.internal.security.policynegotiator.INegotiationProviderCallback;
import org.societies.api.security.sign.ISign;

@Component
public class NegotiationProvider implements INegotiationProvider {

	private final String TAG = NegotiationProvider.class.getName();
	
//	@Autowired
	private ISign sign;
	
//	public NegotiationProvider() {
//		System.out.println(TAG + ", " + "NegotiationProvider()");
//	}

	@Autowired
	public NegotiationProvider(ISign sign) {
		this.sign = sign;
		System.out.println(TAG + ", " + "NegotiationProvider(sign");
	}
	
//	public void init(ISign sign) {
//		this.sign = sign;
//		System.out.println(TAG + ", " + "init()");
//	}

	@PostConstruct
	public void init2() {
		System.out.println(TAG + ", " + "init2(): sign = " + sign);
	}
	
//	@Autowired
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
