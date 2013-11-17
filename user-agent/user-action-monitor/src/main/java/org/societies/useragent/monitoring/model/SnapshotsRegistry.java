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

package org.societies.useragent.monitoring.model;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;

public class SnapshotsRegistry implements Serializable{

	private Logger LOG = LoggerFactory.getLogger(this.getClass());

	private static final long serialVersionUID = 1L;
	Hashtable<CtxAttributeIdentifier, Snapshot> snpshtMappings;
	
	public SnapshotsRegistry(){
		snpshtMappings = new Hashtable<CtxAttributeIdentifier, Snapshot>();
	}
	
	public void addMapping(CtxAttributeIdentifier primary, Snapshot snapshot){
		snpshtMappings.put(primary, snapshot);
	}
	
	public void removeMapping(CtxAttributeIdentifier primary){
		snpshtMappings.remove(primary);
	}
	
	public Snapshot getSnapshot(CtxAttributeIdentifier primary){
		return snpshtMappings.get(primary);
	}
	
	public void updateMapping(CtxAttributeIdentifier primary, Snapshot newSnapshot){
		snpshtMappings.put(primary, newSnapshot);
		
	}
	
	
	public void updateSnapshots(String type, CtxAttributeIdentifier ID, boolean logEnabled){
		Iterator<Snapshot> snpshtMappings_it = snpshtMappings.values().iterator();
		while(snpshtMappings_it.hasNext()){
			Snapshot nextSnpsht = snpshtMappings_it.next();
			if(nextSnpsht.containsType(type)){
				nextSnpsht.setTypeID(type, ID);
				if (logEnabled){
					this.LOG.info("#ctxAttributesFix#: Fixed Snapshot key ("+type+")");
				}
			}
		}
	}

	public void fixWrongKey(CtxEntityIdentifier id, CtxAttributeIdentifier ctxIdentifier) {
		
		//first update all snapshots regardless of primarykey:
		this.updateSnapshots(ctxIdentifier.getType(), ctxIdentifier, true);
		
		//now update snapshotmappings key
		Enumeration<CtxAttributeIdentifier> keys = this.snpshtMappings.keys();
		while(keys.hasMoreElements()){
			CtxAttributeIdentifier key = keys.nextElement();
			if (!key.getScope().toUriString().equalsIgnoreCase(id.toUriString())){
				this.LOG.info("#ctxAttributesFix#: Found key : "+key+" in snapshot registry");
				if (key.getType().equalsIgnoreCase(ctxIdentifier.getType())){
					this.LOG.info("#ctxAttributesFix#: Removing key: "+key+" from snapshot mappings. ");
					//if found:
					//remove from snapshotregistry but keep snapshot
					Snapshot removedSnapshot = snpshtMappings.remove(key);

					//put new key (ctxIdentifier) and updated snapshot to snapshot registry
					this.snpshtMappings.put(ctxIdentifier, removedSnapshot);
					
					this.LOG.info("#ctxAttributesFix#: Put new snapshotRegistry key: "+key.toUriString());
					return ;
				}
			}
		}
		
		this.LOG.info("#ctxAttributesFix#: ctxID: "+ctxIdentifier.toUriString()+" not found as primary key in snpshtMappings.");
	}
}
