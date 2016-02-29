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
    
	RobotContext.setStartPosition(0, 0);
        
	Behavior b1 = new DriveForward(gear);
	Behavior b2 = new JunctionDetectorPro(gear,
								  lsLeft,
			 					  lsRight);
	Behavior b3 = new LineDetector(gear, lsLeft, lsRight);
	
	Behavior [] bArray = {b1, b2};
	Arbitrator arby = new Arbitrator(bArray);
	arby.start();
  }

  public static void _init(GameGrid gg)
  {
	  gg.setSimulationPeriod(100);
	  gg.setBgImagePath("/Users/Daulet/Documents/Course/cs313/Test/sprites/grid4.gif");
  }
  
  static {
	  RobotContext.showNavigationBar();
	  RobotContext.setStartPosition(45, 270);
  }

  public static void main(String[] args)
  {
    new GridBot();
  }
}