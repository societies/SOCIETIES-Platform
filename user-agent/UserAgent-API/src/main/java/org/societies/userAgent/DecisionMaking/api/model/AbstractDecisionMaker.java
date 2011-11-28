package org.societies.userAgent.DecisionMaking.api.model;

import java.util.List;

import org.societies.userAgent.ConflictResolution.api.IConflictResolutionManager;
import org.societies.userAgent.ConflictResolution.api.model.ConflictType;
import org.societies.userAgent.DecisionMaking.api.IDecisionMaker;
import org.societies.userAgent.UserFeedback.api.IUserFeedback;
import org.societies.userAgent.UserFeedback.api.model.IProposal;
import org.societies.userAgent.mock.api.model.IAction;
import org.societies.userAgent.mock.api.model.IOutcome;

public abstract class AbstractDecisionMaker implements IDecisionMaker {
	IConflictResolutionManager manager;
	IUserFeedback feedbackHandler;
	
	public IConflictResolutionManager getManager() {
		return manager;
	}

	public void setManager(IConflictResolutionManager manager) {
		this.manager = manager;
	}

	public IUserFeedback getFeedbackHandler() {
		return feedbackHandler;
	}

	public void setFeedbackHandler(IUserFeedback feedbackHandler) {
		this.feedbackHandler = feedbackHandler;
	}

	public static class IntentVsPreferenceProposal implements IProposal{
		IOutcome preference;
		IOutcome intent;
		
		public IntentVsPreferenceProposal(IOutcome preference,
		IOutcome intent){
			this.preference=preference;
			this.intent=intent;
		}
		@Override
		public String getText() {
			// TODO Auto-generated method stub
			return "Your intent is:"+intent.toString()+
					" BUT Your preference is"+preference.toString();
		}

		@Override
		public String getOptions() {
			// TODO Auto-generated method stub
			return "Intent or Prefernce";
		}
		
	}

	@Override
	public void makeDecision(List<IOutcome> intents, List<IOutcome> preferences) {
		// TODO Auto-generated method stub
		for (IOutcome intent : intents) {
			IAction action=intent;
			for (IOutcome preference : preferences) {
				ConflictType conflict = detectConflict(intent, preference);
				if (conflict == ConflictType.PREFERNCE_INTENT_NOT_MATCH) {
					action = manager.resolveConflict(action,preference);
					if(action ==null){
						if(feedbackHandler.getExplicitFB(
								new IntentVsPreferenceProposal(intent,preference))){
							action=intent;
							/*return true for intent false for preference*/
						}else{
							action=preference;
						}
					}
				}else if (conflict==ConflictType.UNKNOWN_CONFLICT){
					/*handler the unknown work*/
				}
			}
			this.implementIAction(action);
		}
	}

	protected abstract ConflictType detectConflict(IOutcome intent,
			IOutcome prefernce);

	protected abstract void implementIAction(IAction action);

}
