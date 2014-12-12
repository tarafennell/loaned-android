package com.mattallen.loaned.addloan;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mattallen.loaned.R;
import com.mattallen.loaned.storage.DatabaseManager;

/**
 * Created by Tara on 12/12/2014.
 */
public class ItemTypeAdapter extends CursorAdapter {

    private final LayoutInflater mInflater;

    private int layout = R.layout.list_item_type;

    public ItemTypeAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        final View view= mInflater.inflate(layout,parent,false);
        return view;

    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String name =cursor.getString(cursor.getColumnIndex(DatabaseManager.TYPE_NAME));
        int icon = cursor.getInt(cursor.getColumnIndex(DatabaseManager.TYPE_ICON));

        ImageView iconImageView = (ImageView) view
                .findViewById(R.id.type_icon);
        iconImageView.setBackgroundResource(icon);

        TextView nameTextView = (TextView) view
                .findViewById(R.id.type_name);
        nameTextView.setText(name);
    }
}
