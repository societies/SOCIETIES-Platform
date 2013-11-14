package org.societies.webapp.controller.userfeedback;

import org.primefaces.context.RequestContext;
import org.societies.api.comm.xmpp.pubsub.PubsubClient;
import org.societies.api.comm.xmpp.pubsub.Subscriber;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.schema.useragent.feedback.UserFeedbackAccessControlEvent;
import org.societies.api.internal.schema.useragent.feedback.UserFeedbackPrivacyNegotiationEvent;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.internal.useragent.model.ExpProposalType;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.schema.useragent.feedback.*;
import org.societies.useragent.api.feedback.IInternalUserFeedback;
import org.societies.useragent.api.model.UserFeedbackEventTopics;
import org.societies.webapp.ILoginListener;
import org.societies.webapp.controller.BasePageController;
import org.societies.webapp.entity.NotificationQueueItem;
import org.societies.webapp.service.UserService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.primefaces.push.PushContext;
import org.primefaces.push.PushContextFactory;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.inject.Named;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Controller
@Scope("session")
@Named("notifications")
@ManagedBean(name = "notifications", eager = true)
@SessionScoped
public class NotificationsController extends BasePageController {


	private class PubSubListener implements Subscriber {
		//pubsub event schemas
		private final List<String> EVENT_SCHEMA_CLASSES =
				Collections.unmodifiableList(Arrays.asList(
						"org.societies.api.schema.useragent.feedback.UserFeedbackBean",
						"org.societies.api.schema.useragent.feedback.ExpFeedbackResultBean",
						"org.societies.api.schema.useragent.feedback.ImpFeedbackResultBean",
						"org.societies.api.internal.schema.useragent.feedback.UserFeedbackPrivacyNegotiationEvent",
						"org.societies.api.internal.schema.useragent.feedback.UserFeedbackAccessControlEvent"));
		private final List<String> EVENT_TYPES =
				Collections.unmodifiableList(Arrays.asList(
						EventTypes.UF_PRIVACY_NEGOTIATION,
						EventTypes.UF_PRIVACY_NEGOTIATION_RESPONSE,
						EventTypes.UF_PRIVACY_NEGOTIATION_REMOVE_POPUP,
						EventTypes.UF_PRIVACY_ACCESS_CONTROL,
						EventTypes.UF_PRIVACY_ACCESS_CONTROL_RESPONSE,
						EventTypes.UF_PRIVACY_ACCESS_CONTROL_REMOVE_POPUP,
						UserFeedbackEventTopics.EXPLICIT_RESPONSE,
						UserFeedbackEventTopics.IMPLICIT_RESPONSE,
						UserFeedbackEventTopics.REQUEST,
						UserFeedbackEventTopics.COMPLETE));

		public void registerForEvents() {

			if (log.isDebugEnabled())
				log.debug("registerForEvents()");

			if (pubsubClient == null) {
				log.error("PubSubClient was null, cannot register for events");
				return;
			}

			try {
				//register schema classes
				pubsubClient.addSimpleClasses(EVENT_SCHEMA_CLASSES);

			} catch (Exception e) {
				addGlobalMessage("Error subscribing to pubsub schema classes",
						e.getMessage(),
						FacesMessage.SEVERITY_ERROR);
				log.error("Error subscribing to pubsub schema classes", e);
			}

			for (String eventType : EVENT_TYPES) {
				try {
					pubsubClient.subscriberSubscribe(userService.getIdentity(),
							eventType,
							this);

					if (log.isDebugEnabled())
						log.debug("Subscribed to " + eventType + " events");

				} catch (Exception e) {
					addGlobalMessage("Error subscribing to pubsub notifications",
							e.getMessage(),
							FacesMessage.SEVERITY_ERROR);
					log.error("Error subscribing to pubsub notifications (id=" + userService.getIdentity() + " event=" + eventType, e);
				}
			}
		}

		@Override
		public void pubsubEvent(IIdentity pubsubService, String node, String itemId, Object item) {

			if (item == null) {
				log.warn(String.format("Received pubsub event with NULL PAYLOAD - not recording. Node '%s', ID '%s'",
						node,
						itemId)
						);
				return;
			}

			// create the correct notification type for the incoming event or process the response correctly

			if (EventTypes.UF_PRIVACY_NEGOTIATION.equals(node)) {

				processPrivacyNegotiationEvent(node, itemId, item);

			} else if (EventTypes.UF_PRIVACY_NEGOTIATION_RESPONSE.equals(node)
					|| EventTypes.UF_PRIVACY_NEGOTIATION_REMOVE_POPUP.equals(node)) {

				processPrivacyNegotiationResponse(node, itemId, item);

			} else if (EventTypes.UF_PRIVACY_ACCESS_CONTROL.equals(node)) {

				processAccessControlEvent(node, itemId, item);

			} else if (EventTypes.UF_PRIVACY_ACCESS_CONTROL_RESPONSE.equals(node)
					|| EventTypes.UF_PRIVACY_ACCESS_CONTROL_REMOVE_POPUP.equals(node)) {

				processAccessControlResponse(node, itemId, item);

			} else if (UserFeedbackEventTopics.REQUEST.equals(node)) {

				processUserFeedbackEvent(node, itemId, item);

			} else if (UserFeedbackEventTopics.EXPLICIT_RESPONSE.equals(node)
					|| UserFeedbackEventTopics.IMPLICIT_RESPONSE.equals(node)
					|| UserFeedbackEventTopics.COMPLETE.equals(node)) {

				processUserFeedbackResponse(node, itemId, item);

			} else {

				String fmt = "Unknown event %s, payload type %s with ID %s";
				log.warn(String.format(fmt,
						node, item.getClass().getSimpleName(), itemId));

				return;
			}

			// notify the user
			// TODO: Fix PrimeFaces push
			//            PushContext pushContext = PushContextFactory.getDefault().getPushContext();
			//            pushContext.push("/notifications", "");

			if (log.isDebugEnabled()) {
				log.debug("numUnansweredNotifications=" + getNumUnansweredNotifications());
			}
		}


		private void processPrivacyNegotiationEvent(String node, String itemId, Object item) {
			if (!(item instanceof UserFeedbackPrivacyNegotiationEvent)) {
				log.warn(String.format("Received pubsub event with topic '%s', ID '%s' and class '%s' - Required UserFeedbackPrivacyNegotiationEvent",
						node,
						itemId,
						item.getClass().getCanonicalName()
						));
				return;
			}

			UserFeedbackPrivacyNegotiationEvent ppn = (UserFeedbackPrivacyNegotiationEvent) item;
			NotificationQueueItem newItem = NotificationQueueItem.forPrivacyPolicyNotification(String.valueOf(ppn.getRequestId()), ppn);

			addItemToQueue(newItem);

			//synchronized (unansweredPrivacyNegotiationEvents) {
			//	unansweredPrivacyNegotiationEvents.put(ppn.getRequestId(), ppn);
			//	}

		}

		private void processPrivacyNegotiationResponse(String node, String itemId, Object item) {
			if (!(item instanceof UserFeedbackPrivacyNegotiationEvent)) {
				log.warn(String.format("Received pubsub event with topic '%s', ID '%s' and class '%s' - Required UserFeedbackPrivacyNegotiationEvent",
						node,
						itemId,
						item.getClass().getCanonicalName()
						));
				return;
			}

			String id = String.valueOf(((UserFeedbackPrivacyNegotiationEvent) item).getRequestId());

			if (log.isDebugEnabled())
				log.debug(String.format("Received %s event for [%s] with options {%s}",
						node,
						id,
						"null"));

			markQueueItemComplete(id, null);

			synchronized (unansweredPrivacyNegotiationEvents) {
				unansweredPrivacyNegotiationEvents.remove(id);
			}

		}

		private void processAccessControlEvent(String node, String itemId, Object item) {
			if (!(item instanceof UserFeedbackAccessControlEvent)) {
				log.warn(String.format("Received pubsub event with topic '%s', ID '%s' and class '%s' - Required UserFeedbackAccessControlEvent",
						node,
						itemId,
						item.getClass().getCanonicalName()
						));
				return;
			}

			UserFeedbackAccessControlEvent bean = (UserFeedbackAccessControlEvent) item;
			NotificationQueueItem newItem = NotificationQueueItem.forAccessControl(bean.getRequestId(), bean);

			addItemToQueue(newItem);

			//synchronized (unansweredAccessControlEvents) {
			//		unansweredAccessControlEvents.put(bean.getRequestId(), bean);

			//	}

		}

		private void processAccessControlResponse(String node, String itemId, Object item) {
			if (!(item instanceof UserFeedbackAccessControlEvent)) {
				log.warn(String.format("Received pubsub event with topic '%s', ID '%s' and class '%s' - Required UserFeedbackAccessControlEvent",
						node,
						itemId,
						item.getClass().getCanonicalName()
						));
				return;
			}


			String id = String.valueOf(((UserFeedbackAccessControlEvent) item).getRequestId());

			if (log.isDebugEnabled())
				log.debug(String.format("Received %s event for [%s] with options {%s}",
						node,
						id,
						"null"));

			markQueueItemComplete(id, null);

			synchronized (unansweredAccessControlEvents) {
				unansweredAccessControlEvents.remove(id);
			}

		}

		private void processUserFeedbackEvent(String node, String itemId, Object item) {
			if (!(item instanceof UserFeedbackBean)) {
				log.warn(String.format("Received pubsub event with topic '%s', ID '%s' and class '%s' - Required UserFeedbackBean ",
						node,
						itemId,
						item.getClass().getCanonicalName()
						));
				return;
			}

			UserFeedbackBean bean = (UserFeedbackBean) item;
			NotificationQueueItem newItem = createNotificationQueueItemFromUserFeedbackBean(bean);

			// if we get a null item back, something has gone wrong and we've already logged the error
			if (newItem == null)
				return;

			if (bean.getMethod() == FeedbackMethodType.GET_IMPLICIT_FB) {
				// This is a timed abort - add to the list of timed aborts for the watcher thread
				synchronized (timedAbortsToWatch) {
					timedAbortsToWatch.add(newItem);
				}
			}

			addItemToQueue(newItem);
		}

		private void processUserFeedbackResponse(String node, String itemId, Object item) {

			String id;
			String[] options;

			if (item instanceof UserFeedbackBean) {

				id = ((UserFeedbackBean) item).getRequestId();
				options = ((UserFeedbackBean) item).getOptions().toArray(new String[((UserFeedbackBean) item).getOptions().size()]);

			} else if (item instanceof ExpFeedbackResultBean) {

				id = ((ExpFeedbackResultBean) item).getRequestId();
				options = ((ExpFeedbackResultBean) item).getFeedback().toArray(new String[((ExpFeedbackResultBean) item).getFeedback().size()]);

			} else if (item instanceof ImpFeedbackResultBean) {

				id = ((ImpFeedbackResultBean) item).getRequestId();
				options = new String[]{((ImpFeedbackResultBean) item).isAccepted() ? "true" : "false"};

			} else {

				log.warn(String.format("Received pubsub event with topic '%s', ID '%s' and class '%s' - Required UserFeedbackBean, ExpFeedbackResultBean or ImpFeedbackResultBean",
						node,
						itemId,
						item.getClass().getCanonicalName()
						));
				return;
			}

			if (log.isDebugEnabled())
				log.debug(String.format("Received %s event for [%s] with options {%s}",
						node,
						id,
						Arrays.toString(options)));

			markQueueItemComplete(id, options);

		}

	}

	private class LoginListener implements ILoginListener {

		@Override
		public void userLoggedIn() {
			if (log.isDebugEnabled())
				log.debug("userLoggedIn()");

			pubSubListener.registerForEvents();

			// pre-populate the list of notifications
			reloadIncompleteEvents();
		}

		@Override
		public void userLoggedOut() {
			if (log.isDebugEnabled())
				log.debug("userLoggedOut()");
		}
	}

	private class TimedAbortProcessor implements Runnable {

		private boolean abort = false;
		private final List<NotificationQueueItem> timedAbortsToWatch;
		private boolean enabled = true;

		public TimedAbortProcessor(List<NotificationQueueItem> timedAbortsToWatch) {
			this.timedAbortsToWatch = timedAbortsToWatch;
		}

		@Override
		public void run() {
			while (!abort) {
				try {
					if (enabled)
						processTimedAborts();
				} catch (Exception ex) {
					log.error("Error on timed abort processing thread", ex);
				}

				try {
					Thread.sleep(500);
				} catch (InterruptedException ex) {
					log.error("Error sleeping on timed abort processing thread", ex);
				}
			}
		}

		public void stop() {
			abort = true;
		}

		private void processTimedAborts() {
			synchronized (timedAbortsToWatch) {
				for (int i = 0; i < timedAbortsToWatch.size(); i++) {
					NotificationQueueItem ta = timedAbortsToWatch.get(i);

					// check if this TA has expired
					if (!new Date().after(ta.getTimeoutTime())) continue;

					// the TA has expired, send the response
					submitItem(ta.getItemId(), Boolean.TRUE);

					// remove from watch list
					markQueueItemComplete(ta.getItemId(), new String[]{"false"});
					i--;
				}
			}
		}

		public boolean isEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}
	}

	public static final int DEFAULT_FETCH_COUNT = 50;

	private final PubSubListener pubSubListener = new PubSubListener();
	private final LoginListener loginListener = new LoginListener();
	@SuppressWarnings("FieldCanBeLocal")
	private final Thread timedAbortProcessorThread;
	private final TimedAbortProcessor timedAbortProcessor;

	@ManagedProperty(value = "#{pubsubClient}")
	private PubsubClient pubsubClient;

	@ManagedProperty(value = "#{userService}")
	private UserService userService;

	@ManagedProperty(value = "#{userFeedback}")
	private IUserFeedback userFeedback;

	@ManagedProperty(value = "#{internalUserFeedback}")
	private IInternalUserFeedback internalUserFeedback;

	private final List<NotificationQueueItem> timedAbortsToWatch = new ArrayList<NotificationQueueItem>();

	// NB: to avoid deadlocks, always synchronise on allNotifications first, then on allNotificationIDs,
	// then unansweredNotifications, then unansweredNotificationIDs
	private final List<NotificationQueueItem> unansweredNotifications = new LinkedList<NotificationQueueItem>();
	@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
	private final Set<String> unansweredNotificationIDs = new HashSet<String>();
	private final List<NotificationQueueItem> allNotifications = new LinkedList<NotificationQueueItem>();
	private final Set<String> allNotificationIDs = new HashSet<String>();

	private final Map<String, UserFeedbackPrivacyNegotiationEvent> unansweredPrivacyNegotiationEvents = new HashMap<String, UserFeedbackPrivacyNegotiationEvent>();
	private final Map<String, UserFeedbackAccessControlEvent> unansweredAccessControlEvents = new HashMap<String, UserFeedbackAccessControlEvent>();

	public NotificationsController() {
		if(log.isDebugEnabled()) log.debug("NotificationsController ctor()");
		timedAbortProcessor = new TimedAbortProcessor(timedAbortsToWatch);
		timedAbortProcessorThread = new Thread(timedAbortProcessor);
		timedAbortProcessorThread.setName("TimedAbortProcessor");
		timedAbortProcessorThread.setDaemon(true);
	}

	@SuppressWarnings("MethodMayBeStatic")
	public boolean isDebugMode() {
		return false;
	}

	public PubsubClient getPubsubClient() {
		return pubsubClient;
	}

	public void setPubsubClient(PubsubClient pubsubClient) {
		this.pubsubClient = pubsubClient;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		if (log.isDebugEnabled())
			log.debug("setUserService() = " + userService);

		if (this.userService != null) {
			this.userService.removeLoginListener(loginListener);
		}

		this.userService = userService;
		this.userService.addLoginListener(loginListener);
	}

	public IUserFeedback getUserFeedback() {
		return userFeedback;
	}

	public void setUserFeedback(IUserFeedback userFeedback) {
		this.userFeedback = userFeedback;
	}

	public IInternalUserFeedback getInternalUserFeedback() {
		return internalUserFeedback;
	}

	public void setInternalUserFeedback(IInternalUserFeedback internalUserFeedback) {
		this.internalUserFeedback = internalUserFeedback;
	}

	@PostConstruct
	public void postConstruct() {
		timedAbortProcessorThread.start();

		// NB: Generally you DON'T want to use this method to set up your class - you want to use the LoginListener
		// - This method is called whenever the bean is created at the start of the session, while the login listener
		// - is called when the user actually logs in and an identity is available

		// call this in case we're set up after the user has logged in
		if (userService.isUserLoggedIn()) {
			loginListener.userLoggedIn();
		}
	}

	public int getNumUnansweredNotifications() {
		return unansweredNotifications.size();
	}

	public List<NotificationQueueItem> getUnansweredNegotiationQueue() {
		return unansweredNotifications;
	}

	public List<NotificationQueueItem> getAllNotificationsQueue() {
		return allNotifications;
	}

	public void acceptTimedAbort(String itemId) {
		submitItem(itemId, Boolean.TRUE);
	}

	public void abortTimedAbort(String itemId) {
		submitItem(itemId, Boolean.FALSE);
	}

	public void submitItem(String itemId) {
		submitItem(itemId, null);
	}

	private void submitItem(String itemId, Object result) {

		if(log.isDebugEnabled()) log.debug("submitItem() id=" + itemId);

		if (itemId == null) {
			log.warn("Null itemId when calling submitItem(), cannot continue");
			return;
		}

		// find the item
		NotificationQueueItem selectedItem = null;
		for (NotificationQueueItem item : unansweredNotifications) {
			if (itemId.equals(item.getItemId())) {
				selectedItem = item;
				break;
			}
		}

		if (selectedItem == null) {
			log.warn("selected ID not found when calling submitItem(), cannot continue");
			return;
		}

		selectedItem.setComplete(true);

		if (selectedItem.getType().equals(NotificationQueueItem.TYPE_ACK_NACK)
				|| selectedItem.getType().equals(NotificationQueueItem.TYPE_SELECT_ONE)
				|| selectedItem.getType().equals(NotificationQueueItem.TYPE_SELECT_MANY)
				|| selectedItem.getType().equals(NotificationQueueItem.TYPE_NOTIFICATION)) {

			List<String> feedback = new ArrayList<String>();
			if (selectedItem.getType().equals(NotificationQueueItem.TYPE_SELECT_MANY)) {
				// add all results
				Collections.addAll(feedback, selectedItem.getResults());
			} else if (selectedItem.getType().equals(NotificationQueueItem.TYPE_NOTIFICATION)) {
				// no response required - but we still have to send the event
				feedback.clear();
			} else {
				// add one result
				feedback.add(selectedItem.getSubmitResult());
			}

			try {
				userFeedback.submitExplicitResponse(selectedItem.getItemId(), feedback);

				if (log.isDebugEnabled())
					log.debug("Sent " + UserFeedbackEventTopics.EXPLICIT_RESPONSE + " with ID " + selectedItem.getItemId());
			} catch (Exception e) {
				addGlobalMessage("Error publishing notification of completed explicit UF request",
						e.getMessage(),
						FacesMessage.SEVERITY_ERROR);
				log.error("Error publishing notification of completed explicit UF request", e);
				return;
			}

		} else if (selectedItem.getType().equals(NotificationQueueItem.TYPE_TIMED_ABORT)) {

			try {
				userFeedback.submitImplicitResponse(selectedItem.getItemId(), (Boolean) result);
				if (log.isDebugEnabled())
					log.debug("Sent " + UserFeedbackEventTopics.IMPLICIT_RESPONSE + " with ID " + selectedItem.getItemId());
			} catch (Exception e) {
				addGlobalMessage("Error publishing notification of completed implicit UF request",
						e.getMessage(),
						FacesMessage.SEVERITY_ERROR);
				if (log.isDebugEnabled())
					log.error("Error publishing notification of completed implicit UF request", e);
				return;
			}
		}

		synchronized (unansweredNotifications) {
			synchronized (unansweredNotificationIDs) {
				unansweredNotifications.remove(selectedItem);
				unansweredNotificationIDs.remove(selectedItem.getItemId());
			}
		}
		if (log.isDebugEnabled())
			log.info("Notification: " + selectedItem.getItemId() + " has been removed.");
	}

	public void clearNotifications() {
		reloadIncompleteEvents();
	}

	public UserFeedbackPrivacyNegotiationEvent getPrivacyNegotiationEvent(String negotiationID) {
		synchronized (unansweredPrivacyNegotiationEvents) {
			return unansweredPrivacyNegotiationEvents.get(negotiationID);
		}
	}

	public String getView()
	{
		return FacesContext.getCurrentInstance().getViewRoot().getViewId();
	}

	public UserFeedbackAccessControlEvent getAcceessControlEvent(String eventId) {
		synchronized (unansweredAccessControlEvents) {
			return unansweredAccessControlEvents.get(eventId);
		}
	}

	private void reloadIncompleteEvents() {
		if (log.isDebugEnabled())
			log.debug("Loading incomplete UF, PPN and AC notifications");


		if (internalUserFeedback == null) {
			log.warn("internalUserFeedback is null - reloading directly from hibernate repositories (some integration tests might not work)");
		}

		List<UserFeedbackBean> userFeedbackBeans = new ArrayList<UserFeedbackBean>();
		List<UserFeedbackPrivacyNegotiationEvent> privacyNegotiationEvents = new ArrayList<UserFeedbackPrivacyNegotiationEvent>();
		List<UserFeedbackAccessControlEvent> accessControlEvents = new ArrayList<UserFeedbackAccessControlEvent>();

		try {
			if (internalUserFeedback != null)
				userFeedbackBeans = internalUserFeedback.listIncompleteFeedbackBeans();
		} catch (Exception ex) {
			log.warn("Recoverable error: Error recalling UF records: " + ex.getMessage());
		}

		try {
			if (internalUserFeedback != null)
				privacyNegotiationEvents = internalUserFeedback.listIncompletePrivacyRequests();
		} catch (Exception ex) {
			log.warn("Recoverable error: Error recalling PPN records: " + ex.getMessage());
		}

		try {
			if (internalUserFeedback != null)
				accessControlEvents = internalUserFeedback.listIncompleteAccessRequests();
			if(log.isDebugEnabled()) log.debug("Access Control Events Retrieved: " + String.valueOf(accessControlEvents.size()));
		} catch (Exception ex) {
			log.warn("Recoverable error: Error recalling AC records: " + ex.getMessage());
		}

		replaceCacheWithList(userFeedbackBeans, privacyNegotiationEvents, accessControlEvents);
	}

	private void replaceCacheWithList(List<UserFeedbackBean> ufList, List<UserFeedbackPrivacyNegotiationEvent> ppnList, List<UserFeedbackAccessControlEvent> acList) {
		synchronized (allNotifications) {
			synchronized (allNotificationIDs) {
				synchronized (unansweredNotifications) {
					synchronized (unansweredNotificationIDs) {
						if (log.isDebugEnabled())
							log.debug(String.format("Replacing cache with %s UF events, %s PPN events, %S AC events",
									ufList != null ? ufList.size() : 0,
											ppnList != null ? ppnList.size() : 0,
													acList != null ? acList.size() : 0));

						allNotifications.clear();
						unansweredNotifications.clear();
						allNotificationIDs.clear();
						unansweredNotificationIDs.clear();

						if (ufList != null)
							for (UserFeedbackBean uf : ufList) {
								NotificationQueueItem item = createNotificationQueueItemFromUserFeedbackBean(uf);
								addItemToQueue(item);
							}

						if (ppnList != null)
							for (UserFeedbackPrivacyNegotiationEvent ppn : ppnList) {
								NotificationQueueItem item = NotificationQueueItem.forPrivacyPolicyNotification(ppn.getRequestId(), ppn);
								addItemToQueue(item);
							}

						if (acList != null)
							for (UserFeedbackAccessControlEvent ac : acList) {
								if(log.isDebugEnabled()) log.debug(""+ac.getResponseItems().size());
								NotificationQueueItem item = NotificationQueueItem.forAccessControl(ac.getRequestId(), ac);
								addItemToQueue(item);
							}
					}
				}
			}
		}
	}

	private NotificationQueueItem createNotificationQueueItemFromUserFeedbackBean(UserFeedbackBean bean) {
		String proposalText = bean.getProposalText();
		String[] options = bean.getOptions().toArray(new String[bean.getOptions().size()]);

		NotificationQueueItem newItem;

		if (bean.getMethod() == FeedbackMethodType.GET_EXPLICIT_FB) {
			switch (bean.getType()) {
			case ExpProposalType.ACKNACK:
				// This is an AckNack notification
				newItem = NotificationQueueItem.forAckNack(bean.getRequestId(), proposalText, options);
				break;

			case ExpProposalType.CHECKBOXLIST:
				// This is a select-many notification
				newItem = NotificationQueueItem.forSelectMany(bean.getRequestId(), proposalText, options);
				break;

			case ExpProposalType.RADIOLIST:
				// This is a select-one notification
				newItem = NotificationQueueItem.forSelectOne(bean.getRequestId(), proposalText, options);

				break;

			default:
				log.error("Unknown UserFeedbackBean type = " + bean.getType());
				return null;
			}

		} else if (bean.getMethod() == FeedbackMethodType.GET_IMPLICIT_FB) {
			// This is a timed abort
			Date timeout = new Date(new Date().getTime() + (bean.getTimeout()*1000));

			newItem = NotificationQueueItem.forTimedAbort(bean.getRequestId(), proposalText, timeout);

		} else if (bean.getMethod() == FeedbackMethodType.SHOW_NOTIFICATION) {
			// This is a simple (no response required) notification

			newItem = NotificationQueueItem.forNotification(bean.getRequestId(), proposalText);

		} else {
			log.error("Cannot handle UserFeedbackBean with method " + bean.getMethod().toString());
			return null;
		}

		if (bean.getStage() == FeedbackStage.COMPLETED) {
			newItem.setComplete(true);
			newItem.setResults(options);
		}

		return newItem;
	}

	private void addItemToQueue(NotificationQueueItem item) {
		synchronized (allNotifications) {
			synchronized (allNotificationIDs) {
				if (allNotificationIDs.contains(item.getItemId())) {
					log.warn("NQI event ID " + item.getItemId() + " already in cache - ignoring");
					return;
				}

				if (log.isDebugEnabled())
					log.debug("Adding new NQI event ID [" + item.getItemId() + "] to cache");

				allNotificationIDs.add(item.getItemId());
				allNotifications.add(item);
				Collections.sort(allNotifications);

				if (!item.isComplete()) {

					if (log.isDebugEnabled())
						log.debug("NQI event ID [" + item.getItemId() + "] is not completed, adding to unanswered cache");

					synchronized (unansweredNotifications) {
						synchronized (unansweredNotificationIDs) {
							synchronized (unansweredAccessControlEvents) {
								synchronized (unansweredPrivacyNegotiationEvents) {

									unansweredNotificationIDs.add(item.getItemId());
									unansweredNotifications.add(item);
									Collections.sort(unansweredNotifications);
									if(item.getType()==NotificationQueueItem.TYPE_ACCESS_CONTROL)
									{
										unansweredAccessControlEvents.put(item.getAccessControlEvent().getRequestId(), item.getAccessControlEvent());
									}
									else if (item.getType()==NotificationQueueItem.TYPE_PRIVACY_POLICY_NEGOTIATION)
									{
										unansweredPrivacyNegotiationEvents.put(item.getPrivacyNeoEvent().getRequestId(), item.getPrivacyNeoEvent());
									}
								}
							}


						}

					}
				}
			}
		}
	}

	public boolean stopPoll() {
		synchronized(unansweredNotifications)
		{
			if(unansweredNotifications.size() > 0)
			{
				return false;
			}
			return true;
		}
	}

	private void markQueueItemComplete(String itemId, String[] results) {
		if (log.isDebugEnabled()) {
			String fmt = "Completing notification item ID %s";
			log.debug(String.format(fmt, itemId));
		}

		// NB: All incomplete notifications should be in the unanswered queue, and only incomplete ones should be in this queue
		synchronized (unansweredNotifications) {
			synchronized (unansweredNotificationIDs) {
				for (NotificationQueueItem nqi : allNotifications) {
					if (!nqi.getItemId().equals(itemId)) continue;

					if (log.isDebugEnabled()) {
						String fmt = "Removing notification item of type %s with ID %s";
						log.debug(String.format(fmt, nqi.getType(), itemId));
					}

					synchronized (nqi) {
						nqi.setResults(results);
						nqi.setComplete(true);
						unansweredNotifications.remove(nqi);
						unansweredNotificationIDs.remove(itemId);
					}

					break;
				}
			}
		}

		// remove any timed aborts
		synchronized (timedAbortsToWatch) {
			for (NotificationQueueItem nqi : timedAbortsToWatch) {
				if (!nqi.getItemId().equals(itemId)) continue;
				timedAbortsToWatch.remove(nqi);
				break;
			}
		}


	}


	public boolean isTimedAbortProcessorEnabled() {
		return timedAbortProcessor.isEnabled();
	}

	public void toggleTimedAbortProcessorEnabled() {
		boolean enabled = !this.timedAbortProcessor.isEnabled();
		this.timedAbortProcessor.setEnabled(enabled);

		log.info("timedAbortProcessor is now " + (enabled ? "ENABLED" : "DISABLED"));
	}

}
