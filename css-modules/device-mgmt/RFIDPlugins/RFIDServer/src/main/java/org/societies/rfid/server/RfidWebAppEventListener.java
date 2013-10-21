/**
 * 
 */
package org.societies.rfid.server;

import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.osgi.event.CSSEvent;
import org.societies.api.osgi.event.EventListener;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;

/**
 * @author Eliza
 *
 */
public class RfidWebAppEventListener extends EventListener{

	private Logger logging = LoggerFactory.getLogger(this.getClass());

    private static final String RFID_SERVER_EVENT_TYPE = "org/societies/rfid/server";

	private RfidServer server;
	private IEventMgr eventMgr;

	public RfidWebAppEventListener(RfidServer server){
		this.server = server;
		eventMgr = server.getEventMgr();
		eventMgr.subscribeInternalEvent(this, new String[]{RFID_SERVER_EVENT_TYPE}, null);
		logging.debug("registered for events!");
		
	}

	@Override
	public void handleExternalEvent(CSSEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleInternalEvent(InternalEvent event) {
		if(logging.isDebugEnabled()) logging.debug("Received event: "+event.geteventType()+" name: "+event.geteventName());
		if (event.geteventType().equalsIgnoreCase(RFID_SERVER_EVENT_TYPE)){
			Hashtable<String, String> payload = (Hashtable<String, String>) event.geteventInfo();
			if (event.geteventName().equalsIgnoreCase("addNewTag")){
				if(logging.isDebugEnabled()) logging.debug("adding new tag");
				if (payload.containsKey("tag")){
					if (payload.containsKey("password")){
						this.server.addTag(payload.get("tag"), payload.get("password"));
					}else{
						String password = this.server.getPassword();
						this.server.addTag(payload.get("tag"), password);
					}
				}
			}else if (event.geteventName().equalsIgnoreCase("deleteTag")){
				if (payload.containsKey("tag")){
					this.server.requestDeleteTag(payload.get("tag"));
				}
			}
		}
		
	}
}
