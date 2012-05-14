package org.societies.context.userPrediction.impl;

import java.io.File;
import java.io.IOException;

import java.util.HashMap;
import java.util.Vector;
import jxl.Cell;
import jxl.CellType;
import jxl.CellView;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.UnderlineStyle;
import jxl.read.biff.BiffException;
import jxl.write.Formula;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

/**
 *
 * @author jpapai
 */
public class ReadExcel {
    
    private String inputFile;
    private HashMap<String, Vector<String>> mapOfContextData = new HashMap<String, Vector<String>>();
    
    public void setInputFile(String inputFile) {
        this.inputFile = inputFile;
    }
    
    public HashMap<String, Vector<String>> read() throws IOException, BiffException {

        File inputWorkbook = new File(inputFile);
        Workbook w;
        try {
            w = Workbook.getWorkbook(inputWorkbook);
            // Get the first sheet
            Sheet sheet = w.getSheet(0);
            String contextAttribute = "";
            // Loop over column and lines
            // get the labels of the columns
            for (int j = 0; j < sheet.getColumns(); j++) {
                Vector<String> data = new Vector<String>();
                contextAttribute="";
                Cell cell = sheet.getCell(j, 0); //the first row only == the label
                if (cell.getType() == CellType.LABEL) {
                    contextAttribute = cell.getContents();
                }
                
                //for each column store the data
                for (int i = 1; i < sheet.getRows(); i++) { //from the second row and on
                    cell = sheet.getCell(j, i);
                    if (cell.getType() == CellType.LABEL) {
                       data.add(cell.getContents()); 
                    }

                }
                mapOfContextData.put(contextAttribute,data);
                
            }
            
            
        } catch (BiffException e) {
            e.printStackTrace();
        }
        
        return mapOfContextData;
    }

	
}
