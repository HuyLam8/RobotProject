package nl.hva.miw.robot.cohort12;

import java.util.ArrayList;

import lejos.hardware.Brick;
import lejos.hardware.Button;
import lejos.hardware.Key;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.UnregulatedMotor;
import lejos.hardware.port.*;
import lejos.robotics.Color;
import lejos.utility.Delay;

public class Marvin {

	Brick brick;
	UnregulatedMotor mediumMotor = new UnregulatedMotor(MotorPort.A);
	UnregulatedMotor largeMotorRight = new UnregulatedMotor(MotorPort.C);
	UnregulatedMotor largeMotorLeft = new UnregulatedMotor(MotorPort.D);
	DriveControl drive = new DriveControl();
	Grip grip = new Grip();
	ColorSensor color = new ColorSensor(SensorPort.S4);

	final static double RED_VALUE_ON_BORDER_OF_LINE = 0.43;

	public Marvin() {
		super();
		brick = LocalEV3.get();
	}

	public static void main(String[] args) {
		Marvin marvin = new Marvin();
		marvin.run();
	}

	private void run() {
		Sound.beepSequenceUp();
		System.out.println("Press any key to start");
		Button.waitForAnyPress();
		followLineAtRightBorder();
	}

	public void waitForKey(Key key) {
		while (key.isUp()) {
			Delay.msDelay(100);
		}
		while (key.isDown()) {
			Delay.msDelay(100);
		}
	}
	
	public void followLine(int mode) {
		switch (mode) {
		case 1: followLineAtRightBorder();
		}
	}

	public void followLineAtRightBorder() {
		color.setRedMode();
		color.setFloodLight(Color.RED);
		color.setFloodLight(true);
		Button.LEDPattern(4);
		Sound.beepSequenceUp(); 
		
		while (Button.ESCAPE.isUp()) {
			float colorValue = color.getRed();
			System.out.printf("value=%.3f\n", colorValue);
			if (colorValue > 0.43) {
				drive.normalTurnLeft(largeMotorLeft, largeMotorRight);
			} else {
				drive.normalTurnRight(largeMotorLeft, largeMotorRight);
			}
		}
	}

	public void followLineAtLeftBorder() {
		float colorValue = color.getRed();
		if (colorValue < RED_VALUE_ON_BORDER_OF_LINE) {
			drive.normalTurnLeft(largeMotorLeft, largeMotorRight);
		} else {
			drive.normalTurnRight(largeMotorLeft, largeMotorRight);
		}

	}

	public void followLineAtUnknownBorder() {
		int border = findBorder();
		if (border == 0) {
			followLineAtRightBorder();
		} else if (border == 1) {
			followLineAtLeftBorder();
		} else {
			System.out.println("Error");
		}
	}

	public int findBorder() {
		ArrayList<Double> lijstMetKleurwaarden = new ArrayList<>();
		while (Button.ESCAPE.isUp()) {
			float colorValue = color.getRed();
			lijstMetKleurwaarden.add((double) colorValue);
			System.out.printf("value = %.2f", colorValue);
			double average = 0;
			if (lijstMetKleurwaarden.size() < 20) {
				drive.strongTurnLeft(largeMotorLeft, largeMotorRight);
				if (lijstMetKleurwaarden.size() == 19) {
					average = calculateAverage(lijstMetKleurwaarden);
				}
			}

			else if (lijstMetKleurwaarden.size() >= 20 && lijstMetKleurwaarden.size() < 40) {
				drive.strongTurnRight(largeMotorLeft, largeMotorRight);
			} else {
				if (average < 0.5) {
					return 1;
				} else {
					return 0;
				}
			}

		}
		return -1;
	}

	public static double calculateAverage(ArrayList<Double> lijstMetWaarden) {
		double average;
		double sum = 0;
		for (Double waarde : lijstMetWaarden) {
			sum += waarde;
		}
		average = sum / lijstMetWaarden.size();
		return average;
	}
}
