package com.mattallen.loaned.addloan;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.mattallen.loaned.R;
import com.mattallen.loaned.storage.DatabaseManager;

/**
 * Created by Tara on 12/12/2014.
 */
public class AddNewItemFragment extends ListFragment  implements LoaderManager.LoaderCallbacks<Cursor>{

    private Activity mActivity;
    private Context mContext;
    private ItemTypeAdapter itemTypeAdapter;
    private DatabaseManager mDB;

    /**
     * Called to have the fragment instantiate its user interface view, can be
     * null for non-graphical fragments Called after onCreate(Bundle) and
     * beforeonActivityCreated(Bundle).
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_item, container, false);
        return view;
    }

    /**
     * Called when the fragment's activity has been created and this fragment's
     * view hierarchy instantiated. Called after onCreateView(Bundle) and before
     * onViewStateRestored(Bundle).
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mActivity = this.getActivity();
        mContext = this.getActivity().getBaseContext();
         mDB = new DatabaseManager(mActivity);

        itemTypeAdapter = new ItemTypeAdapter(mContext,null,true);
        getListView().setAdapter(itemTypeAdapter);

        getLoaderManager().initLoader(0,null,this);
    }

    /**
     * This makes sure that the container activity has implemented the callback
     * interface. If not, it throws an exception
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        if (activity instanceof OnClickListener) {
//            mCallback = (OnClickListener) activity;
//        } else {
//            throw new ClassCastException(activity.toString()
//                    + " must implemenet OnClickListener");
//        }
    }

    @Override
    public void onListItemClick(ListView listview, View view, int position, long id) {

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new ItemTypeLoader(mContext,mDB);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
/**
 * Check if cursor is not null
 * then check if cursor is not closed
 * then cursor.moveToFirst() returns false if cursor is empty
 */

        if(data!=null && !data.isClosed() && data.moveToFirst()){
            itemTypeAdapter.changeCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
