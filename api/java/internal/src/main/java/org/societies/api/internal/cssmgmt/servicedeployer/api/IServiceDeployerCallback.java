package org.societies.api.internal.cssmgmt.servicedeployer.api;

/**
 * Callbacks for {@link IServiceDeployer}.
 * To be implemented by components that are invoking {@link IServiceDeployer}.
 * 
 * @author Mitja Vardjan
 */
public interface IServiceDeployerCallback {

	/**
	 * Callback for
	 * {@link IServiceDeployer#deployServiceToNode(String, String, IServiceDeployerCallback)}
	 * 
	 * @param success True if the method completed successfully, false otherwise.
	 * 
	 * @param msg Message, usually in case of error.
	 */
	public void onDeployServiceToNode(boolean success, String msg);
}
