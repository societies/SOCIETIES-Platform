package org.societies.orchestration.eca;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.societies.api.context.CtxException;
import org.societies.api.context.event.CtxChangeEvent;
import org.societies.api.context.event.CtxChangeEventListener;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.orchestration.eca.api.IECAManager;
import org.societies.orchestration.eca.model.VisitModel;

public class LocalContextListener implements CtxChangeEventListener {
	
	private IECAManager ecaManager;
	private ICtxBroker ctxBroker;
	
	Set<String> ctxWatching;
		
	public LocalContextListener(IECAManager ecaManager, ICtxBroker ctxBroker) {
		this.ecaManager = ecaManager;
		this.ctxBroker = ctxBroker;
		this.ctxWatching = new HashSet<String>();

	}
	
	public void registerForUpdates(CtxIdentifier ctxID) {
		if(!this.ctxWatching.contains(ctxID.getType())) {
			this.ctxWatching.add(ctxID.getType());
			try {
				this.ctxBroker.registerForChanges(this, ctxID);
			} catch (CtxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public boolean isWatching(String ctxAttributeType) {
		if (this.ctxWatching.contains(ctxAttributeType)) {
			return true;
		} 
		return false;
	}

	@Override
	public void onCreation(CtxChangeEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onModification(CtxChangeEvent arg0) {
		CtxAttribute ctxAttribute = null;
		CtxIdentifier ctxID = arg0.getId();
		try {
			ctxAttribute = (CtxAttribute) ctxBroker.retrieve(ctxID).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.ecaManager.setLocalContext(ctxAttribute);
		
	}

	@Override
	public void onRemoval(CtxChangeEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpdate(CtxChangeEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
