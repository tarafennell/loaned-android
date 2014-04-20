package com.mattallen.loaned.views;

import java.util.ArrayList;
import java.util.Collections;

import com.mattallen.loaned.Person;
import com.mattallen.loaned.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.Paint.Align;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class PersonBarChartView extends View {

	private static final String				TAG = PersonBarChartView.class.getSimpleName();

	private Person							mLoanedToMost;
	private ArrayList<Person>				mPeople;
	private float							mBarScaleFactor;
	private Paint							mBarPaint, mBarNumberPaint, mNameLabelPaint;
	private int[]							mColours;

	public PersonBarChartView(Context context) {
		super(context);
		init(context);
	}

	public PersonBarChartView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context c){
		Log.d(TAG, "init called");
		// Create the colour array
		mColours = new int[6];
		mColours[4] = Color.parseColor("#1bc5a4");
		mColours[1] = Color.parseColor("#9b59b6");
		mColours[0] = Color.parseColor("#2ecc71");
		mColours[2] = Color.parseColor("#e74c3c");
		mColours[3] = Color.parseColor("#3498db");

		mBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mBarPaint.setStyle(Paint.Style.FILL);

		mBarNumberPaint = new Paint();
		mBarNumberPaint.setTypeface(Typeface.create("sans-serif-thin", Typeface.NORMAL));
		mBarNumberPaint.setColor(c.getResources().getColor(R.color.text_main));
		mBarNumberPaint.setTextSize(100);
		mBarNumberPaint.setTextAlign(Align.CENTER);

		mNameLabelPaint = new Paint();
		mNameLabelPaint.setTypeface(Typeface.create("sans-serif-thin", Typeface.NORMAL));
		mNameLabelPaint.setColor(c.getResources().getColor(R.color.text_main));
		mNameLabelPaint.setTextAlign(Align.CENTER);
		mNameLabelPaint.setTextSize(140);
	}

	@Override
	protected void onDraw(Canvas canvas){
		Log.d(TAG, "onDraw called");
		super.onDraw(canvas);
		float barAreaHeight = (getHeight()-mNameLabelPaint.getTextSize()-30)-(mBarNumberPaint.getTextSize()+20);
		mBarScaleFactor = barAreaHeight/mLoanedToMost.getItemsLoaned();
		// Only draw 5 bars here - don't want it too crowded
		// Calculate how much space each bar gets
		float margin = 25f; // Give a margin between each bar
		float barWidth = (getWidth()-(margin*4))/5;
		float bottom = getHeight();
		int index = (mPeople.size()<4)?mPeople.size()-1:4;
		for(int i=0;i<=index;i++){
			float left = (barWidth+margin)*i;
			float top = bottom-(mPeople.get(i).getItemsLoaned()*mBarScaleFactor);
			float right = left+barWidth;
			mBarPaint.setColor(mColours[i]);
			canvas.drawRect(left, top, right, bottom, mBarPaint);
			canvas.drawText(Integer.toString(mPeople.get(i).getItemsLoaned()), left+(barWidth/2), top-15, mBarNumberPaint);
		}
		canvas.drawText(mLoanedToMost.getName(), getWidth()/2, mNameLabelPaint.getTextSize(), mNameLabelPaint);
	}

	public void setData(ArrayList<Person> people){
		Log.d(TAG, "setData called");
		if(people!=null && people.size()>0){
			mPeople = people;
			Collections.sort(mPeople);
			mLoanedToMost = mPeople.get(0);
		}
		invalidate();
		requestLayout();
	}
}