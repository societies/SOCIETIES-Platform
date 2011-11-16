package org.societies.decision.api.imp;

import org.societies.decision.api.IDecisionMaker;
import org.societies.personalisation.common.api.model.*;

public interface IDecisionMakerImp extends IDecisionMaker{
	/* the implementation inteface for decision maker
	 * two more methods should be implemented
	 * (1)detect the confilict between two IOutcome instances
	 * referring to the preference and intention
	 * (2)implementation of decision is to
	 * involke corresponding services by IAction.
	 * @author haoyi.xiong@it-sudparis.eu*/
	boolean detectConflict(IOutcome intent,
			IOutcome preference);
	/*detect conflict between intent and preference
	 * @param intent, the instance of IOutcome referring intent of user
	 * @param preference, the instance of IOutcome referring preference of user*/
	void implementDecision(IAction action);
	/*implement the decision by corresponding IAction
	 * @param action, the instance of IAction given by decision maker
	 * which identifies the result of decision making*/
}