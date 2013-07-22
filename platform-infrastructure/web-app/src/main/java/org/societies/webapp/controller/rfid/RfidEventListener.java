/**
 * 
 */
package org.societies.webapp.controller.rfid;

import java.util.Hashtable;

import org.primefaces.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	 protected final Logger log = LoggerFactory.getLogger(getClass()); 
	private RFidClientController controller;
	private static final String RFID_EVENT_TYPE = "org/societies/rfid";
	public RfidEventListener(RFidClientController controller, IEventMgr eventMgr){
		this.controller = controller;
		eventMgr.subscribeInternalEvent(this, new String[]{RFID_EVENT_TYPE}, null);
		this.log.debug("123456789 - subscribed");
	}

	@Override
	public void handleExternalEvent(CSSEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleInternalEvent(InternalEvent event) {
		if (event.geteventType().equals(RFID_EVENT_TYPE) && (event.geteventName().equals("registrationResult"))){
			Hashtable<String, CtxAttribute> information = (Hashtable<String, CtxAttribute>) event.geteventInfo();
			boolean registered = false;
			if (information.containsKey("RFID_REGISTERED")){
				if (information.get("RFID_REGISTERED").getStringValue().equalsIgnoreCase("true")){
					registered = true;
				}
			}
			controller.setRegisterStatus(false);
			controller.retrieveRfidInfo();
			RequestContext context = RequestContext.getCurrentInstance();
			if (context!=null){
				if (registered){
					this.log.debug("updating panelDisplay");
					context.update(":mainForm:panelDisplay");
					this.log.debug("updated panelDisplay");
				}else{
					this.log.debug("updating panelRequest");
					
					context.update(":mainForm:panelRequest");
					this.log.debug("updated panelRequest");
				}
			}else{
				this.log.debug("RequestContext is null");
			}
			
		}
		
	}

	public void unsubscribe(){
		this.controller.getEventManager().unSubscribeInternalEvent(this, new String[]{RFID_EVENT_TYPE}, null);
		this.log.debug("123456789 - unsubscribed");
	}

}
