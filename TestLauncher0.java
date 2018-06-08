package Test;

import java.util.ArrayList;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.motor.UnregulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.robotics.Color;
import nl.hva.miw.robot.cohort12.ColorSensor;

/**
 * THIS CLASS TESTS WHETHER THE ARRAYLIST ACTUALLY WORKS AS WE EXPECT IT TO IF
 * IT WORKS CORRECTLY, THE OUTPUT SHOULD BE: 
 * *start* 
 * meting 1: meetwaarde ..
 * meting 2: meetwaarde ...
 * ...
 * meting 10: meetwaarde .. 
 * *end*
 * 
 * @author Bjorn Goos
 *
 */

public class TestLauncher0 {

	UnregulatedMotor motorRight = new UnregulatedMotor(MotorPort.C);
	UnregulatedMotor motorLeft = new UnregulatedMotor(MotorPort.D);
	ColorSensor color = new ColorSensor(SensorPort.S4);

	public static void main(String[] args) {
		TestLauncher2 testje = new TestLauncher2();
		testje.run();
	}

	public void run() {
		color.setRedMode();
		color.setFloodLight(Color.RED);
		color.setFloodLight(true);

		Button.LEDPattern(4);
		Sound.beepSequenceUp();

		System.out.println("Press any key to start");

		Button.waitForAnyPress();

		// robot rijdt achterwaarts
		motorRight.backward();
		motorLeft.backward();

		ArrayList<Float> listWithRedVales = new ArrayList<>();
		float colorValue;

		while (Button.ESCAPE.isUp()) {
			colorValue = color.getRed();
			listWithRedVales.add(colorValue);
			System.out.println("meting " + listWithRedVales.size() + ": meetwaarde: " + colorValue);
			if (listWithRedVales.size() >= 10) {
				break;
			}
		}
	}

}
