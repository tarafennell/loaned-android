package com.mattallen.loaned.addloan;

import java.util.ArrayList;

import com.mattallen.loaned.Item;
import com.mattallen.loaned.ItemTypeLookup;
import com.mattallen.loaned.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ChooseItemAdapter extends ArrayAdapter<Item> {

	private int 						mLayoutID;
	private ArrayList<Item> 			mItems;

	public ChooseItemAdapter(Context context, int layoutId, 
			ArrayList<Item> items) {
		super(context, layoutId, items);
		this.mLayoutID = layoutId;
		this.mItems = items;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		View v = convertView;
		if(v==null){
			LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(mLayoutID, null);
		}
		Item item = mItems.get(position);
		if(item!=null){
			TextView name = (TextView)v.findViewById(R.id.chooseitem_name);
			ImageView pic = (ImageView)v.findViewById(R.id.chooseitem_pic);
			name.setText(item.getName());
			pic.setImageResource(ItemTypeLookup.getDrawableForType(item.getType()));
			v.setTag(item);
		}
		return v;
	}
}