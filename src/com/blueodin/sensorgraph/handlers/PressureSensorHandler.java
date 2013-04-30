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
	public GraphView getSensorGraph(int size) {
		mGraphView = new LineGraphView(getContext(), "Air Pressure") {
			@Override
			protected String formatLabel(double value, boolean isValueX) {
				if (!isValueX)
					return getFormattedSensorValue((float)value);

				return DateUtils.getRelativeTimeSpanString((long)value, 
						System.currentTimeMillis(), 
						DateUtils.SECOND_IN_MILLIS, 
						DateUtils.FORMAT_ABBREV_RELATIVE).toString();
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
