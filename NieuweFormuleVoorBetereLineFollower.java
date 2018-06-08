package nl.hva.miw.robot.cohort12;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.motor.*;
import lejos.hardware.port.*;
import lejos.robotics.Color;

public class NieuweFormuleVoorBetereLineFollower {
	static UnregulatedMotor motorRight = new UnregulatedMotor(MotorPort.C);
	static UnregulatedMotor motorLeft = new UnregulatedMotor(MotorPort.D);
	// static TouchSensor touch = new TouchSensor(SensorPort.S1);
	static ColorSensor color = new ColorSensor(SensorPort.S4);
	final static double RED_VALUE_OF_WHITE = 0.85;
	final static double RED_VALUE_OF_BLACK = 0.08;
	final static double RED_VALUE_OF_BORDER = 0.465; // (RED_VALUE_OF_WHITE - RED_VALUE_OF_BLACK) / 2 +
														// RED_VALUE_OF_BLACK; // 0.465
	final static double ADJUSTMENT_FACTOR = 2; // we'll have to find the right factor by testing
	final static int START_POWER = 40;
	final static int MAX_POWER = 100;

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

		motorRight.forward();
		motorLeft.forward();
		int powerOfMotorRight = START_POWER;
		int powerOfMotorLeft = START_POWER;

		while (Button.ESCAPE.isUp()) {
			colorValue = color.getRed();
			double error = RED_VALUE_OF_BORDER - colorValue;
			double adjustedError = (ADJUSTMENT_FACTOR * error);
			powerOfMotorRight = (int) (powerOfMotorRight + adjustedError * (MAX_POWER - powerOfMotorRight) / MAX_POWER);
			powerOfMotorLeft = (int) (powerOfMotorLeft - adjustedError * (MAX_POWER - powerOfMotorLeft) / MAX_POWER);
			motorRight.setPower(powerOfMotorRight);
			motorLeft.setPower(powerOfMotorLeft);
			System.out
					.println("error: " + error + "powerR: " + powerOfMotorRight + "powerL: " + powerOfMotorLeft + "\n");
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
