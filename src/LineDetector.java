import ch.aplu.robotsim.Gear;
import ch.aplu.robotsim.LightSensor;
import lejos.robotics.subsumption.Behavior;

public class LineDetector implements Behavior {
	   
	private PilotGear pilot;
	private LightSensor leftSensor;
	private LightSensor rightSensor;
	private boolean suppressed = false;
	   	   
	public LineDetector(PilotGear pilot,LightSensor leftSensor, LightSensor rightSensor) {
		this.pilot = pilot;
		this.leftSensor = leftSensor;
		this.rightSensor = rightSensor;
	}
	   
	   public boolean takeControl() {
		  return true;
	   }

	   public void suppress() {
		   suppressed = true;
	   }
	   	   
	   public void action() {
	     suppressed = false;
	     
		 System.out.println("LineDetector");
		   
		 int leftVal = leftSensor.getValue();
		 int rightVal = rightSensor.getValue();

		 if (rightVal < leftVal) {
			 pilot.rotate(30);
		 } else
		 if(leftVal > rightVal) {			 	
			 pilot.rotate(-30);
		 }
		 pilot.forward(8);
	   }
	}