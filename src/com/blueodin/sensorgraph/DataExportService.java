package com.blueodin.sensorgraph;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.blueodin.sensorgraph.SensorReadingValues.SensorReading;
import com.blueodin.sensorgraph.SensorReadingValues.SensorType;
import com.google.gson.Gson;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpClient.JSONObjectCallback;
import com.koushikdutta.async.http.AsyncHttpPost;
import com.koushikdutta.async.http.AsyncHttpResponse;

import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataExportService extends Service {
	protected static final String TAG = "DataExportService";
	private static final int NOTIFICATION_ID = 1;
	
	private final IBinder mBinder = new ExportServiceBinder();
	private SensorGraphApplication mApplication;
	private Future<JSONObject> mFutureUploadResult = null;
	private NotificationManager mNotificationManager;
	
	public class ExportServiceBinder extends Binder {
		public DataExportService getService() {
			return DataExportService.this;
		}
	}
	
	public DataExportService() { }

	@Override
	public void onCreate() {
		super.onCreate();
		mApplication = (SensorGraphApplication)getApplication();
		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
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
		public void onUploadProgress(int downloaded, int total);
	}
	
	public void cancelUploadValues() {
		if(mFutureUploadResult == null)
			return;
		
        if(mFutureUploadResult.isCancelled() || mFutureUploadResult.isDone())
        	return;
        
		mFutureUploadResult.cancel(true);
        mFutureUploadResult = null;
	}
	
	public boolean beginUploadValues(final OnUploadResults resultsCallback) {
		return beginUploadValues(mApplication.getServiceUri(), mApplication.getSensorReadings().getReadingsMap(), resultsCallback);
	}
	
	public boolean beginUploadValues(String serviceUri, HashMap<SensorType, List<SensorReading>> readingsMap, final OnUploadResults resultsCallback) {
        if(mFutureUploadResult != null) {
        	if(!mFutureUploadResult.isCancelled() || !mFutureUploadResult.isDone())
        		return false;
        }
        
		AsyncHttpClient httpClient = AsyncHttpClient.getDefaultInstance();
		Gson gson = new Gson();
		
		AsyncHttpPost httpPost = new AsyncHttpPost(serviceUri);
		httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        
        HttpParams httpParams = new BasicHttpParams();
        
        //Type readingsListType = new TypeToken<ArrayList<SensorReading>>() { }.getType();
        
        for(Map.Entry<SensorType, List<SensorReading>> entry : readingsMap.entrySet()) {
        	httpParams.setParameter(entry.getKey().toString(), gson.toJson(entry.getValue().toArray()));
        }
        
        httpPost.setParams(httpParams);
        
        mFutureUploadResult = httpClient.execute(httpPost, new JSONObjectCallback() {
			@Override
			public void onCompleted(Exception ex, AsyncHttpResponse source,
					JSONObject result) {
				if(ex != null) {
					Log.w(TAG, "Uploader Error: " + ex.getMessage());
					if(resultsCallback != null)
						resultsCallback.onUploadCompleted(false, ex.getMessage());
					
					return;
				}

				if(resultsCallback != null)
					resultsCallback.onUploadCompleted(result.optBoolean("success", false), result.optString("message"));
			}
			
			@Override
			public void onProgress(AsyncHttpResponse response,
					int downloaded, int total) {
				super.onProgress(response, downloaded, total);
				if(resultsCallback != null)
					resultsCallback.onUploadProgress(downloaded, total);
			}
		});
        
        return true;
	}
	
	public void showNotification() {
		Notification notification = new NotificationCompat.Builder(this)
				.setContentText("Sensor Export Service")
				.setSmallIcon(R.drawable.ic_stat_service)
				.setAutoCancel(false)
				.setOngoing(true)
				.setTicker("Starting Sensor Export Service")
				.setSubText("Export Service is running")
				.setWhen(System.currentTimeMillis())
				.setContentIntent(
						PendingIntent.getActivity(this, 0,
								new Intent(this,
										MainActivity.class),
								PendingIntent.FLAG_UPDATE_CURRENT))
				.build();
		
		mNotificationManager.notify(NOTIFICATION_ID, notification);
	}
	
	public void removeNotification() {
		mNotificationManager.cancel(NOTIFICATION_ID);
	}
}
