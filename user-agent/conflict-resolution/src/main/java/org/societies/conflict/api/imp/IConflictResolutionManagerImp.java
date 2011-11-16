package org.societies.conflict.api.imp;

import java.util.Map;

import org.societies.personalisation.common.api.model.*;
import org.societies.conflict.api.IConflictResolutionManager;
import org.societies.conflict.api.model.ConflictResolutionRule;
import org.societies.conflict.api.model.ConflictType;
import org.societies.feedback.api.*;

public interface IConflictResolutionManagerImp extends IConflictResolutionManager{
	/* the inner methods  
	 * instances
	 * @author haoyi.xiong@it-sudparis.eu*/
	void addConflictResolutionRules
		(Map<ConflictType, ConflictResolutionRule> rule);
	/*add a rule
	 * @param rule, the rule to add*/
	Map<ConflictType, ConflictResolutionRule> getConflictResolutionRules();
	/*search a rule*/
	IUserFeedback getUserFeedback(IAction action_1,
			IAction action_2);
	/*got feedback when it has no idea between
	 * @param action_1
	 * @param action_2*/
	IAction getUserIntent();
	/* get user intent*/
	IAction getUserPreference();
	/* get user preference*/
	void setUserIntent(IAction intentaction);
	/* set user intent
	 * @param intentaction, action by intent*/
	void setUserPreference(IAction preferaction);
	/* set user preference
	 * @param intentaction, action by preference*/
}