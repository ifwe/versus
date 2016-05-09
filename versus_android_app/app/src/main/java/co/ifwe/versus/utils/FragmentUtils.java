package co.ifwe.versus.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

public final class FragmentUtils {
    private FragmentUtils() {}

    //region Add fragment with container id
    public static void add(Fragment parent, Bundle fragmentState, @IdRes int containerViewResId) {
        add(parent.getActivity(), fragmentState, containerViewResId);
    }

    public static Fragment add(FragmentActivity activity, Bundle fragmentState, @IdRes int containerViewResId) {
        Fragment fragment = FragmentState.createFragment(activity, fragmentState);
        add(activity, fragment, containerViewResId);
        return fragment;
    }

    public static void add(FragmentActivity activity, Fragment fragment, @IdRes int containerViewResId) {
        add(activity.getSupportFragmentManager(), fragment, containerViewResId);
    }

    public static void add(Fragment parent, Fragment fragment, @IdRes int containerViewResId) {
        add(parent.getActivity().getSupportFragmentManager(), fragment, containerViewResId);
    }

    public static void addChild(Fragment parent, Fragment fragment, @IdRes int containerViewResId) {
        add(parent.getChildFragmentManager(), fragment, containerViewResId);
    }

    public static void add(FragmentManager fm, Fragment fragment, @IdRes int containerViewResId) {
        fm.beginTransaction().add(containerViewResId, fragment).commit();
    }

    public static <T extends Fragment> T addSingle(FragmentActivity activity, Bundle fragmentState, @IdRes int containerViewResId) {
        Fragment fragment = findFragment(activity, containerViewResId);
        if (fragment == null) {
            return (T) add(activity, fragmentState, containerViewResId);
        } else {
            return (T) fragment;
        }
    }
    //endregion

    //region Add fragment with tag
    public static void add(Fragment parent, Bundle fragmentState, @NonNull String tag) {
        add(parent.getActivity(), fragmentState, tag);
    }

    public static Fragment add(FragmentActivity activity, Bundle fragmentState, @NonNull String tag) {
        Fragment fragment = FragmentState.createFragment(activity, fragmentState);
        add(activity, fragment, tag);
        return fragment;
    }

    public static void add(FragmentActivity activity, Fragment fragment, @NonNull String tag) {
        add(activity.getSupportFragmentManager(), fragment, tag);
    }

    public static void add(Fragment parent, Fragment fragment, @NonNull String tag) {
        add(parent.getActivity().getSupportFragmentManager(), fragment, tag);
    }

    public static void addChild(Fragment parent, Fragment fragment, @NonNull String tag) {
        add(parent.getChildFragmentManager(), fragment, tag);
    }

    public static void addSingle(Fragment parent, Bundle fragmentState, @NonNull String tag) {
        addSingle(parent.getActivity(), fragmentState, tag);
    }

    public static void addSingle(FragmentActivity activity, Bundle fragmentState, @NonNull String tag) {
        Fragment fragment = findFragment(activity, tag);
        if (fragment == null) {
            add(activity, fragmentState, tag);
        }
    }

    public static void add(FragmentManager fm, Fragment fragment, @NonNull String tag) {
        fm.beginTransaction().add(fragment, tag).commit();
    }
    //endregion

    //region Add Fragment with container id and tag
    public static void add(Fragment parent, Bundle fragmentState, @IdRes int containerViewResId, @NonNull String tag) {
        add(parent.getActivity(), fragmentState, containerViewResId, tag);
    }

    public static Fragment add(FragmentActivity activity, Bundle fragmentState, @IdRes int containerViewResId, @NonNull String tag) {
        Fragment fragment = FragmentState.createFragment(activity, fragmentState);
        add(activity, fragment, containerViewResId, tag);
        return fragment;
    }

    public static void add(FragmentActivity activity, Fragment fragment, @IdRes int containerViewResId, @NonNull String tag) {
        add(activity.getSupportFragmentManager(), fragment, containerViewResId, tag);
    }

    public static void add(Fragment parent, Fragment fragment, @IdRes int containerViewResId, @NonNull String tag) {
        add(parent.getChildFragmentManager(), fragment, containerViewResId, tag);
    }

    public static void addChild(Fragment parent, Fragment fragment, @IdRes int containerViewResId, @NonNull String tag) {
        add(parent.getChildFragmentManager(), fragment, containerViewResId, tag);
    }

    public static void add(FragmentManager fm, Fragment fragment, @IdRes int containerViewResId, @NonNull String tag) {
        fm.beginTransaction().add(containerViewResId, fragment, tag).commit();
    }
    //endregion

    public static void remove(@NonNull FragmentActivity activity, @NonNull String tag) {
        remove(activity.getSupportFragmentManager(), tag);
    }

    public static void remove(@NonNull FragmentActivity activity, Fragment fragment) {
        remove(activity.getSupportFragmentManager(), fragment);
    }

    public static void remove(@NonNull Fragment parentFragment, @NonNull String tag) {
        remove(parentFragment.getChildFragmentManager(), tag);
    }

    public static void remove(@NonNull Fragment parentFragment, Fragment fragment) {
        remove(parentFragment.getChildFragmentManager(), fragment);
    }

    public static void remove(@NonNull FragmentManager fm, @NonNull String tag) {
        Fragment fragment = findFragment(fm, tag);
        remove(fm, fragment);
    }

    public static void remove(@NonNull FragmentManager fm, Fragment fragment) {
        if (fragment == null) return;
        fm.beginTransaction().remove(fragment).commit();
    }

    public static void removeStateless(@NonNull FragmentManager fm, @NonNull String tag) {
        Fragment fragment = findFragment(fm, tag);
        removeStateless(fm, fragment);
    }

    public static void removeStateless(@NonNull FragmentManager fm, Fragment fragment) {
        if (fragment == null) return;
        fm.beginTransaction().remove(fragment).commitAllowingStateLoss();
    }

    public static void removeStateless(@NonNull FragmentActivity activity, Fragment fragment) {
        removeStateless(activity.getSupportFragmentManager(), fragment);
    }

    public static void removeStateless(@NonNull FragmentActivity activity, @NonNull String tag) {
        Fragment fragment = findFragment(activity, tag);
        removeStateless(activity, fragment);
    }

    public static Fragment replace(@NonNull FragmentActivity activity, Bundle fragmentState, @IdRes int containerViewResId) {
        Fragment fragment = FragmentState.createFragment(activity, fragmentState);
        replace(activity, fragment, containerViewResId);
        return fragment;
    }

    public static Fragment replace(@NonNull FragmentActivity activity, Fragment fragment, @IdRes int containerViewResId) {
        activity.getSupportFragmentManager().beginTransaction().replace(containerViewResId, fragment).commit();
        return fragment;
    }

    public static Fragment replaceWithBackstack(@NonNull FragmentActivity activity, Bundle fragmentState, @IdRes int containerViewResId, String tag) {
        FragmentManager fm = activity.getSupportFragmentManager();
        Fragment fragment = FragmentState.createFragment(activity, fragmentState);
        fm.beginTransaction()
            .addToBackStack(tag)
            .replace(containerViewResId, fragment, FragmentState.getFragmentName(fragmentState))
            .commit();
        return fragment;
    }

    public static Fragment replaceWithBackstack(@NonNull FragmentManager fragmentManager, Fragment fragment, @IdRes int containerViewResId, String tag) {
        fragmentManager.beginTransaction()
            .addToBackStack(tag)
            .replace(containerViewResId, fragment)
            .commit();

        return fragment;
    }

    public static Fragment replaceStateless(@NonNull FragmentActivity activity, Bundle fragmentState, @IdRes int containerViewResId) {
        Fragment fragment = FragmentState.createFragment(activity, fragmentState);
        replaceStateless(activity.getSupportFragmentManager(), fragment, containerViewResId, FragmentState.getFragmentName(fragmentState));
        return fragment;
    }

    public static Fragment replaceStateless(@NonNull FragmentManager fragmentManager, Fragment fragment, @IdRes int containerViewResId, String tag) {
        fragmentManager.beginTransaction().replace(containerViewResId, fragment, tag).commitAllowingStateLoss();
        return fragment;
    }

    //region Find fragment by tag
    public static <T extends Fragment> T findFragment(@NonNull Fragment parent, @NonNull String tag) {
        return findFragment(parent.getFragmentManager(), tag);
    }

    public static <T extends Fragment> T findChildFragment(@NonNull Fragment parent, @NonNull String tag) {
        return findFragment(parent.getChildFragmentManager(), tag);
    }

    public static <T extends Fragment> T findFragment(@NonNull FragmentActivity activity, @NonNull String tag) {
        return findFragment(activity.getSupportFragmentManager(), tag);
    }

    public static <T extends Fragment> T findFragment(@NonNull FragmentManager fm, @NonNull String tag) {
        return (T) fm.findFragmentByTag(tag);
    }
    //endregion

    //region Find fragment by id
    public static <T extends Fragment> T findFragment(@NonNull Fragment parent, @IdRes int id) {
        return findFragment(parent.getFragmentManager(), id);
    }

    public static <T extends Fragment> T findChildFragment(@NonNull Fragment parent, @IdRes int id) {
        return findFragment(parent.getChildFragmentManager(), id);
    }

    public static <T extends Fragment> T findFragment(@NonNull FragmentActivity activity, @IdRes int id) {
        return findFragment(activity.getSupportFragmentManager(), id);
    }

    public static <T extends Fragment> T findFragment(@NonNull FragmentManager fm, @IdRes int id) {
        return (T) fm.findFragmentById(id);
    }
    //endregion

    public static void show(FragmentActivity activity, Bundle fragmentState, @IdRes int containerViewResId, String tag) {
        Fragment fragment = findFragment(activity, tag);
        if (fragment == null) {
            add(activity, fragmentState, containerViewResId, tag);
        } else {
            show(fragment);
        }
    }

    public static void show(Fragment fragment) {
        if (fragment != null) {
            FragmentManager fm = fragment.getFragmentManager();
            fm.beginTransaction().show(fragment).commit();
        }
    }

    public static void hideAll(FragmentActivity activity, @IdRes int resId) {
        hideAll(activity.getSupportFragmentManager(), resId);
    }

    public static void hideAll(FragmentManager fm, @IdRes int resId) {
        final List<Fragment> fragments = fm.getFragments();
        if (fragments == null) return;

        FragmentTransaction ft = fm.beginTransaction();
        for (Fragment fragment : fragments) {
            if (fragment != null && fragment.getId() == resId) {
                ft.hide(fragment);
            }
        }
        ft.commit();
    }

    public static void hide(Fragment fragment) {
        if (fragment != null) {
            FragmentManager fm = fragment.getFragmentManager();
            fm.beginTransaction().hide(fragment).commit();
        }
    }

    public static <T> T attachListener(Fragment fragment, @NonNull Class<T> listenerClass) {
        T listener = findListener(fragment, listenerClass);
        if (listener == null) {
            final String caller = fragment.getActivity() + " or " + fragment.getParentFragment();
            throw new ClassCastException(caller + " must implement " + listenerClass.getCanonicalName());
        } else {
            return listener;
        }
    }

    @Nullable
    public static <T> T findListener(Fragment fragment, @NonNull Class<T> listenerClass) {
        final Fragment parent = fragment.getParentFragment();
        if (listenerClass.isInstance(parent)) {
            return (T) parent;
        }

        final Activity activity = fragment.getActivity();
        if (listenerClass.isInstance(activity)) {
            return (T) activity;
        }
        return null;
    }

    @Nullable
    public static String getName(Fragment fragment) {
        return fragment != null ? fragment.getClass().getName() : null;
    }

    public static void onActivityResult(FragmentManager fragmentManager, int requestCode, int resultCode, Intent data) {
        if (fragmentManager != null) {
            final List<Fragment> fragments = fragmentManager.getFragments();

            if (fragments == null || fragments.size() == 0) return;

            for (Fragment frag : fragments) {
                if (frag != null && !frag.isDetached() && !frag.isRemoving()) {
                    frag.onActivityResult(requestCode, resultCode, data);
                }
            }
        }
    }

    public static void removeAll(@NonNull FragmentActivity activity) {
        FragmentManager fm = activity.getSupportFragmentManager();
        if (fm == null || fm.getFragments() == null) return;

        for (Fragment fragment : fm.getFragments()) {
            FragmentUtils.remove(fm, fragment);
        }
    }

    public static void removeAllStateless(FragmentActivity activity) {
        FragmentManager fm = activity.getSupportFragmentManager();
        if (fm == null || fm.getFragments() == null) return;

        for (Fragment fragment : fm.getFragments()) {
            FragmentUtils.removeStateless(fm, fragment);
        }
    }

    public static String dump(Fragment fragment) {
        if (fragment == null) return "";

        StringWriter writer = new StringWriter();
        fragment.dump("", null, new PrintWriter(writer, true), null);
        return writer.toString();
    }
}
