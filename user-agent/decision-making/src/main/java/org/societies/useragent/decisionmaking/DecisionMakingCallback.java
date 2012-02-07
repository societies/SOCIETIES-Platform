package org.societies.useragent.decisionmaking;

import java.util.List;
import java.util.Set;

import org.societies.api.internal.personalisation.model.IOutcome;
import org.societies.api.internal.useragent.feedback.IUserFeedbackCallback;

public class DecisionMakingCallback implements IUserFeedbackCallback{

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

	@Override
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

	@Override
	public void handleImpFeedback(Boolean feedback) {
		// TODO Auto-generated method stub
		
	}

}
