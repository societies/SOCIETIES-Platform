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

package org.societies.useragent.monitoring;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.internal.context.broker.ICtxBroker;

//register listeners to update snapshot if context attributes not yet available
public class SnapshotManager {

	private ICtxBroker ctxBroker;
	Hashtable <String, String[]> snapshots;
	Hashtable <String, List<String>> snapshotUpdates;

	/*
	 * SNAPSHOT DEFINITIONS
	 */
	String[][] snapshotDefinitions = {
			{"symLoc", "status", "activity"},  //SNAPSHOT 1
			{"symLoc", "day"}  //SNAPSHOT 2
	};
	/*
	 * END DEFINITIONS
	 */

	public SnapshotManager(ICtxBroker ctxBroker){
		this.ctxBroker = ctxBroker;
		snapshotUpdates = new Hashtable <String, List<String>>();
		initialiseSnapshots();
	}

	private void initialiseSnapshots(){
		snapshots = new Hashtable <String, String[]>();
		for(int i = 0; i<snapshotDefinitions.length; i++){
			snapshots.put("snapshot"+i, snapshotDefinitions[i]);
		}
	}
	
	public void confirmSnapshot(String key){
		if(snapshotUpdates.containsKey(key)){
			List<String> contextAttributes = snapshotUpdates.get(key);
			for(String s: contextAttributes){
				//CtxAttributeIdentifier 
			}
		}
	}

	public List<CtxAttributeIdentifier> getSnapshot(String snapshotName){
		ArrayList<CtxAttributeIdentifier> snapshot = new ArrayList<CtxAttributeIdentifier>();
		String[] definition = snapshots.get(snapshotName);
		for(int i = 0; i<definition.length; i++){  //for each item in snapshot definition -> get context attribute if exists
			
		}
		return snapshot;
	}
	
	private CtxAttributeIdentifier getAttribute(String attributeName){
		CtxAttributeIdentifier attribute = null;
		return attribute;
	}
}
