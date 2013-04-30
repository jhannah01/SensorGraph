package com.blueodin.sensorgraph;

import android.content.Context;
import android.hardware.Sensor;

import com.blueodin.sensorgraph.handlers.XYZSensorHandler;
import com.jjoe64.graphview.GraphView;

public class RotationVectorSensorHandler extends XYZSensorHandler {
	public RotationVectorSensorHandler(Context context) {
		super(context, Sensor.TYPE_ROTATION_VECTOR);
	}

	@Override
	public String getSensorUnit() {
		return "";
	}

	@Override
	public GraphView getSensorGraph(int size) {
		// TODO Auto-generated method stub
		return null;
	}

}
