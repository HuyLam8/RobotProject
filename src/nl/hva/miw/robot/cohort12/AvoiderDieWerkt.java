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
	private static EV3IRSensor infraredSensor;
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
	 * @param infraredSensor
	 *            The sensor that measures distances (and 'angles' for the Beacon)
	 */
	public AvoiderDieWerkt(RegulatedMotor motorRight, RegulatedMotor motorLeft, RegulatedMotor motorOfHead,
			UnregulatedMotor motorOfGrip, ColorSensor colorSensor, EV3IRSensor infraredSensor) {
		super();
		AvoiderDieWerkt.motorRight = motorRight;
		AvoiderDieWerkt.motorLeft = motorLeft;
		AvoiderDieWerkt.motorOfHead = motorOfHead;
		AvoiderDieWerkt.motorOfGrip = motorOfGrip;
		AvoiderDieWerkt.colorSensor = colorSensor;
		AvoiderDieWerkt.infraredSensor = infraredSensor;
	}

	public AvoiderDieWerkt(RegulatedMotor motorRight, RegulatedMotor motorLeft, RegulatedMotor motorOfHead,
			UnregulatedMotor motorOfGrip, EV3IRSensor infraredSensor) {
		this(motorRight, motorLeft, motorOfHead, motorOfGrip, null, infraredSensor);
	}

	public AvoiderDieWerkt() {
		this(null, null, null, null, null, null);
	}

	public void useObjectAvoider(RegulatedMotor motorRight, RegulatedMotor motorLeft, RegulatedMotor motorOfHead,
			UnregulatedMotor motorOfGrip, ColorSensor colorSensor, EV3IRSensor infraredSensor) {
		AvoiderDieWerkt.motorRight = motorRight;
		AvoiderDieWerkt.motorLeft = motorLeft;
		AvoiderDieWerkt.motorOfHead = motorOfHead;
		AvoiderDieWerkt.motorOfGrip = motorOfGrip;
		AvoiderDieWerkt.colorSensor = colorSensor;
		AvoiderDieWerkt.infraredSensor = infraredSensor;
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

		makeReadySignals("Let me avoid some objects!");

		// Call the three relevant submethods
		startObjectAvoider();
		scoreAGoal();
		comeBack(listWithTachoMeasurements, numberOfObjectsToBePassed);

		// Close all resources
		motorLeft.close();
		motorRight.close();
		motorOfHead.close();
		infraredSensor.close();
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
		makeReadySignals("Play with Marvin!");

		// Start every run with an empty list of tacho measurements
		listWithTachoMeasurements.clear();

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
		infraerdSensor.close();
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
				SensorMode distanceMeasurer = infraredSensor.getDistanceMode();
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
				SensorMode distanceMeasurer = infraredSensor.getDistanceMode();
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
	 * This method uses the list with tacho-measurements to make the robot drive the
	 * same path as it did while avoiding objects, but now backwards. This method
	 * works if the number of objects to be passed is one or two, but can easily be
	 * re-written to generalize it to any number of passed objects. Essentially, the
	 * robot is playing all the elements in the ArrayList 'backwards', i.e. from 11
	 * (in case of two objects) or 5 (in case of one object) back to zero. The
	 * left/right is obviously in opposite direction as well; meaning, for example,
	 * that the robot started (when finding its first object) with going left, so it
	 * will end (on its way back) with going right.
	 * 
	 * @param listWithTachoMeasurements
	 *            List with all the stored tacho-measurements.
	 */

	private static void comeBack(ArrayList<Integer> listWithTachoMeasurements, int numberOfObjectsToBePassed) {
		final int FASTER_SPEED = 800;

		if (numberOfObjectsToBePassed == 2) {
			motorLeft.setSpeed(FASTER_SPEED);
			motorRight.setSpeed(FASTER_SPEED);
			robotTurns90DegreesTo("R");
			motorLeft.rotate(-(listWithTachoMeasurements.get(10) + listWithTachoMeasurements.get(8)), true);
			motorRight.rotate(-(listWithTachoMeasurements.get(11) + listWithTachoMeasurements.get(9)), true);
			motorLeft.waitComplete();
			motorRight.waitComplete();
			robotTurns90DegreesTo("L");
			motorLeft.rotate(-listWithTachoMeasurements.get(6), true);
			motorRight.rotate(-listWithTachoMeasurements.get(7), true);
			motorLeft.waitComplete();
			motorRight.waitComplete();
		}

		motorLeft.setSpeed(FASTER_SPEED);
		motorRight.setSpeed(FASTER_SPEED);
		robotTurns90DegreesTo("L");
		motorLeft.rotate(-(listWithTachoMeasurements.get(4) + listWithTachoMeasurements.get(2)), true);
		motorRight.rotate(-(listWithTachoMeasurements.get(5) + listWithTachoMeasurements.get(3)), true);
		motorLeft.waitComplete();
		motorRight.waitComplete();
		robotTurns90DegreesTo("R");
		motorLeft.rotate(-listWithTachoMeasurements.get(0), true);
		motorRight.rotate(-listWithTachoMeasurements.get(1), true);
		motorLeft.waitComplete();
		motorRight.waitComplete();

	}

	/**
	 * This method uses the list with tacho-measurements to make the robot drive the
	 * same path as it did in the playMode, but now backwards. This method works for
	 * any play-time and any path chosen by the person playing with the robot. This
	 * methods works essentially in the same way as the comeBack-method.
	 * 
	 * @param lijstMetTachoMetingen
	 */
	private static void comeBackAfterPlaying(ArrayList<Integer> lijstMetTachoMetingen) {
		// One playing round is one complete 'circle' of moving to an object, driving
		// past it and moving head
		// Moving to an object and driving past it, adds two times two measurements to
		// the list with measurements; hence, one complete round of playing equals four
		// measurements
		final int MEASUREMENTS_PER_ROUND = 4;
		final int FASTER_SPEED = 800;
		int numberOfPlayingRounds = lijstMetTachoMetingen.size() / MEASUREMENTS_PER_ROUND;
		for (int round = numberOfPlayingRounds; round > 0; round--) {
			for (int step = 1; step <= 3; step += 2) {
				motorLeft.setSpeed(FASTER_SPEED);
				motorRight.setSpeed(FASTER_SPEED);
				robotTurns90DegreesTo("R");
				motorLeft.rotate(-(lijstMetTachoMetingen.get(round * MEASUREMENTS_PER_ROUND - step)), true);
				motorRight.rotate(-(lijstMetTachoMetingen.get(round * MEASUREMENTS_PER_ROUND - step)), true);
				motorLeft.waitComplete();
				motorRight.waitComplete();
			}
		}

	}

	/**
	 * The robot will open its grip (dropping whatever is in it) and close it again.
	 */

	public static void scoreAGoal() {
		balletjeGrijper.useGrip(motorOfGrip);
		balletjeGrijper.openGrip();
		balletjeGrijper.closeGrip();
		// motorLeft.rotate(800, true);
		// motorRight.rotate(-800, true);
		// motorLeft.waitComplete();
		// motorRight.waitComplete();
	}

	/**
	 * The robot walks through a labyrinth by essentially doing three things: 1.
	 * Check after every 'step' whether the road to the left is free. If it is, then
	 * go left. 2. If the road to the left is not free, then check if going
	 * forward/straight is free. If it is, then go forward. 3. If the road forward
	 * is also not free, then the robot will turn 90 degrees to the right and
	 * continue with point 1. again. This of course means that one wall is checked
	 * twice.
	 */
	public void walkThroughRealLabyrinth() {
		// This variable has no function in the current design, but can be used to make
		// the robot stop at the end of the labyrinth in an elegant way, for example by
		// detecting (with a color sensor) a white line (in which case this variable
		// should switch to true and the robot will stop moving).
		boolean done = false;
		// This is the distance the robot covers after each time it checks whether going
		// forward is possible, expressed as rotations of the regular motor.
		final int STEP = 400;
		// This is the distance the robot covers when it sees the road to the left is
		// free, expressed as rotations of the regular motor.
		final int BIG_STEP = 660;

		makeReadySignals("Labyrinth: I'll find the exit!");

		while (!done && Button.ESCAPE.isUp()) {
			if (leftFree()) {
				// This step is needed to make sure the robot has enough room to enter the road,
				// i.e. to make sure that the left part of the robot will not crash into a wall.
				makeStep(BIG_STEP);
				robotTurns90DegreesTo("L");
				// Now the robot 'steps into' the free road. A big step here avoids that the
				// robot will not go into the free road far enough, since this would imply that
				// the robot will after *not completely* going into the road, will check whether
				// 'left is free' and will see the (free) road it just came from.
				makeStep(BIG_STEP);
			} else if (straightOnFree()) {
				makeStep(STEP);
			} else {
				robotTurns90DegreesTo("R");
			}
		}
		motorRight.close();
		motorLeft.close();

	}

	/**
	 * This method checks whether the road to the left is free by moving the head of
	 * the robot to the left and then checking the distance as measured by the
	 * robot's infrared sensor. It's important that the head moves back straight
	 * afterwards, since the next step will be to check whether forward/straight-on
	 * is free.
	 * 
	 * @return True if the road to the left is free, false otherwise.
	 */
	public boolean leftFree() {
		headTurns90DegreesTo("L");
		int measurement = getMeasurement();
		headTurns90DegreesTo("R");
		return (measurement > SMALLEST_DISTANCE_TO_OBJECT);
	}

	/**
	 * This method checks whether the road in front of it is free.
	 * 
	 * @return True if the road is free, false otherwise.
	 */

	public boolean straightOnFree() {
		double correctionFactor = 0.7;
		return (getMeasurement() > SMALLEST_DISTANCE_TO_OBJECT * correctionFactor);
	}

	/**
	 * This method creates one measurement of the distance in front of the infrared
	 * sensor.
	 * 
	 * @return The distance as measured by the infrared sensor.
	 */
	public int getMeasurement() {
		SensorMode distanceMeasurer = infraredSensor.getDistanceMode();
		float[] sample = new float[distanceMeasurer.sampleSize()];
		distanceMeasurer.fetchSample(sample, 0);
		return (int) sample[0];
	}

	/**
	 * This method allows the robot to make one step of a give size.
	 * 
	 * @param stepSize
	 *            The size of the step to be made, expressed as rotations of the
	 *            regulated motor.
	 */
	public void makeStep(int stepSize) {
		motorLeft.rotate(stepSize, true);
		motorRight.rotate(stepSize, true);
		motorLeft.waitComplete();
		motorRight.waitComplete();
	}

	/**
	 * With this method, the robot lets some green lights flash, makes some noises
	 * and then waits for a button to be pressed while a specified message is shown.
	 * 
	 * @param message
	 *            The messaged to be shown on the screen of the robot while waiting
	 *            for the user of the robot to press on a button to start the robot.
	 */

	public void makeReadySignals(String message) {
		Button.LEDPattern(4);
		Sound.beepSequenceUp();
		System.out.println(message);
		Button.waitForAnyPress();
	}
}
