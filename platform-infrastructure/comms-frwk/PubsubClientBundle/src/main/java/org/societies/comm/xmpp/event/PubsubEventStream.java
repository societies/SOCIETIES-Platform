package org.societies.comm.xmpp.event;

import org.societies.api.comm.xmpp.datatypes.Identity;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.w3c.dom.Element;

public abstract class PubsubEventStream implements ApplicationEventMulticaster {

	protected Identity pubsubService;
	protected String node;
	protected ApplicationEventMulticaster multicaster;
	
	public PubsubEventStream(Identity pubsubService, String node, ApplicationEventMulticaster multicaster) {
		this.pubsubService = pubsubService;
		this.node = node;
		this.multicaster = multicaster;
	}
	
	public abstract String publishLocalEvent(Element payload);
	
	protected void multicastRemoteEvent(PubsubEvent pe, String itemId) {
		pe.setPublished(pubsubService, node, itemId);
		multicaster.multicastEvent(pe);
	}
	
	@Override
	public void addApplicationListener(ApplicationListener arg0) {
		multicaster.addApplicationListener(arg0);
	}

	@Override
	public void multicastEvent(ApplicationEvent arg0) {
		if (arg0 instanceof PubsubEvent) {
			PubsubEvent pe = (PubsubEvent)arg0;
			if (!pe.isPublished()) {
				// publish to XMPP node
				String itemId = publishLocalEvent(pe.getPayload());
				pe.setPublished(pubsubService, node, itemId);
			}
			multicaster.multicastEvent(pe);
		}
	}

	@Override
	public void removeAllListeners() {
		multicaster.removeAllListeners();
	}

	@Override
	public void removeApplicationListener(ApplicationListener arg0) {
		multicaster.removeApplicationListener(arg0);
	}

}
