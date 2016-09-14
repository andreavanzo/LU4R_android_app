package it.uniroma1.android.activities;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import it.uniroma1.android.fragments.SettingsFragment;

/**
 * Created by nduccio on 21/01/15.
 */
public class SettingsActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the activities content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
