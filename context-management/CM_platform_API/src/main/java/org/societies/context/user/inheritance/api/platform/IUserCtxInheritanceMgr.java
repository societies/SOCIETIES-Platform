

/**
 * @author yboul
 * @version 1.0
 * @created 12-Nov-2011 7:15:15 PM
 */
public interface IUserCtxInheritanceMgr {

	/**
	 * 
	 * @param cisId
	 */
	public void getCIS(CisId cisId);

	/**
	 * 
	 * @param contextAttributeIdentifier
	 * @param type
	 * @param cisId
	 */
	public void getContextAttribute(ContextAttributeIdentifier contextAttributeIdentifier, ContextAttributeValueType type, CisId cisId);

	/**
	 * 
	 * @param contextAttributeIdentifier
	 * @param type
	 */
	public void inheritContextAttribute(ContextAttributeIdentifier contextAttributeIdentifier, ContextAttributeValueType type);

	/**
	 * 
	 * @param conflictResolutionsAlgorithms
	 */
	public void resolveConflicts(ConflictResolutionAlgorithm conflictResolutionsAlgorithms);

}