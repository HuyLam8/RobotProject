package nl.hva.miw.robot.cohort12;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.motor.*;
import lejos.hardware.port.*;
import lejos.robotics.Color;

public class Lijn {
	static UnregulatedMotor motorRight = new UnregulatedMotor(MotorPort.C);
	static UnregulatedMotor motorLeft = new UnregulatedMotor(MotorPort.D);
	//static TouchSensor touch = new TouchSensor(SensorPort.S1);
	static ColorSensor color = new ColorSensor(SensorPort.S4);

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
		motorRight.setPower(55);
		motorLeft.setPower(55);

		// drive waiting for touch sensor or escape key to stop driving.

		while (Button.ESCAPE.isUp()) {
			colorValue = color.getRed();

			System.out.printf("value=%.3f\n", colorValue);

			if (colorValue > .43) {
				motorRight.setPower(60);
				motorLeft.setPower(-15);
			} else {
				motorRight.setPower(-15);
				motorLeft.setPower(60);
			}
		}

		// stop motors with brakes on.
		motorRight.stop();
		motorLeft.stop();

		// free up resources.
		motorRight.close();
		motorLeft.close();
		//touch.close();
		color.close();

		Sound.beepSequence(); // we are done.
	}
}
