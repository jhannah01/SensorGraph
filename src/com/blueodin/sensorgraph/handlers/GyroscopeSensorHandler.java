package com.blueodin.sensorgraph.handlers;

import android.content.Context;
import android.hardware.Sensor;

import com.blueodin.sensorgraph.XYZSensorHandler;

public class GyroscopeSensorHandler extends XYZSensorHandler {
	public GyroscopeSensorHandler(Context context) {
		super(context, Sensor.TYPE_GYROSCOPE);
	}
	
	@Override
	public String getSensorUnit() {
		return "rads/sec";
	}

	@Override
	public String getGraphTitle() {
		return "Gyroscopic Rotation";
	}
	
}
