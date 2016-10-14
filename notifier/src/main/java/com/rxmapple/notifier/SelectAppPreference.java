package com.rxmapple.notifier;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rxmapple on 2016/10/3.
 */
public class SelectAppPreference extends AppListPreference {

    private static final String TAG = "SelectAppPreference";
    private static final boolean DEBUG = false;
    final private PackageManager mPm;
    private Context mContext;

    public SelectAppPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mPm = context.getPackageManager();
        refreshSelectApps();
    }

    public void refreshSelectApps() {
        new AsyncTask<Void, Void, CharSequence[]>() {
            @Override
            protected CharSequence[] doInBackground(Void... unused) {
                Log.d(TAG, "refreshSelectApps start");
                List<String> apps = resolveLauncherApps();
                return apps.toArray(new String[apps.size()]);
            }

            @Override
            protected void onPostExecute(final CharSequence[] packageNames) {
                setPackageNames(packageNames, null);
                Log.d(TAG, "refreshSelectApps end");
            }
        }.execute();
    }

    private static List<ResolveInfo> getLauncherActivities(PackageManager pm) {
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        return pm.queryIntentActivities(mainIntent, 0);
    }

    private List<String> resolveLauncherApps() {
        List<String> result = new ArrayList<>();

        // Resolve that intent and check that the handleAllWebDataURI boolean is set
        List<ResolveInfo> list = getLauncherActivities(mPm);

        final int count = list.size();
        for (int i=0; i<count; i++) {
            ResolveInfo info = list.get(i);
            ActivityInfo aInfo = info.activityInfo;
            if (aInfo == null) {
                continue;
            }

            if (result.contains(aInfo.packageName)) {
                continue;
            }
            if (DEBUG) {
                Log.d(TAG, "app:" + aInfo.packageName + ">> is added!");
            }
            result.add(aInfo.packageName);
        }

        Log.d(TAG, "resolveLauncherApps end,size: " + result.size());

        return result;
    }

}
