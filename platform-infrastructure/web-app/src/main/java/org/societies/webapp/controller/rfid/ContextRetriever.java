/**
 * 
 */
package org.societies.webapp.controller.rfid;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.context.broker.ICtxBroker;

/**
 * @author Eliza
 *
 */
public class ContextRetriever {

	private static final String TAG_TO_IDENTITY = "tagToIdentity";
	private static final String TAG_TO_PASSWORD = "tagToPassword";
	private static final String RFID_SERVER_ENTITY = "RFID_SERVER_ENTITY";
	private Hashtable<String, String> tagToPassword;
	private Hashtable<String, String> tagToIdentity;
	private CtxEntity ctxEntity;
	private ICtxBroker ctxBroker;
	private IIdentity serverIdentity;

	public ContextRetriever(ICtxBroker ctxBroker, IIdentity serverIdentity){
		this.ctxBroker = ctxBroker;
		this.serverIdentity = serverIdentity;
		this.tagToIdentity = new Hashtable<String, String>();
		this.tagToPassword = new Hashtable<String, String>();

		try {
			List<CtxIdentifier> list = ctxBroker.lookup(serverIdentity, CtxModelType.ENTITY, RFID_SERVER_ENTITY).get();
			if (list.size()>0){
				CtxIdentifier ctxEntityId = list.get(0);
				ctxEntity = (CtxEntity) ctxBroker.retrieve(ctxEntityId).get();
				Set<CtxAttribute> tagToPassAttributes = ctxEntity.getAttributes(TAG_TO_PASSWORD);
				if (tagToPassAttributes.size()>0){

					byte[] binaryValue = tagToPassAttributes.iterator().next().getBinaryValue();
					if (binaryValue!=null){
						this.tagToPassword = (Hashtable<String, String>) SerialisationHelper.deserialise(binaryValue, this.getClass().getClassLoader());
					}else{
						this.tagToPassword = new Hashtable<String, String>();
					}
				}
				Set<CtxAttribute> tagToIdAttributes = ctxEntity.getAttributes(TAG_TO_IDENTITY);
				if (tagToIdAttributes.size()>0){
					byte[] binaryValue = tagToIdAttributes.iterator().next().getBinaryValue();
					if (binaryValue!=null){
						this.tagToIdentity = (Hashtable<String, String>) SerialisationHelper.deserialise(binaryValue, this.getClass().getClassLoader());
					}else{
						this.tagToIdentity = new Hashtable<String, String>();
					}
				}

			}else{
				this.tagToIdentity = new Hashtable<String, String>();
				this.tagToPassword = new Hashtable<String, String>();
			}
		} catch (InterruptedException e) {
			this.tagToIdentity = new Hashtable<String, String>();
			this.tagToPassword = new Hashtable<String, String>();
			e.printStackTrace();
		} catch (ExecutionException e) {
			this.tagToIdentity = new Hashtable<String, String>();
			this.tagToPassword = new Hashtable<String, String>();
			e.printStackTrace();
		} catch (CtxException e) {
			this.tagToIdentity = new Hashtable<String, String>();
			this.tagToPassword = new Hashtable<String, String>();
			e.printStackTrace();
		} catch (IOException e) {
			this.tagToIdentity = new Hashtable<String, String>();
			this.tagToPassword = new Hashtable<String, String>();
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			this.tagToIdentity = new Hashtable<String, String>();
			this.tagToPassword = new Hashtable<String, String>();
			e.printStackTrace();
		}
		
	}

	public Hashtable<String, String> getTagToIdentity() {
		return tagToIdentity;
	}


	public Hashtable<String, String> getTagToPassword() {
		return tagToPassword;
	}

}
