package org.societies.activity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.activity.IActivityFeedCallback;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.schema.activityfeed.MarshaledActivityFeed;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class ActivityFeedCallback implements ICommCallback {

	
	private final static List<String> NAMESPACES = Collections
			.unmodifiableList( Arrays.asList("http://societies.org/api/schema/activityfeed"));
	private final static List<String> PACKAGES = Collections
			.unmodifiableList( Arrays.asList("org.societies.api.schema.activityfeed"));
	
	
	private IActivityFeedCallback sourceCallback = null;
	private static Logger LOG = LoggerFactory.getLogger(ActivityFeedCallback.class);
	
	public ActivityFeedCallback (String clientId, IActivityFeedCallback sourceCallback) {
		this.sourceCallback = sourceCallback;
	}
	
	@Override
	public List<String> getXMLNamespaces() {
		return NAMESPACES;
	}

	@Override
	public List<String> getJavaPackages() {
		return PACKAGES;
	}

	@Override
	public void receiveResult(Stanza stanza, Object payload) {
		LOG.info("receive result received");
		// community namespace
		if (payload instanceof MarshaledActivityFeed) {
			LOG.info("Callback with result");
            MarshaledActivityFeed a = (MarshaledActivityFeed) payload ;
			
			if(a.getDeleteActivityResponse() != null) {
				LOG.info("Delete Activity Response received and equal to " + a.getDeleteActivityResponse().isResult());
            }
			if(a.getAddActivityResponse() != null) {
				LOG.info("Add Activity Response received and equal to " + a.getAddActivityResponse().isResult());
            }
			if(a.getCleanUpActivityFeedResponse() != null) {
				LOG.info("CleanUp Activity Feed Response received and equal to " + a.getCleanUpActivityFeedResponse().getResult());	
            }
			if(a.getGetActivitiesResponse() !=null) {
				LOG.info("Get Activities Response received");
            }
			
		}
		

		// return callback for all cases
		this.sourceCallback.receiveResult((MarshaledActivityFeed)payload);

	}

	@Override
	public void receiveError(Stanza stanza, XMPPError error) {

	}

	@Override
	public void receiveInfo(Stanza stanza, String node, XMPPInfo info) {

	}

	@Override
	public void receiveItems(Stanza stanza, String node, List<String> items) {

	}

	@Override
	public void receiveMessage(Stanza stanza, Object payload) {

	}

}
