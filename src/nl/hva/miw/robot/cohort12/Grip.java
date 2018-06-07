package nl.hva.miw.robot.cohort12;

import lejos.hardware.motor.UnregulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.utility.Delay;

public class Grip {
		
	public Grip() {
		super();
	}

	public void openGrip(UnregulatedMotor motorVanGrip) {
		motorVanGrip.backward(); 
		motorVanGrip.setPower(40);
		Delay.msDelay(3000);
	}
	
	public void closeGrip(UnregulatedMotor motorVanGrip) {
		motorVanGrip.forward(); 
		motorVanGrip.setPower(40);
		Delay.msDelay(3000);
	}
	
}