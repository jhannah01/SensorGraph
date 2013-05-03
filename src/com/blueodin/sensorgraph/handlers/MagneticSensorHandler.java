package com.blueodin.sensorgraph.handlers;

import android.content.Context;
import android.hardware.Sensor;
import com.blueodin.sensorgraph.XYZSensorHandler;

public class MagneticSensorHandler extends XYZSensorHandler {
	public MagneticSensorHandler(Context context) {
		super(context, Sensor.TYPE_MAGNETIC_FIELD);
	}

	@Override
	public String getSensorUnit() {
		return "µT";
	}

	@Override
	public String getGraphTitle() {
		return "Magnetic Field";
	}
}
