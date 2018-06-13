package nl.hva.miw.robot.cohort12;

import java.util.ArrayList;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.motor.*;
import lejos.hardware.sensor.*;
import lejos.robotics.RegulatedMotor;
import lejos.utility.Delay;

/**
 * With (an instance of) this class, a robot can move forward while avoiding all
 * objects on its way. It's possible to set the amount of objects to be passed
 * by the robot. The robot will pass the first object at the left, the second at
 * the right, the third at the left, the fourth at the right, etc.
 * 
 * @author Bjorn Goos
 *
 */

public class AvoiderDieWerkt {
	private static RegulatedMotor motorRight;
	private static RegulatedMotor motorLeft;
	private static RegulatedMotor motorOfHead;
	private static UnregulatedMotor motorOfGrip;
	private static ColorSensor colorSensor;
	private static EV3IRSensor infraRedSensor;
	private static Grip balletjeGrijper = new Grip();
	private static final int UP_TO_OBJECT = 1000;
	private static final int UNTIL_OBJECT_IS_PASSED = 1;
	// The distance number translates differently to an actual distance (in
	// centimetres or inches) for each sensor
	private static final int SMALLEST_DISTANCE_TO_OBJECT = 40; // 55
	private static final int GO_CALMLY_FORWARD = 360;
	private static final int REASONABLE_SPEED = 500;
	private static int numberOfObjectsToBePassed = 2;
	private static ArrayList<Integer> listWithTachoMeasurements = new ArrayList<>();

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
	 *            The sensor that measures distances (and 'angles' for the Beacon)
	 */
	public AvoiderDieWerkt(RegulatedMotor motorRight, RegulatedMotor motorLeft, RegulatedMotor motorOfHead,
			UnregulatedMotor motorOfGrip, ColorSensor colorSensor, EV3IRSensor infraRedSensor) {
		super();
		AvoiderDieWerkt.motorRight = motorRight;
		AvoiderDieWerkt.motorLeft = motorLeft;
		AvoiderDieWerkt.motorOfHead = motorOfHead;
		AvoiderDieWerkt.motorOfGrip = motorOfGrip;
		AvoiderDieWerkt.colorSensor = colorSensor;
		AvoiderDieWerkt.infraRedSensor = infraRedSensor;
	}

	public AvoiderDieWerkt(RegulatedMotor motorRight, RegulatedMotor motorLeft, RegulatedMotor motorOfHead,
			UnregulatedMotor motorOfGrip, EV3IRSensor infraRedSensor) {
		this(motorRight, motorLeft, motorOfHead, motorOfGrip, null, infraRedSensor);
	}

	public AvoiderDieWerkt() {
		this(null, null, null, null, null, null);
	}

	public void useObjectAvoider(RegulatedMotor motorRight, RegulatedMotor motorLeft, RegulatedMotor motorOfHead,
			UnregulatedMotor motorOfGrip, ColorSensor colorSensor, EV3IRSensor infraRedSensor) {
		AvoiderDieWerkt.motorRight = motorRight;
		AvoiderDieWerkt.motorLeft = motorLeft;
		AvoiderDieWerkt.motorOfHead = motorOfHead;
		AvoiderDieWerkt.motorOfGrip = motorOfGrip;
		AvoiderDieWerkt.colorSensor = colorSensor;
		AvoiderDieWerkt.infraRedSensor = infraRedSensor;
	}

	/**
	 * Method to start the object avoider. Place the robot in an 90 degree angle
	 * with respect to the first object for the best results. Also, make sure the
	 * 'head' of the robot is 'straight' (i.e. lined with the robot's moving
	 * direction).
	 */
	public void avoidOpponentsAndScoreGoalFollowedByReplay() {
		// Start every run with an empty list of tacho measurements
		listWithTachoMeasurements.clear();

		// Flash green light and make sound when ready, then wait for press
		Button.LEDPattern(4);
		Sound.beepSequenceUp();
		System.out.println("Initiating Follow Beacon!");
		Button.waitForAnyPress();

		// Call the three relevant submethods
		startObjectAvoider();
		scoreAGoal();
		comeBack(listWithTachoMeasurements);

		// Close all resources
		motorLeft.close();
		motorRight.close();
		motorOfHead.close();
		infraRedSensor.close();
		motorOfGrip.close();
	}

	public void startObjectAvoider() {
		int numberOfObjectsPassed = 0;
		while (numberOfObjectsPassed < numberOfObjectsToBePassed && Button.ESCAPE.isUp()) {
			// Let the robot drive calmly towards the object, until the object is nearer
			// than the SMALLEST_DISTANCE_TO_OBJECT
			keepCalmlyGoingForward(UP_TO_OBJECT);

			// Robot faces away from the object, after which the head turns towards it, so
			// the distance to the object can still be measured
			// Every next object will be passed on the other side than the previous object.
			// Just for the fun of it, but also to make sure that the robot does not
			// 'depart' too much by passing every object at the same side
			if (numberOfObjectsPassed % 2 == 0) {
				robotTurns90DegreesTo("L");
				headTurns90DegreesTo("R");
			}
			if (numberOfObjectsPassed % 2 == 1) {
				robotTurns90DegreesTo("R");
				headTurns90DegreesTo("L");
			}

			// Now, the robot will drive more or less parallel to the object, until it can
			// no longer see it
			keepCalmlyGoingForward(UNTIL_OBJECT_IS_PASSED);

			// In case the robot is quite large, it has to drive a little bit extra, so the
			// whole robot can pass the object
			littleBitExtraForward();

			// Now the robot will turn again, so it can pass the object
			// Next, the head will turn as well, so it aims at the path of the robot again
			if (numberOfObjectsPassed % 2 == 0) {
				robotTurns90DegreesTo("R");
				headTurns90DegreesTo("L");
			}
			if (numberOfObjectsPassed % 2 == 1) {
				robotTurns90DegreesTo("L");
				headTurns90DegreesTo("R");
			}

			numberOfObjectsPassed++;
		}
	}

	public void playWithMarvin(long forHowLong) {
		// Start every run with an empty list of tacho measurements
		listWithTachoMeasurements.clear();

		// Flash green light and make sound when ready, then wait for press
		Button.LEDPattern(4);
		Sound.beepSequenceUp();
		System.out.println("Initiating Follow Beacon!");
		Button.waitForAnyPress();

		// The robot goes at relatively fast speed, i.e. not slow but not fast either
		motorLeft.setSpeed(REASONABLE_SPEED);
		motorRight.setSpeed(REASONABLE_SPEED);

		// Determine the end time by the start time and the requested forHowLong-time
		long startTime = System.currentTimeMillis();
		long endTime = startTime + forHowLong;
		while (System.currentTimeMillis() < endTime) {
			keepCalmlyGoingForward(UP_TO_OBJECT);
			robotTurns90DegreesTo("L");
		}

		// Robot makes some noises when the playing time (forHowLong) is over
		Sound.beepSequence();
		Sound.twoBeeps();

		comeBackAfterPlaying(listWithTachoMeasurements);

		// Close all resources
		motorLeft.close();
		motorRight.close();
		motorOfHead.close();
		infraRedSensor.close();
	}

	/**
	 * Test whether a specified amount of time has passed
	 * 
	 * @param endTime
	 *            The ending time of the period
	 * @return True or false, indicating whether the time has passed or not
	 */
	public static boolean allTheTimeHasPassed(long endTime) {
		return (System.currentTimeMillis() > endTime);
	}

	/**
	 * Let the robot make a turn of 'exactly' 90 degrees.
	 * 
	 * @param direction
	 *            Either right or left, identified by "L" resp. "R"
	 */
	public static void robotTurns90DegreesTo(String direction) {
		// The required rotation for a 90 degrees turn is different for each robot
		final int REQUIRED_ROTATION_FOR_90_DEGREES_TURN = 400;
		if (direction.equals("L")) {
			motorLeft.rotate(-REQUIRED_ROTATION_FOR_90_DEGREES_TURN, true);
			motorRight.rotate(REQUIRED_ROTATION_FOR_90_DEGREES_TURN, true);
			motorLeft.waitComplete();
			motorRight.waitComplete();
		}
		if (direction.equals("R")) {
			motorLeft.rotate(REQUIRED_ROTATION_FOR_90_DEGREES_TURN, true);
			motorRight.rotate(-REQUIRED_ROTATION_FOR_90_DEGREES_TURN, true);
			motorLeft.waitComplete();
			motorRight.waitComplete();
		}
	}

	/**
	 * Let only the head of the robot make a turn of 'exactly' 90 degrees
	 * 
	 * @param direction
	 *            Either right or left, identified by "L" resp. "R"
	 */
	public static void headTurns90DegreesTo(String direction) {
		// The required rotation for a 90 degrees turn is different for each head of a
		// robot
		final int REQUIRED_ROTATION_OF_HEAD_FOR_90_DEGREES_TURN = 65;
		if (direction.equals("L")) {
			motorOfHead.rotate(REQUIRED_ROTATION_OF_HEAD_FOR_90_DEGREES_TURN, true);
			motorOfHead.waitComplete();
		}
		if (direction.equals("R")) {
			motorOfHead.rotate(-REQUIRED_ROTATION_OF_HEAD_FOR_90_DEGREES_TURN, true);
			motorOfHead.waitComplete();
		}
	}

	/**
	 * This method can be used for both heading to an object and stopping before the
	 * SMALLEST_DISTANCE_TO_OBJECT has reached as well as for passing an object as
	 * long as the robot is still within the SMALLEST_DISTANCE_TO_OBJECT
	 * 
	 * @param upToObjectOrUntilObjectIsPassed
	 *            indicates which of the two options is used: heading to an object
	 *            until it's reached or passing the object until it's passed
	 */

	private static void keepCalmlyGoingForward(int upToObjectOrUntilObjectIsPassed) {
		if (upToObjectOrUntilObjectIsPassed == UP_TO_OBJECT) {
			int measuredDistance = upToObjectOrUntilObjectIsPassed;
			// Reset the tacho-meter for each motor
			motorRight.resetTachoCount();
			motorLeft.resetTachoCount();
			// While the object is still far away enough (i.e. further than the
			// SMALLEST_DISTANCE_TO_OBJECT), the robot keeps heading it
			while (measuredDistance > SMALLEST_DISTANCE_TO_OBJECT) {
				SensorMode distanceMeasurer = infraRedSensor.getDistanceMode();
				float[] sample = new float[distanceMeasurer.sampleSize()];
				distanceMeasurer.fetchSample(sample, 0);
				measuredDistance = (int) sample[0];
				System.out.println("Distance: " + measuredDistance);
				motorLeft.rotate(GO_CALMLY_FORWARD, true);
				motorRight.rotate(GO_CALMLY_FORWARD, true);
			}
			// Add the tacho-count to the array list, so it can be used later for the
			// 'replay' of the movement of the robot
			listWithTachoMeasurements.add(motorRight.getTachoCount());
			listWithTachoMeasurements.add(motorLeft.getTachoCount());
		}
		if (upToObjectOrUntilObjectIsPassed == UNTIL_OBJECT_IS_PASSED) {
			final int EXTRA_SPACE = 2;
			int measuredDistance = upToObjectOrUntilObjectIsPassed;
			motorRight.resetTachoCount();
			motorLeft.resetTachoCount();
			// Let the robot move further while the robot is still within its sight;
			// To make sure there is no nearby second object (diagonally) behind the first
			// one, the checking distance is multiplied with an extra space-factor
			while (measuredDistance < EXTRA_SPACE * SMALLEST_DISTANCE_TO_OBJECT) {
				SensorMode distanceMeasurer = infraRedSensor.getDistanceMode();
				float[] sample = new float[distanceMeasurer.sampleSize()];
				distanceMeasurer.fetchSample(sample, 0);
				measuredDistance = (int) sample[0];
				System.out.println("Distance: " + measuredDistance);
				motorLeft.rotate(GO_CALMLY_FORWARD, true);
				motorRight.rotate(GO_CALMLY_FORWARD, true);
			}
			// Add the tacho-count to the array list, so it can be used later for the
			// 'replay' of the movement of the robot
			listWithTachoMeasurements.add(motorRight.getTachoCount());
			listWithTachoMeasurements.add(motorLeft.getTachoCount());
		}
	}

	/**
	 * Depending on the exact design of the robot, it will need extra space to pass.
	 * This method facilitates this extra movement.
	 */
	private static void littleBitExtraForward() {
		final int EXTRA_DISTANCE = 800;
		// Reset the tacho-meter for each motor
		motorRight.resetTachoCount();
		motorLeft.resetTachoCount();

		motorLeft.rotate(EXTRA_DISTANCE, true);
		motorRight.rotate(EXTRA_DISTANCE, true);
		motorLeft.waitComplete();
		motorRight.waitComplete();

		// Add the tacho-count to the array list, so it can be used later for the
		// 'replay' of the movement of the robot
		listWithTachoMeasurements.add(motorRight.getTachoCount());
		listWithTachoMeasurements.add(motorLeft.getTachoCount());
	}

	/**
	 * This method allows the client to choose how many objects his/her robot should
	 * pass.
	 * 
	 * @param numberOfObjectsToBePassed
	 *            The amount of objects to be passed by the robot
	 */
	public static void setNumberOfObjectsToBePassed(int numberOfObjectsToBePassed) {
		AvoiderDieWerkt.numberOfObjectsToBePassed = numberOfObjectsToBePassed;
	}

	/**
	 * 
	 * @param lijstMetTachoMetingen
	 */

	private static void comeBack(ArrayList<Integer> lijstMetTachoMetingen) {
		motorLeft.setSpeed(800);
		motorRight.setSpeed(800);
		robotTurns90DegreesTo("R");
		motorLeft.rotate(-(lijstMetTachoMetingen.get(10) + lijstMetTachoMetingen.get(8)), true); // 0
		motorRight.rotate(-(lijstMetTachoMetingen.get(11) + lijstMetTachoMetingen.get(9)), true); // 1
		motorLeft.waitComplete();
		motorRight.waitComplete();
		robotTurns90DegreesTo("L");
		motorLeft.rotate(-lijstMetTachoMetingen.get(6), true); // 0
		motorRight.rotate(-lijstMetTachoMetingen.get(7), true); // 1
		motorLeft.waitComplete();
		motorRight.waitComplete();
		robotTurns90DegreesTo("L");
		motorLeft.rotate(-(lijstMetTachoMetingen.get(4) + lijstMetTachoMetingen.get(2)), true); // 0
		motorRight.rotate(-(lijstMetTachoMetingen.get(5) + lijstMetTachoMetingen.get(3)), true); // 1
		motorLeft.waitComplete();
		motorRight.waitComplete();
		robotTurns90DegreesTo("R");
		motorLeft.rotate(-lijstMetTachoMetingen.get(0), true);
		motorRight.rotate(-lijstMetTachoMetingen.get(1), true);
		motorLeft.waitComplete();
		motorRight.waitComplete();
	}

	/**
	 * This method calculates the
	 * 
	 * @param lijstMetTachoMetingen
	 */
	private static void comeBackAfterPlaying(ArrayList<Integer> lijstMetTachoMetingen) {
		// One playing round is one complete 'circle' of moving to an object, driving
		// past it and moving head
		// Moving to an object and driving past it, adds two times two measurements to
		// the list with measurements; hence, one complete round of playing equals four
		// measurements
		int numberOfPlayingRounds = lijstMetTachoMetingen.size() / 4;
		for (int round = numberOfPlayingRounds; round > 0; round--) {
			for (int step = 1; step <= 3; step += 2) {
				motorLeft.setSpeed(800);
				motorRight.setSpeed(800);
				robotTurns90DegreesTo("R");
				motorLeft.rotate(-(lijstMetTachoMetingen.get(round * 4 - step)), true);
				motorRight.rotate(-(lijstMetTachoMetingen.get(round * 4 - step)), true);
				motorLeft.waitComplete();
				motorRight.waitComplete();
			}
		}

	}

	public static void scoreAGoal() {
		balletjeGrijper.useGrip(motorOfGrip);
		balletjeGrijper.openGrip();
		balletjeGrijper.closeGrip();
		// motorLeft.rotate(800, true);
		// motorRight.rotate(-800, true);
		// motorLeft.waitComplete();
		// motorRight.waitComplete();
	}

	public void walkThroughEasyLabyrinth() {
		while (Button.ESCAPE.isUp()) {
			keepCalmlyGoingForward(UP_TO_OBJECT);
			robotTurns90DegreesTo("L");
			SensorMode distanceMeasurer = infraRedSensor.getDistanceMode();
			float[] sample = new float[distanceMeasurer.sampleSize()];
			distanceMeasurer.fetchSample(sample, 0);
			int measuredDistance = (int) sample[0];
			if (measuredDistance < SMALLEST_DISTANCE_TO_OBJECT) {
				robotTurns90DegreesTo("L");
				robotTurns90DegreesTo("L");
			}

		}
	}

	public void walkThroughRealLabyrinth() {
		// Flash green light and make sound when ready, then wait for press
		Button.LEDPattern(4);
		Sound.beepSequenceUp();
		System.out.println("Initiating Follow Beacon!");
		Button.waitForAnyPress();
		boolean klaar = false;
		final int STEP = 400;
		final int BIG_STEP = 660;
		while (!klaar && Button.ESCAPE.isUp()) {
			if (leftFree()) {
				motorLeft.rotate(BIG_STEP, true);
				motorRight.rotate(BIG_STEP, true);
				motorLeft.waitComplete();
				motorRight.waitComplete();
				robotTurns90DegreesTo("L");
				motorLeft.rotate(BIG_STEP, true);
				motorRight.rotate(BIG_STEP, true);
				motorLeft.waitComplete();
				motorRight.waitComplete();
			} else if (straightOnFree()) {
				motorLeft.rotate(STEP, true);
				motorRight.rotate(STEP, true);
				motorLeft.waitComplete();
				motorRight.waitComplete();
			} else {
				robotTurns90DegreesTo("R");
			}

		}
		motorRight.close();
		motorLeft.close();
		
	}

	public boolean leftFree() {
		headTurns90DegreesTo("L");
		int measurement = getMeasurement();
		headTurns90DegreesTo("R");
		return (measurement > SMALLEST_DISTANCE_TO_OBJECT);
	}

	public boolean straightOnFree() {
		return (getMeasurement() > SMALLEST_DISTANCE_TO_OBJECT * 0.7);
	}

	public int getMeasurement() {
		SensorMode distanceMeasurer = infraRedSensor.getDistanceMode();
		float[] sample = new float[distanceMeasurer.sampleSize()];
		distanceMeasurer.fetchSample(sample, 0);
		return (int) sample[0];
	}
}
