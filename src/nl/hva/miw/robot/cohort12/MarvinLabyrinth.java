package nl.hva.miw.robot.cohort12;

import lejos.hardware.Brick;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.*;
import lejos.hardware.port.*;
import lejos.hardware.sensor.*;
import lejos.robotics.RegulatedMotor;

public class MarvinLabyrinth {

	Brick brick;
//	private static UnregulatedMotor motorRight = new UnregulatedMotor(MotorPort.D);
//	private static UnregulatedMotor motorLeft = new UnregulatedMotor(MotorPort.C);
	private static RegulatedMotor motorRight = new EV3LargeRegulatedMotor(MotorPort.D);
	private static RegulatedMotor motorLeft = new EV3LargeRegulatedMotor(MotorPort.C);
	private static RegulatedMotor motorOfHead = new EV3MediumRegulatedMotor(MotorPort.B);
	private static UnregulatedMotor motorOfGrip = new UnregulatedMotor(MotorPort.A);
	//private static ColorSensor colorSensor = new ColorSensor(SensorPort.S4);
	private static EV3IRSensor infraredSensor = new EV3IRSensor(SensorPort.S1);
	private static Mario newMario = new Mario();

	// The constants below are for setting the right mode of a LineFollower
	final static int MODE_ONOFF_FOLLOWER_RIGHT = 1;
	final static int MODE_ONOFF_FOLLOWER_LEFT = 2;
	final static int MODE_ADJUSTED_P_CONTROLLER_RIGHT = 3;
	final static int MODE_ADJUSTED_P_CONTROLLER_LEFT = 4;
	final static int MODE_UNKNOWN_STARTING_POINT = 5;

	public MarvinLabyrinth() {
		super();
		brick = LocalEV3.get();
	}

	public static void main(String[] args) {
		MarvinLabyrinth marvin = new MarvinLabyrinth();
		marvin.run();
	}

	private void run() {

		AvoiderDieWerkt ourObjectAvoider = new AvoiderDieWerkt(motorRight, motorLeft, motorOfHead, motorOfGrip,
				infraredSensor);
		ourObjectAvoider.walkThroughRealLabyrinth();
	}
}