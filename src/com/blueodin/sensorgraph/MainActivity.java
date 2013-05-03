package com.blueodin.sensorgraph;

import android.hardware.Sensor;
import android.os.Bundle;
import android.os.IBinder;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.blueodin.sensorgraph.export.DataExportService;
import com.blueodin.sensorgraph.export.DataExportService.ExportServiceBinder;

public class MainActivity extends FragmentActivity implements
		SensorListFragment.OnSensorListInteractionListener {
	private static final String ARG_SENSOR = "arg_sensor";
	private DataExportService mService;
	private boolean mBound = false;
	private int mSensorType = -1;
	private boolean mIsDualPane = false;
	private MenuItem mLoggingMenuItem;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);

		View detailView = findViewById(R.id.sensor_detail);
		mIsDualPane = ((detailView != null) && (detailView.getVisibility() == View.VISIBLE));
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if(mSensorType > 0)
			outState.putInt(ARG_SENSOR, mSensorType);
	}

	@Override
	protected void onStart() {
		super.onStart();

		Intent intent = new Intent(this, DataExportService.class);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onStop() {
		super.onStop();

		if (!mBound)
			return;

		mService.removeNotification();

		unbindService(mConnection);
		mBound = false;
	}

	@Override
	protected void onDestroy() {
		if (mBound && (mService != null))
			mService.removeNotification();

		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		mLoggingMenuItem = menu.findItem(R.id.menu_toggle_logging);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_settings:
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		case R.id.menu_upload:
			uploadResults();
			return true;
		case R.id.menu_toggle_logging:
			if(!mBound) {
				item.setChecked(false);
				item.setIcon(R.drawable.ic_menu_graph);
				return true;
			}
			
			boolean isChecked = !item.isChecked();
			
			if(isChecked)
				mService.startSensorLogger();
			else
				mService.stopSensorLogger();
			
			item.setIcon(isChecked ? android.R.drawable.ic_menu_close_clear_cancel : R.drawable.ic_menu_graph);
			item.setChecked(isChecked);
			
			return true;
		case R.id.menu_exit:
			finish();
			return true;

		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onSensorSelected(Sensor sensor) {
		int sensorType = sensor.getType();
		if(mIsDualPane) {
			SensorDetailFragment f = (SensorDetailFragment)getSupportFragmentManager().findFragmentById(R.id.sensor_detail);
			f.setSensorType(sensorType);
		} else {
			Intent intent = new Intent(this, SensorDetailActivity.class);
			intent.putExtra(SensorDetailActivity.PARAM_SENSOR_TYPE, sensorType);
			startActivity(intent);
		}	
	}

	@Override
	public void onToggleSensor(Sensor sensor, boolean enable) {
		if (!mBound)
			return;
		
		mService.toggleSensorReadings(sensor, enable);
		updateLoggingMenuItem();
	}
	
	private void updateLoggingMenuItem() {
		boolean isLogging = (!mBound ? false : mService.isLogging());
		mLoggingMenuItem.setIcon(isLogging ? android.R.drawable.ic_menu_close_clear_cancel : R.drawable.ic_menu_graph);
		mLoggingMenuItem.setChecked(isLogging);
	}

	protected void uploadResults() {
		if (!mBound) {
			(new AlertDialog.Builder(MainActivity.this))
					.setTitle("Upload Data")
					.setMessage(
							"Unable to upload data: The service does not appear to be running.")
					.setNeutralButton("Close", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							dialog.cancel();
						}
					}).show();
			return;
		}

		boolean result = mService.beginUploadValues(new DataExportService.OnUploadResults() {
			@Override
			public void onUploadCompleted(boolean successful, String message) {
				(new AlertDialog.Builder(MainActivity.this))
						.setTitle("Upload Data Results")
						.setMessage(
								((successful ? "Success: " : "Error: ") + (message
										.isEmpty() ? "Unknown Error" : message)))
						.setNeutralButton("Close",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.cancel();
									}
								}).show();
			}
		});
		
		if(!result)
			Toast.makeText(MainActivity.this, "No values to upload.", Toast.LENGTH_SHORT).show();
		else
			Toast.makeText(MainActivity.this, "Upload in progress...", Toast.LENGTH_SHORT).show();
	}
	
	private ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			mBound = false;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			ExportServiceBinder binder = (ExportServiceBinder) service;
			mService = binder.getService();
			mBound = true;
			mService.showNotification();
		}
	};
}
