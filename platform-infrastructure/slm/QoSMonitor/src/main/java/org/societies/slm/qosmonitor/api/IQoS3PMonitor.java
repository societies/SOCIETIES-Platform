package org.societies.slm.qosmonitor.api;

/**
 * Interface for invoking the third party Quality of Service (QoS) Monitor.
 * To be used by QoS Reporter.
 * 
 * @author Mitja Vardjan
 *
 */
public interface IQoS3PMonitor {
	
	/**
	 * Evaluate QoS at the service backend side, i.e. at service provider side.
	 * 
	 * @param sla XML-formatted Service License Agreement
	 * 
	 * @param violations References to the QoS parameters to be investigated.
	 * Given as array of XPath expressions that point to locations in SLA where
	 * the QoS parameters are defined.
	 * 
	 * @param data Anonymized community data from service consumer. The purpose
	 * of this data is to enable realistic evaluation of QoS by the 3rd party
	 * monitor. It should include the data needed to experience the specified
	 * QoS when using the service backend. Due to privacy concerns, any other
	 * and unnecessary data should not be included. 
	 */
	public void evaluateQoS(String sla, String[] violations, Serializable data);

	/**
	 * Async return for
	 * {@link IQoSReporter#getCommunityData(String, IQoS3PMonitor)}
	 * 
	 * @param dataId Data ID
	 * 
	 * @param data The returned data. The data are anonymized.
	 */
	public void returnCommunityData(String dataId, Serializable data);
}
