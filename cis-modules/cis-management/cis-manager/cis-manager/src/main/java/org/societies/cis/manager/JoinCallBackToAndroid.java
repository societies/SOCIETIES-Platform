package org.societies.cis.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.management.ICisManagerCallback;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.schema.cis.community.Community;
import org.societies.api.schema.cis.community.CommunityMethods;
import org.societies.api.schema.cis.community.JoinResponse;

public class JoinCallBackToAndroid implements ICisManagerCallback {

			
		IIdentity returnAddress;
		ICommManager icom;
		String targetCommunityJid;
		
		private static Logger LOG = LoggerFactory
				.getLogger(CisManager.class);
		
		public JoinCallBackToAndroid(IIdentity returnAddress, ICommManager icom, String targetCommunityJid){
			this.returnAddress = returnAddress;
			this.icom = icom;
			this.targetCommunityJid = targetCommunityJid;
		}

		@Override
		public void receiveResult(CommunityMethods communityResultObject) {
			CommunityMethods resp;
			if(communityResultObject == null || communityResultObject.getJoinResponse() == null){
				LOG.info("null return on JoinCallBack");
				resp = new CommunityMethods();
				JoinResponse r = new JoinResponse();
				r.setResult(false);
				Community c = new Community();
				c.setCommunityJid(targetCommunityJid);
				r.setCommunity(c);
				resp.setJoinResponse(r);
			}
			else{
				LOG.info("Result Status: joined CIS " + communityResultObject.getJoinResponse().isResult());
				resp = communityResultObject;
			}
			
			sendXMPPmessage(resp);
			
		}
		
		public void sendXMPPmessage(Object payload){

			
			LOG.info("finished building join response XMPP message to android");

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
