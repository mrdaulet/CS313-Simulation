
import lejos.nxt.*;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;

public class GridSolve {
	public static void main(String[] args) throws Exception {
		
	    LightSensor lsLeft = new LightSensor(SensorPort.S2);
	    LightSensor lsRight = new LightSensor(SensorPort.S3);
	    UltrasonicSensor us = new UltrasonicSensor(SensorPort.S1);
	    
		DifferentialPilot gear = new DifferentialPilot(56, 26, Motor.A, Motor.B);
		DataStore ds = new DataStore();
		
		Behavior b1 = new DriveForward(gear, ds);
		Behavior b2 = new JunctionDetectorPro(gear,
										ds,
									  lsLeft,
				 					  lsRight);
		Behavior b3 = new ObstacleDetector(gear,us, lsLeft);
		Behavior b4 = new LineDetector(gear, ds, lsLeft, lsRight);
		
		Behavior [] bArray = {b1, b3, b2, b4};
		Arbitrator arby = new
				Arbitrator(bArray);
		arby.start();
	  }
}
