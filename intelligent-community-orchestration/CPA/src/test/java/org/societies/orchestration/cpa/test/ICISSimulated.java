package org.societies.orchestration.cpa.test;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import org.societies.api.activity.IActivity;
import org.societies.api.activity.IActivityFeed;
import org.societies.api.cis.management.ICisManagerCallback;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.cis.management.ICisParticipant;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.schema.cis.community.Community;

public class ICISSimulated implements ICisOwned {
	ArrayList<String> members;
	ArrayList<IActivity> activities;
	public ICISSimulated() {
		// TODO Auto-generated constructor stub
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

	@Override
	public void addCisActivity(IActivity activity, ICisManagerCallback callback) {
		this.activities.add(activity);

	}

	@Override
	public void getActivities(String timePeriod, ICisManagerCallback callback) {
		//nope

	}

	@Override
	public Future<IActivityFeed> getCisActivityFeed() {
		// TODO Auto-generated method stub
		return null;
	}

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
	public int getMembershipCriteria() {
		// TODO Auto-generated method stub
		return 0;
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
	public IActivityFeed getActivityFeed() {
		// TODO Auto-generated method stub
		return null;
	}

}
