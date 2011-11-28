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

package org.societies.context.user.history.api.platform;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.societies.context.model.api.CtxAttribute;
import org.societies.context.model.api.CtxAttributeIdentifier;
import org.societies.context.model.api.CtxHistoryAttribute;

/**
 * @author <a href="mailto:nikosk@cn.ntua.gr">Nikos Kalatzis</a> (ICCS)
 * @version 0.0.1
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
	 * This method returns a list of CtxAttributeIdentifiers corresponding 
	 * to the Context Attributed recorced in the Context History.
	 * 
	 * @param primaryAttrIdentifier
	 * @return list of historic Attributes
	 * @since 0.0.1
	 */
	public List<List <CtxAttributeIdentifier>> getHistoryTuplesID(CtxAttributeIdentifier primaryAttrIdentifier);

	/**
	 * Registers to Context History a list of escording attribute Ids 
	 * corresponding to a Context Attribute. 
	 * 
	 * @param primaryAttrIdentifier
	 * @param listOfEscortingAttributeIds
	 * @since 0.0.1
	 */
	public void registerHistoryTuples(CtxAttributeIdentifier primaryAttrIdentifier, List<CtxAttributeIdentifier> listOfEscortingAttributeIds);

	/**
	 * Registers to Context History a list of escording attribute types 
	 * corresponding to a Context Attribute.
	 * 
	 * @param primaryAttrIdentifier
	 * @param listOfEscortingAttributeTypes
	 * @since 0.0.1
	 */
	public void registerHistoryTuples(CtxAttributeIdentifier primaryAttrIdentifier, CtxAttributeIdentifier listOfEscortingAttributeTypes);

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
	public int removeHistory(CtxAttribute ctxAttribute, Date startDate, Date endDate);

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
	public int removeHistory(String type, Date startDate, Date endDate);

	/**
	 * Returns a list of <code>CtxHistoryAttribute</code> objects recorded for 
	 * the specified context attribute.
	 * 
	 * @param ctxAttribute
	 * @return list of historic Attributes
	 * @since 0.0.1
	 */
	public List<CtxHistoryAttribute> retrieveHistory(CtxAttribute ctxAttribute);

	/**
	 * Returns a list of <code>CtxHistoryAttribute</code> objects recorded for
	 * the specified context attribute for a time period that starts at the indicated 
	 * startDate and ends at the indicated endDate.
	 * 
	 * @param ctxAttribute
	 * @param startDate
	 * @param endDate
	 * @return list of historic Attributes
	 * @since 0.0.1
	 */
	public List<CtxHistoryAttribute> retrieveHistory(CtxAttribute ctxAttribute, Date startDate, Date endDate);

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
	public Map<CtxAttribute, List<CtxAttribute>> retrieveHistoryTuples(CtxAttributeIdentifier primaryAttrID, List<CtxAttributeIdentifier> listOfEscortingAttributeIds, Date startDate, Date endDate);

}