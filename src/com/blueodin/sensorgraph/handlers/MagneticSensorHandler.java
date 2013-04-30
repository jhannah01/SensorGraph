package com.blueodin.sensorgraph.handlers;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.text.format.DateUtils;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewStyle;
import com.jjoe64.graphview.LineGraphView;

public class MagneticSensorHandler extends XYZSensorHandler {
	private LineGraphView mGraphView;
	
	public MagneticSensorHandler(Context context) {
		super(context, Sensor.TYPE_MAGNETIC_FIELD);
	}

	@Override
	public String getSensorUnit() {
		return "µT";
	}
	
	@Override
	public GraphView getSensorGraph(int size) {
		mGraphView = new LineGraphView(getContext(),
				"Magnetic Field") {
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

		for(GraphViewSeries series : getGraphSeriesMap().values())
			mGraphView.addSeries(series);
		
		GraphViewStyle graphStyle = mGraphView.getGraphViewStyle();
		graphStyle.setGridColor(Color.DKGRAY);
		
		mGraphView.setScalable(true);
		mGraphView.setScrollable(true);
		mGraphView.setDrawBackground(true);
		mGraphView.setViewPort(System.currentTimeMillis() - size, size);
		
		return mGraphView;
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		super.onSensorChanged(event);
		
		double timestamp = System.currentTimeMillis();
		
		addDataToSeries(AxisValue.X, new GraphViewData(timestamp, event.values[0]), false);
		addDataToSeries(AxisValue.Y, new GraphViewData(timestamp, event.values[1]), false);
		addDataToSeries(AxisValue.Z, new GraphViewData(timestamp, event.values[2]), false); 
		
		if(shouldAutoScroll() && (getCount() % 4) == 0)
			mGraphView.scrollToEnd();
	}
}
