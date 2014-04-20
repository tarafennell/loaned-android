package com.mattallen.loaned.main;

import java.util.ArrayList;

import com.mattallen.loaned.Person;
import com.mattallen.loaned.R;
import com.mattallen.loaned.RetrieveContactPhoto;
import com.mattallen.loaned.views.RoundedImageView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class LoansByPersonAdapter extends ArrayAdapter<Person> {

	private ArrayList<Person>				mPeople;
	private Context							mContext;

	public LoansByPersonAdapter(Context context, ArrayList<Person> objects) {
		super(context, R.layout.fragment_loanslist_peopleitem, objects);
		mPeople = objects;
		mContext = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		View v = convertView;
		if(v==null){
			LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.fragment_loanslist_peopleitem, null);
		}
		Person person = mPeople.get(position);
		if(person!=null){
			TextView name = (TextView)v.findViewById(R.id.loanslist_peopleitem_name);
			TextView badgeCount = (TextView)v.findViewById(R.id.loanslist_peopleitem_badgenumber);
			RoundedImageView badge = (RoundedImageView)v.findViewById(R.id.loanslist_peopleitem_badge);
			RoundedImageView pic = (RoundedImageView)v.findViewById(R.id.loanslist_peopleitem_pic);
			name.setText(person.getName());
			new RetrieveContactPhoto(person.getLookupURI(), pic, mContext, R.drawable.friend_image_light).execute();
			if(person.getItemsOnLoan()>=1){
				badgeCount.setText(Integer.toString(person.getItemsOnLoan()));
			} else {
				badge.setVisibility(RoundedImageView.INVISIBLE);
				badgeCount.setVisibility(RoundedImageView.INVISIBLE);
			}
			v.setTag(person);
		}
		return v;
	}
}