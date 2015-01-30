import java.util.*;

public class QuadTrash {
	
	static int pickupSpots = 4;
	static String visited = "";
	static int sToAts = 700;
	static int sToSpotts = 750;
	static int sToVsc = 810;
	static int sToEis = 650;
	static int atsToSpotts = 50;
	static int atsToVsc = 80;
	static int atsToEis = 50;
	static int spottsToVsc = 70;
	static int spottsToEis = 60;
	static int eisToVsc = 63;
	static int startDistance = 0;
	static int pickupDistance = 0;
	static int totalDistance = 0;
	static String startSpot = "";
	
	public static int checkEisenberg() {
		int value = 9000;
		
		if((eisToVsc < value) && !visited.contains("Vsc")){
			value = eisToVsc;
			startSpot = "Vsc";
		}
		if((spottsToEis < value) && !visited.contains("Spotts")){
			value = spottsToEis;
			startSpot = "Spotts";
		}
		if((atsToEis < value) && !visited.contains("Ats")){
			value = atsToEis;
			startSpot = "Ats";
		}
		
		if(value == 9000) {
			value = sToEis;
		}
		
		return value;
	}
	
	public static int checkAts() {
		int value = 9000;
		
		if((atsToSpotts < value) && !visited.contains("Spotts")){
			value = atsToSpotts;
			startSpot = "Spotts";
		}
		if((atsToVsc < value) && !visited.contains("Vsc")){
			value = atsToVsc;
			startSpot = "Vsc";
		}
		if((atsToEis < value) && !visited.contains("Eis")){
			value = atsToEis;
			startSpot = "Eis";
		}
		
		if(value == 9000){
			value = sToAts;
		}
		
		return value;
	}
	
	public static int checkSpotts() {
		int value = 9000;
		
		if((atsToSpotts < value) && !visited.contains("Ats")){
			value = atsToSpotts;
			startSpot = "Ats";
		}
		if((spottsToVsc < value) && !visited.contains("Vsc")){
			value = atsToVsc;
			startSpot = "Vsc";
		}
		if((spottsToEis < value) && !visited.contains("Eis")){
			value = atsToEis;
			startSpot = "Eis";
		}
		
		if(value == 9000){
			value = sToSpotts;
		}
		return value;
	}
	
	public static int checkVsc() {
		int value = 9000;
		
		if((atsToVsc < value) && !visited.contains("Ats")){
			value = atsToVsc;
			startSpot = "Ats";
		}
		if((eisToVsc < value) && !visited.contains("Eis")){
			value = eisToVsc;
			startSpot = "Eis";
		}
		if((spottsToVsc < value) && !visited.contains("Spotts")){
			value = spottsToVsc;
			startSpot = "Spotts";
		}
		
		if(value == 9000) {
			value = sToVsc;
		}
		
		return value;
	}
	public static void main(String args[]) {
		
		LinkedList<Integer> starts = new LinkedList<Integer>();
		starts.addLast(sToAts);
		starts.addLast(sToSpotts);
		starts.addLast(sToVsc);
		starts.addLast(sToEis);
		startDistance = starts.get(0);
		
		for(int i = 0; i < starts.size(); i++) {
			if(starts.get(i) < startDistance) {
				startDistance = starts.get(i);
			}
		}
		
		if(startDistance == sToEis) {
			startSpot = "Eis";
			visited = "Eis";
		}
		else if(startDistance == sToAts) {
			startSpot = "Ats";
			visited = "Ats";
		}
		else if(startDistance == sToSpotts) {
			startSpot = "Spotts";
			visited = "Spotts";
		}
		else if(startDistance == sToVsc) {
			startSpot = "Vsc";
			visited = "Vsc";
		}
		
		totalDistance = startDistance;
		
		for(int i = 0; i < pickupSpots; i++) {
			if(startSpot == "Eis"){
				totalDistance += checkEisenberg();
				visited += " Eis";
			}
			else if(startSpot == "Ats"){
				totalDistance += checkAts();
				visited += " Ats";
			}
			else if(startSpot == "Spotts"){
				totalDistance += checkSpotts();
				visited += " Spotts";
			}
			else if(startSpot == "Vsc") {
				totalDistance += checkVsc();
				visited += " Vsc";
			}
		}
		
		System.out.println("Distance of Trip: " + totalDistance);
	}
}