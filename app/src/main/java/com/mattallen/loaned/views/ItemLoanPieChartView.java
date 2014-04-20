package com.mattallen.loaned.views;

import java.util.ArrayList;
import java.util.Collections;

import com.mattallen.loaned.Item;
import com.mattallen.loaned.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class ItemLoanPieChartView extends View {

	private static final String				TAG = ItemLoanPieChartView.class.getSimpleName();

	private ArrayList<Item>					mItems;
	private double							mDivisionFactor;
	private int								mTotalLoans;
	private Paint							mCentreCirclePaint, mCentreTextPaint, mCentreNumberPaint;
	private RectF							mRect;
	private int[]							mColours;
	private Item							mMostLoanedItem;

	public ItemLoanPieChartView(Context context) {
		super(context);
		init(context);
	}

	public ItemLoanPieChartView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context c){
		Log.d(TAG,"Init called");
		// Create the colour array
		mColours = new int[6];
		mColours[0] = Color.parseColor("#1bc5a4");
		mColours[1] = Color.parseColor("#9b59b6");
		mColours[2] = Color.parseColor("#2ecc71");
		mColours[3] = Color.parseColor("#e74c3c");
		mColours[4] = Color.parseColor("#3498db");
		mColours[5] = Color.parseColor("#e67e22");

		mCentreCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mCentreCirclePaint.setColor(Color.parseColor("#f3f3f3"));
		mCentreCirclePaint.setStyle(Paint.Style.FILL);

		mCentreTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mCentreTextPaint.setTypeface(Typeface.create("sans-serif-condensed", Typeface.NORMAL));
		mCentreTextPaint.setColor(mColours[0]);
		mCentreTextPaint.setTextAlign(Align.CENTER);
		mCentreTextPaint.setTextSize(80);
		mCentreNumberPaint = new Paint(mCentreTextPaint);
		mCentreNumberPaint.setTextSize(190);
		mCentreNumberPaint.setColor(c.getResources().getColor(R.color.text_main));
		mCentreNumberPaint.setTypeface(Typeface.create("sans-serif-thin", Typeface.NORMAL));
		mRect = new RectF();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	protected void onDraw(Canvas canvas) {
		Log.d(TAG, "onDraw Called");
		super.onDraw(canvas);
		mRect.set(0, 0, getWidth(), getHeight());
		if(mItems!=null){
			float lastAngle=0f;
			for(int i=0;i<=mItems.size()-1;i++){
				float timesLoaned = mItems.get(i).getTimesLoaned();
				float angle = (float)(timesLoaned/mDivisionFactor);
				canvas.drawArc(mRect, lastAngle, angle, true, getPiePaint(i));
				lastAngle += angle;
			}
			// Do the middle bit
			canvas.drawCircle(getWidth()/2, getHeight()/2, (float)(getWidth()*0.42), mCentreCirclePaint);
			canvas.drawText(mMostLoanedItem.getName(), getWidth()/2, (getHeight()/2)-40, mCentreTextPaint);
			canvas.drawText(Integer.toString(mMostLoanedItem.getTimesLoaned()), getWidth()/2,
					(getHeight()/2)+(mCentreNumberPaint.getTextSize()-20), mCentreNumberPaint);
			canvas.save();
		}
	}

	public void setData(ArrayList<Item> items){
		// Generate multiplication factor (Total/360)
		mTotalLoans = 0;
		mItems = items;
		if(mItems!=null && mItems.size()>0){
			Collections.sort(mItems);
			mMostLoanedItem = mItems.get(0); // Get the first one to compare the rest to initially
			for(int i=0;i<=mItems.size()-1;i++){
				mTotalLoans+=mItems.get(i).getTimesLoaned();
			}
			mDivisionFactor = (float)(mTotalLoans/360.00);
			invalidate();
			requestLayout();
		}
	}

	private Paint getPiePaint(int position){
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.FILL);
		if(position<mColours.length){
			Log.i(TAG,"Setting paint colour to "+mColours[position]);
			paint.setColor(mColours[position]);
		} else {
			Log.i(TAG,"Setting paint colour to "+mColours[mColours.length-1]);
			paint.setColor(mColours[mColours.length-1]);
		}
		return paint;
	}
}