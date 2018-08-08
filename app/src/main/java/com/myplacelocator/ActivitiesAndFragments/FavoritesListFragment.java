package com.myplacelocator.ActivitiesAndFragments;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
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
import android.widget.Toast;

import com.myplacelocator.DataBase.PlacesContract;
import com.myplacelocator.FragmentChanger;
import com.myplacelocator.Functions.Constants;
import com.myplacelocator.Functions.MyAlertDialog;
import com.myplacelocator.Functions.MyPlace;
import com.myplacelocator.R;
import com.myplacelocator.DataBase.MySqlHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoritesListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private SimpleCursorAdapter adapter;
    FragmentChanger fragmentChanger;
    private ListView listview;

    public FavoritesListFragment() {
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

        //Set title of the activity
        getActivity().setTitle(getResources().getText(R.string.favorite_title));

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorites_list, container, false);

        fragmentChanger= (FragmentChanger) getActivity();

        adapter = new SimpleCursorAdapter(getActivity(),android.R.layout.simple_list_item_1,null,new String[]{Constants.NAME_PLACE},new int[]{android.R.id.text1});
        listview = (ListView)view.findViewById(R.id.favoritesLV);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Return place to the favorite table at this position
                MyPlace place=new MySqlHelper(getActivity()).getPlaceToFavory(adapter,position);
                fragmentChanger.changeFragmentsMap(place, true, true);
            }
        });

        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                //Delete place from favorite
                MyAlertDialog.createAlertDialog(getActivity(),getString(R.string.delete_alert_dialog),R.drawable.alert_icon,getString(R.string.favorite_delete_message),getString(R.string.alert_dialog_positive),getString(R.string.alert_dialog_negative), new MyAlertDialog.AlertDialogListener() {
                    @Override
                    public void onPositive(DialogInterface dialogInterface) {
                        getActivity().getContentResolver().delete(PlacesContract.FavoritesPlaces.CONTENT_URI,"_id=?", new String[]{"" + adapter.getCursor().getInt(adapter.getCursor().getColumnIndex(Constants.ID))});
                    }

                    @Override
                    public void onNegative(DialogInterface dialogInterface) {
                        Toast.makeText(getActivity(), getResources().getText(R.string.cancel_delete_favorite), Toast.LENGTH_SHORT).show();
                    }
                });

                return true;
            }
        });



        Bundle bundle = new Bundle();
        getLoaderManager().initLoader(Constants.FAVORITES_LOADER_ID,bundle,this);

        return view;

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == Constants.FAVORITES_LOADER_ID){
            CursorLoader cursorLoader = new CursorLoader(getActivity().getApplicationContext(), PlacesContract.FavoritesPlaces.CONTENT_URI,null,null,null,Constants.ID+" DESC");
            return cursorLoader;
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        cursor.setNotificationUri(getActivity().getContentResolver(),PlacesContract.FavoritesPlaces.CONTENT_URI);
        adapter.swapCursor(cursor);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
