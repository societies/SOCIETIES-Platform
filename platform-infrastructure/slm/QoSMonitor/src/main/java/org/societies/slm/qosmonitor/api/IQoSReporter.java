package org.societies.slm.qosmonitor.api;

/**
 * Interface for invoking the Quality of Service (QoS) Reporter.
 * To be used by QoS Monitor.
 * 
 * @author Mitja Vardjan
 *
 */
public interface IQoSReporter {
	
	/**
	 * Notifies the 3rd party monitor that QoS is below promised level and
	 * requests evaluation of QoS by the 3rd party monitor.
	 * The 3rd party monitor is specified in Service License Agreement (SLA).
	 * 
	 * @param sla XML-formatted Service License Agreement
	 * 
	 * @param violations References to the QoS parameters to be investigated.
	 * Given as array of XPath expressions that point to locations in SLA where
	 * the QoS parameters are defined.
	 */
	public void notifyLowQoS(String sla, String[] violations);
	
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
