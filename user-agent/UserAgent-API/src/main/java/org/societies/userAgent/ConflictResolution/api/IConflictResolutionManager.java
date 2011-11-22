package org.societies.userAgent.ConflictResolution.api;

//import org.societies.personalisation.common.api.model.*;
import org.societies.userAgent.mock.api.model.IAction;

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