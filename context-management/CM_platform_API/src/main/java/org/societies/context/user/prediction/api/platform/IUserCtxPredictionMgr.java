

/**
 * @author nikosk
 * @version 1.0
 * @created 12-Nov-2011 7:15:15 PM
 */
public interface IUserCtxPredictionMgr {

	/**
	 * 
	 * @param predMethod
	 */
	public PredictionMethod getDefaultPredictionMethod(PredictionMethod predMethod);

	/**
	 * 
	 * @param predMethod
	 */
	public PredictionMethod getPredictionMethod(PredictionMethod predMethod);

	/**
	 * 
	 * @param predictionModel
	 * @param ctxAttrID
	 * @param date
	 */
	public ContextAttribute predictContext(PredictionModels predictionModel, ContextAttributeIdentifier ctxAttrID, Date date);

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
	 * @param predictionModel
	 * @param ctxAttrID
	 * @param index
	 */
	public ContextAttribute predictContext(PredictionModels predictionModel, ContextAttributeIdentifier ctxAttrID, int index);

	/**
	 * 
	 * @param predMethod
	 */
	public void removePredictionMethod(PredictionMethod predMethod);

	/**
	 * 
	 * @param predMethod
	 */
	public void setDefaultPredictionMethod(PredictionMethod predMethod);

	/**
	 * 
	 * @param predMethod
	 */
	public void setPredictionMethod(PredictionMethod predMethod);

}