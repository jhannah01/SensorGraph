package com.blueodin.sensorgraph;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.jjoe64.graphview.GraphView;

public class SensorDetailFragment extends Fragment implements SensorHandler.OnSensorChanged {
	private static final String ARG_SENSOR_TYPE = "sensor_type";

	public static SensorDetailFragment newInstance(int sensorType) {
		SensorDetailFragment fragment = new SensorDetailFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SENSOR_TYPE, sensorType);
		fragment.setArguments(args);
		return fragment;
	}

	private SensorHandler mSensorHandler = null;
	private Sensor mSensor = null;
	private TextView mLastValues = null;
	private TextView mAccuracy = null;
	private SensorManager mSensorManager;

	public SensorDetailFragment() {	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		int sensorType = -1;
		
		mSensorManager = (SensorManager)getActivity().getSystemService(Context.SENSOR_SERVICE);
		
		if((savedInstanceState != null) && savedInstanceState.containsKey(ARG_SENSOR_TYPE))
			sensorType = savedInstanceState.getInt(ARG_SENSOR_TYPE);
		else if ((getArguments() != null) && getArguments().containsKey(ARG_SENSOR_TYPE))
			sensorType = getArguments().getInt(ARG_SENSOR_TYPE);

		updateSensor(sensorType);
	}
	
	private void updateSensor(int sensorType) {
		if(sensorType < 0) {
			if(mSensorHandler != null)
				mSensorHandler.unregisterListener();
			
			mSensorHandler = null;
			mSensor = null;
			return;
		}

		mSensorHandler = SensorReadingValues.SensorType.getHandler(getActivity(), sensorType);
		
		if(mSensorHandler == null)
			mSensor = mSensorManager.getDefaultSensor(sensorType);
		else {
			mSensor = mSensorHandler.getSensor();
			mSensorHandler.registerListener();
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if(mSensor != null)
			outState.putInt(ARG_SENSOR_TYPE, mSensor.getType());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_sensor_detail, container, false);
		
		setupView(inflater, view);
		
		return view;
	}
	
	private void setupView() {
		setupView((LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE), getView());
	}
	
	private void setupView(LayoutInflater inflater, View view) {
		LinearLayout layoutDetails = (LinearLayout)view.findViewById(R.id.layout_sensor_detail);
		
		layoutDetails.removeAllViews();
		
		if(mSensor == null) {
			mLastValues = mAccuracy = null;
			
			TextView textNoSensors = new TextView(getActivity());
			textNoSensors.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			textNoSensors.setTextAppearance(getActivity(), R.style.Text_Large_Alt);
			textNoSensors.setGravity(Gravity.CENTER);
			textNoSensors.setText("No sensor selected...");
			layoutDetails.addView(textNoSensors);
			
			return;
		}
		
		View detailView = inflater.inflate(R.layout.sensor_detail_entry, layoutDetails, false);
		
		((TextView)detailView.findViewById(R.id.text_sensor_name)).setText(mSensor.getName());
		((TextView)detailView.findViewById(R.id.text_sensor_type)).setText(SensorReadingValues.SensorType.fromSensor(mSensor).toString());
		((TextView)detailView.findViewById(R.id.text_sensor_vendor)).setText(mSensor.getVendor());
		((TextView)detailView.findViewById(R.id.text_sensor_version)).setText(String.format("[Version: %d]", mSensor.getVersion()));
		
		layoutDetails.addView(detailView);
		
		CheckBox autoScrollButton = (CheckBox)detailView.findViewById(R.id.check_use_autoscroll);
		
		if(mSensorHandler == null) {
			autoScrollButton.setVisibility(View.INVISIBLE);
			return;
		}
		
		autoScrollButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton button, boolean isChecked) {
				mSensorHandler.setAutoScroll(isChecked);
			}
		});
		
		mSensorHandler.setOnSensorChangedCallback(this);
		mLastValues = (TextView)detailView.findViewById(R.id.text_sensor_last_values);
		mAccuracy = (TextView)detailView.findViewById(R.id.text_sensor_accuracy);
		
		final GraphView sensorGraph = mSensorHandler.getGraphView();
		
		if(sensorGraph == null)
			return;
		
		((FrameLayout)detailView.findViewById(R.id.layout_graph_holder)).addView(sensorGraph);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(mSensorHandler != null)
			mSensorHandler.registerListener();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if(mSensorHandler != null)
			mSensorHandler.unregisterListener();
	}

	@Override
	public void onSensorEvent(float[] values) {
		if(mLastValues != null)
			mLastValues.setText(mSensorHandler.getFormattedSensorValues(values));
	}

	@Override
	public void onAccuracyChange(int accuracy) {
		if(mAccuracy != null)
			mAccuracy.setText(String.format("%d", accuracy));
	}

	public void setSensorType(int sensorType) {
		updateSensor(sensorType);
		setupView();
	}
}
