package nl.hva.miw.robot.cohort12;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.motor.UnregulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3IRSensor;
import lejos.hardware.sensor.SensorMode;
import lejos.robotics.RegulatedMotor;
import lejos.utility.Delay;

/**
 * This class is for implementing the code for the Infrared Sensor on the Lego
 * EV3 Mindstorms robot. The methods in this class will instruct the robot to
 * detect the beacon bearing and distance using the IR sensor in combination
 * with the beacon signal. Based on the values fetched the robot will turn and
 * drive towards the beacon and eventually, when in close range, stop in front
 * of the beacon and close the claw to pick up the object.
 * 
 * @author Huy
 *
 */
public class FollowBeaconLauncher {

	private static final int ZERO = 0;
	private static final int DEVIATION = -1;
	private static final int MIN_DISTANCE = 1;
	private static final int MAX_DISTANCE = 100;

	// Create objects for each class
	EV3IRSensor infrared = new EV3IRSensor(SensorPort.S1);
	RegulatedMotor left = new EV3LargeRegulatedMotor(MotorPort.C);
	RegulatedMotor right = new EV3LargeRegulatedMotor(MotorPort.D);
	UnregulatedMotor claw = new UnregulatedMotor(MotorPort.A);
	RegulatedMotor head = new EV3MediumRegulatedMotor(MotorPort.B);
	Mario newMario = new Mario();
	Grip newGrip = new Grip(claw);
	int distance;
	boolean ready = false; // 
	
	SensorMode seekBeacon = infrared.getSeekMode();
	float[] sample = new float[seekBeacon.sampleSize()];

	public static void main(String[] args) {
		System.out.println("Infrared Sensor\n");

		Button.LEDPattern(4); // flash green led and
		Sound.beepSequenceUp(); // make sound when ready.

		System.out.println("Initiating Follow Beacon!");
		Button.waitForAnyPress();

		FollowBeaconLauncher test = new FollowBeaconLauncher();
		test.seekBeacon();
	}

	public void seekBeacon() {
		newMario.start();
		
		while (!ready) {
			// reads bearing and distance every second
			seekBeacon.fetchSample(sample, 0);
			// one pair has 2 elements, in this case: bearing and distance
			int direction = (int) sample[0];
			System.out.println("Direction: " + direction);
			int distance = (int) sample[1];
			System.out.println("Distance: " + distance);

			// TOO FAR AWAY
			if (direction == 0 && distance >= 100) {
//				left.setS(40);
//				right.setPower(40);
//				Delay.msDelay(100);
			} 
			
			else {
				int goalSpeed = 150;
				double kP = 8;
				int error = direction - DEVIATION;
				int powerRight = (int) (goalSpeed + kP * error);
				int powerLeft = (int)(goalSpeed - kP * error);
				
				right.setSpeed(powerLeft);
				left.setSpeed(powerRight);
				right.forward();
				left.forward();
				if (direction > (DEVIATION -2) && direction < DEVIATION + 2) {
					Sound.setVolume(20);
					//Sound.beep();
					
					if (distance > 0 && distance <= MIN_DISTANCE) {
						right.rotate(800, true);
						left.rotate(800, true);
//						right.forward();
//						left.forward();
						Delay.msDelay(2000);
						left.stop();
						right.stop();
						Sound.setVolume(0);
						newMario.setTimes(0);
						newMario.stopRunning();
						Sound.beepSequenceUp();
						System.out.println("I have found my beacon!");
						newGrip.closeGrip();
//						drive.setPower(-100, 100);
//						Delay.msDelay(1600);
//						drive.stop();
						newGrip.openGrip();
						ready = true;
					}
						
				}
			}
		}

		// free motor and sensor resources
		left.close();
		right.close();
		infrared.close();
		head.close();
		claw.close();
	}
}
