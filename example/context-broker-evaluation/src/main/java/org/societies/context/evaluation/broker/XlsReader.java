package org.societies.context.evaluation.broker;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.societies.api.context.model.CtxHistoryAttribute;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

public class XlsReader {

	private static final String XLS_INPUT_FILE = "realityMining.xls";
	
	XlsReader(){
		
	}
	
	
	void readXls(String file){
		System.out.println("started");
		
	}
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
	
		XlsReaderTester tester = new XlsReaderTester();

		tester.readXls(XLS_INPUT_FILE);
	}




	public HashMap<Integer, List<Integer>> xlsFriendsReader(String inputFile) throws Exception {

		final HashMap<Integer, List<Integer>> mapFriendsData = new HashMap<Integer, List<Integer>>();

		final InputStream is = this.getClass().getResourceAsStream("/" + inputFile);
		if (is == null)
			throw new FileNotFoundException(inputFile + " (No such file in resources)");

		try {
			this.w = Workbook.getWorkbook(is);
			// Get the first sheet
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
				mapFriendsData.put(key,data);

			}
		
			if (LOG.isInfoEnabled()) {
				LOG.info("sheet.getColumns():"+sheet.getColumns());
				LOG.info("sheet.getRows():"+sheet.getRows());
			}
	
		} finally {
			if (is != null)
				is.close();
		}

		CtxHistoryAttribute hoc = null;
hoc.
		return mapFriendsData;
	}

















}

	
