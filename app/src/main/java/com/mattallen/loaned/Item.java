package com.mattallen.loaned;

public class Item implements Comparable<Item> {

	private int itemID, itemType, timesLoaned;
	private boolean currentlyOnLoan;
	private String name;
	
	public Item(int itemID, String name, int type, int timesLoaned, boolean currentlyLoaned){
		this.itemID = itemID;
		this.name = name;
		this.itemType = type;
		this.timesLoaned = timesLoaned;
		this.currentlyOnLoan = currentlyLoaned;
	}

	public int getType() {
		return itemType;
	}

	public void setType(int itemType) {
		this.itemType = itemType;
	}

	public int getTimesLoaned() {
		return timesLoaned;
	}

	public void setTimesLoaned(int timesLoaned) {
		this.timesLoaned = timesLoaned;
	}

	public boolean isCurrentlyOnLoan() {
		return currentlyOnLoan;
	}

	public void setCurrentlyOnLoan(boolean currentlyOnLoan) {
		this.currentlyOnLoan = currentlyOnLoan;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getItemID() {
		return itemID;
	}

	@Override
	public int compareTo(Item another) {
		if(another.getTimesLoaned()>timesLoaned){
			return 1;
		} else if(another.getTimesLoaned()==timesLoaned){
			return 0;
		} else {
			return -1;
		}
	}
}