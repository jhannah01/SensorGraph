package com.blueodin.sensorgraph;

import android.app.Activity;
import android.support.v4.app.ListFragment;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import java.util.List;

public class SensorListFragment extends ListFragment {
	private SensorListAdapter mAdapter;
	
	private OnSensorListInteractionListener mListener;
	private SensorManager mSensorManager;
	
	private class SensorListAdapter extends ArrayAdapter<Sensor> {
		private LayoutInflater mInflater;

		@SuppressWarnings("deprecation")
		public SensorListAdapter(Context context, List<Sensor> sensors) {
			super(context, R.layout.sensor_list_row);
			mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			for(Sensor sensor : sensors) {
				if(sensor.getType() != Sensor.TYPE_ORIENTATION)
					add(sensor);
			}
			
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final Sensor sensor = getItem(position);
			
			if(convertView == null)
				convertView = mInflater.inflate(R.layout.sensor_list_row, parent, false);
			
			((TextView)convertView.findViewById(R.id.text_sensor_name)).setText(sensor.getName());
			
			((TextView)convertView.findViewById(R.id.text_sensor_type))
				.setText(String.format("%s - %s [%s]", SensorReadingValues.SensorType.fromSensor(sensor).toString(), sensor.getVendor(), sensor.getVersion()));
			
			((CompoundButton)convertView.findViewById(R.id.button_toggle_sensor)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					mListener.onToggleSensor(sensor, isChecked);
				}
			});
			
			return convertView;
		}		
	}
	
	public SensorListFragment() { }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSensorManager = (SensorManager)getActivity().getSystemService(Context.SENSOR_SERVICE);
		mAdapter = new SensorListAdapter(getActivity(), mSensorManager.getSensorList(Sensor.TYPE_ALL));
		setListAdapter(mAdapter);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnSensorListInteractionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnSensorListInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		mListener.onSensorSelected(mAdapter.getItem(position));
	}
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	}

	public interface OnSensorListInteractionListener {
		public void onSensorSelected(Sensor sensor);
		public void onToggleSensor(Sensor sensor, boolean enable);
	}
}
