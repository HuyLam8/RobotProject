package nl.hva.miw.robot.cohort12;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.motor.*;

/**
 * This class facilitates a robot to follow a line by zigzagging around one of
 * its borders while moving forward. There class provides both basic and more
 * advanced versions which increase the speed and lower the likelihood of the
 * robot missing an inner curve, hence ending on the wrong side of the line.
 * Moreover, the class encompasses a line follower in which the robot
 * 'discovers' on which border of the line he starts and then follows the line
 * along this border.
 * 
 * @author Bjorn Goos
 *
 */

public class LineFollower {
	private static ColorSensor colorSensor;
	private static ControlDrive drive;
	private static double redColorOfBlack = 0.85;
	private static double redColorOfWhite = 0.08;
	private static double redColorOfBorder = 0.43;

	public LineFollower(UnregulatedMotor motorRight, UnregulatedMotor motorLeft, ColorSensor colorSensor) {
		super();
		LineFollower.colorSensor = colorSensor;
		LineFollower.drive = new ControlDrive(motorRight, motorLeft);
	}

	/**
	 * Make the robot follow the line in a particular way.
	 * 
	 * @param mode
	 *            The mode of the LineFollower. There are five different modes which
	 *            each make the robot follow the line in a somewhat different way.
	 */

	public void followLine(int mode) {
		System.out.println("Line Follower\n");
		// Flashes green lights and makes sound when ready
		Button.LEDPattern(4);
		Sound.beepSequenceUp();
		System.out.println("Press any key to start the Line Follower");
		Button.waitForAnyPress();

		switch (mode) {
		case 1:
			simpleLineFollowerRightBorder();
			break;
		case 2:
			simpleLineFollowerLeftBorder();
			break;
		case 3:
			somewhatMoreAdvancedLineFollowerRightBorder();
			break;
		case 4:
			somewhatMoreAdvancedLineFollowerLeftBorder();
			break;
		case 5:
			somewhatMoreAdvancedLineFollowerUnkownBorder();
		}
	}

	/**
	 * In the most simple version of the line follower, the robot always moves
	 * towards the line by, if it starts on the right border, going to the left when
	 * he reads a surface color that is more white than the border respectively
	 * going to the left when he reads a color that is more black than the surface.
	 */

	public void simpleLineFollowerRightBorder() {
		while (Button.ESCAPE.isUp()) {
			drive.goForward();
			float colorValue = colorSensor.getRed();
			double tooWhite = colorValue - redColorOfBorder;
			System.out.println("color: " + colorValue);
			if (tooWhite > 0) {
				drive.normalTurnLeft();
			} else {
				drive.normalTurnRight();
			}
		}
	}

	public void simpleLineFollowerLeftBorder() {
		while (Button.ESCAPE.isUp()) {
			float colorValue = colorSensor.getRed();
			double tooWhite = colorValue - redColorOfBorder;
			if (tooWhite > 0) {
				drive.normalTurnRight();
			} else {
				drive.normalTurnLeft();
			}
		}
	}
	
	public void pControllerWithPanicModeOnRightBorder() {
		while (Button.ESCAPE.isUp()) {
			int rightPower;
			int leftPower;
			float colorValue = colorSensor.getRed();
			double tooWhite = colorValue - redColorOfBorder;
			// make the 'correction' proportional on the tooWhite-error
			if (tooWhite > -0.25) {
				rightPower = Math.max(-100, Math.min(100, (int) (steadyPowerPercentage + kP * tooWhite)));
				leftPower = Math.max(-100, Math.min(100, (int) (steadyPowerPercentage - kP * tooWhite)));
				drive.setPower(rightPower, leftPower);
			}
			// if high risk of 'losing' the innercurve, then let the robot turn around its own axis ('panic mode')
			else {
				rightPower = -100;
				leftPower = 100;
				drive.setPower(rightPower, leftPower);
			}
		}
	}

	/**
	 * In the somewhat more advanced version, there are two improvements: - the
	 * robot zigzags less when it reads a color value close to the color value of
	 * the border; - the robot will do more to prevent losing the inner curve
	 * (losing the outer curve is less problematic, since the robot will then stay
	 * on the correct side of the line and ultimately zigzag his way back to the
	 * line)
	 */

	public void somewhatMoreAdvancedLineFollowerRightBorder() {
		while (Button.ESCAPE.isUp()) {
			float colorValue = colorSensor.getRed();
			double tooWhite = colorValue - redColorOfBorder;

			// If the robot is *very* close to the border,
			// then drive straight
			if (tooWhite < 0.02 && tooWhite > -0.02) {
				drive.moveStraightAtHighSpeed();
			}

			// If the robot is close to the border,
			// then let it move less back and forth and more in a straight line
			else if (tooWhite < 0.05 && tooWhite > -0.05) {
				if (tooWhite > 0) {
					drive.verySmallTurnLeft();
				} else {
					drive.verySmallTurnRight();
				}
			}

			// If there is a risk of 'losing the inside of a bend',
			// then take a sharp turn,
			// since then the risk is too high to end up at the 'wrong' side of the line
			else if (tooWhite < -0.2) {
				drive.strongTurnRight();
			}

			// Last chance to avoid losing the bend, time for extreme measurements!
			else if (tooWhite < -0.25) {
				drive.extremeTurnRight();
			}

			// In all other cases, let him drive as in case of the SimpleLineFollower
			else {
				if (tooWhite > 0) {
					drive.normalTurnLeft();
				} else {
					drive.normalTurnRight();
				}
			}
		}

	}

	public void somewhatMoreAdvancedLineFollowerLeftBorder() {
		while (Button.ESCAPE.isUp()) {
			float colorValue = colorSensor.getRed();
			double tooWhite = colorValue - redColorOfBorder;
			if (tooWhite < 0.02 && tooWhite > -0.02) {
				drive.moveStraightAtHighSpeed();
			} else if (tooWhite < 0.05 && tooWhite > -0.05) {
				if (tooWhite > 0) {
					drive.verySmallTurnRight();
				} else {
					drive.verySmallTurnLeft();
				}
			} else if (tooWhite < -0.2) {
				drive.strongTurnLeft();
			} else if (tooWhite < -0.25) {
				drive.extremeTurnLeft();
			} else {
				if (tooWhite > 0) {
					drive.normalTurnRight();
				} else {
					drive.normalTurnLeft();
				}
			}
		}

	}

	/*-
	 * This method first lets the robot determine on which side of the line it is. 
	 * This is done by forcing the robot to drive to the left for a short time. 
	 * Then, the color value of the first measurement is compared to the value of 
	 * the 10th measurement. If there is an increase in the color value, then the 
	 * robot has started at the left border of the line, i.e. moved from X to P in
	 * the picture below. On contrary, if there is an decrease in the color value, 
	 * then the robot started at the right border (i.e. moved from Y to Q).
	 * 
	 * [w = white, b = black]
	 * 
	 *	 ww|bbb|ww
	 *   ww|bbb|ww
	 *   P | Q |
	 *    \|  \|
	 *     X   Y
	 *     
	 * The robot then uses this information to either follow the left or right border.
	 *	
	 */

	public void somewhatMoreAdvancedLineFollowerUnkownBorder() {
		int aantalMetingen = 0;
		float meting1 = -1;
		float meting13 = -1;

		while (Button.ESCAPE.isUp()) {
			float colorValue = colorSensor.getRed();
			aantalMetingen++;
			// The first ten measurements are intended to decide where the robot is compared
			// to the line. To do so, it is forced to move to the left and collect the
			// required data.
			if (aantalMetingen <= 30) {
				drive.normalTurnLeft();
				if (aantalMetingen == 1) {
					meting1 = colorValue;
				}
				if (aantalMetingen == 30) {
					meting13 = colorValue;
				}
			}
			// With the collected data, it can be determined whether the robot was at the
			// right or left border of the line, after which the robot continues its path
			// accordingly
			else if (aantalMetingen == 31) {
				System.out.println("meting 1: " + meting1);
				System.out.println("meting 30: " + meting13);

				if (meting13 > meting1) {
					System.out.println("Start was at left.");
					this.somewhatMoreAdvancedLineFollowerLeftBorder();
				} else {
					System.out.println("Start was at right.");
					this.somewhatMoreAdvancedLineFollowerRightBorder();
				}
			}
		}
	}
}