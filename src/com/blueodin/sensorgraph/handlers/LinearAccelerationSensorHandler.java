package com.blueodin.sensorgraph.handlers;

import android.content.Context;
import android.hardware.Sensor;

import com.blueodin.sensorgraph.XYZSensorHandler;

public class LinearAccelerationSensorHandler extends XYZSensorHandler {
	public LinearAccelerationSensorHandler(Context context) {
		super(context, Sensor.TYPE_LINEAR_ACCELERATION);
	}

	@Override
	public String getSensorUnit() {
		return "m/s^2";
	}

	@Override
	public String getGraphTitle() {
		return "Linear Acceleration";
	}
}
