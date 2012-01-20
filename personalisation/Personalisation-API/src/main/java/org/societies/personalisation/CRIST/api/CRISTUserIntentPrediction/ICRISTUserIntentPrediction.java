package org.societies.personalisation.CRIST.api.CRISTUserIntentPrediction;

import java.util.ArrayList;

import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.internal.personalisation.model.FeedbackEvent;
import org.societies.api.mock.EntityIdentifier;
import org.societies.api.mock.ServiceResourceIdentifier;
import org.societies.personalisation.CRIST.api.model.ICRISTUserAction;


/**
 * 
 * @author Zhu WANG
 * @version 1.0
 */
public interface ICRISTUserIntentPrediction {

	/**
	 * This method will enable the CRIST prediction
	 * 
	 * @param bool		- true to enable and false to disable
	 */
	public void enableCRISTPrediction(boolean bool);
	
	/**
	 * This method will generate CRIST prediction based on the given information "ctxAttribute"
	 * 
	 * @param ctxAttribute		- a set of context
	 */
	public ArrayList<ICRISTUserAction> getCRISTPrediction(CtxAttribute ctxAttribute);
	
	/**
	 * This method will return the user's current intent
	 * 
	 *  @param requestor	- the ID of the requestor of the Intent
	 *  @param ownerID		- the ID of the owner of the Intent
	 *  @param serviceID	- the ID of the service related to the actions upon 
	 *  which prediction should perform
	 */
	public ICRISTUserAction getCurrentUserIntentAction(EntityIdentifier requestor, EntityIdentifier ownerID, ServiceResourceIdentifier serviceID);
	
	/**
	 * This method will send user's feedback about the predicted user intent
	 * 
	 * @param feedbackEvent		- user's feedback
	 */
	public void sendFeedback(FeedbackEvent feedbackEvent);
	
	/**
	 * This method will update the newly generated CRIST User Intent Model
	 * 
	 * @param ctxModelObj	- the new CRIST Model
	 */
	public void updateReceived(CtxModelObject ctxModelObj);
}
