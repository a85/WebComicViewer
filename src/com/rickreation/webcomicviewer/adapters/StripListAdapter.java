package com.rickreation.webcomicviewer.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rickreation.webcomicviewer.R;
import com.rickreation.webcomicviewer.StripListActivity;
import com.rickreation.webcomicviewer.models.Strip;

public class StripListAdapter extends BaseAdapter {
	private static final String TAG = "StripListAdapter";	
	private ArrayList<Strip> mStrips;		
	private static LayoutInflater mInflater = null;
	private StripListActivity mActivity;

	public StripListAdapter(StripListActivity activity, ArrayList<Strip> strips) {
		mActivity = activity;
		mStrips = strips;
		mInflater = (LayoutInflater)mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public static class StripListViewHolder {
		public TextView titleView;
		public TextView numView;
	}
	
	public int getCount() {
		return mStrips.size();
	}

	public Object getItem(int position) {
		return mStrips.get(position);
	}

	public long getItemId(int position) {
		return mStrips.get(position).getNum();
	}
	
	public void changeStrips(ArrayList<Strip> strips) {
		mStrips = strips;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		
		if(mStrips == null) {
			return vi;
		}
		
		StripListViewHolder holder;
		
		if(convertView==null) {			
			vi = mInflater.inflate(R.layout.item_strip, parent, false);
			holder = new StripListViewHolder();
			holder.titleView = (TextView)vi.findViewById(R.id.item_strip_title);
			holder.numView = (TextView)vi.findViewById(R.id.item_strip_num);
			vi.setTag(holder);
		}
		else {
			holder = (StripListViewHolder)vi.getTag();
		}		
		
		if(mStrips.size() == 0) {
			return vi;
		}
		else {
			Strip s = mStrips.get(position);
			
			holder.titleView.setText(s.getTitle());
			holder.numView.setText(Integer.toString(s.getNum()));
		}
		
		return vi;
	}

}
