package com.blueodin.sensorgraph.handlers;

import android.content.Context;
import android.hardware.Sensor;

import com.blueodin.sensorgraph.XYZSensorHandler;

public class AccelerometerSensorHandler extends XYZSensorHandler {
	public AccelerometerSensorHandler(Context context) {
		super(context, Sensor.TYPE_ACCELEROMETER);
	}
	
	@Override
	public String getSensorUnit() {
		return "m/s^2";
	}

	@Override
	public String getGraphTitle() {
		return "Accelerometer Readings";
	}
}
