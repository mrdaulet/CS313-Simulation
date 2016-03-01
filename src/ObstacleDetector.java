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
 
       private Graph junctionGraph;
       
       private final int CRITICAL_DISTANCE = 10;
       private final int FULL_ROTATION = 1240;
 
       
       public ObstacleDetector(DifferentialPilot pilot, UltrasonicSensor sonic, LightSensor lightSensor, Graph junctionGraph) {
           this.pilot = pilot;
           this.sonic = sonic;
           leftSensor = lightSensor;
           this.junctionGraph = junctionGraph;
       }
     
 
    public boolean takeControl() {
            int distance = sonic.getDistance();
           
            if(distance < CRITICAL_DISTANCE)
                return true;
            return false;
       }
 
       public void suppress() {
          suppressed = true;
       }
 
       public void action() {
           
         suppressed = false;
         int amountToRotate = -20;
         int leftVal = leftSensor.getNormalizedLightValue();
         
         boolean lineCrossed = true;
         
         while(leftVal > 500){
             pilot.rotate(amountToRotate);
             leftVal = leftSensor.getNormalizedLightValue();
         }
         
//       int amountRotated = 0;
         
//       int amountToRotate = 20;
//       
//       while(amountRotated < FULL_ROTATION){
//           pilot.rotate(amountToRotate);
//           amountRotated += amountToRotate;
//       }
         
       }
    }