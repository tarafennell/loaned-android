package com.mattallen.loaned;

import android.content.Context;

import com.mattallen.loaned.settings.SettingsFragment;

import java.util.Date;

/**
 * Created by matt on 26/10/14.
 */
public class LoanBuilder
{
	private Person mPerson;
	private Item mItem;
	private Date mAdded;
	private boolean mNotify;

	public LoanBuilder(Context context)
	{
		mNotify = (SettingsFragment.getNotificationPreference(context) != SettingsFragment.NOTIFICATIONS_OFF);
	}

	public Person getPerson()
	{
		return mPerson;
	}

	public void setPerson(Person mPerson)
	{
		this.mPerson = mPerson;
	}

	public Item getItem()
	{
		return mItem;
	}

	public void setItem(Item mItem)
	{
		this.mItem = mItem;
	}

	public Date getAdded()
	{
		return mAdded;
	}

	public void setAdded(Date mAdded)
	{
		this.mAdded = mAdded;
	}

	public boolean isNotifying()
	{
		return mNotify;
	}

	public void setNotifying(boolean mNotify)
	{
		this.mNotify = mNotify;
	}

	public Loan getLoan()
	{
		return new Loan(0, mItem.getItemID(), mPerson.getPersonID(), mNotify, mAdded, null);
	}
}
