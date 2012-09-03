package org.societies.orchestration.cpa.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import org.societies.activity.ActivityFeed;
import org.societies.api.activity.IActivity;
import org.societies.api.activity.IActivityFeed;
import org.societies.api.cis.attributes.MembershipCriteria;
import org.societies.api.cis.management.ICisManagerCallback;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.cis.management.ICisParticipant;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.schema.cis.community.Community;

public class ICISSimulated implements ICisOwned {
	private ArrayList<String> members;
	public ArrayList<String> getMembers() {
		return members;
	}

	public void setMembers(ArrayList<String> members) {
		this.members = members;
	}

	public ActivityFeed getFeed() {
		return feed;
	}

	public void setFeed(ActivityFeed feed) {
		this.feed = feed;
	}
	private ActivityFeed feed=null;
	public ICISSimulated() {
		members = new ArrayList<String>();
		feed = new ActivityFeed();
	}

	@Override
	public String getCisId() {
		// TODO Auto-generated method stub
		return "1";
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "name";
	}

    @Override
    public void getMembershipCriteria(ICisManagerCallback callback) {
    }

    @Override
	public void getInfo(ICisManagerCallback callback) {
		// TODO Auto-generated method stub

	}

	@Override
	public void getListOfMembers(ICisManagerCallback callback) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setInfo(Community c, ICisManagerCallback callback) {
		// TODO Auto-generated method stub

	}

//	@Override
//	public void addActivity(IActivity activity, ICisManagerCallback callback) {
//		this.feed.addActivity(activity);
//
//	}
//
//	@Override
//	public void getActivities(String timePeriod, ICisManagerCallback callback) {
//		//nope
//
//	}


	@Override
	public Future<Set<ICisParticipant>> getMemberList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<Boolean> addMember(String jid, String role)
			throws CommunicationException {
		final boolean ret = this.members.add(jid);
		return new FutureTask<Boolean>(new Callable<Boolean>() {
		@Override
		public Boolean call() throws Exception {
			// TODO Auto-generated method stub
			return ret;
		}});
	}

	@Override
	public Future<Boolean> removeMemberFromCIS(String jid)
			throws CommunicationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getOwnerId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCisType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String setCisType(String type) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDescription(String description) {
		// TODO Auto-generated method stub

	}

    @Override
    public boolean checkQualification(HashMap<String, String> qualification) {
        return false;
    }

    @Override
    public boolean addCriteria(String contextAtribute, MembershipCriteria m) {
        return false;
    }

    @Override
    public boolean removeCriteria(String contextAtribute, MembershipCriteria m) {
        return false;
    }

    @Override
	public IActivityFeed getActivityFeed() {
		// TODO Auto-generated method stub
		return feed;
	}
	public List<String> getUsers(){
		return this.members;
	}
}
