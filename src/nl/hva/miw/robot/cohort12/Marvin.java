package nl.hva.miw.robot.cohort12;

import lejos.hardware.Brick;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.*;
import lejos.hardware.port.*;
import lejos.hardware.sensor.*;
import lejos.robotics.RegulatedMotor;

public class Marvin {

	Brick brick;
	private static UnregulatedMotor motorRight = new UnregulatedMotor(MotorPort.D);
	private static UnregulatedMotor motorLeft = new UnregulatedMotor(MotorPort.C);
	private static RegulatedMotor motorOfHead = new EV3MediumRegulatedMotor(MotorPort.B);
	private static UnregulatedMotor motorOfGrip = new UnregulatedMotor(MotorPort.A);
	private static ColorSensor colorSensor = new ColorSensor(SensorPort.S4);
	private static EV3IRSensor infraRedSensor = new EV3IRSensor(SensorPort.S1);
  
	final static int MODE_SIMPLE_LINE_FOLLOWER_RIGHT = 1;
	final static int MODE_SIMPLE_LINE_FOLLOWER_LEFT = 2;
	final static int MODE_SOMEWHAT_ADVANCED_LINE_FOLLOWER_RIGHT = 3;
	final static int MODE_SOMEWHAT_ADVANCED_LINE_FOLLOWER_LEFT = 4;
	final static int MODE_SOMEWHAT_ADVANCED_LINE_FOLLOWER_BORDER_UNKNOWN = 5;

	public Marvin() {
		super();
		brick = LocalEV3.get();
	}

	public static void main(String[] args) {
		Marvin marvin = new Marvin();
		marvin.run();
	}

	private void run() {
		LineFollower ourLineFollower = new LineFollower(motorRight, motorLeft, colorSensor);
		ourLineFollower.followLine(MODE_SOMEWHAT_ADVANCED_LINE_FOLLOWER_BORDER_UNKNOWN);
	}
}