package com.mattallen.loaned.history;

import java.text.ParseException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mattallen.loaned.Item;
import com.mattallen.loaned.Loan;
import com.mattallen.loaned.Person;
import com.mattallen.loaned.R;
import com.mattallen.loaned.RetrieveContactPhoto;
import com.mattallen.loaned.views.RoundedImageView;

public class PersonDetailsFragment extends LoanHistoryFragment implements OnClickListener {

	private static final String					TAG = PersonDetailsFragment.class.getSimpleName();

	public static final int 					PICK_CONTACT_REQUEST = 1;

	private TextView							mName, mStatus;
	private RoundedImageView					mPic;
	private Button								mEdit, mReturned;
	private Person								mPerson;
	private ArrayList<Item>						mItems;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.fragment_persondetails, container, false);
		mName = (TextView)v.findViewById(R.id.persondetails_name);
		mStatus = (TextView)v.findViewById(R.id.persondetails_status);
		mEdit = (Button)v.findViewById(R.id.persondetails_edit);
		mReturned = (Button)v.findViewById(R.id.persondetails_returned);
		mPic = (RoundedImageView)v.findViewById(R.id.persondetails_pic);
		mEdit.setOnClickListener(this);
		mReturned.setOnClickListener(this);
		mItems = ((LoanHistoryActivity)getActivity()).getAllItems();
		updateInformation();
		return v;
	}

	@Override
	public void updateInformation() {
		Log.d(TAG,"Updating UI");
		mReturned.setEnabled(true);
		mPerson = ((LoanHistoryActivity)getActivity()).getPerson();
		new RetrieveContactPhoto(
                mPerson.getLookupURI(),
                mPic, getActivity(),
                R.drawable.friend_image_light
        ).execute();
		mName.setText(mPerson.getName());
		if(mPerson.getItemsOnLoan()>0){
			mStatus.setText(String.format(getActivity().getString(
					R.string.loanhistory_persondetails_itemsonloan), mPerson.getItemsOnLoan()));
		} else {
			mStatus.setText(R.string.loanhistory_persondetails_noloans);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.persondetails_returned:
			AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
			if(mPerson.getItemsOnLoan()>0){
				try {
					dialog.setTitle(R.string.persondetails_btn_returned);
					dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
					ArrayList<Loan> loans = ((LoanHistoryActivity)getActivity()).getLoans(mPerson);
					final ArrayList<Loan> currentLoans = new ArrayList<Loan>();
					String[] currentLoanString = new String[mPerson.getItemsOnLoan()];
					for(int i=0;i<=loans.size()-1;i++){
						if(loans.get(i).getReturnDate()==null){
							currentLoanString[currentLoans.size()] = getItemName(loans.get(i).getItemID());
							currentLoans.add(loans.get(i));
						}
					}
					dialog.setItems(currentLoanString, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							((LoanHistoryActivity)getActivity()).itemReturned(currentLoans.get(which));
							mReturned.setEnabled(false);
						}
					});
					dialog.show();
				} catch (Exception e) {
					Toast.makeText(getActivity(), R.string.error_gettingloans, Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}
			} else {
				dialog.setTitle(R.string.dialog_noloansfound_title).setMessage(R.string.dialog_noloansfound_message_person);
				dialog.setNegativeButton(R.string.okay, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				dialog.show();
			}
			break;
		case R.id.persondetails_edit:
			AlertDialog.Builder dialog1 = new AlertDialog.Builder(getActivity());
			dialog1.setTitle(R.string.persondetails_btn_edit);
			String items[];
			// Need to instantiate with exact number. Avoids NullPointer on ArrayAdapter
			// http://stackoverflow.com/questions/3544738/android-arrayadapter-createviewfromresourceint-view-viewgroup-int-line-3
			if(mPerson.getLookupURI()!=null){
				items = new String[4];
				items[0] = getActivity().getResources().getString(R.string.dialog_changename);
				items[1] = getActivity().getString(R.string.dialog_deleteperson_title);
				items[2] = getActivity().getString(R.string.dialog_relink_to_contact);
				items[3] = getActivity().getString(R.string.dialog_unlink_from_contact);
			} else {
				items = new String[3];
				items[0] = getActivity().getResources().getString(R.string.dialog_changename);
				items[1] = getActivity().getString(R.string.dialog_deleteperson_title);
				items[2] = getActivity().getString(R.string.dialog_link_to_contact);
			}
			dialog1.setItems(items, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case 0:
						showNameUpdateDialog();
						break;
					case 1:
						AlertDialog.Builder dialog1 = new AlertDialog.Builder(getActivity());
						dialog1.setTitle(R.string.dialog_deleteperson);
						dialog1.setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								try {
									((LoanHistoryActivity)getActivity()).deletePerson(mPerson);
								} catch (ParseException e) {
									Toast.makeText(getActivity(), R.string.error_deleting_person_and_associated,
											Toast.LENGTH_SHORT).show();
									e.printStackTrace();
								}
							}
						}).setNegativeButton(R.string.cancel, null);
						dialog1.setMessage(R.string.dialog_deleteperson_message).show();
						break;
					case 2:
						openContactPicker();
						break;
					case 3:
						mPerson.setLookupURI(null);
						((LoanHistoryActivity)getActivity()).updatePerson(mPerson);
						break;
					default:
						break;
					}
				}
			});
			dialog1.setNegativeButton(R.string.cancel, null).show();
			break;
		default:
			break;
		}
	}

	private String getItemName(int itemID){
		for(int i=0;i<=mItems.size()-1;i++){
			if(itemID==mItems.get(i).getItemID()){
				return mItems.get(i).getName();
			}
		}
		return "Error!";
	}

	private void showNameUpdateDialog(){
		AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
		dialog.setTitle(R.string.dialog_changename);
		final EditText nameEntry = new EditText(getActivity());
		dialog.setView(nameEntry);
		dialog.setNegativeButton(R.string.cancel, null);
		dialog.setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(nameEntry.getText().toString()!=null && nameEntry.getText().toString().length()>0){
					// Name dialog is not empty
					String name = nameEntry.getText().toString();
					name.replaceAll("[-+.^:,']","").trim();
					if(name.length()==0){
						Toast.makeText(getActivity(), R.string.error_emptyname, Toast.LENGTH_SHORT).show();
					} else{
						mPerson.setName(name);
						((LoanHistoryActivity)getActivity()).updatePerson(mPerson);
					}
				}
			}
		}).show();
	}

	private void openContactPicker(){
		Intent pickContactIntent = new Intent(Intent.ACTION_GET_CONTENT);
		pickContactIntent.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);
		startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		if(requestCode==PersonDetailsFragment.PICK_CONTACT_REQUEST && resultCode==Activity.RESULT_OK){
			try{
				Log.d(TAG, "Callback for contact picker called");
				Uri contactUri = data.getData();
				String[] cols = {ContactsContract.Contacts.DISPLAY_NAME};
				Cursor cursor = getActivity().getContentResolver().query(contactUri, cols, null, null, null);
				cursor.moveToFirst();
				String result = cursor.getString(0).replaceAll("[-+.^:,']","");
				mPerson.setLookupURI(contactUri);
				mPerson.setName(result);
				((LoanHistoryActivity)getActivity()).updatePerson(mPerson);
			} catch (Exception e){
				Toast.makeText(getActivity(), R.string.error_friendnotadded, Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}
		}
	}
}