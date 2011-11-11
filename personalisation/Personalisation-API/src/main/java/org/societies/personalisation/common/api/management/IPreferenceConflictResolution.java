package org.societies.personalisation.common.api.management;

import org.societies.personalisation.CAUI.api.model.IUserIntentAction;
import org.societies.personalisation.CRIST.api.model.ICRISTUserAction;
import org.societies.personalisation.DIANNE.api.model.IDIANNEOutcome;
import org.societies.personalisation.common.api.model.EntityIdentifier;
import org.societies.personalisation.common.api.model.IOutcome;
import org.societies.personalisation.preference.api.model.IPreferenceOutcome;



/**
 * @author Eliza
 * @version 1.0
 * @created 11-Nov-2011 14:43:38
 */
public interface IPreferenceConflictResolution {

	/**
	 * 
	 * @param user_id
	 * @param dianneAction
	 * @param preferenceAction
	 */
	public IOutcome resolveConflict(EntityIdentifier user_id, IDIANNEOutcome dianneAction, IPreferenceOutcome preferenceAction);

	/**
	 * 
	 * @param user_id
	 * @param intentActionICCS
	 * @param intentActionITSUD
	 */
	public IOutcome resolveConflict(EntityIdentifier user_id, IUserIntentAction intentActionICCS, ICRISTUserAction intentActionITSUD);

}