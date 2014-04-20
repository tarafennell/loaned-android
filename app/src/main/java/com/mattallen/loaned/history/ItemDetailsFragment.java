package com.mattallen.loaned.history;

import java.text.ParseException;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mattallen.loaned.Item;
import com.mattallen.loaned.ItemTypeLookup;
import com.mattallen.loaned.Loan;
import com.mattallen.loaned.Person;
import com.mattallen.loaned.R;

public class ItemDetailsFragment extends LoanHistoryFragment implements OnClickListener {

	private static final String					TAG = ItemDetailsFragment.class.getSimpleName();

	private TextView							mName, mStatus;
	private ImageView							mPic;
	private Button								mEdit, mReturned;
	private Item								mItem;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.fragment_itemdetails, container, false);
		mName = (TextView)v.findViewById(R.id.itemdetails_name);
		mStatus = (TextView)v.findViewById(R.id.itemdetails_status);
		mEdit = (Button)v.findViewById(R.id.itemdetails_edit);
		mReturned = (Button)v.findViewById(R.id.itemdetails_returned);
		mPic = (ImageView)v.findViewById(R.id.itemdetails_pic);
		mEdit.setOnClickListener(this);
		mReturned.setOnClickListener(this);
		updateInformation();
		return v;
	}

	@Override
	public void updateInformation() {
		Log.d(TAG,"Updating UI");
		mItem = ((LoanHistoryActivity)getActivity()).getItem();
		mPic.setImageResource(ItemTypeLookup.getDrawableForType(mItem.getType()));
		mName.setText(mItem.getName());
		if(mItem.isCurrentlyOnLoan()){
			mStatus.setText(R.string.loanhistory_itemdetails_onloan);
		} else {
			mStatus.setText(R.string.loanhistory_itemdetails_notonloan);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.itemdetails_returned:
			try {
				Loan currentLoan = null;
				String nameOfPerson = "null";
				ArrayList<Loan> loans = ((LoanHistoryActivity)getActivity()).getLoans(mItem);
				for(int i=0;i<=loans.size()-1;i++){
					if(loans.get(i).getReturnDate()==null){
						currentLoan = loans.get(i);
						Person p = ((LoanHistoryActivity)getActivity()).getPersonByID(loans.get(i).getPersonID());
						nameOfPerson = p.getName();
					}
				}
				final Loan finalLoan = currentLoan; // I have to do this because the variable needs to be final
				if(currentLoan!=null){
					AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
					dialog.setTitle(R.string.itemdetails_btn_returned);
					dialog.setMessage(String.format(getActivity().getString(
							R.string.dialog_itemreturned_message), nameOfPerson)).setNegativeButton(R.string.no, null);
					dialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							((LoanHistoryActivity)getActivity()).itemReturned(finalLoan);
						}
					}).show();
				} else {
					AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
					dialog.setTitle(R.string.dialog_noloansfound_title);
					dialog.setPositiveButton(R.string.okay, null);
					dialog.setMessage(R.string.dialog_noloansfound_message_item).show();
				}
			} catch (ParseException e1) {
				Toast.makeText(getActivity(), R.string.error_setting_as_returned, Toast.LENGTH_SHORT).show();
				e1.printStackTrace();
			}
			break;
		case R.id.itemdetails_edit:
			AlertDialog.Builder dialog1 = new AlertDialog.Builder(getActivity());
			dialog1.setTitle(R.string.itemdetails_btn_edit);
			String[] items = new String[3];
			items[0] = getActivity().getString(R.string.dialog_change_category);
			items[1] = getActivity().getString(R.string.dialog_changename);
			items[2] = getActivity().getString(R.string.dialog_deleteitem);
			dialog1.setItems(items, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case 0:
						showCategoryPickDialog();
						break;
					case 1:
						showNameUpdateDialog();
						break;
					case 2:
						AlertDialog.Builder dialog1 = new AlertDialog.Builder(getActivity());
						dialog1.setTitle(R.string.delete);
						dialog1.setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								try {
									((LoanHistoryActivity)getActivity()).deleteItem(mItem);
								} catch (ParseException e) {
									Toast.makeText(getActivity(), R.string.error_deleting_item_and_associated,
											Toast.LENGTH_SHORT).show();
									e.printStackTrace();
								}
							}
						}).setNegativeButton(R.string.cancel, null);
						dialog1.setMessage(R.string.dialog_deleteitem_message).show();
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
						mItem.setName(name);
						((LoanHistoryActivity)getActivity()).updateItem(mItem);
					}
				}
			}
		}).show();
	}

	private void showCategoryPickDialog(){
		AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
		dialog.setTitle(R.string.dialog_change_category);
		ArrayList<String> typesList = ItemTypeLookup.getAllTypes(getActivity());
		String[] types = new String[typesList.size()];
		for(int i=0;i<=typesList.size()-1;i++){
			types[i] = typesList.get(i);
		}
		dialog.setItems(types, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mItem.setType(which);
				((LoanHistoryActivity)getActivity()).updateItem(mItem);
			}
		}).setNegativeButton(R.string.cancel, null).show();
	}
}