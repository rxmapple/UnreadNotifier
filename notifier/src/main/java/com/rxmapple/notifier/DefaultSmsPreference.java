package com.rxmapple.notifier;

import android.content.Context;
import android.os.AsyncTask;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;

import java.util.Collection;

/**
 * Created by rxmapple on 2016/10/3.
 */
public class DefaultSmsPreference extends AppListPreference {
    private static final String TAG = "DefaultSmsPreference";
    private final Context mContext;
    public DefaultSmsPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        loadSmsApps();
    }

    private void loadSmsApps() {
        new AsyncTask<Void, Void, CharSequence[]>() {
            @Override
            protected CharSequence[] doInBackground(Void... unused) {
                Log.d(TAG, "loadSmsApps start");
                Collection<SmsAppUtils.SmsApplicationData> smsApplications =
                        SmsAppUtils.getApplicationCollection(getContext());

                int count = smsApplications.size();
                String[] packageNames = new String[count];
                int i = 0;
                for (SmsAppUtils.SmsApplicationData smsApplicationData : smsApplications) {
                    packageNames[i++] = smsApplicationData.mPackageName;
                }
                return packageNames;
            }

            @Override
            protected void onPostExecute(final CharSequence[] packageNames) {
                setPackageNames(packageNames, null);
                Log.d(TAG, "loadSmsApps end");
            }
        }.execute();
    }

    private String getDefaultPackage() {
        return Settings.Secure.getString(mContext.getContentResolver(),
                Utils.SMS_DEFAULT_APPLICATION);
    }
}
