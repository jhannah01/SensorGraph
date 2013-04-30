package com.blueodin.sensorgraph.handlers;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.os.Build;
import android.text.format.DateUtils;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LineGraphView;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class RelativeHumiditySensorHandler extends SingleValueSensorHandler {	
	private LineGraphView mGraphView;

	public RelativeHumiditySensorHandler(Context context) {
		super(context, Sensor.TYPE_RELATIVE_HUMIDITY);
	}

	@Override
	public String getSensorUnit() {
		return "%";
	}

	@Override
	public GraphView getSensorGraph(int size) {
		mGraphView = new LineGraphView(getContext(), "Relative Humidity") {
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
		return "Relative Humidity";
	}
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		super.onSensorChanged(event);
		if(shouldAutoScroll() && (getCount() % 4) == 0)
			mGraphView.scrollToEnd();
	}

}
