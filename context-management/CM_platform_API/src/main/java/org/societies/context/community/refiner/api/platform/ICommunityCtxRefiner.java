import WP5.Context.Informational.ContextAttributeIdentifier;

/**
 * @author fran_ko
 * @version 1.0
 * @created 12-Nov-2011 7:15:15 PM
 */
public interface ICommunityCtxRefiner {

	/**
	 * 
	 * @param attrId
	 */
	public void[] refineContext(ContextAttributeIdentifier attrId);

}