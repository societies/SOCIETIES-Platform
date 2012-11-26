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

package org.societies.android.platform.contextclient;

//import org.societies.android.api.context.CtxException;

import java.util.List;

import org.societies.android.api.internal.context.IInternalCtxClient;
import org.societies.api.context.CtxException;
import org.societies.android.api.context.model.ACtxAssociationIdentifier;
import org.societies.android.api.context.model.ACtxAttribute;
import org.societies.android.api.context.model.ACtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.android.api.context.model.ACtxEntity;
import org.societies.android.api.context.model.ACtxAssociation;
import org.societies.android.api.context.model.ACtxEntityIdentifier;
import org.societies.android.api.context.model.ACtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.android.platform.contextservice.ContextManagement;
import org.societies.android.platform.contextservice.ContextManagement.LocalBinder;

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

	IInternalCtxClient contextMgmt;
	boolean contextMgmtConnected = false;

	ACtxEntity entity;
	ACtxAttribute attribute;
	ACtxAssociation association;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        
		//Create Intenet for Context Management and Bind
        Intent intentContextMgmt = new Intent(this.getApplicationContext(), ContextManagement.class);
        this.getApplicationContext().bindService(intentContextMgmt, contextMgmtConnection, Context.BIND_AUTO_CREATE);
        
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
				if(contextMgmtConnected){
					contextMgmt.createEntity("contextClient", "person");
					Log.d(LOG_TAG, "Successfully Created Entity.");
				}
				else {
					Log.d(LOG_TAG, "Not Connected!!!");
				}
			} else if(v.getId() == R.id.button4){
				Log.d(LOG_TAG, "Running Create Attribute method.");
				if(contextMgmtConnected){
					entity = contextMgmt.createEntity("contextClient", "house");
					contextMgmt.createAttribute("contextClient", entity.getId(), "flat");
					Log.d(LOG_TAG, "Successfully Created Attribute.");
				}
				else {
					Log.d(LOG_TAG, "Not Connected!!!");
				}
			} else if(v.getId() == R.id.button5){
				Log.d(LOG_TAG, "Running Create Association method.");
				if(contextMgmtConnected){
					association = contextMgmt.createAssociation("contextClient", "isRelatedTo");
					Log.d(LOG_TAG, "Successfully Created Association.");
				}
				else {
					Log.d(LOG_TAG, "Not Connected!!!");
				}
			} else if(v.getId() == R.id.button6){
				Log.d(LOG_TAG, "Running Lookup method.");
				if(contextMgmtConnected){
                    
					List<ACtxIdentifier> ids;
                    
					// Create test entities.
					final ACtxEntityIdentifier entId1 = contextMgmt.createEntity("contextClient", "FooBar").getId();
					final ACtxEntityIdentifier entId2 = contextMgmt.createEntity("contextClient", "Foo").getId();
					final ACtxEntityIdentifier entId3 = contextMgmt.createEntity("contextClient", "Bar").getId();
                    
					// Create test attributes.
					final ACtxAttributeIdentifier attrId1 = contextMgmt.createAttribute("contextClient", entId1, "FooBar").getId();
					final ACtxAttributeIdentifier attrId2 = contextMgmt.createAttribute("contextClient", entId1, "Foo").getId();
					final ACtxAttributeIdentifier attrId3 = contextMgmt.createAttribute("contextClient", entId1, "Bar").getId();
                    
					// Create test attributes.
					final ACtxAssociationIdentifier assocId1 = contextMgmt.createAssociation("contextClient", "FooBar").getId();
					final ACtxAssociationIdentifier assocId2 = contextMgmt.createAssociation("contextClient", "Foo").getId();
					final ACtxAssociationIdentifier assocId3 = contextMgmt.createAssociation("contextClient", "Bar").getId();
                    
					//
					// Lookup entities
					//
                    
					ids =contextMgmt.lookup("contextClient", CtxModelType.ENTITY, "FooBar");
					Log.d(LOG_TAG, "Looking up Entity FooBar - " + ids.contains(entId1));
					ids = contextMgmt.lookup("contextClient", CtxModelType.ENTITY, "Foo");
					Log.d(LOG_TAG, "Looking up Entity Foo - " + ids.contains(entId2));
					ids = contextMgmt.lookup("contextClient", CtxModelType.ENTITY, "Bar");
					Log.d(LOG_TAG, "Looking up Entity Bar - " + ids.contains(entId3));
                    
					//
					// Lookup attributes
					//
                    
					ids = contextMgmt.lookup("contextClient", CtxModelType.ATTRIBUTE, "FooBar");
					Log.d(LOG_TAG, "Looking up Attribute FooBar - " + ids.contains(attrId1));
					ids = contextMgmt.lookup("contextClient", CtxModelType.ATTRIBUTE, "Foo");
					Log.d(LOG_TAG, "Looking up Attribute Foo - " + ids.contains(attrId2));
					ids = contextMgmt.lookup("contextClient", CtxModelType.ATTRIBUTE, "Bar");
					Log.d(LOG_TAG, "Looking up Attribute Bar - " + ids.contains(attrId3));
                    
					//
					// Lookup associations.
					//
                    
					ids = contextMgmt.lookup("contextClient", CtxModelType.ASSOCIATION, "FooBar");
					Log.d(LOG_TAG, "Looking up Association FooBar - " + ids.contains(assocId1));
					ids = contextMgmt.lookup("contextClient", CtxModelType.ASSOCIATION, "Foo");
					Log.d(LOG_TAG, "Looking up Association Foo - " + ids.contains(assocId2));
					ids = contextMgmt.lookup("contextClient", CtxModelType.ASSOCIATION, "Bar");
					Log.d(LOG_TAG, "Looking up Association Bar - " + ids.contains(assocId3));
                    
					Log.d(LOG_TAG, "Successfully LookedUp.");
				}
				else {
					Log.d(LOG_TAG, "Not Connected!!!");
				}
			} else if(v.getId() == R.id.button7){
				Log.d(LOG_TAG, "Running Lookup Entities method.");
				if(contextMgmtConnected){
                    
					List<ACtxEntityIdentifier> identifiers;
					ACtxEntity entity, entity2;
					ACtxAttribute attribute, attribute2;
					ACtxEntityIdentifier entityId;
                    
					entity = contextMgmt.createEntity("contextClient", "NUMBER");
					attribute = contextMgmt.createAttribute("contextClient", (ACtxEntityIdentifier)entity.getId(), "BOOKS");
					entity2 = contextMgmt.createEntity("contextClient", "NUMBER");
					attribute2 = contextMgmt.createAttribute("contextClient", (ACtxEntityIdentifier)entity2.getId(), "BOOKS");
                    
					// lookup by name attribute
					identifiers = contextMgmt.lookupEntities("contextClient", "NUMBER", "BOOKS", 1, 10);
					Log.d(LOG_TAG, "Size should be 0 and is - " + identifiers.size());
					attribute.setIntegerValue(5);
					attribute.setValueType(CtxAttributeValueType.INTEGER);
					contextMgmt.update("contextClient", attribute);
					attribute2.setIntegerValue(12);
					attribute2.setValueType(CtxAttributeValueType.INTEGER);
					contextMgmt.update("contextClient", attribute2);
                    
					identifiers = contextMgmt.lookupEntities("contextClient", "NUMBER", "BOOKS", 1, 10);
					System.out.println(identifiers);
					Log.d(LOG_TAG, "The identifiers is - " + identifiers);
					Log.d(LOG_TAG, "Size now should be 1 - " + identifiers.size());
                    
					Log.d(LOG_TAG, "Is it instanceof CtxEntityIdentifier? - " + identifiers.get(0));
                    //					assertTrue(identifiers.get(0)instanceof CtxEntityIdentifier);
					entityId = (ACtxEntityIdentifier) identifiers.get(0);
                    
					Log.d(LOG_TAG, "The model type should be " + CtxModelType.ENTITY + "and it is - " + entityId.getModelType());
					Log.d(LOG_TAG, "The type should be NUMBER and it is - " + entityId.getType());
                    
					Log.d(LOG_TAG, "Successfully LookedUp Entities.");
				}
				else {
					Log.d(LOG_TAG, "Not Connected!!!");
				}
			} else if(v.getId() == R.id.button8){
				Log.d(LOG_TAG, "Running Update method.");
				if(contextMgmtConnected){
					entity = contextMgmt.createEntity("contextClient", "house");
					attribute = contextMgmt.createAttribute("contextClient", entity.getId(), "name");
                    
					attribute = (ACtxAttribute) contextMgmt.retrieve("contextClient", attribute.getId());
					attribute.setIntegerValue(5);
					contextMgmt.update("contextClient", attribute);
					//verify update
					attribute = (ACtxAttribute) contextMgmt.retrieve("contextClient", attribute.getId());
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
    
    /**
     * ContextManagement service connection
     */
    private ServiceConnection contextMgmtConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName name, IBinder service) {
        	Log.d(LOG_TAG, "Connecting to ContextManagement service");
        	try {
	        	//GET LOCAL BINDER
	            LocalBinder binder = (LocalBinder) service;
	
	            //OBTAIN SERVICE DISCOVERY API
	            
	            contextMgmt = (IInternalCtxClient) binder.getService();
	            contextMgmtConnected = true;
	            Log.d(LOG_TAG, "Successfully connected to IInternalCtxClient service");
	            
        	} catch (Exception ex) {
        		Log.d(LOG_TAG, "Error binding to service: " + ex.getMessage());
        	}
        }
        
        public void onServiceDisconnected(ComponentName name) {
        	Log.d(LOG_TAG, "Disconnecting ContextManagement service");
        	contextMgmtConnected = false;
        }
    };

}