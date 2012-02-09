package org.societies.api.internal.css.management;

/**
 * Interface defining a CSS device
 */
public interface ICSSDevice {

	/**
	 * Enum for device status types
	 */
	enum deviceStatus {Available, Unavailable, Hibernating};
	/**
	 * Enum for device node types
	 */
	enum nodeType {Android, Cloud, Rich};

	/**
	 * unique within context of CSS
	 */
	String identity = null;
	
	/**
	 * status of device
	 */
	String status = null;
	/**
	 * node type of device
	 */
	String nodeType = null;
}
