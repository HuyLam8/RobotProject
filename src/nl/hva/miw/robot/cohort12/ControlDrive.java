package nl.hva.miw.robot.cohort12;

import lejos.hardware.motor.UnregulatedMotor;

/**
 * This class can be used to control the direction of a robot. The class
 * encompasses methods for different speeds and directions of the robot.
 * 
 * @author Bjorn Goos
 *
 */

public class ControlDrive {

	private final static int VERY_SMALL_TURN_X = 50;
	private final static int VERY_SMALL_TURN_Y = 40;

	private final static int SMALL_TURN_X = 60;
	private final static int SMALL_TURN_Y = 10;

	private final static int NORMAL_TURN_X = 60;
	private final static int NORMAL_TURN_Y = -15;

	private final static int STRONG_TURN_X = 75;
	private final static int STRONG_TURN_Y = -75;

	private final static int EXTREME_TURN_X = 100;
	private final static int EXTREME_TURN_Y = -100;

	private final static int EXTRA_SMALL_TURN_TO_TEST_POSITION_X = 40;
	private final static int EXTRA_SMALL_TURN_TO_TEST_POSITION_Y = 10;

	private final static int NORMAL_FORWARD = 40;
	private final static int FAST_FORWARD = 60;

	private static UnregulatedMotor motorRight;
	private static UnregulatedMotor motorLeft;

	/**
	 * Instantiate a new ControlDrive.
	 */
	public ControlDrive() {
		this(null, null);
	}

	/**
	 * Instantiate a new ControlDrive.
	 * 
	 * @param motorRight
	 *            The motor at the right of the robot.
	 * @param motorLeft
	 *            The motor at the left of the robot.
	 */
	public ControlDrive(UnregulatedMotor motorRight, UnregulatedMotor motorLeft) {
		super();
		ControlDrive.motorRight = motorRight;
		ControlDrive.motorLeft = motorLeft;
	}

	/**
	 * Assign actual motor objects to the motor-variables of an ControlDrive-object.
	 * 
	 * @param motorRight
	 *            The motor at the right of the robot.
	 * @param motorLeft
	 *            The motor at the left of the robot.
	 */
	public void useControlDrive(UnregulatedMotor motorRight, UnregulatedMotor motorLeft) {
		ControlDrive.motorRight = motorRight;
		ControlDrive.motorLeft = motorLeft;
	}

	public void verySmallTurnLeft() {
		ControlDrive.motorRight.setPower(VERY_SMALL_TURN_X);
		ControlDrive.motorLeft.setPower(VERY_SMALL_TURN_Y);
	}

	public void verySmallTurnRight() {
		ControlDrive.motorLeft.setPower(VERY_SMALL_TURN_X);
		ControlDrive.motorRight.setPower(VERY_SMALL_TURN_Y);
	}

	public void smallTurnLeft() {
		ControlDrive.motorRight.setPower(SMALL_TURN_X);
		ControlDrive.motorLeft.setPower(SMALL_TURN_Y);
	}

	public void smallTurnRight() {
		ControlDrive.motorLeft.setPower(SMALL_TURN_X);
		ControlDrive.motorRight.setPower(SMALL_TURN_Y);
	}

	public void normalTurnLeft() {
		ControlDrive.motorRight.setPower(NORMAL_TURN_X);
		ControlDrive.motorLeft.setPower(NORMAL_TURN_Y);
	}

	public void normalTurnRight() {
		ControlDrive.motorLeft.setPower(NORMAL_TURN_X);
		ControlDrive.motorRight.setPower(NORMAL_TURN_Y);
	}

	public void strongTurnRight() {
		ControlDrive.motorLeft.setPower(STRONG_TURN_X);
		ControlDrive.motorRight.setPower(STRONG_TURN_Y);
	}

	public void strongTurnLeft() {
		ControlDrive.motorRight.setPower(STRONG_TURN_X);
		ControlDrive.motorLeft.setPower(STRONG_TURN_Y);
	}

	public void extremeTurnRight() {
		ControlDrive.motorLeft.setPower(EXTREME_TURN_X);
		ControlDrive.motorRight.setPower(EXTREME_TURN_Y);
	}

	public void extremeTurnLeft() {
		ControlDrive.motorRight.setPower(EXTREME_TURN_X);
		ControlDrive.motorLeft.setPower(EXTREME_TURN_Y);
	}

	public void extraSmallTurnToLeftToTestPosition() {
		ControlDrive.motorRight.setPower(EXTRA_SMALL_TURN_TO_TEST_POSITION_X);
		ControlDrive.motorLeft.setPower(EXTRA_SMALL_TURN_TO_TEST_POSITION_Y);
	}

	public void moveStraightAtNormalSpeed() {
		ControlDrive.motorRight.setPower(NORMAL_FORWARD);
		ControlDrive.motorLeft.setPower(NORMAL_FORWARD);
	}

	public void moveStraightAtHighSpeed() {
		ControlDrive.motorRight.setPower(FAST_FORWARD);
		ControlDrive.motorLeft.setPower(FAST_FORWARD);
	}

	public void stop() {
		ControlDrive.motorRight.stop();
		ControlDrive.motorLeft.stop();
	}

	public void goForward() {
		ControlDrive.motorRight.forward();
		ControlDrive.motorLeft.forward();
	}

	public void goBackward() {
		ControlDrive.motorRight.backward();
		ControlDrive.motorLeft.backward();
	}

}
