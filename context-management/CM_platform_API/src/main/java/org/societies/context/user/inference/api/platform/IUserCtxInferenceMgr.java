import WP5.Context.Informational.ContextModelObject;

/**
 * @author nikosk
 * @version 1.0
 * @created 12-Nov-2011 7:15:15 PM
 */
public interface IUserCtxInferenceMgr {

	/**
	 * 
	 * @param object
	 */
	public void checkQuality(ContextModelObject object);

	/**
	 * 
	 * @param ctxID
	 * @param ctxID2
	 */
	public Double evaluateSimilarity(ContextAttributeIdentifier ctxID, ContextAttributeIdentifier ctxID2);

	/**
	 * 
	 * @param listCtxID
	 * @param listCtxID2
	 */
	public Map (<ContextAttributeIdentifier>,<double>) evaluateSimilarity(List<ContextAttributeIdentifier> listCtxID, List<ContextAttributeIdentifier> listCtxID2);

	/**
	 * 
	 * @param predictionMethod
	 */
	public PredictionMethod getDefaultPredictionMethod(PredictionMethod predictionMethod);

	/**
	 * 
	 * @param predictionMethodl
	 */
	public void getPredictionMethod(PredictionMethod predictionMethodl);

	/**
	 * 
	 * @param ctxAttrId
	 * @param type
	 * @param cisid
	 */
	public void inheritContext(ContextAttributeIdentifier ctxAttrId, ContextAttributeValueType type, CISid cisid);

	/**
	 * 
	 * @param ctxAttrID
	 * @param predictionMethod
	 * @param date
	 */
	public ContextAttribute predictContext(ContextAttributeIdentifier ctxAttrID, PredictionMethod predictionMethod, Date date);

	/**
	 * 
	 * @param ctxAttrID
	 * @param predictionMethodl
	 * @param int
	 */
	public ContextAttribute predictContext(ContextAttributeIdentifier ctxAttrID, PredictionMethod predictionMethodl, index int);

	/**
	 * 
	 * @param ctxAttrID
	 * @param date
	 */
	public ContextAttribute predictContext(ContextAttributeIdentifier ctxAttrID, Date date);

	/**
	 * 
	 * @param ctxAttrID
	 * @param index
	 */
	public ContextAttribute predictContext(ContextAttributeIdentifier ctxAttrID, int index);

	/**
	 * 
	 * @param ctxAttrId
	 */
	public void refineContext(ContextAttributeIdentifier ctxAttrId);

	/**
	 * 
	 * @param predictionMethod
	 */
	public void removePredictionMethod(PredictionMethod predictionMethod);

	/**
	 * 
	 * @param predMethod
	 */
	public setDefaultPredictionMethod(PredictionMethod predMethod);

	/**
	 * 
	 * @param predMethod
	 */
	public setPredictionMethod(PredictionMethod predMethod);

}