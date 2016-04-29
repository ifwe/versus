package co.ifwe.versus.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import co.ifwe.versus.R;
import co.ifwe.versus.fragments.ArbitrationFragment;
import co.ifwe.versus.models.Conversation;
import co.ifwe.versus.utils.FragmentUtils;

public class ArbitrationActivity extends VersusActivity {

    public static final String EXTRA_CONVERSATION = "conversation";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Conversation conversation = getIntent().getParcelableExtra(EXTRA_CONVERSATION);

        FragmentUtils.addSingle(this, ArbitrationFragment.createState(conversation), R.id.content_frame);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (isTaskRoot()) {
            MainActivity.start(this);
        } else {
            super.onBackPressed();
        }
    }

    public static Intent createIntent(Context context, Conversation conversation) {
        Intent intent = new Intent(context, ArbitrationActivity.class);
        intent.putExtra(EXTRA_CONVERSATION, conversation);
        return intent;
    }

    public static void start(Context context, Conversation conversation) {
        context.startActivity(createIntent(context, conversation));
    }
}
