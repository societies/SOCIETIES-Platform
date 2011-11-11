package org.societies.personalisation.common.api.management;

import org.societies.personalisation.CAUI.api.model.IUserIntentAction;
import org.societies.personalisation.CRIST.api.model.ICRISTUserAction;
import org.societies.personalisation.DIANNE.api.model.IDIANNEOutcome;
import org.societies.personalisation.common.api.model.ICtxAttributeIdentifier;
import org.societies.personalisation.common.api.model.ContextModelObject;
import org.societies.personalisation.common.api.model.EntityIdentifier;
import org.societies.personalisation.common.api.model.IAction;
import org.societies.personalisation.common.api.model.IFeedbackEvent;
import org.societies.personalisation.common.api.model.ServiceResourceIdentifier;
import org.societies.personalisation.preference.api.model.IPreferenceOutcome;



/**
 * @author Eliza
 * @version 1.0
 * @created 11-Nov-2011 14:43:37
 */
public interface IInternalPersonalisationManager {

	/**
	 * Allows any service to request an context-based evaluated preference outcome.
	 * @return					the outcome in the form of an IAction object
	 * 
	 * @param requestor    the DigitalIdentity of the service requesting the outcome
	 * @param ownerID    the DigitalIdentity of the owner of the preferences (i.e. the
	 * user of this service)
	 * @param serviceID    the service identifier of the service requesting the
	 * outcome
	 * @param preferenceName    the name of the preference requested
	 */
	public IAction getIntentTask(EntityIdentifier requestor, EntityIdentifier ownerID, ServiceResourceIdentifier serviceID, String preferenceName);

	/**
	 * Allows any service to request an context-based evaluated preference outcome.
	 * @return					the outcome in the form of an IAction object
	 * 
	 * @param ownerID    the DigitalIdentity of the owner of the preferences (i.e. the
	 * user of this service)
	 * @param serviceID    the service identifier of the service requesting the
	 * outcome
	 * @param preferenceName    the name of the preference requested
	 */
	public IAction getIntentTask(EntityIdentifier ownerID, ServiceResourceIdentifier serviceID, String preferenceName);

	/**
	 * Allows any service to request an context-based evaluated preference outcome.
	 * @return					the outcome in the form of an IAction object
	 * 
	 * @param requestor    the DigitalIdentity of the service requesting the outcome
	 * @param ownerID    the DigitalIdentity of the owner of the preferences (i.e. the
	 * user of this service)
	 * @param serviceType    the type of the service requesting the outcome
	 * @param serviceID    the service identifier of the service requesting the
	 * outcome
	 * @param preferenceName    the name of the preference requested
	 */
	public IAction getPreference(EntityIdentifier requestor, EntityIdentifier ownerID, String serviceType, ServiceResourceIdentifier serviceID, String preferenceName);

	/**
	 * Allows any service to request an context-based evaluated preference outcome.
	 * @return					the outcome in the form of an IAction object
	 * 
	 * @param ownerID    the DigitalIdentity of the owner of the preferences (i.e. the
	 * user of this service)
	 * @param serviceType    the type of the service requesting the outcome
	 * @param serviceID    the service identifier of the service requesting the
	 * outcome
	 * @param preferenceName    the name of the preference requested
	 */
	public IAction getPreference(EntityIdentifier ownerID, String serviceType, ServiceResourceIdentifier serviceID, String preferenceName);

	/**
	 * 
	 * @param className
	 * @param ctxAttributeId
	 */
	public void registerForContextUpdate(String className, ICtxAttributeIdentifier ctxAttributeId);

	/**
	 * 
	 * @param feedback
	 */
	public void returnFeedback(IFeedbackEvent feedback);

	/**
	 * 
	 * @param owner
	 * @param serviceId
	 * @param dianneOutcome
	 */
	public void sendCAUIOutcome(EntityIdentifier owner, ServiceResourceIdentifier serviceId, IUserIntentAction dianneOutcome);

	/**
	 * 
	 * @param owner
	 * @param serviceId
	 * @param dianneOutcome
	 */
	public void sendDianneOutcome(EntityIdentifier owner, ServiceResourceIdentifier serviceId, IDIANNEOutcome dianneOutcome);

	/**
	 * 
	 * @param owner
	 * @param serviceId
	 * @param dianneOutcome
	 */
	public void sendITSUDUserIntentOutcome(EntityIdentifier owner, ServiceResourceIdentifier serviceId, ICRISTUserAction dianneOutcome);

	/**
	 * 
	 * @param owner
	 * @param serviceType
	 * @param serviceId
	 * @param dianneOutcome
	 */
	public void sendPreferenceOutcome(EntityIdentifier owner, String serviceType, ServiceResourceIdentifier serviceId, IPreferenceOutcome dianneOutcome);

	/**
	 * 
	 * @param ctxModelObj
	 */
	public void updateReceived(ContextModelObject ctxModelObj);

}