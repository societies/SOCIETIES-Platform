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
package org.societies.api.cis.directory;

import java.util.List;
import java.util.concurrent.Future;

import org.societies.api.cis.directory.ICisAdvertisementRecord;
import org.societies.api.schema.cis.directory.CisAdvertisementRecord;
import org.societies.utilities.annotations.SocietiesExternalInterface;
import org.societies.utilities.annotations.SocietiesExternalInterface.SocietiesInterfaceType;

/**
 * Interface used to allow interaction with the CIS Directory
 * @author Babak.Farshchian@sintef.no
 * @author Liam Marshall
 *
 */
@SocietiesExternalInterface(type=SocietiesInterfaceType.PROVIDED)
public interface ICisDirectory {
	
	/**
	 * Description: Search method to search by name for CIS Advertisements that return an array of CISAdvertisementRecords.
	 * 
	 * @return Array ICisAdvertisementRecord[]
	 */
	ICisAdvertisementRecord[] searchByName(String cisName);
	
	/**
	 * Description: Search method to search by Owner for CIS Advertisements that return an array of CISAdvertisementRecords.
	 * 
	 * @return Array ICisAdvertisementRecord[]
	 */
	ICisAdvertisementRecord[] searchByOwner(String ownerId);
	
	/**
	 * Description: Search method to search by  for CIS Advertisements that return an array of CISAdvertisementRecords.
	 * 
	 * @return Array ICisAdvertisementRecord[]
	 */
	ICisAdvertisementRecord[] searchByUri(String uri);
	
	/**
	 * Description: Search method to search by CIS ID for CIS Advertisements that return an array of CISAdvertisementRecords ( will at most return 1, as cis id is unique).
	 * 
	 * @return list of CisAdvertisementRecord from CIS directory
	 */
	Future<List<CisAdvertisementRecord>> searchByID(String cisID);
	
	/**
	 * Description: Search method to search by  list of cisIDs for CIS Advertisements that return an array of CISAdvertisementRecords 
	 * 
	 * @return list of CisAdvertisementRecord from CIS directory
	 */
	Future<List<CisAdvertisementRecord>> searchByIDS(List<String> cisID);
	
	/**
	 * Description: Register a CIS 
	 * 
	 * @param: CisAdevertisementRecord
	 *  
	 * @return Boolean
	 */
	Boolean RegisterCis (ICisAdvertisementRecord cis);
	
	/**
	 * Description: UnRegister a CIS 
	 * 
	 * @param: CisAdevertisementRecord
	 *  
	 * @return Boolean
	 */
	Boolean UnregisterCis (ICisAdvertisementRecord cis);
	
	/**
	 * Description: This method is used to add CIS Directories that reside on other nodes.
	 * 
	 * @param directoryURI URI for the directory to be added.
	 * @param cssId ID for the CSS where the new directory resides.
	 * @param synchMode One of several modes for synchronizing with the new directory. E.g. pull or push.
	 * 
	 */
	Integer AddPeerDirectory(String directoryURI, String cssId, Integer synchMode);
	
	/**
	 * Description: Ping method for checking whether this Directory is alive.
	 */
	Boolean ping();
	
	/**
	 * Description: A method that will return the current URI for this Directory. This URI might be fetched
	 * from XMPP name-space or be a web service.
	 * 
	 * @return String
	 */
	
	public String getURI();
	
	/**
	 * Description: This method provide interface to add new CiS object to CIS
	 * directory
	 * 
	 * @param cis
	 *            object to be added to directory
	 */
	void addCisAdvertisementRecord(CisAdvertisementRecord cisAdvert);

	/**
	 * Description: This method allows to delete specific CIS entry from CIS
	 * Directory
	 * 
	 * @param cis
	 *            object to be deleted from directory
	 */
	void deleteCisAdvertisementRecord(CisAdvertisementRecord cisAdvert);

	/**
	 * Description : This method can be used to update the changes in the CIS
	 * which is already exists in the CIS directory
	 * 
	 * @param cis
	 *            to be updated or replaced
	 * @param update
	 *            new cis object to be placed in the directory
	 */
	void updateCisAdvertisementRecord(CisAdvertisementRecord oldCisAdvert,
			CisAdvertisementRecord updatedCisAdvert);

	/**
	 * Description : Queries list of CIS available in the CIS directory
	 * 
	 * @return list of CisAdvertisementRecord from CIS directory
	 */
	Future<List<CisAdvertisementRecord>> findAllCisAdvertisementRecords();

	/**
	 * Description : Queries list of CIS object with CIS advertisement record
	 * filter
	 * 
	 * @param cisFilter
	 *            for which list of CIS will retrieved from directory
	 * @return list of CisAdvertisementRecords
	 */
	Future<List<CisAdvertisementRecord>> findForAllCis( CisAdvertisementRecord filteredcis, String filter);
}
