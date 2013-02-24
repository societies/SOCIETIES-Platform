package org.societies.api.internal.personalisation;

import java.util.List;

import org.societies.api.internal.sns.ISocialConnectorInternal;
import org.societies.api.internal.sns.ISocialDataInternal;



public interface ISocialProfiler  {
	
	
	/**
	 * Add a new Social Network connector to fetch social data
	 * @param connector
	 */
	public void addSocialNetwork(List<ISocialConnectorInternal> connectors);
	
	/**
	 * Remove a connector from the connectors list
	 * @param connector
	 */
	public void removeSocialNetwork(List<ISocialConnectorInternal> connectors);
	
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
	
	/**
	 * Get a list of all the available connectors
	 * @return List<ISocialConnector>
	 */
	public List<ISocialConnectorInternal> getListOfLinkedSN();
	
	/**
	 * Get instance of ISocialData 
	 * @return ISocialData interdace
	 */
	public ISocialDataInternal getSocialdata();

	/**
	 * Set an instanco of ISocialData
	 * @param socialdata inferface
	 */
	public void setSocialdata(ISocialDataInternal socialdata);
	
	
}
