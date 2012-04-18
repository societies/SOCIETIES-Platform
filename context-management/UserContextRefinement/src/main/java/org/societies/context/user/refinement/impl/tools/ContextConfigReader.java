package org.societies.context.user.refinement.impl.tools;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContextConfigReader {
	
	private static final Logger log = LoggerFactory.getLogger(ContextConfigReader.class);
	public static Properties properties = null;

    private static final String PROPERTY_FILE_NAME = "frequencies.properties";
	private static final String PATH_FILESYSTEM = "./";
	private static final String PATH_OSGI = "/";
	
	public static final String USER;
	
	public static final int CALENDAR_UPDATE_FREQUENCY;
	public static final int DISTANCETOMEETING_UPDATE_FREQUENCY;
	
	public static final int JARVIS_UPDATE_FREQUENCY;
	public static final int TIME_UPDATE_FREQUENCY;
	public static final int NETWORKUSAGE_UPDATE_FREQUENCY;
	public static final int COMPUTERACTIVITY_UPDATE_FREQUENCY;
	public static final int ACTIVEAPPLICATIONS_UPDATE_FREQUENCY;
	
	public static final double TIME_DISTANCE_MEAN;
	public static final double TIME_DISTANCE_VARIANCE;
	
	public static final int[][] TIME_OF_DAY_BOUNDARIES = new int[5][2];

	public static final String OUTLOOK_FOLDER;
	static{
		log.info("Config Reader Loaded;");
		if (properties == null){
			properties = new Properties();
			InputStream stream;
			try {
				stream = ContextConfigReader.class.getResourceAsStream(
						PATH_OSGI + PROPERTY_FILE_NAME);
				log.debug("ConfigReader: properties file = " + stream);
				if (stream == null)
					stream = new FileInputStream(PATH_FILESYSTEM + PROPERTY_FILE_NAME);
				if (stream != null){
					properties.load(stream);
					stream.close();
				}
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		USER = properties.getProperty("owner", System.getProperty("user.name"));
		
		JARVIS_UPDATE_FREQUENCY = Integer.parseInt(properties.getProperty("frequency.jarvis"));
		TIME_UPDATE_FREQUENCY = Integer.parseInt(properties.getProperty("frequency.timeSource"));
		NETWORKUSAGE_UPDATE_FREQUENCY = Integer.parseInt(properties.getProperty("frequency.networkusage"));
		COMPUTERACTIVITY_UPDATE_FREQUENCY = Integer.parseInt(properties.getProperty("frequency.computeractivity"));
		ACTIVEAPPLICATIONS_UPDATE_FREQUENCY = Integer.parseInt(properties.getProperty("frequency.computeractivity"));

		CALENDAR_UPDATE_FREQUENCY = Integer.parseInt(properties.getProperty("frequency.updateAgenda"));
		DISTANCETOMEETING_UPDATE_FREQUENCY = Integer.parseInt(properties.getProperty("frequency.updateDistance"));

		TIME_DISTANCE_MEAN = Double.parseDouble(properties.getProperty("timeDistance.mean"));
		TIME_DISTANCE_VARIANCE = Double.parseDouble(properties.getProperty("timeDistance.variance")); 

		OUTLOOK_FOLDER = properties.getProperty("outlook.folder");

		TIME_OF_DAY_BOUNDARIES[0][0] = Integer.parseInt(properties.getProperty("timeSource.morning.start"));
		TIME_OF_DAY_BOUNDARIES[0][1] = Integer.parseInt(properties.getProperty("timeSource.morning.end"));
		TIME_OF_DAY_BOUNDARIES[1][0] = Integer.parseInt(properties.getProperty("timeSource.noon.start"));
		TIME_OF_DAY_BOUNDARIES[1][1] = Integer.parseInt(properties.getProperty("timeSource.noon.end"));
		TIME_OF_DAY_BOUNDARIES[2][0] = Integer.parseInt(properties.getProperty("timeSource.afternoon.start"));
		TIME_OF_DAY_BOUNDARIES[2][1] = Integer.parseInt(properties.getProperty("timeSource.afternoon.end"));
		TIME_OF_DAY_BOUNDARIES[3][0] = Integer.parseInt(properties.getProperty("timeSource.evening.start"));
		TIME_OF_DAY_BOUNDARIES[3][1] = Integer.parseInt(properties.getProperty("timeSource.evening.end"));
		TIME_OF_DAY_BOUNDARIES[4][0] = Integer.parseInt(properties.getProperty("timeSource.night.start"));
		TIME_OF_DAY_BOUNDARIES[4][1] = Integer.parseInt(properties.getProperty("timeSource.night.end"));
	}
	
}
