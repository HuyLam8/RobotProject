package nl.hva.miw.robot.cohort12;

import lejos.hardware.motor.UnregulatedMotor;

/**
 * This class can be used to control the direction and speed of a robot.
 * 
 * @author Bjorn Goos
 *
 */

public class ControlDrive {
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

	/**
	 * Set the power at which the robot moves. Choose a higher power for the right
	 * motor compared to the left motor to make the robot move to the left (vice
	 * versa).
	 * 
	 * @param rightPower
	 *            Power of the motor on the right of the robot.
	 * @param leftPower
	 *            Power of the motor on the right of the robot.
	 */

	public void setPower(int rightPower, int leftPower) {
		ControlDrive.motorRight.setPower(rightPower);
		ControlDrive.motorLeft.setPower(leftPower);
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
