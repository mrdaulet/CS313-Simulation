import ch.aplu.robotsim.Gear;
import lejos.robotics.navigation.DifferentialPilot;

public class PilotGear {
	Gear gear;
	DifferentialPilot pilot;
	boolean isSimulation;
	
	public PilotGear(Gear g) {
		gear = g;
		isSimulation = true;
	}

	public PilotGear(DifferentialPilot p) {
		pilot = p;
		isSimulation = false;
	}
	
	public void forward(int duration) {
		if (isSimulation) {
			gear.forward(duration);
		} else {
			pilot.travel(duration);
		}
	}
	
	public void rotate(int angle) {
		if (isSimulation) {
			if (angle > 0) {
				gear.right(angle);
			} else {
				gear.left(-angle);
			}
		} else {
			pilot.rotate(angle);
		}
	}
	
	public void stop() {
		if (isSimulation) {
			gear.stop();
		} else {
			pilot.stop();
		}
	}
	
	public int getRotationIndex() {
		if (!isSimulation) {
			System.out.println("Not possible!");
			return -1;
		} else {
			return gear.getRotationIndex();
		}
	}
}
