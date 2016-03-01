import ch.aplu.robotsim.Gear;
import ch.aplu.robotsim.LightSensor;
import ch.aplu.robotsim.Tools;
import lejos.robotics.subsumption.Behavior;

public class JunctionDetectorPro implements Behavior {
	
	private RobotState state = new RobotState();

	private Gear pilot;
	private LightSensor leftSensor;
	private LightSensor rightSensor;
	   	   
	private int L_ABS_DARK_TRESHOLD = 530;
	private int R_ABS_DARK_TRESHOLD = 530;
	private final int REL_DARK_TRESHOLD = 50;
	   
	private final int FULL_360_TURN = 2160; // 2400 for real
	private final int TURN_90 = FULL_360_TURN/4;

	private boolean suppressed = false;
   
	public JunctionDetectorPro(Gear pilot, LightSensor leftSensor, LightSensor rightSensor) {
		this.pilot = pilot;
		//this.pilot.setSpeed(30);
		this.leftSensor = leftSensor;
		this.rightSensor = rightSensor;
	}
   
	public boolean takeControl() {
		
		if (isLeftBlack() || isRightBlack()) { 
			return true;
		}
			  
		return false;
	}

   public void suppress() {
	   suppressed = true;
   }
   
   // In the simulation 1000 is white, 0 is black.
   // In real world ~530 is white and ~350 is black
   private boolean isLeftBlack() {
	   return leftSensor.getValue() < L_ABS_DARK_TRESHOLD;
   }

   private boolean isRightBlack() {
	   return rightSensor.getValue() < R_ABS_DARK_TRESHOLD;
   }
   
   public void action() {
	   suppressed = false;

	   pilot.forward(120); // 75 in real

	   int currentRotation = 0, rotationUnits = 30; // 2160 / 30 = 72 turns
	   int sector = 0;
	   int lastReading = pilot.getRotationIndex(), totalAngle = 0;
	   
	   while (totalAngle < 360) {		   
		   if (isLeftBlack() || isRightBlack()) {
//			   System.out.println(currentRotation + " " + (TURN_90*sector-5*rotationUnits) + " " + (TURN_90*sector+5*rotationUnits));
			   if (currentRotation >= TURN_90*sector-8*rotationUnits && currentRotation <= TURN_90*sector+8*rotationUnits) {
				   String direction = "def";
				   switch (sector) {
					   case 0: 
						   direction = "front";
						   break;
					   case 1: 
						   direction = "right";
						   break;
					   case 2: 
						   direction = "back";
						   break;
					   case 3: 
						   direction = "left";
						   break;
				   }
				   
				   System.out.println("Line " + direction);
			   }
		   }
		   				   		   
		   pilot.right(rotationUnits);
		   Tools.delay(50);
		   
		   /* Only in simulation BEGIN*/
		   totalAngle += (pilot.getRotationIndex() - lastReading + 360) % 360;
		   lastReading = pilot.getRotationIndex();
		   /* Only in simulation END*/
		   
		   currentRotation += rotationUnits;
		   
		   if (currentRotation - (sector+1)*TURN_90 >= 0) {
			   sector++;
			   System.out.println("Changed sector " + currentRotation);
		   }
	   }
	   
	   pilot.stop();
   }
}