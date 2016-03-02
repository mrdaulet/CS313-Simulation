import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.LightSensor;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.subsumption.Behavior;
 
class ObstacleDetector implements Behavior {
	public class Graph {}
	
       private boolean suppressed = false;
       
       private DifferentialPilot pilot;
       private UltrasonicSensor sonic;
       private LightSensor leftSensor;
       
       private final int CRITICAL_DISTANCE = 7; //10 in real
       private final int FULL_ROTATION = 1240;
 
       
       public ObstacleDetector(DifferentialPilot pilot, UltrasonicSensor us, LightSensor lsLeft) {
           this.pilot = pilot;
           this.sonic = us;
           leftSensor = lsLeft;
       }
     
 
    public boolean takeControl() {
            int distance = sonic.getDistance();
//            System.out.println(distance);
            if (distance < CRITICAL_DISTANCE) // IRL
            	return true;
            return false;
       }
 
       public void suppress() {
          suppressed = true;
       }
 
       public void action() {
//    	   ds.currentDistance = 0;
         DataStore.currentlyBlocked = true;
         suppressed = false;
         System.out.println("WALL AHEAD");
         while( !suppressed ) {
        	 pilot.travel(-10);
        	 Thread.yield(); 
         }
//         int leftVal = leftSensor.getValue();
//         
//         boolean lineCrossed = true;
//         
//         while(leftVal > 500){
//             pilot.left(amountToRotate);
//             leftVal = leftSensor.getValue();
//         }
         
//       int amountRotated = 0;
         
//       int amountToRotate = 20;
//       
//       while(amountRotated < FULL_ROTATION){
//           pilot.rotate(amountToRotate);
//           amountRotated += amountToRotate;
//       }
         
       }
    }