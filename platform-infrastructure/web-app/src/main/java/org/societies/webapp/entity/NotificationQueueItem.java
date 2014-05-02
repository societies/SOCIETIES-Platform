package org.societies.webapp.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.schema.useragent.feedback.UserFeedbackAccessControlEvent;
import org.societies.api.internal.schema.useragent.feedback.UserFeedbackPrivacyNegotiationEvent;
import org.societies.api.schema.identity.RequestorCisBean;
import org.societies.api.schema.identity.RequestorServiceBean;

import java.io.Serializable;
import java.util.Date;

public class NotificationQueueItem implements Serializable, Comparable<NotificationQueueItem> {
	protected static final Logger log = LoggerFactory.getLogger(NotificationQueueItem.class);

	public static final String TYPE_PRIVACY_POLICY_NEGOTIATION = "PPN";
	public static final String TYPE_ACCESS_CONTROL = "AC";
	public static final String TYPE_TIMED_ABORT = "TIMED_ABORT";
	public static final String TYPE_ACK_NACK = "ACK_NACK";
	public static final String TYPE_SELECT_ONE = "SELECT_ONE";
	public static final String TYPE_SELECT_MANY = "SELECT_MANY";
	public static final String TYPE_NOTIFICATION = "NOTIFICATION";
	public static final String TYPE_UNKNOWN = "UNKNOWN";

	public static NotificationQueueItem forPrivacyPolicyNotification(String itemId, UserFeedbackPrivacyNegotiationEvent payload) {
		return new NotificationQueueItem(itemId, payload);
	}

	public static NotificationQueueItem forAccessControl(String itemId, UserFeedbackAccessControlEvent bean) {
		return new NotificationQueueItem(itemId, bean);
	}

	public static NotificationQueueItem forTimedAbort(String itemId, String title, Date timeout) {
		return new NotificationQueueItem(itemId, title, timeout);
	}

	public static NotificationQueueItem forAckNack(String itemId, String title, String[] options) {
		return new NotificationQueueItem(itemId, TYPE_ACK_NACK, title, options);
	}

	public static NotificationQueueItem forSelectOne(String itemId, String title, String[] options) {
		return new NotificationQueueItem(itemId, TYPE_SELECT_ONE, title, options);
	}

	public static NotificationQueueItem forSelectMany(String itemId, String title, String[] options) {
		return new NotificationQueueItem(itemId, TYPE_SELECT_MANY, title, options);
	}

	public static NotificationQueueItem forNotification(String itemId, String title) {
		if(log.isDebugEnabled()) log.debug("GETTING NEW NOTIF");
		try{
			return new NotificationQueueItem(itemId, TYPE_NOTIFICATION, title, null);
		}catch(Exception e){log.error("ERROR>", e);}
		return null;
	}

	private final Date arrivalDate;

	private final UserFeedbackPrivacyNegotiationEvent ufPPN;
	private final UserFeedbackAccessControlEvent ufAccessControl;
	private final String title;
	private final String itemId;
	private final String type;
	private final String[] options;
	private final Date timeoutTime;
	private String[] results;
	private boolean complete;

	private NotificationQueueItem(String itemId, UserFeedbackPrivacyNegotiationEvent payload) {
		this.arrivalDate = payload.getRequestDate();
		this.itemId = itemId;
		this.ufPPN = payload;
		this.ufAccessControl = null;
		this.options = new String[0];
		this.type = TYPE_PRIVACY_POLICY_NEGOTIATION;
		//this.title = payload.getNegotiationDetails().getRequestor().getRequestorId();
		this.timeoutTime = null;
		this.complete = false;

		String titleString = "Privacy Policy Negotiation with ";
		
		if(payload.getNegotiationDetails().getRequestor() instanceof RequestorCisBean) {
			titleString = titleString + "CIS: " + ((RequestorCisBean) payload.getNegotiationDetails().getRequestor()).getCisRequestorId();
		} else if(payload.getNegotiationDetails().getRequestor() instanceof RequestorServiceBean) {
			titleString = titleString + "Service: " + ((RequestorServiceBean) payload.getNegotiationDetails().getRequestor()).getRequestorServiceId().getServiceInstanceIdentifier();
		} else {
			titleString = titleString + "CSS: " + payload.getNegotiationDetails().getRequestor().getRequestorId();
		}
		this.title = titleString;
	}

	private NotificationQueueItem(String itemId, UserFeedbackAccessControlEvent payload) {
		this.arrivalDate = payload.getRequestDate();
		this.itemId = itemId;
		this.ufPPN = null;
		this.ufAccessControl = payload;
		this.options = new String[0];
		this.type = TYPE_ACCESS_CONTROL;
		//this.title = "Access Control Request";
		this.timeoutTime = null;
		this.complete = false;

		String titleString = "User Access Control Request from ";
		if(payload.getRequestor() instanceof RequestorCisBean) {
			titleString = titleString + "CIS: " + ((RequestorCisBean) payload.getRequestor()).getCisRequestorId();
		} else if(payload.getRequestor() instanceof RequestorServiceBean) {
			titleString = titleString + "Service: " + ((RequestorServiceBean) payload.getRequestor()).getRequestorServiceId().getServiceInstanceIdentifier();
		} else {
			titleString = titleString + "CSS: " + payload.getRequestor().getRequestorId();
		}
		
		this.title = titleString;
	}

	private NotificationQueueItem(String itemId, String title, Date timeout) {
		this.arrivalDate = new Date();
		this.itemId = itemId;
		this.ufPPN = null;
		this.ufAccessControl = null;
		this.options = new String[0];
		this.type = TYPE_TIMED_ABORT;
		this.title = title;
		this.timeoutTime = timeout;
		this.complete = false;
	}

	private NotificationQueueItem(String itemId, String type, String title, String[] options) {
		this.arrivalDate = new Date();
		this.itemId = itemId;
		this.ufPPN = null;
		this.ufAccessControl = null;
		this.options = options;
		this.type = type;
		this.title = title;
		this.timeoutTime = null;
		this.complete = false;
	}

	public Date getArrivalDate() {
		return arrivalDate;
	}

	public String getItemId() {
		return itemId;
	}

	public String getTitle() {
		return title;
	}

	public UserFeedbackAccessControlEvent getAccessControlEvent()
	{
		return ufAccessControl;
	}

	public UserFeedbackPrivacyNegotiationEvent getPrivacyNeoEvent()
	{
		return ufPPN;
	}

	public String getInfoLink() {
		if (ufPPN != null)
			return "privacy_policy_negotiation.xhtml?id=" + itemId;
		if (ufAccessControl != null)
			return "access_control.xhtml?id=" + itemId;

		return "";
	}


	public String getType() {
		return type;
	}

	public String[] getOptions() {
		return options;
	}

	public Date getTimeoutTime() {
		return timeoutTime;
	}

	public String getSubmitResult()
	{
		if(null == results || results.length == 0 || null == results[0] )
		{
			if  ((this.options!=null || this.options.length>0) && this.options[0]!=null)
			{
				return options[0];
			}
			else{
				return "";
			}
		}  


		return results[0];
	}

	public String getResult() {
		if(this.type.equals(TYPE_ACK_NACK))
		{
			return null;
		}

		if(null == results || results.length == 0 || null == results[0] )
		{
			if  ((this.options!=null || this.options.length>0) && this.options[0]!=null)
			{
				return options[0];
			}
			else{
				return "";
			}
		}  


		return results[0];

	}


	public void setResult(String result) {

		if(null==result || result.isEmpty())
		{
			if(null!=options && null!=options[0])
			{
				this.results = new String[]{options[0]};
			}
			else
			{
				this.results = new String[]{""};
			}
		}
		else
		{
			this.results = new String[]{result};
		}
	}

	public String[] getResults() {
		return results;
	}

	public void setResults(String[] results) {
		this.results = results;
	}

	public String getFriendlyTimeLeft() {
		if (timeoutTime == null)
			return "forever";

		long seconds = (timeoutTime.getTime() - new Date().getTime()) / 1000L;
		if (seconds < 0L)
			seconds = 0;

		// hours:minutes
		if (seconds > 3600) {
			long minutes = ((seconds % 3600L) - (seconds % 60L)) / 60L;
			long hours = (seconds - (seconds % 3600L)) / 3600L;

			return hours + "hrs, " + minutes + "mins";
		}

		// minutes:seconds
		if (seconds > 60) {
			long minutes = (seconds - (seconds % 60L)) / 60L;

			return minutes + "mins, " + (seconds % 60L) + "sec";
		}

		// seconds only
		return seconds + "sec";
	}

	public void setComplete(boolean complete) {
		this.complete = complete;
	}

	public boolean isComplete() {
		return complete;
	}

	@Override
	public int compareTo(NotificationQueueItem that) {

		if (this.isComplete() && !that.isComplete())
			return 1;
		if (!this.isComplete() && that.isComplete())
			return -1;

		return this.arrivalDate.compareTo(that.arrivalDate);
	}

}
