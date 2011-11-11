package org.societies.privacytrust.privacyprotection.api.model.preference;


import java.io.Serializable;

import javax.swing.tree.TreeModel;

import org.societies.privacytrust.privacyprotection.api.model.preference.constants.PrivacyPreferenceTypeConstants;

/**
 * This interface defines the methods required from a class that encapsulates the tree model for a privacy preference.
 * @author Elizabeth
 *
 */
public interface IPrivacyPreferenceTreeModel extends TreeModel,Serializable {
	public IPrivacyPreference getRootPreference();
	public PrivacyPreferenceTypeConstants getPrivacyType();
}
