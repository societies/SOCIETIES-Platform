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

package org.societies.api.css.directory;

import java.util.List;

import org.societies.api.css.directory.ICssDirectoryCallback;
import org.societies.api.schema.css.directory.CssAdvertisementRecord;
import org.societies.utilities.annotations.SocietiesExternalInterface;
import org.societies.utilities.annotations.SocietiesExternalInterface.SocietiesInterfaceType;


/**
 * A remote interface to the CssDirectory {@link org.societies.api.css.directory.ICssDirectory}
 *
 * @author mmannion
 */

@SocietiesExternalInterface(type=SocietiesInterfaceType.PROVIDED)
public interface ICssDirectoryRemote {
    
    /**
     * Adds the Css Advertisement Record {@link org.societies.api.schema.css.directory.CssAdvertisementRecord} to the CssDirectory.
     *
     * @param cssAdvert the Css Advertisement Record {@link org.societies.api.schema.css.directory.CssAdvertisementRecord} to be added to the CssDirectory
     */
    public void addCssAdvertisementRecord(CssAdvertisementRecord cssAdvert);
    
    /**
     * Delete the specified Css Advertisement Record {@link org.societies.api.schema.css.directory.CssAdvertisementRecord} from the CssDirectory
     *
     * @param cssAdvert the Css Advertisement Record {@link org.societies.api.schema.css.directory.CssAdvertisementRecord} to be deleted from the CssDirectory
     */
    public void deleteCssAdvertisementRecord(CssAdvertisementRecord cssAdvert);
	
	/**
	 * Updates the Css Advertisement Record {@link  org.societies.api.schema.css.directory.CssAdvertisementRecord} in the CssDirectory
	 *
	 * @param oldCssAdvert the old Css Advertisement Record {@link org.societies.api.schema.css.directory.CssAdvertisementRecord} to be removed from CssDirectory
	 * @param updatedCssAdvert the updated Css Advertisement Record {@link org.societies.api.schema.css.directory.CssAdvertisementRecord} to be added to the CssDirectory
	 */
	public void updateCssAdvertisementRecord(CssAdvertisementRecord oldCssAdvert,
			CssAdvertisementRecord updatedCssAdvert);

	/**
	 * Find all Css Advertisement Records {@link org.societies.api.schema.css.directory.CssAdvertisementRecord} in the CssDirectory.
	 *
	 * @param callback the callback class of the remote CssDirectory client
	 */
	public void findAllCssAdvertisementRecords(ICssDirectoryCallback callback);
	
	/**
	 * Find all Css Advertisement Records {@link org.societies.api.schema.css.directory.CssAdvertisementRecord} that match the specified filter 
	 *
	 * @param cssFilter the filter to be applied to the CssDirectory database search
	 * @param callback the callback class of the remote Css Directory client
	 */
	public void findForAllCss(CssAdvertisementRecord cssFilter, ICssDirectoryCallback callback);
	
	
	/**
	 * Description : Queries by css id's
	 * 
	 * @param cssidList
	 *            list of id's to return ad rec  
	 *  @param callback the callback class of the remote Css Directory client
	 */
	public void searchByID(
			List<String> cssIdList, ICssDirectoryCallback callback);
	
	
}



