package org.societies.android.platform.comms.state;

import org.societies.android.platform.comms.state.IConnectionState.ConnectionAction;
import org.societies.android.platform.comms.state.IConnectionState.ConnectionState;

/**
 * Class that encapsulates the action to take and the next state that the FSM will be in
 * after the action handler has been invoked.
 *
 */
class StateEventAction {
	private ConnectionAction actionHandler;
	private ConnectionState futureState;

	public StateEventAction(ConnectionAction actionHandler, ConnectionState futureState) {
		this.actionHandler = actionHandler;
		this.futureState = futureState;
	}
	
	public ConnectionAction getActionHandler() {
		return actionHandler;
	}
	public void setActionHandler(ConnectionAction actionHandler) {
		this.actionHandler = actionHandler;
	}
	public ConnectionState getFutureState() {
		return futureState;
	}
	public void setFutureState(ConnectionState futureState) {
		this.futureState = futureState;
	}
}
