package com.blueodin.sensorgraph.handlers;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.text.format.DateUtils;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;

public class PressureSensorHandler extends SingleValueSensorHandler {
	private LineGraphView mGraphView;

	public PressureSensorHandler(Context context) {
		super(context, Sensor.TYPE_PRESSURE);
	}

	@Override
	public String getSensorUnit() {
		return "hPa";
	}

	@Override
	protected String getGraphTitle() {
		return "Air Pressure";
	}	
	
	@Override
	protected String getGraphSeriesTitle() {
		return "Pressure Values";
	}

	@Override
	protected GraphViewSeriesStyle getGraphViewSeriesStyle() {
		return new GraphViewSeriesStyle(Color.GREEN, 2);
	}
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		super.onSensorChanged(event);
		if(shouldAutoScroll() && (getCount() % 4) == 0)
			mGraphView.scrollToEnd();
	}
}
