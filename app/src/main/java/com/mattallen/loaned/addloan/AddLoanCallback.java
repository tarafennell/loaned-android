package com.mattallen.loaned.addloan;

import com.mattallen.loaned.Item;
import com.mattallen.loaned.Person;

public interface AddLoanCallback {
	public void onNextButtonClicked(Person person);
	public void onSaveLoan(Item item);
    public void onCreateNewItem();
}