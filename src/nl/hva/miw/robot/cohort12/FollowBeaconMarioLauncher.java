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

public class FollowBeaconMarioLauncher {
	private static final int ZERO = 0;
	private static final int DEVIATION = -2;
	private static final int MIN_DISTANCE = 15;
	private static final int MAX_DISTANCE = 100;

	EV3IRSensor infrared = new EV3IRSensor(SensorPort.S1);
	UnregulatedMotor left = new UnregulatedMotor(MotorPort.C);
	UnregulatedMotor right = new UnregulatedMotor(MotorPort.D);
	UnregulatedMotor claw = new UnregulatedMotor(MotorPort.A);
	RegulatedMotor head = new EV3MediumRegulatedMotor(MotorPort.B);

	int distance;
	Grip newGrip = new Grip();

	SensorMode seekBeacon = infrared.getSeekMode();
	float[] sample = new float[seekBeacon.sampleSize()];
	// Brick brick;
	// TextLCD display = brick.getTextLCD();
	// SensorMode distance = infrared.getDistanceMode();

	public static void main(String[] args) {
		System.out.println("Infrared Sensor\n");

		Button.LEDPattern(4); // flash green led and
		Sound.beepSequenceUp(); // make sound when ready.

		System.out.println("Initiating Follow Mario Beacon!");
		Button.waitForAnyPress();

		FollowBeaconMarioLauncher test = new FollowBeaconMarioLauncher();
		test.seekBeacon();

	}

	public void seekBeacon() {
		Mario newMario = new Mario();
		newMario.start();
		while (Button.ESCAPE.isUp() || (distance > MIN_DISTANCE && distance < MAX_DISTANCE)) {
			// reads bearing and distance every second
			seekBeacon.fetchSample(sample, 0);
			// one pair has 2 elements, in this case: bearing and distance
			int direction = (int) sample[0];
			System.out.println("Direction: " + direction);
			// display.drawString("Direction: " + direction, 0, 3);
			int distance = (int) sample[1];
			System.out.println("Distance: " + distance);

			// while () {
			// if beacon too far it will drive forward
			if (direction == 0 && distance >= 100) {
				// left.setPower(40);
				// right.setPower(40);
			}
			// move to the right
			else if (direction > DEVIATION) {
				// left.setPower(40);
				// right.setPower(-10);
				// gear will turn the head the opposite direction
				// head.setPower(10);
				// move to the left
			} else if (direction < DEVIATION) {
				// left.setPower(-10);
				// right.setPower(40);
				// gear will turn the head the opposite direction
				// head.setPower(-10);

				// if beacon is right in front of IR sensor, stop turning head and drive forward
			} else if (direction <= 0 && direction == DEVIATION) {
				// left.setPower(40);
				// right.setPower(40);
				// head.stop();
			}
			if (distance > ZERO && distance <= MIN_DISTANCE) {
				// left.stop();
				// right.stop();
				Sound.setVolume(0);
				System.out.println("I have found my beacon!");
				// thread.interrupt();
				// newGrip.closeGrip(claw);
				// newGrip.openGrip(claw);
// Comment out to try faster stop!
//				try {
//					Thread.sleep(100);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}

				// call stopRunning() method whenever you want to stop a thread
				newMario.setTimes(0);
				newMario.stopRunning();
				break;

			}

			// after checking direction sample value for the conditions continue to check
			// conditions for distance sample value
			// if not found keep on going forward until found
			// if found stop and initiate claw motor to pick up object
			// closest distance value is 1

		}

		//
		// if (distance == MIN_DISTANCE) {
		// left.stop();
		// right.stop();
		// Sound.beepSequenceUp();
		// newGrip.closeGrip(claw);
		// }

		// free motor and sensor resources
		left.close();
		right.close();
		infrared.close();
	}
}
