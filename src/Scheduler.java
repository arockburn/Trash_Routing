/***
 * Puts together a 6 day pickup schedule of trash for Slippery Rock University pickup points.
 * 
 * @author Sean M Brown
 * @author Aaron Rockburn made some changes to include a schedule for any day of the week
 * verison 1.1
 * Last Revision: 9/5/14
 */

import java.util.*;

public class Scheduler 
{
	private LinkedList<String> finalSchedule = new LinkedList<String>();
	private LinkedList<String> combinations = new LinkedList<String>();
	private LinkedList<Double> distance = new LinkedList<Double>();
	private LinkedList<Double> time = new LinkedList<Double>();
	private LinkedList<String> day = new LinkedList<String>();
	private LinkedList<String> path = new LinkedList<String>();
	private LinkedList<String> exhaustedDays = new LinkedList<String>();
	private LinkedList<String> badPick = new LinkedList<String>();
	LinkedList<String> lastUsedPath = new LinkedList<String>();
	LinkedList<String> lastUsedDay = new LinkedList<String>();
	private int oneUsedCount = 0;
	private int twoUsedCount = 0;	//these variables count how many times each day has been used 
	private int threeUsedCount = 0;
	private int fourUsedCount = 0;
	private int fiveUsedCount = 0;
	private static int days = 6; 	//this variable controls what schedule is printed when the program runs
	
	/**
	 * Loads the combinations linked list with all 14 possible combinations that will be used to
	 * build a schedule. Each combination is a daily path.
	 */
	private void loadCombinations(int days)
	{
		//loads all combinations into a linked list
		TextFileRouting combo = new TextFileRouting();
		
		combinations = combo.comboAlgorithm(days);
	}
	
	/**
	 * Loads the day linked list with all of the "days". The days are either 1 or 2. 1 signifies a MWF
	 * path with MWF assessed penalties, and a 2 signifies a TRS day.
	 */
	private void getDay(LinkedList<String> list)
	{
		day.add(list.get(0));
		
		for(int i = 0; i < list.size(); i++)
		{
			if(list.get(i) == "END_OF_PATH")
			{
				try 
				{
					day.add(list.get(i+1));
				}
				catch(Exception e){}
			}
		}
	}
	
	/**
	 * Loads all of the path combinations into  the path linked list. Not the entire path, just the 
	 * title of the path, for example: 6432 would be a path.
	 */
	private void getPath(LinkedList<String> list)
	{
		path.add(list.get(1));
		
		for(int i = 0; i < list.size(); i++)
		{
			if(list.get(i) == "END_OF_PATH")
			{
				try 
				{
					path.add(list.get(i+2));
				}
				catch(Exception e){}
			}
		}
	}
	
	/**
	 * Loads all the times that each path took into the time linked list.
	 */
	private void getTimes(LinkedList<String> list)
	{
		for(int i = 0; i < list.size(); i++)
		{
			if(list.get(i) == "END_OF_PATH")
			{
				try 
				{
					time.add(Double.parseDouble(list.get(i-1)));
				}
				catch(Exception e){}
			}
		}
	}
	
	/**
	 * Loads all the distances that the paths took into the distance linked list.
	 */
	private void getDistances(LinkedList<String> list)
	{
		for(int i = 0; i < list.size(); i++)
		{
			if(list.get(i) == "END_OF_PATH")
			{
				try 
				{
					distance.add(Double.parseDouble(list.get(i-2)));
				}
				catch(Exception e){}
			}
		}
	}
	
	/**
	 * Insertion sort that sorts the linked lists based on the time each path took. This is done in 
	 * ascending order.
	 */
	private void sort() 
	{
		for(int i = 0; i < time.size(); i++) 
		{

			double currVal = time.get(i);
			double currDist = distance.get(i);
			String currDay = day.get(i);
			String currPath = path.get(i);
			int j = i;
			
			while(j > 0 && (currVal < time.get(j - 1))) 
			{
				time.set(j, time.get(j - 1));
				distance.set(j, distance.get(j - 1));
				day.set(j, day.get(j - 1));
				path.set(j, path.get(j -1));
				j--;
			}
			time.set(j, currVal);
			distance.set(j, currDist);
			day.set(j, currDay);
			path.set(j, currPath);
		}
	}
	
	/**
	 * Returns the integer value of how many taboo paths there are.
	 * 
	 * @return integer value of bad paths
	 */
	private int getBadPathCount()
	{
		int count = 0;
		for(int i = 0; i < badPick.size(); i++)
		{
			if(badPick.get(i).equals("END_BAD_PATH"))
			{
				count++;
			}
		}
		
		if(count == 0 && badPick.size() != 0)
		{
			count = 1;
		}
		
		return count;
	}
	
	/**
	 * Checks to see if there are enough days left to fill the required amount of day usage for each
	 * path. If there are enough spots left to fill all the days, then it returns true, if not it returns
	 * false.
	 * 
	 * @param iteration
	 * @return true or false
	 */
	private boolean goodCountCheck(int iteration)
	{
		boolean goodCount = true;
		//checks whether the amount of times a day has been used is legal or not for the 6 day schedule
		if(days == 6){
			if((twoUsedCount < 2) && ((days - iteration) < ( 2 - twoUsedCount)))
			{
				goodCount = false;
			}
			else if((threeUsedCount < 3) && ((days - iteration) < (3 - threeUsedCount)))
			{
				goodCount = false;
			}
			else if((fourUsedCount < 4) && ((days - iteration) < (4 - fourUsedCount)))
			{
				goodCount = false;
			}
			else if((fiveUsedCount < 5) && ((days - iteration) < (5 - fiveUsedCount)))
			{
				goodCount = false;
			}
			else if((oneUsedCount < 1) && ((days - iteration) < (1 - oneUsedCount)))
			{
				goodCount = false;
			}
		}
		//checks the day counts for all schedules not including the 6 day schedule
		else{
			if((twoUsedCount < 2) && ((days - iteration) < ( 2 - twoUsedCount)) && (days != 2))
			{
				if(twoUsedCount != 0){
					goodCount = false;
				}
			}
			else if((threeUsedCount < 3) && ((days - iteration) < (3 - threeUsedCount)) && (days != 3))
			{
				if(threeUsedCount != 0){
					goodCount = false;
				}
			}
			else if((fourUsedCount < 4) && ((days - iteration) < (4 - fourUsedCount))  && (days != 4))
			{
				if(fourUsedCount != 0){
					goodCount = false;
				}
			}
			else if((fiveUsedCount < 5) && ((days - iteration) < (5 - fiveUsedCount)) && (days != 5))
			{
				if(fiveUsedCount != 0){
					goodCount = false;
				}
			}
			else if((oneUsedCount < 1) && ((days - iteration) < (1 - oneUsedCount)) && (days != 1))
			{
				if(oneUsedCount != 0){
					goodCount = false;
				}
			}
		}
		return goodCount;
	}
	
	/**
	 * Finds the next index location of the path that will be used in the schedule. This is going to be
	 * the path with the shortest time, that will allow for all days to be used, that was not used in
	 * the last day, has the correct penalty assessment (MWF or TR), and is not a previously taboo move
	 * that would yield in an undesirable result.
	 * 
	 * @param iteration
	 * @param lastUsedDay
	 * @param lastUsedPath
	 * @param list
	 * @return next index of path to be used in schedule
	 */
	private int getNextIndex(int iteration, String lastUsedDay, String lastUsedPath, LinkedList<String> list)
	{
		int index = 0;
		int bpCount = getBadPathCount();
		
		for(int i = 0; i < day.size(); i++)
		{
			boolean usable = true;
			boolean goodCount = true;
			
			if(!(day.get(i).equals(lastUsedDay)) && !(path.get(i).equals(lastUsedPath)))
			{
				boolean noTripped = false;
				LinkedList<String> temp = new LinkedList<String>();
				
				for(int j = 0; j < this.lastUsedPath.size(); j++)
				{
					temp.addLast(this.lastUsedPath.get(j));
				}
				temp.addLast(path.get(i));
				
				incrementDayUse(temp.getLast());
				goodCount = goodCountCheck(iteration+1);
				decrementDayUse(temp.getLast());
				
				int loopDex = 0;
				boolean tripped = false;
				
				for(int k = 0; k < bpCount; k++) 
				{	
					tripped = false;
					
					for(int j = 0; j < temp.size(); j++)
					{
						while(loopDex >= badPick.size()){
							loopDex--;
						}
						if(!temp.get(j).equals(badPick.get(loopDex)) && !badPick.get(loopDex).equals("END_BAD_PATH"))
						{
							tripped = true;
						}
						
						if(badPick.get(index).equals("END_BAD_PATH") && !(j == temp.size() - 1))
						{
							loopDex++;
							break;
						}
						else if(j == temp.size() - 1)
						{
							int tempI = loopDex;
							while(!badPick.get(tempI).equals("END_BAD_PATH")) 
							{
								tempI++;
							}
							loopDex = tempI;
						}
						
						loopDex++;
					}
					
					if(tripped == false)
					{
						noTripped = true;
					}
				}
				
				for(int j = 0; j < list.size(); j++)
				{
					if(path.get(i).contains(list.get(j)))
					{
						usable = false;
					}
				}
				if(usable && !noTripped && goodCount) 
				{
					index = i;
					break;
				}
			}
		}
		
		return index;
	}
	
	/**
	 * Checks to see if there is any path available that meets all of the conditions needed (all the ones
	 * mentioned in pathAvailable() documentation). If there is a path it returns true, if not it will
	 * return false.
	 * 
	 * @param iteration
	 * @param lastUsedDay
	 * @param lastUsedPath
	 * @param list
	 * @return true or false
	 */
	private boolean pathAvailable(int iteration, String lastUsedDay, String lastUsedPath, LinkedList<String> list)
	{
		boolean pathAvailable = false;
		int bpCount = getBadPathCount();
		
		for(int i = 0; i < day.size(); i++)
		{
			boolean usable = true;
			boolean goodCount = true;
			boolean tripped = false;
			boolean noTripped = false;
			
			if(!(day.get(i).equals(lastUsedDay)) && !(path.get(i).equals(lastUsedPath)))
			{
				LinkedList<String> temp = new LinkedList<String>();
				
				
				for(int j = 0; j < this.lastUsedPath.size(); j++)
				{
					temp.addLast(this.lastUsedPath.get(j));
				}
				temp.addLast(path.get(i));
				
				incrementDayUse(temp.getLast());
				goodCount = goodCountCheck(iteration+1);
				decrementDayUse(temp.getLast());
				
				int index = 0;
				for(int k = 0; k < bpCount; k++) 
				{
					tripped = false;
					for(int j = 0; j < temp.size(); j++)
					{
						if(!temp.get(j).equals(badPick.get(index)) && !badPick.get(index).equals("END_BAD_PATH"))
						{
							tripped = true;
						}
						
						if(badPick.get(index).equals("END_BAD_PATH") && !(j == temp.size() - 1))
						{
							index++;
							break;
						}
						else if(j == temp.size() - 1)
						{
							int tempI = index;
							while(!badPick.get(tempI).equals("END_BAD_PATH")) 
							{
								tempI++;
							}
							index = tempI;
						}
						
						index++;
					}
		
					if(!tripped)
					{
						noTripped = true;
					}
				}
				
				for(int j = 0; j < list.size(); j++)
				{
					if(path.get(i).contains(list.get(j)))
					{
						usable = false;
					}
				}
				if(usable && !noTripped && goodCount)
				{
					pathAvailable = true;
					break;
				}
			}
		}
		return pathAvailable;
	}
	
	/**
	 * Increments the amount of times each day was used based on the path provided.
	 * 
	 * @param path
	 */
	private void incrementDayUse(String path)
	{
		//parse the path name into 3 different strings 
		String lastDay = path.substring(path.length() - 1);
		String secondLast = "";
		if(path.length() > 2){
			secondLast = path.substring(path.length() - 2);
			secondLast = String.valueOf(secondLast.charAt(0));
		}
		
		String thirdLast = "";
		if(path.length() > 3){
			thirdLast = path.substring(path.length() - 3);
			thirdLast = String.valueOf(thirdLast.charAt(0));
		}	
		for(int i = 1; i < 6; i++)
		{
			String matchedDay = String.valueOf(i);
			
			//this increments the specific days that have been used in a particular path
			if(lastDay.equals(matchedDay) || secondLast.equals(matchedDay) || thirdLast.equals(matchedDay))
			{
				if(matchedDay.equals("2"))
				{
					twoUsedCount++;
					
					if(twoUsedCount == 2)
					{
						exhaustedDays.addLast("2");
					}
				}
				else if(matchedDay.equals("3"))
				{
					threeUsedCount++;
					
					if(threeUsedCount == 3)
					{
						exhaustedDays.addLast("3");
					}
				}
				else if(matchedDay.equals("4"))
				{
					fourUsedCount++;
					
					if(fourUsedCount == 4)
					{
						exhaustedDays.addLast("4");
					}
				}
				else if(matchedDay.equals("5"))
				{
					fiveUsedCount++;
					
					if(fiveUsedCount == 5)
					{
						exhaustedDays.addLast("5");
					}
				}
				else if(matchedDay.equals("1"))
				{
					oneUsedCount++;

					if(oneUsedCount == 1){
						exhaustedDays.addLast("1");
					}
				}
			}
		}
	}
	
	/**
	 * Decrements the amount of times each day was used based on the path provided.
	 * 
	 * @param path
	 */
	private void decrementDayUse(String path)
	{
		//parse the path name into different strings 
		String lastDay = path.substring(path.length() - 1);
		String secondLast = "";
		if(path.length() > 2){
				secondLast = path.substring(path.length() - 2);
				secondLast = String.valueOf(secondLast.charAt(0));
		}
		String thirdLast = "";
		if(path.length() > 3){
			thirdLast = path.substring(path.length() - 3);
			thirdLast = String.valueOf(thirdLast.charAt(0));
		}
		
		for(int i = 1; i < 6; i++)
		{
			String matchedDay = String.valueOf(i);
			//if the matchedDay is equal to one of the parsed strings, decrement the appropriate counter
			if(lastDay.equals(matchedDay) || secondLast.equals(matchedDay) || thirdLast.equals(matchedDay))
			{
				if(matchedDay.equals("2"))
				{
					twoUsedCount--;
					
					if(twoUsedCount < 2)
					{
						try {
						exhaustedDays.remove("2");
						} catch(Exception e){}
					}
				}
				else if(matchedDay.equals("3"))
				{
					threeUsedCount--;
					
					if(threeUsedCount < 3)
					{
						try {
							exhaustedDays.remove("3");
							} catch(Exception e){}
					}
				}
				else if(matchedDay.equals("4"))
				{
					fourUsedCount--;
					
					if(fourUsedCount < 4)
					{
						try {
							exhaustedDays.remove("4");
							} catch(Exception e){}
					}
				}
				else if(matchedDay.equals("5"))
				{
					fiveUsedCount--;
					
					if(fiveUsedCount < 5)
					{
						try {
							exhaustedDays.remove("5");
							} catch(Exception e){}
					}
				}
				else if(matchedDay.equals("1"))
				{
					oneUsedCount--;

					if(oneUsedCount < 5)
					{
						try {
							exhaustedDays.remove("1");
						} catch(Exception e){}
					}
				}
			}
		}
	}
	
	/**
	 * Puts together a 6 day schedule from the combinations.  No path is used twice in a row, every day
	 * is used. MWF have correct penalties assessed, as do TRS. 
	 */
	private void scheduleAlgorithm(int days)
	{	
		loadCombinations(days);//loads all combinations via comboAlgorithm
		getDay(combinations);	//load the days, paths, distances, and times into a list and sort the list
		getPath(combinations);
		getDistances(combinations);
		getTimes(combinations);		
		sort();
		
		int shortestIndex = 0;
		lastUsedPath.addLast("");
		lastUsedDay.addLast("");
		
		for(int m = 0; m < days; m++) 
		{
			boolean skip = false;
			
			//if the path is okay, fetch an index with the shortest time 
			if(pathAvailable(m, lastUsedDay.getLast(), lastUsedPath.getLast(), exhaustedDays))
			{
				shortestIndex = getNextIndex(m, lastUsedDay.getLast(), lastUsedPath.getLast(), exhaustedDays);
			}
			else
			//if path not available, check to see if the last point was bad. If so, remove it and decrement
			//the for loop counter
			{	
				boolean badFlag = false;
				while(!pathAvailable(m, lastUsedDay.getLast(), lastUsedPath.getLast(), exhaustedDays))
				{
					if(badFlag)
					{
						badPick.removeLast();
					}
					else
					{
						badFlag = true;
						for(int i = 0; i < lastUsedPath.size(); i++)
						{
							badPick.addLast(lastUsedPath.get(i));
						}
					}
					decrementDayUse(lastUsedPath.getLast());
					finalSchedule.removeLast();
					lastUsedDay.removeLast();
					lastUsedPath.removeLast();
					badPick.addLast("END_BAD_PATH");
					skip = true;
					m--;
				}
				m--;
			}
			
			//if the path is available
			if(!skip)
			{
				for(int i = 0; i < combinations.size(); i++)
				{
					//check the path and day of the index read in earlier. If it matches the path and day
					//of the combination list, add them to their respective lastUsed lists
					String pathStr = "";
					if((combinations.get(i).equals(day.get(shortestIndex))) && (combinations.get(i+1).equals(path.get(shortestIndex))))
					{
						lastUsedDay.addLast(day.get(shortestIndex));
						lastUsedPath.addLast(path.get(shortestIndex));
				
						//for the size of the list, read in from the combinations list to a path string
						for(int j = i; j < combinations.size(); j++) 
						{
							if(combinations.get(j) != "END_OF_PATH") 
							{
								pathStr += combinations.get(j) + "\n";
							}
							//once the end of the path has been reached, add the path string to the finalSchedule
							else 
							{
								finalSchedule.add(pathStr);
								incrementDayUse(path.get(shortestIndex));
								break;
							}
						}
						break;
					}
				}
			}
		}
		
	}

	public LinkedList<String> getFinalSchedule(){
		return finalSchedule;
	}

	public int getNumDays(){
		return this.days;
	}
	
	public static void main(String[] args)
	{
		//call the schedule algorithm using the the constant variable days as a parameter
		Scheduler schedule = new Scheduler();
//		schedule.scheduleAlgorithm(days);
		ScheduleByDay sbd = new ScheduleByDay(schedule);
		QualityAssurance qa = new QualityAssurance(sbd.getReturnableFinalSchedule(), days);
		boolean areResultsGood = qa.checkFinalOutput();
		if(areResultsGood){
			System.out.print("Results have passed the quality assurance check.\nPrinting to output file now...\n\n");
		}
		else
			System.out.print("Results failed the quality assurance check.\nExiting program...\n\n");
		if (areResultsGood) {
			TextFileRouting print = new TextFileRouting();
			print.printPath(sbd.getReturnableFinalSchedule());        //print the calculated routes
			System.out.print("File written successfully.\nExiting program now...\n\n");
		}
	}
}
