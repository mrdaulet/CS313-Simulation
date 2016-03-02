import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.subsumption.Behavior;

class DriveForward implements Behavior {
   private boolean suppressed = false;
   
   private DifferentialPilot pilot;
   private DataStore dataStore;
   
   public DriveForward(DifferentialPilot pilot, DataStore ds) {
	   this.pilot = pilot;
	   this.dataStore = ds;
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
    	 
    	 int distance = 10;
    	 pilot.travel(distance);
    	 dataStore.currentDistance += distance;
    	 
//    	 Tools.delay(50);
    	 Thread.yield(); 
     }
   }
}