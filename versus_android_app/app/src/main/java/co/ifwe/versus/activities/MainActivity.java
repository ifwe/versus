package co.ifwe.versus.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.facebook.Profile;
import com.facebook.login.LoginManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import co.ifwe.versus.BuildConfig;
import co.ifwe.versus.R;
import co.ifwe.versus.adapters.ShelfAdapter;
import co.ifwe.versus.fragments.ConversationListFragment;
import co.ifwe.versus.models.QueueResult;
import co.ifwe.versus.models.Result;
import co.ifwe.versus.models.User;
import co.ifwe.versus.provider.Projection;
import co.ifwe.versus.provider.VersusContract;
import co.ifwe.versus.services.AuthService;
import co.ifwe.versus.services.ConversationsService;
import co.ifwe.versus.services.StartupService;
import co.ifwe.versus.services.SubscribeService;
import co.ifwe.versus.services.callbacks.StubCallback;
import co.ifwe.versus.utils.CursorUtils;
import co.ifwe.versus.utils.FragmentUtils;
import co.ifwe.versus.utils.VersusPrefUtils;
import co.ifwe.versus.views.GlideRoundedTransformationBuilder;
import co.ifwe.versus.views.ShelfItem;

public class MainActivity extends VersusActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = MainActivity.class.getCanonicalName();
    private static final int MAX_PROFILE_IMAGE_SIZE = 72 * 4;

    private static final int WIN_LOSS_LOADER_ID = 1;

    private ActionBarDrawerToggle mDrawerToggle;
    private ShelfAdapter mShelfAdapter;
    private Timer mTimer;
    private User mUser;

    private TimerTask mUserTimerTask;

    @Inject
    SharedPreferences mSharedPreferences;

    @Inject
    StartupService mStartupService;

    @Inject
    ConversationsService mConversationsService;

    @Inject
    AuthService mAuthService;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @Bind(R.id.left_drawer)
    ListView mDrawerList;

    @Bind(R.id.logout_text_view)
    TextView mLogoutView;

    @Bind(R.id.profile_image_view)
    ImageView mProfileImageView;

    @Bind(R.id.name_text_view)
    TextView mNameTextView;

    @Bind(R.id.energy_text_view)
    TextView mEnergyTextView;

    @Bind(R.id.refresh_text_view)
    TextView mRefreshTextView;

    @Bind(R.id.win_text_view)
    TextView mWinTextView;

    @Bind(R.id.loss_text_view)
    TextView mLossTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Intent serviceIntent = new Intent(this, SubscribeService.class);
        startService(serviceIntent);
        setSupportActionBar(mToolbar);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close);
        mDrawerToggle.setDrawerIndicatorEnabled(true);

        mDrawerLayout.addDrawerListener(mDrawerToggle);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        User user = VersusPrefUtils.getUser(mSharedPreferences);
        if (user != null) {
            updateUser(user);
        }
        Transformation transformation = new GlideRoundedTransformationBuilder()
                .cornerRadiusDp(4)
                .build(this);
        Glide.with(this)
                .load(Profile.getCurrentProfile().getProfilePictureUri(MAX_PROFILE_IMAGE_SIZE, MAX_PROFILE_IMAGE_SIZE))
                .placeholder(R.drawable.ic_placeholder)
                .centerCrop()
                .crossFade()
                .bitmapTransform(transformation)
                .into(mProfileImageView);

        mNameTextView.setText(mUser.getName());

        List<ShelfItem> shelfItems = new ArrayList<>();
        shelfItems.add(ShelfItem.INBOX);
        shelfItems.add(ShelfItem.ARCHIVE);
        shelfItems.add(ShelfItem.ARBITRATION);

        mShelfAdapter = new ShelfAdapter(this, shelfItems);
        mDrawerList.setAdapter(mShelfAdapter);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_power_settings_new_24px, null);
            drawable.setColorFilter(ResourcesCompat.getColor(getResources(), R.color.menuIconColor, null), PorterDuff.Mode.SRC_IN);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                mLogoutView.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, null, null, null);
            } else {
                mLogoutView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
            }
        }

        FragmentUtils.addSingle(this, ConversationListFragment.createState(true), R.id.content_frame);
        getSupportLoaderManager().initLoader(WIN_LOSS_LOADER_ID, null, this);
    }

    @OnItemClick(R.id.left_drawer)
    void onDrawerItemClick(int position) {
        FragmentUtils.replace(MainActivity.this, mShelfAdapter.getItem(position).getFragmentState(),
                R.id.content_frame);
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    @OnClick(R.id.logout_text_view)
    void onLogoutClick() {
        new MaterialDialog.Builder(this)
                .content(R.string.main_logout_message)
                .positiveText(R.string.main_logout_yes)
                .negativeText(R.string.main_logout_no)
                .onPositive((dialog, which) -> {
                    VersusPrefUtils.removeUserId(mSharedPreferences);
                    LoginManager.getInstance().logOut();
                    LoginActivity.start(MainActivity.this);
                    finish();
                })
                .show();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            FragmentManager manager = getSupportFragmentManager();
            Fragment fragment = manager.findFragmentById(R.id.content_frame);
            if (fragment instanceof ConversationListFragment) {
                boolean active = fragment.getArguments().getBoolean(ConversationListFragment.ARG_ACTIVE, true);
                if (active) {
                    finish();
                    return;
                }
            }
            FragmentUtils.replace(MainActivity.this, ConversationListFragment.createState(true), R.id.content_frame);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
        mStartupService.refreshData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CategoryActivity.REQUEST_CODE) {
            switch (resultCode) {
                case RESULT_OK:
                    QueueResult result = data.getParcelableExtra(CategoryActivity.EXTRA_RESULT);
                    ConversationActivity.start(this, result.getRoomName());
                    break;
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    private void updateUser(User user) {
        mUser = user;
        updateUserInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTimer = new Timer();
        mUserTimerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> updateUserInfo());
                if (System.currentTimeMillis() > mUser.getNextEnergyUpdate()) {
                    mAuthService.getUser(new StubCallback<User>() {
                        @Override
                        public void onSuccess(User user) {
                            super.onSuccess(user);
                            mUser = user;
                            runOnUiThread(MainActivity.this::updateUserInfo);

                            if (mUser.getNextEnergyUpdate() <= 0) {
                                mTimer.cancel();
                                mTimer.purge();
                            }
                        }
                    });
                }
            }
        };
        mTimer.schedule(mUserTimerTask, 0, 1000);
        mAuthService.getUser(new StubCallback<User>() {
            @Override
            public void onSuccess(User user) {
                super.onSuccess(user);
                updateUser(user);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mTimer.cancel();
        mTimer.purge();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                this,
                VersusContract.Conversations.buildResultsUri(),
                Projection.RESULTS,
                VersusContract.Conversations.buildResultsSelection(Result.WIN, Result.LOSS, Result.DRAW),
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int wins = 0;
        int losses = 0;
        if (data != null && data.moveToFirst()) {
            do {
                int count = CursorUtils.getInt(data, "COUNT(*)", 0);
                String code = CursorUtils.getString(data, VersusContract.Conversations.RESULT, Result.NONE.getCode());
                Result result = Result.fromCode(code);
                if (result == Result.WIN) {
                    wins = count;
                } else if (result == Result.LOSS) {
                    losses = count;
                }
            } while (data.moveToNext());
        }
        mWinTextView.setText(getString(R.string.user_win_loss, wins));
        mLossTextView.setText(getString(R.string.user_win_loss, losses));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void updateUserInfo() {
        mEnergyTextView.setText(Integer.toString(mUser.getEnergy()));
        long timeDiff = mUser.getNextEnergyUpdate() - System.currentTimeMillis();
        if (timeDiff > 0) {
            long minutes = TimeUnit.MILLISECONDS.toMinutes(timeDiff);
            long seconds = TimeUnit.MILLISECONDS.toSeconds(timeDiff) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeDiff));
            mRefreshTextView.setText(String.format("%02d:%02d",minutes, seconds));
        } else {
            mRefreshTextView.setText("");
        }
    }

    public static Intent createIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    public static void start(Context context) {
        context.startActivity(createIntent(context));
    }
}
