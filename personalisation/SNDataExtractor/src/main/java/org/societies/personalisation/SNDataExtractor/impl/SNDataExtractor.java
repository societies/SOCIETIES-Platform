package org.societies.personalisation.SNDataExtractor.impl;

import org.societies.api.internal.context.broker.IUserCtxBroker;

public class SNDataExtractor  {
	
	private IUserCtxBroker ctxBroker;

	public SNDataExtractor(){
	}
	
	public void initializationSNDataExtractor(){
		System.out.println("SNData initialized");
	}

	
	public IUserCtxBroker getCtxBroker() {
		return ctxBroker;
	}

	public void setCtxBroker(IUserCtxBroker ctxBroker) {
		this.ctxBroker = ctxBroker;
	}
	
	
	
}
