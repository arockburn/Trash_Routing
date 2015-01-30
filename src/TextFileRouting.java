/***
 * Puts together all the possible combinations of paths to be taken that can be used in the schedule.
 * Gets the shortest path for each segment and gets the combination based on time.
 *
 * @author Sean M Brown
 * Last Edited: 4/25/13
 * Version 1.0
 */

import jxl.*;
import jxl.write.*;
import jxl.write.Label;
import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TextFileRouting
{
	private static final double ITERATIONS = 1;		//this variable controls how many times the comboAlgorithm loop
														// is run. The higher the iterations, the better the final results
	private LinkedLists linkedListParser = new LinkedLists();
	private LinkedList<Object> wholeBook = linkedListParser.getWholeBook();
	private LinkedList<String> pickupPointNames = new LinkedList<String>();
	private LinkedList<String> indexedPoints = new LinkedList<String>();
	private LinkedList<String> pickupPoints = new LinkedList<String>();
	private LinkedList<String> pickupPaths = new LinkedList<String>();
	private LinkedList<Double> distances = new LinkedList<Double>();
	private LinkedList<Double> dayDistances = new LinkedList<Double>();
	private LinkedList<Double> times = new LinkedList<Double>();
	private LinkedList<Integer> bins = new LinkedList<Integer>();
	private LinkedList<Double> binServiceTime = new LinkedList<Double>();
	private LinkedList<Double> startTime = new LinkedList<Double>();
	private LinkedList<Double> stopTime = new LinkedList<Double>();
	private LinkedList<Double> penalty = new LinkedList<Double>();
	private LinkedList<String> dayPath = new LinkedList<String>();
	private LinkedList<String> finalPath = new LinkedList<String>();
	private LinkedList<String> returnPath = new LinkedList<String>();
	//these lists hold the combination for the corresponding path
	//the 6XX paths are used for the 5XX paths as well
	private LinkedList<String> sixThreeTwo = new LinkedList<String>();
	private LinkedList<String> sixFourTwo = new LinkedList<String>();
	private LinkedList<String> sixFiveTwo = new LinkedList<String>();
	private LinkedList<String> sixFourThree = new LinkedList<String>();
	private LinkedList<String> sixFiveThree = new LinkedList<String>();
	private LinkedList<String> sixFiveFour = new LinkedList<String>();
	private LinkedList<String> sixFiveFourTwo = new LinkedList<String>();
	private LinkedList<String> sixFiveThreeTwo = new LinkedList<String>();
	private LinkedList<String> sixFourThreeTwo = new LinkedList<String>();
	private LinkedList<String> sixFiveFourThree = new LinkedList<String>();
	private LinkedList<String> fiveFour = new LinkedList<String>();
	private LinkedList<String> fourThreeTwo = new LinkedList<String>();
	private LinkedList<String> fourTwo = new LinkedList<String>();
	private LinkedList<String> fourThree = new LinkedList<String>();
	private LinkedList<String> threeTwo = new LinkedList<String>();
	private LinkedList<String> three = new LinkedList<String>();
	private LinkedList<String> two = new LinkedList<String>();
	private LinkedList<String> twoOne = new LinkedList<String>();
	private LinkedList<String> one = new LinkedList<String>();
	//these doubles save the total time for each path
	private double bttSixThreeTwo = 0;
	private double bttSixFourTwo = 0;
	private double bttSixFiveTwo = 0;
	private double bttSixFourThree = 0;
	private double bttSixFiveThree = 0;
	private double bttSixFiveFour = 0;
	private double bttSixFiveFourTwo = 0;
	private double bttSixFiveThreeTwo = 0;
	private double bttSixFourThreeTwo = 0;
	private double bttSixFiveFourThree = 0;
	private double bttFiveFour = 0;
	private double bttFourThreeTwo = 0;
	private double bttFourTwo = 0;
	private double bttFourThree = 0;
	private double bttThreeTwo = 0;
	private double bttThree = 0;
	private double bttTwo = 0;
	private double bttTwoOne = 0;
	private double bttOne = 0;
	private double totalDistance = 0;
	private double totalTime = 0;
	private LinkedList<String> prevStart = new LinkedList<String>();
	private String path = "";
	private String day = "";
	private String currStart = "";
	private String endPoint = "";
	private int loadCount = 0;
	private int maxDay = 0;

	/**
	 * Loads all the pickup points into the pickupPoints array.
	 *
	 * @param pickupDays
	 */
	private void loadPickups(int pickupDays)
	{
		LinkedLists listLoad = new LinkedLists();
		LinkedList<Object> list = new LinkedList<Object>();
		list = listLoad.loadSpecificList(list, pickupDays);//use pickupDays to identify and load a list into a linked list
		if(pickupDays == maxDay){
			if(maxDay <= 5){
				list = listLoad.loadSpecificList(list, 6);
			}
			if(maxDay <= 4){
				list = listLoad.loadSpecificList(list, 5);
			}
			if(maxDay <= 3){
				list = listLoad.loadSpecificList(list, 4);
			}
			if(maxDay <= 2){
				list = listLoad.loadSpecificList(list, 3);
			}
			if(maxDay <= 1){
				list = listLoad.loadSpecificList(list, 2);
			}
		}
		int cols = LinkedLists.getColumns();//returns 16
		LinkedLists.closeBook();

		for(int i = 0; i < list.size()/cols; i++)
		{//for each row, add the pickupPoint to pickupPoints linked list
			pickupPoints.addLast(LinkedLists.getPickupPointByRow(list, i));
		}
	}

	/**
	 * Loads all names of the points to be visited
	 */
	private void loadNames()
	{
		//goes through workbook and pulls out pick up point names
		LinkedLists list = new LinkedLists(); //this is the LinkedLists class I made not the Java.Util.LinkedList

		//there are less than 60 points in total to be visited, this loop covers all possible points
		for(int i = 1; i < 60; i++)

		{
			String name = "";

			if(i > 9) {
				name = list.getNameByPickupPoint(wholeBook, "" + i);

				if(name != "no_name")
				{
					pickupPointNames.addLast(name);
					indexedPoints.addLast("" + i);
				}
			}
			else {
				name = list.getNameByPickupPoint(wholeBook, "0" + i);

				if(name != "no_name")
				{
					pickupPointNames.addLast(name);
					indexedPoints.addLast("0"+i);
				}
			}
		}
		LinkedLists.closeBook();
	}

	/**
	 * Retrieves all the distances from the given text file, and loads them into the distances linked
	 * list.
	 *
	 * @param fileName
	 */
	private void loadPickupDistances(String fileName)
	{
		TextFileLoader loader = new TextFileLoader();
		String[] sDist = loader.getDataFromFileAtIndex(fileName, 0);

		for(int i = 0; i < sDist.length; i++)
		{
			distances.addLast(Double.parseDouble(sDist[i]));
		}
	}

	/**
	 * Loads all the pickup paths from a text file and saves them into the pickupPaths linked list.
	 *
	 * @param fileName
	 */
	private void loadPickupPaths(String fileName)
	{
		TextFileLoader loader = new TextFileLoader();
		String[] sPath = loader.getDataFromFileAtIndex(fileName, 1);

		for(int i = 0; i < sPath.length; i++)
		{
			sPath[i] = sPath[i].substring(sPath[i].indexOf(" ")+1).trim();
			pickupPaths.addLast(sPath[i]);
		}
	}

	/**
	 * Loads the dayPath and dayDistances linked lists with the paths and distances from pickup points
	 * that are currently being used to route with.
	 */
	private void parseIntoList()
	{
		for(int i = 0; i < pickupPoints.size(); i++)
		{
			String currPoint = "NONSENSICALVALUE";

			if(!pickupPoints.get(i).equals("01") && !pickupPoints.get(i).equals(currStart))
			{
				if(!prevStart.contains(pickupPoints.get(i)))
				{
					currPoint = pickupPoints.get(i);

					for(int j = 0; j < pickupPaths.size(); j++)
					{
						if(pickupPaths.get(j).contains(currPoint))
						{
							dayPath.addLast(pickupPaths.get(j));
							dayDistances.addLast(distances.get(j));
							break;
						}
					}
				}
			}
		}
	}

	/**
	 * Removes a particular point from the pickupPoints list
	 *
	 * @param point
	 */
	private void removePickupPoint(String point)
	{
		for(int i = 0; i < pickupPoints.size(); i++)
		{
			if(pickupPoints.get(i).equals(point))
			{
				pickupPoints.remove(i);
			}
		}
	}
	/**
	 * Sorts a collection of linked lists
	 */

	private void sortMixed()
	{
		for(int i = 0; i < dayDistances.size(); i++)
		{
			double currVal = times.get(i);
			double currDist = dayDistances.get(i);
			int currBins = bins.get(i);
			double currBinService = binServiceTime.get(i);
			double currPenalty = penalty.get(i);
			double currStop = stopTime.get(i);
			double currStart = startTime.get(i);
			String currDay = dayPath.get(i);
			int j = i;

			int rand = 0 + (int)(Math.random() * ((100 - 0) + 1));
			int randWeight = 0 + (int)(Math.random() * ((95 - 0) + 1));

			if(rand > randWeight)
			{
				while(j > 0 && ((currDist < dayDistances.get(j - 1))))
				{
					times.set(j, times.get(j - 1));
					dayDistances.set(j, dayDistances.get(j - 1));
					bins.set(j, bins.get(j-1));
					binServiceTime.set(j, binServiceTime.get(j-1));
					stopTime.set(j, stopTime.get(j-1));
					startTime.set(j, startTime.get(j-1));
					penalty.set(j, penalty.set(j,  penalty.get(j-1)));
					dayPath.set(j, dayPath.get(j - 1));
					j--;
				}
			}
			else
			{
				while(j > 0 && ((currVal < times.get(j - 1))))
				{
					times.set(j, times.get(j - 1));
					dayDistances.set(j, dayDistances.get(j - 1));
					bins.set(j, bins.get(j-1));
					binServiceTime.set(j, binServiceTime.get(j-1));
					stopTime.set(j, stopTime.get(j-1));
					startTime.set(j, startTime.get(j-1));
					penalty.set(j, penalty.set(j,  penalty.get(j-1)));
					dayPath.set(j, dayPath.get(j - 1));
					j--;
				}
			}
			times.set(j, currVal);
			dayDistances.set(j, currDist);
			bins.set(j, currBins);
			binServiceTime.set(j, currBinService);
			stopTime.set(j, currStop);
			startTime.set(j, currStart);
			penalty.set(j, currPenalty);
			dayPath.set(j, currDay);
		}
	}

	/**
	 * Sorts linked lists in ascending order based on distance.
	 */
	private void sortByDist()
	{
		for(int i = 0; i < dayDistances.size(); i++)
		{
			double currVal = times.get(i);
			double currDist = dayDistances.get(i);
			int currBins = bins.get(i);
			double currBinService = binServiceTime.get(i);
			double currPenalty = penalty.get(i);
			double currStop = stopTime.get(i);
			double currStart = startTime.get(i);
			String currDay = dayPath.get(i);
			int j = i;

			while(j > 0 && (currDist < dayDistances.get(j - 1)))
			{
				times.set(j, times.get(j - 1));
				dayDistances.set(j, dayDistances.get(j - 1));
				bins.set(j, bins.get(j-1));
				binServiceTime.set(j, binServiceTime.get(j-1));
				stopTime.set(j, stopTime.get(j-1));
				startTime.set(j, startTime.get(j-1));
				penalty.set(j, penalty.set(j,  penalty.get(j-1)));
				dayPath.set(j, dayPath.get(j - 1));
				j--;
			}
			times.set(j, currVal);
			dayDistances.set(j, currDist);
			bins.set(j, currBins);
			binServiceTime.set(j, currBinService);
			stopTime.set(j, currStop);
			startTime.set(j, currStart);
			penalty.set(j, currPenalty);
			dayPath.set(j, currDay);
		}
	}

	/**
	 * Sorts the linked lists in ascending order based on time.
	 */
	private void sortByTime()
	{
		for(int i = 0; i < times.size(); i++)
		{
			double currVal = times.get(i);
			double currDist = dayDistances.get(i);
			int currBins = bins.get(i);
			double currBinService = binServiceTime.get(i);
			double currPenalty = penalty.get(i);
			double currStop = stopTime.get(i);
			double currStart = startTime.get(i);
			String currDay = dayPath.get(i);
			int j = i;

			while(j > 0 && (currVal < times.get(j - 1)))
			{
				times.set(j, times.get(j - 1));
				dayDistances.set(j, dayDistances.get(j - 1));
				bins.set(j, bins.get(j-1));
				binServiceTime.set(j, binServiceTime.get(j-1));
				stopTime.set(j, stopTime.get(j-1));
				startTime.set(j, startTime.get(j-1));
				penalty.set(j, penalty.set(j,  penalty.get(j-1)));
				dayPath.set(j, dayPath.get(j - 1));
				j--;
			}
			times.set(j, currVal);
			dayDistances.set(j, currDist);
			bins.set(j, currBins);
			binServiceTime.set(j, currBinService);
			stopTime.set(j, currStop);
			startTime.set(j, currStart);
			penalty.set(j, currPenalty);
			dayPath.set(j, currDay);
		}
	}

	/**
	 * Gets the time each drive from one spot to the next, and picking up bins will take, penalties
	 * included. Loads these times into the times linked list.
	 *
	 * @param permanentRoute
	 * @param dayOfWeek
	 */
	private void getTimes(boolean permanentRoute, int dayOfWeek)
	{
		LinkedLists getBins = new LinkedLists();
		int bins = 0;
		int bType = 0;
		int cBuildNear = 0;
		int fBuildNear = 0;

		if(!permanentRoute) {
			for(int i = 0; i < dayDistances.size(); i++)
			{
				bins = getBins.getBinCountByPickupPoint(wholeBook, Integer.parseInt(pickupPoints.get(i)));
				if(bins == 0)
				{
					//System.out.println();
				}
				this.bins.addLast(bins);
				bType = getBins.getBuildingType(wholeBook, Integer.parseInt(pickupPoints.get(i)));
				cBuildNear = getBins.getClassBuildingsNear(wholeBook, Integer.parseInt(pickupPoints.get(i)));
				fBuildNear = getBins.getFoodBuildingsNear(wholeBook, Integer.parseInt(pickupPoints.get(i)));
				LinkedList<String> timeInfo = Timing.getPenalty(dayDistances.get(i), bins, permanentRoute, dayOfWeek, bType, cBuildNear, fBuildNear);
				//calculate the total time to get each bin including penalties
				binServiceTime.addLast(Double.parseDouble(timeInfo.get(0)));
				penalty.addLast(Double.parseDouble(timeInfo.get(1)));
				stopTime.addLast(Double.parseDouble(timeInfo.get(3)));
				startTime.addLast(Double.parseDouble(timeInfo.get(4)));
				times.addLast(Double.parseDouble(timeInfo.get(2)));
			}
		}
		else
		{
			if(dayPath.size() != 0){
			String pickupPoint = (dayPath.get(0)).substring((dayPath.get(0)).lastIndexOf(" ")+1);
				bins = getBins.getBinCountByPickupPoint(wholeBook, Integer.parseInt(pickupPoint));
				bType = getBins.getBuildingType(wholeBook, Integer.parseInt(pickupPoint));
				cBuildNear = getBins.getClassBuildingsNear(wholeBook, Integer.parseInt(pickupPoint));
				fBuildNear = getBins.getFoodBuildingsNear(wholeBook, Integer.parseInt(pickupPoint));
				Timing.getPenalty(dayDistances.get(0), bins, permanentRoute, dayOfWeek, bType, cBuildNear, fBuildNear);
			}
		}
		LinkedLists.closeBook();
	}

	/**
	 * Finds the shortest possible route using a greedy search technique. Stores this route in the final
	 * path linked list.
	 *
	 * @param permanentRoute
	 * @param dayOfWeek
	 */
	private void routeAlgorithm(boolean permanentRoute, int dayOfWeek)
	{
		boolean firstBreak = false, secondBreak = false, lunchBreak = false;
		loadPickupDistances("AI/ShortestPathFiles/Point No 01.txt");
		loadPickupPaths("AI/ShortestPathFiles/Point No 01.txt");
		parseIntoList();
		getTimes(permanentRoute, dayOfWeek);
		sortMixed();
		getTimes(true, dayOfWeek);
		totalDistance += dayDistances.get(0);


		
		totalTime += times.get(0);
		endPoint = (dayPath.get(0)).substring((dayPath.get(0)).lastIndexOf(" ")+1);
		currStart = endPoint;
		removePickupPoint(endPoint);
		addToFinal();
		prevStart.addLast("1");
		pickupPaths.clear();
		distances.clear();
		dayPath.clear();
		times.clear();
		bins.clear();
		binServiceTime.clear();
		penalty.clear();
		startTime.clear();
		stopTime.clear();
		dayDistances.clear();

		int iteration = 0;

		while(true)
		{
			try {
				if(iteration != 0)
				{
				currStart = endPoint;
				}
				loadPickupDistances("AI/ShortestPathFiles/Point No " + endPoint + ".txt");
				loadPickupPaths("AI/ShortestPathFiles/Point No " + endPoint + ".txt");
				parseIntoList();
				getTimes(permanentRoute, dayOfWeek);
				sortMixed();
				getTimes(true, dayOfWeek);
				totalDistance += dayDistances.get(0);
				totalTime += times.get(0);
				if(totalTime > 120 && !firstBreak){
					totalTime += 15;
					firstBreak = true;
				}
				if(totalTime > 240 && !lunchBreak){
					totalTime += 30;
					lunchBreak = true;
				}
				if(totalTime > 360 && !secondBreak){
					totalTime += 15;
					secondBreak = true;
				}
				addToFinal();
				prevStart.addLast(endPoint);
				endPoint = (dayPath.get(0)).substring((dayPath.get(0)).lastIndexOf(" ")+1);
				removePickupPoint(endPoint);
				pickupPaths.clear();
				distances.clear();
				dayPath.clear();
				times.clear();
				bins.clear();
				binServiceTime.clear();
				penalty.clear();
				startTime.clear();
				stopTime.clear();
				dayDistances.clear();
				iteration++;
			} catch(Exception e) {
				pickupPaths.clear();
				dayDistances.clear();
				distances.clear();
				times.clear();
				bins.clear();
				binServiceTime.clear();
				penalty.clear();
				startTime.clear();
				stopTime.clear();
				dayPath.clear();
				break;
				}
		}
	}

	/**
	 * Runs the routing algorithm with every possible combination of paths. Returns the combinations
	 * in a linked list.
	 *
	 * @return the combinations of paths
	 */
	public LinkedList<String> comboAlgorithm(int days)
	{
		loadNames();
		maxDay = days;		//load the parameter from method call to a global variable

		for(int x = 0; x < ITERATIONS; x++) //not sure where iteration number came from
			{
				for(int j = 1; j < 3; j++) { //runs twice for the mwf and trs schedule

					int twoCounter = 3; 	 //variable to count the 2nd day uses as in x in the 5x2 path
					int threeCounter = 2;	 //variable to count the 3rd day uses as in x in the 53x path
					if(j == 2)
					{
						clearAllPaths(); //if gone through one time, clear any data from previous iteration
					}
					for(int i = 0; i < days; i++) //change loop to 5
					{
						if(days >= 5){
							loadPickups(days); //change this to 5
							removePickupPoint("01");
							if(i == 0)
							{//632 schedule
								loadPickups(twoCounter);		//load the path points
								loadPickups(threeCounter);
								loadPickups(1);
								routeAlgorithm(false, j);		//run the routing algorithm
								path = String.valueOf(days) + twoCounter + threeCounter + "1"; //632
								day = "" + j;
								if(x == 0)			//if the first iteration, save the path and path time immediately
								{
									saveBlock(sixThreeTwo, finalPath);
									bttSixThreeTwo = totalTime;
								}
								else //if it is not the first iteration, compare this path to the current fastest path
								{
									if(totalTime < bttSixThreeTwo)
									{
										bttSixThreeTwo = totalTime;
										saveBlock(sixThreeTwo, finalPath);
									}
								}
								finalPath.clear();			//clear all variables, lists, and strings so a new combination can be run
								totalDistance = 0;
								totalTime = 0;
								pickupPoints.clear();
								prevStart.clear();
								Timing.resetStartTime();
								twoCounter++;				//update 2nd digit counter
							}//end if
							else if(i == 1 || i == 2)
							{//642
								loadPickups(twoCounter);		//load path points and run algorithm
								loadPickups(threeCounter);
								routeAlgorithm(false, j);
								path = String.valueOf(days) + twoCounter + threeCounter;
								day = "" + j;
								if(x == 0)
								{
									if(i == 1) //642
									{
										saveBlock(sixFourTwo, finalPath);
										bttSixFourTwo = totalTime;
									}
									else //643
									{
										saveBlock(sixFourThree, finalPath);
										bttSixFourThree = totalTime;
									}
								}
								else
								{
									if(totalTime < bttSixFourTwo)//642
									{
										if(i == 1)
										{
											saveBlock(sixFourTwo, finalPath);
											bttSixFourTwo = totalTime;
										}
									}
									else if(totalTime < bttSixFourThree)//643
									{
										if(i == 2)
										{
											saveBlock(sixFourThree, finalPath);
											bttSixFourThree = totalTime;
										}
									}
								}
								finalPath.clear();
								totalDistance = 0;
								totalTime = 0;
								pickupPoints.clear();
								prevStart.clear();
								Timing.resetStartTime();
								threeCounter++;
								if(i == 2)
								{
									twoCounter++;
									threeCounter = 2;
								}
							} //end if
							else if(days > 5){
								loadPickups(twoCounter);
								loadPickups(threeCounter);
								routeAlgorithm(false, j);
								path = String.valueOf(days) + twoCounter + threeCounter;
								day = "" + j;
								if(x == 0)
								{
									if(i == 3) //652
									{
										saveBlock(sixFiveTwo, finalPath);
										bttSixFiveTwo = totalTime;
									}
									else if(i == 4) //653
									{
										saveBlock(sixFiveThree, finalPath);
										bttSixFiveThree = totalTime;
									}
									else if(i == 5) //654
									{
										saveBlock(sixFiveFour, finalPath);
										bttSixFiveFour = totalTime;
									}
								}//end if
								else
								{
									if(i == 3) //652
									{
										if(totalTime < bttSixFiveTwo)
										{
											saveBlock(sixFiveTwo, finalPath);
											bttSixFiveTwo = totalTime;
										}
									}
									else if(i == 4) //653
									{
										if(totalTime < bttSixFiveThree)
										{
											saveBlock(sixFiveThree, finalPath);
											bttSixFiveThree = totalTime;
										}
									}
									else if(i == 5) //654
									{
										if(totalTime < bttSixFiveFour)
										{
											saveBlock(sixFiveFour, finalPath);
											bttSixFiveFour = totalTime;
										}
									}
								}// end else
								finalPath.clear();
								totalDistance = 0;
								totalTime = 0;
								pickupPoints.clear();
								prevStart.clear();
								Timing.resetStartTime();
								threeCounter++;
							}
						}
					} //end for loop

					if(days == 5){ //54 path
						loadPickups(5);
						loadPickups(4);
						removePickupPoint("01");
						routeAlgorithm(false, j);
						path = "54";
						day = "" + j;
						if(x == 0){
							saveBlock(fiveFour, finalPath);
							bttFiveFour = totalTime;
						}
						else{
							if(totalTime < bttFiveFour){
								saveBlock(fiveFour, finalPath);
								bttFiveFour = totalTime;
							}
						}
						finalPath.clear();
						totalDistance = 0;
						totalTime = 0;
						pickupPoints.clear();
						prevStart.clear();
						Timing.resetStartTime();
					}

					twoCounter = 4;
					threeCounter = 3;
					int fourCounter = 2;

					if(days == 5 || days == 6){
						for(int i = 0; i < 4; i++)
						{
							loadPickups(days);
							removePickupPoint("01");

							if(i == 0)
							{
								loadPickups(twoCounter); //6432 and 5432
								loadPickups(threeCounter);
								loadPickups(fourCounter);
								routeAlgorithm(false, j);
								path = String.valueOf(days) + twoCounter + threeCounter + fourCounter;
								day = "" + j;
								if(x == 0)//6432
								{
									saveBlock(sixFourThreeTwo, finalPath);
									bttSixFourThreeTwo = totalTime;
								}
								else //6432
								{
									if(totalTime < bttSixFourThreeTwo)
									{
										saveBlock(sixFourThreeTwo, finalPath);
										bttSixFourThreeTwo = totalTime;
									}
								}
								finalPath.clear();
								totalDistance = 0;
								totalTime = 0;
								pickupPoints.clear();
								prevStart.clear();
								Timing.resetStartTime();
								twoCounter++;
							}
							if(days == 6) {
								loadPickups(days);
								removePickupPoint("01");
								loadPickups(twoCounter);
								loadPickups(threeCounter);
								loadPickups(fourCounter);
								routeAlgorithm(false, j);
								path = String.valueOf(days) + twoCounter + threeCounter + fourCounter;
								day = "" + j;
								if(x == 0)
								{
									if(i == 0) //6532
									// if(i == 1) //6532
									{
										saveBlock(sixFiveThreeTwo, finalPath);
										bttSixFiveThreeTwo = totalTime;
									}
									if(i == 1) //6532
//									if(i == 2) //6542
									{
										saveBlock(sixFiveFourTwo, finalPath);
										bttSixFiveFourTwo = totalTime;
									}
									if(i == 2) //6543
//									if(i == 3) //6543
									{
										saveBlock(sixFiveFourThree, finalPath);
										bttSixFiveFourThree = totalTime;
									}
								}
								else
								{
									if(i == 0) //6532
//									if(i == 1) //6532
									{
										if(totalTime < bttSixFiveThreeTwo)
										{
											saveBlock(sixFiveThreeTwo, finalPath);
											bttSixFiveThreeTwo = totalTime;
										}
									}
									if(i == 1) //6542
//									if(i == 2) //6542
									{
										if(totalTime < bttSixFiveFourTwo)
										{
											saveBlock(sixFiveFourTwo, finalPath);
											bttSixFiveFourTwo = totalTime;
										}
									}
									if(i == 2) //6543
//									if(i == 3) //6543
									{
										if(totalTime < bttSixFiveFourThree)
										{
											saveBlock(sixFiveFourThree, finalPath);
											bttSixFiveFourThree = totalTime;
										}
									}
								}
								finalPath.clear();
								totalDistance = 0;
								totalTime = 0;
								pickupPoints.clear();
								prevStart.clear();
								Timing.resetStartTime();
								if(i == 0)
								{
									threeCounter++;
								}
								else if(threeCounter == fourCounter){
									fourCounter++;
								}
								else{
									fourCounter++;
								}

								if(i == 3){
									fourCounter = fourCounter;
								}
							}
						}
					}
					if(days == 4){		//for 4 day route schedule
						for(int t = 0; t < 3; t++){
							loadPickups(4);
							removePickupPoint("01");
							if(t == 0){
								loadPickups(3);			//4321 path
								loadPickups(2);
								loadPickups(1);
								routeAlgorithm(false, j);
								path = "4321";
								day = "" + j;
								if(x == 0){
									saveBlock(fourThreeTwo, finalPath);
									bttFourThreeTwo = totalTime;
								}
								else{
									if(totalTime < bttFourThreeTwo){
										saveBlock(fourThreeTwo, finalPath);
										bttFourThreeTwo = totalTime;
									}
								}
							}
							else{
								if(t == 1){
									loadPickups(3);			//43 path
									routeAlgorithm(false, j);
									path = "43";
									day = "" + j;
									if(x == 0){
										saveBlock(fourThree, finalPath);
										bttFourThree = totalTime;
									}
									else{
										if(totalTime < bttFourThree){
											saveBlock(fourThree, finalPath);
											bttFourThree = totalTime;
										}
									}
								}
								if(t == 2){
									loadPickups(2);			//42 path
									routeAlgorithm(false, j);
									path = "42";
									day = "" + j;
									if(x == 0){
										saveBlock(fourTwo, finalPath);
										bttFourTwo = totalTime;
									}
									else{
										if(totalTime < bttFourTwo){
											saveBlock(fourTwo, finalPath);
											bttFourTwo = totalTime;
										}
									}
								}
							}
							finalPath.clear();			//clear lists and variables to run another combo
							totalDistance = 0;
							totalTime = 0;
							pickupPoints.clear();
							prevStart.clear();
							Timing.resetStartTime();
						}
					}
					if(days == 3){			//3 day path schedule
						for(int t = 0; t < 2; t++){
							loadPickups(3);			//3 path
							removePickupPoint("01");
							if(t == 0){
								routeAlgorithm(false, j);
								path = "3";
								day = "" + j;
								if(x == 0){
									saveBlock(three, finalPath);
									bttThree = totalTime;
								}
								else{
									if(totalTime < bttThree){
										saveBlock(three, finalPath);
										bttThree = totalTime;
									}
								}
							}
							else if(t == 1){
								loadPickups(2);			//321 path
								loadPickups(1);
								routeAlgorithm(false, j);
								path = "321";
								day = "" + j;
								if(x == 0){
									saveBlock(threeTwo, finalPath);
									bttThreeTwo = totalTime;
								}
								else{
									if(totalTime < bttThreeTwo){
										saveBlock(threeTwo, finalPath);
										bttThreeTwo = totalTime;
									}
								}
							}
							finalPath.clear();
							totalDistance = 0;
							totalTime = 0;
							pickupPoints.clear();
							prevStart.clear();
							Timing.resetStartTime();
						}
					}
					if(days == 2){			//2 day path schedule
						for(int t = 0; t < 2; t++){
							loadPickups(2);			//2 path
							removePickupPoint("01");
							if(t == 0){
								routeAlgorithm(false, j);
								path = "2";
								day = "" + j;
								if(x == 0){
									saveBlock(two, finalPath);
									bttTwo = totalTime;
								}
								else{
									if(totalTime < bttTwo){
										saveBlock(two, finalPath);
										bttTwo = totalTime;
									}
								}
							}
							else if(t == 1){
								loadPickups(1);			//21 path
								routeAlgorithm(false, j);
								path = "21";
								day = "" + j;
								if(x == 0){
									saveBlock(twoOne, finalPath);
									bttTwoOne = totalTime;
								}
								else{
									if(totalTime < bttTwoOne){
										saveBlock(twoOne, finalPath);
										bttTwoOne = totalTime;
									}
								}
							}
							finalPath.clear();
							totalDistance = 0;
							totalTime = 0;
							pickupPoints.clear();
							prevStart.clear();
							Timing.resetStartTime();
						}
					}

					if(days == 1){ 			//1 day path schedule
						loadPickups(1);
						removePickupPoint("01");
						routeAlgorithm(false, j);
						path = "1";
						day = "" + j;
						if(x == 0){
							saveBlock(one, finalPath);
							bttOne = totalTime;
						}
						else{
							if(totalTime < bttOne){
								saveBlock(twoOne, finalPath);
								bttOne = totalTime;
							}
						}
						finalPath.clear();
						totalDistance = 0;
						totalTime = 0;
						pickupPoints.clear();
						prevStart.clear();
						Timing.resetStartTime();
					}
					//copies all lists to a final list to be returned to the user
					copyAllToReturn();
				}
			}

		return returnPath;
	}

	/**
	 * Print out a linked list.
	 *
	 * @param list
	 */
	public void printPath(LinkedList list)
	{
		String[] dayNames = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
		int row = 0;
		int col = 0;
		int pointCnt = 0;

		String fileName = "output/FinalOutput.xls";
		try {
			WritableWorkbook workbook = Workbook.createWorkbook(new File(fileName));
			WritableSheet sheet = workbook.createSheet("FinalOutput", 0);

			for(int i = 0; i < list.size(); i++){
				String daySched = list.get(i).toString();
				String[] expandedSched = daySched.split("\n");
				for(int j = 0; j < expandedSched.length; j++) {
					String inputStr = null;
					col = 0;
					if(j == 0){
						Label label = new Label(col, row, dayNames[i]);
						sheet.addCell(label);
					}
					else if(j == 1){
						inputStr = expandedSched[j];
						Label label = new Label(col, row, inputStr);
						sheet.addCell(label);
					}
					else if(j == expandedSched.length - 2 || j == expandedSched.length - 1){
						inputStr = expandedSched[j];
						String[] schedLine = expandedSched[j].split(":");
						if(j == expandedSched.length - 2){
							Label dist = new Label(col, row, "Total Distance");
							sheet.addCell(dist);
							col++;

							Label label = new Label(col, row, inputStr);
							sheet.addCell(label);
						}
						if(j == expandedSched.length - 1){
							Label time = new Label(col, row, "Total Time");
							sheet.addCell(time);
							col++;

							Label label = new Label(col, row, inputStr);
							sheet.addCell(label);
						}

					}
					else{
						inputStr = expandedSched[j];
						String[] schedLine = expandedSched[j].split(":");
						for(int x = 0; x < schedLine.length; x++){
							Label label = new Label(col, row, schedLine[x]);
							sheet.addCell(label);
							col++;
						}
						pointCnt++;
					}
					row++;
				}
				pointCnt++;
				row++;
			}

			workbook.write();
			workbook.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}

	}

	/**
	 * This method saves a particular path scenario to a list, which includes the day,
	 * path name, length, and total time.
	 *
	 * @param saveList
	 * @param list
	 */
	private void saveBlock(LinkedList<String> saveList, LinkedList<String> list)
	{
		saveList.clear();
		saveList.add(day);
		saveList.add(path);
		for(int i = 0; i < list.size(); i++)
		{
			saveList.add(list.get(i));
		}
//		saveList.add("Total Distance: " + String.valueOf((double)Math.round(totalDistance * 10000) / 10000) + " miles");
//		saveList.add("Total Time: " + String.valueOf((double)Math.round(totalTime * 10000) / 10000) + " minutes");
		saveList.add(String.valueOf((double)Math.round(totalDistance * 10000) / 10000));
		saveList.add(String.valueOf((double)Math.round(totalTime * 10000) / 10000));
		saveList.add("END_OF_PATH");
	}

	//this method clears all lists and timing variables
	private void clearAllPaths()
	{
		sixThreeTwo.clear();
		sixFourTwo.clear();
		sixFourThree.clear();
		sixFiveTwo.clear();
		sixFiveThree.clear();
		sixFiveFour.clear();
		sixFiveThreeTwo.clear();
		sixFourThreeTwo.clear();
		sixFiveFourTwo.clear();
		sixFiveFourThree.clear();

		bttSixThreeTwo = 0;
		bttSixFourTwo = 0;
		bttSixFiveTwo = 0;
		bttSixFourThree = 0;
		bttSixFiveThree = 0;
		bttSixFiveFour = 0;
		bttSixFiveFourTwo = 0;
		bttSixFiveThreeTwo = 0;
		bttSixFourThreeTwo = 0;
		bttSixFiveFourThree = 0;
	}

	private void copyAllToReturn()
	{
		//copies all possible paths to the final return list
		copyToReturn(sixThreeTwo);
		copyToReturn(sixFourTwo);
		copyToReturn(sixFourThree);
		copyToReturn(sixFiveTwo);
		copyToReturn(sixFiveThree);
		copyToReturn(sixFiveFour);
		copyToReturn(sixFiveThreeTwo);
		copyToReturn(sixFourThreeTwo);
		copyToReturn(sixFiveFourTwo);
		copyToReturn(sixFiveFourThree);
		copyToReturn(fiveFour);
		copyToReturn(fourThreeTwo);
		copyToReturn(fourThree);
		copyToReturn(fourTwo);
		copyToReturn(three);
		copyToReturn(threeTwo);
		copyToReturn(two);
		copyToReturn(twoOne);
		copyToReturn(one);
	}

	/**
	 * Copy a linked list into the return path linked list.
	 *
	 * @param list
	 */
	private void copyToReturn(LinkedList<String> list)
	{
		for(int i = 0; i < list.size(); i++)
		{
			returnPath.add(list.get(i));
		}
	}

	/**
	 * This method takes each start point and end point for a particular part
	 * of a path and adds it to a final list that will contain all points on that path
	 *
	 *
	 */
	private void addToFinal()
	{
		LinkedLists getName = new LinkedLists();

		//get the first point which is to be traveled from
		String path = dayPath.get(0);
		String firstPoint = path.substring(0, path.indexOf(":"));
		firstPoint = firstPoint.substring(9).trim();

		for(int i = 0; i < pickupPointNames.size(); i++)
		{
			if(indexedPoints.get(i).equals(firstPoint))
			{
				firstPoint = pickupPointNames.get(i);
			}
		}

		//get the second point to travel to
		String secondPoint = path.substring(path.indexOf(":"));
		secondPoint = secondPoint.substring(10);

		for(int i = 0; i < pickupPointNames.size(); i++)
		{
			if(indexedPoints.get(i).equals(secondPoint))
			{
				secondPoint = pickupPointNames.get(i);
			}
		}

		//add the string to the finalPath list which contains all other points and paths
		finalPath.addLast(firstPoint + ":" + secondPoint + ":" + (double)Math.round(dayDistances.get(0) * 10000) / 10000
				+ ":" + (double)Math.round(((dayDistances.get(0) / Timing.getTruckSpeed()) * 60) * 10000) / 10000
				+ ":" + (double)Math.round(times.get(0) * 10000) / 10000 + ":" + bins.get(0) + ":"
				+ binServiceTime.get(0) + ":" + (stopTime.get(0) + dayDistances.get(0) / Timing.getTruckSpeed())
				+ ":" + startTime.get(0) + ":" + penalty.get(0));
	}

	/**
	 * Created by Aaron Rockburn 1/9/15
	 *
	 * Pass in a list of pickup points that will be run using the routeAlgorithm in this class
	 * @param daySchedule
	 */
	public LinkedList runRouteAlgorithm(LinkedList daySchedule, int dayNum){
		pickupPoints.clear();
		finalPath.clear();			//clear all variables, lists, and strings so a new combination can be run
		totalDistance = 0;
		totalTime = 0;
		pickupPoints.clear();
		prevStart.clear();

		for(int i = 0; i < daySchedule.size(); i++){
			pickupPoints.add(String.valueOf(daySchedule.get(i)));
		}
		routeAlgorithm(false, dayNum);

		return finalPath;
	}

	/**
	 * Get the linked list that contains the information pulled from excel file
	 * @return wholeBook
	 */
	public LinkedList getWholeBook(){
		return this.wholeBook;
	}
}
