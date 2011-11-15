package org.societies.context.community.history.api.platform;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.societies.context.model.api.CtxAttribute;
import org.societies.context.model.api.CtxAttributeIdentifier;
import org.societies.context.model.api.CtxHistoryAttribute;


/**
 * @author nikosk
 * @version 1.0
 * @created 12-Nov-2011 7:15:15 PM
 */
public interface ICommunityCtxHistoryMgr {

	public void disableCommCtxRecording();

	public void enableCommCtxRecording();

	/**
	 * 
	 * @param primaryAttrIdentifier
	 */
	public List<List <CtxAttributeIdentifier>> getHistoryTuplesID(CtxAttributeIdentifier primaryAttrIdentifier);

	/**
	 * 
	 * @param primaryAttrIdentifier
	 * @param listOfEscortingAttributeIds
	 */
	public void registerHistoryTuples(CtxAttributeIdentifier primaryAttrIdentifier, List<CtxAttributeIdentifier> listOfEscortingAttributeIds);

	/**
	 * 
	 * @param primaryAttrIdentifier
	 * @param listOfEscortingAttributeTypes
	 */
	public void registerHistoryTuples(CtxAttributeIdentifier primaryAttrIdentifier, CtxAttributeIdentifier listOfEscortingAttributeTypes);

	/**
	 * 
	 * @param ctxAttribute
	 * @param startDate
	 * @param endDate
	 */
	public int removeHistory(CtxAttribute ctxAttribute, Date startDate, Date endDate);

	/**
	 * 
	 * @param ctxAttribute
	 */
	public List<CtxHistoryAttribute> retrieveHistory(CtxAttribute ctxAttribute);

	/**
	 * 
	 * @param ctxAttribute
	 * @param startDate
	 * @param endDate
	 */
	public List<CtxHistoryAttribute> retrieveHistory(CtxAttribute ctxAttribute, Date startDate, Date endDate);

	/**
	 * 
	 * @param primaryAttrID
	 * @param listOfEscortingAttributeIds
	 * @param startDate
	 * @param endDate
	 */
	public Map<CtxAttribute, List<CtxAttribute>> retrieveHistoryTuples(CtxAttributeIdentifier primaryAttrID, List<CtxAttributeIdentifier> listOfEscortingAttributeIds, Date startDate, Date endDate);

}