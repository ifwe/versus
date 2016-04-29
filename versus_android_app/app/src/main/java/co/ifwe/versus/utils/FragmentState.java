package co.ifwe.versus.utils;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

public final class FragmentState {
    public static final String ARG_FRAGMENT_NAME = "args_fragment_name";
    public static final String ARG_FRAGMENT_ARGS = "args_fragment_args";

    private FragmentState() { }

    @Nullable
    public static Bundle create(Fragment fragment) {
        if (fragment == null) return null;
        FragmentManager fm = fragment.getFragmentManager();
        Fragment.SavedState savedState = fm.saveFragmentInstanceState(fragment);
        return create(fragment.getClass(), fragment.getArguments(), savedState);
    }

    public static Bundle create(Class fragmentClass, Bundle args) {
        return create(fragmentClass, args, null);
    }

    public static Bundle create(Class fragmentClass, Bundle args, Fragment.SavedState savedState) {
        Bundle state = new Bundle();
        putFragmentName(state, fragmentClass);
        putFragmentArgs(state, args);
        return state;
    }

    public static String getFragmentName(Bundle state) {
        return BundleUtils.getString(state, ARG_FRAGMENT_NAME);
    }

    public static Bundle getFragmentArgs(Bundle state) {
        return BundleUtils.getBundle(state, ARG_FRAGMENT_ARGS);
    }

    public static void putFragmentName(Bundle state, Class fragmentClass) {
        state.putString(ARG_FRAGMENT_NAME, fragmentClass.getName());
    }

    public static void putFragmentArgs(Bundle state, Bundle args) {
        state.putBundle(ARG_FRAGMENT_ARGS, args);
    }

    public static void addToFragmentArgs(Bundle state, Bundle args) {
        Bundle newArgs = BundleUtils.merge(getFragmentArgs(state), args);
        putFragmentArgs(state, newArgs);
    }

    public static <T extends Fragment> T createFragment(Context context, Bundle state) {
        return (T) Fragment.instantiate(context, getFragmentName(state), getFragmentArgs(state));
    }

}
