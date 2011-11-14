package org.societies.conflict.api;

import java.util.Map;

import org.societies.personalisation.common.api.model.*;
import org.societies.conflict.api.model.ConflictResolutionRule;
import org.societies.conflict.api.model.ConflictType;
import org.societies.feedback.api.*;

public interface IConflictResolutionManagerImp extends IConflictResolutionManager{
	/* the inner methods  
	 * instances
	 * @author haoyi.xiong@it-sudparis.eu*/
	void addConflictResolutionRules
		(Map<ConflictType, ConflictResolutionRule> rule);
	/*add a rule*/
	Map<ConflictType, ConflictResolutionRule> getConflictResolutionRules();
	/*search a rule*/
	IUserFeedback getUserFeedback(IAction action$1,
			IAction action$2);
	/*got feedback when it has no idea*/
	IAction getUserIntent();
	/* get user intent*/
	IAction getUserPreference();
	/* get user preference*/
	void setUserIntent(IAction intentaction);
	/* set user intent*/
	void setUserPreference(IAction preferaction);
	/* set user preference*/
}