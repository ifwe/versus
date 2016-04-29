package co.ifwe.versus.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.text.TextUtilsCompat;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.Locale;

@SuppressWarnings("unchecked")
public final class ViewUtils {

    private ViewUtils() {
    }

    public static <T extends View> T findView(@NonNull View view, @IdRes int viewId) {
        return (T) view.findViewById(viewId);
    }

    public static <T extends View> T findView(@NonNull ViewGroup view, Class clazz) {
        for (int i = 0; i < view.getChildCount(); i++) {
            View potential = view.getChildAt(i);
            if (potential != null && potential.getClass() == clazz) {
                return (T) potential;
            }
        }
        return null;
    }

    public static <T extends View> T findViewInstanceOf(@NonNull ViewGroup view, Class clazz) {
        LinkedList<ViewGroup> queue = new LinkedList();
        queue.add(view);

        while (!queue.isEmpty()) {
            ViewGroup group = queue.remove(0);

            for (int i = 0; i < group.getChildCount(); i++) {
                View potential = group.getChildAt(i);
                if (potential != null && clazz.isAssignableFrom(potential.getClass())) {
                    return (T) potential;
                }

                if (potential instanceof ViewGroup) {
                    queue.add((ViewGroup) potential);
                }
            }
        }
        return null;
    }

    public static <T extends View> T findView(@NonNull Activity activity, @IdRes int viewId) {
        return (T) activity.findViewById(viewId);
    }

    public static void show(@Nullable View view, boolean show) {
        changeVisibility(view, show, View.VISIBLE, View.GONE);
    }

    public static void visible(@Nullable View view, boolean visible) {
        changeVisibility(view, visible, View.VISIBLE, View.INVISIBLE);
    }

    private static void changeVisibility(View view, boolean show, int on, int off) {
        if (view != null) view.setVisibility(show ? on : off);
    }

    public static <T extends View> T attach(View root, @LayoutRes int layoutId, @IdRes int parentIdRes) {
        final ViewGroup parent = findView(root, parentIdRes);
        return inflate(layoutId, parent, true);
    }

    public static <T extends View> T inflate(@LayoutRes int layoutResId, @NonNull ViewGroup parent) {
        return inflate(parent.getContext(), layoutResId, parent);
    }

    public static <T extends View> T inflate(@LayoutRes int layoutResId, @NonNull ViewGroup parent, boolean attachToParent) {
        return inflate(parent.getContext(), layoutResId, parent, attachToParent);
    }

    public static <T extends View> T inflate(Context context, @LayoutRes int layoutResId) {
        return inflate(context, layoutResId, null, false);
    }

    public static <T extends View> T inflate(Context context, @LayoutRes int layoutResId, @Nullable ViewGroup parent) {
        return inflate(context, layoutResId, parent, false);
    }

    public static <T extends View> T inflate(Context context, @LayoutRes int layoutResId, @Nullable ViewGroup parent, boolean attachToParent) {
        return (T) LayoutInflater.from(context).inflate(layoutResId, parent, attachToParent);
    }

    public static void setText(View root, @IdRes int textViewResId, @StringRes int stringResId) {
        setText(root, textViewResId, root.getResources().getString(stringResId));
    }

    public static void setText(View root, @IdRes int textViewResId, String text) {
        final TextView textView = findView(root, textViewResId);
        textView.setText(text);
    }

    public static void setPaddingTop(@NonNull View view, int padding) {
        view.setPadding(view.getPaddingLeft(), padding, view.getPaddingRight(), view.getPaddingBottom());
    }

    public static int getViewTopInWindow(View view) {
        int[] coords = new int[2];
        view.getLocationInWindow(coords);
        return coords[1];
    }

    public static int getViewBottomInWindow(View view) {
        return getViewTopInWindow(view) + view.getHeight();
    }

    public static boolean inBounds(View view, int x, int y) {
        int[] coords = new int[2];
        view.getLocationInWindow(coords);

        if (x >= 0) {
            if (coords[0] > x || coords[0] + view.getWidth() < x) return false;
        }
        if (y >= 0) {
            if (coords[1] > y || coords[1] + view.getHeight() < y) return false;
        }
        return true;
    }

    public static void setPaddingRight(@NonNull View view, int padding) {
        view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), padding, view.getPaddingBottom());
    }

    public static boolean isRtl() {
        return TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault()) ==
            ViewCompat.LAYOUT_DIRECTION_RTL;
    }

    public static void setSize(View view, int width, int height) {
        setHeight(view, height);
        setWidth(view, width);
    }

    public static void setHeight(View view, int height) {
        if (view != null) {
            ViewGroup.LayoutParams params = view.getLayoutParams();
            if (params != null && params.height != height) {
                params.height = height;
                view.setLayoutParams(params);
            }
        }
    }

    public static void setWidth(View view, int width) {
        if (view != null) {
            ViewGroup.LayoutParams params = view.getLayoutParams();
            if (params != null && params.width != width) {
                params.width = width;
                view.setLayoutParams(params);
            }
        }
    }

    public static void sendEvent(View view, int action, MotionEvent baselineEvent) {
        MotionEvent ev;
        if (baselineEvent != null) {
            ev = MotionEvent.obtain(baselineEvent);
            ev.setAction(action);
        } else {
            ev = MotionEvent.obtain(0, 0, action, 0, 0, 0);
        }
        view.dispatchTouchEvent(ev);
    }

    public static void startGesture(View view, MotionEvent ev) {
        sendEvent(view, MotionEvent.ACTION_DOWN, ev);
    }

    public static void cancelGesture(View view, MotionEvent ev) {
        sendEvent(view, MotionEvent.ACTION_CANCEL, ev);
    }

    public static void setCompoundDrawablesRelativeWithIntrinsicBounds(
            @NonNull TextView textView, Drawable start, Drawable top, Drawable end, Drawable bottom) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            textView.setCompoundDrawablesRelativeWithIntrinsicBounds(start, top, end, bottom);
        } else {
            textView.setCompoundDrawablesWithIntrinsicBounds(start, top, end, bottom);
        }
    }

    public static ViewGroup.LayoutParams createViewGroupLayoutParamsMatchParent() {
        return new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    public static void setVerticalMargins(View view, int margin) {
        if (view == null) return;

        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) layoutParams;
            marginParams.setMargins(marginParams.leftMargin, margin, marginParams.rightMargin, margin);
            view.setLayoutParams(layoutParams);
        }
    }
}
