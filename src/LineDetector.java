import java.util.LinkedList;
import java.util.Queue;

import ch.aplu.robotsim.LightSensor;
import lejos.robotics.subsumption.Behavior;

public class LineDetector implements Behavior {
	   
	private PilotGear pilot;
	private LightSensor leftSensor;
	private LightSensor rightSensor;
	private boolean suppressed = false;
	   	   
	private Queue<int[]> readings;
	private int maxReadingsSize = 10;
	
	private final int SIM_DARK_TRESHOLD = 1000;
	
	public LineDetector(PilotGear pilot,LightSensor leftSensor, LightSensor rightSensor) {
		this.pilot = pilot;
		this.leftSensor = leftSensor;
		this.rightSensor = rightSensor;
		readings = new LinkedList<int[]>();
	}
	   
	// In the simulation 1000 is white, 0 is black.
	// In real world ~530 is white and ~350 is black
	private boolean isLeftBlack() {
		return leftSensor.getValue() < SIM_DARK_TRESHOLD;
	}
	
	private boolean isRightBlack() {
		return rightSensor.getValue() < SIM_DARK_TRESHOLD;
	}
	
	public boolean takeControl() {
		int l = leftSensor.getValue(), r = rightSensor.getValue();
		readings.add(new int[] { l, r });
		if (readings.size() > maxReadingsSize) {
			readings.poll();
		}
		
		if (isSmoothTransition() && (isLeftBlack() ^ isRightBlack())) {
			return true;
		}
		return true;
	}

	private boolean isSmoothTransition() {
		int[][] diffs = new int[readings.size() - 1][2];
		int cnt = 0, lastLeft = 0, lastRight = 0;
		for (int[] sensors : readings) {
			int left = sensors[0], right = sensors[1];		
			
			if (cnt > 0) {
				diffs[cnt][0] = lastLeft - left;
				diffs[cnt][1] = lastRight - right;
			}
			
			lastLeft = left;
			lastRight = right;
			cnt++;
		}
		
		final int diffThreshold = 10; 
		
		// Need to see how differences change
		System.out.println("Differences:");
		for (int i = 0; i < diffs.length; i++) {
			System.out.print(diffs[i][0] + " ");
		}
		System.out.println();
		
		return false;
	}

	public void suppress() {
		suppressed = true;
	}
	   	   
	public void action() {
		suppressed = false;
	     
		System.out.println("LineDetector");
		   
		int leftVal = leftSensor.getValue();
		int rightVal = rightSensor.getValue();

		if (rightVal < leftVal) {
			pilot.rotate(30);
		} else
		if(leftVal > rightVal) {			 	
			pilot.rotate(-30);
		}
		pilot.forward(8);
	}
}