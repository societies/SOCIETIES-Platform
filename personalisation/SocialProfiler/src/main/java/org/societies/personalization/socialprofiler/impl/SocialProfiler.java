package org.societies.personalization.socialprofiler.impl;

import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.sns.ISocialConnector;



public interface SocialProfiler  {
		
	public ICtxBroker getCtxBroker(); 
	
	public void setCtxBroker(ICtxBroker ctxBroker);

	public void addSocialNetwork(ISocialConnector connector);
	
	public void removeSocialNetwork(ISocialConnector connector);
	
	
}
