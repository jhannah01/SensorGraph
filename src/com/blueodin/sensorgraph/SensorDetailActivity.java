package com.blueodin.sensorgraph;

import android.os.Bundle;
import android.view.MenuItem;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.os.Build;

public class SensorDetailActivity extends FragmentActivity {
	public static final String PARAM_SENSOR_TYPE = "param_sensor_type";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getResources().getBoolean(R.bool.has_two_panes)) {
			finish();
			return;
		}
		
		int sensorType = -1;
		
		if(getIntent().hasExtra(PARAM_SENSOR_TYPE))
			sensorType = getIntent().getExtras().getInt(PARAM_SENSOR_TYPE);
		else if((savedInstanceState != null) && (savedInstanceState.containsKey(PARAM_SENSOR_TYPE)))
			sensorType = savedInstanceState.getInt(PARAM_SENSOR_TYPE);
		
		SensorDetailFragment f = ((sensorType == -1) ? new SensorDetailFragment() : SensorDetailFragment.newInstance(sensorType));
		
		getSupportFragmentManager().beginTransaction()
		.add(android.R.id.content, f)
		.commit();
		
		setupActionBar();
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
