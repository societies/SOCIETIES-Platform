package org.societies.slm.qosmonitor.api;

/**
 * Interface for invoking the Quality of Service (QoS) Reporter.
 * To be used by QoS Monitor from same CSS.
 * 
 * @author Mitja Vardjan
 *
 */
public interface IQoSReporterInternal {
	
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
}
