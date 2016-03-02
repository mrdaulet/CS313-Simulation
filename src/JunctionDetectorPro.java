import java.util.ArrayList;
import java.util.LinkedList;

import lejos.nxt.LightSensor;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.subsumption.Behavior;

public class JunctionDetectorPro implements Behavior {

	private DifferentialPilot pilot;
	private LightSensor leftSensor;
	private LightSensor rightSensor;
	private DataStore ds;
	
	private int gridX = 0;
	private int gridY = -1;
	private Junction currentJunction;	
	
	private int L_ABS_DARK_TRESHOLD = 530;
	private int R_ABS_DARK_TRESHOLD = 530;
	   
	private final int FULL_360_TURN = 2550; 
	private final int TURN_90 = FULL_360_TURN/4;

	private boolean suppressed = false;
   
	private ArrayList<Integer> distanceTravelled = new ArrayList<Integer>();
	
	public JunctionDetectorPro(DifferentialPilot pilot, DataStore ds, LightSensor leftSensor, LightSensor rightSensor) {
		this.pilot = pilot;
		this.pilot.setTravelSpeed(40);
		this.pilot.setRotateSpeed(900);
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
	   return leftSensor.getNormalizedLightValue() < L_ABS_DARK_TRESHOLD;
   }

   private boolean isRightBlack() {
	   return rightSensor.getNormalizedLightValue() < R_ABS_DARK_TRESHOLD;
   }
   
   private int heading = 0;
   private boolean isExploring = true;
   
   private boolean explorationDone = false;
   LinkedList<Integer> shortestPath;
   
   /* junction is observed */
   public void action() {
	   suppressed = false;
	   
	   ds.currentDistance = 0;
	   ds.junctionCnt++;
	   
	   if (explorationDone)
	   {
		   int newHeading = (shortestPath.remove(shortestPath.size() - 1)).intValue();
		   if (heading != newHeading)
			   rotateToNewHeading(heading,newHeading);
		   
		   heading = newHeading;
		   
		   pilot.stop();
	   }
	   
	   boolean encounteredBefore = false;
	   
	   if(!DataStore.currentlyBlocked){
		   pilot.travel(75); // 75 in real
		   switch(heading){
			   case 0: gridY++; break;
			   case 1: gridX++; break;
			   case 2: gridY--; break;
			   case 3: gridX--; break;
		   }
		   
		   Junction n = ds.getJunction(gridX, gridY);
		   
		   if (n == null){
			   System.out.println("--NEW JUNCTION--");
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
		   pilot.travel(50);
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
		   System.out.println("---DONE EXPLORING---");
		   pilot.stop();
		   return;
	   }
	   
	   System.out.println("Next heading: " + newHeading);
	   
	   if (heading != newHeading)
		   rotateToNewHeading(heading,newHeading);
	   
	   heading = newHeading;
	   
	   currentJunction.edges[heading] = Junction.type.EXPLORED;
	   
	   ds.setJunction(gridX, gridY, currentJunction);
	   
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

	   int rotationUnits = 15; // 2160 / 30 = 72 turns
	   int differenceInHeading = newHeading - heading;
	   
	   if (Math.abs(differenceInHeading) > 2) {
		   System.out.println(differenceInHeading + " " + (2 - differenceInHeading) % 4);
		   differenceInHeading = (2 - differenceInHeading) % 4;
	   }
	   
	   
	   String direction = differenceInHeading < 0 ? "left" : "right";
	   int maxAngle = (int) ((double) (Math.abs(differenceInHeading) * 90) / 360 * FULL_360_TURN);
	   
	   System.out.println("Difference in heading: " + differenceInHeading);
	   System.out.println("Rotating " + direction + " : " + maxAngle);
	   
	   int totalAngle = 0;
	   while (totalAngle < maxAngle) {
		   if(direction.equals("left"))
			   pilot.rotate(-rotationUnits);
		   else
			   pilot.rotate(rotationUnits);

		   /* Only in simulation BEGIN*/
		   totalAngle += rotationUnits;
		   /* Only in simulation END*/
	   }	
	}
	
	private void scoutJunctionNighbors(Junction n) {
	   int currentRotation = 0, rotationUnits = 15; // 2160 / 30 = 72 turns
	   int sector = -1;
	   int totalAngle = 0;
	   while (totalAngle < FULL_360_TURN) {		   
		   if (isLeftBlack() || isRightBlack()) {
			if (currentRotation >= TURN_90*sector-8*rotationUnits && currentRotation <= TURN_90*sector+8*rotationUnits) {
				   if (sector == 2)
					   n.edges[(heading + 2)%4] = Junction.type.ORIGIN;
				   else
					   n.edges[(heading + sector)%4] = Junction.type.UNEXPLORED;
			   }
		   }
		   				   		   
		   pilot.rotate(rotationUnits);
//		   Tools.delay(50);
		   
		   /* Only in simulation BEGIN*/
		   totalAngle += rotationUnits;
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