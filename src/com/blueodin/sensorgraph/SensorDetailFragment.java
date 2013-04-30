package com.blueodin.sensorgraph;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.LegendAlign;

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
	private int mSensorType = -1;

	public SensorDetailFragment() {	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if((savedInstanceState != null) && savedInstanceState.containsKey(ARG_SENSOR_TYPE))
			mSensorType = savedInstanceState.getInt(ARG_SENSOR_TYPE);
		else if ((getArguments() != null) && getArguments().containsKey(ARG_SENSOR_TYPE))
			mSensorType = getArguments().getInt(ARG_SENSOR_TYPE);
		
		if(mSensorType != -1) {
			mSensorHandler = SensorReadingValues.SensorType.getHandler(getActivity(), mSensorType);
			
			if(mSensorHandler == null)
				mSensor = ((SensorManager)getActivity().getSystemService(Context.SENSOR_SERVICE)).getDefaultSensor(mSensorType);
			else
				mSensor = mSensorHandler.getSensor();
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
		
		if(mSensor == null)
			return view;
		
		LinearLayout layoutDetails = (LinearLayout)view.findViewById(R.id.layout_sensor_detail);
		
		layoutDetails.removeAllViews();
		
		View detailView = inflater.inflate(R.layout.sensor_detail_entry, layoutDetails, false);
				
		((TextView)detailView.findViewById(R.id.text_sensor_name)).setText(mSensor.getName());
		((TextView)detailView.findViewById(R.id.text_sensor_type)).setText(SensorHandler.getSensorType(mSensor));
		((TextView)detailView.findViewById(R.id.text_sensor_vendor)).setText(mSensor.getVendor());
		((TextView)detailView.findViewById(R.id.text_sensor_version)).setText(String.format("[Version: %d]", mSensor.getVersion()));
		
		layoutDetails.addView(detailView);
		
		CheckBox autoScrollButton = (CheckBox)detailView.findViewById(R.id.check_use_autoscroll);
		ToggleButton toggleShowLegend = (ToggleButton)detailView.findViewById(R.id.toggle_show_legend);
		
		if(mSensorHandler == null) {
			autoScrollButton.setVisibility(View.INVISIBLE);
			toggleShowLegend.setVisibility(View.INVISIBLE);
			detailView.findViewById(R.id.label_show_legend).setVisibility(View.INVISIBLE);
			return view;
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
		
		final GraphView sensorGraph = mSensorHandler.getSensorGraph();
		
		sensorGraph.setOnClickListener(new View.OnClickListener() {
			private boolean showLegend = false;

			@Override
			public void onClick(View v) {
				showLegend = !showLegend;
				sensorGraph.setShowLegend(showLegend);
			}
		});
		
		sensorGraph.setLegendAlign(LegendAlign.BOTTOM);
		sensorGraph.setLegendWidth(200);
		
		toggleShowLegend.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				sensorGraph.setShowLegend(isChecked);
			}
		});
		
		
		((FrameLayout)detailView.findViewById(R.id.layout_graph_holder)).addView(sensorGraph);
		
		return view;
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
}
