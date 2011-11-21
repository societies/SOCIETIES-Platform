package org.societies.user-agent.ConflictResolution.api;

import org.societies.personalisation.common.api.model.*;

public interface IConflictResolutionManager{
	/*conflict resolved between two IAction 
	 * instances
	 * @author haoyi.xiong@it-sudparis.eu*/
	IAction resolveConflict(IAction intentaction
			,IAction preferaction);
	/*resolve conflict between intent and preference
	 * @param intentaction, the action by intent
	 * @param preferaction, the action by preference*/
}