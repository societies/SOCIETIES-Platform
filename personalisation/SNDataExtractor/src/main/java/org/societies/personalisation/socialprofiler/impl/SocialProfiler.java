package org.societies.personalisation.socialprofiler.impl;

import org.societies.api.internal.context.broker.IUserCtxBroker;

public class SocialProfiler  {
	
	private IUserCtxBroker ctxBroker;

	public SocialProfiler(){
	}
	
	public void initializationSocialProfiler(){
		System.out.println("Social Profiler Intialized");
	}

	
	public IUserCtxBroker getCtxBroker() {
		return ctxBroker;
	}

	public void setCtxBroker(IUserCtxBroker ctxBroker) {
		this.ctxBroker = ctxBroker;
	}
	
	
	
}
