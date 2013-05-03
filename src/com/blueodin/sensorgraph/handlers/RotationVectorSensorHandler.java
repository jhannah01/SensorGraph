package com.blueodin.sensorgraph.handlers;

import android.content.Context;
import android.hardware.Sensor;

import com.blueodin.sensorgraph.XYZSensorHandler;

public class RotationVectorSensorHandler extends XYZSensorHandler {
	public RotationVectorSensorHandler(Context context) {
		super(context, Sensor.TYPE_ROTATION_VECTOR);
	}

	@Override
	public String getSensorUnit() {
		return "";
	}

	@Override
	public String getGraphTitle() {
		return "Rotation Vector";
	}
}
