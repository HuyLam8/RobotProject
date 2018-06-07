package nl.hva.miw.robot.cohort12;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.motor.*;
import lejos.hardware.port.*;
import lejos.robotics.Color;

public class LijnVolger {
	static UnregulatedMotor motorRight = new UnregulatedMotor(MotorPort.C);
	static UnregulatedMotor motorLeft = new UnregulatedMotor(MotorPort.D);
	// static TouchSensor touch = new TouchSensor(SensorPort.S1);
	static ColorSensor color = new ColorSensor(SensorPort.S4);
	final static double RED_VALUE_OF_BORDER = (0.85 - 0.08) / 2 + 0.08;
	final static double ADJUSTMENT_FACTOR = 1; // we'll have to find the right factor by testing
	final static int BASIC_SPEED = 40; // same is true for the optimal speed

	public static void main(String[] args) {
		float colorValue;

		System.out.println("Line Follower\n");

		color.setRedMode();
		color.setFloodLight(Color.RED);
		color.setFloodLight(true);

		Button.LEDPattern(4); // flash green led and
		Sound.beepSequenceUp(); // make sound when ready.

		System.out.println("Press any key to start");

		Button.waitForAnyPress();

		motorRight.backward();
		motorLeft.backward();
		motorRight.setPower(BASIC_SPEED);
		motorLeft.setPower(BASIC_SPEED);

		// drive waiting for touch sensor or escape key to stop driving.

		while (Button.ESCAPE.isUp()) {
			colorValue = color.getRed();
			// this will give a positive value if the Robot passes a black surface
			// and a negative in case of a white surface
			double error = RED_VALUE_OF_BORDER - colorValue;
			int requiredAdjustment = (int)(ADJUSTMENT_FACTOR * error);
			// *assuming the Robot starts at the right border of the line*,
			// it should move to the right if it passes a (too) black surface.
			// meaning the left-motor has to increase its power
			// (same reasoning for white in the opposite direction)
			motorRight.setPower(BASIC_SPEED - requiredAdjustment);
			motorLeft.setPower(BASIC_SPEED + requiredAdjustment);
		}

		// stop motors with brakes on.
		motorRight.stop();
		motorLeft.stop();

		// free up resources.
		motorRight.close();
		motorLeft.close();
		// touch.close();
		color.close();

		Sound.beepSequence(); // we are done.
	}
}