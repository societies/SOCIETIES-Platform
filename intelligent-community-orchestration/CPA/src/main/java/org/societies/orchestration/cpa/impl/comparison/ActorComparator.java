package org.societies.orchestration.cpa.impl.comparison;

import java.util.List;

import org.societies.api.activity.IActivity;
import org.societies.orchestration.cpa.impl.SocialGraphVertex;

public interface ActorComparator {
	public double compare(SocialGraphVertex member1,SocialGraphVertex member2, List<IActivity> activityDiff);
}
