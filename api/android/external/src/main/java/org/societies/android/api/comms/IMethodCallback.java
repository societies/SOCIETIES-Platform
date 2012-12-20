package org.societies.android.api.comms;

/**
 * Simple callback interface to allow the ClientCommunicationMgr to route return values to its correct caller
 *
 */
public interface IMethodCallback {

	void returnAction(Object o);
}
