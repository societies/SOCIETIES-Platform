package org.societies.userAgent.DecisionMaking.api;

import java.util.List;

//import org.societies.personalisation.common.api.model.*;
import org.societies.userAgent.mock.api.model.IOutcome;

public interface IDecisionMaker{
	/*  make decision upon the list of 
	 *  preferences and the list of
	 *  intentions.
	 *  this interface mainly provides a
	 *  outside view for decision maker
	 *  @author haoyi.xiong@it-sudparis.eu*/
	void makeDecision(List<IOutcome> intents,List<IOutcome> preferences);
	/*@param intents, the list of IOutcome instances referring intents
	 *@param preferences, the list of IOutcome instances referring preferences */
}