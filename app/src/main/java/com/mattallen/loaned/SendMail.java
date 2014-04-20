package com.mattallen.loaned;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

public class SendMail {

	private static final String TAG = SendMail.class.getName();

	/**
	 * Start intent for sending an email to my email with a set subject.
	 * @param c The Context to run in
	 */
	public static void sendFeedback(Context c){
		Intent intent = new Intent(Intent.ACTION_SEND);
		String emailBody = "API Level: "+Integer.toString(Build.VERSION.SDK_INT)+
				"\nManufacturer: "+Build.MANUFACTURER+"\nModel: "+Build.MODEL+"\n\nMessage\n-----------------\n";
		intent.setType("message/rfc822");
		intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"mattallen092@gmail.com"});
		intent.putExtra(Intent.EXTRA_SUBJECT, "Loaned Feedback");
		intent.putExtra(Intent.EXTRA_TEXT, emailBody);
		Log.i(TAG, "Opening feedback email intent...");
		c.startActivity(Intent.createChooser(intent, "Send Email"));
		Log.i(TAG, "Successful.");
	}

	/**
	 * Send an email to an address about the items on loan
	 * 
	 * @param c Context to run in
	 * @param amount Negate if you owe them
	 * @param email Address to send to
	 */
	public static void emailFriend(Context c, String email){
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("message/rfc822");
		intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
		intent.putExtra(Intent.EXTRA_SUBJECT, "Subject"); // TODO Change this value - be aware of different languages
		String emailBody = "Email Body";
		intent.putExtra(Intent.EXTRA_TEXT, emailBody);
		Log.i(TAG, "Opening email intent...");
		c.startActivity(Intent.createChooser(intent, "Send Email"));
		Log.i(TAG, "Successful.");
	}

	public static void smsFriend(Context c, String phoneNumber){
		// Set the body of the text
		String smsBody = "Body SMS"; // TODO Change this value - be aware of different languages
		Log.d(TAG, "Begin sending SMS...");
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("sms:"+phoneNumber));
		intent.putExtra("sms_body", smsBody);
		c.startActivity(intent);
		Log.d(TAG, "SMS sent");
	}
}