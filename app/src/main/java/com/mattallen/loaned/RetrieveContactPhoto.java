package com.mattallen.loaned;

import java.io.IOException;

import android.content.Context;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.ImageView;

public class RetrieveContactPhoto extends AsyncTask<Void, Void, Bitmap> {
	// Potential overlapping issue:
	// http://stackoverflow.com/questions/1906132/android-listvitem-asynctask-images-overlapping
	/**
	 * Retrieve a contact's photo from the Android address book
	 */
	private static final String TAG = RetrieveContactPhoto.class.getName();
	private Uri contactUri;
	private ImageView imageView;
	private Context c;
	private int backup;
	
	public RetrieveContactPhoto(Uri contactUri, ImageView iView, Context c, int preferredBackupImage){
		this.contactUri = contactUri;
		this.imageView = iView;
		this.c = c;
		this.backup = preferredBackupImage;
	}
	
	@Override
	protected void onPreExecute(){
		imageView.setImageDrawable(c.getResources().getDrawable(backup));
	}
	
	@Override
	protected Bitmap doInBackground(Void... params) {
		Bitmap image;
		try {
			image = ContactLookup.getContactPhoto(contactUri, c, true);
		} catch (IOException e) {
			image = null;
		} catch (SQLException e){
			image = null;
			Log.e(TAG,"SQL Exception occured, Possibly device-specific");
			Log.e(TAG,"Device: "+Build.MANUFACTURER+" "+Build.MODEL);
		} catch (NullPointerException e){
			image = null;
			String logMsg = "Not using photo from contacts. hasLookupID=";
			logMsg += (contactUri!=null) ? "true" : "false";
			logMsg += ", API="+Integer.toString(Build.VERSION.SDK_INT);
			Log.i(TAG, logMsg);
		}
		return image;
	}
	
	@Override
	protected void onPostExecute(Bitmap image) {
		if(image!=null){
			imageView.setImageBitmap(image);
		} else {
			imageView.setImageDrawable(c.getResources().getDrawable(backup));
		}
	}
}