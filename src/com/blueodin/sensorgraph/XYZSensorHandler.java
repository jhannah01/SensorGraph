package com.blueodin.sensorgraph;

import android.content.Context;
import android.graphics.Color;
import android.hardware.SensorEvent;

import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	@Override
	protected List<GraphViewSeries> getGraphSeries() {
		return new ArrayList<GraphViewSeries>(mGraphSeriesMap.values());
	}
	
	protected HashMap<AxisValue,GraphViewSeriesStyle> getGraphViewSeriesStyles() {
		HashMap<AxisValue, GraphViewSeriesStyle> seriesStyles = new HashMap<AxisValue, GraphViewSeriesStyle>();
		seriesStyles.put(AxisValue.X, new GraphViewSeriesStyle(Color.RED, 2));
		seriesStyles.put(AxisValue.Y, new GraphViewSeriesStyle(Color.GREEN, 2));
		seriesStyles.put(AxisValue.Z, new GraphViewSeriesStyle(Color.CYAN, 2));
		return seriesStyles;
	}
	
	protected void setupAxisSeries() {
		for(Map.Entry<AxisValue, GraphViewSeriesStyle> entry : getGraphViewSeriesStyles().entrySet())
			mGraphSeriesMap.put(entry.getKey(), new GraphViewSeries(entry.getKey().toString(), entry.getValue(), new GraphViewData[] {})); 
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
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		super.onSensorChanged(event);
		
		double timestamp = System.currentTimeMillis();
		
		addDataToSeries(AxisValue.X, new GraphViewData(timestamp, event.values[0]), false);
		addDataToSeries(AxisValue.Y, new GraphViewData(timestamp, event.values[1]), false);
		addDataToSeries(AxisValue.Z, new GraphViewData(timestamp, event.values[2]), false); 
		
		if(shouldAutoScroll() && (getCount() % 4) == 0)
			getGraphView().scrollToEnd();
	}
}
