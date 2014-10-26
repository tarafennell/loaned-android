package com.mattallen.loaned;

import java.util.Date;

public class Loan {

	private int loanID, itemID, personID;
	private boolean notifying;
	private Date startDate, returnDate;
	
	public Loan(int loadID, int itemID, int personID, boolean notifying, Date startDate, Date returnDate){
		this.itemID = itemID;
		this.loanID = loadID;
		this.personID = personID;
		this.notifying = notifying;
		this.startDate = startDate;
		this.returnDate = returnDate;
	}

	public int getItemID() {
		return itemID;
	}

	public int getPersonID() {
		return personID;
	}

	public boolean isNotifying() {
		return notifying;
	}

	public void setNotifying(boolean notifying) {
		this.notifying = notifying;
	}

	public Date getStartDate() {
		return startDate;
	}

	public Date getReturnDate() {
		return returnDate;
	}

	public void setReturnDate(Date returnDate) {
		this.returnDate = returnDate;
	}

	public int getLoanID() {
		return loanID;
	}
}