package com.mattallen.loaned.main;

import java.util.ArrayList;

import com.mattallen.loaned.R;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author Matt Allen
 * 
 * Sets the list items in the DrawerLayout with a title and icon
 */
public class NavDrawerListAdapter extends ArrayAdapter<NavDrawerListItem> {

	private static final String						TAG = NavDrawerListAdapter.class.getSimpleName();
	private ArrayList<NavDrawerListItem>			mTitles;
	private int										mLayout;

	public NavDrawerListAdapter(Context context, int resource, ArrayList<NavDrawerListItem> objects) {
		super(context, resource, objects);
		mTitles = objects;
		mLayout = resource;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		View v = convertView;
		if(v==null){
			LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(mLayout, null);
		}
		TextView title = (TextView)v.findViewById(R.id.main_navitem_title);
		ImageView icon = (ImageView)v.findViewById(R.id.main_navitem_icon);
		Log.d(TAG, "Setting title to "+mTitles.get(position).getTitle());
		Log.d(TAG, "Setting icon to "+Integer.toString(mTitles.get(position).getIconResource()));
		title.setText(mTitles.get(position).getTitle());
		icon.setImageResource(mTitles.get(position).getIconResource());
		return v;
	}
}