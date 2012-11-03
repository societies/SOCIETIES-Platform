package org.societies.context.evaluation.broker;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.model.CtxHistoryAttribute;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

public class XlsReader {


	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(CtxBrokerEvaluation.class);

	private static final String XLS_INPUT_FILE = "realityMining.xls";

	Workbook w = null;

	XlsReader(){
		
		try {
			this.loadWorkBook();
			
			System.out.println("number of sheets: "+this.w.getNumberOfSheets());
			System.out.println("ids: "+getEntitiesIntegerList());
			System.out.println("friends reader: "+ this.xlsFriendsReader());
			System.out.println("proximity Lab reader: "+ this.xlsProximityLabReader());
			System.out.println("proximity Outlab reader: "+ this.xlsProximityOutLabReader());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {

		XlsReader tester = new XlsReader();
	}


	private void loadWorkBook() throws Exception{

		final InputStream is = this.getClass().getResourceAsStream("/" + XLS_INPUT_FILE);
		
		if (is == null)
			throw new FileNotFoundException(XLS_INPUT_FILE + " (No such file in resources)");

		try{
			this.w = Workbook.getWorkbook(is);

		} finally {
			if (is != null)
				is.close();
		}	
	}
	
	/*
	 * create a map with indi enities and respective friends
	 */
	public HashMap<Integer, List<Integer>> xlsFriendsReader()  {

		final HashMap<Integer, List<Integer>> mapFriendsData = new HashMap<Integer, List<Integer>>();

			Sheet sheet = this.w.getSheet(0);
			Integer key = 0;
			// Loop over column and lines
			// get the labels of the columns

			for (int j = 0; j < sheet.getColumns(); j++) {
				List<Integer> data = new ArrayList<Integer>();
				key=j;

				//for each column store the data
				for (int i = 0; i < sheet.getRows(); i++) { 
					Cell cell = sheet.getCell(j, i);
					//System.out.println("key:"+j);
					//System.out.println("cell column:"+cell.getColumn());
					//System.out.println("cell getContents:"+cell.getContents());
					if (cell.getContents().equals("1") ) {
						data.add(i+1); 
					}
				}
				mapFriendsData.put(key+1,data);
			}

			if (LOG.isInfoEnabled()) {
				LOG.info("sheet.getColumns():"+sheet.getColumns());
				LOG.info("sheet.getRows():"+sheet.getRows());
			}
			
		return mapFriendsData;
	}

	/*
	 * user '1' has proximity with {'2#5,'3#1','4#2'} userId#ProximityLevel
	 */
	public HashMap<Integer, List<String>> xlsProximityLabReader()  {

		// user '1' has proximity with {'2#5,'3#1','4#2'} userId#ProximityLevel
		final HashMap<Integer, List<String>> mapOfProximityData = new HashMap<Integer, List<String>>();

		// 1 is outlab
		Sheet sheet = this.w.getSheet(1);
		Integer key = 0;
		for (int j = 0; j < sheet.getColumns(); j++) {

			List<String> data = new ArrayList<String>();
			key=j;
			String proximityValue;
			for (int i = 0; i < sheet.getRows(); i++) { 
				Cell cell = sheet.getCell(j, i);
				String proxValue = cell.getContents();
				
				if(!proxValue.equals("NaN")){
					proximityValue = i+1+"#"+cell.getContents();
					//System.out.println("prox value for user : j"+j+" proxValue:"+proximityValue);
					data.add(proximityValue);	
				}
				
			}
			mapOfProximityData.put(j+1,data);
		}
		return mapOfProximityData;
	}


	public HashMap<Integer, List<String>> xlsProximityOutLabReader()  {

		// user '1' has proximity with {'2#5,'3#1','4#2'} userId#ProximityLevel
		final HashMap<Integer, List<String>> mapOfProximityData = new HashMap<Integer, List<String>>();

		// 1 is outlab
		Sheet sheet = this.w.getSheet(1);
		Integer key = 0;
		for (int j = 0; j < sheet.getColumns(); j++) {

			List<String> data = new ArrayList<String>();
			key=j;
			String proximityValue;
			for (int i = 0; i < sheet.getRows(); i++) { 
				Cell cell = sheet.getCell(j, i);
				String proxValue = cell.getContents();
				
				if(!proxValue.equals("NaN")){
					proximityValue = i+1+"#"+cell.getContents();
					//System.out.println("prox value for user : j"+j+" proxValue:"+proximityValue);
					data.add(proximityValue);	
				}
				
			}
			mapOfProximityData.put(j+1,data);
		}
		return mapOfProximityData;
	}


	
	
	public List<Integer> getEntitiesIntegerList(){
		
		List<Integer> results =  new ArrayList<Integer>();
		Sheet sheet = this.w.getSheet(1);
		
		for (int j = 0 ; j < sheet.getColumns(); j++) {
			results.add(j+1);
		}
		return results;
	}

}