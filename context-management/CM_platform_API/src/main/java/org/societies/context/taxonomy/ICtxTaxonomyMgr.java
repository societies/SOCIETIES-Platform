import WP5.Context.Informational.ContextAttribute;
import WP5.Context.Informational.CtxAttributeSemanticDescription;

/**
 * @author TI
 * @version 1.0
 * @created 12-Nov-2011 7:15:15 PM
 */
public interface ICtxTaxonomyMgr {

	/**
	 * it returns the children of the element in the taxonomy tree
	 * 
	 * @param attrubute
	 */
	public ContextAttribute[] getChildren(ContextAttribute attrubute);

	/**
	 * 
	 * @param attributeB
	 * @param attributeA
	 */
	public int getDistance(ContextAttribute attributeB, ContextAttribute attributeA);

	/**
	 * it returns the parent of the element in the taxonomy tree
	 * 
	 * @param attribute
	 */
	public ContextAttribute getParent(ContextAttribute attribute);

	/**
	 * 
	 * @param attribute
	 */
	public CtxAttributeSemanticDescription getSemanticDescription()(ContextAttribute attribute);

	/**
	 * It return an array of context attributes which are on the same level on the
	 * specific tree leaf
	 * 
	 * @param attribute
	 */
	public ContextAttribute[] getSiblings()(ContextAttribute attribute);

	/**
	 * 
	 * @param attrubute
	 */
	public boolean isContextAttributeAvailable(ContextAttribute attrubute);

}