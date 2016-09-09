package it.uniroma1.android.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import it.uniroma1.android.R;

/**
 * Created by nduccio on 21/01/15.
 */
public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
