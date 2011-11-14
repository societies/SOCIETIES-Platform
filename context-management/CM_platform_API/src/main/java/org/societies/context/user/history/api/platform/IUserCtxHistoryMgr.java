import WP5.Context.Informational.ContextAttributeValueType;
import WP5.Context.Informational.ContextAttribute;

/**
 * @author nikosk
 * @version 1.0
 * @created 12-Nov-2011 7:15:15 PM
 */
public interface IUserCtxHistoryMgr {

	public void disableCtxRecording();

	public void enableCtxRecording();

	/**
	 * 
	 * @param primaryAttrIdentifier
	 */
	public List<List <ContextAttributeIdentifier>> getHistoryTuplesID(ContextAttributeIdentifier primaryAttrIdentifier);

	/**
	 * 
	 * @param primaryAttrIdentifier
	 * @param listOfEscortingAttributeIds
	 */
	public void registerHistoryTuples(ContextAttributeIdentifier primaryAttrIdentifier, List<ContextAttributeIdentifier> listOfEscortingAttributeIds);

	/**
	 * 
	 * @param primaryAttrIdentifier
	 * @param listOfEscortingAttributeTypes
	 */
	public void registerHistoryTuples(ContextAttributeIdentifier primaryAttrIdentifier, ContextAttributeValueType listOfEscortingAttributeTypes);

	/**
	 * 
	 * @param ctxAttribute
	 * @param startDate
	 * @param endDate
	 */
	public int removeHistory(ContextAttribute ctxAttribute, Date startDate, Date endDate);

	/**
	 * 
	 * @param type
	 * @param startDate
	 * @param endDate
	 */
	public int removeHistory(String type, Date startDate, Date endDate);

	/**
	 * 
	 * @param ctxAttribute
	 */
	public List<ContextHistoryAttribute> retrieveHistory(ContextAttribute ctxAttribute);

	/**
	 * 
	 * @param ctxAttribute
	 * @param startDate
	 * @param endDate
	 */
	public List<ContextHistoryAttribute> retrieveHistory(ContextAttribute ctxAttribute, Date startDate, Date endDate);

	/**
	 * 
	 * @param primaryAttrID
	 * @param listOfEscortingAttributeIds
	 * @param startDate
	 * @param endDate
	 */
	public Map<ContextAttribute, List<ContextAttribute>> retrieveHistoryTuples(ContextAttributeIdentifier primaryAttrID, List<ContextAttributeIdentifier> listOfEscortingAttributeIds, Date startDate, Date endDate);

}