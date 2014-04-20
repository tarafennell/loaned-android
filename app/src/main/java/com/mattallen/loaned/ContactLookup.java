package com.mattallen.loaned;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

public class ContactLookup {

	private static final String TAG = ContactLookup.class.getName();

	private static int[] typesPhone = new int[]{ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE, ContactsContract.CommonDataKinds.Phone.TYPE_MAIN, 
		ContactsContract.CommonDataKinds.Phone.TYPE_HOME, ContactsContract.CommonDataKinds.Phone.TYPE_WORK};
	private static int[] typesEmail = new int[]{ContactsContract.CommonDataKinds.Phone.TYPE_HOME, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE, 
		ContactsContract.CommonDataKinds.Phone.TYPE_WORK};

	public static Bitmap getContactPhoto(final Uri lookupUri, final Context c, boolean preferHighRes) throws IOException{
		try{
			Cursor cursor = c.getContentResolver().query(ContactsContract.Data.CONTENT_URI,
					null, ContactsContract.Data.CONTACT_ID + "=" + lookupUri.getLastPathSegment() + " AND "
							+ ContactsContract.Data.MIMETYPE + "='"
							+ ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE
							+ "'", null, null);
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					if(cursor.getString(68)==null){ // Column 68 is photoID - should return an int on a String
						cursor.close();
						return null; // No photo stored
					}
					InputStream photo_stream = ContactsContract.Contacts.openContactPhotoInputStream(c.getContentResolver(), 
							lookupUri, preferHighRes);
					Bitmap contactImg = null;
					try{
						BufferedInputStream input = new BufferedInputStream(photo_stream);
						contactImg = BitmapFactory.decodeStream(input);
						input.close();
					} catch (IOException e){
						Log.e(TAG, e.getMessage());
						contactImg = null;
					}
					cursor.close();
					return contactImg;
				} else {
					cursor.close();
					return null; // No entry
				}
			} else {
				return null; // Error in cursor process
			}

		} catch (Exception e) {
			Log.i(TAG, e.getMessage());
			return null;
		}

	}

	/**
	 * @param c
	 * @param contactID
	 * @return Phone number, if available. The search order is: mobile, main, home, work.
	 */
	public static HashMap<String,String> getContactPhoneNumber(Context c, String contactID){
		HashMap<String,String> phoneNums = new HashMap<String,String>();
		for(int i=0;i<=typesPhone.length-1;i++){
			String[] whereArgs = new String[] {String.valueOf(contactID), String.valueOf(typesPhone[i])};
			String phoneNum = queryContactForPhoneNum(c, whereArgs);
			if(phoneNum!=null){
				if(ContactsContract.CommonDataKinds.Phone.TYPE_HOME==typesPhone[i]){
					phoneNums.put("Home", phoneNum);
				}
				else if(ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE==typesPhone[i]){
					phoneNums.put("Mobile", phoneNum);
				}
				else if(ContactsContract.CommonDataKinds.Phone.TYPE_WORK==typesPhone[i]){
					phoneNums.put("Work", phoneNum);
				}
				else if(ContactsContract.CommonDataKinds.Phone.TYPE_MAIN==typesPhone[i]){
					phoneNums.put("Main", phoneNum);
				}
			}
		}
		return phoneNums;
	}

	public static boolean hasContactData(Context c, String contactID){
		boolean hasData = false;
		for(int i=0;i<=typesPhone.length-1;i++){
			String[] whereArgs = new String[] {String.valueOf(contactID), String.valueOf(typesPhone[i])};
			String phoneNum = queryContactForPhoneNum(c, whereArgs);
			if(phoneNum!=null){
				hasData = true;
				break;
			}
		}
		if(!hasData){
			for(int i=0;i<=typesEmail.length-1;i++){
				String[] whereArgs = new String[] {String.valueOf(contactID), String.valueOf(typesEmail[i])};
				String email = queryContactForEmail(c, whereArgs);
				if(email!=null){
					hasData = true;
					break;
				}
			}
		}
		return hasData;
	}

	private static String queryContactForPhoneNum(Context c, String[] whereArgs){
		String phoneNumber = null;
		Cursor cursor = c.getContentResolver().query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? and " + ContactsContract.CommonDataKinds.Phone.TYPE + " = ?", 
				whereArgs, null);

		int phoneNumberIndex = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER);

		if (cursor != null) {
			Log.d(TAG, "Returned contact count: "+cursor.getCount());
			try {
				if (cursor.moveToFirst()) {
					phoneNumber = cursor.getString(phoneNumberIndex);
				}
			} finally {
				cursor.close();
			}
		}
		Log.i(TAG, "Returning phone number: " + phoneNumber);
		return phoneNumber;
	}


	/**
	 * Get the main email address of the contact
	 * @param contactID The last known ContactID of the contact
	 * @param context The context to run in
	 * @return String representation of their email address
	 * @throws CursorIndexOutOfBoundsException
	 */
	public static HashMap<String,String> getContactsEmailAddress(final String contactID, final Context c) throws CursorIndexOutOfBoundsException{
		/*
		 * For some shitting reason, using ContactsContract.CommonDataKinds.Phone works instead of Email?
		 * Leaving it anyway, might just be some stupid HTC Sense 5 bug
		 */
		HashMap<String,String> emails = new HashMap<String,String>();
		for(int i=0;i<=typesEmail.length-1;i++){
			String[] whereArgs = new String[] {String.valueOf(contactID), String.valueOf(typesEmail[i])};
			String email = queryContactForEmail(c, whereArgs);
			if(email!=null){
				if(ContactsContract.CommonDataKinds.Phone.TYPE_HOME==typesEmail[i]){
					emails.put("Home", email);
				}
				else if(ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE==typesEmail[i]){
					emails.put("Other", email);
				}
				else if(ContactsContract.CommonDataKinds.Phone.TYPE_WORK==typesEmail[i]){
					emails.put("Work", email);
				}
			}
		}
		return emails;
	}

	private static String queryContactForEmail(Context c, String[] whereArgs){
		String phoneNumber = null;
		Cursor cursor = c.getContentResolver().query(
				ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
				ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ? and " + ContactsContract.CommonDataKinds.Email.TYPE + " = ?", 
				whereArgs, null);

		int phoneNumberIndex = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Email.ADDRESS);

		if (cursor != null) {
			Log.d(TAG, "Returned contact count: "+cursor.getCount());
			try {
				if (cursor.moveToFirst()) {
					phoneNumber = cursor.getString(phoneNumberIndex);
				}
			} finally {
				cursor.close();
			}
		}
		Log.i(TAG, "Returning email address: " + phoneNumber);
		return phoneNumber;
	}
}