package nl.hva.miw.robot.cohort12;

import lejos.hardware.Brick;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.*;
import lejos.hardware.port.*;
import lejos.hardware.sensor.*;
import lejos.robotics.RegulatedMotor;
import lejos.utility.Delay;

public class TestRegulatedMotor {

	Brick brick;
	private static RegulatedMotor motorRight = new EV3LargeRegulatedMotor(MotorPort.D);
	private static RegulatedMotor motorLeft = new EV3LargeRegulatedMotor(MotorPort.C);
	private static RegulatedMotor motorOfHead = new EV3MediumRegulatedMotor(MotorPort.B);
	private static UnregulatedMotor motorOfGrip = new UnregulatedMotor(MotorPort.A);
	// private static ColorSensor colorSensor = new ColorSensor(SensorPort.S4);
	private static EV3IRSensor infraRedSensor = new EV3IRSensor(SensorPort.S1);

	// The constants below are for setting the right mode of a LineFollower
	final static int MODE_ONOFF_FOLLOWER_RIGHT = 1;
	final static int MODE_ONOFF_FOLLOWER_LEFT = 2;
	final static int MODE_ADJUSTED_P_CONTROLLER_RIGHT = 3;
	final static int MODE_ADJUSTED_P_CONTROLLER_LEFT = 4;
	final static int MODE_UNKNOWN_STARTING_POINT = 5;

	public TestRegulatedMotor() {
		super();
		brick = LocalEV3.get();
	}

	public static void main(String[] args) {
		TestRegulatedMotor marvin = new TestRegulatedMotor();
		marvin.run();
	}

	private void run() {

		Sound.beepSequence();
		System.out.println("Press a key, Anderson");
		Button.waitForAnyPress();

		// DRAAIT ROBOT 90 GRADEN NAAR LINKS
		// motorLeft.rotate(-400, true);
		// motorRight.rotate(400, true);
		// motorLeft.waitComplete();
		// motorRight.waitComplete();

		// DRAAIT HOOFD 90 GRADEN NAAR RECHTS
		// motorOfHead.rotateTo(-70, true);
		// motorOfHead.waitComplete();

		// LAAT ROBOT VOORUIT GAAN
		// motorLeft.rotate(360, true);
		// motorRight.rotate(360, true);
		// motorLeft.waitComplete();
		// motorRight.waitComplete();

		while (Button.ESCAPE.isUp()) {

			int mateVanHoofdDraaien = 65;
			int mateVanRobotDraaien = 400;

			int afstand = 1000000;
			while (afstand > 40) {
				SensorMode distance = infraRedSensor.getDistanceMode();
				float[] sample = new float[distance.sampleSize()];
				distance.fetchSample(sample, 0);
				afstand = (int) sample[0];
				System.out.println("Distance: " + afstand);
				motorLeft.rotate(360, true);
				motorRight.rotate(360, true);
				// motorLeft.waitComplete();
				// motorRight.waitComplete();
			}

			// DRAAIT ROBOT 90 GRADEN NAAR LINKS
			motorLeft.rotate(-mateVanRobotDraaien, true);
			motorRight.rotate(mateVanRobotDraaien, true);
			motorLeft.waitComplete();
			motorRight.waitComplete();

			// DRAAIT HOOFD 90 GRADEN NAAR RECHTS
			motorOfHead.rotate(-mateVanHoofdDraaien, true); // of rotateTo?
			motorOfHead.waitComplete();

			int afstand2 = 10;
			while (afstand2 < 70) {
				SensorMode distance = infraRedSensor.getDistanceMode();
				float[] sample = new float[distance.sampleSize()];
				distance.fetchSample(sample, 0);
				afstand2 = (int) sample[0];
				System.out.println("Distance: " + afstand);
				motorLeft.rotate(360, true);
				motorRight.rotate(360, true);
				// motorLeft.waitComplete();
				// motorRight.waitComplete();
			}
			motorLeft.rotate(700, true);
			motorRight.rotate(700, true);
			motorLeft.waitComplete();
			motorRight.waitComplete();

			// DRAAIT ROBOT 90 GRADEN NAAR RECHTS
			motorLeft.rotate(mateVanRobotDraaien, true);
			motorRight.rotate(-mateVanRobotDraaien, true);
			motorLeft.waitComplete();
			motorRight.waitComplete();

			// DRAAIT HOOFD 90 GRADEN NAAR LINKS
			motorOfHead.rotate(mateVanHoofdDraaien, true); // of rotateTo?
			motorOfHead.waitComplete();

			// // LAAT ROBOT VOORUIT GAAN
			// motorLeft.rotate(1000, true);
			// motorRight.rotate(1000, true);
			// motorLeft.waitComplete();
			// motorRight.waitComplete();

			int afstand3 = 1000000;
			while (afstand3 > 40) {
				SensorMode distance = infraRedSensor.getDistanceMode();
				float[] sample = new float[distance.sampleSize()];
				distance.fetchSample(sample, 0);
				afstand3 = (int) sample[0];
				System.out.println("Distance: " + afstand3);
				motorLeft.rotate(360, true);
				motorRight.rotate(360, true);
				// motorLeft.waitComplete();
				// motorRight.waitComplete();
			}

			// DRAAIT ROBOT 90 GRADEN NAAR RECHTS
			motorLeft.rotate(mateVanRobotDraaien, true);
			motorRight.rotate(-mateVanRobotDraaien, true);
			motorLeft.waitComplete();
			motorRight.waitComplete();

			// DRAAIT HOOFD 90 GRADEN NAAR LINKS
			motorOfHead.rotate(mateVanHoofdDraaien, true); // of rotateTo?
			motorOfHead.waitComplete();

			int afstand9 = 10;
			while (afstand9 < 70) {
				SensorMode distance = infraRedSensor.getDistanceMode();
				float[] sample = new float[distance.sampleSize()];
				distance.fetchSample(sample, 0);
				afstand2 = (int) sample[0];
				System.out.println("Distance: " + afstand9);
				motorLeft.rotate(360, true);
				motorRight.rotate(360, true);
				// motorLeft.waitComplete();
				// motorRight.waitComplete();
			}
			motorLeft.rotate(700, true);
			motorRight.rotate(700, true);
			motorLeft.waitComplete();
			motorRight.waitComplete();

			// DRAAIT ROBOT 90 GRADEN NAAR RECHTS
			motorLeft.rotate(-mateVanRobotDraaien, true);
			motorRight.rotate(mateVanRobotDraaien, true);
			motorLeft.waitComplete();
			motorRight.waitComplete();

			// DRAAIT HOOFD 90 GRADEN NAAR LINKS
			motorOfHead.rotate(-mateVanHoofdDraaien, true); // of rotateTo?
			motorOfHead.waitComplete();

			motorLeft.stop();
			motorRight.stop();
		}

		motorLeft.close();
		motorRight.close();
		motorOfHead.close();

	}
}