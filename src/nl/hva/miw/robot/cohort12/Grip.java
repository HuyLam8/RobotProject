package nl.hva.miw.robot.cohort12;

import lejos.hardware.motor.UnregulatedMotor;
import lejos.utility.Delay;

/**
 * With this class a grip/claw can be controlled by manipulating the rotation of
 * the motor attached to it.
 * 
 * @author Bjorn Goos
 *
 */

public class Grip {

	private static UnregulatedMotor motorOfClaw;
	public final static int SPEED_OF_OPENING_AND_CLOSING = 40;
	public final static int REQUIRED_TIME_OF_OPENING_AND_CLOSING = 3000;

	/**
	 * Instantiate a new grip.
	 */
	public Grip() {
		this(null);
	}

	/**
	 * Instantiate a new grip.
	 * 
	 * @param motorOfClaw
	 *            The motor that controls the closing and opening of the claw.
	 */
	public Grip(UnregulatedMotor motorOfClaw) {
		super();
		Grip.motorOfClaw = motorOfClaw;
	}

	/**
	 * Assign an actual motor object to the motorOfClaw-variable.
	 * 
	 * @param motorOfClaw
	 *            The motor that controls the closing and opening of the claw.
	 */
	public void useGrip(UnregulatedMotor motorOfClaw) {
		Grip.motorOfClaw = motorOfClaw;
	}

	/**
	 * Open grip by making the motor turn backward.
	 */
	public void openGrip() {
		Grip.motorOfClaw.backward();
		Grip.motorOfClaw.setPower(SPEED_OF_OPENING_AND_CLOSING);
		Delay.msDelay(REQUIRED_TIME_OF_OPENING_AND_CLOSING);
	}

	/**
	 * Close grip by making the motor turn forward.
	 */
	public void closeGrip() {
		Grip.motorOfClaw.forward();
		Grip.motorOfClaw.setPower(SPEED_OF_OPENING_AND_CLOSING);
		Delay.msDelay(REQUIRED_TIME_OF_OPENING_AND_CLOSING);
	}

}
