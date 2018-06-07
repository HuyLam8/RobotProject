package nl.hva.miw.robot.cohort12;

import lejos.hardware.motor.UnregulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.utility.Delay;

public class Grip {
	
	UnregulatedMotor motorVanGrip = new UnregulatedMotor(MotorPort.A);
		
	public Grip() {
		super();
	}

	public void openGrip() {
		motorVanGrip.backward(); 
		motorVanGrip.setPower(40);
		Delay.msDelay(3000);
	}
	
	public void closeGrip() {
		motorVanGrip.forward(); 
		motorVanGrip.setPower(40);
		Delay.msDelay(3000);
	}
	
}