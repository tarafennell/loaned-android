package com.mattallen.loaned;

import android.net.Uri;

public class Person implements Comparable<Person> {

	private int personID, itemsOnLoan, itemsLoaned;
	private String name;
	private Uri lookupURI;
	
	public Person(int personID, String name, int itemsOnLoan, int itemsLoaned,
			Uri lookupURI) {
		super();
		this.personID = personID;
		this.name = name;
		this.itemsOnLoan = itemsOnLoan;
		this.itemsLoaned = itemsLoaned;
		this.lookupURI = lookupURI;
	}
	
	public int getItemsOnLoan() {
		return itemsOnLoan;
	}

	public void setItemsOnLoan(int itemsOnLoan) {
		this.itemsOnLoan = itemsOnLoan;
	}

	public int getItemsLoaned() {
		return itemsLoaned;
	}

	public void setItemsLoaned(int itemsLoaned) {
		this.itemsLoaned = itemsLoaned;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Uri getLookupURI() {
		return lookupURI;
	}

	public void setLookupURI(Uri lookupURI) {
		this.lookupURI = lookupURI;
	}

	public int getPersonID() {
		return personID;
	}

	@Override
	public int compareTo(Person another) {
		if(another.getItemsLoaned()>itemsLoaned){
			return 1;
		} else if(another.getItemsLoaned()==itemsLoaned){
			return 0;
		} else {
			return -1;
		}
	}
}