
import ch.aplu.robotsim.Gear;
import ch.aplu.robotsim.UltrasonicSensor;
import lejos.nxt.Button;
import lejos.nxt.LCD;
import ch.aplu.robotsim.LightSensor;
import ch.aplu.robotsim.TouchSensor;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.subsumption.Behavior;
 
class ObstacleDetector implements Behavior {
	public class Graph {}
	
       private boolean suppressed = false;
       
       private Gear pilot;
       private TouchSensor sonic;
       private LightSensor leftSensor;
       
       private final int CRITICAL_DISTANCE = 2; //10 in real
       private final int FULL_ROTATION = 1240;
 
       
       public ObstacleDetector(Gear pilot, TouchSensor us, LightSensor lsLeft) {
           this.pilot = pilot;
           this.sonic = us;
           leftSensor = lsLeft;
       }
     
 
    public boolean takeControl() {
//            int distance = sonic.getDistance();
            //if (distance < CRITICAL_DISTANCE) // IRL
            if (sonic.isPressed())
            	return true;
            return false;
       }
 
       public void suppress() {
          suppressed = true;
       }
 
       public void action() {
         DataStore.currentlyBlocked = true;
         suppressed = false;
         System.out.println("WALL AHEAD");
         while( !suppressed ) {
        	 pilot.backward(30);
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