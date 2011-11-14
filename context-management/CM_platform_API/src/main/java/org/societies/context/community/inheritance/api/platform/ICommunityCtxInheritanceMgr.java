

/**
 * @author yboul
 * @version 1.0
 * @created 12-Nov-2011 7:15:15 PM
 */
public interface ICommunityCtxInheritanceMgr {

	/**
	 * 
	 * @param cidIdentifier
	 */
	public void getParentCis(CisIdentifier cidIdentifier);

	/**
	 * 
	 * @param ctxAttributeIdentifier
	 * @param type
	 * @param cisId
	 */
	public void inheritContext(ContextAttributeIdentifier ctxAttributeIdentifier, ContextAttributeValueType type, CISid cisId);

	/**
	 * 
	 * @param ctxAttributeIdentifier
	 * @param type
	 * @param cisId
	 */
	public void retrieveCtx(ContextAttributeIdentifier ctxAttributeIdentifier, ContextAttributeValueType type, CISid cisId);

}