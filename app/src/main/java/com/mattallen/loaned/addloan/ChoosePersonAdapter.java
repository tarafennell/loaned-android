package com.mattallen.loaned.addloan;

import java.util.ArrayList;

import com.mattallen.loaned.Person;
import com.mattallen.loaned.R;
import com.mattallen.loaned.RetrieveContactPhoto;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ChoosePersonAdapter extends ArrayAdapter<Person> {

	private int 						mLayoutID;
	private ArrayList<Person> 			mPeople;
	private Context						mContext;

	public ChoosePersonAdapter(Context context, int layoutId, 
			ArrayList<Person> people) {
		super(context, layoutId, people);
		this.mLayoutID = layoutId;
		this.mPeople = people;
		this.mContext = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		View v = convertView;
		if(v==null){
			LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(mLayoutID, null);
		}
		Person person = mPeople.get(position);
		if(person!=null){
			TextView name = (TextView)v.findViewById(R.id.chooseperson_name);
			ImageView pic = (ImageView)v.findViewById(R.id.chooseperson_pic);
			name.setText(person.getName());
			new RetrieveContactPhoto(person.getLookupURI(), pic, mContext, R.drawable.friend_image_light).execute();
			v.setTag(person);
		}
		return v;
	}
}