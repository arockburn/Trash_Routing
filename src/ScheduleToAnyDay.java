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
        numDays = days;
    }

    public void assignPoints(LinkedList[] pickupPoints, int numDays){
        for(int i = ScheduleByDay.ALLDAYS - 1; i >= 0 ; i--){
            assignToRandom(pickupPoints, i);
        }

    }

    private void assignToRandom(LinkedList[] p, int freq){
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
                if(daysScheduled == freq)
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
    }
}
