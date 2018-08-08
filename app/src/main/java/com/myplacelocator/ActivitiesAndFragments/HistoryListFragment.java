package com.myplacelocator.ActivitiesAndFragments;


import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.myplacelocator.DataBase.MySqlHelper;
import com.myplacelocator.DataBase.PlacesContract;
import com.myplacelocator.FragmentChanger;
import com.myplacelocator.Functions.Constants;
import com.myplacelocator.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private SimpleCursorAdapter adapter;
    FragmentChanger fragmentChanger;
    private ListView listview;

    public HistoryListFragment() {
        // Required empty public constructor
    }


    /**
     * Create the fragment view
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Set activity's title
        getActivity().setTitle(getResources().getText(R.string.history_title));

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history_list, container, false);

        fragmentChanger= (FragmentChanger) getActivity();

        adapter = new SimpleCursorAdapter(getActivity(),android.R.layout.simple_list_item_1,null,new String[]{Constants.SEARCH},new int[]{android.R.id.text1});
        listview = (ListView)view.findViewById(R.id.historyLV);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Return search from history table
                new MySqlHelper(getActivity()).searchFromHistory(adapter,position,fragmentChanger,getActivity());
            }
        });

        Bundle bundle = new Bundle();
        getLoaderManager().initLoader(Constants.HISTORY_LOADER_ID,bundle,this);

        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == Constants.HISTORY_LOADER_ID){
            CursorLoader cursorLoader = new CursorLoader(getActivity().getApplicationContext(), PlacesContract.historySearch.CONTENT_URI,null,null,null,Constants.ID+" DESC");
            return cursorLoader;
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        cursor.setNotificationUri(getActivity().getContentResolver(),PlacesContract.LastSearchResults.CONTENT_URI);
        adapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
