package com.blueodin.sensorgraph.handlers;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.text.format.DateUtils;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;
import com.jjoe64.graphview.LineGraphView;

public class LightSensorHandler extends SingleValueSensorHandler {
	public LightSensorHandler(Context context) {
		super(context, Sensor.TYPE_LIGHT);
	}

	private LineGraphView mGraphView;
	
	@Override
	public String getSensorUnit() {
		return "lux";
	}
	
	/*@Override
	public GraphView getSensorGraph(int size) {
		mGraphView = new LineGraphView(getContext(), "Ambient Light") {
			@Override
			protected String formatLabel(double value, boolean isValueX) {
				return formatGraphLabel(value, isValueX);
			}
		};

		mGraphView.addSeries(getGraphSeries());
		
		mGraphView.setScalable(true);
		mGraphView.setScrollable(true);
		mGraphView.setDrawBackground(true);
		mGraphView.setViewPort(System.currentTimeMillis() - size, size);
		
		return mGraphView;
	}*/
	
	@Override
	protected String getGraphTitle() {
		return "Ambient Light";
	}

	@Override
	protected String getGraphSeriesTitle() {
		return "Light Level";
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
