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
		this(null, null, null);
	}

	/**
	 * Instantiate a new ControlDrive.
	 * 
	 * @param motor1
	 *            The motor at the right of the robot.
	 * @param motor2
	 *            The motor at the left of the robot.
	 */
	public ControlDrive(UnregulatedMotor motor1, UnregulatedMotor motor2, String leftOrRight) {
		super();
		if (leftOrRight.equals("R")) {
			ControlDrive.motorRight = motor1;
			ControlDrive.motorLeft = motor2;
		}
		if (leftOrRight.equals("L")) {
			ControlDrive.motorLeft = motor1;
			ControlDrive.motorRight = motor2;
		}
	}

	/**
	 * Assign actual motor objects to the motor-variables of an ControlDrive-object.
	 * 
	 * @param motorRight
	 *            The motor at the right of the robot.
	 * @param motorLeft
	 *            The motor at the left of the robot.
	 */
	public void useControlDrive(UnregulatedMotor motor1, UnregulatedMotor motor2, String leftOrRight) {
		if (leftOrRight.equals("R")) {
			ControlDrive.motorRight = motor1;
			ControlDrive.motorLeft = motor2;
		}
		if (leftOrRight.equals("L")) {
			ControlDrive.motorLeft = motor1;
			ControlDrive.motorRight = motor2;
		}
	}

	/**
	 * Set the power at which the robot moves. Choose a higher power for the right
	 * motor compared to the left motor to make the robot move to the left (vice
	 * versa).
	 * 
	 * @param power1
	 *            Power of the motor on the right of the robot.
	 * @param power2
	 *            Power of the motor on the right of the robot.
	 */

	public void setPower(int power1, int power2) {
		ControlDrive.motorRight.setPower(power1);
		ControlDrive.motorLeft.setPower(power2);
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
