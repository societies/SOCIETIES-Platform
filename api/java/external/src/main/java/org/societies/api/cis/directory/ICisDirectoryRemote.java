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

import org.societies.api.cis.directory.ICisDirectoryCallback;
import org.societies.api.schema.cis.directory.CisAdvertisementRecord;
import org.societies.utilities.annotations.SocietiesExternalInterface;
import org.societies.utilities.annotations.SocietiesExternalInterface.SocietiesInterfaceType;


/**
 * A remote interface to the CisDirectory {@link org.societies.api.cis.directory.ICisDirectory}
 *
 * @author mmannion
 */

@SocietiesExternalInterface(type=SocietiesInterfaceType.PROVIDED)
public interface ICisDirectoryRemote {
    
    /**
     * Adds the Cis Advertisement Record {@link org.societies.api.schema.cis.directory.CisAdvertisementRecord} to the CisDirectory.
     *
     * @param cisAdvert the Cis Advertisement Record {@link org.societies.api.schema.cis.directory.CisAdvertisementRecord} to be added to the CisDirectory
     */
    public void addCisAdvertisementRecord(CisAdvertisementRecord cisAdvert);
    
    /**
     * Delete the specified Cis Advertisement Record {@link org.societies.api.schema.cis.directory.CisAdvertisementRecord} from the CisDirectory
     *
     * @param cisAdvert the Cis Advertisement Record {@link org.societies.api.schema.cis.directory.CisAdvertisementRecord} to be deleted from the CisDirectory
     */
    public void deleteCisAdvertisementRecord(CisAdvertisementRecord cisAdvert);
	
	/**
	 * Updates the Cis Advertisement Record {@link  org.societies.api.schema.cis.directory.CisAdvertisementRecord} in the CisDirectory
	 *
	 * @param oldCisAdvert the old Cis Advertisement Record {@link org.societies.api.schema.cis.directory.CisAdvertisementRecord} to be removed from CisDirectory
	 * @param updatedCisAdvert the updated Cis Advertisement Record {@link org.societies.api.schema.cis.directory.CisAdvertisementRecord} to be added to the CisDirectory
	 */
	public void updateCisAdvertisementRecord(CisAdvertisementRecord oldCisAdvert,
			CisAdvertisementRecord updatedCisAdvert);

	/**
	 * Find all Cis Advertisement Records {@link org.societies.api.schema.cis.directory.CisAdvertisementRecord} in the CisDirectory.
	 *
	 * @param callback the callback class of the remote CisDirectory client
	 */
	public void findAllCisAdvertisementRecords(ICisDirectoryCallback callback);
	
	/**
	 * Find all Cis Advertisement Records {@link org.societies.api.schema.cis.directory.CisAdvertisementRecord} that match the specified filter 
	 *
	 * @param cisFilter the filter to be applied to the CisDirectory database search
	 * @param callback the callback class of the remote Cis Directory client
	 */
	public void findForAllCis(CisAdvertisementRecord filteredcis, String filter, ICisDirectoryCallback callback);
	
	
	/**
	 * Search for a advertisement record based on CisID  in the CisDirectory
	 *
	 * @param cis_id ID of cis advertisment to return
	 */
	public void searchByID(String cisID, ICisDirectoryCallback callback);
	
	/**
	 * Search for a list of advertisement records based on list of CisID  in the CisDirectory
	 *
	 * @param cisIds List of ID of cis advertisments to return
	 */
	public void searchByIDS(List<String> cisIDs, ICisDirectoryCallback callback);
	
	
}



