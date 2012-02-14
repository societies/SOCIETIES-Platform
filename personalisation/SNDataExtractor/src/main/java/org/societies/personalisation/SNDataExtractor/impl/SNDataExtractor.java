package org.societies.personalisation.SNDataExtractor.impl;

import org.societies.api.internal.context.broker.ICtxBroker;

public class SNDataExtractor  {
	
	private ICtxBroker ctxBroker;

	public SNDataExtractor(){
	}
	
	public void initializationSNDataExtractor(){
		System.out.println("SNData initialized");
	}

	
	public ICtxBroker getCtxBroker() {
		return ctxBroker;
	}

	public void setCtxBroker(ICtxBroker ctxBroker) {
		this.ctxBroker = ctxBroker;
	}
	
	
	
}
