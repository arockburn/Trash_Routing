import java.util.LinkedList;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by aaron on 2/27/15.
 */
public class ScheduleToAnyDay {
    LinkedList[] points;
    int numDays;

    ScheduleToAnyDay(int days){
        points = new LinkedList[days];
        for(int i = 0; i < days; i++)
            points[i] = new LinkedList();
        numDays = days;
    }

    public LinkedList[] assignPoints(LinkedList[] pickupPoints, int numDays){
        int[] pointSchedule = new int[numDays];
        boolean evenDistribution = false;
        while(!evenDistribution) {
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

    private int[] assignToRandom(int freq){
        int[] schedule = new int[numDays];


        if(freq >= numDays){
            Arrays.fill(schedule, 1);
        }
        else{
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
                        if(extraDays == 2){
                            if(gapDays[0] > gapDays[1]){
                                int temp = gapDays[0];
                                gapDays[0] = gapDays[1];
                                gapDays[1] = temp;
                            }
                        }

                        int gapDayCnt = 0;
                        boolean gapsUsed = false;
                        for(int i = 0; i < numDays; i++){
                            boolean gap = false;
                            if(!gapsUsed){
                                if(i == gapDays[gapDayCnt]){
                                    schedule[i] = 0;
                                    gapDayCnt++;
                                    gap = true;
                                    if(gapDayCnt == extraDays)
                                        gapsUsed = true;
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
