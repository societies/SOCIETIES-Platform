package org.societies.slm.qosmonitor.api;

/**
 * Interface for invoking the Quality of Service (QoS) Reporter.
 * To be used by 3rd party QoS Monitor.
 * 
 * @author Mitja Vardjan
 *
 */
public interface IQoSReporter {
	
	/**
	 * Get additional community data required by 3rd party monitor in order to
	 * use the service properly and evaluate QoS. This method has to be invoked
	 * only when the data sent with original evaluation request are insufficient.
	 * The data are anonymized before returning.
	 * 
	 * @param dataId Data ID. TODO: How will data be retrieved?
	 * 
	 * @param callback The callback for async return
	 */
	public void getCommunityData(String dataId, IQoS3PMonitor callback);
	
	/**
	 * Notify about completed evaluation of QoS.
	 * 
	 * @param report Evaluation report digitally signed by the 3rd party QoS
	 * monitor.
	 */
	public void notifyEvaluationResult(String report);
}
