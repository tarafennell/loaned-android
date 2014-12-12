package com.mattallen.loaned.addloan;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;

import com.mattallen.loaned.storage.DatabaseManager;

/**
 * Created by Tara on 12/12/2014.
 */
public class ItemTypeLoader extends CursorLoader {

    private final DatabaseManager dbManager;

    public ItemTypeLoader(Context context,DatabaseManager dbManager) {
        super(context);
        this.dbManager = dbManager;
    }

    @Override
    public Cursor loadInBackground() {
        // this is just a simple query, could be anything that gets a cursor
        return dbManager.getItemTypesCursor();
    }
}
