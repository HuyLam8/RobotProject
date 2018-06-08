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
 * THIS CLASS IS ALSMOST THE SAME AS TestLauncher1, BUT NOW, INSTEAD OF PRINTING
 * WHERE THE STARTPOSITION WAS, THE CODE RETURNS A VALUE (0 = LEFT, 1 = RIGHT)
 * AND THEN THE CALLING METHOD WILL SAY WHAT THE STARTPOSITION WAS
 * 
 * EXPECTED OUTPUT IS THE SAME AS WITH TestLauncher1, EXCEPT WITH EITHER A 0 OR
 * A 1 ABOVE "Start was links/rechts"
 * 
 * @author Bjorn Goos
 *
 */

public class TestLauncher2 {

	UnregulatedMotor motorRight = new UnregulatedMotor(MotorPort.C);
	UnregulatedMotor motorLeft = new UnregulatedMotor(MotorPort.D);
	ColorSensor color = new ColorSensor(SensorPort.S4);

	public static void main(String[] args) {
		TestLauncher2 testje = new TestLauncher2();
		testje.run();
	}

	public void run() {
		int startpositie = this.start();
		System.out.println(startpositie);
		String startwas = (startpositie == 0) ? "links" : "rechts";
		System.out.println("Start was " + startwas);
	}

	public int start() {
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
		float meting2 = -1;
		float meting12 = -1;

		while (Button.ESCAPE.isUp()) {
			colorValue = color.getRed();
			listWithRedVales.add(colorValue);
			System.out.println("meting " + listWithRedVales.size() + ": meetwaarde: " + colorValue);

			// laat robot bv. 10 metingen naar links gaan en bewaar de 1 en 10e meting
			if (listWithRedVales.size() < 13) {
				motorRight.setPower(50);
				motorLeft.setPower(-10);
				if (listWithRedVales.size() == 1) {
					meting2 = colorValue;
				}
				if (listWithRedVales.size() == 11) {
					meting12 = colorValue;
				}
			}
			System.out.println("---");
			System.out.println("m2: " + meting2);
			System.out.println("m12: " + meting12);

			if (meting12 > meting2)
				return 0; // System.out.println("Start was links");
			else
				return 1; // System.out.println("Start was rechts");
		}
		return -1;

	}

}
