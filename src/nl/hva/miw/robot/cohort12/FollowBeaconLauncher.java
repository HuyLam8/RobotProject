package nl.hva.miw.robot.cohort12;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.motor.UnregulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3IRSensor;
import lejos.hardware.sensor.SensorMode;

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

	private static final int MIN_DISTANCE = 5;
	private static final int MAX_DISTANCE = 100;

	EV3IRSensor infrared = new EV3IRSensor(SensorPort.S1);
	UnregulatedMotor left = new UnregulatedMotor(MotorPort.C);
	UnregulatedMotor right = new UnregulatedMotor(MotorPort.D);
	UnregulatedMotor claw = new UnregulatedMotor(MotorPort.A);
	Grip newGrip = new Grip();
	// UnregulatedMotor head = new UnregulatedMotor(MotorPort.B);

	SensorMode seekBeacon = infrared.getSeekMode();
	float[] sample = new float[seekBeacon.sampleSize()];
	// Brick brick;
	// TextLCD display = brick.getTextLCD();
	// SensorMode distance = infrared.getDistanceMode();

	public static void main(String[] args) {
		System.out.println("Infrared Sensor\n");

		Button.LEDPattern(4); // flash green led and
		Sound.beepSequenceUp(); // make sound when ready.

		System.out.println("Press any key to start");
		Button.waitForAnyPress();

		FollowBeaconLauncher test = new FollowBeaconLauncher();
		test.seekBeacon();
	}

	public void seekBeacon() {

		while (Button.ESCAPE.isUp()) {
			// reads bearing and distance every second
			seekBeacon.fetchSample(sample, 0);
			// one pair has 2 elements, in this case: bearing and distance
			int direction = (int) sample[0];
			System.out.println("Direction: " + direction);
			// display.drawString("Direction: " + direction, 0, 3);
			int distance = (int) sample[1];
			System.out.println("Distance: " + distance);

			// move to the right
			if (direction > 0) {
				left.setPower(40);
				right.setPower(-10);
				// head.setPower(40);

				// move to the left
			} else if (direction < 0) {
				left.setPower(-10);
				right.setPower(40);
				// head.setPower(40);

				// if beacon is right in front of the IR sensor stop turning
			} else if (direction == 0) {
				left.setPower(0);
				right.setPower(0);
				// head.setPower(0);

				// after checking direction sample value for the conditions continue to check
				// conditions for distance sample value
			} else {
				// if not found keep on going forward until found
				if (distance > MIN_DISTANCE || distance < MAX_DISTANCE) {
					System.out.println("I have found my beacon!");
					Sound.beepSequenceUp();
					left.setPower(40);
					right.setPower(40);

					// if found stop and initiate claw motor to pick up object
					// closest distance value is 1
				} else {
					left.stop();
					right.stop();
					newGrip.closeGrip(claw);
				}
			}
		}
		// free motor and sensor resources
		left.close();
		right.close();
		infrared.close();
	}
}