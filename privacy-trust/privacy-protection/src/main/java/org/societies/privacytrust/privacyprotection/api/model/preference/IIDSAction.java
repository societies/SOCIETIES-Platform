package org.societies.privacytrust.privacyprotection.api.model.preference;

import java.util.List;

import org.societies.personalisation.common.api.model.ContextAttribute;
import org.societies.personalisation.common.api.model.EntityIdentifier;
import org.societies.personalisation.common.api.model.ServiceResourceIdentifier;

/**
 * This interface is used to represent an identity selection decision made by the
 * user and is used by the Privacy PreferenceLearning component.
 * @author Eliza
 * @version 1.0
 * @created 11-Nov-2011 17:06:55
 */
public interface IIDSAction {

	public List<ContextAttribute> getContext();

	public EntityIdentifier getSelectedIdentity();

	public ServiceResourceIdentifier getServiceIdentifier();

	public EntityIdentifier getServiceIdentity();

}