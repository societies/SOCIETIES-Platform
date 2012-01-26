package org.societies.personalisation.preference.api.callback;

import org.societies.api.internal.personalisation.model.PreferenceDetails;
import org.societies.api.mock.EntityIdentifier;
import org.societies.api.servicelifecycle.model.IServiceResourceIdentifier;
import org.societies.personalisation.preference.api.model.IPreferenceTreeModel;

public interface IPreferenceMgmtCallback {

	public void receiveModel(EntityIdentifier ownerID, String serviceType, IServiceResourceIdentifier serviceID, String preferenceName, IPreferenceTreeModel model);
	public void receiveModel(EntityIdentifier ownerID, PreferenceDetails details, IPreferenceTreeModel model);
}
