package org.societies.orchestration.eca;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.event.CtxChangeEvent;
import org.societies.api.context.event.CtxChangeEventListener;

public class LocationChangeListener implements CtxChangeEventListener {

	private Logger log = LoggerFactory.getLogger(LocationChangeListener.class);
	
//

	@Override
	public void onCreation(CtxChangeEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpdate(CtxChangeEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onModification(CtxChangeEvent event) {
		log.debug("It has been modified");

	}

	@Override
	public void onRemoval(CtxChangeEvent event) {
		// TODO Auto-generated method stub

	}
	
	private void updateLocation(String location) {
		
	}

}
