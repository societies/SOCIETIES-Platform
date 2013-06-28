/**
 * 
 */
package org.societies.rfid.server;

import java.util.Hashtable;

import org.societies.api.osgi.event.CSSEvent;
import org.societies.api.osgi.event.EventListener;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;

/**
 * @author Eliza
 *
 */
public class RfidWebAppEventListener extends EventListener{

	private static final String RFID_EVENT_TYPE = "org/societies/rfid";
	private RfidServer server;
	private IEventMgr eventMgr;

	public RfidWebAppEventListener(RfidServer server){
		this.server = server;
		eventMgr = server.getEventMgr();
		eventMgr.subscribeInternalEvent(this, new String[]{RFID_EVENT_TYPE}, null);
		
	}

	@Override
	public void handleExternalEvent(CSSEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleInternalEvent(InternalEvent event) {
		if (event.geteventType().equalsIgnoreCase(RFID_EVENT_TYPE)){
			Hashtable<String, String> payload = (Hashtable<String, String>) event.geteventInfo();
			if (event.geteventName().equalsIgnoreCase("addNewTag")){
				
				if (payload.containsKey("tag")){
					if (payload.containsKey("password")){
						this.server.storePassword(payload.get("tag"), payload.get("password"));
					}else{
						String password = this.server.getPassword();
						this.server.storePassword(payload.get("tag"), password);
					}
				}
			}else if (event.geteventName().equalsIgnoreCase("deleteTag")){
				if (payload.containsKey("tag")){
					this.server.deleteTag(payload.get("tag"));
				}
			}
		}
		
	}
}
