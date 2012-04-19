package org.societies.context.user.refinement.impl.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.impl.BasicGreedyHillClimber;

public class LearningPropertyLoader
{
	private static final String  PropertiesFilePath = "resources";
	private static final String  PropertiesFileName = "restartconfiguration.properties";
	
	private static boolean RestartConfiguration_IncreasingNumberOfModifiedNodes;
	private static boolean RestartConfiguration_RandomHigherNumberOfModifiedNodes;
	private static double RestartConfiguration_RemoveAllParentsProbability;
	private static double RestartConfiguration_DontAddNewArcsWhenNoArcsAreRemovedProbability;
	private static double RestartConfiguration_AddNewArcsAlthoughArcsHaveBeenRemovedProbability;
	private static int RestartConfiguration_MaxNoNodesModifiedByRestart; //range: 1:(#RVs-1)
	private static double RestartConfiguration_PercentageOfGlobalRandomRestarts;
	private static double RestartConfiguration_DurationOfRandomRestartsWithLocalBestInHours;
	private static double RestartConfiguration_DurationOfRandomRestartsWithAbsoluteBestInHours;
	private static double RestartConfiguration_CacheHitRateThreshold;
	private static int RestartConfiguration_maxLocalRestarts;

	private static Logger log4j = LoggerFactory.getLogger(BasicGreedyHillClimber.class);
	
	static{
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(new File(PropertiesFilePath+File.separator+PropertiesFileName)));
			String temp = (String) prop.get("RestartConfiguration_RemoveAllParentsProbability");
			RestartConfiguration_RemoveAllParentsProbability = Double.parseDouble(temp);
			temp = (String) prop.get("RestartConfiguration_DontAddNewArcsWhenNoArcsAreRemovedProbability");
			RestartConfiguration_DontAddNewArcsWhenNoArcsAreRemovedProbability = Double.parseDouble(temp);
			temp = (String) prop.get("RestartConfiguration_AddNewArcsAlthoughArcsHaveBeenRemovedProbability");
			RestartConfiguration_AddNewArcsAlthoughArcsHaveBeenRemovedProbability = Double.parseDouble(temp);
			temp = (String) prop.get("RestartConfiguration_MaxNoNodesModifiedByRestart");
			RestartConfiguration_MaxNoNodesModifiedByRestart = Integer.parseInt(temp);
			temp = (String) prop.get("RestartConfiguration_IncreasingNumberOfModifiedNodes");
			RestartConfiguration_IncreasingNumberOfModifiedNodes = Boolean.parseBoolean(temp);
			temp = (String) prop.get("RestartConfiguration_RandomHigherNumberOfModifiedNodes");
			RestartConfiguration_RandomHigherNumberOfModifiedNodes = Boolean.parseBoolean(temp); 
			temp = (String) prop.get("RestartConfiguration_PercentageOfGlobalRandomRestarts");
			RestartConfiguration_PercentageOfGlobalRandomRestarts = Double.parseDouble(temp);
			temp = (String) prop.get("RestartConfiguration_DurationOfRandomRestartsWithLocalBestInHours");
			RestartConfiguration_DurationOfRandomRestartsWithLocalBestInHours = Double.parseDouble(temp);
			temp = (String) prop.get("RestartConfiguration_DurationOfRandomRestartsWithAbsoluteBestInHours");
			RestartConfiguration_DurationOfRandomRestartsWithAbsoluteBestInHours = Double.parseDouble(temp);
			temp = (String) prop.get("RestartConfiguration_CacheHitRateThreshold");
			RestartConfiguration_CacheHitRateThreshold = Double.parseDouble(temp);
			temp = (String) prop.get("RestartConfiguration_maxLocalRestarts");
			RestartConfiguration_maxLocalRestarts = Integer.parseInt(temp);
			
		} catch (FileNotFoundException e1) {
			log4j.error("FileNotFoundException for properties file. BasicGreedyHillClimber will use default parameters.");
		} catch (IOException e1) {
			log4j.error("IOException when reading properties file. BasicGreedyHillClimber will use default parameters.");
		}
	}

	public static double getRestartConfiguration_RemoveAllParentsProbability() {
		return RestartConfiguration_RemoveAllParentsProbability;
	}

	public static double getRestartConfiguration_DontAddNewArcsWhenNoArcsAreRemovedProbability() {
		return RestartConfiguration_DontAddNewArcsWhenNoArcsAreRemovedProbability;
	}

	public static double getRestartConfiguration_AddNewArcsAlthoughArcsHaveBeenRemovedProbability() {
		return RestartConfiguration_AddNewArcsAlthoughArcsHaveBeenRemovedProbability;
	}

	public static int getRestartConfiguration_MaxNoNodesModifiedByRestart() {
		return RestartConfiguration_MaxNoNodesModifiedByRestart;
	}

	public static boolean getRestartConfiguration_IncreasingNumberOfModifiedNodes() {
		return RestartConfiguration_IncreasingNumberOfModifiedNodes;
	}

	public static boolean getRestartConfiguration_RandomHigherNumberOfModifiedNodes() {
		return RestartConfiguration_RandomHigherNumberOfModifiedNodes;
	}

	public static double getRestartConfiguration_PercentageOfGlobalRandomRestarts() {
		return RestartConfiguration_PercentageOfGlobalRandomRestarts;
	}

	public static double getRestartConfiguration_DurationOfRandomRestartsWithLocalBestInHours() {
		return RestartConfiguration_DurationOfRandomRestartsWithLocalBestInHours;
	}

	public static double getRestartConfiguration_DurationOfRandomRestartsWithAbsoluteBestInHours() {
		return RestartConfiguration_DurationOfRandomRestartsWithAbsoluteBestInHours;
	}

	public static double getRestartConfiguration_CacheHitRateThreshold() {
		return RestartConfiguration_CacheHitRateThreshold;
	}


	public static int getRestartConfiguration_maxLocalRestarts() {
		return RestartConfiguration_maxLocalRestarts;
	}
}