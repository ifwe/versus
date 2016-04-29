package co.ifwe.versus.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.ifwe.versus.R;
import co.ifwe.versus.activities.CategoryActivity;
import co.ifwe.versus.models.Category;
import co.ifwe.versus.models.QueueResult;
import co.ifwe.versus.models.Topic;
import co.ifwe.versus.services.ConversationsService;
import co.ifwe.versus.services.TopicsService;
import co.ifwe.versus.services.VersusService;
import co.ifwe.versus.services.callbacks.StubCallback;
import co.ifwe.versus.utils.FragmentState;
import co.ifwe.versus.utils.FragmentUtils;

public class TopicFragment extends VersusFragment {
    private static final String TAG = TopicFragment.class.getCanonicalName();

    private static final String ARG_CATEGORY = "category";

    private Category mCategory;
    private Topic mTopic;

    @Bind(R.id.topic_a_image_view)
    ImageView mARoundedImageView;

    @Bind(R.id.topic_b_image_view)
    ImageView mBRoundedImageView;

    @Bind(R.id.topic_a_text_view)
    TextView mATextView;

    @Bind(R.id.topic_b_text_view)
    TextView mBTextView;

    @Bind(R.id.progress_overlay)
    View mProgressOverlay;

    @Inject
    ConversationsService mConversationsService;

    @Inject
    TopicsService mTopicsService;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Bundle args = getArguments();
        mCategory = args.getParcelable(ARG_CATEGORY);
        if (mCategory == null) {
            throw new IllegalArgumentException("A category is required");
        }
        getActivity().setTitle(mCategory.getName());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_topic, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTopicsService.getTopicForNewConversation(mCategory, new StubCallback<Topic>() {
            @Override
            public void onSuccess(Topic topic) {
//                Transformation transformation = new GlideRoundedTransformationBuilder()
//                        .borderColor(ResourcesCompat.getColor(getResources(), R.color.slate, null))
//                        .borderWidthDp(5)
//                        .cornerRadiusDp(60)
//                        .oval(true)
//                        .scaleType(ImageView.ScaleType.CENTER_CROP)
//                        .build(getActivity());
//
//                Transformation cropTransform = new CropCircleTransformation(getActivity());


                mTopic = topic;
                Glide.with(getActivity())
                    .load(topic.getSideAUrl())
                    .fallback(R.drawable.ic_broken)
                    .centerCrop()
                    .into(mARoundedImageView);

                Glide.with(getActivity())
                    .load(topic.getSideBUrl())
                    .fallback(R.drawable.ic_broken)
                    .centerCrop()
                    .into(mBRoundedImageView);

                mATextView.setText(topic.getSideA());
                mBTextView.setText(topic.getSideB());
            }

            @Override
            public void onError(int errorCode) {
                super.onError(errorCode);
                new MaterialDialog.Builder(getActivity())
                        .content(R.string.topic_fetch_error_message)
                        .positiveText(R.string.topic_fetch_error_ok)
                        .onAny((dialog, which) -> getActivity().finish())
                        .show();
            }
        });
    }

    @OnClick(R.id.topic_a_image_view)
    public void onTopicAClick() {
        if (mTopic != null) {
            mProgressOverlay.setVisibility(View.VISIBLE);
            mConversationsService.addToQueue(mTopic.getTopicId(), "side_a", new StubCallback<QueueResult>() {
                @Override
                public void onSuccess(QueueResult queueResult) {
                    processQueueResults(queueResult);
                }

                @Override
                public void onError(int errorCode) {
                    super.onError(errorCode);
                    onQueueError(errorCode);
                }
            });
        }
    }

    @OnClick(R.id.topic_b_image_view)
    public void onTopicBClick() {
        if (mTopic != null) {
            mProgressOverlay.setVisibility(View.VISIBLE);
            mConversationsService.addToQueue(mTopic.getTopicId(), "side_b", new StubCallback<QueueResult>() {
                @Override
                public void onSuccess(QueueResult queueResult) {
                    processQueueResults(queueResult);
                }

                @Override
                public void onError(int errorCode) {
                    super.onError(errorCode);
                    onQueueError(errorCode);
                }
            });
        }
    }

    private void processQueueResults(QueueResult queueResult) {
        CategoryActivity.ResultCodes result = queueResult.isMatched() ?
                CategoryActivity.ResultCodes.MATCHED : CategoryActivity.ResultCodes.ADDED;
        Intent intent = new Intent();
        intent.putExtra(CategoryActivity.EXTRA_RESULT, queueResult);
        getActivity().setResult(result.getCode(), intent);
        getActivity().finish();
    }

    private void onQueueError(int errorCode) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());
        if (errorCode == VersusService.ServiceResult.ERROR_TOO_MANY_REQUESTS) {
            builder.content(R.string.queue_error_insufficient_energy)
                    .positiveText(R.string.queue_error_ok)
                    .onAny((dialog, which) -> getActivity().finish());
        } else {
            builder.content(R.string.queue_error_message)
                    .positiveText(R.string.queue_error_ok)
                    .onAny((dialog, which) -> getActivity().finish());
        }
        builder.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Bundle fragmentState = CategoryListFragment.createState();
                FragmentUtils.replace(getActivity(), fragmentState, R.id.content_frame);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static Bundle createState(Category category) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_CATEGORY, category);
        return FragmentState.create(TopicFragment.class, args);
    }
}
