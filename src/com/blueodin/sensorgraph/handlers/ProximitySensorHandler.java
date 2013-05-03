package com.blueodin.sensorgraph.handlers;

import android.content.Context;
import android.hardware.Sensor;

import com.blueodin.sensorgraph.SingleValueSensorHandler;

public class ProximitySensorHandler extends SingleValueSensorHandler {
	public ProximitySensorHandler(Context context) {
		super(context, Sensor.TYPE_PROXIMITY);
	}
	
	@Override
	public String getSensorUnit() {
		return "cm";
	}
	
	@Override
	public String getGraphTitle() {
		return "Proximity Values";
	}

	@Override
	protected String getGraphSeriesTitle() {
		return "Proximity";
	}
}
