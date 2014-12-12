package com.mattallen.loaned.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.mattallen.loaned.Item;
import com.mattallen.loaned.ItemTypeLookup;
import com.mattallen.loaned.Loan;
import com.mattallen.loaned.Person;
import com.mattallen.loaned.views.ItemType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class DatabaseManager extends SQLiteOpenHelper {

	public static final String 			DATABASE_NAME = "loaned.db";
	public static final int				DATABASE_VERSION = 1;

	private static final String			DATE_FORMAT = "EEE MMM dd HH:mm:ss zzz yyyy";

	private static final String			LOAN_TABLENAME = "loans";
	private static final String			LOAN_ID = "loanID";
	private static final String			LOAN_PERSONID = "personID";
	private static final String			LOAN_ITEMID = "itemID";
	private static final String			LOAN_STARTDATE = "startdate";
	private static final String			LOAN_RETURNDATE = "returndate";
	private static final String			LOAN_NOTIFY = "notify";

	private static final String			PEOPLE_TABLENAME = "people";
	private static final String			PEOPLE_ID = "personID";
	private static final String			PEOPLE_NAME = "name";
	private static final String			PEOPLE_LOOKUPURI = "lookupUri";
	private static final String			PEOPLE_ITEMSONLOAN = "itemsOnLoan";
	private static final String			PEOPLE_ITEMSLOANED = "itemsLoaned";

	private static final String			ITEMS_TABLENAME = "items";
	private static final String			ITEMS_ID = "itemID";
	private static final String			ITEMS_NAME = "name";
	private static final String			ITEMS_TYPE = "type";
	private static final String			ITEMS_TIMESLOANED = "timesLoaned";
	private static final String			ITEMS_CURRENTLYONLOAN = "currentlyOnLoan";

    private static final String         TYPE_TABLENAME = "type";
    private static final String         TYPE_ID = "_id";
    public static final String			TYPE_NAME = "name";
    public static final String			TYPE_ICON = "icon";

    private final Context mContext;

    public DatabaseManager(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// Create items table
		db.execSQL(String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s INTEGER, %s INTEGER, %s INTEGER)",
				ITEMS_TABLENAME, ITEMS_ID, ITEMS_NAME, ITEMS_TYPE, ITEMS_TIMESLOANED, ITEMS_CURRENTLYONLOAN));
		// Create people table
		db.execSQL(String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s INTEGER, %s INTEGER)",
				PEOPLE_TABLENAME, PEOPLE_ID, PEOPLE_NAME, PEOPLE_LOOKUPURI, PEOPLE_ITEMSONLOAN, PEOPLE_ITEMSLOANED));
		// Create loans table
		db.execSQL(String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s INTEGER, %s INTEGER, %s STRING, %s STRING, %s INTEGER)",
				LOAN_TABLENAME, LOAN_ID, LOAN_PERSONID, LOAN_ITEMID, LOAN_STARTDATE, LOAN_RETURNDATE, LOAN_NOTIFY));
        // Create type table
        db.execSQL(String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s STRING, %s INTEGER)",
                TYPE_TABLENAME, TYPE_ID, TYPE_NAME, TYPE_ICON));

        //create default types


        ArrayList<String> types = new ArrayList<String>();
        for(int i=0;i<=9;i++){
            db.execSQL(String.format("INSERT INTO %s (%s, %s)" +
                            "VALUES ('%s', '" + ItemTypeLookup.getDrawableForType(i) + "');",
                    TYPE_TABLENAME, TYPE_NAME, TYPE_ICON, ItemTypeLookup.getNameByID(mContext, i)
            ));
        }
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// No upgrade path needed yet
        //FIXME add upgrade code
	}

	public ArrayList<Person> getAllPeople(){
		SQLiteDatabase db = getReadableDatabase();
		ArrayList<Person> people = new ArrayList<Person>();
		Cursor c;
		c = db.query(PEOPLE_TABLENAME, null, null, null, null, null, null);
		if(c.moveToFirst()){
			do{
				Uri lookup = null;
				if(c.getString(2)!=null){
					lookup = Uri.parse(c.getString(2));
				}
				people.add(new Person(c.getInt(0), c.getString(1), c.getInt(3), c.getInt(4), lookup));
			}while(c.moveToNext());
		}
		db.close();
		return people;
	}

	public ArrayList<Item> getAllItems(){
		SQLiteDatabase db = getReadableDatabase();
		ArrayList<Item> items = new ArrayList<Item>();
		Cursor c;
		c = db.query(ITEMS_TABLENAME, null, null, null, null, null, null);
		if(c.moveToFirst()){
			do{
				boolean onLoan = false;
				if(c.getInt(4)==1){
					onLoan = true;
				}
				items.add(new Item(c.getInt(0), c.getString(1), c.getInt(2), c.getInt(3), onLoan));
			}while(c.moveToNext());
		}
		db.close();
		return items;
	}

    public ArrayList<ItemType> getAllItemTypes(){
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<ItemType> items = new ArrayList<ItemType>();
        Cursor c;
        c = db.query(TYPE_TABLENAME, null, null, null, null, null, null);
        if(c.moveToFirst()){
            do{
                items.add(new ItemType(c.getInt(0), c.getString(1), c.getInt(2)));
            }while(c.moveToNext());
        }
        db.close();
        return items;
    }

    public Cursor getItemTypesCursor(){
        SQLiteDatabase db = getReadableDatabase();
        return  db.query(TYPE_TABLENAME, null, null, null, null, null, null);
    }

	public Item getItemByID(int itemID){
		SQLiteDatabase db = getReadableDatabase();
		Cursor c = db.query(ITEMS_TABLENAME, null, ITEMS_ID+"=?", new String[]{Integer.toString(itemID)}, null, null, null);
		if(c.moveToFirst()){
			boolean onLoan = false;
			if(c.getInt(4)==1){
				onLoan = true;
			}
			db.close();
			return new Item(c.getInt(0), c.getString(1), c.getInt(2), c.getInt(3), onLoan);
		}
		db.close();
		return null;
	}

    public ItemType getItemTypeByID(int itemTypeID){
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TYPE_TABLENAME, null, TYPE_ID+"=?", new String[]{Integer.toString(itemTypeID)}, null, null, null);
        if(c.moveToFirst()){
            db.close();
            return new ItemType(c.getInt(0), c.getString(1), c.getInt(2));
        }
        db.close();
        return null;
    }

	public Person getPersonByID(int personID){
		SQLiteDatabase db = getReadableDatabase();
		Cursor c = db.query(PEOPLE_TABLENAME, null, PEOPLE_ID+"=?", new String[]{Integer.toString(personID)}, null, null, null);
		if(c.moveToFirst()){
			db.close();
			Uri lookup = null;
			if(c.getString(2)!=null){
				lookup = Uri.parse(c.getString(2));
			}
			return new Person(c.getInt(0), c.getString(1), c.getInt(3), c.getInt(4), lookup);
		}
		db.close();
		return null;
	}

	public ArrayList<Loan> getAllLoans() throws ParseException{
		SQLiteDatabase db = getReadableDatabase();
		ArrayList<Loan> loans = new ArrayList<Loan>();
		Cursor c = db.query(LOAN_TABLENAME, null, null, null, null, null, null);
		if(c.moveToFirst()){
			do{
				boolean notify = false;
				if(c.getInt(5)==1){
					notify = true;
				}
				SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
				Date returnDate = null;
				if(c.getString(4)!=null){
					returnDate = sdf.parse(c.getString(4));
				}
				loans.add(new Loan(c.getInt(0), c.getInt(2), c.getInt(1), notify,
						sdf.parse(c.getString(3)), returnDate));
			}while(c.moveToNext());
		}
		db.close();
		Collections.reverse(loans);
		return loans;
	}

	public ArrayList<Loan> getAllLoansByPersonID(int personID) throws ParseException{
		SQLiteDatabase db = getReadableDatabase();
		ArrayList<Loan> loans = new ArrayList<Loan>();
		Cursor c = db.query(LOAN_TABLENAME, null, PEOPLE_ID+"=?", new String[]{Integer.toString(personID)}, null, null, null);
		if(c.moveToFirst()){
			do{
				boolean notify = false;
				if(c.getInt(5)==1){
					notify = true;
				}
				SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
				Date returnDate = null;
				if(c.getString(4)!=null){
					returnDate = sdf.parse(c.getString(4));
				}
				loans.add(new Loan(c.getInt(0), c.getInt(2), c.getInt(1), notify,
						sdf.parse(c.getString(3)), returnDate));
			}while(c.moveToNext());
		}
		db.close();
		Collections.reverse(loans);
		return loans;
	}

	public ArrayList<Loan> getAllLoansByItemID(int itemID) throws ParseException{
		SQLiteDatabase db = getReadableDatabase();
		ArrayList<Loan> loans = new ArrayList<Loan>();
		Cursor c = db.query(LOAN_TABLENAME, null, ITEMS_ID+"=?", new String[]{Integer.toString(itemID)}, null, null, null);
		if(c.moveToFirst()){
			do{
				boolean notify = false;
				if(c.getInt(5)==1){
					notify = true;
				}
				SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
				Date returnDate = null;
				if(c.getString(4)!=null){
					returnDate = sdf.parse(c.getString(4));
				}
				loans.add(new Loan(c.getInt(0), c.getInt(2), c.getInt(1), notify,
						sdf.parse(c.getString(3)), returnDate));
			}while(c.moveToNext());
		}
		db.close();
		Collections.reverse(loans);
		return loans;
	}

	public Loan getLoanByID(int loadID) throws ParseException{
		SQLiteDatabase db = getReadableDatabase();
		Cursor c = db.query(LOAN_TABLENAME, null, LOAN_ID+"=?", new String[]{Integer.toString(loadID)}, null, null, null);
		if(c.moveToFirst()){
			boolean notify = false;
			if(c.getInt(5)==1){
				notify = true;
			}
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
			Date returnDate = null;
			if(c.getString(4)!=null){
				returnDate = sdf.parse(c.getString(4));
			}
			return new Loan(c.getInt(0), c.getInt(2), c.getInt(1), notify,
					sdf.parse(c.getString(3)), returnDate);
		} else {
			db.close();
			return null;
		}
	}

	public void updatePerson(Person person){
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(PEOPLE_ITEMSLOANED, person.getItemsLoaned());
		values.put(PEOPLE_ITEMSONLOAN, person.getItemsOnLoan());
		if(person.getLookupURI()!=null){
			values.put(PEOPLE_LOOKUPURI, person.getLookupURI().toString());
		} else {
			values.putNull(PEOPLE_LOOKUPURI);
		}
		values.put(PEOPLE_NAME, person.getName());
		db.update(PEOPLE_TABLENAME, values, PEOPLE_ID+"=?", new String[]{Integer.toString(person.getPersonID())});
		db.close();
	}

	public void updateItem(Item item){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ITEMS_NAME, item.getName());
        values.put(ITEMS_TYPE, item.getType());
        if(item.isCurrentlyOnLoan()){
            values.put(ITEMS_CURRENTLYONLOAN, 1);
        } else {
            values.put(ITEMS_CURRENTLYONLOAN, 0);
        }
        values.put(ITEMS_TIMESLOANED, item.getTimesLoaned());
        db.update(ITEMS_TABLENAME, values, ITEMS_ID+"=?", new String[]{Integer.toString(item.getItemID())});
        db.close();
    }

    public void updateItemType(ItemType itemType){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TYPE_NAME, itemType.getName());
        values.put(TYPE_ICON, itemType.getIconId());
        db.update(TYPE_TABLENAME, values, TYPE_ID+"=?", new String[]{Integer.toString(itemType.getItemTypeID())});
        db.close();
    }

	public void updateLoan(Loan loan){
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		if(loan.isNotifying()){
			values.put(LOAN_NOTIFY, 1);
		} else {
			values.put(LOAN_NOTIFY, 0);
		}
		values.put(LOAN_STARTDATE, loan.getStartDate().toString());
		if(loan.getReturnDate()==null){
			values.putNull(LOAN_RETURNDATE);
		} else {
			values.put(LOAN_RETURNDATE, loan.getReturnDate().toString());
		}
		values.put(LOAN_ITEMID, loan.getItemID());
		values.put(LOAN_PERSONID, loan.getPersonID());
		db.update(LOAN_TABLENAME, values, LOAN_ID+"=?", new String[]{Integer.toString(loan.getLoanID())});
		db.close();
	}

	/**
	 * Removes Person from Loaned - includes all previous and current loans for this person too
	 * @param person
	 * @throws ParseException
	 */
	public void removePerson(Person person) throws ParseException{
		SQLiteDatabase db = getWritableDatabase();
		db.delete(PEOPLE_TABLENAME, PEOPLE_ID+"=?", new String[]{Integer.toString(person.getPersonID())});
		db.close();
		// To remove this person from the database, remove all their loans too
		ArrayList<Loan> loans = getAllLoansByPersonID(person.getPersonID());
		for(int i=0;i<=loans.size()-1;i++){
			// Set this item as returned if it's still out
			if(loans.get(i).getReturnDate()==null){
				Item item = getItemByID(loans.get(i).getItemID());
				item.setCurrentlyOnLoan(false);
				updateItem(item);
			}
			// Delete loan
			removeLoan(loans.get(i), false);
		}
	}

	public void removeLoan(Loan loan, boolean setReturned){
		if(setReturned){
			Person p = getPersonByID(loan.getPersonID());
			p.setItemsOnLoan(p.getItemsOnLoan()-1);
			updatePerson(p);
			Item i = getItemByID(loan.getItemID());
			i.setCurrentlyOnLoan(false);
			updateItem(i);
		}
		SQLiteDatabase db = getWritableDatabase();
		db.delete(LOAN_TABLENAME, LOAN_ID+"=?", new String[]{Integer.toString(loan.getLoanID())});
		db.close();
	}

	public void removeItem(Item item) throws ParseException{
		SQLiteDatabase db = getWritableDatabase();
		db.delete(ITEMS_TABLENAME, ITEMS_ID+"=?", new String[]{Integer.toString(item.getItemID())});
		db.close();
		ArrayList<Loan> loans = getAllLoansByItemID(item.getItemID());
		for(int i=0;i<=loans.size()-1;i++){
			// Set this item as returned if it's still out
			if(loans.get(i).getReturnDate()==null){
				Person p = getPersonByID(loans.get(i).getPersonID());
				p.setItemsOnLoan(p.getItemsOnLoan()-1);
				updatePerson(p);
			}
			// Delete loan
			removeLoan(loans.get(i), false);
		}
	}

    public void removeItemType(ItemType itemType) throws ParseException{
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TYPE_TABLENAME, ITEMS_ID+"=?", new String[]{Integer.toString(itemType.getItemTypeID())});
        db.close();
    }

	public void addPerson(String name, Uri lookup, int itemsOnLoan, int itemsLoaned){
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(PEOPLE_ITEMSLOANED, itemsLoaned);
		values.put(PEOPLE_ITEMSONLOAN, itemsOnLoan);
		if(lookup!=null){
			values.put(PEOPLE_LOOKUPURI, lookup.toString());
		} else {
			values.putNull(PEOPLE_LOOKUPURI);
		}
		values.put(PEOPLE_NAME, name);
		db.insert(PEOPLE_TABLENAME, null, values);
		db.close();
	}

	public void addItem(String name, int type){
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(ITEMS_NAME, name);
		values.put(ITEMS_TYPE, type);
		values.put(ITEMS_CURRENTLYONLOAN, 0);
		values.put(ITEMS_TIMESLOANED, 0);
		db.insert(ITEMS_TABLENAME, null, values);
		db.close();
	}

    public void addItemType(String name, int icon){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TYPE_NAME, name);
        values.put(TYPE_ICON, icon);
        db.insert(TYPE_TABLENAME, null, values);
        db.close();
    }

	public void addLoan(int personID, int itemID, Date startDate, boolean notify){
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		if(notify){
			values.put(LOAN_NOTIFY, 1);
		} else {
			values.put(LOAN_NOTIFY, 0);
		}
		values.put(LOAN_STARTDATE, startDate.toString());
		values.putNull(LOAN_RETURNDATE);
		values.put(LOAN_ITEMID, itemID);
		values.put(LOAN_PERSONID, personID);
		db.insert(LOAN_TABLENAME, null, values);
		db.close();
	}

	/**
	 * Searches through all existing loans for an item to see if any are outstanding.
	 * NOTE: Possibly resource-intensive task, depending on the amount of loans for an item. Worker thread advised
	 * @param itemID
	 * @return
	 * @throws ParseException
	 */
	public boolean isAlreadyOnLoan(int itemID) throws ParseException{
		ArrayList<Loan> loans = getAllLoansByItemID(itemID);
		for(int i=0;i<=loans.size()-1;i++){
			if(loans.get(i).getReturnDate()==null){
				return true;
			}
		} return false;
	}

	/**
	 * Convience method for when an item is returned.
	 * NOTE: Possibly resource-itensive call, use worker thread
	 * @param loan The loan representing that of the returned item
	 */
	public void itemReturned(Loan loan){
		Person p = getPersonByID(loan.getPersonID());
		p.setItemsOnLoan(p.getItemsOnLoan()-1);
		updatePerson(p);
		Item i = getItemByID(loan.getItemID());
		i.setCurrentlyOnLoan(false);
		updateItem(i);
		loan.setReturnDate(new Date());
		updateLoan(loan);
	}

	public Loan getMostRecentLoanByBothIDs(int personID, int itemID) throws ParseException{
		SQLiteDatabase db = getReadableDatabase();
		Cursor c = db.query(LOAN_TABLENAME, null, LOAN_ITEMID+"=? AND "+LOAN_PERSONID+"=?", 
				new String[]{Integer.toString(itemID),Integer.toString(personID)}, null, null, null);
		if(c.moveToLast()){
			boolean notify = false;
			if(c.getInt(5)==1){
				notify = true;
			}
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
			Date returnDate = null;
			if(c.getString(4)!=null){
				returnDate = sdf.parse(c.getString(4));
			}
			return new Loan(c.getInt(0), c.getInt(2), c.getInt(1), notify,
					sdf.parse(c.getString(3)), returnDate);
		} else {
			db.close();
			return null;
		}
	}
}