package org.societies.cis.manager;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.management.ICisManagerCallback;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.schema.cis.community.CommunityMethods;



public class CisManagerClientCallback implements ICommCallback {

	//MAP TO STORE THE ALL THE CLIENT CONNECTIONS
	 private final Map<String, ICisManagerCallback> clients = new HashMap<String, ICisManagerCallback>();
	

	private final static List<String> NAMESPACES = Collections
			.unmodifiableList( Arrays.asList("http://societies.org/api/schema/cis/manager",
//								"http://societies.org/api/schema/activity",
						  		"http://societies.org/api/schema/cis/community"));
	private final static List<String> PACKAGES = Collections
			.unmodifiableList( Arrays.asList("org.societies.api.schema.cis.manager",
//					"org.societies.api.schema.activity",
					"org.societies.api.schema.cis.community"));
	
	private static Logger LOG = LoggerFactory.getLogger(CisManagerClientCallback.class);
	
	private CisManager cisManag;
	
	public CisManagerClientCallback (String clientId, ICisManagerCallback sourceCallback,CisManager cisManag) {
		clients.put(clientId, sourceCallback);
		
		this.cisManag = cisManag;
	}
	
	/**Returns the correct client callback for this request
	 * @param requestID the id of the initiating request
	 * @return
	 * @throws UnavailableException
	 */
	private ICisManagerCallback getRequestingClient(String requestID) {
		ICisManagerCallback requestingClient = (ICisManagerCallback) clients.get(requestID);
		clients.remove(requestID);
		return requestingClient;
	}
	
	
	@Override
	public List<String> getXMLNamespaces() {	
		return this.NAMESPACES;
	}

	@Override
	public List<String> getJavaPackages() {
		return this.PACKAGES;
	}

	@Override
	public void receiveResult(Stanza stanza, Object payload) {
		
		LOG.info("receive result received");
		// community namespace
		if (payload instanceof CommunityMethods) {
			LOG.info("Callback with result");
			CommunityMethods c = (CommunityMethods) payload ;
			
			
			// join response
			if(c.getJoinResponse() != null){
				LOG.debug("Join response received");
				if(c.getJoinResponse().isResult() && c.getJoinResponse().getCommunity() != null){
					// updates the list of CIS where I belong
					cisManag.subscribeToCis(new CisRecord(c.getJoinResponse().getCommunity().getCommunityName(),
														  c.getJoinResponse().getCommunity().getCommunityJid(),
														  c.getJoinResponse().getCommunity().getOwnerJid(),
														  c.getJoinResponse().getCommunity().getDescription(),
														  c.getJoinResponse().getCommunity().getCommunityType()
														  ));
					LOG.debug("subscription worked");
					if(null != cisManag.getiUsrFeedback()){
						if(cisManag.isPrivacyPolicyNegotiationIncluded())
							cisManag.getiUsrFeedback().showNotification("join completed");
						//LOG.debug("usr feedback component called on join");
					}
	
				}
				else{ // there is no result field
					LOG.warn("join failed =S");
					if(null != cisManag.getiUsrFeedback()){
						cisManag.getiUsrFeedback().showNotification("join failed");
					}
				}
			}
			// end of join response
			
			
			// leave response
			if(c.getLeaveResponse() != null){
				LOG.debug("Leave response received");
				if(c.getLeaveResponse().isResult() ){
					// updates the list of CIS where I belong
					if (!cisManag.unsubscribeToCis(stanza.getFrom().getBareJid()))
						LOG.debug("unsubscription did not worked");
						LOG.debug("unsubscription worked");

					
				}
				else{ // there is no result field
					LOG.warn("unsubscription response was mallformed");

				}
			}
			// end of join response

			// get info response
			if(c.getGetInfoResponse() != null){
				LOG.debug("Get info response received");
				if(c.getGetInfoResponse().isResult()  ){
					LOG.debug("get info arrived fine");
	
				}
				else{ // there is no result field
					LOG.warn("get info failed");

				}
			}

			// return callback for all cases
			ICisManagerCallback callback = this.getRequestingClient(stanza.getId());
			callback.receiveResult(c);

			
			
			
			
		}
	}

	@Override
	public void receiveError(Stanza stanza, XMPPError error) {
		// TODO Auto-generated method stub
		LOG.info("receive error on CisManagerClient" +error.getMessage());

	}

	@Override
	public void receiveInfo(Stanza stanza, String node, XMPPInfo info) {
		// TODO Auto-generated method stub
		LOG.info("receive info on CisManagerClient" + info.getIdentityName());

	}

	@Override
	public void receiveItems(Stanza stanza, String node, List<String> items) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receiveMessage(Stanza stanza, Object payload) {
		// TODO Auto-generated method stub

	}

}
