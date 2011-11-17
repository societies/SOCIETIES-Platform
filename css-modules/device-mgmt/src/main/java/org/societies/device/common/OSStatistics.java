package societies.device.common;

/**
 * 
 *	Some operating system specific statistics 
 *
 */
public interface OSStatistics {
	float getPercentageMemoryUsage();
	float getPercentageCpuUsage();
	float getBatteryPercentage();
}
