package com.blueodin.sensorgraph.handlers;

import android.content.Context;
import android.hardware.Sensor;
import com.blueodin.sensorgraph.SingleValueSensorHandler;

public class LightSensorHandler extends SingleValueSensorHandler {
	public LightSensorHandler(Context context) {
		super(context, Sensor.TYPE_LIGHT);
	}
	
	@Override
	public String getSensorUnit() {
		return "lux";
	}

	@Override
	public String getGraphTitle() {
		return "Ambient Light";
	}

	@Override
	protected String getGraphSeriesTitle() {
		return "Light Level";
	}
}
