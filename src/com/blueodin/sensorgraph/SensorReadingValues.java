package com.blueodin.sensorgraph;

import android.content.Context;
import android.hardware.Sensor;

import com.blueodin.sensorgraph.handlers.AccelerometerSensorHandler;
import com.blueodin.sensorgraph.handlers.GravitySensorHandler;
import com.blueodin.sensorgraph.handlers.GyroscopeSensorHandler;
import com.blueodin.sensorgraph.handlers.LightSensorHandler;
import com.blueodin.sensorgraph.handlers.LinearAccelerationSensorHandler;
import com.blueodin.sensorgraph.handlers.MagneticSensorHandler;
import com.blueodin.sensorgraph.handlers.PressureSensorHandler;
import com.blueodin.sensorgraph.handlers.ProximitySensorHandler;
import com.blueodin.sensorgraph.handlers.RelativeHumiditySensorHandler;
import com.blueodin.sensorgraph.handlers.SensorHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SensorReadingValues {
	private HashMap<SensorType, List<SensorReading>> mReadingsMap = new HashMap<SensorType, List<SensorReading>>();

	public enum SensorType {
		Accelerometer, AmbientTemperature, Gravity, Gyroscope, Light, LinearAcceleration, MagneticField, Pressure, Proximity, RelativeHumidity, RotationVector, Unknown;

		public static SensorType fromSensor(Sensor sensor) {
			return fromSensor(sensor.getType());
		}

		public static SensorType fromSensor(int sensorType) {
			switch (sensorType) {
			case Sensor.TYPE_ACCELEROMETER:
				return SensorType.Accelerometer;
			case Sensor.TYPE_AMBIENT_TEMPERATURE:
				return SensorType.AmbientTemperature;
			case Sensor.TYPE_GRAVITY:
				return SensorType.Gravity;
			case Sensor.TYPE_GYROSCOPE:
				return SensorType.Gyroscope;
			case Sensor.TYPE_LIGHT:
				return SensorType.Light;
			case Sensor.TYPE_LINEAR_ACCELERATION:
				return SensorType.LinearAcceleration;
			case Sensor.TYPE_MAGNETIC_FIELD:
				return SensorType.MagneticField;
			case Sensor.TYPE_PRESSURE:
				return SensorType.Pressure;
			case Sensor.TYPE_PROXIMITY:
				return SensorType.Proximity;
			case Sensor.TYPE_RELATIVE_HUMIDITY:
				return SensorType.RelativeHumidity;
			case Sensor.TYPE_ROTATION_VECTOR:
				return SensorType.RotationVector;
			default:
				return SensorType.Unknown;
			}
		}

		public static SensorType fromString(String sensorType) {
			if (sensorType.equalsIgnoreCase("Accelerometer"))
				return SensorType.Accelerometer;

			if (sensorType.equalsIgnoreCase("AmbientTemperature"))
				return SensorType.AmbientTemperature;

			if (sensorType.equalsIgnoreCase("Gravity"))
				return SensorType.Gravity;

			if (sensorType.equalsIgnoreCase("Gyroscope"))
				return SensorType.Gyroscope;

			if (sensorType.equalsIgnoreCase("Light"))
				return SensorType.Light;

			if (sensorType.equalsIgnoreCase("LinearAcceleration"))
				return SensorType.LinearAcceleration;

			if (sensorType.equalsIgnoreCase("MagneticField"))
				return SensorType.MagneticField;

			if (sensorType.equalsIgnoreCase("Pressure"))
				return SensorType.Pressure;

			if (sensorType.equalsIgnoreCase("Proximity"))
				return SensorType.Proximity;

			if (sensorType.equalsIgnoreCase("RelativeHumidity"))
				return SensorType.RelativeHumidity;

			if (sensorType.equalsIgnoreCase("RotationVector"))
				return SensorType.RotationVector;

			return SensorType.Unknown;
		}
		
		public static SensorHandler getHandler(Context context, Sensor sensor) {
			return getHandler(context, SensorType.fromSensor(sensor));
		}
		
		public static SensorHandler getHandler(Context context, int type) {
			return getHandler(context, SensorType.fromSensor(type));
		}
		
		public static SensorHandler getHandler(Context context, SensorType type) {
			switch(type) {
			case Accelerometer:
				return new AccelerometerSensorHandler(context);
			case Gravity:
				return new GravitySensorHandler(context);
			case Gyroscope:
				return new GyroscopeSensorHandler(context);
			case Light:
				return new LightSensorHandler(context);
			case LinearAcceleration:
				return new LinearAccelerationSensorHandler(context);
			case MagneticField:
				return new MagneticSensorHandler(context);
			case Pressure:
				return new PressureSensorHandler(context);
			case Proximity:
				return new ProximitySensorHandler(context); 
			case RelativeHumidity:
				return new RelativeHumiditySensorHandler(context);
			case RotationVector:
				return new RotationVectorSensorHandler(context);
			default:
				return null;
 
			}
		}

	}

	public static class SensorReading {
		private SensorType sensorType;
		private long timestamp;
		private float[] values;

		public SensorReading(SensorType sensorType, long timestamp,
				float[] values) {
			this.sensorType = sensorType;
			this.timestamp = timestamp;
			this.values = values;
		}

		public SensorReading(SensorType sensorType, float[] values) {
			this(sensorType, System.currentTimeMillis(), values);
		}

		public SensorType getSensorType() {
			return sensorType;
		}

		public long getTimestamp() {
			return timestamp;
		}

		public float[] getValues() {
			return values;
		}
	}

	public SensorReadingValues() {
		clearReadings();
	}

	public HashMap<SensorType, List<SensorReading>> getReadingsMap() {
		return mReadingsMap;
	}

	public List<SensorReading> getReadings(SensorType sensorType) {
		if (!mReadingsMap.containsKey(sensorType))
			return new ArrayList<SensorReading>();

		return mReadingsMap.get(sensorType);
	}

	public List<SensorReading> getReadings() {
		List<SensorReading> allReadings = new ArrayList<SensorReading>();

		for (List<SensorReading> readings : mReadingsMap.values())
			allReadings.addAll(readings);

		return allReadings;
	}

	public void addReading(SensorReading reading) {
		if (!mReadingsMap.containsKey(reading.getSensorType()))
			mReadingsMap.put(reading.getSensorType(),
					new ArrayList<SensorReading>());

		mReadingsMap.get(reading.getSensorType()).add(reading);
	}

	public void clearReadings(SensorType sensorType) {
		if (mReadingsMap.containsKey(sensorType))
			mReadingsMap.get(sensorType).clear();
	}

	public void clearReadings() {
		mReadingsMap.put(SensorType.Accelerometer,
				new ArrayList<SensorReading>());
		mReadingsMap.put(SensorType.AmbientTemperature,
				new ArrayList<SensorReading>());
		mReadingsMap.put(SensorType.Gravity, new ArrayList<SensorReading>());
		mReadingsMap.put(SensorType.Gyroscope, new ArrayList<SensorReading>());
		mReadingsMap.put(SensorType.Light, new ArrayList<SensorReading>());
		mReadingsMap.put(SensorType.LinearAcceleration,
				new ArrayList<SensorReading>());
		mReadingsMap.put(SensorType.MagneticField,
				new ArrayList<SensorReading>());
		mReadingsMap.put(SensorType.Pressure, new ArrayList<SensorReading>());
		mReadingsMap.put(SensorType.Proximity, new ArrayList<SensorReading>());
		mReadingsMap.put(SensorType.RelativeHumidity,
				new ArrayList<SensorReading>());
		mReadingsMap.put(SensorType.RotationVector,
				new ArrayList<SensorReading>());
	}
}
