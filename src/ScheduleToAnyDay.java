import java.util.LinkedList;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by aaron on 2/27/15.
 */
public class ScheduleToAnyDay {
    LinkedList[] points;
    int numDays;

    //constructor
    ScheduleToAnyDay(int days){
        points = new LinkedList[days];
        //create a linked list for each day in schedule
        for(int i = 0; i < days; i++)
            points[i] = new LinkedList();
        numDays = days;
    }

    //Assign all points to the schedule
    public LinkedList[] assignPoints(LinkedList[] pickupPoints, int numDays){
        int[] pointSchedule = new int[numDays];
        //this variable is for whether points should be scheduled strictly to the days they
        //are assigned within the data file, or if they should be distributed evenly among
        //all days in the schedule
        boolean evenDistribution = false;
        while(!evenDistribution) {
            //loop starts with days that require the most visits
            //this is so the days with less required visits can fill
            //in the gaps left by days that need more visits.
            for (int i = ScheduleByDay.ALLDAYS - 1; i >= 0; i--) {
                for (int j = 0; j < pickupPoints[i].size() / 16; j++) {
                    pointSchedule = assignToRandom(i + 1);
                    for (int x = 0; x < numDays; x++) {
                        if (pointSchedule[x] == 1) {
                            for (int z = j * 16; z < (j + 1) * 16; z++) {
                                points[x].addLast(pickupPoints[i].get(z));
                            }
                        }
                    }
                }
            }

            int[] stopCounts = new int[numDays];
            for(int i = 0; i < points.length; i++){
                stopCounts[i] = points[i].size()/16;
            }

            //this loop makes sure the days are distributed evenly
            //check the distribution of days made by the random assignments
            //if there is a difference of more than 5 stops between the busiest and
            //least scheduled days, try and reassign for a more even distribution
            int max = 0;
            int min = 50;
            for(int i = 0; i < stopCounts.length; i++){
                if(stopCounts[i] > max)
                    max = stopCounts[i];

                if(stopCounts[i] < min)
                    min = stopCounts[i];
            }

            if(max - min <= 5)
                evenDistribution = true;
            else
                for(int i = 0; i < points.length; i++)
                    points[i] = new LinkedList();
        }

        return points;

    }

    //assign point *freq* times to random days
    private int[] assignToRandom(int freq){
        int[] schedule = new int[numDays];

        if(freq >= numDays){
            Arrays.fill(schedule, 1);
        }
        else{
            //to evenly distribute the visits, a period is established for which at least
            //one visit must occur. For example, if a point requires 3 visits in a 6 day schedule,
            //that point must be visited once every 2 days. Any extra days are recorded in the
            //extra days variable
            int period = numDays/freq;
            int extraDays = numDays % freq;
            int daysScheduled = 0;
            boolean doneScheduling = false;
            Random random = new Random();

            while(!doneScheduling){
                if(daysScheduled == numDays)
                    doneScheduling = true;
                else if(period == 1){
                    if(extraDays >= 1){
                        int[] gapDays = new int[extraDays];
                        for(int i = 0; i < extraDays; i++){
                            int rand = random.nextInt(numDays);
                            if(i == 0)
                                gapDays[0] = rand;
                            else{
                                if(gapDays[0] == rand)
                                    gapDays[i] = (rand + 2) % numDays;
                                else
                                    gapDays[i] = rand;
                            }
                        }
                        //the largest excess day number in this problem set is 2
                        if(extraDays == 2){
                            if(gapDays[0] > gapDays[1]){
                                int temp = gapDays[0];
                                gapDays[0] = gapDays[1];
                                gapDays[1] = temp;
                            }
                        }

                        //total numbers of gaps used
                        int gapDayCnt = 0;
                        boolean allGapsUsed = false;
                        for(int i = 0; i < numDays; i++){
                            boolean gap = false;
                            if(!allGapsUsed){
                                if(i == gapDays[gapDayCnt]){
                                    schedule[i] = 0;
                                    gapDayCnt++;
                                    gap = true;
                                    if(gapDayCnt == extraDays)
                                        allGapsUsed = true;
                                }
                            }
                            if(!gap)
                                schedule[i] = 1;

                            daysScheduled++;
                        }
                    }
                }

                else{
                    int dayToSchedule = random.nextInt(period);
                    for(int i = 0; i < period; i++){
                        if(daysScheduled > 0){
                            if(schedule[daysScheduled - 1] == 1 && dayToSchedule == i)
                                dayToSchedule++;
                        }
                        if(i != dayToSchedule)
                            schedule[daysScheduled] = 0;
                        else
                            schedule[daysScheduled] = 1;

                        daysScheduled++;
                    }
                    if(extraDays != 0){
                        schedule[daysScheduled] = 0;
                        daysScheduled++;
                        extraDays--;
                    }
                }
            }
        }
        return schedule;
    }
}
