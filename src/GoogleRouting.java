/**
 * Was going to be used to do what TextFileRouting.java does, but was replaced by the aforementioned 
 * class before I got this class working. So, it does not fully work. It was going to get all the 
 * combinations of paths, but this took an extremely long time using google maps.
 * 
 * @author Sean M Brown
 * Version 1.0
 * Revision Date: March 2013
 */
import java.util.*;

public class GoogleRouting {

	//All of the linked lists that are to be used below, as well as the start spot
	private LinkedList<Double> distances = new LinkedList<Double>();
	private LinkedList<String> endPlaces = new LinkedList<String>();
	private LinkedList<Double> sortedDistances = new LinkedList<Double>();
	private LinkedList<String> sortedEndPlaces = new LinkedList<String>();
	private static LinkedList<String> finalPath = new LinkedList<String>();
	private String startPlace = "";
	private static double total = 0;
	
	/**
	 * Sorts a linked list from smallest to largest using an insertion sort, because a quick sort
	 * was too much work for me.
	 * 
	 * @param list
	 */
	private void sortDistances(LinkedList<Double> list) {
	    int i, j;
	    double currentValue;
	    String currentEndPlace;
	    
	    for(int k = 0; k < endPlaces.size(); k++) {
	    	sortedEndPlaces.addLast(endPlaces.get(k));
	    }
	    
	    for(int l = 0; l < list.size(); l++) {
	    	sortedDistances.addLast(list.get(l));
	    }
	    
	    for (i = 1; i < sortedDistances.size(); i++) {
	          currentValue = sortedDistances.get(i);
	          currentEndPlace = sortedEndPlaces.get(i);
	          j = i;
	          while (j > 0 && list.get(j - 1) > currentValue) {
	                sortedDistances.set(j, sortedDistances.get(j - 1));
	                sortedEndPlaces.set(j, sortedEndPlaces.get(j-1));
	                j--;
	          }
	          sortedEndPlaces.set(j, currentEndPlace);
	          sortedDistances.set(j, currentValue);
	    }
	}
	
	private void plotTwoPoints(String start, String end) 
	{
		GoogleMaps.startMap();
		
		GoogleMaps.initialEndSpot(end);
		GoogleMaps.enterStartSpot(start);
		GoogleMaps.clickDirections();
		
		distances.addLast(GoogleMaps.getDistance());
	}
	
	/**
	 * Plots all the latitude and longitude values of a linked list into Google Maps, starting at
	 * the specified location and saves their distances into a linked list
	 * 
	 * @param list
	 * @param startSpotRow
	 */
	private void plotPath(LinkedList<Object> list, int startSpotRow) 
	{
		//the start lat/long of the specified row, the name of the start place
		LinkedLists lists = new LinkedLists();
		String start = lists.getLatLongByRow(list, startSpotRow);
		startPlace = lists.getNameByRow(list, startSpotRow);
		String end = "";
		
		//the size of the linked list divided by 12 (the amount of columns) so this makes it
		//how many rows are in the list
		int size = list.size()/12;
		
		//start FireFox to Google Maps
		GoogleMaps.startMap();
		
		//Go through each row, and put it into Google Maps
		for(int i = 0; i < size; i++) 
		{
			try {
				//sets the end value to the place in the first row
				end = lists.getLatLongByRow(list, i);
			
				if(start.equals(end)) 
				{
					//if the start place and end place are the same, skip this spot
					end = lists.getLatLongByRow(list, i + 1);
					
					if(i != 0) 
					{
						//if it is not the first place you are skipping, then you need to increment
						//the counter. Not sure why not every time but it works ;)
						i+=1;
					}
				}
				
				//the first time through you have to do it this way
				if(i == 0) 
				{
					//enter the initial end spot, then the start spot, then get directions
					GoogleMaps.initialEndSpot(end);
					GoogleMaps.enterStartSpot(start);
					GoogleMaps.clickDirections();
				}
				else 
				{
					//since its not the first time you don't need to enter the initial end spot
					//just enter in the end spot, the start spot is already entered, and get directions
					GoogleMaps.enterEndSpot(end);
					GoogleMaps.clickDirections();
				}	
			} catch (Exception e) {/*some shit happened that made me mad so i put this here (bad coding)*/}
			
			//add the end place you just entered into a linked list, as well as its distance from the start
			endPlaces.addLast(lists.getNameByRow(list, i));
			distances.addLast(GoogleMaps.getDistance());
		}
	}
	
	/**
	 * Gets the shortest route from all a linked lists points to each other.
	 * 
	 * @param list
	 */
	private void pickPath(LinkedList<Object> list)
	{
		LinkedLists lists = new LinkedLists();
		String initialStart = lists.getLatLongByName(list, list.get(0).toString());
		
		//the first time through the sorted end places are going to be blank so you can't put this
		//inside the loop. Start off by plotting the path from the list, starting with the first spot
		plotPath(list, 0);
		
		//then sort all the distances that were calculated from the plotted paths
		sortDistances(distances);
		
		//take the shortest path (first value in sortedDistances) and its corresponding start and end
		//places and add them to the final path
		finalPath.addLast(startPlace + " to " + 
		sortedEndPlaces.getFirst() + ": " + sortedDistances.getFirst());
		total += sortedDistances.getFirst();
		
		//remove the start point you just used from the list, so when you plot the route again it does
		//not get used again, then clear out the lists you just used so they can be used again
		lists.removeRowByName(list, startPlace);
		distances.clear();
		sortedDistances.clear();
		endPlaces.clear();
		
		
		//exact same code as before, except every time you are going to start with the first value of
		//the sorted end places, which is the place you went to last time. When I sort the distances I
		//also sort the end places, so the end place is matched up with its distance. Oh and I have no
		//damn clue why the iteration of the list size-8 times works but it does, lol more bad code.
		for(int i = 0; i < list.size() - 8; i++) {
			plotPath(list, lists.getRowByName(list, sortedEndPlaces.getFirst()));
			sortedEndPlaces.clear();
			sortDistances(distances);
			finalPath.addLast(startPlace + " to " +
					sortedEndPlaces.getFirst() + ": " + sortedDistances.getFirst());
			total += sortedDistances.getFirst();
			lists.removeRowByName(list, startPlace);
			distances.clear();
			sortedDistances.clear();
			endPlaces.clear();
		}
		
		plotTwoPoints(lists.getLatLongByName(list, sortedEndPlaces.getFirst()), initialStart);
		finalPath.addLast(sortedEndPlaces.getFirst() + " to start spot" + ": " + distances.getLast());
		total += distances.getLast();
		
	}
	
	public void plotComboRoutes() 
	{
		LinkedLists lists = new LinkedLists();
		LinkedList<Object> sixDayList = new LinkedList<Object>();
		sixDayList = lists.loadSpecificList(sixDayList, 6);
		LinkedList<Object> fiveDayList = new LinkedList<Object>();
		fiveDayList = lists.loadSpecificList(fiveDayList, 5);
		LinkedList<Object> fourDayList = new LinkedList<Object>();
		fourDayList = lists.loadSpecificList(fourDayList, 4);
		LinkedList<Object> threeDayList = new LinkedList<Object>();
		threeDayList = lists.loadSpecificList(threeDayList, 3);
		LinkedList<Object> twoDayList = new LinkedList<Object>();
		twoDayList = lists.loadSpecificList(twoDayList, 2);
		LinkedList<Object> comboDistances = new LinkedList<Object>();
		
		double sixFiveFourDist = 0;
		double sixFiveThreeDist = 0;
		double sixFiveTwoDist = 0;
		double sixFourThreeDist = 0;
		double sixFourTwoDist = 0;
		double sixThreeTwoDist = 0;
		
		int counter = 0;
		for(int i = 0; i < 6; i++) 
		{
			total = 0;
			LinkedList<Object> comboList = sixDayList;
			
			if(counter < 3) 
			{
				counter++;
				
				for(int j = 0; j < fiveDayList.size(); j++) 
				{
					comboList.addLast(fiveDayList.get(j));
				}
				
				if(i == 0) 
				{
					for(int k = 0; k < fourDayList.size(); k++) 
					{
						comboList.addLast(fourDayList.get(k));
					}
					findShortestRoute(comboList);
					sixFiveFourDist = total;
					comboDistances.addLast(sixFiveFourDist);
				}
				else if(i == 1) 
				{
					for(int k = 0; k < threeDayList.size(); k++) 
					{
						comboList.addLast(threeDayList.get(k));
					}
					findShortestRoute(comboList);
					sixFiveThreeDist = total;
					comboDistances.addLast(sixFiveThreeDist);
				}
				else if(i == 2) 
				{
					for(int k = 0; k < twoDayList.size(); k++) 
					{
						comboList.addLast(twoDayList.get(k));
					}
					findShortestRoute(comboList);
					sixFiveTwoDist = total;
					comboDistances.addLast(sixFiveTwoDist);
				}
			}
			else if (counter >= 3 && counter < 6) 
			{
				counter++;
				
				for(int a = 0; a < fourDayList.size(); a++) 
				{
					comboList.addLast(fourDayList.get(a));
				}
				
				if(i == 3) 
				{
					for(int b = 0; b < threeDayList.size(); b++)  
					{
						comboList.addLast(threeDayList.get(b));
					}
					findShortestRoute(comboList);
					sixFourThreeDist = total;
					comboDistances.addLast(sixFourThreeDist);
				}
				else if(i == 4) 
				{
					for(int b = 0; b < twoDayList.size(); b++)
					{
						comboList.addLast(twoDayList.get(b));
					}
					findShortestRoute(comboList);
					sixFourTwoDist = total;
					comboDistances.addLast(sixFourTwoDist);
				}
			}
			else if(counter == 5) 
			{
				for(int c = 0; c < threeDayList.size(); c++) 
				{
					comboList.addLast(threeDayList.get(c));
				}
				
				if(i == 5) 
				{
					for(int d = 0; d < twoDayList.size(); d++) 
					{
						comboList.addLast(twoDayList.get(d));
					}
					findShortestRoute(comboList);
					sixThreeTwoDist = total;
					comboDistances.addLast(sixThreeTwoDist);
				}
			}
		}
		for(int i = 0; i < comboDistances.size(); i++) {
			System.out.println(comboDistances.get(i));
		}
		
	}
	
	/**
	 * Prints out the final path linked lists that shows the path of the trash truck
	 */
	private void printFinalPath() 
	{
		for(int i = 0; i < finalPath.size(); i++) {
			System.out.println(finalPath.get(i));
		}
		System.out.println("Total Distance: " + total);
	}
	
	/**
	 * Finds the shortest route of points in a linked list with defined amount of pickup days
	 * @param pickups
	 */
	public void findShortestRoute(int pickups) 
	{
		LinkedLists listLoader = new LinkedLists();
		GoogleRouting plot = new GoogleRouting();
		LinkedList<Object> list = new LinkedList<Object>();
		list = listLoader.loadSpecificList(list, pickups);
		plot.pickPath(list);
		printFinalPath();
		System.out.println();
	}
	
	/**
	 * Finds the shortest route between points in a linked list
	 * @param list
	 */
	public void findShortestRoute(LinkedList<Object> list) 
	{
		GoogleRouting plot = new GoogleRouting();
		plot.pickPath(list);
		printFinalPath();
	}
	
	
	/**
	 * RUN THE Program!!!!!!
	 * 
	 * @param args
	 */
	public static void main(String args[]) 
	{
		GoogleRouting router = new GoogleRouting();
		router.plotComboRoutes();
	}
}
