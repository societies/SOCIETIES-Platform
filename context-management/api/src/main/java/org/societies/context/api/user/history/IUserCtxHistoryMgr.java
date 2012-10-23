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
package org.societies.context.api.user.history;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxHistoryAttribute;

/**
 * @author <a href="mailto:nikosk@cn.ntua.gr">Nikos Kalatzis</a> (ICCS)
 */
public interface IUserCtxHistoryMgr {

	/**
	 * Disables Context Recording.
	 * 
	 * @since 0.0.1
	 */
	public void disableCtxRecording();

	/**
	 * Enables Context Recording.
	 * 
	 * @since 0.0.1
	 */
	public void enableCtxRecording();

	
	/**
	 * This method allows to set a primary context attribute that will be stored in context History Database
	 * upon value update along with a list of other context attributes. 
	 * 
	 * @param primaryAttrIdentifier
	 * @param listOfEscortingAttributeIds
	 * @throws CtxException 
	 * @since 0.0.1
	 */
	public Boolean setCtxHistoryTuples(CtxAttributeIdentifier primaryAttrIdentifier,
			List<CtxAttributeIdentifier> listOfEscortingAttributeIds) throws CtxException;

	/**
	 * This method allows to get the list of the context attribute identifiers that consist the snapshot that is stored
	 * in context history database along with the a primary context attribute.  
	 * 
	 * @param primaryAttrIdentifier
	 * @param listOfEscortingAttributeIds
	 * @throws CtxException 
	 * @since 0.0.1
	 */
	public List<CtxAttributeIdentifier> getCtxHistoryTuples(CtxAttributeIdentifier primaryAttrIdentifier,
			List<CtxAttributeIdentifier> listOfEscortingAttributeIds) throws CtxException;

	/**
	 * This method allows to update the list of the context attribute identifiers that consist the snapshot that is stored
	 * in context history database along with the a primary context attribute.  
	 *  
	 * @param primaryAttrIdentifier
	 * @param listOfEscortingAttributeIds
	 * @throws CtxException 
	 * @since 0.0.1
	 */
	public List<CtxAttributeIdentifier> updateCtxHistoryTuples(CtxAttributeIdentifier primaryAttrIdentifier,
			List<CtxAttributeIdentifier> listOfEscortingAttributeIds) throws CtxException;

	/**
	 * This method allows to remove the list of the context attribute identifiers that consist the snapshot that is stored
	 * in context history database along with the a primary context attribute.  
	 * 
	 * @param primaryAttrIdentifier
	 * @param listOfEscortingAttributeIds
	 * @throws CtxException 
	 * @since 0.0.1
	 */
	public Boolean removeCtxHistoryTuples(
			CtxAttributeIdentifier primaryAttrIdentifier,
			List<CtxAttributeIdentifier> listOfEscortingAttributeIds) throws CtxException;
	
	
	/**
	 * Removes recorded history for the indicated Context Attribute from the 
	 * start of the recorded history until the end. 
	 * 
	 * @param ctxAttribute
	 * @param startDate
	 * @param endDate
	 * @return number of removed records
	 * @since 0.0.1
	 */
	public int removeCtxHistory(CtxAttribute ctxAttribute, Date startDate, Date endDate) throws CtxException;

	/**
	 * Removes recorded history for the indicated type from the start of the 
	 * recorded history until the end.
	 * 
	 * @param type
	 * @param startDate
	 * @param endDate
	 * @return number of removed records
	 * @since 0.0.1
	 */
	public int removeHistory(String type, Date startDate, Date endDate) throws CtxException;

	/**
	 * Returns a list of <code>CtxHistoryAttribute</code> objects recorded for 
	 * the specified context attribute.
	 * 
	 * @param CtxAttributeIdentifier
	 * @return list of historic Attributes
	 * @since 0.0.1
	 */
	public List<CtxHistoryAttribute> retrieveHistory(CtxAttributeIdentifier attrId) throws CtxException;

	
	/**
	 * Returns a list of <code>CtxHistoryAttribute</code> objects recorded for
	 * the specified context attribute for a time period that starts at the indicated 
	 * startDate and ends at the indicated endDate.
	 * 
	 * @param attrId
	 * @param startDate
	 * @param endDate
	 * @return list of historic Attributes
	 * @since 0.0.1
	 */
	public List<CtxHistoryAttribute> retrieveHistory(CtxAttributeIdentifier attrId, Date startDate, Date endDate) throws CtxException;

	/**
	 * Returns a list of <code>CtxHistoryAttribute</code> objects recorded for
	 * the specified context attribute.
	 * 
	 * @param attrId
	 * @param modificationIndex
	 * @since 0.0.1
	 */
	public List<CtxHistoryAttribute> retrieveHistory(CtxAttributeIdentifier attrId,int modificationIndex) throws CtxException;
	
	
	/**
	 * This method returns a linked map with key the CtxAttribute and value 
	 * a list of CtxAttributes recorded on the same time.
     * 
	 * @param primaryAttrID
	 * @param listOfEscortingAttributeIds
	 * @param startDate
	 * @param endDate
	 * @return map
	 * @since 0.0.1
	 */
	public Map<CtxHistoryAttribute, List<CtxHistoryAttribute>>  retrieveHistoryTuples(CtxAttributeIdentifier primaryAttrID, List<CtxAttributeIdentifier> listOfEscortingAttributeIds, Date startDate, Date endDate) throws CtxException;

	
	
	/**
	 * Stores the historic Attribute to HoC Database.
	 * 
	 * @param hocAttribute
	 * @param date
	 */
	public void storeHoCAttribute(CtxAttribute hocAttribute) throws CtxException;
	
	/**
	 * Creates and stores a historic Attribute to HoC Database. Mainly used for testing purposes
	 * 
	 * @param hocAttribute
	 * @param date
	 */
	public CtxHistoryAttribute createHistoryAttribute(CtxAttribute ctxAttribute) throws CtxException;
	
	/**
	 * Stores the historic Attribute to HoC Database.
	 * 
	 * @param hocAttribute
	 * @param date
	 */
	public CtxHistoryAttribute createHistoryAttribute(CtxAttributeIdentifier attID, Date date, Serializable value, CtxAttributeValueType valueType) throws CtxException;
		
	
	public void printHocDB();
}