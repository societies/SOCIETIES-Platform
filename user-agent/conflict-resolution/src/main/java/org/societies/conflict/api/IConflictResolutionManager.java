package org.societies.conflict.api;

import org.societies.personalisation.common.api.model.*;

public interface IConflictResolutionManager{
	/*conflict resolved between two IAction 
	 * instances
	 * @author haoyi.xiong@it-sudparis.eu*/
	IAction resolveConflict(IAction intentaction
			,IAction preferaction);
	
}