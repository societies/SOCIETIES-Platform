package org.societies.personalisation.DIANNE.api.DianneNetwork;

import org.societies.personalisation.common.api.model.IOutcome;
import org.societies.personalisation.common.api.model.ServiceResourceIdentifier;
import org.societies.personalisation.common.api.model.EntityIdentifier;
import org.societies.personalisation.common.api.model.ContextAttribute;

/**
 * 
 * @author S.Gallacher@hw.ac.uk
 *
 */

public interface IDIANNE {
	
	/**
	 * This method will return the current value of the DIANNE preference
	 * @param ownerId	the DigitalIdentity of the owner of the preferences
	 * @param serviceId	the service identifier of the service requesting the outcome
	 * @param preferenceName	the name of the preference being requested
	 */
	public IOutcome getOutcome(EntityIdentifier ownerId, ServiceResourceIdentifier serviceId, String preferenceName);
	
	/**
	 * This method will return the current value of the DIANNE preference given the new context update
	 * @param ownerId	the DigitalIdentity of the owner of the preference
	 * @param serviceId	the service identifier of the service requesting the outcome
	 * @param preferenceName	the name of the preference being requested
	 * @param attribute		the context attribute update to implement in the DIANNE before retrieval
	 */
	public IOutcome getOutcome(EntityIdentifier ownerId, ServiceResourceIdentifier serviceId, String preferenceName, ContextAttribute attribute);
	
	/**
	 * This method will start DIANNE learning
	 */
	public void enableDIANNELearning();
	
	/**
	 * This method will stop DIANNE learning
	 */
	public void disableDIANNELearning();

}
