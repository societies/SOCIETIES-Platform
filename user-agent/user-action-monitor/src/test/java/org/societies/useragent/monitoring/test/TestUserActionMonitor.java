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

package org.societies.useragent.monitoring.test;

import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Ignore;
import org.junit.Test;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IdentityType;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.useragent.monitoring.SnapshotManager;
import org.societies.useragent.monitoring.model.Snapshot;
import org.societies.useragent.monitoring.model.SnapshotsRegistry;
import org.societies.useragent.monitoring.UserActionMonitor;
import org.springframework.scheduling.annotation.AsyncResult;

public class TestUserActionMonitor /*extends TestCase*/{

	ICtxBroker mockCtxBroker;
	UserActionMonitor uam;
	SnapshotManager snpshtMgr;

	//mock values
	IIdentity mockCssID;
	String stringId;
	CtxIdentifier mockPersonId;
	CtxEntityIdentifier mockEntityId;
	CtxIdentifier mockSymLocId;
	//CtxIdentifier mockStatusId;
	//CtxIdentifier mockTempId;
	
	CtxIdentifier mockTodId;
	CtxIdentifier mockHodId;
	CtxIdentifier mockDowId;
	
	
	CtxIdentifier mockSnpshtRegistryId;
	CtxModelObject mockSnpshtRegistryObject;
	//CtxAttribute mockSnpshtRegistryAttribute;
	SnapshotsRegistry snpshtsRegistry;

	//arraylists
	List<CtxIdentifier> mockPersonIds;
	List<CtxIdentifier> mockSymLocIds;
	//List<CtxIdentifier> mockStatusIds;
	//List<CtxIdentifier> mockTempIds;
	List<CtxIdentifier> mockTodIds;
	List<CtxIdentifier> mockHodIds;
	List<CtxIdentifier> mockDowIds;
	List<CtxIdentifier> mockSnpshtRegistryIds;

	//futures
	Future<List<CtxIdentifier>> mockSymLocIdFuture;
	//Future<List<CtxIdentifier>> mockStatusIdFuture;
	//Future<List<CtxIdentifier>> mockTempIdFuture;
	Future<List<CtxIdentifier>> mockTodIdFuture;
	Future<List<CtxIdentifier>> mockHodIdFuture;
	Future<List<CtxIdentifier>> mockDowIdFuture;
	
	Future<List<CtxIdentifier>> mockSnpshtRegistryIdFuture;
	Future<CtxModelObject> mockSnpshtRegistryObjectFuture;
	private IndividualCtxEntity ctxEntity;

	public void setUp() throws Exception {
		mockCtxBroker = mock(ICtxBroker.class);
		uam = new UserActionMonitor();
		uam.setCtxBroker(mockCtxBroker);

		stringId = "sarah@societies.org";
		mockCssID = new MockIdentity(IdentityType.CSS, "sarah", "societies.org");
		snpshtsRegistry = new SnapshotsRegistry();

		/*
		 * define context elements
		 */
		mockPersonId = new CtxEntityIdentifier(stringId, "PERSON", new Long(12345));
		ctxEntity = new IndividualCtxEntity(mockEntityId);
		mockEntityId = new CtxEntityIdentifier(stringId, "testEntity", new Long(12345));
		mockSymLocId = new CtxAttributeIdentifier(mockEntityId, "symLoc", new Long(12345));
		//mockStatusId = new CtxAttributeIdentifier(mockEntityId, "status", new Long(12345));
		//mockTempId = new CtxAttributeIdentifier(mockEntityId, "temperature", new Long(12345));
		
		mockTodId = new CtxAttributeIdentifier(mockEntityId, CtxAttributeTypes.TIME_OF_DAY, new Long(12345));
		mockHodId = new CtxAttributeIdentifier(mockEntityId, CtxAttributeTypes.HOUR_OF_DAY, new Long(12345));
		mockDowId = new CtxAttributeIdentifier(mockEntityId, CtxAttributeTypes.DAY_OF_WEEK, new Long(12345));
		
		mockSnpshtRegistryId = new CtxAttributeIdentifier(mockEntityId, "snpshtRegistry", new Long(12345));
		mockSnpshtRegistryObject = new CtxAttribute((CtxAttributeIdentifier)mockSnpshtRegistryId);
		this.ctxEntity.addAttribute((CtxAttribute) mockSnpshtRegistryObject);

		/*
		 * define arraylists
		 */
		mockPersonIds = new ArrayList<CtxIdentifier>();
		mockSymLocIds = new ArrayList<CtxIdentifier>();
		//mockStatusIds = new ArrayList<CtxIdentifier>();
		//mockTempIds = new ArrayList<CtxIdentifier>();
		
		mockTodIds = new ArrayList<CtxIdentifier>();
		mockHodIds = new ArrayList<CtxIdentifier>();
		mockDowIds = new ArrayList<CtxIdentifier>();
		
		mockSnpshtRegistryIds = new ArrayList<CtxIdentifier>();

		/*
		 * Define futures
		 */
		//mock symLoc
		mockSymLocIdFuture = new FutureTask<List<CtxIdentifier>>(
				new Runnable(){
					public void run() {
						mockSymLocIds.add(mockSymLocId);
					}
				}, mockSymLocIds);
		this.executeFuture((FutureTask<List<CtxIdentifier>>)mockSymLocIdFuture);

		//mock time of day
		mockTodIdFuture = new FutureTask<List<CtxIdentifier>>(
				new Runnable(){
					public void run(){
						mockTodIds.add(mockTodId);
					}
				}, mockTodIds);
		this.executeFuture((FutureTask<List<CtxIdentifier>>)mockTodIdFuture);

		//mock hour of day
		mockHodIdFuture = new FutureTask<List<CtxIdentifier>>(
				new Runnable(){
					public void run(){
						mockHodIds.add(mockHodId);
					}
				}, mockHodIds);
		this.executeFuture((FutureTask<List<CtxIdentifier>>)mockHodIdFuture);

		//mock day of week
		mockDowIdFuture = new FutureTask<List<CtxIdentifier>>(
				new Runnable(){
					public void run(){
						mockDowIds.add(mockDowId);
					}
				}, mockDowIds);
		this.executeFuture((FutureTask<List<CtxIdentifier>>)mockDowIdFuture);
		
		
		//mock snpshtRegistry
		mockSnpshtRegistryIdFuture = new FutureTask<List<CtxIdentifier>>(
				new Runnable(){
					public void run() {
						mockSnpshtRegistryIds.add(mockSnpshtRegistryId);
					}
				}, mockSnpshtRegistryIds);
		this.executeFuture((FutureTask<List<CtxIdentifier>>)mockSnpshtRegistryIdFuture);

		//mock snpshtRegistry object
		mockSnpshtRegistryObjectFuture = new FutureTask<CtxModelObject>(
				new Runnable(){
					public void run() {
						try {
							byte[] blobValue = SerialisationHelper.serialise(snpshtsRegistry);
							((CtxAttribute)mockSnpshtRegistryObject).setBinaryValue(blobValue);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}, mockSnpshtRegistryObject);
		System.out.println("mockAttribute = "+mockSnpshtRegistryObject);
		this.executeFuture((FutureTask<CtxModelObject>)mockSnpshtRegistryObjectFuture);
	}

	public void tearDown() throws Exception {
		mockCtxBroker = null;
		uam = null;
		stringId = null;
		snpshtsRegistry = null;
		mockPersonId = null;
		mockEntityId = null;
		mockSymLocId = null;
		//mockStatusId = null;
		//mockTempId = null;
		mockDowId = null;
		mockTodId = null;
		mockHodId = null;
		
		mockSnpshtRegistryId = null;
		mockSnpshtRegistryObject = null;
		mockPersonIds = null;
		mockSymLocIds = null;
		//mockStatusIds = null;
		//mockTempIds = null;
		mockDowIds = null;
		mockTodIds = null;
		mockHodIds = null;
		
		
		mockSnpshtRegistryIds = null;
		mockSymLocIdFuture = null;
		//mockStatusIdFuture = null;
		//mockTempIdFuture = null;
		mockDowIdFuture = null;
		mockTodIdFuture = null;
		mockHodIdFuture = null;
		
		mockSnpshtRegistryIdFuture = null;
		mockSnpshtRegistryObjectFuture = null;
	}

	@Ignore
	@Test
	public void testSnapshotManager() {
		/*
		 * UAM: initialiseUserActionMonitor
		 *-> ContextCommunicator: constructor
		 *-->SnapshotManager: constructor
		 *--->SnapshotManager: initialiseDefaultSnpsht
		 *---->SnapshotManager: retrieveSnpshtRegistry
		 */
		try {
			when(mockCtxBroker.lookup(CtxModelType.ATTRIBUTE, CtxAttributeTypes.LOCATION_SYMBOLIC)).thenReturn(mockSymLocIdFuture);
			//when(mockCtxBroker.lookup(CtxModelType.ATTRIBUTE, CtxAttributeTypes.STATUS)).thenReturn(mockStatusIdFuture);
			//when(mockCtxBroker.lookup(CtxModelType.ATTRIBUTE, CtxAttributeTypes.TEMPERATURE)).thenReturn(mockTempIdFuture);
			when(mockCtxBroker.lookup(CtxModelType.ATTRIBUTE, CtxAttributeTypes.HOUR_OF_DAY)).thenReturn(mockHodIdFuture);
			when(mockCtxBroker.lookup(CtxModelType.ATTRIBUTE, CtxAttributeTypes.TIME_OF_DAY)).thenReturn(mockTodIdFuture);
			when(mockCtxBroker.lookup(CtxModelType.ATTRIBUTE, CtxAttributeTypes.DAY_OF_WEEK)).thenReturn(mockDowIdFuture);
			
			//when(mockCtxBroker.lookup(CtxModelType.ATTRIBUTE, CtxAttributeTypes.SNAPSHOT_REG)).thenReturn(mockSnpshtRegistryIdFuture);
			when(mockCtxBroker.retrieveIndividualEntity(mockCssID)).thenReturn(new AsyncResult<IndividualCtxEntity>(this.ctxEntity));
			when(mockCtxBroker.retrieve(mockSnpshtRegistryId)).thenReturn(mockSnpshtRegistryObjectFuture);
		} catch (CtxException e) {
			e.printStackTrace();
		}
		//create class under test
		System.out.println("Creating snpshtManager");
		snpshtMgr = new SnapshotManager(mockCtxBroker, mockCssID);  //default snapshot created and registry retrieved
		try {
			verify(mockCtxBroker).lookup(CtxModelType.ATTRIBUTE, CtxAttributeTypes.LOCATION_SYMBOLIC);
			//verify(mockCtxBroker).lookup(CtxModelType.ATTRIBUTE, CtxAttributeTypes.STATUS);
			//verify(mockCtxBroker).lookup(CtxModelType.ATTRIBUTE, CtxAttributeTypes.TEMPERATURE);
			verify(mockCtxBroker).lookup(CtxModelType.ATTRIBUTE, CtxAttributeTypes.HOUR_OF_DAY);
			verify(mockCtxBroker).lookup(CtxModelType.ATTRIBUTE, CtxAttributeTypes.TIME_OF_DAY);
			verify(mockCtxBroker).lookup(CtxModelType.ATTRIBUTE, CtxAttributeTypes.DAY_OF_WEEK);
			
			verify(mockCtxBroker).lookup(CtxModelType.ATTRIBUTE, CtxAttributeTypes.SNAPSHOT_REG);
			verify(mockCtxBroker).retrieve(mockSnpshtRegistryId);
		} catch (CtxException e) {
			e.printStackTrace();
		}

		//try to retrieve new snapshot
		System.out.println("Requesting snapshot for volume");
		CtxAttributeIdentifier volumePrimary = new CtxAttributeIdentifier(mockEntityId, "volume", new Long(12345)); //serviceId-param attribute
		List<CtxAttributeIdentifier> retrievedSnpsht = snpshtMgr.getSnapshot(volumePrimary).getIDList();
		Assert.assertTrue(retrievedSnpsht.contains(mockSymLocId));
		Assert.assertTrue(retrievedSnpsht.contains(mockTodId));
		Assert.assertTrue(retrievedSnpsht.contains(mockHodId));
		Assert.assertTrue(retrievedSnpsht.contains(mockDowId));
		
		//Assert.assertTrue(retrievedSnpsht.contains(mockStatusId));
		//Assert.assertTrue(retrievedSnpsht.contains(mockTempId));

		//try to retrieve second new snapshot
		CtxAttributeIdentifier colourPrimary = new CtxAttributeIdentifier(mockEntityId, "colour", new Long(12345)); //serviceId-param attribute
		retrievedSnpsht = snpshtMgr.getSnapshot(colourPrimary).getIDList();
		Assert.assertTrue(retrievedSnpsht.contains(mockSymLocId));
		Assert.assertTrue(retrievedSnpsht.contains(mockTodId));
		Assert.assertTrue(retrievedSnpsht.contains(mockHodId));
		Assert.assertTrue(retrievedSnpsht.contains(mockDowId));
		//Assert.assertTrue(retrievedSnpsht.contains(mockStatusId));
		//Assert.assertTrue(retrievedSnpsht.contains(mockTempId));
		
		//try update existing snapshot
		CtxIdentifier mockActivityId = new CtxAttributeIdentifier(mockEntityId, "activity", new Long(12345));  //additional context attribute
		Snapshot newSnapshot = new Snapshot();
		newSnapshot.setTypeID(CtxAttributeTypes.LOCATION_SYMBOLIC, (CtxAttributeIdentifier)mockSymLocId);
		//newSnapshot.setTypeID(CtxAttributeTypes.TEMPERATURE, (CtxAttributeIdentifier)mockTempId);
		newSnapshot.setTypeID(CtxAttributeTypes.TIME_OF_DAY, (CtxAttributeIdentifier)mockTodId);
		
		newSnapshot.setTypeID("activity", (CtxAttributeIdentifier)mockActivityId);
		snpshtMgr.updateSnapshot(volumePrimary, newSnapshot);

		//try to retrieve updated existing snapshot
		retrievedSnpsht = snpshtMgr.getSnapshot(volumePrimary).getIDList();
		Assert.assertTrue(retrievedSnpsht.contains(mockSymLocId));
		Assert.assertTrue(retrievedSnpsht.contains(mockTodId));
		Assert.assertTrue(retrievedSnpsht.contains(mockActivityId));

		//check non-updated existing snapshot
		retrievedSnpsht = snpshtMgr.getSnapshot(colourPrimary).getIDList();
		Assert.assertTrue(retrievedSnpsht.contains(mockSymLocId));
		Assert.assertTrue(retrievedSnpsht.contains(mockTodId));
		Assert.assertTrue(retrievedSnpsht.contains(mockHodId));
		Assert.assertTrue(retrievedSnpsht.contains(mockDowId));
	}

	private void executeFuture(FutureTask task){
		task.run();
	}

}
