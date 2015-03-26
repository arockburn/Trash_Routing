import java.util.LinkedList;

/**
 * Class that assesses penalties, travel time from point to point, and bin pickup times.
 * 
 * @author Stacy Wise
 */
public class Timing {
	//pretend we're starting a route at 8am
	private static double START_TIME = 8;
	
	//lunch break is at noon
	private double LUNCH_TIME = 12;
	
	//second shift starts at 12:30
	private double START_AFTER_LUNCH = 12.5;
	
	//workday ends at 4:30
	private double END_TIME = 16.5;
	
	//average speed of the truck
	private static double TRUCK_SPEED_MPH = 12;
	
	static double currentTime = START_TIME;
	private static double binDisposalTime = BinTiming.getAvgBin();
	private static double tipcartDisposalTime = BinTiming.getAvgTipcart();
	
	//days of the week
	static final int MONDAY = 1;
	static final int TUESDAY = 2;
	static final int WEDNESDAY = 3;
	static final int THURSDAY = 4;
	static final int FRIDAY = 5;
	static final int SATURDAY = 6;
	
	//building types
	private static final int CLASSROOM_BUILDING = 0;
	private static final int FOOD_BUILDING = 1;
	private static final int MISC_BUILDING = 2;
	
	//forgot the constructor
	public Timing() {
		//empty
	}
	
	public static double getTruckSpeed()
	{
		return TRUCK_SPEED_MPH;
	}
	
	public static void resetStartTime()
	{
		currentTime = 8;
	}
	
	/**
	 * Takes an amount of minutes (as a double) and returns 
	 * a string.
	 * @param minutes Amount of time in minutes
	 * @return String timestamp of amount of passed time
	 */
	private static String getTime(double minutes) {
		//string variable
		String time = "";
		
		//variable for hours
		int hour = 0;
		
		//calculate timestamp
		while (minutes >= 60) {
			minutes = minutes-60;
			hour++;
		}
		
		//set hour
		time += hour + ":";
		
		//add minutes
		if (minutes < 10)
			time += "0";
		time += (int)minutes;
		
		//return constructed string
		return time;
	}
	
	/**
	 * Calculates the penalty for operating near certain buildings
	 * at certain times
	 * @param dayOfWeek What day of the week it is
	 * @param timeInHours What time it is (in hours, military time...
	 * @param thisBuildingType What kind of building this building is
	 * @param classroomBuildings The number of classroom buildings that are nearby
	 * @param foodBuildings The number of food buildings that are nearby 
	 * i.e., 12.5 would be 12:30pm)
	 * @return Number of minutes to be penalized
	 */
	public static double penaltyCheck(int dayOfWeek, double timeInHours, 
			int thisBuildingType, int classroomBuildings, int foodBuildings) {
		//penalty in minutes
		double penalty = 0;
		
		int hours = (int) timeInHours;
		double minutes = (timeInHours - hours) * 60;
		
		//classroom building penalties
		if (thisBuildingType == CLASSROOM_BUILDING
				|| classroomBuildings > 0) {
			/* if it's a Monday, Wednesday, or Friday,
			 * penalize between 8am-3pm from :50-:55 past the hour */
			if (dayOfWeek == MONDAY ||
					dayOfWeek == WEDNESDAY ||
					dayOfWeek == FRIDAY) {
				if ((hours >= 8 && hours < 16) &&
						(minutes >= 50 && minutes <= 58)) {
					//ten minute penalty
					penalty += 10;
				}
			}
			
			/* if it's a Tuesday or Thursday,
			 * penalize every hour and a half between 7:45am-12:15pm 
			 * and 1:45pm-4:45pm */
			else if (dayOfWeek == TUESDAY ||
					dayOfWeek == THURSDAY) {
				if ((minutes >= 15 && minutes <= 25) &&
						(hours == 9 ||
						hours == 12 ||
						hours == 15)) {
					//ten minute penalty
					penalty += 10;
				}
				else if ((minutes >= 45 && minutes <= 55) &&
						(hours == 7 ||
						hours == 10 ||
						hours == 13 ||
						hours == 16)) {
					//ten minute penalty
					penalty += 10;
				}
			}
		}
		
		//food building penalties
		else if (thisBuildingType == FOOD_BUILDING
					|| foodBuildings > 0) {
			//penalize between 12:15pm-1pm on Tues & Thurs
			if ((dayOfWeek == TUESDAY || dayOfWeek == THURSDAY) &&
					(hours == 12 && minutes >= 10)) {
				penalty += 10;
			}
			//penalize between 11-2 at :50-:55 past the hour
			if (dayOfWeek != SATURDAY) {
				if ((hours >= 11 && hours < 2) &&
						(minutes >= 50 && minutes <= 55)) {
					//ten minute penalty
					penalty += 10;
				}
			}
		}
		//return the penalty
		return penalty;
	}
	
	/**
	 * Calculates the amount of time it takes to travel a specified
	 * distance at a constant rate of speed
	 * @param distance The distance that will be traveled (in miles)
	 * @param numberOfBins The number of bins at that location
	 * @return The amount of time (in minutes) it will take to travel that 
	 * distance and unload the bins
	 */
	public static double calculateTime(Double distance, int numberOfBins) {
		//time (in minutes)
		double time = 0;
		
		//rate divided by distance equals time
		time = distance / TRUCK_SPEED_MPH;
		
		//convert from hours to minutes
		time = time * 60;
		
		//get time for bins by multiplying the number of bins by the average time
		double timeForBins = (double)numberOfBins * BinTiming.getAvgBin();
		
		//add that time to the total time
		time += timeForBins;
		
		//return the total time
		return time;
	} 
	
	/**
	 * Calculates the time it takes to travel a distance at the 
	 * fixed speed of the truck
	 * @param distance The distance which will be traveled
	 * @return The amount of time it takes in minutes
	 */
	public static double calculateTime(double distance) {
		//time (in minutes)
		double time = 0;
		
		//rate divided by distance equals time
		time = distance / TRUCK_SPEED_MPH;
		
		//convert from hours to minutes
		time = time * 60;
		
		//return the total time
		return time;
	} 
	

	public static LinkedList<String> getPenalty(double distance, int bins, 
			boolean permanentRoute, int dayOfWeek, int buildingType,
			int nearbyClassroomBuildings, int nearbyFoodBuildings) 
	{
		LinkedList<String> timeInfo = new LinkedList<String>();
		
		//current time in hours
		double now = 0;
		double stopTime = currentTime;
		double startTime;
		
		double totalPenalty = 0;
		
		//minutes this route will take sans bins (in minutes)
		//double elapsed = calculateTime((float)distance);
		double elapsed = calculateTime(distance);
		
		//make minutes into hours
		now += minutesToHours(elapsed);
		elapsed = 0;
		
		//check for penalty
		elapsed = penaltyCheck(dayOfWeek, currentTime+now, buildingType, 
				nearbyClassroomBuildings, nearbyFoodBuildings);
		totalPenalty = elapsed;
		
		//make minutes into hours again
		now += minutesToHours(elapsed);
		elapsed = 0;
		
		//calculate time spent emptying bins
		if(bins >= 0) 
		{
			elapsed = (double)bins * binDisposalTime;
		}
		else 
		{
			elapsed = tipcartDisposalTime;
		}
		
		//make minutes into hours again
		now += minutesToHours(elapsed);
		timeInfo.addLast(String.valueOf((double)Math.round(elapsed * 10000) / 10000));
		elapsed = 0;
		
		//check for penalty again
		elapsed = penaltyCheck(dayOfWeek, currentTime+now, buildingType, 
				nearbyClassroomBuildings, nearbyFoodBuildings);
		totalPenalty += elapsed;
		timeInfo.addLast(String.valueOf((double)Math.round(totalPenalty * 10000) / 10000));
		
		//minutes to hours again
		now += minutesToHours(elapsed);
		elapsed = 0;
		
		//if this is a permanent route, update the current time
		if (permanentRoute) {
			currentTime += now;
			startTime = currentTime;
		}
		else 
		{
			startTime = (currentTime + now);
		}
		timeInfo.addLast(String.valueOf(now*60));
		timeInfo.addLast(String.valueOf(stopTime));
		timeInfo.addLast(String.valueOf(startTime));

		return timeInfo;
	}
	
	private static double minutesToHours(double minutes) {
		return minutes / 60;
	}
	
	/**
	 * Calculates whether the route is valid based on the amount of time
	 * it took.
	 * @param minutes Number of minutes since the beginning of the day. (24 hour format)
	 * @param startTime The time the shift starts
	 * @param endTime The time the shift ends
	 * @return True if the route begins after the start of the shift and 
	 * ends before the end of the shift. Otherwise, False.
	 */
	public boolean timeIsValid(double minutes, double startTime, double endTime) {
		//empty variable for hour
		int hour = 0;
		
		//calculate hour & minutes
		while (minutes >= 60) {
			minutes = minutes-60;
			hour++;
		}
		
		//double to calculate actual time
		double time = hour + (minutes/60);
		
		//if current hour is after the start of the shift and 
		//before end of the shift, our route is valid
		if ((time >= startTime) && (time <= endTime))
			return true;
		//if not, route is invalid. must return to starting point 
		//by the end of the shift.
		else
			return false;
	}
	
	/**
	 * This method is to double check the time calculations between each point. This is to ensure a quality check
	 * so that the results will be accurate and consistent.
	 * 
	 * @author Aaron Rockburn 
	 * 
	 * @param bins
	 * @param day
	 * @param buildType
	 * @param nearFood
	 * @param nearClass
	 * @param timeCheck
	 * @param pointDistance
	 * @param permRoute
	 * @return
	 */
	
	private static boolean qualityCheck(int bins, int day, int buildType, int nearFood, int nearClass,
							double timeCheck, double pointDistance, boolean permRoute, double startTime){
		
		//declare and set variables
		double doubleCheckTime = 0;
		double end = startTime;
		double begin;
		double timeToConvert = 0;
		double penalty = 0;
		boolean qualityCheckGood = false;
		
		double timeTracker = calculateTime(pointDistance);
		
		doubleCheckTime += minutesToHours(timeTracker);
		timeTracker = 0;
		
		//include assessed penalty time
		timeTracker = penaltyCheck(day, startTime+doubleCheckTime, buildType, 
				nearClass, nearFood);
		penalty = timeTracker;
		
		doubleCheckTime += minutesToHours(timeTracker);
		timeTracker = 0;
		
		//include time for bin collection
		if(bins >= 0) 
		{
			timeTracker = (double)bins * binDisposalTime;
		}
		else 
		{
			timeTracker = tipcartDisposalTime;
		}
		
		doubleCheckTime += minutesToHours(timeTracker);
		timeTracker = 0;
		
		timeTracker = penaltyCheck(day, startTime+doubleCheckTime, buildType, 
				nearClass, nearFood);
		penalty += timeTracker;
		
		doubleCheckTime += minutesToHours(timeTracker);
		timeTracker = 0;
		
		
		//take into account permanent route or not
		if (permRoute) {
			startTime += doubleCheckTime;
			begin = startTime;
		}
		else 
		{
			begin = (startTime + doubleCheckTime);
		}
		
		if(begin == timeCheck){
			qualityCheckGood = true;
		}
		else{
			qualityCheckGood = false;
		}
		
		return qualityCheckGood;
	}
}