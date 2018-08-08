package com.myplacelocator.ActivitiesAndFragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.myplacelocator.Functions.Constants;
import com.myplacelocator.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragment {

    public SettingsFragment() {
        // Required empty public constructor
    }


    /**
     * Settings definition
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        getActivity().setTitle("Settings");

        ListPreference unitPreference = (ListPreference)findPreference("unitLP");
        String[] allUnits = new String[]{"Kilometers","Miles"};
        String[] allValues = new String[]{"Km","Mi"};

        unitPreference.setEntries(allUnits);
        unitPreference.setEntryValues(allValues);
        unitPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Toast.makeText(getActivity(), "Distance unit change to "+newValue, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String unit = preferences.getString(Constants.SHARED_PREFERENCES_UNIT,"Km");
        if (!(unit.equals("Km")) || (unit.equals("Mi")))
            preferences.edit().putString(Constants.SHARED_PREFERENCES_UNIT,"Km").commit();
    }
}
