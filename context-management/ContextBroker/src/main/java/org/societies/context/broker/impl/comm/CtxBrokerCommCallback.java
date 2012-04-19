package org.societies.context.broker.impl.comm;

import java.util.HashMap;
import java.util.Map;

import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.schema.context.contextmanagement.CtxBrokerCreateEntityBean;
import org.societies.context.broker.api.ICtxCallback;

public class CtxBrokerCommCallback {

	//MAP TO STORE THE ALL THE CLIENT CONNECTIONS
	private final Map<String, ICtxCallback> ctxClients = new HashMap<String, ICtxCallback>();
		
	
	public CtxBrokerCommCallback(String clientID, ICtxCallback ctxBrokerClient) {
		//STORE THIS CALLBACK WITH THIS REQUEST ID
		ctxClients.put(clientID, ctxBrokerClient);
	}

	public void receiveResult(Stanza returnStanza, Object msgBean) {
		//CHECK WHICH END SERVICE IS SENDING US A MESSAGE
		if (msgBean.getClass().equals(CtxBrokerCreateEntityBean.class)) {
			CtxBrokerCreateEntityBean entityBean = 
					(CtxBrokerCreateEntityBean) msgBean;
			
			ICtxCallback ctxCallbackClient = getRequestingClient(returnStanza.getId());
			ctxCallbackClient.receiveCtxResult(entityBean);
		}
	}
	
	private ICtxCallback getRequestingClient(String requestID) {
		ICtxCallback requestingClient = (ICtxCallback) ctxClients.get(requestID);
		ctxClients.remove(requestID);
		return requestingClient;
	}
}
