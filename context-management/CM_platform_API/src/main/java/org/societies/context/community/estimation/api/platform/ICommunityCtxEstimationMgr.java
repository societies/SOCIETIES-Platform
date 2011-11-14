

/**
 * @author yboul
 * @version 1.0
 * @created 12-Nov-2011 7:15:14 PM
 */
public interface ICommunityCtxEstimationMgr {

	/**
	 * 
	 * @param estimationModel
	 * @param list
	 */
	public void estimateContext(EstimationModels estimationModel, List<ContextAttribute> list);

	/**
	 * 
	 * @param Current
	 * @param communityID
	 * @param list
	 */
	public void retrieveCurrentCisContext(boolean Current, CisId communityID, List<ContextAttribute> list);

	/**
	 * 
	 * @param Current
	 * @param communityID
	 * @param list
	 */
	public void retrieveHistoryCisContext(boolean Current, CisId communityID, List<ContextAttribute> list);

	/**
	 * 
	 * @param estimatedContext
	 */
	public void updateContextModelObject(ContextEntity estimatedContext);

}