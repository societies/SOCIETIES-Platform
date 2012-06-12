package org.societies.useragent.decisionmaking;

import java.util.List;
import java.util.Set;

import org.societies.api.internal.personalisation.model.IOutcome;

public class DecisionMakingCallback{

	private AbstractDecisionMaker maker;
	private IOutcome intent;
	private Set<IOutcome> preference;
	
	public DecisionMakingCallback(AbstractDecisionMaker maker,
			IOutcome intent,
			Set<IOutcome> preference) {
		super();
		this.maker = maker;
		this.intent=intent;
		this.preference=preference;
	}

	public void handleExpFeedback(List<String> feedback) {
		// TODO Auto-generated method stub
		if(feedback.get(0).equals(intent.toString()))
			maker.implementIAction(intent);
		else {
			for(IOutcome iou:preference){
				if(iou.toString().equals(feedback.get(0)))
					maker.implementIAction(iou);
			}
		}
	}

	public void handleImpFeedback(Boolean feedback) {
		// TODO Auto-generated method stub
		
	}

}
