import ch.aplu.robotsim.*;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;

import java.awt.Color;
import java.awt.Point;

import ch.aplu.jgamegrid.*;

public class GridBot 
{
  private Gear gear = new Gear();

  public GridBot()
  {
    LegoRobot robot = new LegoRobot();
    LightSensor lsLeft = new LightSensor(SensorPort.S1);
    LightSensor lsRight = new LightSensor(SensorPort.S2);
    TouchSensor us = new TouchSensor(SensorPort.S3);
    robot.addPart(gear);
    robot.addPart(lsLeft);
    robot.addPart(lsRight);
    robot.addPart(us);
	DataStore ds = new DataStore();
	
	Behavior b1 = new DriveForward(gear);
	Behavior b2 = new JunctionDetectorPro(gear,
									ds,
								  lsLeft,
			 					  lsRight);
	Behavior b3 = new ObstacleDetector(gear,us, lsLeft);
	
	Behavior [] bArray = {b1, b3, b2};
	Arbitrator arby = new Arbitrator(bArray);
	
	Tools.delay(200);
	arby.start();
  }

  public static void _init(GameGrid gg)
  {
	  gg.setBgImagePath("sprites/grid-cell.gif");
  }
  
  static {
	  	RobotContext.setStartPosition(47, 298);
	  	RobotContext.showNavigationBar();

//	    RobotContext.useObstacle("sprites/wall1.gif", 180, 260);
//	    RobotContext.useObstacle("sprites/wall2.gif", 75, 200);
  }

  public static void main(String[] args)
  {
    new GridBot();
  }
}