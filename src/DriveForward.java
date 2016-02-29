import ch.aplu.robotsim.Gear;
import lejos.robotics.subsumption.Behavior;

class DriveForward implements Behavior {
	   private boolean suppressed = false;
	   
	   private Gear pilot;
	   
	   public DriveForward(Gear pilot) {
		   this.pilot = pilot;
	   }
	   
	   public boolean takeControl() {
	      return true;
	   }

	   public void suppress() {
	      suppressed = true;
	   }

	   public void action() {
	     suppressed = false;

	     while( !suppressed ) {
	    	 pilot.forward(5);
	    	 Thread.yield(); 
	     }
	   }
	}