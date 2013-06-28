/**
 * 
 */
package org.societies.webapp.controller.rfid;

import java.util.Hashtable;

import org.societies.api.context.model.CtxAttribute;
import org.societies.api.osgi.event.CSSEvent;
import org.societies.api.osgi.event.EventListener;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;

/**
 * @author Eliza
 *
 */
public class RfidEventListener extends EventListener{

	private RFidClientController controller;
	private static final String RFID_EVENT_TYPE = "org/societies/rfid";
	public RfidEventListener(RFidClientController controller, IEventMgr eventMgr){
		this.controller = controller;
		eventMgr.subscribeInternalEvent(this, new String[]{RFID_EVENT_TYPE}, null);
		
	}

	@Override
	public void handleExternalEvent(CSSEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleInternalEvent(InternalEvent event) {
		if (event.geteventType().equals(RFID_EVENT_TYPE) && (event.geteventName().equals("registrationResult"))){
			Hashtable<String, CtxAttribute> information = (Hashtable<String, CtxAttribute>) event.geteventInfo();
			controller.retrieveRfidInfo();
		}
		
	}
}
