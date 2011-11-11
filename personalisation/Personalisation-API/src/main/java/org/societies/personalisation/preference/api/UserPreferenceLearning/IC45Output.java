package org.societies.personalisation.preference.api.UserPreferenceLearning;

import java.util.List;

import org.societies.personalisation.common.api.model.EntityIdentifier;
import org.societies.personalisation.common.api.model.ServiceResourceIdentifier;
import org.societies.personalisation.preference.api.model.IPreferenceTreeModel;



/**
 * @author Eliza
 * @version 1.0
 * @created 11-Nov-2011 15:06:05
 */
public interface IC45Output {

	/**
	 * This method adds a new DefaultTreeModel to the object learned under the object
	 * IDigitalPersonalIdentifier and IServiceIdentifier
	 * 
	 * @param tree    - the tree to be added
	 */
	public void addTree(IPreferenceTreeModel tree);

	/**
	 * This method returns the IDigitalPersonalIdentifier related to this object
	 * @return IDigitalPersonalIdentifier - the dpi under which the info in this
	 * IC45Output object corresponds
	 */
	public EntityIdentifier getDPI();

	/**
	 * This method returns the IServiceIdentifier related to this object
	 * @return IServiceIdentifier - the service ID
	 */
	public ServiceResourceIdentifier getServiceId();

	/**
	 * This method returns the type of the service e.g. "multi-media"
	 * @return String - the service type
	 */
	public String getServiceType();

	/**
	 * This method returns a list of DefaultTreeModel objects that have been learned
	 * under this objects associated IDigitalPersonalIdentifier and IServiceIdentifier
	 * 
	 * @return a list of IDecisionTree objects
	 */
	public List<IPreferenceTreeModel> getTreeList();

}