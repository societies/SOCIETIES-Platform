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
package org.societies.privacytrust.privacyprotection.assessment.logic;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.DataAccessLogEntry;

/**
 * Parses the log and creates report about classes and identities that accessed local data.
 * The report should be suitable to be displayed in web browser.
 *
 * @author Mitja Vardjan
 *
 */
public class DataAccessAnalyzer {
	private static Logger LOG = LoggerFactory.getLogger(DataTransferAnalyzer.class);

	private List<DataAccessLogEntry> dataAccess;
	
	public DataAccessAnalyzer(List<DataAccessLogEntry> dataAccess) {
		LOG.info("Constructor");
		this.dataAccess = dataAccess;
	}

	public List<DataAccessLogEntry> getDataAccess(IIdentity requestor, Date start, Date end) {
		
		List<DataAccessLogEntry> matchedEntries = new ArrayList<DataAccessLogEntry>();
		String requestorJid;
		IIdentity identity;
		
		if (requestor == null || requestor.getJid() == null) {
			LOG.warn("getDataAccess({}): requestor or requestor JID is null", requestor);
			return matchedEntries;
		}
		
		requestorJid = requestor.getJid();
		for (DataAccessLogEntry da : dataAccess) {
			identity = da.getRequestor();
			if (identity == null) {
				continue;
			}
			if (requestorJid.equals(identity.getJid()) &&
					da.getTime().after(start) && da.getTime().before(end)) {
				matchedEntries.add(da);
			}
		}
		return matchedEntries;
	}
	
	public List<DataAccessLogEntry> getDataAccess(String requestor, Date start, Date end) {
		
		List<DataAccessLogEntry> matchedEntries = new ArrayList<DataAccessLogEntry>();

		if (requestor == null) {
			LOG.warn("getDataAccess({}): requestor is null", requestor);
			return matchedEntries;
		}
		
		for (DataAccessLogEntry da : dataAccess) {
			if (requestor.equals(da.getRequestorClass()) &&
					da.getTime().after(start) && da.getTime().before(end)) {
				matchedEntries.add(da);
			}
		}
		return matchedEntries;
	}
	
	public List<DataAccessLogEntry> getDataAccessForBundle(String requestor, Date start, Date end) {
		
		List<DataAccessLogEntry> matchedEntries = new ArrayList<DataAccessLogEntry>();

		if (requestor == null) {
			LOG.warn("getDataAccessForBundle({}): requestor is null", requestor);
			return matchedEntries;
		}
		
		for (DataAccessLogEntry da : dataAccess) {
			if (da.getRequestorBundles().contains(requestor) &&
					da.getTime().after(start) && da.getTime().before(end)) {
				matchedEntries.add(da);
			}
		}
		return matchedEntries;
	}
	
	/**
	 * Get number of events in certain time period where given requestor accessed local data.
	 * 
	 * @param requestor Identity of the requestor (the one who requested data access)
	 * @param start Match only events after this time
	 * @param end Match only events before this time
	 * @return All events where requestor matches
	 */
	public int getNumDataAccessEvents(IIdentity requestor, Date start, Date end) {
		
		List<DataAccessLogEntry> matchedEntries = getDataAccess(requestor, start, end);
		
		return matchedEntries.size();
	}
	
	/**
	 * Get number of events in certain time period where given requestor accessed local data.
	 * 
	 * @param requestorClass Class name of the requestor (the one who requested data access)
	 * @param start Match only events after this time
	 * @param end Match only events before this time
	 * @return All events where requestor matches
	 */
	public int getNumDataAccessEvents(String requestorClass, Date start, Date end) {
		
		List<DataAccessLogEntry> matchedEntries = getDataAccess(requestorClass, start, end);
		
		return matchedEntries.size();
	}
	
	/**
	 * Get number of events in certain time period where given requestor accessed local data.
	 * 
	 * @param requestorBundle Symbolic name of the requestor bundle (the one who requested data access)
	 * @param start Match only events after this time
	 * @param end Match only events before this time
	 * @return All events where requestor matches
	 */
	public int getNumDataAccessEventsForBundle(String requestorBundle, Date start, Date end) {
		
		List<DataAccessLogEntry> matchedEntries = getDataAccessForBundle(requestorBundle, start, end);
		
		return matchedEntries.size();
	}
	
	public List<IIdentity> getDataAccessRequestors() {
		
		List<IIdentity> matches = new ArrayList<IIdentity>();
		IIdentity requestor;
		
		for (DataAccessLogEntry da : dataAccess) {
			requestor = da.getRequestor();
			if (!matches.contains(requestor) && requestor != null) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("getDataAccessRequestors(): Adding identity {}", requestor);
				}
				matches.add(requestor);
			}
		}
		return matches;
	}
	
	public List<String> getDataAccessRequestorClasses() {
		
		List<String> matches = new ArrayList<String>();
		String requestor;
		
		for (DataAccessLogEntry da : dataAccess) {
			requestor = da.getRequestorClass();
			if (!matches.contains(requestor) && requestor != null) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("getDataAccessRequestorClasses(): Adding class {}", requestor);
				}
				matches.add(requestor);
			}
		}
		return matches;
	}
	
	public List<String> getDataAccessRequestorBundles() {
		
		List<String> matches = new ArrayList<String>();
		List<String> requestors;
		
		for (DataAccessLogEntry da : dataAccess) {
			requestors = da.getRequestorBundles();
			for (String requestor : requestors) {
				if (!matches.contains(requestor) && requestor != null) {
					if (LOG.isDebugEnabled()) {
						LOG.debug("getDataAccessRequestorBundles(): Adding bundle {}", requestor);
					}
					matches.add(requestor);
				}
			}
		}
		return matches;
	}
	
	public Map<IIdentity, Integer> getNumDataAccessEventsForAllIdentities(Date start, Date end) {
		
		Map<IIdentity, Integer> result = new HashMap<IIdentity, Integer>();
		List<IIdentity> requestors = getDataAccessRequestors();
		
		for (IIdentity requestor : requestors) {
			result.put(requestor, getNumDataAccessEvents(requestor, start, end));
		}
		
		return result;
	}
	
	public Map<String, Integer> getNumDataAccessEventsForAllClasses(Date start, Date end) {

		Map<String, Integer> result = new HashMap<String, Integer>();
		List<String> requestors = getDataAccessRequestorClasses();
		
		for (String requestor : requestors) {
			result.put(requestor, getNumDataAccessEvents(requestor, start, end));
		}
		
		return result;
	}
	
	public Map<String, Integer> getNumDataAccessEventsForAllBundles(Date start, Date end) {

		Map<String, Integer> result = new HashMap<String, Integer>();
		List<String> requestors = getDataAccessRequestorBundles();
		
		for (String requestor : requestors) {
			result.put(requestor, getNumDataAccessEventsForBundle(requestor, start, end));
		}
		
		return result;
	}
}
