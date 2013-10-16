package org.societies.useragent.decisionmaking;

import java.util.List;
import java.util.Set;

import org.societies.api.internal.personalisation.model.IOutcome;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.internal.personalisation.model.FeedbackEvent;
import org.societies.api.internal.personalisation.model.FeedbackTypes;
import org.societies.api.identity.IIdentity;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;


public class DecisionMakingCallback{

	private AbstractDecisionMaker maker;
	private IOutcome intent;
	private Set<IOutcome> preference;
	private String uuid;
	
	public DecisionMakingCallback(AbstractDecisionMaker maker,
			IOutcome intent,
			Set<IOutcome> preference,
			String uuid) {
		super();
		this.maker = maker;
		this.intent=intent;
		this.preference=preference;
		this.uuid=uuid;
	}

	public void handleExpFeedback(List<String> feedback) {
		// TODO Auto-generated method stub
		if(feedback.get(0).equalsIgnoreCase(this.maker.getFriendlyName(intent))){
			FeedbackEvent fedb = new FeedbackEvent(maker.getEntityID(),
					intent, true, FeedbackTypes.USER_CHOICE);
			fedb.setUuid(uuid);
			InternalEvent event = new InternalEvent(EventTypes.UI_EVENT,
					"feedback", "org/societies/useragent/decisionmaker",
					fedb);
			try {
				maker.getEventMgr().publishInternalEvent(event);
			} catch (Exception e) {
				e.printStackTrace();
			}
//			maker.hasBeenChecked.add(intent);
			maker.implementIAction(intent,uuid);
		}else {
			for(IOutcome iou:preference){
				if(this.maker.getFriendlyName(iou).equalsIgnoreCase(feedback.get(0))){
					FeedbackEvent fedb = new FeedbackEvent(maker.getEntityID(),
							iou, true, FeedbackTypes.USER_CHOICE);
					fedb.setUuid(uuid);
					InternalEvent event = new InternalEvent(EventTypes.UI_EVENT,
							"feedback", "org/societies/useragent/decisionmaker",
							fedb);
					try {
						maker.getEventMgr().publishInternalEvent(event);
					} catch (Exception e) {
						e.printStackTrace();
					}
//					maker.hasBeenChecked.add(iou);
					maker.implementIAction(iou,uuid);
				}
			}
		}
	}

	public void handleImpFeedback(Boolean feedback) {
		// TODO Auto-generated method stub
		
	}

}
