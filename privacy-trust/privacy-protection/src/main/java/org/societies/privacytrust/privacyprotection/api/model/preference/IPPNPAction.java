package org.societies.privacytrust.privacyprotection.api.model.preference;

import java.util.List;

import org.societies.personalisation.common.api.model.ContextAttribute;
import org.societies.personalisation.common.api.model.EntityIdentifier;
import org.societies.personalisation.common.api.model.ICtxAttributeIdentifier;
import org.societies.personalisation.common.api.model.ServiceResourceIdentifier;
import org.societies.privacytrust.privacyprotection.api.model.privacyPolicy.Decision;

/**
 * This interface is used to represent an access permission decision made by the
 * user and is used by the Privacy PreferenceLearning component.
 * @author Eliza
 * @version 1.0
 * @created 11-Nov-2011 17:06:55
 */
public interface IPPNPAction {

	public List<ContextAttribute> getContext();

	public ICtxAttributeIdentifier getICtxAttributeIdentifier();

	public Decision getDecision();

	public ServiceResourceIdentifier getServiceIdentifier();

	public EntityIdentifier getServiceIdentity();

}