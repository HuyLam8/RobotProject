package nl.hva.miw.robot.cohort12;

import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.robotics.RegulatedMotor;

/**
 * Subclass for thread to run a separate while loop for the head scanner
 * movement
 * 
 * @author Huy
 *
 */

public class HeadBeaconScanner extends FollowBeaconLauncher implements Runnable {

	public void run() {
		boolean running = true;
		RegulatedMotor newHead = new EV3MediumRegulatedMotor(MotorPort.B);
		try {
			System.out.println("Running head beacon scanner");
			while (running) {
				newHead.rotateTo(-45);
				newHead.rotateTo(90);
				Thread.sleep(500);
			}
		} catch (Exception e) {
		} 	
		finally {
			newHead.close();
		}
	}
}
