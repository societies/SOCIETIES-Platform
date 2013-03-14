package org.societies.comm.xmpp.pubsub.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.HostedNode;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.comm.xmpp.pubsub.model.PubsubNodeDAO;

// TODO collection node support
public class PubsubNode extends HostedNode {
	
	private static Logger LOG = LoggerFactory
			.getLogger(PubsubNode.class);
	
	private IIdentity owner;
	// configurations
	private Map<String, IIdentity> subscriptionsById;
	private Map<IIdentity, List<String>> subscriptionsByUser;
	// subscriberoptions
	private Stack<String> itemIdByOrder;
	private Map<String, Object> itemsById;
	private Map<String, String> publisherByItemId;
	// itempublishoptions??? these suck!
	
	private SessionFactory sf;
	private IIdentityManager idm;
	private PubsubNodeDAO dao;
	
	public PubsubNode(IIdentity owner, String nodeId) {
		super(nodeId, null); // TODO collection nodes
		this.owner = owner;
		subscriptionsById = new HashMap<String, IIdentity>();
		subscriptionsByUser = new HashMap<IIdentity, List<String>>();
		itemIdByOrder = new Stack<String>();
		itemsById = new HashMap<String, Object>();
		publisherByItemId = new HashMap<String, String>();
	}
	
	public PubsubNode(PubsubNodeDAO dao, SessionFactory sf, IIdentityManager idm) {
		super(dao.getNodeId(), null);
		this.sf = sf;
		this.idm = idm;
		this.dao = dao;
		
		subscriptionsById = new HashMap<String, IIdentity>();
		subscriptionsByUser = new HashMap<IIdentity, List<String>>();
		itemIdByOrder = new Stack<String>();
		itemsById = new HashMap<String, Object>();
		publisherByItemId = new HashMap<String, String>();
		
		loadFromDAO();
	}
	
	public PubsubNodeDAO enablePersistence(SessionFactory sf, IIdentityManager idm) {
		if (dao==null) {
			this.sf = sf;
			this.idm = idm;
			
			dao = new PubsubNodeDAO();
			dao.setNodeId(getNode());
			dao.setOwner(owner.getBareJid());
			for (String subId : subscriptionsById.keySet()) {
				String subJid = subscriptionsById.get(subId).getBareJid();
				dao.getSubscriptionsById().put(subId, subJid);
			}
		}
		return dao;
	}
	
	private void loadFromDAO() {
		subscriptionsById = new HashMap<String, IIdentity>();
		subscriptionsByUser = new HashMap<IIdentity, List<String>>();
		
		try {
			owner = idm.fromJid(dao.getOwner());
			for (String subId : dao.getSubscriptionsById().keySet()) {
				String subJid = dao.getSubscriptionsById().get(subId);
//				System.out.println("restoring subscription: subId="+subId+";subJid="+subJid);
				IIdentity subIdentity = idm.fromJid(subJid);
				subscriptionsById.put(subId, subIdentity);
				List<String> subIdList = subscriptionsByUser.get(subJid);
				if (subIdList==null) {
					subIdList = new ArrayList<String>();
					subscriptionsByUser.put(subIdentity, subIdList);
				}
				subIdList.add(subId);
			}
		} catch (InvalidFormatException e) {
			LOG.error("InvalidFormatException getting identities from database", e);
		}
	}
	
	private void writeToDAO(String subId) {
		if (sf != null) {
			Session s = sf.openSession();
			Transaction tx = null;
			try {
				tx = s.beginTransaction();
				dao = (PubsubNodeDAO) s.load(PubsubNodeDAO.class, dao.getHbnId());
				
				if (subscriptionsById.get(subId)!=null) {
					String subJid = subscriptionsById.get(subId).getBareJid();
					dao.getSubscriptionsById().put(subId, subJid);
				}
				else {
					dao.getSubscriptionsById().remove(subId);
				}
				s.update(dao);
				tx.commit();
			}
			catch (HibernateException e) {
				if (tx!=null)
					tx.rollback();
				throw e;
			}
			finally {
				s.close();
			}
		}
	}
	
	public PubsubNodeDAO getDAO() {
		return dao;
	}

	public String newSubscription(IIdentity subscriber) {
		// Generate subId
		String subId = UUID.randomUUID().toString();
		while (subscriptionsById.containsKey(subId))
			subId = UUID.randomUUID().toString();
		
		// Subscribe
		subscriptionsById.put(subId, subscriber);
		List<String> subIdList = subscriptionsByUser.get(subscriber);
		if (subIdList==null) {
			subIdList = new ArrayList<String>();
			subscriptionsByUser.put(subscriber, subIdList);
		}
		subIdList.add(subId);
		
		writeToDAO(subId);
		return subId;
	}
	
	public List<String> getSubscriptions(IIdentity subscriber) {
		return subscriptionsByUser.get(subscriber);
	}
	
	public Collection<IIdentity> getSubscribers() {
		//return subscriptionsByUser.keySet(); 
		return subscriptionsById.values();
	}

	public void unsubscribe(String string) {
		IIdentity subscriber = subscriptionsById.get(string);
		subscriptionsById.remove(string);
		subscriptionsByUser.remove(subscriber); // removes all subscriptions... TODO handle subIds separately
		writeToDAO(string);
	}

	public String publishItem(String itemId, Object itemObject, String publisher) {
		if (itemId!=null) {
			// Check for existing itemId and delete old item
			Object oldItem = itemsById.get(itemId);
			if (oldItem!=null) {
				itemsById.remove(itemId);
				itemIdByOrder.remove(itemId); // TODO pop the changed item to the top of the stack?
			}
		}
		else {
			// Generate itemId
			itemId = UUID.randomUUID().toString();
			while (itemsById.containsKey(itemId))
				itemId = UUID.randomUUID().toString();
		}
		
		// Publish
		itemsById.put(itemId, itemObject);
		itemIdByOrder.push(itemId);
		publisherByItemId.put(itemId, publisher);
		return itemId;
	}

	public Object getItemPayload(String itemId) {
		return itemsById.get(itemId);
	}

	public List<String> getItemIds() {
		return Collections.unmodifiableList(itemIdByOrder);
	}

	public void removeItem(String itemId) {
		itemsById.remove(itemId);
		itemIdByOrder.remove(itemId);
	}

	public void purge() {
		itemIdByOrder = new Stack<String>();
		itemsById = new HashMap<String, Object>();
		publisherByItemId = new HashMap<String, String>();
	}

	public IIdentity getOwner() {
		return owner;
	}
}
