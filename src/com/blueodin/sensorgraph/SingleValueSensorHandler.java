package com.blueodin.sensorgraph;

import android.content.Context;
import android.graphics.Color;
import android.hardware.SensorEvent;

import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;

import java.util.ArrayList;
import java.util.List;

public abstract class SingleValueSensorHandler extends SensorHandler {
	private GraphViewSeries mGraphSeries;
	
	protected SingleValueSensorHandler(Context context, int sensorType) {
		super(context, sensorType);
		setupGraphSeries();
	}

	@Override
	protected List<GraphViewSeries> getGraphSeries() {
		List<GraphViewSeries> series = new ArrayList<GraphViewSeries>();
		series.add(mGraphSeries);
		return series;
	}
	
	protected void addDataToSeries(float value) {
		mGraphSeries.appendData(new GraphViewData(System.currentTimeMillis(), value), false);
	}
	
	protected abstract String getGraphSeriesTitle();
	protected GraphViewSeriesStyle getGraphViewSeriesStyle() {
		return new GraphViewSeriesStyle(Color.GREEN, 2);
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
		
		addDataToSeries(event.values[0]);
		
		if((getCount() % 4) == 0)
			getGraphView().scrollToEnd();
	}
}
