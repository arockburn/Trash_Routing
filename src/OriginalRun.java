import java.util.*;
import java.io.*;
import jxl.*;
import java.io.File;
import java.io.FileInputStream;
import jxl.Workbook;


public class OriginalRun {
	//declare the file input stream to read the excel file, then put that file into a workbook
	private static FileInputStream trashBinExcel = loadFile();
	private static Workbook trashBinWorkbook = fetchWorkbook(trashBinExcel);
	
	private static int numSheet = 0;
	
	private static int numColumn = 0;
	private static int numRow = 0;
	
	private static double totalTime = 0;
	
	
	private static double tipcartDelayTime = DelayFileAccess.getDelayTime("Tipcart");
	private static double binDelayTime = DelayFileAccess.getDelayTime("Bin");
	
	
	
	/**
	 * Loads the excel document at the specified path and returns it.
	 * @return trashBinExcel
	 */
	private static FileInputStream loadFile(){
		try {
			FileInputStream trashBinExcel = new FileInputStream(new File(
				"E:/Trash RoutingComplete/AI/Excel Files/TrashRoutes-Dallas-12-19-2012.xls"));
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
	
	
	private static void makeRun(Workbook routeBook){
		numSheet = routeBook.getNumberOfSheets();
		//run through each sheet
		for (int i=0; i<numSheet; i++){
			totalTime = 0;
			Sheet sheet = routeBook.getSheet(i);
			
			numColumn = sheet.getColumns();
			numRow = sheet.getRows();
			
			//contains each pickup point number in the order they go
			Cell colTwoData[];
			colTwoData = sheet.getColumn(1);
			
			//contains the bin counts at each location
			Cell colSixData[];
			colSixData = sheet.getColumn(5);
			
			
			for (int j=1; j<numRow; j++){
				//System.out.println(colTwoData[j].getContents());
				
				//find the time it takes to drive from each pick up point
				//each run through must have two points a start and finish
				//  so to find the distance j must be at least one less then
				//  the total number of rows
				if (j<numRow-1){
					String fileName;
					String pickupPointNoA = colTwoData[j].getContents();
				    String pickupPointNoB = colTwoData[j+1].getContents();
					
					/*
				     * only need the file for point A because that contains the distance from A to B
				     */
				    fileName = "E:/Trash RoutingComplete/AI/Excel Files/paths/";
				    if (Integer.parseInt(pickupPointNoA) > 9){
				    	fileName += "Point No "+pickupPointNoA+".txt";
				    }
				    else if (Integer.parseInt(pickupPointNoA) > 0){
				    	fileName += "Point No 0"+pickupPointNoA+".txt";
				    }
				    
				    TextFileLoader loader = new TextFileLoader();
					String[] sDist = loader.getDataFromFileAtIndex(fileName, 0);
					
					int temp = Integer.parseInt(pickupPointNoB);
					//System.out.println(temp);
					
					if (Integer.parseInt(pickupPointNoB) > 24){
						temp = temp - 2;
					}
					if (Integer.parseInt(pickupPointNoB) > Integer.parseInt(pickupPointNoA)){
						temp = temp - 1;
					}
					
					float travelDistance = Float.parseFloat(sDist[temp-1]);
					
					double travelTime = Timing.calculateTime(travelDistance);
					
					//System.out.println(travelTime);
					//totalTime spent driving from point to point
					totalTime = totalTime + travelTime;
				}
				
				//now need to add in the time it takes to unload the trash at each point
				
				//System.out.println(colSixData[j].getContents());
				if (colSixData[j].getContents().equals("tip-cart")){
					//System.out.println(tipcartDelayTime);
					totalTime = totalTime + tipcartDelayTime;
				}
				else if(colSixData[j].getContents().equals("unknown")){
					//System.out.println("UNKNOWN");
				}
				else if (Integer.parseInt(colSixData[j].getContents()) > 0){
					//System.out.println(binDelayTime);
					totalTime = totalTime +
							(binDelayTime * Integer.parseInt(colSixData[j].getContents()));
				}
				else {
					
				}
				//System.out.println(totalTime);
				
				
			}
			
			System.out.println("totalTime for sheet " + i + ": " + totalTime);
		}
		
	}
	
	//Main function
	public static void main(String[] args) { 
		makeRun(trashBinWorkbook);
		
	}
	
	
}
