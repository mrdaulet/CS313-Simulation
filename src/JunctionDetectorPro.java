import java.util.LinkedList;

import ch.aplu.robotsim.Gear;
import ch.aplu.robotsim.LightSensor;
import ch.aplu.robotsim.RobotContext;
import ch.aplu.robotsim.Tools;
import lejos.robotics.subsumption.Behavior;

public class JunctionDetectorPro implements Behavior {
	
	private RobotState state = new RobotState();

	private Gear pilot;
	private LightSensor leftSensor;
	private LightSensor rightSensor;
	private DataStore ds;
	
	private int gridX = 0;
	private int gridY = -1;
	private Junction currentJunction;	
	
	private int L_ABS_DARK_TRESHOLD = 530;
	private int R_ABS_DARK_TRESHOLD = 530;
	private final int SIM_DARK_TRESHOLD = 1000;
	   
	private final int FULL_360_TURN = 2160; // 2400 for real
	private final int TURN_90 = FULL_360_TURN/4;

	private boolean suppressed = false;
   
	public JunctionDetectorPro(Gear pilot, DataStore ds, LightSensor leftSensor, LightSensor rightSensor) {
		this.pilot = pilot;
		//this.pilot.setSpeed(30);
		this.leftSensor = leftSensor;
		this.rightSensor = rightSensor;
		this.ds = ds;
		currentJunction = new Junction();   
//		currentJunction.edges[0] = Junction.type.UNEXPLORED;
//		currentJunction.edges[1] = Junction.type.UNEXPLORED;
//		ds.setJunction(gridX,gridY,currentJunction);
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
	   return leftSensor.getValue() < SIM_DARK_TRESHOLD;
   }

   private boolean isRightBlack() {
	   return rightSensor.getValue() < SIM_DARK_TRESHOLD;
   }
   
   private int heading = 0;
   private boolean isExploring = true;
   
   private boolean explorationDone = false;
   LinkedList<Integer> shortestPath;
   
   /* junction is observed */
   public void action() {
	   suppressed = false;
	   
	   if (explorationDone)
	   {
		   int newHeading = shortestPath.pop().intValue();
		   if (heading != newHeading)
			   rotateToNewHeading(heading,newHeading);
		   
		   heading = newHeading;
		   
		   pilot.stop();
	   }
	   
	   boolean encounteredBefore = false;
	   
	   if(!DataStore.currentlyBlocked){
		   pilot.forward(200); // 75 in real
		   switch(heading){
			   case 0: gridY++; break;
			   case 1: gridX++; break;
			   case 2: gridY--; break;
			   case 3: gridX--; break;
		   }
		   
		   Junction n = ds.getJunction(gridX, gridY);
		   
		   if (n == null){
			   System.out.println("---------------NEW JUNCTION--------------");
			   n = new Junction();
			   scoutJunctionNighbors(n);
		   }
		   else
			   encounteredBefore = true;
		   
		   currentJunction.neghbors[(heading + 0)%4] = n;
		   n.neghbors[(heading + 2)%4] = currentJunction;
		   currentJunction = n;
		   
	   }
	   else {
		   pilot.forward(100);
		   DataStore.currentlyBlocked = false;
		   System.out.println("Current heading: " + heading);
		   isExploring = false;
	   }
	   System.out.println("Currently at: " + gridX + ", " + gridY);

	   
	   int newHeading = getNextHeading(currentJunction, encounteredBefore);
	   
	   if(newHeading == -1){
		   explorationDone = true;
		   shortestPath = ds.getShortestPath();
		   rotateToNewHeading(heading,0);
		   System.out.println("--------------DONE EXPLORING--------------------");
		   pilot.stop();
		   return;
	   }
	   
	   System.out.println("Next heading: " + newHeading);
	   
	   if (heading != newHeading)
		   rotateToNewHeading(heading,newHeading);
	   
	   heading = newHeading;
	   
	   currentJunction.edges[heading] = Junction.type.EXPLORED;
	   
	   ds.setJunction(gridX, gridY, currentJunction);
	   System.out.println("----------------------------------");
	   
	   pilot.stop();
   }

	private int getNextHeading(Junction n, boolean encounteredBefore) {
		int count = 0;
		int origin = -1;
		for(int i = 0; i < 4; i++)
		{
			if(n.edges[i] == Junction.type.UNEXPLORED)
				count++;
			if(n.edges[i] == Junction.type.ORIGIN)
				origin = i;
		}
		
		if (encounteredBefore && isExploring) {
			isExploring = false;
			return (heading + 2) % 4;
		}
			
		System.out.println("Unexplored edges: "+ count);
		
		int rand = (int)(Math.random()*count);
		
		count = 0;
		for(int i = 0; i < 4; i++)
		{
			if(n.edges[i] != Junction.type.UNEXPLORED)
				continue;
			
			isExploring = true;
			
			if( count == rand)
				return i;
			else
				count++;
		}
		if (isExploring) {
			isExploring = false;
			return (heading + 2) % 4;
		}
		else {
			return origin;
		}
		
	}

	private void rotateToNewHeading(int heading, int newHeading) {

	   int rotationUnits = 30; // 2160 / 30 = 72 turns
	   int lastReading = pilot.getRotationIndex(), totalAngle = 0;
	   int differenceInHeading = newHeading - heading;
	   
	   String direction = differenceInHeading < 0 ? "left" : "right";
	   int maxAngle = Math.abs(differenceInHeading) * 90;
	   
	   System.out.println("Difference in heading: " + differenceInHeading);
	   System.out.println("Rotating " + direction + " : " + maxAngle);
	   
	   while (totalAngle < maxAngle) {
		   if(direction.equals("left"))
			   pilot.left(rotationUnits);
		   else
			   pilot.right(rotationUnits);

		   /* Only in simulation BEGIN*/
		   totalAngle += ((direction.equals("left") ? -1 : 1) * (pilot.getRotationIndex() - lastReading) + 360) % 360;
		   lastReading = pilot.getRotationIndex();
		   /* Only in simulation END*/
	   }	
	}
	
	private void scoutJunctionNighbors(Junction n) {
	   int currentRotation = 0, rotationUnits = 30; // 2160 / 30 = 72 turns
	   int sector = -1;
	   int lastReading = pilot.getRotationIndex(), totalAngle = 0;
	   while (totalAngle < 360) {		   
		   if (isLeftBlack() || isRightBlack()) {
			if (currentRotation >= TURN_90*sector-8*rotationUnits && currentRotation <= TURN_90*sector+8*rotationUnits) {
				   if (sector == 2)
					   n.edges[(heading + 2)%4] = Junction.type.ORIGIN;
				   else
					   n.edges[(heading + sector)%4] = Junction.type.UNEXPLORED;
			   }
		   }
		   				   		   
		   pilot.right(rotationUnits);
//		   Tools.delay(50);
		   
		   /* Only in simulation BEGIN*/
		   totalAngle += (pilot.getRotationIndex() - lastReading + 360) % 360;
		   lastReading = pilot.getRotationIndex();
		   /* Only in simulation END*/
		   
		   currentRotation += rotationUnits;
		   
		   if (currentRotation - (sector+1)*TURN_90 >= 0) {
			   sector++;
//			   System.out.println("Changed sector " + currentRotation);
		   }
	   }
	    int count = 0;
		for(int i = 0; i < 4; i++)
		{
			if(n.edges[i] == Junction.type.UNEXPLORED || n.edges[i] == Junction.type.ORIGIN)
				count++;
		}
		if(count == 2 && gridX != 0 && gridY != 0){
			System.out.println("Found target at: " + gridX + ", " + gridY);
			ds.targetX = gridX;
			ds.targetY = gridY;
		}
	   
	}
}