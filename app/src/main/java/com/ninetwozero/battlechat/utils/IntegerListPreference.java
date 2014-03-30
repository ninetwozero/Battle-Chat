package com.ninetwozero.battlechat.utils;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;

import com.ninetwozero.battlechat.R;

public class IntegerListPreference extends DialogPreference {
    private static final String TAG = "[IntegerListPreference]";

    private CharSequence[] entries;
    private int[] values;
    private int value;
    private int clickedDialogEntryIndex;

    public IntegerListPreference(final Context context, final AttributeSet attrs) {
        super(context, attrs);

        final TypedArray attributes = context.obtainStyledAttributes(
            attrs, R.styleable.IntegerListPreference, 0, 0
        );

        if (attributes == null) {
            throw new IllegalStateException(
                TAG + " Unable to get R.styleable.IntegerListPreference"
            );
        }

        entries = attributes.getTextArray(R.styleable.IntegerListPreference_entryList);
        if (entries == null) {
            throw new IllegalArgumentException(TAG + " entryList is not specified");
        }

        int valuesResId = attributes.getResourceId(R.styleable.IntegerListPreference_valueList, 0);
        if (valuesResId == 0) {
            throw new IllegalArgumentException(TAG + " valueList is not specified");
        }

        values = attributes.getResources().getIntArray(valuesResId);
        attributes.recycle();
    }

    public void setEntries(CharSequence[] entries) {
        this.entries = entries;
    }

    public CharSequence[] getEntries() {
        return entries;
    }

    public void setEntries(int entriesResId) {
        setEntries(getContext().getResources().getTextArray(entriesResId));
    }

    public void setEntryValues(final int[] entryValues) {
        values = entryValues;
    }

    public int[] getEntryValues() {
        return values;
    }

    public void setEntryValues(final int entryValuesResId) {
        setEntryValues(getContext().getResources().getIntArray(entryValuesResId));
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
        persistInt(value);
    }

    public CharSequence getEntry() {
        final int index = getValueIndex();
        return index >= 0 && entries != null ? entries[index] : null;
    }

    public int findIndexOfValue(final int value) {
        for (int i = values.length - 1; i >= 0; i--) {
            if (values[i] == value) {
                return i;
            }
        }
        return -1;
    }

    private int getValueIndex() {
        return findIndexOfValue(value);
    }

    public void setValueIndex(int index) {
        if (values != null) {
            setValue(values[index]);
        }
    }

    @Override
    protected void onPrepareDialogBuilder(final Builder builder) {
        super.onPrepareDialogBuilder(builder);

        if (entries == null || values == null) {
            throw new IllegalStateException(
                "IntegerListPreference requires an entryList and a valueList.");
        }

        clickedDialogEntryIndex = getValueIndex();
        builder.setSingleChoiceItems(
            entries,
            clickedDialogEntryIndex,
            new DialogInterface.OnClickListener() {
                public void onClick(final DialogInterface dialog, final int which) {
                    clickedDialogEntryIndex = which;
                }
            }
        );
    }

    @Override
    protected void onDialogClosed(final boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult && clickedDialogEntryIndex >= 0 && values != null) {
            int value = values[clickedDialogEntryIndex];
            if (callChangeListener(value)) {
                setValue(value);
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(final TypedArray typedArray, final int index) {
        return typedArray.getInt(index, 0);
    }

    @Override
    protected void onSetInitialValue(final boolean restoreValue, final Object defaultValue) {
        setValue(restoreValue ? getPersistedInt(value) : (Integer) defaultValue);
    }
}
