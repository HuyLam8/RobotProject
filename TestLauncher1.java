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
 * THIS CLASS TESTS WHETHER WE CAN - TRACK THE E.G. 2ND AND 12TH MEASURED VALUE
 * - COMPARE THE VALUES TO EACHOTHER - GIVE AN OUTPUT THAT DEPENDS ON WHETHER
 * THE 2ND OR THE 12TH VALUE IS BIGGER
 * 
 * THE OUTPUT SHOULD BE, FOR A WHITE BACKGROUND: 
 * *start* 
 * meting 1: meetwaarde ...
 * meting 2: meetwaarde ... 
 * ... 
 * meting 12: meetwaarde ... 
 * --- 
 * m2: <waarde van meting 2> 
 * m12: <waarde van meting 12> [this value should be higher than m2]
 * Start was links 
 * *end*
 * 
 * FOR A BLACK BACKGROUND: 
 * *start* 
 * <same as for white background> 
 * --- 
 * m2: <waarde van meting 2> 
 * m12: <waarde van meting 12> [this value should be lower than m2] 
 * Start was rechts 
 * *end*
 * 
 * @author Bjorn Goos
 *
 */

public class TestLauncher1 {

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
		float meting2 = -1;
		float meting12 = -1;

		while (Button.ESCAPE.isUp()) {
			colorValue = color.getRed();
			listWithRedVales.add(colorValue);
			System.out.println("meting " + listWithRedVales.size() + ": meetwaarde: " + colorValue);

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
				System.out.println("Start was links");
			else
				System.out.println("Start was rechts");
		}
	}

}
