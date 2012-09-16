package org.societies.context.broker.impl.comm.callbacks;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.context.broker.impl.comm.ICtxCallback;

public class LookupCallback implements ICtxCallback {

	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(LookupCallback.class);

	private List<CtxIdentifier> idList;

	@Override
	public void onCreatedEntity(CtxEntity retObject) {
	}

	@Override
	public void onCreatedAttribute(CtxAttribute retObject) {
	}

	@Override
	public void receiveCtxResult(Object retObject, String type) {
	}

	@Override
	public void onLookupCallback(List<CtxIdentifier> ctxIdsList) {

		LOG.info("onLookupCallback retObject " +idList);
		this.idList = ctxIdsList;
		synchronized (this) {	            
			notifyAll();	        
		}
		LOG.info("onLookupCallback, notify all done");
	}


	@Override
	public void onRetrieveCtx(CtxModelObject ctxObj) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpdateCtx(CtxModelObject ctxObj) {
		// TODO Auto-generated method stub

	}
	
	public List<CtxIdentifier> getResult() {
		return this.idList;
	}
}