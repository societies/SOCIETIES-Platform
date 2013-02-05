package org.societies.android.api.personalisation;


import org.societies.android.api.identity.ARequestor;
import org.societies.android.api.personalisation.model.AAction;
import org.societies.android.api.servicelifecycle.AServiceResourceIdentifier;
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

	public String methodsArray[] = {"getIntentAction(String clientID, org.societies.android.api.identity.ARequestor requestor, String ownerID, org.societies.android.api.servicelifecycle.AServiceResourceIdentifier serviceID, String preferenceName)",
	"getPreference(String clientID, org.societies.android.api.identity.ARequestor requestor, String ownerID, String serviceType, org.societies.android.api.servicelifecycle.AServiceResourceIdentifier serviceID, String preferenceName)"};
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
	 * @return	this method doesn't actually return anything. use the INTENT_RETURN_VALUE to retrieve the result 
	 */
	public AAction getIntentAction(String clientID, ARequestor requestor, String ownerID, AServiceResourceIdentifier serviceID, String preferenceName);



	/**
	 * Allows any service to request an context-based evaluated preference outcome.
	 * 
	 * @param requestor    the DigitalIdentity of the service requesting the outcome
	 * @param ownerID    the DigitalIdentity of the owner of the preferences (i.e. the
	 * user of this service)
	 * @param serviceType    the type of the service requesting the outcome
	 * @param serviceID    the service identifier of the service requesting the
	 * outcome
	 * @param preferenceName    the name of the preference requested
	 * @return	this method doesn't actually return anything. use the INTENT_RETURN_VALUE to retrieve the result 
	 */
	public AAction getPreference(String clientID, ARequestor requestor, String ownerID, String serviceType, AServiceResourceIdentifier serviceID, String preferenceName);


}
