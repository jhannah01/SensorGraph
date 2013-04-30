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
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class SensorListFragment extends ListFragment {
	private SensorListAdapter mAdapter;
	
	private OnSensorListInteractionListener mListener;
	private SensorManager mSensorManager;

	private class SensorListAdapter extends ArrayAdapter<Sensor> {
		private LayoutInflater mInflater;

		public SensorListAdapter(Context context, List<Sensor> sensors) {
			super(context, android.R.layout.simple_list_item_2, sensors);
			mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Sensor sensor = getItem(position);
			
			if(convertView == null)
				convertView = mInflater.inflate(android.R.layout.simple_list_item_2, parent, false);
			
			((TextView)convertView.findViewById(android.R.id.text1)).setText(sensor.getName());
			
			((TextView)convertView.findViewById(android.R.id.text2))
				.setText(String.format("%s - %s [%s]", SensorHandler.getSensorType(sensor), sensor.getVendor(), sensor.getVersion()));
			
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
		super.onListItemClick(l, v, position, id);

		if (null != mListener)
			mListener.onSensorSelected(mAdapter.getItem(position));
	}

	public interface OnSensorListInteractionListener {
		public void onSensorSelected(Sensor sensor);
	}
}
