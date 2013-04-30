package com.blueodin.sensorgraph;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.blueodin.sensorgraph.SensorReadingValues.SensorReading;
import com.blueodin.sensorgraph.SensorReadingValues.SensorType;
import com.jjoe64.graphview.GraphView;

public abstract class SensorHandler implements SensorEventListener {
	private final Context mContext;
	private final SensorManager mSensorManager;
	private final Sensor mSensor;
	protected OnSensorChanged sensorChangedCallback = null;
	private boolean mAutoScroll = true;
	private float[] mLastValues = new float[] { 0, 0, 0 };
	private int mCount = 0;
	private SensorReadingValues mSensorReadingValues;
	
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
	
	public enum AxisValue {
		X,
		Y,
		Z;
		
		@Override
		public String toString() {
			return String.format("%s Axis", super.toString()); 
		}
	}
	
	public static String getSensorType(Sensor sensor) {
		switch(sensor.getType()) {
		case Sensor.TYPE_ACCELEROMETER:
			return "Accelerometer";
		case Sensor.TYPE_AMBIENT_TEMPERATURE:
			return "Ambient Temperature";
		case Sensor.TYPE_GRAVITY:
			return "Gravity";
		case Sensor.TYPE_GYROSCOPE:
			return "Gyroscope";
		case Sensor.TYPE_LIGHT:
			return "Light";
		case Sensor.TYPE_LINEAR_ACCELERATION:
			return "Linear Acceleration";
		case Sensor.TYPE_MAGNETIC_FIELD:
			return "Magnetic Field";
		case Sensor.TYPE_PRESSURE:
			return "Pressure";
		case Sensor.TYPE_PROXIMITY:
			return "Proximity";
		case Sensor.TYPE_RELATIVE_HUMIDITY:
			return "Relative Humidity";
		case Sensor.TYPE_ROTATION_VECTOR:
			return "Rotation Vector";
		default:
			return "Unknown";
		}
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
	
	public abstract String getSensorUnit();
	public abstract String getFormattedSensorValue(float value);
	public abstract int getValueCount();
	public abstract GraphView getSensorGraph(int size);
	
	public GraphView getSensorGraph() {
		return getSensorGraph(15 * 1000);
	}
	
	public String getFormattedSensorValues(float[] values) {
		if(getValueCount() == 1)
			return getFormattedSensorValue(values[0]);
		
		String value = String.format("x=%s, y=%s", getFormattedSensorValue(values[0]), getFormattedSensorValue(values[1]));
		
		if(getValueCount() == 3)
			value = String.format("%s, z=%s", value, getFormattedSensorValue(values[2]));
		
		return value;
	}
	
	public float[] getLastValues() {
		return mLastValues;
	}
	
	

	public String getSensorType() {
		return SensorHandler.getSensorType(mSensor);
	}
}
