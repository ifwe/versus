package co.ifwe.versus.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import co.ifwe.versus.R;
import co.ifwe.versus.fragments.ConversationFragmentV2;
import co.ifwe.versus.models.Conversation;
import co.ifwe.versus.utils.FragmentUtils;

public class ConversationActivity extends VersusActivity {

    public static final String EXTRA_CONVERSATION = "conversation";
    public static final String EXTRA_ROOM_NAME = "room_name";
    public static final String EXTRA_TOPIC = "topic";
    public static final String EXTRA_END_TIME = "end_time";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        String roomName = getIntent().getStringExtra(EXTRA_ROOM_NAME);
        Conversation conversation = getIntent().getParcelableExtra(EXTRA_CONVERSATION);

        if (conversation == null) {
            FragmentUtils.addSingle(this, ConversationFragmentV2.createState(roomName), R.id.content_frame);
        } else {
            FragmentUtils.addSingle(this, ConversationFragmentV2.createState(conversation), R.id.content_frame);
        }

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
        NavUtils.navigateUpFromSameTask(this);
    }

    public static Intent createIntent(Context context, String roomName) {
        Intent intent = new Intent(context, ConversationActivity.class);
        intent.putExtra(EXTRA_ROOM_NAME, roomName);
        return intent;
    }

    public static Intent createIntent(Context context, Conversation conversation) {
        Intent intent = new Intent(context, ConversationActivity.class);
        intent.putExtra(EXTRA_CONVERSATION, conversation);
        return intent;
    }

    public static Intent createIntent(Context context, String roomName, String topic, long endTime) {
        Intent intent = new Intent(context, ConversationActivity.class);
        intent.putExtra(EXTRA_ROOM_NAME, roomName);
        intent.putExtra(EXTRA_TOPIC, topic);
        intent.putExtra(EXTRA_END_TIME, endTime);
        return intent;
    }

    public static void start(Context context, String roomName, String topic, long endTime) {
        context.startActivity(createIntent(context, roomName, topic, endTime));
    }

    public static void start(Context context, Conversation conversation) {
        context.startActivity(createIntent(context, conversation));
    }

    public static void start(Context context, String roomName) {
        context.startActivity(createIntent(context, roomName));
    }
}
