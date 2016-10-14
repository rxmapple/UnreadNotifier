package com.rxmapple.notifier;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rxmapple on 2016/10/3.
 */
public class AppListPreference extends ListPreference {
    public static final String ITEM_NONE_VALUE = "";
    private Drawable[] mEntryDrawables;
    private boolean mShowItemNone = false;
    public class AppArrayAdapter extends ArrayAdapter<CharSequence> {
        private Drawable[] mImageDrawables = null;
        private int mSelectedIndex = 0;
        public AppArrayAdapter(Context context, int textViewResourceId,
                               CharSequence[] objects, Drawable[] imageDrawables, int selectedIndex) {
            super(context, textViewResourceId, objects);
            mSelectedIndex = selectedIndex;
            mImageDrawables = imageDrawables;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = ((Activity)getContext()).getLayoutInflater();
            @SuppressLint("ViewHolder") View view = inflater.inflate(R.layout.app_preference_item,
                    parent, false);
            TextView textView = (TextView) view.findViewById(R.id.app_label);
            textView.setText(getItem(position));
            if (position == mSelectedIndex) {
                view.findViewById(R.id.select_label).setVisibility(View.VISIBLE);
            }
            ImageView imageView = (ImageView)view.findViewById(R.id.app_image);
            imageView.setImageDrawable(mImageDrawables[position]);
            return view;
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AppListPreference(Context context, AttributeSet attrs,
                             int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public AppListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setShowItemNone(boolean showItemNone) {
        mShowItemNone = showItemNone;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setPackageNames(CharSequence[] packageNames, CharSequence defaultPackageName) {
        // Look up all package names in PackageManager. Skip ones we can't find.
        PackageManager pm = getContext().getPackageManager();
        final int entryCount = packageNames.length + (mShowItemNone ? 1 : 0);
        List<CharSequence> applicationNames = new ArrayList<>(entryCount);
        List<CharSequence> validatedPackageNames = new ArrayList<>(entryCount);
        List<Drawable> entryDrawables = new ArrayList<>(entryCount);
        int selectedIndex = -1;
        for (int i = 0; i < packageNames.length; i++) {
            try {
                ApplicationInfo appInfo = pm.getApplicationInfo(packageNames[i].toString(), 0);
                applicationNames.add(appInfo.loadLabel(pm));
                validatedPackageNames.add(appInfo.packageName);
                entryDrawables.add(appInfo.loadIcon(pm));
                if (defaultPackageName != null &&
                        appInfo.packageName.contentEquals(defaultPackageName)) {
                    selectedIndex = i;
                }
            } catch (PackageManager.NameNotFoundException e) {
                // Skip unknown packages.
            }
        }
        if (mShowItemNone) {
            applicationNames.add(
                    getContext().getResources().getText(R.string.app_list_preference_none));
            validatedPackageNames.add(ITEM_NONE_VALUE);
            entryDrawables.add(getContext().getDrawable(R.drawable.ic_remove_circle));
        }
        setEntries(applicationNames.toArray(new CharSequence[applicationNames.size()]));
        setEntryValues(
                validatedPackageNames.toArray(new CharSequence[validatedPackageNames.size()]));
        mEntryDrawables = entryDrawables.toArray(new Drawable[entryDrawables.size()]);
        if (selectedIndex != -1) {
            setValueIndex(selectedIndex);
        } else {
            setValue(null);
        }
    }
    protected ListAdapter createListAdapter() {
        final String selectedValue = getValue();
        final boolean selectedNone = selectedValue == null ||
                (mShowItemNone && selectedValue.contentEquals(ITEM_NONE_VALUE));
        int selectedIndex = selectedNone ? -1 : findIndexOfValue(selectedValue);
        return new AppArrayAdapter(getContext(),
                R.layout.app_preference_item, getEntries(), mEntryDrawables, selectedIndex);
    }
    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        builder.setAdapter(createListAdapter(), this);
        super.onPrepareDialogBuilder(builder);
    }
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        return new SavedState(getEntryValues(), getValue(), mShowItemNone, superState);
    }
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof SavedState) {
            SavedState savedState = (SavedState) state;
            mShowItemNone = savedState.showItemNone;
            setPackageNames(savedState.entryValues, savedState.value);
            super.onRestoreInstanceState(savedState.superState);
        } else {
            super.onRestoreInstanceState(state);
        }
    }
    private static class SavedState implements Parcelable {
        public final CharSequence[] entryValues;
        public final CharSequence value;
        public final boolean showItemNone;
        public final Parcelable superState;
        public SavedState(CharSequence[] entryValues, CharSequence value, boolean showItemNone,
                          Parcelable superState) {
            this.entryValues = entryValues;
            this.value = value;
            this.showItemNone = showItemNone;
            this.superState = superState;
        }
        @Override
        public int describeContents() {
            return 0;
        }
        @Override
        public void writeToParcel(Parcel dest, int flags) {
            int N = entryValues.length;
            String[] stringArray = new String[N];
            for (int i = 0; i < N; i++) {
                stringArray[i] = entryValues[i].toString();
            }
            dest.writeInt(N);
            dest.writeStringArray(stringArray);
            dest.writeString(value.toString());
            dest.writeInt(showItemNone ? 1 : 0);
            dest.writeParcelable(superState, flags);
        }
        public static Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel source) {
                int N = source.readInt();
                String[] stringArray = new String[N];
                source.readStringArray(stringArray);

                CharSequence[] entryValues = new CharSequence[N];
                System.arraycopy(stringArray, 0, entryValues, 0, N);
                CharSequence value = source.readString();
                boolean showItemNone = source.readInt() != 0;
                Parcelable superState = source.readParcelable(getClass().getClassLoader());
                return new SavedState(entryValues, value, showItemNone, superState);
            }
            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}