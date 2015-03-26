import java.util.Random;
import java.util.LinkedList;

/**
 * Created by aaron on 1/8/15.
 */
public class ScheduleByDay {

    int BREAK = 15;
    int LUNCH = 30;
    int ITERATIONS = 500;
    int COLS = 16;
    int DAYS;
    public static final int ALLDAYS = 6;
    boolean stickToSelectedDays = false;
    LinkedList[] dayPickupPoints;
    LinkedList[] daySchedule;
    LinkedList[] pickupPointsByDay;
    LinkedList[] dayRoutes;
    LinkedList[] finalRoutes;
    LinkedList<String> returnableFinalSchedule = new LinkedList<String>();
    TextFileRouting tfr = new TextFileRouting();
    Scheduler scheduler = new Scheduler();
    LinkedLists allDays = new LinkedLists();

    /**
     * Default constructor for the class
     *
     * @param s
     */
    ScheduleByDay(Scheduler s) {
        scheduler = s;
        DAYS = scheduler.getNumDays();
        dayPickupPoints = new LinkedList[ALLDAYS];
        daySchedule = new LinkedList[DAYS];
        pickupPointsByDay = new LinkedList[DAYS];
        dayRoutes = new LinkedList[DAYS];
        finalRoutes = new LinkedList[DAYS];
        loadAllPickupPoints();
        if (stickToSelectedDays)
            loadPointsBySelectedDays();
        else
            loadPointsToAnyFit();
        createDayScheduleByPoints();
        finalRoutes = getNewRoutes();
        finalRoutes = findBestRoute(finalRoutes);
        convertFinalSchedule(finalRoutes);
    }


    private void loadPointsToAnyFit() {
        ScheduleToAnyDay anyDay = new ScheduleToAnyDay(DAYS);
        daySchedule = anyDay.assignPoints(dayPickupPoints, DAYS);
    }

    /**
     * Creates a schedule for each day based on the number of days in the requested schedule.
     */
    public void loadPointsBySelectedDays() {
        for (int i = 0; i < ALLDAYS; i++) {
            decodeDays(dayPickupPoints[i]);
        }

    }

    /**
     * Load all pickup points into this class
     */
    private void loadAllPickupPoints() {
        initializeArrays();
        allDays.loadAllDays();
        LinkedList loadedDays = allDays.getWholeBook();
        for (int i = 0; i < loadedDays.size() / COLS; i++) {
            String freq = String.valueOf(loadedDays.get((i * COLS) + 4));
            for (int j = 0; j < COLS; j++) {
                dayPickupPoints[Integer.parseInt(freq) - 1].addLast(loadedDays.get((i * COLS) + j));
            }
        }
    }

    /**
     * Initialize the arrays that will be used by this class
     */
    private void initializeArrays() {
        for (int i = 0; i < DAYS; i++) {
            daySchedule[i] = new LinkedList();
            pickupPointsByDay[i] = new LinkedList();
            dayRoutes[i] = new LinkedList();
            finalRoutes[i] = new LinkedList();
        }
        for (int i = 0; i < ALLDAYS; i++) {
            dayPickupPoints[i] = new LinkedList();
        }
    }

    /**
     * For each pickup point, find the days on which it is supposed to be visited and add it to the corresponding linked list
     *
     * @param dayPoints
     */
    private void decodeDays(LinkedList dayPoints) {
        int previousCode = -2;
        for (int i = 0; i < dayPoints.size() / COLS; i++) {
            for (int j = 6; j < 12; j++) {
                int code = getDayCode(String.valueOf(dayPoints.get((i * COLS) + j)));
                if (code != -1 && code + 1 <= ALLDAYS) {
                    if (code == 5 && DAYS < ALLDAYS) {
                        if (previousCode == 4)
                            code = 3;
                        else
                            code = 4;
                    }
                    assignPointToDay(code, i * COLS, dayPoints);
                    previousCode = code;
                }
            }
        }
    }

    /**
     * Translate the string code into an int
     *
     * @param code
     * @return
     */
    private int getDayCode(String code) {
        int dayCode = -1;
        if (code.equals("M")) {
            dayCode = 0;
            return dayCode;
        }
        if (code.equals("T")) {
            dayCode = 1;
            return dayCode;
        }
        if (code.equals("W")) {
            dayCode = 2;
            return dayCode;
        }
        if (code.equals("R")) {
            dayCode = 3;
            return dayCode;
        }
        if (code.equals("F")) {
            dayCode = 4;
            return dayCode;
        }
        if (code.equals("Sat")) {
            dayCode = 5;
            return dayCode;
        }
        return dayCode;
    }

    /**
     * Add a pickup point to a linked list
     *
     * @param dayCode
     * @param listPos
     * @param pointLL
     */
    private void assignPointToDay(int dayCode, int listPos, LinkedList pointLL) {
        for (int i = 0; i < COLS; i++) {
            daySchedule[dayCode].addLast(pointLL.get(listPos + i));
        }
    }

    private void createDayScheduleByPoints() {
        for (int i = 0; i < DAYS; i++) {
            for (int j = 0; j < daySchedule[i].size() / COLS; j++) {
                pickupPointsByDay[i].addLast(daySchedule[i].get((j * COLS) + 1));
            }
            //remove the starting point, as it is already accounted for in TextFileRouting's routeAlgorithm
            pickupPointsByDay[i].remove("01");
        }
    }

    /**
     * Create the initial routes for each day. These will be used to compare future iterations to find the best route
     *
     * @return namedRoutes
     */

    private LinkedList[] getNewRoutes() {
        LinkedList[] routes = new LinkedList[DAYS];
        LinkedList[] namedRoutes = new LinkedList[DAYS];

        for (int x = 0; x < routes.length; x++) {
            routes[x] = new LinkedList();
            namedRoutes[x] = new LinkedList();
        }

        for (int i = 0; i < DAYS; i++) {
            LinkedList tempList = tfr.runRouteAlgorithm(pickupPointsByDay[i], i);
            for (int j = 0; j < tempList.size(); j++) {
                routes[i].addLast(String.valueOf(tempList.get(j)));
            }
            namedRoutes[i] = insertStopNames(routes[i], i);

        }

        return namedRoutes;
    }

    /**
     * Replaces the pickup point number in each list with the name of the point.
     *
     * @param list
     * @param day
     * @return renamed List
     */
    private LinkedList insertStopNames(LinkedList list, int day) {
        LinkedList routeBook = tfr.getWholeBook();
        LinkedList newList = new LinkedList();
        double listDistance = 0;
        double listTime = 0;

        for (int i = 0; i < list.size(); i++) {
            String listLine = String.valueOf(list.get(i));
            String[] splitListLine = listLine.split(":");
            if(splitListLine.length > 1) {
                String firstStop = splitListLine[0];
                String secondStop = splitListLine[1];

                int firstStopIndex = routeBook.indexOf(firstStop) - 1;
                int secondStopIndex = routeBook.indexOf(secondStop) - 1;

                String firstStopName = String.valueOf(routeBook.get(firstStopIndex));
                String secondStopName = String.valueOf(routeBook.get(secondStopIndex));
                splitListLine[0] = firstStopName;
                splitListLine[1] = secondStopName;

                listDistance += Double.parseDouble(splitListLine[2]);
                listTime += Double.parseDouble(splitListLine[4]);
            }

            String newLine = "";
            for (int j = 0; j < splitListLine.length; j++) {
                if(splitListLine.length > 1) {
                    if (j != splitListLine.length - 1)
                        newLine += splitListLine[j] + ":";
                    else
                        newLine += splitListLine[j];
                }
                else
                    newLine = splitListLine[0];
            }


            newList.addLast(newLine);
        }
        if (listTime >= 120)
            listTime += BREAK;
        if (listTime >= 240)
            listTime += LUNCH;
        if (listTime >= 360)
            listTime += BREAK;

        newList.addLast(listDistance);
        newList.addLast(listTime);

        return newList;
    }

    /**
     * The name for this function is a bit deceiving. It finds the best possible route out of all routes explored. Being an NP-Complete problem, finding the best
     * solution is not practical. This function saves the best overall routes for each day and returns them to the main class.
     *
     * @param currentBestRoutes
     * @return bestRoutes
     */
    private LinkedList[] findBestRoute(LinkedList[] currentBestRoutes) {
        LinkedList[] bestRoutes = new LinkedList[DAYS];
        LinkedList[] newRoutes = new LinkedList[DAYS];
        for (int i = 0; i < DAYS; i++) {
            bestRoutes[i] = new LinkedList();
            newRoutes[i] = new LinkedList();
            for (int j = 0; j < currentBestRoutes[i].size(); j++) {
                bestRoutes[i].addLast(currentBestRoutes[i].get(j));
            }
        }

        /*
        main loop that finds the best route for the schedule. Iterates x times where ITERATIONS is a constant declared at the top of this
        class.
        */
        for (int x = 0; x < ITERATIONS; x++) {
            newRoutes = getNewRoutes();
            for (int i = 0; i < DAYS; i++) {
                double currBestTime = Double.parseDouble(String.valueOf(bestRoutes[i].get(bestRoutes[i].size() - 1)));
                double newBestTime = Double.parseDouble(String.valueOf(newRoutes[i].get(newRoutes[i].size() - 1)));
                if (newBestTime < currBestTime) {
                    bestRoutes[i].clear();
                    for (int j = 0; j < newRoutes[i].size(); j++) {
                        bestRoutes[i].add(j, newRoutes[i].get(j));
                    }
                }
                newRoutes[i].clear();
            }
        }

        return bestRoutes;
    }

    /**
     * Converts the linked list containing the best schedule to one linked list. It does this because the rest of the program
     * is based off of having a schedule in one linked list, instead of in an array of linked lists like this class.
     *
     * @param schedToBeConverted a schedule in an array of linked lists. This should be the result of the findBestRoute function
     */
    private void convertFinalSchedule(LinkedList[] schedToBeConverted) {
        for (int i = 0; i < DAYS; i++) {
            String daySchedule = "";
            for (int j = 0; j < schedToBeConverted[i].size(); j++) {
                if (j != schedToBeConverted[i].size() - 1)
                    daySchedule += schedToBeConverted[i].get(j) + "\n";
                else
                    daySchedule += schedToBeConverted[i].get(j);
            }
            returnableFinalSchedule.addLast(daySchedule);
        }
    }

    public LinkedList<String> getReturnableFinalSchedule() {
        return returnableFinalSchedule;
    }
}

