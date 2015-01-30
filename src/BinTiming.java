import java.util.*;
import java.io.*;
import jxl.*;
import jxl.write.*;
import jxl.write.Number;

/**
 * Evaluates the amount of time it takes to unload one bin or one tip-cart
 * It finds these values by taking in input from a time sheet of previous routes
 * sorting the values based on the time the truck arrives at each point
 * then evaluating how long it would have taken to get from point A to point B
 * then finding how long it would have taken to unload each bin at the location
 * then averaging all the data based on all the location given in the data
 * 
 * getAvgBin() and getAvgTipcart() are the functions to call to get the time it takes
 * in minutes to empty one bin of each type
 * the value is returned as a double 
 * @author Alexander Rorick
 * 
 * last modified: 3/26/2013
 * by: Alexander Rorick
 */
public class BinTiming {
	
	//declare the file input stream to read the excel file, then put that file into a workbook
	private static FileInputStream trashBinExcel = loadFile();
	private static Workbook trashBinWorkbook = fetchWorkbook(trashBinExcel);
	
	//stores the count of the rows or columns of the Excel sheet
	private static int column;
	private static int row;
	
	
	private static double binTimeTotal;    //holds the total amount of time to empty bins
	private static double binAvgTotal;    //holds the time it takes to empty one bin
	private static int binCountTotal;    //holds the total number of bins
	
	private static double tipcartTimeTotal;    //holds the total amount of time to empty tipcarts
	private static double tipcartAvgTotal;    //holds the time it takes to empy one tipcart
	private static int tipcartCountTotal;    //holds the total number of tipcarts
	
	private static double returnBinAvgTotal;  //value of the delay of the bin from the excel file
	private static double returnTipcartAvgTotal;  //value of the delay of the tipcart from the excel file
	
	
	//declare the file input stream to read the excel file, then put that file into a workbook
	private static FileInputStream delayTimesExcel = loadFileDelayTimes();
	private static Workbook delayTimesWorkbook = fetchWorkbookDelayTimes(delayTimesExcel);
	
	//used to output the average times to an Excel file
	private static String delayTimesFile = "AI/ExcelFiles/DelayTimes.xls";
	
	//labels used in the DelayTimes Excel file for each delay
	private static String delayTimesBinLabel = "Bin";
	private static String delayTimesTipcartLabel = "Tipcart";
	
	
	/**
	 * Loads the excel document at the specified path and returns it.
	 * @return trashBinExcel
	 */
	private static FileInputStream loadFile(){
		try {
			FileInputStream trashBinExcel = new FileInputStream(new File(
				"AI/Excel Files/TrashRoutes-Dallas-12-19-2012.xls"));
			return trashBinExcel;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Takes an excel workbook file and returns it in an excel Workbook form.
	 * @param trashBook
	 * @return trashBinExcel
	 */
	private static Workbook fetchWorkbook(FileInputStream trashBook) {
		try {
			Workbook trashBinWorkbook = Workbook.getWorkbook(trashBook);
			return trashBinWorkbook;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * function to dig out the times from the Excel spreedsheet
	 * @param trashBook
	 */
	private static void evaluateWorkBook(Workbook trashBook){
		//run for times for each set of days
		//int sheetNum = 0;
		for (int sheetNum = 0; sheetNum < 4; sheetNum++){
		    //gets the sheet that contains the data for each run through
			Sheet sheet = trashBook.getSheet(sheetNum);
			
			column = sheet.getColumns();
		    row = sheet.getRows();
		    //System.out.println("row: " + row);
			
		    //int columnNum = 2;
		    //for loop need column 2 to 4 because that is where the time trials are stored
		    for (int columnNum = 2; columnNum < 5; columnNum++){
		    	//System.out.println("columnNum: " + columnNum);
		    	/*
			     * this for loop runs through each row comparing the first point (A) with
			     * the second point (B)
			     * the max value to stop at is the amount of rows -2 because
			     * the title row does not need counted and the last pair is one from the bottom
			     * because you would not be able to compare to the next point with the last point
			     */
		    	int newTempCount = 0;
				for (int rowNum=0; rowNum < row-2; rowNum++){
					Cell colData[] = null;
					float colNum[];
					
				    Cell rowData[] = null;
				    Cell rowDataB[] = null;
				    
				    /*
				     * sort the column before the checking of the rows but after getting the current sheet
				     * this is so that you know the route that was taken on each specific run
				     * so that all the data can be used to gather an average on the bin time
				    */
				    colData = sheet.getColumn(columnNum);
				    colNum = new float[colData.length-1];
				    //for loop to get all the column data into an array with float values 10:26 becomes 10.26
				    //   this data can then be sorted from low to high
				    for (int i = 1; i < row; i++){
				    	//System.out.print(colData[i].getContents() + ", ");
				    	String temp;
				    	temp = colData[i].getContents();
				    	temp = temp.replace(":", ".");
				    	
				    	colNum [i-1] = Float.parseFloat(temp);
				    	//System.out.print(colNum[i-1] + ", ");
				    }
				    //System.out.println();
				    
				    //Sort the column data
				    colNum = Quicksort.sort(colNum);
				    
				    int p = -1;
				    //gets rid of any bad data such as values of zero
				    //   always adds at least one so start p at -1
				    do {
				    	p++;
				    } while (colNum[p] < 1);
				    
				    if (newTempCount == 0){
				    	String lowLabel = "lowest"+sheetNum+":"+columnNum;
				    	String highLabel = "highest"+sheetNum+":"+columnNum;
				    	
				    	System.out.println(lowLabel);
				    	System.out.println(colNum[p]);
					    //DelayFileAccess.setOriginalTime(lowLabel, colNum[p]);
				    	
				    	System.out.println(highLabel);
					    System.out.println(colNum[colNum.length-1]);
					    //DelayFileAccess.setOriginalTime(highLabel, colNum[colNum.length-1]);
					    newTempCount++;
				    }
				    
				    /*
				    //output the column data to the system
				    for (int i = 1; i < row; i++){

				    	System.out.print(colNum[i-1] + ", ");
				    }
				    System.out.println();
				    */
				    
				    /*
				     * after the column is sorted then proceed to evaluate the data
				     * take the first arrival time and subtract the next arrival time
				     *   so that value will be the time it took to unload the bins at location one
				     *   and then to drive from location one the location two
				     *   store it as totalTime
				     */
				    float totalTime = 0;    //total time from arriving at point A to arriving at point B
				    String timeCell;    //String value of totalTime, it is used to search Excel sheet
				    String timeCellB;    //String value of totalTime for point B, it is used to search Excel sheet
				    int rowIndex;    //stores the index of point A's row on the table
				    int rowIndexB;    //stores the index of point B's row on the table
				    float totalBinTime = 0;    //stores the time it takes to empty all the bins
				    double travelTime = 0;    //stores the time it takes to get from point A to B
				    float travelDistance = 0;    //the distance between point A and point B
				    int binCount = 0;    //The number of bins at point A
				    String tempBinCount;    //stores the String from the table of binCount
				    double binTime;    //stores the time it takes for each bin
				    
				    /*
				     * totalTime works
				     * This will give the value of total time worked in min
				     * in the format of HR.MIN
				     */
				    
				    /*
				     * convert the value of each point into just minutes to subtract them
				     * to convert HR.MIN into just minutes it must go through a while loop
				     * while the time >= 1 there is still another hour to convert
				     * counting each each hour by subtracting by 1
				     * then multiply the number of hours(tempACount) by 60 (# of minutes in a hour)
				     * you get the total number of minutes from the hours
				     * then add on the remaining minutes by converting them from decimal
				     * by multiplying by 100 and then adding them to the total
				     * giving you the converted number of minutes (tempAMin) 
				     */
				    float tempA = colNum[rowNum];
				    int tempACount = 0;
				    float tempAMin = 0;
				    while (tempA >= 1){
				    	tempA = tempA - 1;
				    	tempACount++;
				    }
				    tempAMin = tempACount * 60;
				    tempA = tempA * 100;
				    tempAMin = tempAMin + tempA;
				    
				    float tempB = colNum[rowNum+1];
				    int tempBCount = 0;
				    float tempBMin = 0;
				    while (tempB >= 1){
				    	tempB = tempB - 1;
				    	tempBCount++;
				    }
				    tempBMin = tempBCount * 60;
				    tempB = tempB * 100;
				    tempBMin = tempBMin + tempB;
				    
				    totalTime = tempBMin - tempAMin;
				    //System.out.println(tempBMin + " - " + tempAMin + " = " + totalTime);
				    
				    /*
				     * convert back into minutes, though if over 99 minutes it is not accurate
				     * conversion that is handled later because if totalTime is over an hour
				     * that is considered to be bad data and is thrown out
				     */
				    totalTime = totalTime / 100;
				    
				    /*
				     * timeCell contains a string value of the time and changes the . back to a :
				     * so that you can search the Excel document for the correct row
				     * which then you can find the location and the bin count for the time
				     */
				    timeCell = "" + colNum[rowNum];
				    timeCell = timeCell.replace(".", ":");
				    
				    /*
				     * if the value of time ended in one 0 it needs to append that 0 back on
				     * else if the value of time ended in two 0s it needs to append both 0s on
				     */
				    if (timeCell.substring(timeCell.indexOf(":")).length() == 2 ){
				    	timeCell += "0";
				    	//System.out.println("test: " + timeCell);
				    }
				    else if (timeCell.substring(timeCell.indexOf(":")).length() == 1 ){
				    	timeCell += "00";
				    }
				    
				    rowIndex = sheet.findCell(timeCell).getRow();
				    rowData = sheet.getRow(rowIndex);
				    
				    //System.out.println(rowData[0].getContents());
				    
				    /*
				     * timeCellB contains a string value of the time and changes the . back to a :
				     * so that you can search the Excel document for the correct row
				     * which then you can find the location and the bin count for the time
				     */
				    timeCellB = "" + colNum[rowNum+1];
				    timeCellB = timeCellB.replace(".", ":");
				    
				    /*
				     * if the value of time ended in one 0 it needs to append that 0 back on
				     * else if the value of time ended in two 0s it needs to append both 0s on
				     */
				    if (timeCellB.substring(timeCellB.indexOf(":")).length() == 2 ){
				    	timeCellB += "0";
				    	//System.out.println("test: " + timeCellB);
				    }
				    else if (timeCellB.substring(timeCellB.indexOf(":")).length() == 1 ){
				    	timeCellB += "00";
				    }
				    
				    rowIndexB = sheet.findCell(timeCellB).getRow();
				    rowDataB = sheet.getRow(rowIndexB);
				    
				    //System.out.println(rowDataB[0].getContents());
				    
				    /*
				     * find the distance between the two points and then the time it takes to cover that distance
				     * then subtract that travel time off of totalTime which will leave you with total time for bin
				     */
				    //find the distance between the two points
				    String fileName;
				    String pickupPointNoA;
				    String pickupPointNoB;
				    
				    pickupPointNoA = rowData[1].getContents();
				    pickupPointNoB = rowDataB[1].getContents();
				    
				    
				    /*
				     * only need the file for point A because that contains the distance from A to B
				     */
				    fileName = "Y:/CPSC-476-01.0113/Project 5 Period Routing Problem/Files/ShortestPathFiles/";
				    if (Integer.parseInt(pickupPointNoA) > 9){
				    	fileName += "Point No "+pickupPointNoA+".txt";
				    }
				    else if (Integer.parseInt(pickupPointNoA) > 0){
				    	fileName += "Point No 0"+pickupPointNoA+".txt";
				    }
				    
				    
				    //System.out.println(fileName);
				    TextFileLoader loader = new TextFileLoader();
					String[] sDist = loader.getDataFromFileAtIndex(fileName, 0);
				    /*
				    for (int j=0; j<sDist.length; j++){
				    	System.out.print(sDist[j]);
				    }
				    System.out.println();
				    */
					//System.out.println(pickupPointNoB);
					/*
					 * if greater than 24 need to subtract two because points 22 and 23 are not in the data 
					 * if point B is higher than point A then subtract 1 because there is no distance from A to A
					 */
					int temp = Integer.parseInt(pickupPointNoB);
					//System.out.println(temp);
					
					if (Integer.parseInt(pickupPointNoB) > 24){
						temp = temp - 2;
					}
					if (Integer.parseInt(pickupPointNoB) > Integer.parseInt(pickupPointNoA)){
						temp = temp - 1;
					}
					
					travelDistance = Float.parseFloat(sDist[temp-1]);
					//System.out.println(travelDistance);
					
				    //find the time it takes to cover that distance
					/*
					 * travelTime here gives time in a count of minutes
					 * but what is needed is that number to be a decimal
					 * HR.MIN is the correct format
					 */
				    travelTime = Timing.calculateTime(travelDistance);
				    //System.out.println(travelTime);
				    
				    /*
				     * to convert the amount of minutes into the correct format
				     * if there is more that 60 minutes then there is at least an hour
				     * repeat this process until you have under 60 mins left
				     * then divide that number of minutes by 100
				     *   to move the decimal to the right two
				     * and then add the amount of hours to that new value
				     * which then will give you HR.MIN format needed
				     */
				    int tempCount = 0;
				    while (travelTime > 60){
				    	travelTime = travelTime - 60;
				    	tempCount++;
				    }
				    travelTime = travelTime / 100;
				    travelTime = travelTime + tempCount;
				    
				    //System.out.println("                  " + totalTime + " - " + travelTime);
				    
				    /*
				     * find the time it takes to unload the bins by subtracting totalTime by travelTime
				     */
				    binTime = totalTime - travelTime;
				    
				    //System.out.println(binTime);
				    
				    /*
				     * binTime contains the time it takes to empty out the bins
				     * so now divide that by how many bins are at location one
				     * if value is unknown then set binCount = 0
				     * if value is tip-cart then set binCount = -1
				     */
				    tempBinCount = rowData[5].getContents();
				    if (tempBinCount.contains("tip-cart")){
				    	binCount = -1;
				    }
				    else if (tempBinCount.contains("unknown")){
				    	binCount = 0;
				    }
				    else {
				    	binCount = Integer.parseInt(tempBinCount);
				    }
				    
				    //System.out.println(binCount);
				    
				    /*
				     * if binCount is greater than 0 and binTime less than .60 or an hour
				     * binTime being less than an hour then eliminates any bad data
				     *   then it is a bin location and binTimeTotal needs updated
				     *   it is updated by adding the average time per bin it took at that location
				     *   then binCountTotal is increased by one for the average of all locations
				     * if binCount is equal to -1 and binTime less than .60 or an hour
				     * binTime being less than an hour then eliminates any bad data
				     *   then it is a tipcart location so tipcartTimeTotal needs updated
				     *   it is updated by adding the average time per tipcart (1) it took at that location
				     *   then tipcartCountTotal is increased by one for the average of all locations
				     * if neither of those then it is an unknown bin count on location so nothing happens
				     */
				    if (binCount > 0 && binTime < .60){
				    	binTimeTotal += (binTime / binCount);
				    	binCountTotal++;
				    }
				    else if (binCount == -1 && binTime < .60){
				    	tipcartTimeTotal += binTime;
				    	tipcartCountTotal++;
				    	//System.out.println(binTime);
				    }
				    else {
				    	
				    }
				    
				    //System.out.println();
				    
				} //end of rowNum for loop
				   
		    } //end of columnNum for loop
			
			 
		} //end of sheetNum for loop
	}
	
	/**
	 * finds the average amount for both the bin time and tip cart time
	 */
	private static void AvgBinTime(){
		//System.out.println(binAvgTotal + ", " + binTimeTotal + ", " + binCountTotal);
		binAvgTotal = binTimeTotal / binCountTotal;
		tipcartAvgTotal = tipcartTimeTotal / tipcartCountTotal;
		
		/*
		 * after finding the average of each point the values are still in the format HR.MIN
		 * so a conversion to just minutes is required for both bin and tipcart 
		 */
		int tempCount = 0;
	    while (binAvgTotal > 60){
	    	binAvgTotal = binAvgTotal - 60;
	    	tempCount++;
	    }
	    binAvgTotal = binAvgTotal * 100;
	    binAvgTotal = binAvgTotal + tempCount;
	    
	    tempCount = 0;
	    while (tipcartAvgTotal > 60){
	    	tipcartAvgTotal = tipcartAvgTotal - 60;
	    	tempCount++;
	    }
	    tipcartAvgTotal = tipcartAvgTotal * 100;
	    tipcartAvgTotal = tipcartAvgTotal + tempCount;
	    
	}
	
	/**
	 * Loads the excel document at the specified path and returns it.
	 * @return delayTimesExcel
	 */
	private static FileInputStream loadFileDelayTimes(){
		try {
			FileInputStream delayTimesExcel = new FileInputStream(new File(
				"AI/Excel Files/DelayTimes.xls"));
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
	 * get function allows other classes to get the average time it takes to empty a bin
	 * @return binAvgTotal
	 */
	public static double getAvgBin(){
		Sheet sheet = delayTimesWorkbook.getSheet(0);

		//contains all the delay times in the file, column #2
		Cell colData[] = null;
		colData = sheet.getColumn(1);
		
		//contains the row number that the Bin delay time is on
		int rowIndex;
		rowIndex = sheet.findCell(delayTimesBinLabel).getRow();
		
		//contains the delay time from the excel sheet parsed to a double instead of string
		returnBinAvgTotal = Double.parseDouble(colData[rowIndex].getContents());
		return returnBinAvgTotal;
	}
	/**
	 * get function allows other classes to get the average time it takes to empty a tipcart
	 * @return tipcartAvgTotal
	 */
	public static double getAvgTipcart(){
		Sheet sheet = delayTimesWorkbook.getSheet(0);

		//contains all the delay times in the file, column #2
		Cell colData[] = null;
		colData = sheet.getColumn(1);
		
		//contains the row number that the Tipcart delay time is on
		int rowIndex;
		rowIndex = sheet.findCell(delayTimesTipcartLabel).getRow();
		
		//contains the delay time from the excel sheet parsed to a double instead of string
		returnTipcartAvgTotal = Double.parseDouble(colData[rowIndex].getContents());
		return returnTipcartAvgTotal;
	}
	
	/**
	 * writes the average times of the bins and tipcarts to an Excel file 
	 */
	public static void write() {
		DelayFileAccess.setDelayTime(delayTimesBinLabel, binAvgTotal);
		    
		DelayFileAccess.setDelayTime(delayTimesTipcartLabel, tipcartAvgTotal);
	}
	
	
	
	//Main function
	public static void BinTimingCall(){ 
		evaluateWorkBook(trashBinWorkbook);
		AvgBinTime();
		
		//output the data to Excel spreedsheet
		write();
		
		//System.out.println();
		//System.out.println();
		System.out.println("Bin Time: " + getAvgBin());
		System.out.println("Tipcart Time: " + getAvgTipcart());
		
	}
	
}
