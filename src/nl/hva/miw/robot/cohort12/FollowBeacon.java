package nl.hva.miw.robot.cohort12;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.motor.UnregulatedMotor;
import lejos.hardware.sensor.EV3IRSensor;
import lejos.hardware.sensor.SensorMode;
import lejos.robotics.RegulatedMotor;
import lejos.utility.Delay;

/**
 * This class is for implementing the code for the Infrared Sensor on the Lego
 * EV3 Mindstorms robot. The methods in this class will instruct the robot to
 * detect the beacon bearing (direction) and distance using the IR sensor in
 * combination with the beacon signal. Based on the values/samples fetched the
 * robot will turn and drive towards the beacon and eventually, when in close
 * range, stop in front of the beacon and close the claw to pick up an object.
 * 
 * @author Huy
 *
 */
public class FollowBeacon {

	private static final int ZERO = 0;

	// when the beacon is right in front of the IR sensor there is a slight
	// deviation in the bearing
	private static final int DEVIATION = -1;

	// when the beacon has been detected within these 2 bearing (direction) values,
	// it will be considered as in the center of the IR sensor
	private static final int MAX_BEARING_LEFT = -3;
	private static final int MAX_BEARING_RIGHT = 1;

	private static final int MIN_DISTANCE = 1; // distance is not to scale, the closer to 1 the closer the beacon is to
												// the sensor
	private static final int MAX_DISTANCE = 100; // 100 indicates that the beacon is out of range or behind the robot
	private static final int KP = 8; // the higher the kP the faster the robot will turn (turning correction) towards
										// the beacon once it has been found
	private static final int GOAL_SPEED = 150; // the speed that you want to achieve with the robot

//	private static final int SPEED_OF_OPENING_AND_CLOSING = 40; // speed of opening and closing the grip of the robot
//	private static final int REQUIRED_TIME_OF_OPENING_AND_CLOSING = 3000; // delay time for the process of closing and
//																			// opening the grip

	private static RegulatedMotor motorRight;
	private static RegulatedMotor motorLeft;
	private static UnregulatedMotor motorOfGrip;
	private static Grip newGrip;
	private static EV3IRSensor infraredSensor;
	private static Mario newMario;

	// constructor for the FollowBeacon object
	public FollowBeacon(RegulatedMotor motorRight, RegulatedMotor motorLeft, UnregulatedMotor motorOfGrip, Grip newGrip,
			EV3IRSensor infraRedSensor, Mario newMario) {
		super();
		FollowBeacon.motorRight = motorRight;
		FollowBeacon.motorLeft = motorLeft;
		FollowBeacon.motorOfGrip = motorOfGrip;
		FollowBeacon.newGrip = newGrip;
		FollowBeacon.infraredSensor = infraRedSensor;
		FollowBeacon.newMario = newMario;

	}

	public void run() {
		seekBeacon();
		// free motor and sensor resources
		motorLeft.close();
		motorRight.close();
		motorOfGrip.close();
		infraredSensor.close();
	}

	public void seekBeacon() {
		// initiates sensormode = "seek beacon mode" of the infrared sensor
		SensorMode seekBeacon = infraredSensor.getSeekMode();
		// creates an aray to put in the fetched samples for bearing (direction) and
		// distance
		float[] sample = new float[seekBeacon.sampleSize()];

		// set boolean "ready" on false so the while loop will run as long as "ready" is
		// not true
		boolean ready = false;

		System.out.println("Infrared Sensor\n");

		Button.LEDPattern(4); // flash green led and
		Sound.beepSequenceUp(); // make sound when ready.

		System.out.println("Initiating Follow Beacon!");
		Button.waitForAnyPress(); // wait for the user to press any button
		
		// start playing the Super Mario tune/theme song
		newMario.start();

		while (!ready) {
			// reads bearing (direction) and distance every second
			seekBeacon.fetchSample(sample, 0);
			// one pair has 2 elements, in this case: bearing (direction) and distance
			int bearing = (int) sample[0];
			System.out.println("Direction: " + bearing);
			int distance = (int) sample[1];
			System.out.println("Distance: " + distance);

			// direction 0 and distance 100 indicates that the beacon is either out of range
			// or behind the robot
			if (bearing == ZERO && distance >= MAX_DISTANCE) {
				// robot does nothing when the fetched samples meet this condition
			} else {
				// parameters for the P-controller
				int goalSpeed = GOAL_SPEED;
				double kP = KP;
				int error = bearing - DEVIATION;
				int powerRight = (int) (goalSpeed + kP * error); // turn speed for the right motor
				int powerLeft = (int) (goalSpeed - kP * error); // turn speed for the left motor

				// setting the speed for the motors by using the P-controller output
				motorRight.setSpeed(powerLeft);
				motorLeft.setSpeed(powerRight);

				// instructing both motors to move forward
				motorRight.forward();
				motorLeft.forward();

				// if the bearing value is between the bearing boundaries, it's technically in
				// the
				// center of the IR sensor, the Mario tune will stop playing by setting the
				// volume on 0
				// and the robot will continue to drive forward for a short distance and
				// eventually close the grip
				// to pick up an object
				if ((bearing > MAX_BEARING_LEFT && bearing < MAX_BEARING_RIGHT)
						&& (distance > ZERO && distance <= MIN_DISTANCE)) {
					// drive forward for a short distance
					motorRight.rotate(1000, true);
					motorLeft.rotate(1000, true);
					Delay.msDelay(2000);
					motorLeft.stop();
					motorRight.stop();

					// stop playing the Mario tune
					Sound.setVolume(ZERO);
					newMario.setTimes(ZERO);
					newMario.stopRunning();

					// sound to indicate that the robot has found the beacon
					Sound.beepSequenceUp();
					System.out.println("I have found my beacon!");

					// close the grip to pick up an object
					newGrip.closeGrip();
					motorRight.setSpeed(700);
					motorLeft.setSpeed(700);
					motorRight.rotate(-1000, true);
					motorLeft.rotate(1000, true);
					motorRight.waitComplete();
					motorLeft.waitComplete();
					motorRight.setSpeed(700);
					motorLeft.setSpeed(700);
					motorRight.rotate(2000, true);
					motorLeft.rotate(2000, true);
					motorRight.waitComplete();
					motorLeft.waitComplete();			
					motorRight.stop();
					motorLeft.stop();
					newGrip.openGrip();
//					motorOfGrip.backward();
//					motorOfGrip.setPower(SPEED_OF_OPENING_AND_CLOSING);
//					Delay.msDelay(REQUIRED_TIME_OF_OPENING_AND_CLOSING);
					// the robot is now ready so ready = true
					ready = true;
				}
			}
		}
	}
}
