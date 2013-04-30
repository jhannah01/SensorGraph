package com.blueodin.sensorgraph.handlers;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.text.format.DateUtils;

import com.blueodin.sensorgraph.SensorHandler;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;

public class GyroscopeSensorHandler extends SensorHandler {
	private LineGraphView mGraphView;
	
	private GraphViewSeries mGraphSeriesX = new GraphViewSeries("X Axis", new GraphViewSeriesStyle(Color.RED, 2), new GraphViewData[] { });
	private GraphViewSeries mGraphSeriesY = new GraphViewSeries("Y Axis", new GraphViewSeriesStyle(Color.GREEN, 2), new GraphViewData[] { });
	private GraphViewSeries mGraphSeriesZ = new GraphViewSeries("Z Axis", new GraphViewSeriesStyle(Color.CYAN, 2), new GraphViewData[] { });
	private int mCount = 0;
	
	public GyroscopeSensorHandler(Context context) {
		super(context, Sensor.TYPE_GYROSCOPE);
	}
	
	@Override
	public String getSensorUnit() {
		return "rads/sec";
	}

	@Override
	public String getFormattedSensorValue(float value) {
		return String.format("%d %s", (int)value, getSensorUnit());
	}

	@Override
	public int getValueCount() {
		return 3;
	}
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		super.onSensorChanged(event);
		
		double timestamp = System.currentTimeMillis();
		
		mGraphSeriesX.appendData(new GraphViewData(timestamp, event.values[0]), false);
		mGraphSeriesY.appendData(new GraphViewData(timestamp, event.values[1]), false);
		mGraphSeriesZ.appendData(new GraphViewData(timestamp, event.values[2]), false);
	
		mCount++;
		
		if(shouldAutoScroll() && (mCount % 4) == 0)
			mGraphView.scrollToEnd();
	}

	@Override
	public GraphView getSensorGraph(int size) {
		mGraphView = new LineGraphView(getContext(), "Gyroscopic Rotation") {
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

		mGraphView.addSeries(mGraphSeriesX);
		mGraphView.addSeries(mGraphSeriesY);
		mGraphView.addSeries(mGraphSeriesZ);
		
		mGraphView.setScalable(true);
		mGraphView.setScrollable(true);
		mGraphView.setDrawBackground(true);
		mGraphView.setViewPort(System.currentTimeMillis() - size, size);
		
		return mGraphView;
	}

}
