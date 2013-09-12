package org.societies.cis.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.management.ICisManagerCallback;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.schema.cis.community.CommunityMethods;
import org.societies.api.schema.cis.community.LeaveResponse;

public class LeaveCallBackToAndroid implements ICisManagerCallback {

	IIdentity returnAddress;
	ICommManager icom;
	String targetCommunityJid;
	
	private static Logger LOG = LoggerFactory
			.getLogger(CisManager.class);
	
	public LeaveCallBackToAndroid(IIdentity returnAddress, ICommManager icom, String targetCommunityJid){
		this.returnAddress = returnAddress;
		this.icom = icom;
		this.targetCommunityJid = targetCommunityJid;
	}

	@Override
	public void receiveResult(CommunityMethods communityResultObject) {
		CommunityMethods resp;
		if(communityResultObject == null || communityResultObject.getLeaveResponse() == null){
			LOG.info("null return on Leave Callback");
			resp = new CommunityMethods();
			LeaveResponse r = new LeaveResponse();
			r.setResult(false);
			r.setCommunityJid(targetCommunityJid);
			resp.setLeaveResponse(r);
		}
		else{
			LOG.info("Result Status: left CIS " + communityResultObject.getLeaveResponse().isResult());
			resp = communityResultObject;
		}
		
		sendXMPPmessage(resp);
		
	}
	
	public void sendXMPPmessage(Object payload){

		
		LOG.info("finished building leave response XMPP message to android");

		Stanza sta = new Stanza(returnAddress);
		try {
			icom.sendMessage(sta, payload);
		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		LOG.info("notification sent to android");
	}

}
