package nl.hva.miw.robot.cohort12;

import lejos.hardware.Button;
import lejos.hardware.motor.UnregulatedMotor;
import lejos.hardware.sensor.EV3IRSensor;
import lejos.hardware.sensor.SensorMode;
import lejos.robotics.RegulatedMotor;

public class Rotator extends Thread implements Runnable {

	protected static RegulatedMotor motorOfHead;
	private String direction;
	// protected static RegulatedMotor motorRight;
	// protected static RegulatedMotor motorLeft;

	// public void robotTurns90DegreesTo(String direction) {
	// int requiredMotorRotationFor90Degrees = 400; // negative for direction
	// if (direction.equals("L")) {
	// motorLeft.rotate(-requiredMotorRotationFor90Degrees, true);
	// motorRight.rotate(requiredMotorRotationFor90Degrees, true);
	// motorLeft.waitComplete();
	// motorRight.waitComplete();
	// }
	// if (direction.equals("R")) {
	// motorLeft.rotate(requiredMotorRotationFor90Degrees, true);
	// motorRight.rotate(-requiredMotorRotationFor90Degrees, true);
	// motorLeft.waitComplete();
	// motorRight.waitComplete();
	// }
	// }

	public Rotator(String direction, RegulatedMotor motorOfHead) {
		super();
		this.direction = direction;
		this.motorOfHead = motorOfHead;
	}

	public Rotator() {
		// TODO Auto-generated constructor stub
	}


	public void headTurns90DegreesTo(String direction) { 
    int motorRotationRequiredForHeadToMakeA90DegreesTurn = 65; 
    int delay = 2000; 
    if (direction.equals("L")) { 
      motorOfHead.setSpeed(100); 
      motorOfHead.rotate(motorRotationRequiredForHeadToMakeA90DegreesTurn, true); 
      motorOfHead.waitComplete(); 
    } 
    if (direction.equals("R")) { 
      motorOfHead.setSpeed(100); 
      motorOfHead.rotate(-motorRotationRequiredForHeadToMakeA90DegreesTurn, true); 
      motorOfHead.waitComplete(); 
       
    } 
  } 

	@Override
	public void run() {
		headTurns90DegreesTo(direction);
	}

}