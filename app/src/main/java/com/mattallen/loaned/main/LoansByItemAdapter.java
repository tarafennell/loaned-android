package com.mattallen.loaned.main;

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

public class LoansByItemAdapter extends ArrayAdapter<Item> {
	
	private ArrayList<Item>				mItems;

	public LoansByItemAdapter(Context context, ArrayList<Item> objects) {
		super(context, R.layout.fragment_loanslist_item, objects);
		mItems = objects;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		View v = convertView;
		if(v==null){
			LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.fragment_loanslist_item, null);
		}
		Item item = mItems.get(position);
		if(item!=null){
			TextView name = (TextView)v.findViewById(R.id.loanslist_item_name);
			ImageView pic = (ImageView)v.findViewById(R.id.loanslist_item_pic);
			TextView onLoan = (TextView)v.findViewById(R.id.loanslist_item_onloan);
			name.setText(item.getName());
			pic.setImageResource(ItemTypeLookup.getDrawableForType(item.getType()));
			if(!item.isCurrentlyOnLoan()){
				onLoan.setVisibility(TextView.INVISIBLE);
			}
			v.setTag(item);
		}
		return v;
	}
}