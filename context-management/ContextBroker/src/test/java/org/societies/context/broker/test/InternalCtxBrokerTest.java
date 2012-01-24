/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.context.broker.test;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Time;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxHistoryAttribute;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.internal.context.broker.IUserCtxBrokerCallback;
import org.societies.context.broker.impl.InternalCtxBroker;
import org.societies.context.user.db.impl.UserCtxDBMgr;
import org.societies.context.userHistory.impl.UserContextHistoryManagement;


public class InternalCtxBrokerTest {

	private static InternalCtxBroker internalCtxBroker = null;
	BrokerCallbackImpl callback ;

	//Constructor
	InternalCtxBrokerTest() {

		callback = new  BrokerCallbackImpl();

		internalCtxBroker = new InternalCtxBroker();
		internalCtxBroker.setUserCtxDBMgr(new UserCtxDBMgr());
		//internalCtxBroker.setUserCtxHistoryMgr(new UserContextHistoryManagement());

		System.out.println("-- start of testing --");
		testCreateCtxEntity();
		testCreateCtxAttribute();
		testRetrieveAttribute();
		testUpdateAttribute();
		testUpdateAttributeBlob();
		//testHistoryAttributeInteger();
		//testHistoryAttributeString();
		testHistoryAttributeBlob();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)  {

		InternalCtxBrokerTest testing = new InternalCtxBrokerTest();

	}


	private void testCreateCtxEntity(){
		System.out.println("---- test CreateCtxEntity");
		internalCtxBroker.createEntity("person", callback);
	}

	private void testCreateCtxAttribute(){
		System.out.println("---- test testCreateCtxAttribute");
		internalCtxBroker.createAttribute(callback.getCtxEntity().getId(), CtxAttributeValueType.INDIVIDUAL, "size", callback);
	}

	private void testRetrieveAttribute(){
		System.out.println("---- testRetrieveCtxAttribute");
		CtxAttribute ctxAttribute = callback.getCtxAttribute();
		internalCtxBroker.retrieve(ctxAttribute.getId(), callback);
		ctxAttribute = (CtxAttribute) callback.getCtxModelObject();
	}

	private void testUpdateAttribute(){
		System.out.println("---- testUpdateAttribute");
		CtxAttribute ctxAttribute = (CtxAttribute) callback.getCtxModelObject();
		internalCtxBroker.retrieve(ctxAttribute.getId(), callback);
		ctxAttribute = (CtxAttribute) callback.getCtxModelObject();
		ctxAttribute.setIntegerValue(100);
		internalCtxBroker.update(ctxAttribute, callback);
		//verify update
		internalCtxBroker.retrieve(ctxAttribute.getId(), callback);
		ctxAttribute = (CtxAttribute) callback.getCtxModelObject();
		System.out.println("attribute value should be 100 and it is:"+ctxAttribute.getIntegerValue());

		ctxAttribute.setIntegerValue(200);
		internalCtxBroker.update(ctxAttribute, callback);
		//verify update
		internalCtxBroker.retrieve(ctxAttribute.getId(), callback);
		ctxAttribute = (CtxAttribute) callback.getCtxModelObject();
		System.out.println("attribute value should be 200 and it is:"+ctxAttribute.getIntegerValue());


	}


	private void testHistoryAttributeInteger(){
		System.out.println("---- test testHistoryAttributeInteger");


		CtxAttribute ctxAttribute = callback.getCtxAttribute();
		Date date = new Date();
		System.out.println("date="+ date.getTime());
		ctxAttribute.setHistoryRecorded(true);
		System.out.println("---- Create history");

		for (int i =1; i<10; i++){
			ctxAttribute.setIntegerValue(i);
			//System.out.println("LAST MODIFIED: "+ctxAttribute.getLastModified());
			ctxAttribute = internalUpdateRetrieve(ctxAttribute);
			System.out.println("attribute value "+ctxAttribute.getIntegerValue());
			Thread thisThread = Thread.currentThread();

			try
			{
				thisThread.sleep(1000);
			}
			catch (Throwable t)
			{
				throw new OutOfMemoryError("An Error has occured");
			}

		}
		System.out.println("---- History created");

		System.out.println("---- Retrieve History");
		internalCtxBroker.retrievePast(ctxAttribute.getId(), null, null, callback);

		System.out.println("----  History Retrieved");



		/*
		internalCtxBroker.update(ctxAttribute,  callback);

		internalCtxBroker.retrieve(ctxAttribute.getId(), callback);
		ctxAttribute = (CtxAttribute) callback.getCtxModelObject();
		 */

		//System.out.println("attribute value should be 250 and it is:"+ctxAttribute.getIntegerValue());


	}


	private void  testHistoryAttributeString(){
		System.out.println("---- test testHistoryAttributeString");

		internalCtxBroker.createAttribute(callback.getCtxEntity().getId(), CtxAttributeValueType.INDIVIDUAL, "name", callback);
		CtxAttribute ctxAttributeString = (CtxAttribute) callback.getCtxAttribute();

		ctxAttributeString.setStringValue("Aris");
		ctxAttributeString.setHistoryRecorded(true);
		internalCtxBroker.update(ctxAttributeString, callback);
		CtxAttribute ctxAttributeStringRtrvd = (CtxAttribute) callback.getCtxAttribute();

		//System.out.println("ctxAttributeString"+ ctxAttributeStringRtrvd.getId());
		System.out.println("ctxAttributeString id "+ ctxAttributeString.getId());
		System.out.println("ctxAttributeString value: "+ ctxAttributeStringRtrvd.getStringValue());

		ctxAttributeString.setStringValue("Zeus");
		ctxAttributeString = internalUpdateRetrieve(ctxAttributeString);
		System.out.println("ctxAttributeString value: "+ ctxAttributeStringRtrvd.getStringValue());

		ctxAttributeString.setStringValue("Jupiter");
		ctxAttributeString = internalUpdateRetrieve(ctxAttributeString);
		System.out.println("ctxAttributeString value: "+ ctxAttributeStringRtrvd.getStringValue());

		internalCtxBroker.retrievePast(ctxAttributeString.getId(), null, null, callback);
	}



	private void testUpdateAttributeBlob() {
		System.out.println("---- testUpdateAttributeBlob");
		CtxAttribute ctxAttribute = (CtxAttribute) callback.getCtxModelObject();

		MockBlobClass valueMockClass = new MockBlobClass();

		System.out.println("Attribute value contained in serialised class ="+valueMockClass.i);

		byte[] blobValue;
		try {
			blobValue = SerialisationHelper.serialise(valueMockClass);
			ctxAttribute.setBinaryValue(blobValue);
			internalCtxBroker.update(ctxAttribute, callback);
			internalCtxBroker.retrieve(ctxAttribute.getId(), callback);
			ctxAttribute = (CtxAttribute) callback.getCtxModelObject();
			System.out.println("attribute containing blob retrieved:"+ctxAttribute.getBinaryValue());

			MockBlobClass valueMockClassDeserialised = (MockBlobClass) SerialisationHelper.deserialise(ctxAttribute.getBinaryValue(), this.getClass().getClassLoader());

			System.out.println("attribute value contained in deserialised class retrieved:"+valueMockClassDeserialised.i);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}




	private void  testHistoryAttributeBlob(){
		System.out.println("---- test testHistoryAttributeBlob");

		internalCtxBroker.createAttribute(callback.getCtxEntity().getId(), CtxAttributeValueType.INDIVIDUAL, "blobAttrValue", callback);
		CtxAttribute ctxAttributeBlob = (CtxAttribute) callback.getCtxAttribute();

		Map<String,String> blobClass = new HashMap<String,String>();
		blobClass.put("key1","value1");

		byte[] blobValue;
		try {
			blobValue = SerialisationHelper.serialise((Serializable) blobClass);
			ctxAttributeBlob.setHistoryRecorded(true);
			ctxAttributeBlob.setBinaryValue(blobValue);
			ctxAttributeBlob = internalUpdateRetrieve(ctxAttributeBlob);

			blobClass.put("key2", "value2");
			blobValue = SerialisationHelper.serialise((Serializable) blobClass);
			ctxAttributeBlob.setBinaryValue(blobValue);
			ctxAttributeBlob = internalUpdateRetrieve(ctxAttributeBlob);
			internalCtxBroker.retrievePast(ctxAttributeBlob.getId(), null, null, callback);

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
	}


	private CtxAttribute internalUpdateRetrieve(CtxAttribute ctxAttribute){

		internalCtxBroker.update(ctxAttribute,  callback);
		internalCtxBroker.retrieve(ctxAttribute.getId(), callback);
		ctxAttribute = (CtxAttribute) callback.getCtxModelObject();
		return ctxAttribute;
	}



	private class BrokerCallbackImpl implements IUserCtxBrokerCallback{

		CtxEntity ctxEntity = null;
		CtxAttribute ctxAttribute = null;
		CtxModelObject ctxModelObject = null;


		public CtxEntity getCtxEntity(){
			return ctxEntity;
		}

		public CtxAttribute getCtxAttribute(){
			return ctxAttribute;
		}

		public CtxModelObject getCtxModelObject(){
			return  this.ctxModelObject;
		}

		@Override
		public void cancel(CtxIdentifier c_id, String reason) {
			// TODO Auto-generated method stub

		}

		@Override
		public void ctxAssociationCreated(CtxAssociation ctxEntity) {
			// TODO Auto-generated method stub

		}

		@Override
		public void ctxAttributeCreated(CtxAttribute ctxAttribute) {
			System.out.println("CtxAttribute created "+ ctxAttribute.getId());
			this.ctxAttribute = ctxAttribute;

		}

		@Override
		public void ctxEntitiesLookedup(List<CtxEntityIdentifier> list) {
			// TODO Auto-generated method stub

		}

		@Override
		public void ctxEntityCreated(CtxEntity ctxEntity) {
			System.out.println("Entity created "+ ctxEntity.getId());
			this.ctxEntity = ctxEntity;
		}

		@Override
		public void ctxIndividualCtxEntityCreated(CtxEntity ctxEntity) {
			// TODO Auto-generated method stub

		}

		@Override
		public void ctxModelObjectRemoved(CtxModelObject ctxModelObject) {
			// TODO Auto-generated method stub

		}

		@Override
		public void ctxModelObjectRetrieved(CtxModelObject ctxModelObject) {
			System.out.println("ctxModelObject Retrieved "+ ctxModelObject.getId());
			this.ctxModelObject = ctxModelObject;
		}

		@Override
		public void ctxModelObjectsLookedup(List<CtxIdentifier> list) {
			// TODO Auto-generated method stub

		}

		@Override
		public void ctxModelObjectUpdated(CtxModelObject ctxModelObject) {
			System.out.println("ctxModelObject Updated "+ ctxModelObject.getId());
		}

		@Override
		public void futureCtxRetrieved(List<CtxAttribute> futCtx) {
			// TODO Auto-generated method stub

		}

		@Override
		public void futureCtxRetrieved(CtxAttribute futCtx) {
			// TODO Auto-generated method stub

		}

		@Override
		public void historyCtxRetrieved(CtxHistoryAttribute hoc) {
			// TODO Auto-generated method stub

		}

		@Override
		public void historyCtxRetrieved(List<CtxHistoryAttribute> hoc) {
			System.out.println("history size retrieved  "+ hoc.size());
			for(int i=0 ; i<hoc.size(); i++){
				CtxHistoryAttribute hocAttr = hoc.get(i);
				System.out.println("HoC AttrID:"+hocAttr.getId() +" time recorded:"+hocAttr.getLastModified()+" value: ");
				if(hocAttr.getStringValue() != null) System.out.println(hocAttr.getStringValue());
				if(hocAttr.getIntegerValue() != null) System.out.println(hocAttr.getIntegerValue());
				if(hocAttr.getDoubleValue() != null) System.out.println(hocAttr.getDoubleValue());
				if(hocAttr.getBinaryValue() != null) {
					try {
						HashMap<String,String> blobValue = (HashMap<String,String>) SerialisationHelper.deserialise(hocAttr.getBinaryValue(), this.getClass().getClassLoader());
						System.out.println(blobValue);
					
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					
					System.out.println(hocAttr.getBinaryValue());
				}
			}

			//	ctxAttribute.getQuality().getLastUpdated();
		}

		@Override
		public void ok(CtxIdentifier c_id) {
			// TODO Auto-generated method stub

		}

		@Override
		public void ok_list(List<CtxIdentifier> list) {
			// TODO Auto-generated method stub

		}

		@Override
		public void ok_values(List<Object> list) {
			// TODO Auto-generated method stub

		}

		@Override
		public void similartyResults(List<Object> results) {
			// TODO Auto-generated method stub

		}

		@Override
		public void updateReceived(CtxModelObject ctxModelObj) {
			// TODO Auto-generated method stub

		}

	}

}
