package org.societies.orchestration.sca.comms;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.orchestration.ICommunitySuggestion;
import org.societies.api.schema.orchestration.sca.scasuggestedcisbean.SCASuggestedBean;
import org.societies.orchestration.sca.api.ISCARemote;

public class SCACommsClient implements ICommCallback, ISCARemote {

	private static Logger log = LoggerFactory.getLogger(SCACommsClient.class);


	private ICommManager commManager;

	private static final List<String> NAMESPACES = Collections.unmodifiableList(Arrays.asList(
			"http://societies.org/api/schema/orchestration/sca/scasuggestedcisbean"
			));

	private static final List<String> PACKAGES = Collections.unmodifiableList(Arrays.asList(
			"org.societies.api.schema.orchestration.sca.scasuggestedcisbean"
			));

	public SCACommsClient(ICommManager commManager) {
		this.commManager = commManager;
		try {
			this.commManager.register(this);
		} catch (CommunicationException e) {
			e.printStackTrace();
		}
		log.debug("Registered as IFeatureServer");
	}

	@Override
	public List<String> getJavaPackages() {
		// TODO Auto-generated method stub
		return PACKAGES;
	}

	@Override
	public List<String> getXMLNamespaces() {
		// TODO Auto-generated method stub
		return NAMESPACES;
	}

	@Override
	public void receiveError(Stanza arg0, XMPPError arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receiveInfo(Stanza arg0, String arg1, XMPPInfo arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receiveItems(Stanza arg0, String arg1, List<String> arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receiveMessage(Stanza arg0, Object arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receiveResult(Stanza arg0, Object arg1) {
		// TODO Auto-generated method stub
		log.debug("I have recieved my result!");
		//	log.debug((String) arg1); 

	}

	@Override
	public void sendJoinSuggestion(String userJID,
			String suggestedCIS) {

		IIdentity userID = null;
		try {
			userID = this.commManager.getIdManager().fromJid(userJID);
		} catch (InvalidFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if(null!=userID) {
			SCASuggestedBean bean = new SCASuggestedBean();
			bean.setCisID(suggestedCIS);
			bean.setSuggestedType("join");

			Stanza stanza = new Stanza(userID);
			try {			
				this.commManager.sendIQGet(stanza, bean, this);
			} catch (CommunicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Override
	public void sendLeaveSuggestion(String userJID,
			String suggestedCIS) {
		// TODO Auto-generated method stub

	}

}
