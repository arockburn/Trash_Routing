/**
 * Created by Aaron Rockburn on 12/14/14
 * The purpose of this class is to check the output of the scheduling algorithm against
 * any parameters or deadlines and double check the calculations.
 */

import java.util.*;

public class QualityAssurance {
	
	private Scheduler s;
	private boolean isSolutionGood = false;

	/**
	 * Constructor for base QualityAssurance instantiation
	 * Has one variable that is a Scheduler which is used to access the final output of a run
	 */
	QualityAssurance(){
		s = new Scheduler();
	}
	
	public QualityAssurance(Scheduler scheduler){
		s = scheduler;
	}

	public void setScheduler(Scheduler scheduler){
		s = scheduler;
	}

	/**
	 * Checks final results to make sure all calculations are correct and the results are within any deadlines
	 *
	 * @return isSolutionGood
	 */
	public boolean checkFinalOutput(){

		if(checkDistances() && checkStopCounts() && checkTimes())
			return true;
		else
			return false;
	}

	/**
	 * Checks to see if times in final output are within the acceptable range
	 *
	 * @return areTimesGood
	 */
	public boolean checkTimes() {
		boolean areTimesGood = false;
		double epsilon = .1;

		for(int i = 0; i < s.getNumDays(); i++){
			double totalTime = 0;
			String daySched = this.s.getFinalSchedule().get(i).toString();
			String[] sched = daySched.split("\n");
			for(int j = 0; j < sched.length; j++){
				if(j != 0 && j != 1 && j != sched.length - 2 && j != sched.length - 1){
					String[] schedLine = sched[j].split(":");
					double travelTime = Double.parseDouble(schedLine[4]);
					//double binCollectionTime = Double.parseDouble(schedLine[6]);
					totalTime += travelTime;
				}
			}

			if(totalTime >= 120)
				totalTime += 15;
			if(totalTime >= 240)
				totalTime += 30;
			if(totalTime >= 360){
				totalTime += 15;
			}

			double outputTotalTime = Double.parseDouble(sched[sched.length - 1]);
			if(Math.abs(totalTime - outputTotalTime) < epsilon){
				areTimesGood = true;
			}
			else{
				areTimesGood = false;
				break;
			}
		}

		return areTimesGood;
	}

	/**
	 * Checks to makes sure each point is visited the correct amount of times
	 *
	 * @return areStopCountsGood
	 */
	public boolean checkStopCounts(){
		boolean areStopCountsGood = false;
		int totalFreq = 0;
		int calculatedFreq = 0;
		int currentFreq = 0;

		LinkedLists list = new LinkedLists();
		list.loadAllDays();
		LinkedList allDays = list.getWholeBook();
		for(int i = 0; i < allDays.size() / 16; i++){
			int freq = Integer.parseInt(allDays.get((i * 16) + 4).toString());
			if(freq > s.getNumDays())
				freq = s.getNumDays();
			totalFreq += freq;
			String pointName = allDays.get(i * 16).toString();
			for(int j = 0; j < s.getNumDays(); j++) {
				String daySched = this.s.getFinalSchedule().get(j).toString();
				String[] sched = daySched.split("\n");
				currentFreq += findNumOccurances(sched, pointName);
			}
			if (currentFreq == freq) {
				calculatedFreq += currentFreq;
			}
			currentFreq = 0;
		}

		if(calculatedFreq == totalFreq)
			areStopCountsGood = true;

		return areStopCountsGood;
	}

	/**
	 * Checks to see if the distances calculated in the program are correct
	 *
	 * @return areDistancesGood
	 */
	public boolean checkDistances(){
		boolean areDistancesGood = false;
		double roundOff = .1;

		for(int i = 0; i < s.getNumDays(); i++){
			String daySched = this.s.getFinalSchedule().get(i).toString();
			String[] sched = daySched.split("\n");
			double outputTotalTime = Double.parseDouble(sched[sched.length - 2]);
			double calculatedTotalTime = 0;
			for(int j = 0; j < sched.length; j++){
				if(j != 0 && j != 1 && j != sched.length - 2 && j != sched.length - 1) {
					String[] schedLine = sched[j].split(":");
					String timeForVisit = schedLine[2];
					calculatedTotalTime += Double.parseDouble(timeForVisit);
				}
			}

			if(Math.abs(calculatedTotalTime - outputTotalTime) < roundOff)
				areDistancesGood = true;
			else{
				areDistancesGood = false;
				break;

			}
		}

		return areDistancesGood;
	}

	private int findNumOccurances(String[] schedule, String pointName){
		int cnt = 0;
		for(int i = 0; i < schedule.length; i++){
			if(i != 0 && i != 1 && i != schedule.length -2 && i != schedule.length -1) {
				String[] splitRoute = schedule[i].split(":");
				if(pointName.equals("start spot")){
					if(splitRoute[0].equals(pointName))
						cnt++;
				}
				else if (splitRoute[1].equals(pointName))
					cnt++;
			}
		}

		return cnt;
	}

}
