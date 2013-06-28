/**
 * 
 */
package org.societies.rfid.server;

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
		this.tagToPassword = new Hashtable<String, String>();
		this.tagToIdentity = new Hashtable<String, String>();
		try {
			List<CtxIdentifier> list = ctxBroker.lookup(CtxModelType.ENTITY, RFID_SERVER_ENTITY).get();
			if (list.size()>0){
				CtxIdentifier ctxEntityId = list.get(0);
				ctxEntity = (CtxEntity) ctxBroker.retrieve(ctxEntityId).get();
				Set<CtxAttribute> tagToPassAttributes = ctxEntity.getAttributes(TAG_TO_PASSWORD);
				if (tagToPassAttributes.size()>0){
					this.tagToPassword = (Hashtable<String, String>) SerialisationHelper.deserialise(tagToPassAttributes.iterator().next().getBinaryValue(), this.getClass().getClassLoader());
				}
				Set<CtxAttribute> tagToIdAttributes = ctxEntity.getAttributes(TAG_TO_IDENTITY);
				if (tagToIdAttributes.size()>0){
					this.tagToIdentity = (Hashtable<String, String>) SerialisationHelper.deserialise(tagToIdAttributes.iterator().next().getBinaryValue(), this.getClass().getClassLoader());
				}

			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Hashtable<String, String> getTagToIdentity() {
		return tagToIdentity;
	}

	public void setTagToIdentity(Hashtable<String, String> tagToIdentity) {
		this.tagToIdentity = tagToIdentity;
	}

	public Hashtable<String, String> getTagToPassword() {
		return tagToPassword;
	}

	public void setTagToPassword(Hashtable<String, String> tagToPassword) {
		this.tagToPassword = tagToPassword;
	}

	public void updateContext(){
		if ((this.tagToIdentity.size()==0) && (this.tagToPassword.size()==0)){
			return;
		}
		try {
			if (this.ctxEntity==null){

				this.ctxEntity = this.ctxBroker.createEntity(serverIdentity, RFID_SERVER_ENTITY).get();
			}
			
			if (this.tagToPassword.size()!=0){
				Set<CtxAttribute> tagToPasswordAttributes = ctxEntity.getAttributes(TAG_TO_PASSWORD);
				if (tagToPasswordAttributes.size()==0){
					CtxAttribute tagToPasswordAttribute = this.ctxBroker.createAttribute(this.ctxEntity.getId(), TAG_TO_PASSWORD).get();
					tagToPasswordAttribute.setBinaryValue(SerialisationHelper.serialise(tagToPassword));
					ctxBroker.update(tagToPasswordAttribute);

				}else{
					CtxAttribute tagToPasswordAttribute = tagToPasswordAttributes.iterator().next();
					tagToPasswordAttribute.setBinaryValue(SerialisationHelper.serialise(tagToPassword));
					ctxBroker.update(tagToPasswordAttribute);

				}
			}
			
			if (this.tagToIdentity.size()!=0){
				Set<CtxAttribute> tagToIdAttributes = ctxEntity.getAttributes(TAG_TO_IDENTITY);
				if (tagToIdAttributes.size()==0){
					CtxAttribute tagToIdentityAttribute = this.ctxBroker.createAttribute(this.ctxEntity.getId(), TAG_TO_IDENTITY).get();
					tagToIdentityAttribute.setBinaryValue(SerialisationHelper.serialise(tagToIdentity));
					ctxBroker.update(tagToIdentityAttribute);
					
				}else{
					CtxAttribute tagToIdentityAttribute = tagToIdAttributes.iterator().next();
					tagToIdentityAttribute.setBinaryValue(SerialisationHelper.serialise(tagToIdentity));
					ctxBroker.update(tagToIdentityAttribute);
				}
				
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
