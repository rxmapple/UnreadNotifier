package com.rxmapple.notifier;

/**
 * Created by rxmapple on 2016/10/3.
 */

import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

/**
 * This fragment shows the launcher preferences.
 */
public class UnreadSettingsFragment extends PreferenceFragment
        implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
    private static final boolean DEBUG = false;
    private static final String TAG = "UnreadSettingsFragment";
    private SharedPreferences mSharedPrefs;
    private PackageManager mPm;

    private static final String PREF_KEY_MISS_CALL = "pref_missed_call_count";
    private static final String PREF_KEY_UNREAD_SMS = "pref_unread_sms_count";
    private static final String PREF_KEY_GMAIL = "pref_unread_gmail_count";
    private static final String PREF_KEY_QQ = "pref_unread_qq_count";
    private static final String PREF_KEY_WECHAT = "pref_unread_wechat_count";
    private static final String PREF_KEY_CUSTOM_1 = "pref_unread_custom1_count";
    private static final String PREF_KEY_CUSTOM_2 = "pref_unread_custom2_count";
    private static final String PREF_KEY_CUSTOM_3 = "pref_unread_custom3_count";

    private DefaultPhonePreference mDefaultPhonePref;
    private DefaultSmsPreference   mDefaultSmsPref;
    private CheckBoxPreference     mGmailPref;
    private SelectAppPreference    mWeChatPref;
    private SelectAppPreference    mQQPref;
    private SelectAppPreference    mCustom1Pref;
    private SelectAppPreference    mCustom2Pref;
    private SelectAppPreference    mCustom3Pref;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.unread_settings_preferences);

        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mPm = getActivity().getPackageManager();

        Log.d(TAG, "initPreferences");
        initPreferences();
        Log.d(TAG, "initPreferences end");

        Log.d(TAG, "loadSettings");
        loadSettings();
        Log.d(TAG, "loadSettings end");
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        final String key = preference.getKey();

        if (DEBUG) {
            Log.d( TAG, "onPreferenceChange, key:" + key);
        }

        if (preference instanceof ListPreference) {
            final String pkgName = (String) newValue;
            if (TextUtils.isEmpty(pkgName)) {
                return false;
            }
            ((ListPreference) preference).setValue(pkgName);
            preference.setSummary(((ListPreference) preference).getEntry());
        }

        return true;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        return false;
    }

    private void initPreferences() {
        mDefaultPhonePref = (DefaultPhonePreference) findPreference(PREF_KEY_MISS_CALL);
        setPreferenceListener(mDefaultPhonePref);

        mDefaultSmsPref = (DefaultSmsPreference) findPreference(PREF_KEY_UNREAD_SMS);
        setPreferenceListener(mDefaultSmsPref);

        mGmailPref = (CheckBoxPreference) findPreference(PREF_KEY_GMAIL);
        setPreferenceListener(mGmailPref);

        mQQPref = (SelectAppPreference) findPreference(PREF_KEY_QQ);
        setPreferenceListener(mQQPref);

        mWeChatPref = (SelectAppPreference) findPreference(PREF_KEY_WECHAT);
        setPreferenceListener(mWeChatPref);

        mCustom1Pref = (SelectAppPreference) findPreference(PREF_KEY_CUSTOM_1);
        setPreferenceListener(mCustom1Pref);

        mCustom2Pref = (SelectAppPreference) findPreference(PREF_KEY_CUSTOM_2);
        setPreferenceListener(mCustom2Pref);

        mCustom3Pref = (SelectAppPreference) findPreference(PREF_KEY_CUSTOM_3);
        setPreferenceListener(mCustom3Pref);
    }

    private void setPreferenceListener(Preference preference) {
        if (preference != null) {
            preference.setOnPreferenceChangeListener(this);
            preference.setOnPreferenceClickListener(this);
        }
    }

    private boolean loadSettings() {
        loadPrefsSetting(mDefaultPhonePref, R.string.pref_missed_call_count_summary);
        loadPrefsSetting(mDefaultSmsPref, R.string.pref_unread_sms_count_summary);
        loadPrefsSetting(mQQPref, R.string.app_list_preference_none);
        loadPrefsSetting(mWeChatPref, R.string.app_list_preference_none);
        loadPrefsSetting(mCustom1Pref, R.string.app_list_preference_none);
        loadPrefsSetting(mCustom2Pref, R.string.app_list_preference_none);
        loadPrefsSetting(mCustom3Pref, R.string.app_list_preference_none);
        return true;
    }

    private void loadPrefsSetting(Preference preference, int failSummaryID) {
        if (mSharedPrefs == null || preference == null) {
            return;
        }
        if (preference instanceof AppListPreference) {
            boolean ret = false;
            ApplicationInfo info = null;
            String pkgName = mSharedPrefs.getString(preference.getKey(), null);
            if (!TextUtils.isEmpty(pkgName)) {
                try {
                    info = mPm.getApplicationInfo(pkgName, 0);
                    ret = info != null;
                } catch (PackageManager.NameNotFoundException e) {
                    Log.w(TAG, "loadPrefsSetting, get app failed, e:" + e);
                }
            }
            preference.setSummary(ret ? info.loadLabel(mPm) : getString(failSummaryID));
        }
    }

}
