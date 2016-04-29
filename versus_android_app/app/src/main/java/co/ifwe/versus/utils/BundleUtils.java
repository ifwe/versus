/*
 * Author: Vladimir Baryshnikov vovkab@gmail.com
 * Date:   Nov 13, 2014
 */

package co.ifwe.versus.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import java.io.Serializable;
import java.util.ArrayList;

public final class BundleUtils {

    private BundleUtils() {
    }

    public static int getInt(Bundle bundle, String name) {
        return getInt(bundle, name, 0);
    }

    public static int getInt(@Nullable Bundle bundle, String name, int defaultValue) {
        return bundle == null ? defaultValue : bundle.getInt(name, defaultValue);
    }

    public static long getLong(@Nullable Bundle bundle, String name) {
        return getLong(bundle, name, 0);
    }

    public static long getLong(@Nullable Bundle bundle, String name, long defaultValue) {
        return bundle == null ? defaultValue : bundle.getLong(name, defaultValue);
    }

    @Nullable
    public static long getLong(Intent intent, String name) {
        return getLong(intent, name, 0);
    }

    @Nullable
    public static long getLong(Intent intent, String name, long defaultValue) {
        return getLong(intent != null ? intent.getExtras() : null, name, defaultValue);
    }

    public static double getDouble(@Nullable Bundle bundle, String name) {
        return getDouble(bundle, name, 0d);
    }

    public static double getDouble(@Nullable Bundle bundle, String name, double defaultValue) {
        return bundle == null ? defaultValue : bundle.getDouble(name, defaultValue);
    }

    public static boolean getBoolean(Bundle bundle, String name) {
        return getBoolean(bundle, name, false);
    }

    public static boolean getBoolean(Bundle bundle, String name, boolean defaultValue) {
        return bundle == null ? defaultValue : bundle.getBoolean(name, defaultValue);
    }

    @Nullable
    public static String getString(Bundle bundle, String name) {
        return getString(bundle, name, null);
    }

    @Nullable
    public static String getString(Bundle bundle, String name, String defaultValue) {
        return bundle == null ? defaultValue : bundle.getString(name, defaultValue);
    }

    @Nullable
    public static String getString(Activity activity, String name) {
        return getString(activity, name, null);
    }

    @Nullable
    public static String getString(Activity activity, String name, String defaultValue) {
        return getString(activity.getIntent(), name, defaultValue);
    }

    @Nullable
    public static String getString(Intent intent, String name) {
        return getString(intent, name, null);
    }

    @Nullable
    public static String getString(Intent intent, String name, String defaultValue) {
        return getString(intent != null ? intent.getExtras() : null, name, defaultValue);
    }

    @Nullable
    public static String getString(Fragment fragment, String name) {
        return getString(fragment, name, null);
    }

    @Nullable
    public static String getString(Fragment fragment, String name, String defaultValue) {
        return getString(fragment != null ? fragment.getArguments() : null, name, defaultValue);
    }

    @Nullable
    public static Bundle getBundle(Bundle bundle, String name) {
        return bundle == null ? null : bundle.getBundle(name);
    }

    @Nullable
    public static Bundle getBundle(Bundle bundle, String name, Bundle defaultValue) {
        final Bundle value = getBundle(bundle, name);
        return value == null ? defaultValue : value;
    }

    @Nullable
    public static <T extends Parcelable> T getParcelable(Activity activity, String name, T defaultValue) {
        return getParcelable(activity.getIntent().getExtras(), name, defaultValue);
    }

    @Nullable
    public static <T extends Parcelable> T getParcelable(Bundle bundle, String name) {
        return getParcelable(bundle, name, null);
    }

    @Nullable
    public static <T extends Parcelable> T getParcelable(Bundle bundle, String name, T defaultValue) {
        T parcelable = bundle == null ? null : (T) bundle.getParcelable(name);
        return parcelable == null ? defaultValue : parcelable;
    }

    @Nullable
    public static Parcelable[] getParcelableArray(Bundle bundle, String name) {
        return bundle == null ? null : bundle.getParcelableArray(name);
    }

    @Nullable
    public static <T extends Parcelable> ArrayList<T> getParcelableArrayList(Bundle bundle, String name) {
        return getParcelableArrayList(bundle, name, null);
    }

    @Nullable
    public static <T extends Parcelable> ArrayList<T> getParcelableArrayList(Bundle bundle, String name, ArrayList<T> defaultValue) {
        if (bundle == null) {
            return defaultValue;
        } else {
            final ArrayList<T> list = bundle.getParcelableArrayList(name);
            return list == null ? defaultValue : list;
        }
    }

    @Nullable
    public static <T extends Serializable> T getSerializable(Bundle bundle, String name) {
        return bundle == null ? null : (T) bundle.getSerializable(name);
    }

    @Nullable
    public static <T extends Serializable> T getSerializable(Bundle bundle, String name, T defaultValue) {
        T serializable = getSerializable(bundle, name);
        return serializable == null ? defaultValue : serializable;
    }

    public static Bundle merge(Bundle bundle1, Bundle bundle2) {
        Bundle bundle = new Bundle();
        if (bundle1 != null) bundle.putAll(bundle1);
        if (bundle2 != null) bundle.putAll(bundle2);
        return bundle;
    }

    public static String dump(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (String key : bundle.keySet()) {
            Object value = bundle.get(key);
            sb.append(String.format("%s %s (%s)", key,
                value.toString(), value.getClass().getName()));
        }
        sb.append("]");
        return sb.toString();
    }
}
