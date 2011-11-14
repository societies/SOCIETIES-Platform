

/**
 * This interface represents a callback interface for the ICommunityCtxBroker
 * interface.
 * @author mcrotty
 * @version 1.0
 * @created 12-Nov-2011 7:15:14 PM
 */
public interface ICommunityCtxBrokerCallback {

	/**
	 * 
	 * @param admCssRetr
	 */
	public void adminCSSRetrieved(ContextEntity admCssRetr);

	/**
	 * 
	 * @param ctxAttribute
	 */
	public void bondsRetrieved(ContextAttribute ctxAttribute);

	/**
	 * 
	 * @param childComms
	 */
	public void childCommsRetrieved(List<ContextEntityIdentifier> childComms);

	/**
	 * 
	 * @param commMembs
	 */
	public void commMembersRetrieved(List <ContextEntityIdentifier> commMembs);

	/**
	 * 
	 * @param parentComms
	 */
	public void parentCommsRetrieved(List<ContextEntityIdentifier> parentComms);

}