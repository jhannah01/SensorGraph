package com.blueodin.sensorgraph.handlers;

import android.content.Context;
import android.hardware.Sensor;

import com.blueodin.sensorgraph.SensorHandler;
import com.jjoe64.graphview.GraphView;
public class GravitySensorHandler extends XYZSensorHandler {
	public GravitySensorHandler(Context context) {
		super(context, Sensor.TYPE_GRAVITY);
	}

	@Override
	public String getSensorUnit() {
		return "m/s^2";
	}

	@Override
	public GraphView getSensorGraph(int size) {
		// TODO Auto-generated method stub
		return null;
	}

}
