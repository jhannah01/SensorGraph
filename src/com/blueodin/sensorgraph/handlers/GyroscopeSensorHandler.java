package com.blueodin.sensorgraph.handlers;

import android.content.Context;
import android.hardware.Sensor;

public class GyroscopeSensorHandler extends XYZSensorHandler {
	public GyroscopeSensorHandler(Context context) {
		super(context, Sensor.TYPE_GYROSCOPE);
	}
	
	@Override
	public String getSensorUnit() {
		return "rads/sec";
	}

	@Override
	protected String getGraphTitle() {
		return "Gyroscopic Rotation";
	}
	
}
