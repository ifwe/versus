package co.ifwe.versus.views;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import co.ifwe.versus.R;
import co.ifwe.versus.fragments.ArbitrationStartFragment;
import co.ifwe.versus.fragments.ConversationListFragment;

/**
 * Created by schoi on 3/18/16.
 */
public enum ShelfItem {
    INBOX(R.string.drawer_item_inbox, R.drawable.ic_question_answer_24px) {
        @Override
        public Bundle getFragmentState() {
            return ConversationListFragment.createState(true);
        }
    },
    ARCHIVE(R.string.drawer_item_archive, R.drawable.ic_archive_24dp) {
        @Override
        public Bundle getFragmentState() {
            return ConversationListFragment.createState(false);
        }
    },
    ARBITRATION(R.string.drawer_item_arbitration, R.drawable.ic_gavel_24px) {
        @Override
        public Bundle getFragmentState() {
            return ArbitrationStartFragment.createState();
        }
    },
    LOGOUT(R.string.drawer_item_logout, R.drawable.ic_power_settings_new_24px) {
        @Override
        public Bundle getFragmentState() {
            return null;
        }
    };

    @StringRes
    private int mStringResId;

    @DrawableRes
    private int mDrawableResId;

    ShelfItem(@StringRes int stringResId, @DrawableRes int drawableResId) {
        mStringResId = stringResId;
        mDrawableResId = drawableResId;
    }

    public int getStringResId() {
        return mStringResId;
    }

    public int getDrawableResId() {
        return mDrawableResId;
    }

    public abstract Bundle getFragmentState();
}
