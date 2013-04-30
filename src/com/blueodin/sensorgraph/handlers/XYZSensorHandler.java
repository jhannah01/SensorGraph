package com.blueodin.sensorgraph.handlers;

import android.content.Context;
import android.graphics.Color;

import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;

import java.util.HashMap;

public abstract class XYZSensorHandler extends SensorHandler {
	private HashMap<AxisValue, GraphViewSeries> mGraphSeriesMap = new HashMap<AxisValue, GraphViewSeries>();
	
	public enum AxisValue {
		X,
		Y,
		Z;
		
		@Override
		public String toString() {
			return String.format("%s Axis", super.toString()); 
		}
	}
	
	protected XYZSensorHandler(Context context, int sensorType) {
		super(context, sensorType);
		setupAxisSeries();
	}

	protected HashMap<AxisValue, GraphViewSeries> getGraphSeriesMap() {
		return mGraphSeriesMap;
	}
	
	protected GraphViewSeries getGraphSeries(AxisValue axis) {
		return mGraphSeriesMap.get(axis);
	}
	
	protected void addDataToSeries(AxisValue axis, GraphViewData data, boolean scrollToEnd) {
		getGraphSeries(axis).appendData(data, scrollToEnd);
	}
	
	protected String getGraphTitle() {
		return String.format("%s Values", getSensorType());
	}
	
	protected void setupAxisSeries() {
		mGraphSeriesMap.put(AxisValue.X, new GraphViewSeries("X Axis", new GraphViewSeriesStyle(Color.RED, 2), new GraphViewData[] {}));
		mGraphSeriesMap.put(AxisValue.Y, new GraphViewSeries("Y Axis", new GraphViewSeriesStyle(Color.GREEN, 2), new GraphViewData[] {}));
		mGraphSeriesMap.put(AxisValue.Z, new GraphViewSeries("Z Axis", new GraphViewSeriesStyle(Color.CYAN, 2), new GraphViewData[] {})); 
	}
	
	@Override
	public String getFormattedSensorValue(float value) {
		if(Math.round(value) == value)
			return String.format("%d %s", (int)Math.round(value), getSensorUnit());
		
		return String.format("%.02f %s", value, getSensorUnit());
	}

	@Override
	public int getValueCount() {
		return 3;
	}
}
