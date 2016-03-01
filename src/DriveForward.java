import lejos.robotics.subsumption.Behavior;

class DriveForward implements Behavior {
   private boolean suppressed = false;
   
   private PilotGear pilot;
   
   public DriveForward(PilotGear pilot) {
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
    	 pilot.forward(30);
//    	 Tools.delay(50);
    	 Thread.yield(); 
     }
   }
}