package org.societies.context.source.api;

public interface ICtxSourceMgrCallback {

	/**
	 * 
	 * @param identifier
	 */
	public void cancel(String identifier);

	/**
	 * 
	 * @param id
	 */
	public void handleCallbackObject(String id);
}
