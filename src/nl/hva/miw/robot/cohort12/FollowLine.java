package nl.hva.miw.robot.cohort12;

import lejos.hardware.Brick;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.*;
import lejos.hardware.port.*;
import lejos.hardware.sensor.*;
import lejos.robotics.RegulatedMotor;

public class LineFollowerRight {

	Brick brick;
	private static UnregulatedMotor motorRight = new UnregulatedMotor(MotorPort.D);
	private static UnregulatedMotor motorLeft = new UnregulatedMotor(MotorPort.C);
	private static ColorSensor colorSensor = new ColorSensor(SensorPort.S4);

	// The constants below are for setting the right mode of a LineFollower
	final static int MODE_ONOFF_FOLLOWER_RIGHT = 1;
	final static int MODE_ONOFF_FOLLOWER_LEFT = 2;
	final static int MODE_ADJUSTED_P_CONTROLLER_RIGHT = 3;
	final static int MODE_ADJUSTED_P_CONTROLLER_LEFT = 4;
	final static int MODE_UNKNOWN_STARTING_POINT = 5;

	public LineFollowerRight() {
		super();
		brick = LocalEV3.get();
	}

	public static void main(String[] args) {
		LineFollowerRight marvin = new LineFollowerRight();
		marvin.run();
	}

	private void run() {

		 LineFollower ourLineFollower = new LineFollower(motorRight, motorLeft,
		 colorSensor); 
		 ourLineFollower.followLine(MODE_ONOFF_FOLLOWER_RIGHT);
	}
}