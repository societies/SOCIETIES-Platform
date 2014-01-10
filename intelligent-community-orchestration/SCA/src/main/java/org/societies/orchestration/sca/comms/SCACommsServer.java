package org.societies.orchestration.sca.comms;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.interfaces.IFeatureServer;
import org.societies.orchestration.sca.api.ISCAManager;
import org.societies.api.schema.orchestration.sca.scasuggestedcisbean.SCASuggestedBean;

public class SCACommsServer implements IFeatureServer {

	private ISCAManager scaManager;
	private ICommManager commManager;

	private static Logger log = LoggerFactory.getLogger(SCACommsServer.class);


	private static final List<String> NAMESPACES = Collections.unmodifiableList(Arrays.asList(
			"http://societies.org/api/schema/orchestration/sca/scasuggestedcisbean"
			));

	private static final List<String> PACKAGES = Collections.unmodifiableList(Arrays.asList(
			"org.societies.api.schema.orchestration.sca.scasuggestedcisbean"
			));

	public SCACommsServer() {
	}



	public void InitService() {
		//REGISTER OUR ServiceManager WITH THE XMPP Communication Manager
		try {
			getCommManager().register(this);
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
	public Object getQuery(Stanza arg0, Object arg1) throws XMPPError {
		log.debug("Got Message!");
		if(arg1 instanceof SCASuggestedBean) {
			SCASuggestedBean newbean = (SCASuggestedBean) arg1;
			log.debug("My type is: " + newbean.getSuggestedType());
			log.debug("Payload is of correct bean!");
		}
		// TODO Auto-generated method stub
		//SEND A MESSAGE TO JIANNIS TO JOIN
		return new SCASuggestedBean();
	}

	@Override
	public List<String> getXMLNamespaces() {
		// TODO Auto-generated method stub
		return NAMESPACES;
	}

	@Override
	public void receiveMessage(Stanza arg0, Object arg1) {
		// TODO Auto-generated method stub
		log.debug("I got the message");

	}

	@Override
	public Object setQuery(Stanza arg0, Object arg1) throws XMPPError {
		// TODO Auto-generated method stub
		return null;
	}

	public ISCAManager getScaManager() {
		return scaManager;
	}

	public void setScaManager(ISCAManager scaManager) {
		this.scaManager = scaManager;
	}

	public ICommManager getCommManager() {
		return commManager;
	}

	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
	}

}
