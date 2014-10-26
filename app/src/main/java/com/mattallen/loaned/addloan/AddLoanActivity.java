package com.mattallen.loaned.addloan;

import com.mattallen.loaned.LoanBuilder;
import com.mattallen.loaned.R;
import com.mattallen.loaned.storage.DatabaseManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;

public class AddLoanActivity extends Activity
{
	private static final String FRAGMENT_TAG = "fragment";
	private LoanBuilder mBuilder;
	private DatabaseManager mDB;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addloan);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		// Show first Fragment
		FragmentTransaction mFragMan = getFragmentManager().beginTransaction();
		mFragMan.add(R.id.addloan_frame, new ChoosePersonFragment(), FRAGMENT_TAG);
		mFragMan.commit();
		mDB = new DatabaseManager(this);
	}

	public LoanBuilder getBuilder()
	{
		return mBuilder;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (data != null && requestCode == ChoosePersonFragment.PICK_CONTACT_REQUEST && resultCode == RESULT_OK)
		{
			Uri contactUri = data.getData();
			String[] cols = {ContactsContract.Contacts.DISPLAY_NAME};
			Cursor cursor = getContentResolver().query(contactUri, cols, null, null, null);
			cursor.moveToFirst();
			String result = cursor.getString(0).replaceAll("[-+.^:,']","");
			mDB.addPerson(result, contactUri, 0, 0);
		}
	}

	public void goToNext()
	{
		if (getFragmentManager().findFragmentByTag(FRAGMENT_TAG) instanceof ChoosePersonFragment)
		{
			FragmentTransaction mFragMan = getFragmentManager().beginTransaction();
			mFragMan.replace(R.id.addloan_frame, new ChooseItemFragment());
			mFragMan.addToBackStack(null);
			mFragMan.commit();
		}
		else if (getFragmentManager().findFragmentByTag(FRAGMENT_TAG) instanceof ChooseItemFragment)
		{
			saveLoan();
		}
	}

	public void saveLoan()
	{
		if (mBuilder.getItem().isCurrentlyOnLoan())
		{
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setTitle(R.string.loanhistory_itemdetails_onloan).setMessage(R.string.dialog_addloan_alreadyonloan);
			dialog.setPositiveButton(R.string.okay, null).show();
		}
		else
		{
			mDB.addLoan(mBuilder.getPerson().getPersonID(), mBuilder.getItem().getItemID(), mBuilder.getAdded(), mBuilder.isNotifying());
			mBuilder.getPerson().setItemsLoaned(mBuilder.getPerson().getItemsLoaned() + 1);
			mBuilder.getPerson().setItemsOnLoan(mBuilder.getPerson().getItemsOnLoan() + 1);
			mDB.updatePerson(mBuilder.getPerson());
			mBuilder.getItem().setCurrentlyOnLoan(true);
			mBuilder.getItem().setTimesLoaned(mBuilder.getItem().getTimesLoaned() + 1);
			mDB.updateItem(mBuilder.getItem());
		}
	}
}