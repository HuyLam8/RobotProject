package nl.hva.miw.robot.cohort12;

import lejos.hardware.Button;
import lejos.hardware.Sound;
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
 * of the beacon.
 * 
 * @author Huy
 *
 */
public class FollowBeaconLauncher {

	private static final int ZERO = 0;
	private static final int DEVIATION = -3;
	private static final int MIN_DISTANCE = 15;
	private static final int MAX_DISTANCE = 100;

	// Create objects for each class
	EV3IRSensor infrared = new EV3IRSensor(SensorPort.S1);
	UnregulatedMotor left = new UnregulatedMotor(MotorPort.C);
	UnregulatedMotor right = new UnregulatedMotor(MotorPort.D);
	UnregulatedMotor claw = new UnregulatedMotor(MotorPort.A);
	RegulatedMotor head = new EV3MediumRegulatedMotor(MotorPort.B);
	Mario newMario = new Mario();
	Grip newGrip = new Grip(claw);
	int distance;
	ControlDrive drive = new ControlDrive(right, left);
	boolean klaar = false;
	
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
		
		while (!klaar) {
			// reads bearing and distance every second
			seekBeacon.fetchSample(sample, 0);
			// one pair has 2 elements, in this case: bearing and distance
			int direction = (int) sample[0];
			System.out.println("Direction: " + direction);
			int distance = (int) sample[1];
			System.out.println("Distance: " + distance);

			// TOO FAR AWAY
			if (direction == 0 && distance >= 100) {
				left.setPower(40);
				right.setPower(40);
				Delay.msDelay(1000);
			} 
			
			else {
				int goalSpeed = 30;
				double kP = 1.5;
				int error = direction - DEVIATION;
				int powerRight = (int) (goalSpeed + kP * error);
				int powerLeft = (int)(goalSpeed - kP * error);
				right.setPower(powerLeft);
				left.setPower(powerRight);
				if (direction > (DEVIATION -2) && direction < DEVIATION + 2) {
					Sound.setVolume(20);
					//Sound.beep();
					
					if (distance > 0 && distance < MIN_DISTANCE) {
						drive.setPower(0, 0);
						Sound.setVolume(0);
						newMario.setTimes(0);
						newMario.stopRunning();
						Sound.beepSequenceUp();
						System.out.println("I have found my beacon!");
						newGrip.closeGrip();
						drive.setPower(-100, 100);
						Delay.msDelay(1600);
						drive.stop();
						newGrip.openGrip();
						klaar = true;
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
