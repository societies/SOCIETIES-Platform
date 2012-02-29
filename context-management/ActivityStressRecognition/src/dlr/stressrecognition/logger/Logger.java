package dlr.stressrecognition.logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import dlr.stressrecognition.MainActivity;

/**
 * Logging class for saving data to csv-files.
 * 
 * @author Michael Gross
 *
 */
public class Logger {
	private File file;
	private String fileName;
	private static String logPath = "/sdcard/stress/";
	private BufferedWriter fw;
	private long startup;
	
	public Logger (String fileName) {
		startup = MainActivity.STARTUP;
		this.fileName = fileName+"-"+System.currentTimeMillis()+".csv";
        SimpleDateFormat timingFormat = new SimpleDateFormat("dd-MM-yyyy");
		File logDir = new File(logPath + timingFormat.format(new Date())+"/"+MainActivity.NAME+"/");
		logDir.mkdirs();
		
		try {
			file = new File(logDir, this.fileName);
	  		if(!file.exists())
	  			file.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void writeHeader(String[] columns) {
		String line = "Timestamp,";
		for(int i = 0; i < columns.length; i++) {
			if(i < columns.length-1) {
				line += columns[i] + ",";
			} else {
				line += columns[i];
			}
		}
		line += "\n";
		writeToFile(line);
	}
	
	public void write(String value) {
		long currTime = System.nanoTime() - startup;
		String line = "" + currTime*0.000000001 + "," + value;
		line += "\n";
		writeToFile(line);
	}
	
	public void write(String[] values) {
		long currTime = System.nanoTime() - startup;
		String line = "" + currTime*0.000000001 + ",";
		for(int i=0; i < values.length; i++) {
			if(i < values.length-1) {
				line += values[i] + ",";
			} else {
				line += values[i];
			}
		}
		line += "\n";
		writeToFile(line);
	}
	
	private void writeToFile(String line) {
		try {
			fw = new BufferedWriter(new FileWriter(file, true));
			fw.write(line);
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
