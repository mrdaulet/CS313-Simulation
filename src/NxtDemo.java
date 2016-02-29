// NxtDemo.java

import ch.aplu.nxt.*;

public class NxtDemo
{
  public NxtDemo()
  {
    // Ask for the name, create robot and connect
    LegoRobot robot = new LegoRobot();

    // Create two motors
    Motor motA = new Motor(MotorPort.A);
    Motor motB = new Motor(MotorPort.B);

    // Assemble motors into robot
    robot.addPart(motA);
    robot.addPart(motB);

    // Use motors
    motA.forward();
    motB.forward();

    // Wait a moment
    Tools.delay(5000);

    // Disconnect and terminate
    robot.exit();
  }

  public static void main(String[] args)
  {
    new NxtDemo();
  }
}