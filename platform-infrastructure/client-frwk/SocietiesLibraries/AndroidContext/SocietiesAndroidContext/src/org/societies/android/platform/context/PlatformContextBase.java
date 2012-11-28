package org.societies.android.platform.context;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.societies.android.api.context.ACtxClient;
import org.societies.android.api.context.ICtxClient;
import org.societies.android.platform.androidutils.SocietiesSerialiser;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.identity.Requestor;
import org.societies.comm.xmpp.client.impl.ClientCommunicationMgr;

import android.content.Context;

public class PlatformContextBase implements ACtxClient {
	
	//XMPP Communication namespaces and associated entities
	private static final List<String> ELEMENT_NAMES = Arrays.asList("cssManagerMessageBean", "cssManagerResultBean");
    private static final List<String> NAME_SPACES = Arrays.asList("http://societies.org/api/schema/cssmanagement");
    private static final List<String> PACKAGES = Arrays.asList("org.societies.api.schema.cssmanagement");

	private Context context;
	private ClientCommunicationMgr ccm;
	private boolean restrictBroadcast;
	
	/**
	 * Default constructor
	 * 
	 * @param context
	 * @param ccm
	 * @param restrictBroadcast true for testing
	 */
	public PlatformContextBase(Context context, ClientCommunicationMgr ccm, boolean restrictBroadcast) {
		this.context = context;
		this.ccm = ccm;
		this.restrictBroadcast = restrictBroadcast;
	}

	public CtxAssociation createAssociation(String arg0, String arg1,
			String arg2, String arg3) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public CtxAttribute createAttribute(String arg0, String arg1, String arg2,
			String arg3) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public CtxEntity createEntity(String client, String requestor, String targetCSS,
			String type) throws CtxException {

		try {
			Requestor requestorObj = (Requestor) SocietiesSerialiser.Read(Requestor.class, requestor);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	public List<CtxEntityIdentifier> lookupEntities(String arg0, String arg1,
			String arg2, String arg3, String arg4, Serializable arg5,
			Serializable arg6) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<CtxIdentifier> lookupEntity(String arg0, String arg1,
			String arg2, CtxModelType arg3, String arg4) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<CtxIdentifier> lookupTarget(String arg0, String arg1,
			String arg2, CtxModelType arg3, String arg4) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public CtxModelObject remove(String arg0, String arg1, String arg2)
			throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public CtxModelObject retrieve(String arg0, String arg1, String arg2)
			throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public CtxEntityIdentifier retrieveCommunityEntityId(String arg0,
			String arg1, String arg2) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public CtxEntityIdentifier retrieveIndividualEntityId(String arg0,
			String arg1, String arg2) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public CtxModelObject update(String arg0, String arg1, String arg2)
			throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

}
