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

package org.societies.android.platform.context;

//import org.societies.android.api.context.CtxException;

import java.util.List;

import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAssociationIdentifier;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.android.api.internal.context.broker.ICtxClientBroker;
import org.societies.android.platform.context.ContextManagement.CtxLocalBinder;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class GUI_UserContext extends Activity implements OnClickListener{

	private static final String LOG_TAG = GUI_UserContext.class.getName();
	ICtxClientBroker cmService = null;
	boolean connectedToService = false;
	CtxEntity entity;
	CtxAttribute attribute;
	CtxAssociation association;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//bind to ContextManagement service
		this.bindToService();

		setContentView(R.layout.user_context);
		
		Button createEntity = (Button)findViewById(R.id.button3);
		createEntity.setOnClickListener(this);

		Button createAttribute = (Button)findViewById(R.id.button4);
		createAttribute.setOnClickListener(this);
		
		Button createAssociation = (Button)findViewById(R.id.button5);
		createAssociation.setOnClickListener(this);
		
		Button lookup = (Button)findViewById(R.id.button6);
		lookup.setOnClickListener(this);

		Button lookupEntities = (Button)findViewById(R.id.button7);
		lookupEntities.setOnClickListener(this);

		Button update = (Button)findViewById(R.id.button8);
		update.setOnClickListener(this);

//		setContentView(R.layout.create_entity);
		
//		Log.d(LOG_TAG, "Running Create Entity method.");

	}
	
	public void onClick(View v){
		
		try {
			if(v.getId() == R.id.button3){
				Log.d(LOG_TAG, "Running Create Entity method.");
				if(connectedToService){
					cmService.createEntity("person");
					Log.d(LOG_TAG, "Successfully Created Entity.");
				}
				else {
					Log.d(LOG_TAG, "Not Connected!!!");
				}
			} else if(v.getId() == R.id.button4){
				Log.d(LOG_TAG, "Running Create Attribute method.");
				if(connectedToService){
					entity = cmService.createEntity("house");
					cmService.createAttribute(entity.getId(), "flat");
					Log.d(LOG_TAG, "Successfully Created Attribute.");
				}
				else {
					Log.d(LOG_TAG, "Not Connected!!!");
				}
			} else if(v.getId() == R.id.button5){
				Log.d(LOG_TAG, "Running Create Association method.");
				if(connectedToService){
					association = cmService.createAssociation("isRelatedTo");
					Log.d(LOG_TAG, "Successfully Created Association.");
				}
				else {
					Log.d(LOG_TAG, "Not Connected!!!");
				}
			} else if(v.getId() == R.id.button6){
				Log.d(LOG_TAG, "Running Lookup method.");
				if(connectedToService){

					List<CtxIdentifier> ids;
				       
					// Create test entities.
					final CtxEntityIdentifier entId1 = cmService.createEntity("FooBar").getId();
					final CtxEntityIdentifier entId2 = cmService.createEntity("Foo").getId();
					final CtxEntityIdentifier entId3 = cmService.createEntity("Bar").getId();
				      
					// Create test attributes.
					final CtxAttributeIdentifier attrId1 = cmService.createAttribute(entId1, "FooBar").getId();
					final CtxAttributeIdentifier attrId2 = cmService.createAttribute(entId1, "Foo").getId();
					final CtxAttributeIdentifier attrId3 = cmService.createAttribute(entId1, "Bar").getId();
				       
					// Create test attributes.
					final CtxAssociationIdentifier assocId1 = cmService.createAssociation("FooBar").getId();
					final CtxAssociationIdentifier assocId2 = cmService.createAssociation("Foo").getId();
					final CtxAssociationIdentifier assocId3 = cmService.createAssociation("Bar").getId();

					//
					// Lookup entities
					//
				       
					ids =cmService.lookup(CtxModelType.ENTITY, "FooBar");
					Log.d(LOG_TAG, "Looking up Entity FooBar - " + ids.contains(entId1));
					ids = cmService.lookup(CtxModelType.ENTITY, "Foo");
					Log.d(LOG_TAG, "Looking up Entity Foo - " + ids.contains(entId2));
					ids = cmService.lookup(CtxModelType.ENTITY, "Bar");
					Log.d(LOG_TAG, "Looking up Entity Bar - " + ids.contains(entId3));
				       
					//
					// Lookup attributes
					//
				       
					ids = cmService.lookup(CtxModelType.ATTRIBUTE, "FooBar");
					Log.d(LOG_TAG, "Looking up Attribute FooBar - " + ids.contains(attrId1));
					ids = cmService.lookup(CtxModelType.ATTRIBUTE, "Foo");
					Log.d(LOG_TAG, "Looking up Attribute Foo - " + ids.contains(attrId2));
					ids = cmService.lookup(CtxModelType.ATTRIBUTE, "Bar");
					Log.d(LOG_TAG, "Looking up Attribute Bar - " + ids.contains(attrId3));
				
					//
					// Lookup associations.
					//
				       
					ids = cmService.lookup(CtxModelType.ASSOCIATION, "FooBar");
					Log.d(LOG_TAG, "Looking up Association FooBar - " + ids.contains(assocId1));
					ids = cmService.lookup(CtxModelType.ASSOCIATION, "Foo");
					Log.d(LOG_TAG, "Looking up Association Foo - " + ids.contains(assocId2));
					ids = cmService.lookup(CtxModelType.ASSOCIATION, "Bar");
					Log.d(LOG_TAG, "Looking up Association Bar - " + ids.contains(assocId3));

					Log.d(LOG_TAG, "Successfully LookedUp.");
				}
				else {
					Log.d(LOG_TAG, "Not Connected!!!");
				}
			} else if(v.getId() == R.id.button7){
				Log.d(LOG_TAG, "Running Lookup Entities method.");
				if(connectedToService){

					List<CtxEntityIdentifier> identifiers;
					CtxEntity entity, entity2;
					CtxAttribute attribute, attribute2;
					CtxEntityIdentifier entityId;

					entity = cmService.createEntity("NUMBER");
					attribute = cmService.createAttribute((CtxEntityIdentifier)entity.getId(), "BOOKS");
					entity2 = cmService.createEntity("NUMBER");
					attribute2 = cmService.createAttribute((CtxEntityIdentifier)entity2.getId(), "BOOKS");
				       
					// lookup by name attribute
					identifiers = cmService.lookupEntities("NUMBER", "BOOKS", 1, 10);
					Log.d(LOG_TAG, "Size should be 0 and is - " + identifiers.size());
					attribute.setIntegerValue(5);
					attribute.setValueType(CtxAttributeValueType.INTEGER);
					cmService.update(attribute);
					attribute2.setIntegerValue(12);
					attribute2.setValueType(CtxAttributeValueType.INTEGER);
					cmService.update(attribute2);

					identifiers = cmService.lookupEntities("NUMBER", "BOOKS", 1, 10);
					System.out.println(identifiers);
					Log.d(LOG_TAG, "The identifiers is - " + identifiers);
					Log.d(LOG_TAG, "Size now should be 1 - " + identifiers.size());

					Log.d(LOG_TAG, "Is it instanceof CtxEntityIdentifier? - " + identifiers.get(0));
//					assertTrue(identifiers.get(0)instanceof CtxEntityIdentifier);
					entityId = (CtxEntityIdentifier) identifiers.get(0);

					Log.d(LOG_TAG, "The model type should be " + CtxModelType.ENTITY + "and it is - " + entityId.getModelType());
					Log.d(LOG_TAG, "The type should be NUMBER and it is - " + entityId.getType());
					
					Log.d(LOG_TAG, "Successfully LookedUp Entities.");
				}
				else {
					Log.d(LOG_TAG, "Not Connected!!!");
				} 
			} else if(v.getId() == R.id.button8){
				Log.d(LOG_TAG, "Running Update method.");
				if(connectedToService){
					entity = cmService.createEntity("house");
					attribute = cmService.createAttribute(entity.getId(), "name");

					attribute = (CtxAttribute) cmService.retrieve(attribute.getId());
					attribute.setIntegerValue(5);
					cmService.update(attribute);
					//verify update
					attribute = (CtxAttribute) cmService.retrieve(attribute.getId());
					Log.d(LOG_TAG, "attribute value should be 5 and it is:"+attribute.getIntegerValue());
					
					Log.d(LOG_TAG, "Successfully Updated Attribute.");
				}
				else {
					Log.d(LOG_TAG, "Not Connected!!!");
				}
			}
		}catch (CtxException e) {
			e.printStackTrace();
		} 
	}
	
/*	private void createEntity() {
		if(connectedToService){
			Log.d(LOG_TAG, "Running Create Entity method.");
			try {
				cmService.createEntity("person");
				Log.d(LOG_TAG, "Successfully Created Entity.");
			} catch (CtxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.d(LOG_TAG, "Or NotSuccessfully Created Entity.");
		}
		else {
			Log.d(LOG_TAG, "Not Connected!!!");
		}
	}
	*/
	private void bindToService(){
		//Create intent to select service to bind to
		Intent bindIntent = new Intent(this, ContextManagement.class);
		//bind to service
		bindService(bindIntent, cmConnection, Context.BIND_AUTO_CREATE);
	}
	
	private ServiceConnection cmConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			cmService = ((CtxLocalBinder) service).getService();
			connectedToService = true;
			Log.d(LOG_TAG, "UserContext GUI connected to ContextManagement service");
		}

		public void onServiceDisconnected(ComponentName className) {
			// As our service is in the same process, this should never be called
			connectedToService = false;
			Log.d(LOG_TAG, "UserContext GUI disconnected from ContextManagement service");
		}
	};
	
}
