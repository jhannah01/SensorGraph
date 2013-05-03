package com.blueodin.sensorgraph;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.text.format.DateUtils;

import com.blueodin.sensorgraph.SensorReadingValues.SensorReading;
import com.blueodin.sensorgraph.SensorReadingValues.SensorType;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.GraphView.LegendAlign;

import java.util.List;

public abstract class SensorHandler implements SensorEventListener {
	private final Context mContext;
	private final SensorManager mSensorManager;
	private final Sensor mSensor;
	protected OnSensorChanged sensorChangedCallback = null;
	private boolean mAutoScroll = true;
	private float[] mLastValues = new float[] { 0, 0, 0 };
	private int mCount = 0;
	private SensorReadingValues mSensorReadingValues;
	private GraphView mGraphView = null;
	
	public interface OnSensorChanged {
		public void onSensorEvent(float[] values);
		public void onAccuracyChange(int value);
	}
	
	protected SensorHandler(Context context, int sensorType) {
		mContext = context;
		mSensorReadingValues = ((SensorGraphApplication)context.getApplicationContext()).getSensorReadings();
		mSensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(sensorType);
	}
	
	protected Context getContext() {
		return mContext;
	}
	
	public SensorManager getSensorManager() {
		return mSensorManager;
	}
	
	public Sensor getSensor() {
		return mSensor;
	}
	
	public void registerListener() {
		mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	public void unregisterListener() {
		mSensorManager.unregisterListener(this);
	}
	
	public void setOnSensorChangedCallback(OnSensorChanged callback) {
		sensorChangedCallback = callback;
	}
	
	public void setAutoScroll(boolean value) {
		mAutoScroll = value;
	}
	
	protected boolean shouldAutoScroll() {
		return mAutoScroll;
	}
	
	public int getCount() {
		return mCount;
	}

	public abstract String getSensorUnit();
	public abstract String getFormattedSensorValue(float value);
	public abstract String getGraphTitle();
	public abstract int getValueCount();
	protected abstract List<GraphViewSeries> getGraphSeries();
	
	public GraphView getGraphView() {
		if(mGraphView == null)
			return buildGraphView();
		
		return mGraphView;
	}
	
	public GraphView buildGraphView() {
		return buildGraphView(15 * 1000);
	}
	
	public GraphView buildGraphView(int size) {
		mGraphView = new LineGraphView(getContext(), getGraphTitle()) {
			@Override
			protected String formatLabel(double value, boolean isValueX) {
				return formatGraphLabel(value, isValueX);
			}
		};
		
		for(GraphViewSeries series : getGraphSeries())
			mGraphView.addSeries(series);
		
		mGraphView.setLegendAlign(LegendAlign.BOTTOM);
		mGraphView.setLegendWidth(200);

		mGraphView.setScalable(true);
		mGraphView.setScrollable(true);
		mGraphView.setViewPort(System.currentTimeMillis() - size, size);
		
		return mGraphView;
	}
	
	public String getFormattedSensorValues(float[] values) {
		if(getValueCount() == 1)
			return getFormattedSensorValue(values[0]);
		
		String value = String.format("x=%s, y=%s", getFormattedSensorValue(values[0]), getFormattedSensorValue(values[1]));
		
		if(getValueCount() == 3)
			value = String.format("%s, z=%s", value, getFormattedSensorValue(values[2]));
		
		return value;
	}
	
	protected String formatGraphLabel(double value, boolean isValueX) {
		if (!isValueX)
			return getFormattedSensorValue((float)value);

		return DateUtils.getRelativeTimeSpanString((long)value, 
				System.currentTimeMillis(), 
				DateUtils.SECOND_IN_MILLIS, 
				DateUtils.FORMAT_ABBREV_RELATIVE).toString();
	}
	
	public float[] getLastValues() {
		return mLastValues;
	}

	public String getSensorType() {
		return SensorReadingValues.SensorType.fromSensor(mSensor).toString();
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		if (sensorChangedCallback != null)
			sensorChangedCallback.onAccuracyChange(accuracy);
	}
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		mLastValues = event.values;
		
		mSensorReadingValues.addReading(new SensorReading(SensorType.fromSensor(getSensor()), event.values));
		
		mCount++;
		
		if (sensorChangedCallback != null)
			sensorChangedCallback.onSensorEvent(event.values);
	}
}
