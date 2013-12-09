package org.societies.display.client;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.css.devicemgmt.display.IDisplayDriver;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.internal.useragent.model.*;

public class NotificationControl implements Runnable  {
	
	private DisplayPortalClient displayClient;
	private IUserFeedback userFeedback;
	private String location;
	private String[] answers;
	private final String uuid;
	
	private static Logger log = LoggerFactory.getLogger(NotificationControl.class);
	
	public NotificationControl(String uuid, DisplayPortalClient displayClient, IUserFeedback userFeedback, String location)
	{
		this.uuid = uuid;
		this.displayClient = displayClient;
		this.userFeedback = userFeedback;
		this.location=location;
		answers = new String[2];
		answers[0]="Yes";
		answers[1]="No";
	}

	public void sendStartNotification()
	{
		List<String> feedback = new ArrayList<String>();
		try {
			if(log.isDebugEnabled()) log.debug("SENDING NOTIFICATION");
			feedback = this.userFeedback.getExplicitFB(ExpProposalType.ACKNACK, new ExpProposalContent("Do you want to start a session with "+ this.location+"?", answers)).get();
		} catch (InterruptedException e) {
			log.error("ERROR", e);
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			log.error("ERROR", e);
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(log.isDebugEnabled()) log.debug(feedback.toString());
		if(feedback.get(0).equals("Yes"))
		{
			displayClient.sendStartSessionRequest(this.location);
		}
		else
		{
			displayClient.acknowledgeRefuse(this.location);
		}
	}
	
	
	@Override
	public void run() {
		sendStartNotification();
	}

	public String getUuid() {
		return uuid;
	}

}
