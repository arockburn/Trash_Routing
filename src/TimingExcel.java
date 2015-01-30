/**
 * Same exact class as Timing, except it reads from an excel file. This slowed down the algorithm to the point where
 */
import java.io.File;

import jxl.*;

/**
 * This class is for me to play with how to calculate the 
 * timing in our routing program.  It doesn't factor in
 * any actual routing, it just returns what time it'll be 
 * when the route is completed, given a distance.
 * @author Stacy Wise
 */
public class TimingExcel {
    //pretend we're starting a route at 8am
    private static double START_TIME = 8;
    
 /* Other time variables ... so far they aren't used
    //lunch break is at noon
    private final static double LUNCH_TIME = 12;
    
    //second shift starts at 12:30
    private final static double START_AFTER_LUNCH = 12.5;
    
    //workday ends at 4:30
    private final static double END_TIME = 16.5;
  */
    
    //average speed of the truck
    private static double TRUCK_SPEED_MPH = 12;
    
    //set the current time to the start time of the route
    public static double currentTime = START_TIME;
    
    //days of the week
    public static final int MONDAY = 1;
    public static final int TUESDAY = 2;
    public static final int WEDNESDAY = 3;
    public static final int THURSDAY = 4;
    public static final int FRIDAY = 5;
    public static final int SATURDAY = 6;
    
    //building types
    public static final int CLASSROOM_BUILDING = 0;
    public static final int FOOD_BUILDING = 1;
    public static final int MISC_BUILDING = 2;
    
    //constructor
    public TimingExcel() {
        //empty
    }
    
    /**
     * Sets the start time of the route. By default it is 8am.
     * @param time The time, in hours, at which the route will begin
     */
    public static void setStartTime(double time) {
    	//set the start time to the new time
    	START_TIME = time;
    	
    	//set the current time to the new start time
    	currentTime = START_TIME;
    }
    
    /**
     * Gets the start time of the route that's about to begin.
     * @return The current start time of the route
     */
    public static double getStartTime() {
    	return START_TIME;
    }
    
    /**
     * Gets the current time of the route that is running
     * @return The current time in hours
     */
    public static double getCurrentTime() {
    	return currentTime;
    }
    
    /**
     * Takes an amount of minutes (as a double) and returns 
     * a string.
     * @param minutes Amount of time in minutes
     * @return String timestamp of amount of passed time
     */
    public static String getTime(double minutes) {
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
     *         i.e., 12.5 would be 12:30pm)
     * @param thisBuildingType What kind of building this building is
     * @param classroomBuildings The number of classroom buildings that are nearby
     * @param foodBuildings The number of food buildings that are nearby 
     * @return Number of minutes to be penalized
     */
    public static double penaltyCheck(int dayOfWeek, double timeInHours,
            int thisBuildingType, int classroomBuildings, int foodBuildings) {
    	//penalty in minutes
    	double penalty = 0;
    	String day = "M";           //start on Monday by default
    	String building = "CLASS";  //classroom building by default
    	
    	//set day to string so excel will recognize it
    	if (dayOfWeek == MONDAY)
    		day = "M";
    	else if (dayOfWeek == TUESDAY)
    		day = "T";
    	else if (dayOfWeek == WEDNESDAY)
    		day = "W";
    	else if (dayOfWeek == THURSDAY)
    		day = "T";
    	else if (dayOfWeek == FRIDAY)
    		day = "F";
    	else if (dayOfWeek == SATURDAY)
    		day = "S";
    	
    	//set building type to string so excel will recognize it
    	if (thisBuildingType == CLASSROOM_BUILDING)
    		building = "CLASS";
    	else if (thisBuildingType == FOOD_BUILDING)
    		building = "FOOD";
    	else
    		building = "MISC";
    	
    	penalty = getExcelPenalty(timeInHours, building, day, classroomBuildings, foodBuildings);
    	
    	//penalty for stopping/starting the truck
    	penalty += 5;
    	
    	return penalty;
    }
    
    /**
     * Calculates the penalty for a building
     * @param max_penalty The maximum number of minutes that will be assigned as a penalty
     * @param max_length The maximum amount of time the penalty is effective
     * @param length How far into the length of the penalty we are currently at
     * @return The penalty in minutes
     */
    private static double calculatePenalty(double max_penalty, double max_length, double length) {
        //variable to return
        double penalty = 0;
        
        double fraction = Math.abs(1 - (length / max_length));
        
        penalty = max_penalty * fraction;
        
        //return the penalty in minutes
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
    public static double calculateTime(float distance, int numberOfBins) {
        //time (in minutes)
        double time = 0;
        
        //distance divided by rate equals time
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
    public static double calculateTime(float distance) {
        //time (in minutes)
        double time = 0;
        
        //distance divided by rate equals time
        time = distance / TRUCK_SPEED_MPH;
        
        //convert from hours to minutes
        time = time * 60;
        
        //return the total time
        return time;
    } 
    
    /**
     * Returns the time it would be after a route is performed.
     * @param distance The distance between the two points
     * @param bins The number of bins at the destination point
     * @param permanentRoute If true, the current time will be updated
     * for the next point on the route. If false, this is considered a 
     * test and will not update the current time.
     * @param dayOfWeek The day of the week for penalty checking
     * @param buildingType The destination's building type
     * @param nearbyClassroomBuildings Classroom buildings near the
     * destination.
     * @param nearbyFoodBuildings Food buildings near the destination
     * @return The time current time (in minutes)
     */
    public static double routeTiming(double distance, int bins, 
            boolean permanentRoute, int dayOfWeek, int buildingType,
            int nearbyClassroomBuildings, int nearbyFoodBuildings) {
        //current time in hours
        double now = currentTime;
        
        //minutes this route will take sans bins (in minutes)
        double elapsed = calculateTime((float)distance);
        
        //make minutes into hours
        now += minutesToHours(elapsed);
        elapsed = 0;
        
        //check for penalty
        elapsed = penaltyCheck(dayOfWeek, now, buildingType, 
                nearbyClassroomBuildings, nearbyFoodBuildings);
        
        //make minutes into hours again
        now += minutesToHours(elapsed);
        elapsed = 0;
        
        //calculate time spent emptying bins
        elapsed = (double)bins * BinTiming.getAvgBin();
        
        //make minutes into hours again
        now += minutesToHours(elapsed);
        elapsed = 0;
        
        //check for penalty again
        elapsed = penaltyCheck(dayOfWeek, now, buildingType, 
                nearbyClassroomBuildings, nearbyFoodBuildings);
        
        //minutes to hours again
        now += minutesToHours(elapsed);
        elapsed = 0;
        
        //if this is a permanent route, update the current time
        if (permanentRoute) {
            currentTime = now;
        }
        
        //return current time in minutes
        return now*60;
    }
    
    /**
     * Turn minutes into hours
     * @param minutes Number of minutes
     * @return Number of hours
     */
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
     * Gets the amount of time a penalty is assigned through an excel file
     * @param timeInHours What time it currently is in hours
     * @param buildingType The type of the current building
     * @param dayOfWeek The day of the week it currently is
     * @param classroomBuildings The number of classroom buildings that are nearby
     * @param foodBuildings The number of food buildings that are nearby
     * @return The amount of time that will be penalized
     */
    public static double getExcelPenalty(double timeInHours, String buildingType, String dayOfWeek,
    		int classroomBuildings, int foodBuildings) {
    	//convert variables to what I need them to be
    	buildingType = buildingType.toUpperCase();
    	dayOfWeek = dayOfWeek.substring(0,1).toUpperCase();
    	double initialMultiplier = .75;
    	
        //open excel
    	File penaltyFile = new File("Penalty.xls");
    	Workbook w;
    	double penalty = 0;
    	try { //open the workbook
    	    w = Workbook.getWorkbook(penaltyFile);
    	    Sheet s = w.getSheet(0);
    	    //find the row for which the current penalty applies
    	    for (int i = 0; i < s.getRows(); i++) {
    	    	if (s.getCell(0,i).getContents().contains(buildingType.toUpperCase())
    	    			&& s.getCell(3, i).getContents().contains(dayOfWeek)) {
    	    		double penaltyStartTime = stringToTime24Hour(s.getCell(1,i).getContents());
    	    		double penaltyEndTime = stringToTime24Hour(s.getCell(2,i).getContents());
    	    		double possiblePenalty = Double.parseDouble(s.getCell(4,i).getContents());
    	    		if ((timeInHours >= penaltyStartTime)
    	    				&& (timeInHours < penaltyEndTime)) {
    	    			double penaltyStartInMinutes = penaltyStartTime * 60;
    	    			double timeInMinutes = timeInHours * 60;
    	    			double penaltyEndInMinutes = penaltyEndTime * 60;
    	    			double maximumLength = penaltyEndInMinutes - penaltyStartInMinutes;
    	    			double length = timeInMinutes - penaltyStartInMinutes;
    	    			penalty += calculatePenalty(possiblePenalty, maximumLength, length);
    	    		}
    	    	}
    	    }
    	    
    	    //set multiplier
	    	double multiplier = initialMultiplier;
	    	
	    	//penalties for extra class buildings
    	    while (classroomBuildings > 0) {
    	    	for (int i = 1; i < s.getRows(); i++) {
    	    		if (s.getCell(0,i).getContents().contains("CLASS")
    	    				&& s.getCell(3,i).getContents().contains(dayOfWeek)) {
        	    		double penaltyStartTime = stringToTime24Hour(s.getCell(1,i).getContents());
        	    		double penaltyEndTime = stringToTime24Hour(s.getCell(2,i).getContents());
        	    		double possiblePenalty = Double.parseDouble(s.getCell(4,i).getContents());
        	    		if ((timeInHours >= penaltyStartTime)
        	    				&& (timeInHours < penaltyEndTime)) {
        	    			double penaltyStartInMinutes = penaltyStartTime * 60;
        	    			double timeInMinutes = timeInHours * 60;
        	    			double penaltyEndInMinutes = penaltyEndTime * 60;
        	    			double maximumLength = penaltyEndInMinutes - penaltyStartInMinutes;
        	    			double length = timeInMinutes - penaltyStartInMinutes;
        	    			penalty += calculatePenalty((possiblePenalty * multiplier), maximumLength, length);
        	    		}
    	    		}
    	    	}
	    		classroomBuildings--;
	    		multiplier = (multiplier * .75);
    	    }
    	    
    	    //reset multiplier
    	    multiplier = initialMultiplier;
    	    
    	    //penalties for extra food buildings
    	    if (foodBuildings > 0) {
    	    	for (int i = 1; i < s.getRows(); i++) {
    	    		if (s.getCell(0,i).getContents().contains("FOOD")
    	    				&& s.getCell(3,i).getContents().contains(dayOfWeek)) {
        	    		double penaltyStartTime = stringToTime24Hour(s.getCell(1,i).getContents());
        	    		double penaltyEndTime = stringToTime24Hour(s.getCell(2,i).getContents());
        	    		double possiblePenalty = Double.parseDouble(s.getCell(4,i).getContents());
        	    		if ((timeInHours >= penaltyStartTime)
        	    				&& (timeInHours < penaltyEndTime)) {
        	    			double penaltyStartInMinutes = penaltyStartTime * 60;
        	    			double timeInMinutes = timeInHours * 60;
        	    			double penaltyEndInMinutes = penaltyEndTime * 60;
        	    			double maximumLength = penaltyEndInMinutes - penaltyStartInMinutes;
        	    			double length = timeInMinutes - penaltyStartInMinutes;
        	    			penalty += calculatePenalty((possiblePenalty * multiplier), maximumLength, length);
        	    		}
    	    		}
    	    	}
	    		foodBuildings--;
	    		multiplier = (multiplier * .75);
    	    }
    	    w.close();
    	} catch (Exception e) {
    		e.printStackTrace();
    		return penalty;
    	}
    	return penalty;
    }
    
    /**
     * Turns a string timestamp into a double
     * @param timestamp Timestamp (in military time)
     * @return A double of the represented time in hours
     */
    private static double stringToTime24Hour(String timestamp) {
    	double timeInHours = 0;
    	double hours = Double.parseDouble(timestamp.substring(0, timestamp.indexOf(':')));
    	double minutes = Double.parseDouble(timestamp.substring(timestamp.indexOf(':')+1));
    	minutes = minutes / 60;
    	timeInHours += hours;
    	timeInHours += minutes;
    	return timeInHours;
    }
}