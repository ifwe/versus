package co.ifwe.versus.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import co.ifwe.versus.R;
import co.ifwe.versus.fragments.CategoryListFragment;
import co.ifwe.versus.fragments.TopicFragment;
import co.ifwe.versus.utils.FragmentUtils;

public class CategoryActivity extends VersusActivity {

    public static final int REQUEST_CODE = 0xca;
    public static final String EXTRA_RESULT = "queue_result";

    public enum ResultCodes {
        ADDED (RESULT_OK),
        MATCHED (RESULT_OK),
        CANCELLED (RESULT_CANCELED);

        private int mCode;

        ResultCodes (int code) {
            mCode = code;
        }

        public int getCode() {
            return mCode;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        FragmentUtils.addSingle(this, CategoryListFragment.createState(), R.id.content_frame);
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (fragment instanceof TopicFragment) {
            FragmentUtils.replace(this, CategoryListFragment.createState(), R.id.content_frame);
        } else {
            super.onBackPressed();
        }
    }

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, CategoryActivity.class);
        return intent;
    }

    public static void start(Context context) {
        context.startActivity(createIntent(context));
    }

    public static void startForResult(Activity context) {
        context.startActivityForResult(createIntent(context), REQUEST_CODE);
    }
}
