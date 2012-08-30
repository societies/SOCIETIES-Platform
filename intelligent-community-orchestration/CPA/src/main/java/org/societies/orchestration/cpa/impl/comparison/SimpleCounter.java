package org.societies.orchestration.cpa.impl.comparison;

import java.util.List;

import org.societies.api.activity.IActivity;
import org.societies.orchestration.cpa.impl.SocialGraphVertex;


public class SimpleCounter implements ActorComparator {
	public SimpleCounter(){
		
	}
	@Override
	public double compare(SocialGraphVertex member1,SocialGraphVertex member2, List<IActivity> activityDiff) {
		double ret = 0;
		for(IActivity act: activityDiff){
			if(contains(member1,act) && contains(member2,act)){
				//add new link (or add weight to an old link)
				ret += 1.0;
			}
				
		}
		
		return ret;
	}

	public boolean contains(SocialGraphVertex participant, IActivity act){
		if(act.getActor().contains(participant.getName()))
			return true;
		if(act.getObject().contains(participant.getName()))
			return true;
		if(act.getTarget().contains(participant.getName()))
			return true;
		return false;
	}
	
}
