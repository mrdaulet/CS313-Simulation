import java.util.ArrayList;

import lejos.nxt.LightSensor;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.subsumption.Behavior;

public class LineDetector implements Behavior {
	   
	private DifferentialPilot pilot;
	private LightSensor leftSensor;
	private LightSensor rightSensor;
	private boolean suppressed = false;
	
	private DataStore ds;
	
	private final int L_DARK_TRESHOLD = 530;
	private final int R_DARK_TRESHOLD = 530;
	
	public LineDetector(DifferentialPilot pilot, DataStore ds, LightSensor leftSensor, LightSensor rightSensor) {
		this.pilot = pilot;
		this.leftSensor = leftSensor;
		this.rightSensor = rightSensor;
		this.ds = ds;
	}
	   
	// In the simulation 1000 is white, 0 is black.
	// In real world ~530 is white and ~350 is black
	private boolean isLeftBlack() {
		return leftSensor.getNormalizedLightValue() < L_DARK_TRESHOLD;
	}
	
	private boolean isRightBlack() {
		return rightSensor.getNormalizedLightValue() < R_DARK_TRESHOLD;
	}
	
	public boolean takeControl() {
		int l = leftSensor.getNormalizedLightValue(), r = rightSensor.getNormalizedLightValue();

		if (movingToJunction() && (isLeftBlack() ^ isRightBlack())) {
			return true;
		}
		return false;
	}

	private boolean movingToJunction() {
		
		if (ds.cellWidth != 0) { // Already calibrated
			System.out.println("J: " + ds.junctionCnt + " cur: " + ds.currentDistance);
			
			if (ds.junctionCnt > 1 && ds.currentDistance + 20 < ds.cellWidth) {
				return true;
			}
		}
		
		return false;
	}

	   public void suppress() {
		   suppressed = true;
	   }
	   	   
	   public void action() {
	     suppressed = false;
	     
		 System.out.println("LineDetector");
		   

		 while (isLeftBlack() || isRightBlack()) {
			 int leftVal = leftSensor.getNormalizedLightValue();
			 int rightVal = rightSensor.getNormalizedLightValue();

			 if (rightVal < leftVal)
				 {
				 	pilot.rotate(30);
				 }
				 else
					if(leftVal > rightVal)
				 {			 	
					pilot.rotate(-30);
				 }
			 Thread.yield();
		 }
//		 pilot.travel(8);
		 
	   }
	}