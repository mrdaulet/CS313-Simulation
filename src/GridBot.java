import ch.aplu.robotsim.*;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;
import ch.aplu.jgamegrid.*;

public class GridBot 
{
  private Gear gear = new Gear();

  public GridBot()
  {
    LegoRobot robot = new LegoRobot();
    LightSensor lsLeft = new LightSensor(SensorPort.S2);
    LightSensor lsRight = new LightSensor(SensorPort.S1);
    robot.addPart(gear);
    robot.addPart(lsLeft);
    robot.addPart(lsRight);
    
    PilotGear pilot = new PilotGear(gear);
    DataStore ds = new DataStore();
	
	Behavior b1 = new DriveForward(pilot);
	Behavior b2 = new JunctionDetectorPro(pilot, ds, lsLeft, lsRight);
	Behavior b3 = new LineDetector(pilot, lsLeft, lsRight);
	
	Behavior [] bArray = {b1, b3, b2};
	Arbitrator arby = new Arbitrator(bArray);
	
	Tools.delay(200);
	arby.start();
  }

  public static void _init(GameGrid gg)
  {
	  gg.setBgImagePath("sprites/grid5.gif");
  }
  
  static {
	  RobotContext.setStartPosition(47, 70);
//	  RobotContext.setStartPosition(47, 270);
  }

  public static void main(String[] args)
  {
    new GridBot();
  }
}