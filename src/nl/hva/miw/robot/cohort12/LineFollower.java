// deze versie is alleen voor de presentatie; zie de master-branch voor het final bestand (met nette code etc.)

//

//

//

//

//

// deze versie is alleen voor de presentatie; zie de master-branch voor het final bestand (met nette code etc.)

//

//

//

//


//


// deze versie is alleen voor de presentatie; zie de master-branch voor het final bestand (met nette code etc.)

package nl.hva.miw.robot.cohort12;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.motor.*;

/**
 * This class facilitates a robot to follow a line by zigzagging around one of
 * its borders while moving forward. This class provides several types of line
 * followers: 1. A basic OnOff-method 2. A P-controller with the options to 2a.
 * increase the speed/power at straight lines 2b. use a 'panic mode' when there
 * is a risk of losing the inner curve of a bend and 2c. deal with dashed lines;
 * 3. A 'smart' line follower which before zigzagging around the border of a
 * line first determines whether it is placed at the right or the left border;
 * 
 * @author Bjorn Goos
 *
 */

public class LineFollower {
	private static ColorSensor colorSensor;
	private static ControlDrive drive;

	// The measured color values of black, white and the border. This class
	// encompasses a 'calibrate'-method to adjust these figures.
	private static double redColorOfBlack = 0.85;
	private static double redColorOfWhite = 0.08;
	// With redColorOfBlack at 0.85 and redColorOfWhite at 0.08, the
	// redColorOfBorder equals 0.465
	private static double redColorOfBorder = (redColorOfBlack - redColorOfWhite) / 2 + redColorOfWhite;

	// The power of the robot's motors in case of an on/off-line follower
	// If robot is placed at the right border, then OuterMoter = right motor,
	// InnerMotor = left motor
	// If robot is placed at the left border, then OuterMoter = left motor,
	// InnerMotor = right motor
	private static int onOffPowerOuterMotor = 60;
	private static int onOffPowerInnerMotor = -15;

	// The required variables for setting and adjusting the P-controller
	// A higher kP implies that the robot will move faster to the border of the
	// line. This will generally lower the duration of zigzagging, but also
	// temporarily increase the extent of the zigzagging
	private static int kP = 150;
	// The robot moves faster at a higher power, but the risk of losing curves also
	// increases
	private static int steadyPower = 37;
	// If the robot keeps missing inner curves, then consider to increase (i.e. move
	// closer to zero) the panic boundary. Another option is to lower the above
	// mentioned steady power
	static boolean panicMode = true;
	private static double panicBoundary = -0.25;
	private static int panicKpInner = 1000;
	private static int panicSteadyPowerInner = 250;
	private static int panicKpOuter = -1000;
	private static int panicSteadyPowerOuter = -175;
	private static int panicPower = 100;
	// If the robot does not accelerate soon enough when it is driving almost
	// straight, then consider increasing the speedUpBoundary
	static boolean useSpeeder = false;
	private static double speedUpBoundary = 0.05;
	private static int speedPower = 50;
	private static int speedKp1 = 400;
	private static int speedKp2 = 100;
	// If the robot has to follow a dashed line as well, then use these variables
	static boolean dealWithDashedLines = false;
	private static double dashedLineHelperBoundary = 0.25;
	private static double dashedLineBoundary = 0.35;
	private static int dashedLinePowerOuterMotor = 20;
	private static int dashedLinePowerInnerMotor = 30;

	// The required variables for the line follower that determines at which border
	// of the line the robot is starting
	final static int testTurnRightMotor = 60;
	final static int testTurnLeftMotor = -15;

	public LineFollower(UnregulatedMotor motorRight, UnregulatedMotor motorLeft, ColorSensor colorSensor) {
		super();
		LineFollower.colorSensor = colorSensor;
		LineFollower.drive = new ControlDrive(motorRight, motorLeft);
	}

	/**
	 * Make the robot follow the line in a particular way.
	 * 
	 * @param mode
	 *            The mode of the LineFollower.
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
			OnOffRight();
			break;
		case 2:
			OnOffLeft();
			break;
		case 3:
			adjustablePControllerRight();
			break;
		case 4:
			adjustablePControllerLeft();
			break;
		case 5:
			startFromUnknowBorder();
		}
	}

	/**
	 * In the most simple version of the line follower, the robot always moves
	 * towards the border of the line by, if it starts on the right border, going to
	 * the left when it reads a surface color that is more white than the border
	 * respectively going to the right when it reads a color that is more black than
	 * the border.
	 */

	private void OnOffRight() {
		while (Button.ESCAPE.isUp()) {
			float colorValue = colorSensor.getRed();
			double tooWhite = colorValue - redColorOfBorder;
			System.out.println("color: " + colorValue);
			if (tooWhite > 0) {
				drive.setPower(onOffPowerOuterMotor, onOffPowerInnerMotor);
			} else {
				drive.setPower(onOffPowerInnerMotor, onOffPowerOuterMotor);
			}
		}
	}

	private void OnOffLeft() {
		while (Button.ESCAPE.isUp()) {
			float colorValue = colorSensor.getRed();
			double tooWhite = colorValue - redColorOfBorder;
			System.out.println("color: " + colorValue);
			if (tooWhite > 0) {
				drive.setPower(onOffPowerInnerMotor, onOffPowerOuterMotor);
			} else {
				drive.setPower(onOffPowerOuterMotor, onOffPowerInnerMotor);
			}
		}
	}

	/**
	 * This is a P-controller with 3 possible adjustments: 1. If there is a risk of
	 * losing the inner curve of a bend, then there is a very strong correction.
	 * This correction is needed since missing an inner curve means ending up on the
	 * wrong side of the line; 2. If the robot is moving over a 'straight line',
	 * i.e. the error is very small, then the 'speed' (i.e. the power of the
	 * unregulated motors) increases; by this, the robot will move extra fast on
	 * straight lines.
	 * 
	 * This picture shows the idea behind the first 2 adjustments:
	 * https://imgur.com/a/3DXAoaZ (with pK= 200, goalPower = 50, speedPower = 70)
	 * 
	 * 3. If the robot has to follow a dashed line as well, then, with this method,
	 * it can only do so if it *only* measures a very high tooWhite value (i.e. the
	 * dashedLineColorValue) if it passes a dashed line. In other words, it should
	 * be prevented that the robot measures a very high white value if it e.g.
	 * misses the outer curve of a bend.
	 * 
	 * This adjusted P-controller can easily be turned into a standard P-controller
	 * by setting all the booleans (useSpeeder, dealWithDashedLines) at false.
	 */
	private void adjustablePControllerRight() {
		while (Button.ESCAPE.isUp()) {
			int rightPower;
			int leftPower;
			float colorValue = colorSensor.getRed();
			double tooWhite = colorValue - redColorOfBorder;
			// If there is a risk of losing the 'inner curve' of a bend, then a 'panic mode'
			// applies
			if (panicMode == true) {
				if (tooWhite < panicBoundary) {
					// rightPower = Math.max(-100, Math.min(100, (int) (panicSteadyPowerOuter +
					// panicKpOuter *
					// tooWhite)));
					// leftPower = Math.max(-100, Math.min(100, (int) (panicSteadyPowerInner -
					// panicKpInner *
					// tooWhite)));
					rightPower = -panicPower;
					leftPower = panicPower;
					drive.setPower(rightPower, leftPower);
				}
			}
			// If the robot is driving over a more or less straight line, then the speed
			// goes up
			if (useSpeeder == true) {
				if (tooWhite < speedUpBoundary && tooWhite > -speedUpBoundary) {
					if (tooWhite > -speedUpBoundary) {
						rightPower = Math.max(-100, (Math.min(100, (int) (speedPower + speedKp1 * tooWhite))));
						leftPower = Math.max(-100, Math.min(100, (int) (speedPower - speedKp1 * tooWhite)));
						drive.setPower(rightPower, leftPower);
					}
					if (tooWhite < speedUpBoundary) {
						rightPower = Math.max(-100, (Math.min(100, (int) (speedPower + speedKp2 * tooWhite))));
						leftPower = Math.max(-100, Math.min(100, (int) (speedPower - speedKp2 * tooWhite)));
						drive.setPower(rightPower, leftPower);
					}
				}
			}
			// If the robot has to follow a dashed line as well, then it has to first detect
			// that there is a dashed line. In our method, the robot will see (i.e. assume
			// there is) a dashed line if the color value is above a certain threshold (i.e.
			// the dashedLineBoundary). In this case, the robot's outer motor should
			// have a somewhat lower power than it's inner motor. This way, the
			// robot will, hopefully, continue to the next black part of the dashed line.
			// Ideally, the robot will move something like this: https://imgur.com/a/VC9pdkQ
			// With this method, there is of course the risk that the robot will,
			// ultimately, end up either on the wrong side of the line or miss the line
			// completely.

			if (dealWithDashedLines == true) {
				// Make sure that the robot never reaches a 'very white' surface when e.g.
				// missing the outer curve of a bend
				if (tooWhite > dashedLineHelperBoundary) {
					rightPower = panicPower;
					leftPower = -panicPower;
					drive.setPower(rightPower, leftPower);
				}
				//
				else if (tooWhite > dashedLineBoundary) {
					rightPower = dashedLinePowerOuterMotor;
					leftPower = dashedLinePowerInnerMotor;
					drive.setPower(rightPower, leftPower);
				}
			}

			// This is the basic P-controller
			else {
				rightPower = Math.max(-100, Math.min(100, (int) (steadyPower + kP * tooWhite)));
				leftPower = Math.max(-100, Math.min(100, (int) (steadyPower - kP * tooWhite)));
				drive.setPower(rightPower, leftPower);
			}
		}
	}

	private void adjustablePControllerLeft() {
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
	 *	 ww|bbb|ww
	 *   ww|bbb|ww
	 *   P | Q |
	 *    \|  \|
	 *     X   Y
	 *     
	 * The robot then uses this information to either follow the left or right border.
	 *	
	 */

	private void startFromUnknowBorder() {
		int aantalMetingen = 0;
		float meting1 = -1;
		float meting13 = -1;

		while (Button.ESCAPE.isUp()) {
			float colorValue = colorSensor.getRed();
			aantalMetingen++;
			// The first ten measurements are intended to decide where the robot is compared
			// to the line. To do so, it is forced to move to the left and collect the
			// required data.
			if (aantalMetingen <= 14) {
				drive.setPower(testTurnRightMotor, testTurnLeftMotor);
				if (aantalMetingen == 1) {
					meting1 = colorValue;
				}
				if (aantalMetingen == 13) {
					meting13 = colorValue;
				}
			}
			// With the collected data, it can be determined whether the robot was at the
			// right or left border of the line, after which the robot continues its path
			// accordingly
			else {
				System.out.println("meting 1: " + meting1);
				System.out.println("meting 13: " + meting13);

				if (meting13 > meting1) {
					System.out.println("Start was at left border");
					this.adjustablePControllerLeft();
				} else {
					System.out.println("Start was at right border");
					this.adjustablePControllerRight();
				}
			}
		}

	}

	public void calibrate() {
		System.out.println("Put the sensor on black");
		Sound.beepSequenceUp();
		Button.waitForAnyPress();
		LineFollower.redColorOfBlack = colorSensor.getRed();
		System.out.println("Black: " + redColorOfBlack);
		System.out.println("Put the sensor on white");
		Sound.beepSequenceUp();
		Button.waitForAnyPress();
		LineFollower.redColorOfWhite = colorSensor.getRed();
		System.out.println("White: " + redColorOfWhite);
	}
}
