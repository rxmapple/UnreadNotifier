package com.rxmapple.notifier;

import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;

import java.util.List;

/**
 * Created by rxmapple on 2016/10/3.
 */
public class DefaultPhonePreference extends AppListPreference {
    private static final String TAG = "DefaultPhonePreference";

    public DefaultPhonePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadDialerApps();
    }

    private void loadDialerApps() {
        new AsyncTask<Void, Void, CharSequence[]>() {
            @Override
            protected CharSequence[] doInBackground(Void... unused) {
                Log.d(TAG, "loadDialerApps start");
                List<String> dialerPackages =
                        Utils.getInstalledDialerApplications(getContext());
                final String[] dealers = new String[dialerPackages.size()];
                for (int i = 0; i < dialerPackages.size(); i++) {
                    dealers[i] = dialerPackages.get(i);
                }
                return dealers;
            }

            @Override
            protected void onPostExecute(final CharSequence[] packageNames) {
                setPackageNames(packageNames, null);
                Log.d(TAG, "loadDialerApps end");
            }
        }.execute();
    }

    private String getDefaultPackage() {
        return Utils.getDefaultDialerApplication(getContext());
    }

}
