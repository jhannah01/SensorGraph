package com.blueodin.sensorgraph.handlers;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.text.format.DateUtils;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LineGraphView;

public class ProximitySensorHandler extends SingleValueSensorHandler {
	private LineGraphView mGraphView;

	public ProximitySensorHandler(Context context) {
		super(context, Sensor.TYPE_PROXIMITY);
	}
	
	@Override
	public String getSensorUnit() {
		return "cm";
	}

	@Override
	public GraphView getSensorGraph(int size) {
		mGraphView = new LineGraphView(getContext(), "Proximity Values") {
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
	}

	@Override
	protected String getGraphSeriesTitle() {
		return "Proximity";
	}
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		super.onSensorChanged(event);
		
		if(shouldAutoScroll() && (getCount() % 4) == 0)
			mGraphView.scrollToEnd();
	}
	

}
