package com.blueodin.sensorgraph;

import android.hardware.Sensor;
import android.os.Bundle;
import android.os.IBinder;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.blueodin.sensorgraph.DataExportService.ExportServiceBinder;

public class MainActivity extends FragmentActivity implements
		SensorListFragment.OnSensorListInteractionListener {
	private static final String TAG_DETAIL_FRAGMENT = "detail_fragment";
	private DataExportService mService;
	private boolean mBound = false;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		FragmentManager fm = getSupportFragmentManager();

		((Button) findViewById(R.id.button_upload))
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (!mBound) {
							(new AlertDialog.Builder(MainActivity.this))
									.setTitle("Upload Data")
									.setMessage(
											"Unable to upload data: The service does not appear to be running.")
									.show();
							return;
						}

						mService.beginUploadValues(new DataExportService.OnUploadResults() {
							@Override
							public void onUploadProgress(int downloaded,
									int total) {

							}

							@Override
							public void onUploadCompleted(boolean successful,
									String message) {
								(new AlertDialog.Builder(MainActivity.this))
										.setTitle("Upload Data Results")
										.setMessage(
												((successful ? "Success: "
														: "Error: ") + (message
														.isEmpty() ? "Unknown Error"
														: message))).show();
							}
						});
						Toast.makeText(MainActivity.this, "Upload beginning..",
								Toast.LENGTH_SHORT).show();
					}
				});

		if ((savedInstanceState == null)
				|| fm.findFragmentByTag(TAG_DETAIL_FRAGMENT) == null) {
			fm.beginTransaction()
				.replace(R.id.layout_fragment_detail, new SensorDetailFragment(), TAG_DETAIL_FRAGMENT)
				.commit();
		}

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
		if(mBound && (mService != null))
			mService.removeNotification();
		
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_settings:
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		case R.id.menu_exit:
			finish();
			return true;

		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onSensorSelected(Sensor sensor) {
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.layout_fragment_detail,
						SensorDetailFragment.newInstance(sensor.getType()),
						TAG_DETAIL_FRAGMENT).commit();
	}
}
