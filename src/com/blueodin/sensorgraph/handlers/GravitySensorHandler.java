package com.blueodin.sensorgraph.handlers;

import android.content.Context;
import android.hardware.Sensor;

import com.blueodin.sensorgraph.XYZSensorHandler;

public class GravitySensorHandler extends XYZSensorHandler {
	public GravitySensorHandler(Context context) {
		super(context, Sensor.TYPE_GRAVITY);
	}

	@Override
	public String getSensorUnit() {
		return "m/s^2";
	}

	@Override
	public String getGraphTitle() {
		return "Gravity Readings";
	}

}
