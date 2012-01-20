package org.societies.api.internal.cssmgmt.servicedeployer.api;

/**
 * Interface for invoking Service Deployer.
 * The asynchronous methods return via {@link IServiceDeployerCallback}.
 * 
 * @author Mitja Vardjan
 */
public interface IServiceDeployer {

	/**
	 * Downloads given service from marketplace and deploys the service to local CSS.
	 * 
	 * @param serviceId Service ID
	 * 
	 * @param nodeId CSS node ID.
	 * 
	 * @param callback The callback to be invoked after method is finished.
	 */
	public void deployServiceToNode(String serviceId, String nodeId, IServiceDeployerCallback callback);
}
