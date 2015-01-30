import java.util.*;
import java.io.*;

import jxl.*;

import java.io.File;
import java.io.FileInputStream;

import jxl.Workbook;

public class FrequencyExtract {
	//this class reads an excel sheet and extracts the top frequency for which all trash points are to be collected
	//Author: Aaron Rockburn
	
	private static FileInputStream trashBinExcel = loadFile();
	private static Workbook trashBinWorkbook = fetchWorkbook(trashBinExcel);
	
	private static int numSheets;
	private static int maxFrequency;
	private static int nextFrequency;
	private static int numRows;
	
	private static FileInputStream loadFile(){
		//this method loads an excel file into a file stream
		try {
			FileInputStream trashBinExcel = new FileInputStream(new File(
				"E:/Trash RoutingComplete/AI/Excel Files/TrashRoutes-Frequency.xls"));
			return trashBinExcel;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static Workbook fetchWorkbook(FileInputStream trashBook) {
		//this method converts a file stream into a workbook in order to access its data more easily
		try {
			Workbook trashBinWorkbook = Workbook.getWorkbook(trashBook);
			return trashBinWorkbook;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static int getFrequency(Workbook wkbk){
		//this method parses through a workbook looking for 
		Sheet sheet1 = wkbk.getSheet(0);
		Cell freqColContent[];
		freqColContent = sheet1.getColumn(4);
		String firstCell = freqColContent[18].getContents();
		String nextCell = freqColContent[20].getContents();
		maxFrequency = Integer.parseInt(firstCell);
		nextFrequency = Integer.parseInt(nextCell);
		numRows = sheet1.getRows();
		
		
		for(int i = 1; i < numRows; i++){
			if(maxFrequency < nextFrequency){
				maxFrequency = nextFrequency;
			}
			nextCell = freqColContent[i].getContents();
			nextFrequency = Integer.parseInt(nextCell);
		}
		return maxFrequency;
	}

	public static void main(String[] args){
		getFrequency(trashBinWorkbook);
	}
}
