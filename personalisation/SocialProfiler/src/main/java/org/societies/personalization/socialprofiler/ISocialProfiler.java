package org.societies.personalization.socialprofiler;

import java.util.List;

import org.societies.api.context.broker.ICtxBroker;
import org.societies.api.internal.sns.ISocialConnector;



public interface SocialProfiler  {
	
	
	/**
	 * Return a ContextBroker Interface instance	
	 * @return ICtxBroker
	 */
	public ICtxBroker getCtxBroker(); 
	
	
	/**
	 * Set a Context Broker instance to store social data
	 * @param ctxBroker
	 */
	public void setCtxBroker(ICtxBroker ctxBroker);

	/**
	 * Add a new Social Network connector to fetch social data
	 * @param connector
	 */
	public void addSocialNetwork(ISocialConnector connector);
	
	/**
	 * Remove a connector from the connectors list
	 * @param connector
	 */
	public void removeSocialNetwork(ISocialConnector connector);
	
	/**
	 * Set frequency on how the graph will be updated
	 * @param frequency (DAY)
	 */
	public void setUpdateFrequency(float frequency);
	
	/**
	 * Get Frequency (DAY) of the updates
	 * @return Day of frequency
	 */
	public float getUpdateFrequency();
	
	public List<ISocialConnector> getListOfLinkedSN();
	
	
}
