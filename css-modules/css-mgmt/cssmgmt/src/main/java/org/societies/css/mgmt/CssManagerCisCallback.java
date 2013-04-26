package org.societies.css.mgmt;


import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.management.ICisManagerCallback;
import org.societies.api.schema.cis.community.CommunityMethods;
import org.societies.api.schema.cis.community.Participant;


public class CssManagerCisCallback {

	private static Logger log = LoggerFactory.getLogger(CssManagerCisCallback.class);
	
	
	public CountDownLatch cisManagerCallbackSignal;
	public boolean bResponseReceived ;
	public List<Participant> memberList;

	public CssManagerCisCallback() {
		super();
		cisManagerCallbackSignal = new CountDownLatch(1);
		bResponseReceived = false;
		memberList = null;
	}





	// callback
	ICisManagerCallback iCisManagerCallback = new ICisManagerCallback() {

		
		@Override
		public void receiveResult(CommunityMethods communityResultObject) {
			if (communityResultObject != null) {
				if(communityResultObject.getWhoResponse() != null){
					if (communityResultObject.getWhoResponse().getParticipant() != null)
					{
						log.info("Got response to getmembers " + communityResultObject.getWhoResponse().getParticipant().size());
						memberList = communityResultObject.getWhoResponse().getParticipant();
					}
					else
					{
						log.info("Got response to getmembers : error");
					}
					bResponseReceived = true;
					cisManagerCallbackSignal.countDown();
							
				}
			}
				
		}


	};
	
}