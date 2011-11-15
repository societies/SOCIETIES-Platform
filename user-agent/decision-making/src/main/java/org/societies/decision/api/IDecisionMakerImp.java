package org.societies.decision.api;

import org.societies.personalisation.common.api.model.*;

public interface IDecisionMakerImp extends IDecisionMaker{
	/* the implementation inteface for decision maker
	 * two more methods should be implemented
	 * (1)detect the confilict between two IOutcome instances
	 * referring to the preference and intention
	 * (2)implementation of decision is to
	 * involke corresponding services by IAction.
	 * @author haoyi.xiong@it-sudparis.eu*/
	boolean detectConflict(IOutcome intent,IOutcome preference);
	void implementDecision(IAction action);
	
}