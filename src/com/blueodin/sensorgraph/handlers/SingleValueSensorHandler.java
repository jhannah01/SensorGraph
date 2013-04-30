package com.blueodin.sensorgraph.handlers;

import android.content.Context;
import android.graphics.Color;
import android.hardware.SensorEvent;

import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;

import java.util.HashMap;

public abstract class SingleValueSensorHandler extends SensorHandler {
	private GraphViewSeries mGraphSeries;
	
	protected SingleValueSensorHandler(Context context, int sensorType) {
		super(context, sensorType);
		setupGraphSeries();
	}

	protected GraphViewSeries getGraphSeries() {
		return mGraphSeries;
	}
	
	protected void addDataToSeries(GraphViewData data, boolean scrollToEnd) {
		mGraphSeries.appendData(data, scrollToEnd);
	}
	
	@Override
	protected void setupGraphView() {
		super.setupGraphView();
		getSensorGraph().addSeries(getGraphSeries());
	}
	
	protected String getGraphTitle() {
		return String.format("%s Values", getSensorType());
	}
	
	protected abstract String getGraphSeriesTitle();
	
	protected GraphViewSeriesStyle getGraphViewSeriesStyle() {
		return new GraphViewSeriesStyle(Color.RED, 2);
	}
	
	protected void setupGraphSeries() {
		mGraphSeries = new GraphViewSeries(getGraphSeriesTitle(), getGraphViewSeriesStyle(), new GraphViewData[] {});
	}
	
	@Override
	public String getFormattedSensorValue(float value) {
		if(Math.round(value) == value)
			return String.format("%d %s", (int)Math.round(value), getSensorUnit());
		
		return String.format("%.02f %s", value, getSensorUnit());
	}

	@Override
	public int getValueCount() {
		return 1;
	}
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		super.onSensorChanged(event);
		
		addDataToSeries(new GraphViewData(System.currentTimeMillis(), event.values[0]), false);
	}
}
