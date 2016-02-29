import ch.aplu.robotsim.Gear;
import ch.aplu.robotsim.LightSensor;
import lejos.robotics.subsumption.Behavior;

public class LineDetector implements Behavior {
	   
	   private Gear pilot;
	   private LightSensor leftSensor;
	   private LightSensor rightSensor;
	   	   
	   public LineDetector(Gear pilot,LightSensor leftSensor, LightSensor rightSensor) {
		   this.pilot = pilot;
		   this.leftSensor = leftSensor;
		   this.rightSensor = rightSensor;
	   }
	   
	   public boolean takeControl() {
		  return true;
	   }

	   public void suppress() {
	   }
	   	   
	   public void action() {
	     
		 System.out.println("LineDetector");
		   
		 int leftVal = leftSensor.getValue();
		 int rightVal = rightSensor.getValue();

		 if (rightVal < leftVal)
			 {
			 	pilot.left(30);
			 }
			 else
				if(leftVal > rightVal)
			 {			 	
				pilot.right(30);
			 }
		 pilot.forward(8);

		 Thread.yield();
		 
	   }
	}