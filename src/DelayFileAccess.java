/**
 * Loads the excel sheet DelayTimes.xls with the bin delay time data.
 * 
 * @author Alexander Rorick
 * Revision Date: April 2013
 * Version 1.0
 */
import java.io.File;
import java.io.FileInputStream;
import java.util.Locale;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;


public class DelayFileAccess {
	
	
	private static double returnDelayTime;  //value of the delay of the bin from the excel file
	
	
	
	//used to output the average times to an Excel file
	private static String delayTimesFile = "AI/Excel Files/DelayTimes.xls";

	//declare the file input stream to read the excel file, then put that file into a workbook
	private static FileInputStream delayTimesExcel = loadFileDelayTimes();
	private static Workbook delayTimesWorkbook = fetchWorkbookDelayTimes(delayTimesExcel);
	
	//used to output the average times to an Excel file
	private static String originalTimesFile = "AI/Excel Files/OriginalTimes.xls";

	//declare the file input stream to read the excel file, then put that file into a workbook
	private static FileInputStream originalTimesExcel = loadFileOriginalTimes();
	private static Workbook originalTimesWorkbook = fetchWorkbookOriginalTimes(originalTimesExcel);
	
	/**
	 * Loads the excel document at the specified path and returns it.
	 * @return delayTimesExcel
	 */
	private static FileInputStream loadFileDelayTimes(){
		try {
			FileInputStream delayTimesExcel = new FileInputStream(new File(
				"AI\\Excel Files\\DelayTimes.xls"));
			return delayTimesExcel;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Takes an excel workbook file and returns it in an excel Workbook form.
	 * @param trashBook
	 * @return delayTimesWorkbook
	 */
	private static Workbook fetchWorkbookDelayTimes(FileInputStream trashBook) {
		try {
			Workbook delayTimesWorkbook = Workbook.getWorkbook(trashBook);
			return delayTimesWorkbook;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Loads the excel document at the specified path and returns it.
	 * @return delayTimesExcel
	 */
	private static FileInputStream loadFileOriginalTimes(){
		try {
			FileInputStream delayTimesExcel = new FileInputStream(new File(
				"AI/Excel Files/OriginalTimes.xls"));
			return delayTimesExcel;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Takes an excel workbook file and returns it in an excel Workbook form.
	 * @param trashBook
	 * @return delayTimesWorkbook
	 */
	private static Workbook fetchWorkbookOriginalTimes(FileInputStream trashBook) {
		try {
			Workbook delayTimesWorkbook = Workbook.getWorkbook(trashBook);
			return delayTimesWorkbook;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Takes in a String value that will match the label of a row in the DelayTimes file
	 * and returns the delay time as a type double
	 * @param pLabel
	 * @return returnDelayTime
	 */
	public static double getDelayTime(String pLabel){
		Sheet sheet = delayTimesWorkbook.getSheet(0);

		//contains all the delay times in the file, column #2
		Cell colData[] = null;
		colData = sheet.getColumn(1);
		
		//contains the row number that the Bin delay time is on
		int rowIndex;
		rowIndex = sheet.findCell(pLabel).getRow();
		
		//contains the delay time from the excel sheet parsed to a double instead of string
		returnDelayTime = Double.parseDouble(colData[rowIndex].getContents());
		return returnDelayTime;
	}
	
	/**
	 * takes in a string for the label of the row and a double that is the delay time
	 * then writes them to the DelayTimes file
	 * In order to keep the old data though it must be rewritten into the file
	 *    so colDataOne and colDataTwo store the old data for rewriting into the file
	 * @param pLabel
	 * @param pDelay
	 */
	public static void setDelayTime(String pLabel, double pDelay){
		try {
			boolean pLabelMatches = false;
			int rowIndex = 0;
			File file = new File(delayTimesFile);
		    WorkbookSettings wbSettings = new WorkbookSettings();

		    //gets the old data from the file before overwriting it
		    Sheet oldWorkbook = delayTimesWorkbook.getSheet(0);
		    
		    Cell colDataOne[] = null;
		    colDataOne = oldWorkbook.getColumn(0);
		    Cell colDataTwo[] = null;
		    colDataTwo = oldWorkbook.getColumn(1);
		    
		    //run through the labels to see if any match pLabel
		    //  if so then set pLabelMatches to true and save the index
		    for (int i=0; i<colDataOne.length; i++){
		    	if (colDataOne[i].getContents().equals(pLabel)){
		    		pLabelMatches = true;
		    		rowIndex = i;
		    	}
		    }
		    
		    wbSettings.setLocale(new Locale("en", "EN"));

		    WritableWorkbook workbook = Workbook.createWorkbook(file, wbSettings);
		    workbook.createSheet("Report", 0);
		    
		    WritableSheet excelSheet = workbook.getSheet(0);
		    
		    //add the content into the excel sheet
		    Label label;

		    //adds the labels to the top of the file
		    label = new Label(0, 0, "Delay Name");
		    excelSheet.addCell(label);
		    label = new Label(1, 0, "Delay Time");
		    excelSheet.addCell(label);
		    
		    //refill the old data
		    for (int i=1; i < colDataOne.length; i++){
		    	label = new Label(0, i, colDataOne[i].getContents());
			    excelSheet.addCell(label);
		    }
		    for (int i=1; i < colDataTwo.length; i++){
			    label = new Label(1, i, colDataTwo[i].getContents());
			    excelSheet.addCell(label);
		    }
		    
		    //if there is already a label in the file replace it
		    if (pLabelMatches){
		    	//updates the new value to the spot where it belongs
			    label = new Label(0, rowIndex, pLabel);
			    excelSheet.addCell(label);
			    label = new Label(1, rowIndex, ""+pDelay);
			    excelSheet.addCell(label);
		    }
		    //if not appened it to the bottom of the file
		    else {
			    //adds in the new value to the bottom of the file
			    label = new Label(0, colDataOne.length, pLabel);
			    excelSheet.addCell(label);
			    label = new Label(1, colDataOne.length, ""+pDelay);
			    excelSheet.addCell(label);
			    
		    }
		    
		    workbook.write();
		    workbook.close();
		    
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Takes in a String value that will match the label of a row in the DelayTimes file
	 * and returns the delay time as a type double
	 * @param pLabel
	 * @return returnDelayTime
	 */
	public static double getOriginalTime(String pLabel){
		Sheet sheet = delayTimesWorkbook.getSheet(0);

		//contains all the delay times in the file, column #2
		Cell colData[] = null;
		colData = sheet.getColumn(1);
		
		//contains the row number that the Bin delay time is on
		int rowIndex;
		rowIndex = sheet.findCell(pLabel).getRow();
		
		//contains the delay time from the excel sheet parsed to a double instead of string
		returnDelayTime = Double.parseDouble(colData[rowIndex].getContents());
		return returnDelayTime;
	}
	
	/**
	 * takes in a string for the label of the row and a double that is the delay time
	 * then writes them to the DelayTimes file
	 * In order to keep the old data though it must be rewritten into the file
	 *    so colDataOne and colDataTwo store the old data for rewriting into the file
	 * @param pLabel
	 * @param pDelay
	 */
	public static void setOriginalTime(String pLabel, double pDelay){
		try {
			boolean pLabelMatches = false;
			int rowIndex = 0;
			File file = new File(originalTimesFile);
		    WorkbookSettings wbSettings = new WorkbookSettings();

		    //gets the old data from the file before overwriting it
		    Sheet oldWorkbook = originalTimesWorkbook.getSheet(0);
		    
		    Cell colDataOne[] = null;
		    colDataOne = oldWorkbook.getColumn(0);
		    Cell colDataTwo[] = null;
		    colDataTwo = oldWorkbook.getColumn(1);
		    
		    //run through the labels to see if any match pLabel
		    //  if so then set pLabelMatches to true and save the index
		    for (int i=0; i<colDataOne.length; i++){
		    	if (colDataOne[i].getContents().equals(pLabel)){
		    		pLabelMatches = true;
		    		rowIndex = i;
		    	}
		    }
		    
		    wbSettings.setLocale(new Locale("en", "EN"));

		    WritableWorkbook workbook = Workbook.createWorkbook(file, wbSettings);
		    workbook.createSheet("Report", 0);
		    
		    WritableSheet excelSheet = workbook.getSheet(0);
		    
		    //add the content into the excel sheet
		    Label label;

		    //adds the labels to the top of the file
		    label = new Label(0, 0, "Delay Name");
		    excelSheet.addCell(label);
		    label = new Label(1, 0, "Delay Time");
		    excelSheet.addCell(label);
		    
		    //refill the old data
		    for (int i=1; i < colDataOne.length; i++){
		    	label = new Label(0, i, colDataOne[i].getContents());
			    excelSheet.addCell(label);
		    }
		    for (int i=1; i < colDataTwo.length; i++){
			    label = new Label(1, i, colDataTwo[i].getContents());
			    excelSheet.addCell(label);
		    }
		    
		    //if there is already a label in the file replace it
		    if (pLabelMatches){
		    	//updates the new value to the spot where it belongs
			    label = new Label(0, rowIndex, pLabel);
			    excelSheet.addCell(label);
			    label = new Label(1, rowIndex, ""+pDelay);
			    excelSheet.addCell(label);
		    }
		    //if not appened it to the bottom of the file
		    else {
			    //adds in the new value to the bottom of the file
			    label = new Label(0, colDataOne.length, pLabel);
			    excelSheet.addCell(label);
			    label = new Label(1, colDataOne.length, ""+pDelay);
			    excelSheet.addCell(label);
			    
		    }
		    
		    workbook.write();
		    workbook.close();
		    
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
