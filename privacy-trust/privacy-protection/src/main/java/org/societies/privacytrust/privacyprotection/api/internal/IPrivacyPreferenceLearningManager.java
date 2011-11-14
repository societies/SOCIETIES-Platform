package org.societies.privacytrust.privacyprotection.api.internal;

import org.societies.privacytrust.privacyprotection.api.model.preference.IDObfAction;
import org.societies.privacytrust.privacyprotection.api.model.preference.IIDSAction;
import org.societies.privacytrust.privacyprotection.api.model.preference.IPPNPAction;

/**
 * @author Eliza
 * @version 1.0
 * @created 11-Nov-2011 18:58:44
 */
public interface IPrivacyPreferenceLearningManager {

	/**
	 * 
	 * @param idsAction
	 */
	public void mergeIDSAction(IIDSAction idsAction);

	/**
	 * 
	 * @param ppnpAction
	 */
	public void mergePPNPAction(IPPNPAction ppnpAction);
	
	
	public void mergeDOBFAction(IDObfAction dobfAction);

}