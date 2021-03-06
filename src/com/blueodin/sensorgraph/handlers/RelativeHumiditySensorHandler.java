package com.blueodin.sensorgraph.handlers;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.Sensor;
import android.os.Build;

import com.blueodin.sensorgraph.SingleValueSensorHandler;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class RelativeHumiditySensorHandler extends SingleValueSensorHandler {	
	public RelativeHumiditySensorHandler(Context context) {
		super(context, Sensor.TYPE_RELATIVE_HUMIDITY);
	}

	@Override
	public String getSensorUnit() {
		return "%";
	}

	@Override
	protected String getGraphSeriesTitle() {
		return "Relative Humidity";
	}

	@Override
	public String getGraphTitle() {
		return "Relative Humidity";
	}

}
