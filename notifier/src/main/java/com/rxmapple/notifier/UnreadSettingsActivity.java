package com.rxmapple.notifier;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class UnreadSettingsActivity extends AppCompatActivity {

    private static final String TAG = "UnreadSettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );

        // Display the fragment as the main content.
        Log.d(TAG, "activity oncreat");
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new UnreadSettingsFragment())
                .commit();
        Log.d(TAG, "activity oncreat end");
    }
}
