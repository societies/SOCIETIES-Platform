package org.societies.comm.xmpp.event;

import java.util.List;

import javax.xml.bind.JAXBException;

import org.societies.api.identity.IIdentity;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationEventMulticaster;

public abstract class PubsubEventStream implements ApplicationEventMulticaster {

	protected IIdentity pubsubService;
	protected String node;
	protected ApplicationEventMulticaster multicaster;
	
	public PubsubEventStream(IIdentity pubsubService, String node, ApplicationEventMulticaster multicaster) {
		this.pubsubService = pubsubService;
		this.node = node;
		this.multicaster = multicaster;
	}
	
	public abstract String publishLocalEvent(Object payload);
	
	public abstract void addJaxbPackages(List<String> packageList) throws JAXBException;
	
	protected void multicastRemoteEvent(PubsubEvent pe, String itemId) {
		pe.setPublished(pubsubService, node, itemId);
		multicaster.multicastEvent(pe);
	}
	
	@Override
	public void addApplicationListener(ApplicationListener arg0) {
		multicaster.addApplicationListener(arg0);
	}

	@Override
	public void multicastEvent(ApplicationEvent event) {
		if (event instanceof PubsubEvent) {
			PubsubEvent pe = (PubsubEvent)event;
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
	
	@Override
	public void addApplicationListenerBean(String arg0) {
		multicaster.addApplicationListenerBean(arg0);
		
	}

	@Override
	public void removeApplicationListenerBean(String arg0) {
		multicaster.removeApplicationListenerBean(arg0);
	}

}
