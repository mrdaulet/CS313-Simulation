import ch.aplu.robotsim.Gear;
import ch.aplu.robotsim.LightSensor;
import lejos.robotics.subsumption.Behavior;

public class JunctionDetectorPro implements Behavior {
	
	private RobotState state = new RobotState();

	private Gear pilot;
	private LightSensor leftSensor;
	private LightSensor rightSensor;
	   	   
	private int L_ABS_DARK_TRESHOLD = 530;
	private int R_ABS_DARK_TRESHOLD = 530;
	private final int REL_DARK_TRESHOLD = 50;
	   
	private final int FULL_360_TURN = 2150; // 2400
	private final int TURN_90 = FULL_360_TURN/4;
   
	public JunctionDetectorPro(Gear pilot, LightSensor leftSensor, LightSensor rightSensor) {
		this.pilot = pilot;
		//this.pilot.setSpeed(30);
		this.leftSensor = leftSensor;
		this.rightSensor = rightSensor;
	}
   
	public boolean takeControl() {
		   
		System.out.println(this.getClass().getName());
		
		if (isLeftBlack() || isRightBlack()) { 
			return true;
		}
			  
		return false;
	}

   public void suppress() {
   }
   
   private boolean isLeftBlack() {
	   return leftSensor.getValue() > L_ABS_DARK_TRESHOLD;
   }

   private boolean isRightBlack() {
	   return rightSensor.getValue() > R_ABS_DARK_TRESHOLD;
   }
   
   public void action() {
     
	   while (takeControl()) {
		   
		   pilot.forward(100);

		   int currentAngle = 0, rotation = FULL_360_TURN/80; // 18/2 deg each turn, 2*5 turns for 90 deg
		   int sector = 0;
		   
		   while (currentAngle < FULL_360_TURN) {
			   
			   //System.out.println("l: " + (isLeftBlack()?1:0) + ", r: " + (isRightBlack()?1:0));
			   
			   if (isLeftBlack() || isRightBlack()) {
				   
				   //System.out.print("B, s=" + sector + ",cA=" + currentAngle);
				   
				   if (currentAngle >= TURN_90*sector-5*rotation && currentAngle <= TURN_90*sector+5*rotation) {
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
				   
				   state.lightReadings.add(new Pair(isLeftBlack(), isRightBlack()));
			   }
			   				   		   
			   pilot.right(rotation);
			   currentAngle += rotation;
			   if (currentAngle - (sector+1)*TURN_90 >= 0) {
				   sector++;
				   System.out.println("Changed sector " + currentAngle);
			   }
		   }
		   state.lightReadings.add(new Pair(isLeftBlack(), isRightBlack()));
		   
		   for (int i = 0; i < state.lightReadings.size(); i++) {
			   System.out.println("l: " + state.lightReadings.get(i).l + " r: " + state.lightReadings.get(i).r);
		   }
		   
	   }
	   Thread.yield();	 
	   pilot.stop();
   }
}