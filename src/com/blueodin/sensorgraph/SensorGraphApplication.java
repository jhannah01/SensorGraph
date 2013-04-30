package com.blueodin.sensorgraph;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.blueodin.sensorgraph.SensorReadingValues.SensorType;

import java.util.List;

public class SensorGraphApplication extends Application {
	private SensorReadingValues mSensorReadings = new SensorReadingValues();
	private SharedPreferences mSharedPreferences;
	
	@Override
	public void onCreate() {
		super.onCreate();
		PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
		
		mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
	}
	
	public SensorReadingValues getSensorReadings() {
		return mSensorReadings;
	}
	
	public SharedPreferences getSharedPreferences() {
		return mSharedPreferences;
	}

	public String getServiceUri() {
		return SettingsActivity.getServiceUri(this, mSharedPreferences);
	}
	
	public List<SensorType> getEnabledSensors() {
		return SettingsActivity.getEnabledSensorTypes(this, mSharedPreferences);
	}
}