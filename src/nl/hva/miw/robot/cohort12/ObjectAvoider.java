package nl.hva.miw.robot.cohort12;

import lejos.hardware.Brick;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.*;
import lejos.hardware.port.*;
import lejos.hardware.sensor.*;
import lejos.robotics.RegulatedMotor;
import lejos.utility.Delay;

/**
 * With (an instance of) this class, a robot can move forward while avoiding all
 * objects on its way. It's possible to set the amount of objects to be passed.
 * The robot will pass the first object at the left, the second at the right,
 * the third at the left, the fourth at the right, etc.
 * 
 * @author Bjorn Goos
 *
 */

public class ObjectAvoider {
	private static RegulatedMotor motorRight;
	private static RegulatedMotor motorLeft;
	private static RegulatedMotor motorOfHead;
	private static UnregulatedMotor motorOfGrip;
	private static ColorSensor colorSensor;
	private static EV3IRSensor infraRedSensor;
	private static final int LARGE_DISTANCE = 1000;
	private static final int SMALL_DISTANCE = 1;
	private static final int SMALLEST_DISTANCE_TO_OBJECT = 40;
	private static final int GO_CALMLY_FORWARD = 360;
	private static final int numberOfObjectsToBePassed = 2;

	/**
	 * Instantiate an object avoider
	 * 
	 * @param motorRight
	 *            The motor at the right of the robot
	 * @param motorLeft
	 *            The motor at the left of the robot
	 * @param motorOfHead
	 *            The motor that turns the 'head' of the robot, i.e. the place of
	 *            the robot where the IR-sensor is attached to
	 * @param motorOfGrip
	 *            The motor that makes the grip open and close
	 * @param colorSensor
	 *            The sensor that measures color values
	 * @param infraRedSensor
	 *            The sensor that measures distances (and 'angels' for the Beacon)
	 */

	public ObjectAvoider(RegulatedMotor motorRight, RegulatedMotor motorLeft, RegulatedMotor motorOfHead,
			UnregulatedMotor motorOfGrip, ColorSensor colorSensor, EV3IRSensor infraRedSensor) {
		super();
		this.motorRight = motorRight;
		this.motorLeft = motorLeft;
		this.motorOfHead = motorOfHead;
		this.motorOfGrip = motorOfGrip;
		this.colorSensor = colorSensor;
		this.infraRedSensor = infraRedSensor;
	}

	/**
	 * Method to start the object avoider. Place the robot in an 90 degree angle
	 * with respect to the first object for the best results. Also, make sure the
	 * 'head' of the robot is 'straight' (i.e. lined with the robot's moving
	 * direction).
	 */
	public void startObjectAvoider() {
		Sound.beepSequence();
		System.out.println("Press a key to start the Object Avoider");
		Button.waitForAnyPress();

		for (int numberOfObjectsPassed = 0; numberOfObjectsPassed < numberOfObjectsToBePassed; numberOfObjectsPassed++) {

			while (Button.ESCAPE.isUp()) {

				// Let the robot drive calmly towards the object, until the object is nearer
				// than the SMALLEST_DISTANCE_TO_OBJECT
				int measuredDistance = LARGE_DISTANCE;
				while (measuredDistance > SMALLEST_DISTANCE_TO_OBJECT) {
					keepCalmlyGoingForward();
				}

				// Robot faces away from the object, after which the head turns towards it, so
				// the distance to the object can still be measured
				if (numberOfObjectsPassed % 2 == 0) {
					robotTurns90DegreesTo("L");
					headTurns90DegreesTo("R");
				}
				// Every next object will be passed on the other side than the previous object.
				// Just for the fun of it, but also to make sure that the robot does not
				// 'depart' too much by passing every object at the same side
				if (numberOfObjectsPassed % 2 == 1) {
					robotTurns90DegreesTo("R");
					headTurns90DegreesTo("L");
				}

				// Now, the robot will drive more or less parallel to the object, until it can
				// no longer see it
				// There has to be enough space behind the object for the robot to pass the
				// follow-up object as well, hence the multiplication with two
				int followUpDistance = SMALL_DISTANCE;
				while (followUpDistance < 2 * SMALLEST_DISTANCE_TO_OBJECT) {
					keepCalmlyGoingForward();
				}

				// In case the robot is quite large, it has to drive a little bit extra, so the
				// whole robot can pass the object
				littleBitExtraForward();

				// Now the robot will turn again, so it can pass the object
				// Next, the head will turn as well, so it aims at the path of the robot again
				if (numberOfObjectsPassed % 2 == 0) {
					robotTurns90DegreesTo("R");
					headTurns90DegreesTo("L");
				}
				// Again, other way around for consecutively passed objects
				if (numberOfObjectsPassed % 2 == 0) {
					robotTurns90DegreesTo("L");
					headTurns90DegreesTo("R");
				}
			}
		}

		motorLeft.close();
		motorRight.close();
		motorOfHead.close();

	}

	public void robotTurns90DegreesTo(String direction) {
		int requiredMotorRotationFor90Degrees = 400; // negative for direction
		if (direction.equals("L")) {
			motorLeft.rotate(-requiredMotorRotationFor90Degrees, true);
			motorRight.rotate(requiredMotorRotationFor90Degrees, true);
			motorLeft.waitComplete();
			motorRight.waitComplete();
		}
		if (direction.equals("R")) {
			motorLeft.rotate(requiredMotorRotationFor90Degrees, true);
			motorRight.rotate(-requiredMotorRotationFor90Degrees, true);
			motorLeft.waitComplete();
			motorRight.waitComplete();
		}
	}

	public void headTurns90DegreesTo(String direction) {
		int motorRotationRequiredForHeadToMakeA90DegreesTurn = 65;
		if (direction.equals("L")) {
			motorOfHead.rotate(motorRotationRequiredForHeadToMakeA90DegreesTurn);
			motorOfHead.waitComplete();
		}
		if (direction.equals("R")) {
			motorOfHead.rotate(-motorRotationRequiredForHeadToMakeA90DegreesTurn);
			motorOfHead.waitComplete();
		}
	}

	private void keepCalmlyGoingForward() {
		SensorMode distance = infraRedSensor.getDistanceMode();
		float[] sample = new float[distance.sampleSize()];
		distance.fetchSample(sample, 0);
		int measuredDistance = (int) sample[0];
		System.out.println("Distance: " + measuredDistance);
		motorLeft.rotate(GO_CALMLY_FORWARD, true);
		motorRight.rotate(GO_CALMLY_FORWARD, true);
	}

	private void littleBitExtraForward() {
		int extraDistance = 700;
		motorLeft.rotate(extraDistance, true);
		motorRight.rotate(extraDistance, true);
		motorLeft.waitComplete();
		motorRight.waitComplete();
	}
}