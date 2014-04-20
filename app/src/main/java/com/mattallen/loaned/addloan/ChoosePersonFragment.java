package com.mattallen.loaned.addloan;

import java.util.ArrayList;

import com.mattallen.loaned.Person;
import com.mattallen.loaned.R;
import com.mattallen.loaned.storage.DatabaseManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ChoosePersonFragment extends Fragment implements OnItemClickListener {

	private static final String				TAG = ChoosePersonFragment.class.getSimpleName();

	public static final int 				PICK_CONTACT_REQUEST = 1;

	private TextView						mEmptyState;
	private ProgressBar						mProgress;
	private ListView						mList;
	private ChoosePersonAdapter				mAdapter;
	private DatabaseManager					mDB;
	private ArrayList<Person>				mPeople;
	private AddLoanCallback					mCallback;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true); // Tell the activity that we have ActionBar items
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.fragment_chooseperson, container, false);
		mEmptyState = (TextView)v.findViewById(R.id.chooseperson_emptystate);
		mProgress = (ProgressBar)v.findViewById(R.id.chooseperson_progress);
		mList = (ListView)v.findViewById(R.id.chooseperson_list);
		mList.setOnItemClickListener(this);
		mDB = new DatabaseManager(getActivity());
		Log.d(TAG, "Fragment created");
		return v;
	}

	@Override
	public void onStart(){
		super.onStart();
		new GetPeople().execute();
	}

	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		try{
			mCallback = (AddLoanCallback)activity;
		}catch(ClassCastException e){
			throw new ClassCastException(activity.toString() + "\n" +
					mCallback.getClass().getName() + " not implemented in host activity");
		}
	}

	/*
	 * Here we add the extra menu items needed into the ActionBar. Even with
	 * implementing this method, we still need to tell the Activity that we
	 * have menu items to add
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu,MenuInflater inf){
		super.onCreateOptionsMenu(menu, inf);
		inf.inflate(R.menu.chooseperson, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case R.id.action_addperson:
			Log.d(TAG, "Add person menu item clicked");
			AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
			dialog.setTitle(R.string.action_addperson);
			dialog.setItems(new CharSequence[]{getActivity().getResources().getString(R.string.dialog_addfromcontacts),
					getActivity().getResources().getString(R.string.dialog_addbyname)}, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (which==0){
						Log.d(TAG, "Opening contact picker");
						openContactPicker();
					}
					else if(which==1){
						Log.d(TAG, "Showing name entry dialog");
						addFriendByName();
					}
				}
			});
			dialog.show();
			return true;

		default:
			break;
		}
		return false;
	}

	public void addFriendByName(){
		AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
		dialog.setTitle(R.string.dialog_addbyname);
		final EditText nameEntry = new EditText(getActivity());
		dialog.setView(nameEntry);
		dialog.setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String name = nameEntry.getText().toString();
				try {
					if(name==null || name.equals("")){
						throw new NullPointerException();
					}
					mDB.addPerson(name, null, 0, 0);
					new GetPeople().execute();
				} catch (SQLException e) {
					Toast.makeText(getActivity(), R.string.error_friendnotadded, Toast.LENGTH_SHORT).show();
				} catch (NullPointerException e){
					Toast.makeText(getActivity(), R.string.error_emptyname, Toast.LENGTH_SHORT).show();
				}
			}
		});
		dialog.show();
	}

	public void addContactCallback(Intent data){
		try{
			Log.d(TAG, "Callback for contact picker called");
			Uri contactUri = data.getData();
			String[] cols = {ContactsContract.Contacts.DISPLAY_NAME};
			Cursor cursor = getActivity().getContentResolver().query(contactUri, cols, null, null, null);
			cursor.moveToFirst();
			String result = cursor.getString(0).replaceAll("[-+.^:,']","");
			mDB.addPerson(result, contactUri, 0, 0);
			new GetPeople().execute();
		} catch (Exception e){
			Toast.makeText(getActivity(), "Could not add this person to Loaned", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}

	private void openContactPicker(){
		Intent pickContactIntent = new Intent(Intent.ACTION_GET_CONTENT);
		pickContactIntent.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);
		getActivity().startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
	}

	private class GetPeople extends AsyncTask<Void, Void, Exception>{
		@Override
		protected void onPreExecute(){
			mProgress.setVisibility(ProgressBar.VISIBLE);
			mList.setVisibility(GridView.INVISIBLE);
			mEmptyState.setVisibility(TextView.INVISIBLE);
			mPeople = null;
		}
		@Override
		protected Exception doInBackground(Void... params) {
			try{
				Log.d(TAG, "Getting all people from the database");
				mPeople = mDB.getAllPeople();
			} catch (Exception e){
				return e;
			}
			return null;
		}
		@Override
		protected void onPostExecute(Exception e){
			mProgress.setVisibility(ProgressBar.INVISIBLE);
			if(e!=null){
				Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
			} else {
				if(mPeople!=null && mPeople.size()>=1){
					mAdapter = new ChoosePersonAdapter(getActivity(), R.layout.fragment_chooseperson_item, mPeople);
					mList.setAdapter(mAdapter);
					mList.setVisibility(ListView.VISIBLE);
				} else {
					mEmptyState.setVisibility(TextView.VISIBLE);
				}
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> list, View listItem, int position, long arg3) {
		Person person = (Person)listItem.getTag();
		Log.d(TAG, "List item clicked. Using callback");
		mCallback.onNextButtonClicked(person);
	}
}