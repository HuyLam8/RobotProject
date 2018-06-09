package nl.hva.miw.robot.cohort12;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.Color;

public class ColorSensor extends EV3ColorSensor {
	float[] measuredValues;

	public ColorSensor(Port port) {
		super(port);
		// The LineFollower seems to work best in the red mode
		this.setRedMode();
		super.setFloodlight(true);
		super.setFloodlight(Color.RED);
	}

	/**
	 * Set color sensor to RED light level mode.
	 */
	public void setRedMode() {
		super.setCurrentMode("Red");
		measuredValues = new float[super.sampleSize()];
	}

	/**
	 * Return red light level.
	 * 
	 * @return Light level as range of 0 to 1.
	 */
	public float getRed() {
		super.fetchSample(measuredValues, 0);
		return measuredValues[0];
	}

}
