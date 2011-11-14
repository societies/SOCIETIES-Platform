

/**
 * @author yboul
 * @version 1.0
 * @created 12-Nov-2011 7:15:15 PM
 */
public interface ICommunityCtxPredictionMgr {

	/**
	 * 
	 * @param cisID
	 */
	public void getCommunity(CisId cisID);

	/**
	 * 
	 * @param predictionModel
	 * @param ctxObjModel
	 * @param date
	 */
	public predicted context predictContext(PredictionModel predictionModel, ContextAttributeIdentifier ctxObjModel, Date date);

}