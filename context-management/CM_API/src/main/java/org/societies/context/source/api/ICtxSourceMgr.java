package org.societies.context.source.api;

import java.io.Serializable;

public interface ICtxSourceMgr {

	/**
	 * 
	 * @param name
	 * @param contextType
	 * @param ctxSourceCallback
	 */
	public void register(String name, String contextType, ICtxSourceMgrCallback ctxSourceCallback);

	/**
	 * 
	 * @param identifier
	 * @param data
	 * @param owner
	 */
	public void sendUpdate(String identifier, Serializable data, CtxEntity owner);

	/**
	 * 
	 * @param identifier
	 * @param data
	 */
	public void sendUpdate(String identifier, Serializable data);

	/**
	 * 
	 * @param identifier
	 */
	public void unregister(String identifier);
}