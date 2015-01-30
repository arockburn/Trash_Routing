/***
 * Puts together all the possible combinations of paths to be taken that can be used in the schedule.
 * Gets the shortest path for each segment and gets the combination based on time.
 * 
 * @author Sean M Brown
 * Last Edited: 4/25/13
 * Version 1.0
 */

import java.util.*;

public class TheirTextRouting 
{
	private static final double ITERATIONS = 550;
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
	private LinkedList<Integer> indexes = new LinkedList<Integer>();
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
	private double totalDistance = 0;
	private double totalTime = 0;
	private LinkedList<String> prevStart = new LinkedList<String>();
	private String path = "";
	private String day = "";
	private String currStart = "";
	private String endPoint = "";
	
	/**
	 * Loads all the pickup points into the pickupPoints array.
	 * 
	 * @param pickupDays
	 */
	private void loadPickups(int pickupDays) 
	{
		LinkedLists listLoad = new LinkedLists();
		LinkedList<Object> list = new LinkedList<Object>();
		list = listLoad.loadSpecificList(list, pickupDays);
		int columns = LinkedLists.getColumns();
		
		for(int i = 0; i < list.size()/columns; i++) 
		{
			 pickupPoints.addLast(LinkedLists.getPickupPointByRow(list, i));
		}
	}
	
	private void loadNames()
	{
		LinkedLists list = new LinkedLists();
		
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
		list.closeBook();
	}
	
	private void loadIndexes(int pickupDays)
	{
		LinkedLists listLoad = new LinkedLists();
		LinkedList<Object> list = new LinkedList<Object>();
		list = listLoad.loadSpecificList(list, pickupDays);
		indexes = listLoad.getIndexes(list, indexes);
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
			int randWeight = 0 + (int)(Math.random() * ((100 - 0) + 1));
			
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
					penalty.set(j, penalty.get(j-1));
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
					penalty.set(j, penalty.get(j-1));
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
				penalty.set(j, penalty.get(j-1));
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
	
	private void pickByIndex(int i)
	{
		for(int j = 0; j < indexes.size(); j++)
		{
			int currIndex = indexes.get(0);
			double currVal = times.get(0);
			double currDist = dayDistances.get(0);
			int currBins = bins.get(0);
			double currBinService = binServiceTime.get(0);
			double currPenalty = penalty.get(0);
			double currStop = stopTime.get(0);
			double currStart = startTime.get(0);
			String currPickup = pickupPoints.get(0);
			String currDay = dayPath.get(0);
			
			if(indexes.get(j) == i)
			{
				indexes.set(0, indexes.get(j));
				times.set(0, times.get(j));
				dayDistances.set(0, dayDistances.get(j));
				bins.set(0, bins.get(j));
				binServiceTime.set(0, binServiceTime.get(j));
				stopTime.set(0, stopTime.get(j));
				startTime.set(0, startTime.get(j));
				penalty.set(0, penalty.get(j));
				dayPath.set(0, dayPath.get(j));
				pickupPoints.set(0, pickupPoints.get(j));

				indexes.set(j, currIndex);
				times.set(j, currVal);
				dayDistances.set(j, currDist);
				bins.set(j, currBins);
				binServiceTime.set(j, currBinService);
				stopTime.set(j, currStop);
				startTime.set(j, currStart);
				penalty.set(j, currPenalty);
				dayPath.set(j, currDay);
				pickupPoints.set(j, currPickup);
			}
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
				this.bins.addLast(bins);
				bType = getBins.getBuildingType(wholeBook, Integer.parseInt(pickupPoints.get(i)));
				cBuildNear = getBins.getClassBuildingsNear(wholeBook, Integer.parseInt(pickupPoints.get(i)));
				fBuildNear = getBins.getFoodBuildingsNear(wholeBook, Integer.parseInt(pickupPoints.get(i)));
				LinkedList<String> timeInfo = Timing.getPenalty(dayDistances.get(i), bins, permanentRoute, dayOfWeek, bType, cBuildNear, fBuildNear);
				binServiceTime.addLast(Double.parseDouble(timeInfo.get(0)));
				penalty.addLast(Double.parseDouble(timeInfo.get(1)));
				stopTime.addLast(Double.parseDouble(timeInfo.get(3)));
				startTime.addLast(Double.parseDouble(timeInfo.get(4)));
				times.addLast(Double.parseDouble(timeInfo.get(2)));
			}
		}
		else 
		{
			String pickupPoint = (dayPath.get(0)).substring((dayPath.get(0)).lastIndexOf(" ")+1);
			bins = getBins.getBinCountByPickupPoint(wholeBook, Integer.parseInt(pickupPoint));
			bType = getBins.getBuildingType(wholeBook, Integer.parseInt(pickupPoint));
			cBuildNear = getBins.getClassBuildingsNear(wholeBook, Integer.parseInt(pickupPoint));
			fBuildNear = getBins.getFoodBuildingsNear(wholeBook, Integer.parseInt(pickupPoint));
			Timing.getPenalty(dayDistances.get(0), bins, permanentRoute, dayOfWeek, bType, cBuildNear, fBuildNear);
		}
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
		loadPickupDistances("AI/ShortestPathFiles/Point No 01.txt");
		loadPickupPaths("AI/ShortestPathFiles/Point No 01.txt");
		parseIntoList();
		getTimes(permanentRoute, dayOfWeek);
		sortMixed();
		getTimes(true, dayOfWeek);
		totalDistance += dayDistances.get(0);
		totalTime += times.get(0) + 90;
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
	public LinkedList<String> comboAlgorithm() 
	{
		for(int i = 0; i < ITERATIONS; i++) 
		{
		loadPickups(6);
		loadPickups(4);
		loadPickups(3);
		removePickupPoint("01");
		routeAlgorithm(false, 0);
		day = "Saturday";
		path = "643";
		if(i == 0)
		{
			saveBlock(sixThreeTwo, finalPath);
			bttSixThreeTwo = totalTime;
		}
		else 
		{
			if(totalTime < bttSixThreeTwo)
			{
				bttSixThreeTwo = totalTime;
				saveBlock(sixThreeTwo, finalPath);
			}
		}
		finalPath.clear();
		totalDistance = 0;
		totalTime = 0;
		pickupPoints.clear();
		prevStart.clear();
		Timing.resetStartTime();
		}
		copyToReturn(sixThreeTwo);
		return returnPath;
	}
	
	/**
	 * Print out a linked list.
	 * 
	 * @param list
	 */
	public void printPath(LinkedList list) 
	{
		for(int i = 0; i < list.size(); i++) 
		{
			System.out.println(list.get(i));
		}
	}
	
	private void saveBlock(LinkedList<String> saveList, LinkedList<String> list)
	{
		saveList.clear();
		saveList.add(day);
		saveList.add(path);
		for(int i = 0; i < list.size(); i++)
		{
			saveList.add(list.get(i));
		}
		saveList.add("Total Distance: " + String.valueOf((double)Math.round(totalDistance * 10000) / 10000) + " miles");
		saveList.add("Total Time: " + String.valueOf((double)Math.round(totalTime * 10000) / 10000) + " minutes");
		saveList.add("END_OF_PATH");
	}
	
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
		returnPath.add("Total Time: " + totalTime + " minutes");
		returnPath.add("Total Distance: " + totalDistance + " miles");
	}
	
	private void addToFinal()
	{
		LinkedLists getName = new LinkedLists();
		
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
		
		String secondPoint = path.substring(path.indexOf(":"));
		secondPoint = secondPoint.substring(10);
		
		for(int i = 0; i < pickupPointNames.size(); i++)
		{
			if(indexedPoints.get(i).equals(secondPoint))
			{
				secondPoint = pickupPointNames.get(i);
			}
		}
		
		
		finalPath.addLast(firstPoint + ":" + secondPoint + ":" + (double)Math.round(dayDistances.get(0) * 10000) / 10000 
				+ ":" + (double)Math.round(((dayDistances.get(0) / Timing.getTruckSpeed()) * 60) * 10000) / 10000 
				+ ":" + (double)Math.round(times.get(0) * 10000) / 10000 + ":" + bins.get(0) + ":"
				+ binServiceTime.get(0) + ":" + (stopTime.get(0) + dayDistances.get(0) / Timing.getTruckSpeed()) 
				+ ":" + startTime.get(0) + ":" + penalty.get(0));	
	}
	
	public static void main(String[] args)
	{
		TheirTextRouting run = new TheirTextRouting();
		run.loadNames();
		LinkedList<String> path = run.comboAlgorithm();
		run.printPath(path);
	}
}
