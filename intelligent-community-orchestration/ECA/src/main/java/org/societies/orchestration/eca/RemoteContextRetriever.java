package org.societies.orchestration.eca;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.management.ICis;
import org.societies.api.cis.management.ICisManagerCallback;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.broker.CtxAccessControlException;
import org.societies.api.context.broker.ICtxBroker;
import org.societies.api.context.event.CtxChangeEvent;
import org.societies.api.context.event.CtxChangeEventListener;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityTypes;
import org.societies.api.context.model.CtxHistoryAttribute;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.api.schema.cis.community.CommunityMethods;
import org.societies.api.schema.cis.community.Participant;
import org.societies.api.schema.css.directory.CssAdvertisementRecord;
import org.societies.api.schema.identity.RequestorCisBean;
import org.societies.orchestration.eca.api.IECAManager;

public class RemoteContextRetriever  {

	private ICtxBroker ctxBrokerRemote;
	private ICommManager commManager;
	private IECAManager ecaManager;

	private IIdentity localID;
	private String localJID;

	private Requestor requestor;

	private Map<String, Set<String>> waitingResponse;
	private Map<String, Set<String>> blockedUsers;

	private Logger log = LoggerFactory.getLogger(RemoteContextRetriever.class);

	public RemoteContextRetriever() {
		waitingResponse = new HashMap<String, Set<String>>() {{
			put(CtxAttributeTypes.INTERESTS, new HashSet<String>());
			put(CtxAttributeTypes.WORK_POSITION, new HashSet<String>());
			put(CtxAttributeTypes.LOCATION_SYMBOLIC, new HashSet<String>());
		}};
		blockedUsers = new HashMap<String, Set<String>>(){{
			put(CtxAttributeTypes.INTERESTS, new HashSet<String>());
			put(CtxAttributeTypes.WORK_POSITION, new HashSet<String>());
			put(CtxAttributeTypes.LOCATION_SYMBOLIC, new HashSet<String>());
		}};
	}

	public void registerECAManager(IECAManager ecaManager) {
		this.ecaManager = ecaManager;
	}

	public List<CtxIdentifier> checkForWaitingRequests(List<CtxIdentifier> ctxIdentifiers) {
		Iterator<CtxIdentifier> it = ctxIdentifiers.iterator();
		CtxIdentifier ctxID = null;
		while(it.hasNext()){
			ctxID = it.next();
			synchronized (waitingResponse) {
				log.debug("Checking if " + ctxID.getOwnerId() + " is already waiting for " + ctxID.getType());
				if(waitingResponse.get(ctxID.getType()).contains(ctxID.getOwnerId())) {
					log.debug("Waiting for response on : " + ctxID.getType());
					ctxIdentifiers.remove(ctxID);
				}				
			}
		}
		return ctxIdentifiers;
	}

	public void addToWaitingRequests(List<CtxIdentifier> ctxIdentifiers) {
		for (CtxIdentifier ctxID : ctxIdentifiers) {
			synchronized (waitingResponse) {
				log.debug("Adding " + ctxID.getOwnerId() + " to waiting for " + ctxID.getType());
				waitingResponse.get(ctxID.getType()).add(ctxID.getOwnerId());
			}
		}
	}

	public void removeFromWaitingRequests(List<CtxIdentifier> ctxIdentifiers) {
		for(CtxIdentifier ctxID : ctxIdentifiers) {
			synchronized (waitingResponse) {
				waitingResponse.get(ctxID.getType()).remove(ctxID.getOwnerId());
			}
		}
	}

	public void registerRemoteCSS(Set<String> userJIDs) {
		for(String user : userJIDs) {
			try {
				List<CtxIdentifier> symLocCtxID = this.ctxBrokerRemote.lookup(this.requestor, this.commManager.getIdManager().fromJid(user), CtxAttributeTypes.LOCATION_SYMBOLIC).get();
				List<CtxIdentifier> interestsCtxID = this.ctxBrokerRemote.lookup(this.requestor, this.commManager.getIdManager().fromJid(user), CtxAttributeTypes.INTERESTS).get();
				List<CtxIdentifier> workPosCtxID = this.ctxBrokerRemote.lookup(this.requestor, this.commManager.getIdManager().fromJid(user), CtxAttributeTypes.WORK_POSITION).get();

				List<CtxIdentifier> ctxIdentifiers = new ArrayList<CtxIdentifier>();

				if(symLocCtxID.size()>0) {
					ctxIdentifiers.add(symLocCtxID.get(0));

				}
				if(interestsCtxID.size()>0) {
					ctxIdentifiers.add(interestsCtxID.get(0));

				}
				if(workPosCtxID.size()>0) {
					ctxIdentifiers.add(workPosCtxID.get(0));
				}		
				ctxIdentifiers = checkForWaitingRequests(ctxIdentifiers);

				if(ctxIdentifiers.size()>0) {
					addToWaitingRequests(ctxIdentifiers);
					RetrieveCSSContext retrieveContext = new RetrieveCSSContext(ctxIdentifiers, this, this.ctxBrokerRemote, this.requestor);
					Thread thread = new Thread(retrieveContext);
					thread.start();
				}
			} catch (CtxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


	public void recieveUserContext(List<CtxModelObject> ctxModels, List<CtxIdentifier> ctxIdentifiers) {

		removeFromWaitingRequests(ctxIdentifiers);

		if(ctxModels==null) {
			//USER HAS DECLINED ACCESS
			for(CtxIdentifier ctxID : ctxIdentifiers) {
				synchronized (blockedUsers) {
					log.debug("USer has blocked access to all requested items, lets put them in the map!");
					blockedUsers.get(ctxID.getType()).add(ctxID.getOwnerId());
					log.debug("Map now contains; " + blockedUsers.get(ctxID).toString());
				}
			}
		}
		else {
			List<CtxAttribute> attList = new ArrayList<CtxAttribute>();
			for(CtxModelObject model : ctxModels) {
				ctxIdentifiers.remove(model.getId());
				CtxAttribute att = (CtxAttribute) model;
				attList.add(att);
			}
			for(CtxIdentifier ctxID : ctxIdentifiers) {
				synchronized(blockedUsers) {
					log.debug("User has declined access to some entities");
					blockedUsers.get(ctxID.getType()).add(ctxID.getOwnerId());
					log.debug("Map now contains " + blockedUsers.get(ctxID.getType()).toString());
				}
			}
			this.ecaManager.addUsersContext(attList);
		}
	}


	class RetrieveCSSContext implements Runnable {

		private List<CtxIdentifier> ctxIdentifiers;
		private RemoteContextRetriever remoteContextRetriever;
		private ICtxBroker ctxBroker;
		private Requestor requestor;


		public RetrieveCSSContext(List<CtxIdentifier> ctxIdentifiers, RemoteContextRetriever remoteContextRetriever, ICtxBroker ctxBroker, Requestor requestor) {
			this.ctxIdentifiers = ctxIdentifiers;
			this.remoteContextRetriever = remoteContextRetriever;
			this.ctxBroker = ctxBroker;
			this.requestor = requestor;
		}

		@Override
		public void run() {
			//	List<CtxModelObject> ctxModels = new ArrayList<CtxModelObject>();
			//	for(CtxIdentifier ctxID : this.ctxIdentifiers) {
			List<CtxModelObject> ctxModels = new ArrayList<CtxModelObject>();
			//	}
			try {
				ctxModels = this.ctxBroker.retrieve(this.requestor, ctxIdentifiers).get();
				remoteContextRetriever.recieveUserContext(ctxModels, this.ctxIdentifiers);
				//CtxEception would have happened by now if we were denied access. Now we are given access, lets register for changes


			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CtxException e) {
				remoteContextRetriever.recieveUserContext(null, this.ctxIdentifiers);
			}
		}

	}

	public void initRemoteCtxRetriever() {
		this.localID = this.commManager.getIdManager().getThisNetworkNode();
		this.localJID = this.localID.getBareJid();
		this.requestor = new Requestor(localID);
		//getFriends();
	}


	public ICtxBroker getCtxBrokerRemote() {
		return ctxBrokerRemote;
	}

	public void setCtxBrokerRemote(ICtxBroker ctxBrokerRemote) {
		this.ctxBrokerRemote = ctxBrokerRemote;
	}

	public ICommManager getCommManager() {
		return commManager;
	}

	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
	}





}

