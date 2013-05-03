package com.blueodin.sensorgraph.handlers;

import android.content.Context;
import android.hardware.Sensor;

import com.blueodin.sensorgraph.SingleValueSensorHandler;

public class PressureSensorHandler extends SingleValueSensorHandler {
	public PressureSensorHandler(Context context) {
		super(context, Sensor.TYPE_PRESSURE);
	}

	@Override
	public String getSensorUnit() {
		return "hPa";
	}

	@Override
	public String getGraphTitle() {
		return "Air Pressure";
	}	
	
	@Override
	protected String getGraphSeriesTitle() {
		return "Pressure Values";
	}
}
