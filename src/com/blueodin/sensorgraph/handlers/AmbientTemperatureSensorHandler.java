package com.blueodin.sensorgraph.handlers;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.Sensor;
import android.os.Build;

import com.blueodin.sensorgraph.SingleValueSensorHandler;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class AmbientTemperatureSensorHandler extends SingleValueSensorHandler {
	public AmbientTemperatureSensorHandler(Context context) {
		super(context, Sensor.TYPE_AMBIENT_TEMPERATURE);
	}
	
	@Override
	protected String getGraphSeriesTitle() {
		return "Ambient Temp.";
	}

	@Override
	public String getSensorUnit() {
		return "°C";
	}

	@Override
	public String getGraphTitle() {
		return "Ambient Temperature Readings";
	}

}
