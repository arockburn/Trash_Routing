/**
 * Takes all of the data from the excel sheets and has many different methods for parsing and loading them
 * into linked lists, etc. A data organizing class for the excel sheets that is used throughout
 * the entire program.
 * 
 * @author Sean M Brown
 * Version 1.0
 * Last revised: April 2013
 */

import java.text.*;
import java.util.*;
import java.io.*;

import jxl.*;

public class LinkedLists {
	
	//Declare the linked list variables, named by pickup days 
	public static LinkedList<Object> twoDayTrash = new LinkedList<Object>();
	public static LinkedList<Object> threeDayTrash = new LinkedList<Object>();
	public static LinkedList<Object> fourDayTrash = new LinkedList<Object>();
	public static LinkedList<Object> fiveDayTrash = new LinkedList<Object>();
	public static LinkedList<Object> sixDayTrash = new LinkedList<Object>(); //only going to need 5
	public static LinkedList<Object> wholeBook = new LinkedList<Object>();
	
	//declare the file input stream to read the excel file, then put that file into a workbook
	public static FileInputStream trashExcel;
	public static Workbook trashRoutesWorkbook;
	
	//variable to be used to keep count of how many columns/rows are used in the sheet
	private static int columns = 16;
	private static int rows = 0;
	
	/**
	 * Default constructor for the LinkedLists class, loads file
	 */
	LinkedLists() 
	{
		trashExcel = loadFile();
		trashRoutesWorkbook = fetchWorkbook(trashExcel);
	}
	
	public LinkedList<Object> getWholeBook()
	{
		loadAllDays();
		return wholeBook;
	}
	
	public static void closeBook()
	{
		trashRoutesWorkbook.close();
	}
	
	/**
	 * Loads the excel document at the specified path and returns it.
	 * 
	 * @return FileInputStream trashExcel
	 */
	private static FileInputStream loadFile()
	{
		try {
			FileInputStream trashExcel = new FileInputStream(new File("AI/Excel Files/TrashRoutes-Frequency.xls"));
			return trashExcel;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Takes an excel workbook file and returns it in an excel Workbook form.
	 * 
	 * @param trashBook
	 * @return trashExcel
	 */
	private static Workbook fetchWorkbook(FileInputStream trashBook) {
		try 
		{
			Workbook trashExcel = Workbook.getWorkbook(trashBook);
			return trashExcel;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Takes an excel workbook and parses its first sheet into a linked list, based on its amount of
	 * pickup days. Each linked list is comprised of the data of each row that is found to contain
	 * the desired amount of pickup days.
	 * 
	 * @param trashBook
	 * @param list
	 * @param days
	 */
	public static void workbookToLinkedLists(Workbook trashBook, LinkedList<Object> list, int days) 
	{
		/*declare variable for the first Worksheet of the Excel Workbook, also for the cell data, 
		and the amount of columns in the sheet. */
		Sheet allSheet = trashBook.getSheet(0);
		Cell cellData[] = null;
		rows = allSheet.getRows();
		
		for(int i = 0; i < rows; i++) {
			//set the cellData to the (i)th row in the sheet
			cellData = allSheet.getRow(i);
			try {
				//if the current cell isn't empty then continue
				if(!cellData[0].getContents().isEmpty() && cellData != null) {
					for(int j = 0; j < 16; j++) {
						//get the contents of the current cell in the row
						String contents = cellData[j].getContents();
						//if the current column is 4 this is the column with the pickup days, so continue
						if(j == 4) {
							try {
								//checks to see if this cell is equal to the "days" variable you want
								if(Integer.parseInt(contents.trim()) == days) {
									//if this is a row with your desired pickup date, add it all to the list
									for(int k = 0; k < 16; k++) {
										list.addLast(cellData[k].getContents());
									}
								}
							} catch (Exception e) {/*keeps going if contents aren't parsable
							such as a string of words, etc. Yes this is bad coding */}
						}
					}
				}
			} catch (Exception e) 
			{
				//excel misreads columns so it goes out of bounds. Keep going though
			}
		}
	}
	
	/**
	 * Returns the number of rows in the excel sheet
	 * @return row count
	 */
	public static int getColumns()
	{
		return 16;
	}
	
	/**
	 * Prints the linked list in an easily readable form. It resembles the rows of the excel sheet
	 * it was derived from.
	 * 
	 * @param list
	 */
	public void printList(LinkedList<Object> list) {
		
		//a counter to keep track of which column you are in, so it can add an endline at the end
		int j = 0;
		
		for(int i = 0; i < list.size(); i++){
			//increment column counter and print the node of the list with a space after it
			j++;
			System.out.print(list.get(i) + "  ");
			
			//if the counter is equal to the column you are at the end, add a new line and reset counter
			if(j == columns) {
				j = 0;
				System.out.println();
			}
		}
	}
	
	/**
	 * Prints the latitude and longitude values of the entire list in an easily readable form.
	 * 
	 * @param list
	 */
	public void printLatLong(LinkedList<Object> list) {
		//i is the latitude location index of a row, j is the longitude
		int i = 2;
		int j = 3;
		
		//for every row, print out its latitude and longitude location
		for(int k = 0; k < rows; k++) {
			//if k isn't 0 (because you need to print out the first row's lat/long first increment
			if(k!= 0) 
			{
				/*you increment by column length because this essentially increments down a row,
				as the linked list is stored as a node containing each cell*/
				i += columns;
				j += columns;
			}
			
			//print the lat/long at this location
			System.out.println(list.get(i) + " , " + list.get(j));
		}
	}
	
	/**
	 * Returns the latitude and longitude value contained in the specified row.
	 * 
	 * @param list
	 * @param row
	 * @return latLong
	 */
	public static String getLatLongByRow(LinkedList<Object> list, int row)
	{
		//i and j are index locations of the lat/long of a row
		String latLong = "";
		int i = 2;
		int j = 3;
		
		//increment to the desired row
		i += columns * row;
		j += columns * row;
		
		//print the desired row's lat/long value
		latLong = list.get(i) + " , "  + list.get(j);
		return latLong;
	}
	
	/**
	 * Gets a pickup point from a particular row
	 * 
	 * @param list
	 * @param row
	 * @return the name of the pickup point
	 */
	public static String getPickupPointByRow(LinkedList<Object> list, int row) 
	{
		String pickupPoint = "";
		int i = 1;
		i += columns * row;
		
		pickupPoint = list.get(i).toString();
		return pickupPoint;
	}
	
	/**
	 * Returns the latitude and longitude values stored in a given row, specified by the name of the
	 * pickup point of the row.
	 * 
	 * @param list
	 * @param name
	 * @return latLong
	 */
	public static String getLatLongByName(LinkedList<Object> list, String name) 
	{
		String latLong = "";
		
		//go through the list searching for the desired name
		for(int i = 0; i < list.size(); i++) {
			//if the node contains your name then continue
			if(name.equals(list.get(i).toString())){
				/*increment up 2 and 3 because this is where lat/long value will be stored, 
				then save it into a string*/
				latLong = list.get(i + 2) + " , " + list.get(i + 3);
				break;
			}
		}
		
		return latLong;
	}
	
	/**
	 * Removes a row from the linked list as specified by the name of the pickup point of the row.
	 * 
	 * @param list
	 * @param name
	 */
	public static void removeRowByName(LinkedList<Object> list, String name) 
	{
		//search through the list for your specified name
		for(int i = 0; i < list.size(); i++) {
			//if the node contains the name, continue
			if(name.equals(list.get(i).toString())) {
				/*remove all the columns of the row that starts at this index, this works because
				when you remove from a linked list the values shift left*/
				for(int j = 0; j < columns; j++) {
					list.remove(i);
				}
			}
		}
	}
	
	/**
	 * Gets the name of the pickup point at the specified row.
	 * 
	 * @param list
	 * @param row
	 * @return name
	 */
	public static String getNameByRow(LinkedList<Object> list, int row)
	{
		//i is the index of where the name is at the desired row
		int i = columns * row;
		String name = "";
		
		//return the name at the index
		name = list.get(i).toString();
		return name;
	}
	
	/**
	 * Gets the number of the row that contains the specified pickup point name.
	 * 
	 * @param list
	 * @param name
	 * @return
	 */
	public static int getRowByName(LinkedList<Object> list, String name) 
	{
		//this is the variable to be returned
		int row = 0;
		
		//go through the list, search for the node that contains your desired name
		for(int i = 0; i < list.size(); i++) 
		{
			//if the node matches your name continue
			if(list.get(i).equals(name)) 
			{
				/*the name is at the beginning of a column, so just divide by the columns
				and that will give you the row number*/
				row = i / columns;
			}
		}
		
		return row;
	}
	
	/**
	 * Returns the bin count at the given pickup point.
	 * 
	 * @param pickupPoint
	 * @return
	 */
	public int getBinCountByPickupPoint(LinkedList<Object> wholeBook, int pickupPoint) 
	{
		int pointIndex = 1;
		int binIndex = 4;
		int binCount = 0;
		NumberFormat format = new DecimalFormat("00");
		
		
		for(int i = 0; i < wholeBook.size(); i++) 
		{
			if(i == (pointIndex)) 
			{
				pointIndex += columns;
				//if(wholeBook.get(i).equals(String.valueOf(pickupPoint))) 
				if(wholeBook.get(i).equals(String.valueOf(format.format(pickupPoint)))) 
				{
					binIndex += i;
					try {
						binCount = Integer.parseInt(wholeBook.get(binIndex).toString());
					} catch (Exception e) {return -1;}
					break;
				}
			}
		}
		return binCount;
	}
	
	/**
	 * Returns the building type of the given pickup point.
	 * 
	 * @param pickupPoint
	 * @return building type
	 */
	public int getBuildingType(LinkedList<Object> wholeBook, int pickupPoint)
	{
		int pointIndex = 1;
		int bTypeIndex = 12;
		int bType = 0;
		
		for(int i = 0; i < wholeBook.size(); i++) 
		{
			if(i == (pointIndex)) 
			{
				pointIndex += columns;
				if(wholeBook.get(i).equals(String.valueOf(pickupPoint))) 
				{
					bTypeIndex += i;
					try {
						bType = Integer.parseInt(wholeBook.get(bTypeIndex).toString());
					} catch (Exception e) {return 0;}
					break;
				}
			}
		}
		return bType;
	}
	
	/**
	 * Returns the amount of classroom buildings near the given pickup point.
	 * 
	 * @param pickupPoint
	 * @return class buildings near pickup point
	 */
	public int getClassBuildingsNear(LinkedList<Object> wholeBook, int pickupPoint)
	{
		int pointIndex = 1;
		int cBldNearIndex = 13;
		int cBldNear = 0;
		
		for(int i = 0; i < wholeBook.size(); i++) 
		{
			if(i == (pointIndex)) 
			{
				pointIndex += columns;
				if(wholeBook.get(i).equals(String.valueOf(pickupPoint))) 
				{
					cBldNearIndex += i;
					try {
						cBldNear = Integer.parseInt(wholeBook.get(cBldNearIndex).toString());
					} catch (Exception e) {return 0;}
					break;
				}
			}
		}
		return cBldNear;
	}
	
	/**
	 * Returns the amount of food buildings near the given pickup point.
	 * 
	 * @param pickupPoint
	 * @return food buildings near
	 */
	public int getFoodBuildingsNear(LinkedList<Object> wholeBook, int pickupPoint)
	{
		int pointIndex = 1;
		int fBldNearIndex = 14;
		int fBldNear = 0;
		
		for(int i = 0; i < wholeBook.size(); i++) 
		{
			if(i == (pointIndex)) 
			{
				pointIndex += columns;
				if(wholeBook.get(i).equals(String.valueOf(pickupPoint))) 
				{
					fBldNearIndex += i;
					try {
						fBldNear = Integer.parseInt(wholeBook.get(fBldNearIndex).toString());
					} catch (Exception e) {return 0;}
					break;
				}
			}
		}
		return fBldNear;
	}
	/**
	 * Searches through a list in search of a particular name "pickupPoint"
	 * 
	 * @param wholeBook
	 * @param pickupPoint
	 * @return name of pickup point
	 */
	
	public String getNameByPickupPoint(LinkedList<Object> wholeBook, String pickupPoint)
	{
		int pickupIndex = 1;
		int nameIndex = 0;
		String name = "no_name";
		
		for(int i = 0; i < wholeBook.size(); i++)
		{
			if(i == (pickupIndex))
			{
				pickupIndex += columns;
				try 
				{
					if(wholeBook.get(i).equals(pickupPoint))
					{
						name = wholeBook.get(i-1).toString();
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		
		return name;
	}
	/**
	 * Loads the indexes of a particular list into a saveList
	 * 
	 * @param populatedList
	 * @param saveList
	 * @return the saveList
	 */
	public LinkedList<Integer> getIndexes(LinkedList<Object> populatedList, LinkedList<Integer> saveList)
	{
		int row = 1;
		for(int i = 0; i < populatedList.size(); i++)
		{
			if(row == 16 && !populatedList.get(i).toString().equals("None"))
			{
				saveList.addLast(Integer.parseInt(populatedList.get(i).toString()));
			}
			if(row != 16) 
			{
				row++;
			}	
			else
			{
				row = 1;
			}
		}
		
		return saveList;
	}
	/**
	 * Loads all the in the excel sheet into 1, 2, 3, 4, 5, 6 day linked lists. 
	 */
	public void loadAllDays() 
	{
		wholeBook = new LinkedList<Object>();
		workbookToLinkedLists(trashRoutesWorkbook, wholeBook, 1);
		workbookToLinkedLists(trashRoutesWorkbook, wholeBook, 2);
		workbookToLinkedLists(trashRoutesWorkbook, wholeBook, 3);
		workbookToLinkedLists(trashRoutesWorkbook, wholeBook, 4);
		workbookToLinkedLists(trashRoutesWorkbook, wholeBook, 5);
		workbookToLinkedLists(trashRoutesWorkbook, wholeBook, 6);
	}
	/**
	 * Loads data from an excel sheet into a list
	 * @param list
	 * @param pickups
	 * @return the new list
	 */
	
	public LinkedList<Object> loadSpecificList(LinkedList<Object> list, int pickups) 
	{
		workbookToLinkedLists(trashRoutesWorkbook, list, pickups);//convert workbook to linked list
		return list;
	}
}
