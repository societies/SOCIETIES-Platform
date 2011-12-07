package org.societies.personalisation.SNDataExtractor.api.extractor;

public interface ISNDataExtractor  {
	
	/**
	 * This method will add a new USER connection between an user (new or already provisioned) 
	 * and the social network, by the specific Social Network Connector provided by T4.6
	 * @param ownerId the DigitalIdentity of the user (entity)
	 * @param 
	 */
	public SNConnector doConnectionToSN(EntityIdentifier entityId, String connectorType);
	
	
	/**
	 * This method will provide the preferences from the SN of an entity User.
	 * @param ownerId the DigitalIdentity of the user (entity)
	 */
	public IOutcome getPreferences(EntityIdentifier entityId);
	
	/**
	 * This method will provide the raw data from a specific social network 
	 * @param ownerId the DigitalIdentity of the user (entity)
	 * @parem connector the Social Network connector
     * @param serviceId	the service identifier of the service requesting the outcome
	 */
	public String getSocialPreferences(EntityIdentifier entityId, SNConnector connector, ServiceResourceIdentifier serviceId);
	
	/**
	 * This method will remove the entity User from the graph and its connection with the social network
	 * @param ownerId the DigitalIdentity of the user (entity)
	 * @parem connector the Social Network connector
     * @param serviceId	the service identifier of the service requesting the outcome
	 */
	public boolean removeConnection(EntityIdentifier entityId, SNConnector connector);
	
	
	/**
	 * This method will clear the User HISTORY to reset start the preference analisys from the beginning
	 * @param ownerId the DigitalIdentity of the user (entity)
	 * @parem connector the Social Network connector
	 */
	public void resetPreferences(EntityIdentifier entityId, SNConncetor connector);
	
		
}
