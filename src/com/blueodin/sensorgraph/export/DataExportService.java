package com.blueodin.sensorgraph.export;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.blueodin.sensorgraph.MainActivity;
import com.blueodin.sensorgraph.R;
import com.blueodin.sensorgraph.SensorGraphApplication;
import com.blueodin.sensorgraph.SensorReadingValues;
import com.blueodin.sensorgraph.SensorReadingValues.SensorReading;
import com.blueodin.sensorgraph.SensorReadingValues.SensorType;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataExportService extends Service implements SensorEventListener {
	protected static final String TAG = "DataExportService";
	private static final int NOTIFICATION_ID = 1;

	private final IBinder mBinder = new ExportServiceBinder();
	private SensorGraphApplication mApplication;
	private NotificationManager mNotificationManager;
	private NotificationCompat.Builder mNotificationBuilder;

	private HashMap<Sensor, List<SensorReading>> mSensorValues = new HashMap<Sensor, List<SensorReading>>();
	private SensorManager mSensorManager;
	private boolean mIsLogging;

	public class ExportServiceBinder extends Binder {
		public DataExportService getService() {
			return DataExportService.this;
		}
	}

	public DataExportService() {
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mApplication = (SensorGraphApplication) getApplication();
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationBuilder = new NotificationCompat.Builder(this)
				.setSmallIcon(R.drawable.ic_action_service)
				.setAutoCancel(false)
				.setOngoing(true)
				.setContentTitle("Sensor Export Service")
				.setContentIntent(
						PendingIntent.getActivity(this, 0, new Intent(this,
								MainActivity.class),
								PendingIntent.FLAG_UPDATE_CURRENT));
	}

	@Override
	public void onDestroy() {
		removeNotification();
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	public interface OnUploadResults {
		public void onUploadCompleted(boolean successful, String message);
	}

	public boolean beginUploadValues(OnUploadResults resultsCallback) {
		return beginUploadValues(mApplication.getServiceUri(),
				mApplication.getSensorReadings(), resultsCallback);
	}

	public boolean beginUploadValues(final String serviceUri,
			final SensorReadingValues sensorValues,
			final OnUploadResults resultsCallback) {
		
		if(mSensorValues.size() == 0)
			return false;
		
		AsyncTask<Void, Integer, UploadResults> uploadTask = new AsyncTask<Void, Integer, DataExportService.UploadResults>() {
			@Override
			protected UploadResults doInBackground(Void... params) {
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPostRequest = new HttpPost(serviceUri);
				updateNotification("Beginning upload...");

				try {
					httpPostRequest.setEntity(new StringEntity(sensorValuesToJson()));
					httpPostRequest.setHeader("Accept", "application/json");
					httpPostRequest.setHeader("Content-type",
							"application/json");

					HttpResponse response = (HttpResponse) httpClient
							.execute(httpPostRequest);
					HttpEntity entity = response.getEntity();

					if (entity == null)
						return new UploadResults(false,
								"Empty response returned");

					JsonReader jsonReader = new JsonReader(
							new InputStreamReader(entity.getContent()));

					UploadResults results = (new Gson()).fromJson(jsonReader,
							UploadResults.class);
					jsonReader.close();
					return results;
				} catch (UnsupportedEncodingException ex) {
					ex.printStackTrace();
					String message = "Unsupposed Encoding: " + ex.getMessage();
					Log.w(TAG, message);
					return new UploadResults(false, message);
				} catch (ClientProtocolException ex) {
					ex.printStackTrace();
					String message = "Client Error: " + ex.getMessage();
					Log.w(TAG, message);
					return new UploadResults(false, message);
				} catch (IOException ex) {
					ex.printStackTrace();
					String message = "Read/Write Error: " + ex.getMessage();
					Log.w(TAG, message);
					return new UploadResults(false, message);
				} catch (Exception ex) {
					ex.printStackTrace();
					String message = "Error: " + ex.getMessage();
					Log.w(TAG, message);
					return new UploadResults(false, message);
				}
			}

			@Override
			protected void onPostExecute(UploadResults result) {
				if (resultsCallback != null)
					resultsCallback.onUploadCompleted(result.success,
							result.message);
				else
					Toast.makeText(DataExportService.this, result.toString(),
							Toast.LENGTH_SHORT).show();

				if(result.success)
					clearSensorValues();
				updateNotification("Last upload: " + result.toString());
			}
		};

		uploadTask.execute();

		return true;
	}

	public void showNotification(String message) {
		Notification notification = mNotificationBuilder
				.setContentText(message).setWhen(System.currentTimeMillis())
				.build();

		mNotificationManager.notify(NOTIFICATION_ID, notification);
	}

	protected String sensorValuesToJson() {
		List<SensorReading> sensorReadings = new ArrayList<SensorReading>();
		
		for (List<SensorReading> readings : mSensorValues.values()) {
			sensorReadings.addAll(readings);
		}

		return new Gson().toJson(sensorReadings);
	}

	public void showNotification() {
		showNotification("Export service is running");
	}

	public void updateNotification(String message) {
		mNotificationManager
				.notify(NOTIFICATION_ID,
						mNotificationBuilder
								.setWhen(System.currentTimeMillis())
								.setTicker(message).build());
	}

	public void removeNotification() {
		mNotificationManager.cancel(NOTIFICATION_ID);
	}

	public class UploadResults {
		public boolean success;
		public String message;

		public UploadResults(boolean success, String message) {
			this.success = success;
			this.message = message;
		}

		@Override
		public String toString() {
			if (this.success)
				return "Success"
						+ (this.message.isEmpty() ? "" : ": " + this.message);

			return (this.message.startsWith("Error:") ? this.message
					: "Error: " + this.message);
		}
	}

	public void toggleSensorReadings(Sensor sensor, boolean enable) {
		if (!enable) {
			if(mIsLogging)
				mSensorManager.unregisterListener(this, sensor);	
			
			if (mSensorValues.containsKey(sensor))
				mSensorValues.remove(sensor);
			
			if((mSensorValues.size() == 0) && mIsLogging)
				stopSensorLogger();
		} else {
			mSensorValues.put(sensor, new ArrayList<SensorReading>());
			if(!mIsLogging)
				startSensorLogger();
		}
	}

	public boolean isLogging() {
		return mIsLogging;
	}
	
	public void startSensorLogger() {
		for (Sensor sensor : mSensorValues.keySet())
			mSensorManager.registerListener(this, sensor,
					SensorManager.SENSOR_DELAY_NORMAL);
		mIsLogging = true;
		updateNotification("Started logging sensor values.");
	}

	public void stopSensorLogger() {
		for (Sensor sensor : mSensorValues.keySet())
			mSensorManager.unregisterListener(this, sensor);
		mIsLogging = false;
		updateNotification("Stopped logging sensor values.");
	}

	public HashMap<Sensor, List<SensorReading>> getSensorValues() {
		return mSensorValues;
	}

	public List<SensorReading> getSensorValue(Sensor sensor) {
		if (!mSensorValues.containsKey(sensor))
			return new ArrayList<SensorReading>();

		return mSensorValues.get(sensor);
	}
	
	private void clearSensorValues() {
		for(Sensor sensor : mSensorValues.keySet())
			mSensorValues.get(sensor).clear();
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (!mSensorValues.containsKey(event.sensor))
			return;
		SensorReading sensorReading = new SensorReading(SensorType.fromSensor(event.sensor), event.values);
		mSensorValues.get(event.sensor).add(sensorReading);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}
}
