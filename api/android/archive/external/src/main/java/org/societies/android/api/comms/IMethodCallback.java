package org.societies.android.api.comms;

/**
 * Simple callback interface to allow the ClientCommunicationMgr to route return values to its correct caller
 *
 */
public interface IMethodCallback {
	/**
	 * A generic method to receive the callback action
	 * @param resultFlag
	 */
	void returnAction(boolean resultFlag);
	/**
	 * A generic method to receive the callback action
	 * @param result
	 */
	void returnAction(String result);
}
