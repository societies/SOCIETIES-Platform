package org.societies.android.api.personalisation;

import java.util.concurrent.Future;

import org.societies.api.identity.IIdentity;
import org.societies.api.identity.Requestor;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.utilities.annotations.SocietiesExternalInterface;
import org.societies.utilities.annotations.SocietiesExternalInterface.SocietiesInterfaceType;



/**
 * This is the interface of the Personalisation Manager. Services can use this interface
 * to request a preference or intent action from the Personalisation system. 
 * @author Eliza
 * @version 1.0
 * @created 11-Nov-2011 14:42:55
 */
@SocietiesExternalInterface(type=SocietiesInterfaceType.PROVIDED)
public interface IPersonalisationManagerAndroid {

	public static final String INTENT_RETURN_VALUE = "org.societies.android.api.personalisation.ReturnValue";
	public static final String GET_INTENT_ACTION = "org.societies.android.api.personalisation.getIntentAction";
	public static final String GET_PREFERENCE = "org.societies.android.api.personalisation.getPreference";
	
	public String methodsArray[] = {"getIntentAction(String clientID, Requestor requestor, IIdentity ownerID, ServiceResourceIdentifier serviceID, String preferenceName)",
			"getPreference(String clientID, Requestor requestor, IIdentity ownerID, String serviceType, ServiceResourceIdentifier serviceID, String preferenceName)"};
	/**
	 * Allows any service to request an context-based evaluated preference outcome.
	 * @return TODO
	 * @return					the outcome in the form of an IAction object
	 * 
	 * @param requestor    the DigitalIdentity of the service requesting the outcome
	 * @param ownerID    the DigitalIdentity of the owner of the preferences (i.e. the
	 * user of this service)
	 * @param serviceID    the service identifier of the service requesting the
	 * outcome
	 * @param preferenceName    the name of the preference requested
	 */
	public IAction getIntentAction(String clientID, Requestor requestor, IIdentity ownerID, ServiceResourceIdentifier serviceID, String preferenceName);

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
	public IAction getPreference(String clientID, Requestor requestor, IIdentity ownerID, String serviceType, ServiceResourceIdentifier serviceID, String preferenceName);
}
