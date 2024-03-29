package com.example.android.baking_app.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.android.baking_app.R;
import com.example.android.baking_app.model.Step;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

public class StepDetailFragment extends Fragment {

    public static final String STEP_DATA = "STEP_DATA";
    public static final String FULLSCREEN_VIDEO = "FULLSCREEN_VIDEO";
    public static final String VIDEO_POSITION_KEY = "VIDEO_POSITION_KEY";
    public static final String VIDEO_PLAYING_KEY = "VIDEO_PLAYING_KEY";

    private String mVideoUrl = null;
    private SimpleExoPlayerView mVideoPlayerView = null;
    private SimpleExoPlayer mVideoPlayer = null;
    private long mCurrentVideoPosition = C.INDEX_UNSET;
    private boolean mPlayVideoWhenReady = true;
    private String mThumbnailUrl = null;
    private ImageView mThumbnailImageView = null;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater,
                             @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
        final View rootView =
                inflater.inflate( R.layout.fragment_step_detail, container, false);
        final Bundle arguments = getArguments();
        final Step step = (Step) arguments.getParcelable(STEP_DATA);
        mVideoUrl = step.getVideoUrl();
        mThumbnailUrl = step.getThumbnailUrl();
        if (null != mVideoUrl && !mVideoUrl.isEmpty()) {
            mVideoPlayerView = (SimpleExoPlayerView) rootView.findViewById(R.id.sepv_step_visualization);
            if (null != savedInstanceState && savedInstanceState.containsKey(VIDEO_POSITION_KEY)
                    && savedInstanceState.containsKey(VIDEO_PLAYING_KEY)) {
                mCurrentVideoPosition = savedInstanceState.getLong(VIDEO_POSITION_KEY);
                mPlayVideoWhenReady = savedInstanceState.getBoolean(VIDEO_PLAYING_KEY);
            }

            showVideoViewOnly();
            initializeVideoPlayer();

            if (arguments.getBoolean(FULLSCREEN_VIDEO, false)) {
                mVideoPlayerView.post(new Runnable() {
                    @Override
                    public void run() {
                        final int fullscreenHeight = rootView.getLayoutParams().height;
                        mVideoPlayerView.getLayoutParams().height = fullscreenHeight;
                        mVideoPlayerView.requestLayout();
                    }
                });
            }
        } else if (null != mThumbnailUrl && !mThumbnailUrl.isEmpty()) {
            mThumbnailImageView = rootView.findViewById(R.id.iv_step_visualization);
            showImageViewOnly();
            Picasso.with(getContext())
                    .load(Uri.parse(mThumbnailUrl))
                    .into(mThumbnailImageView);
        } else {
            hideImageAndVideoViews();
        }

        final TextView description = (TextView) rootView.findViewById(R.id.tv_step_description);
        description.setText(step.getDescription());

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        initializeVideoPlayer();
    }

    @Override
    public void onResume() {
        super.onResume();
        initializeVideoPlayer();
    }

    @Override
    public void onPause() {
        super.onPause();
        releaseVideoPlayer();
    }

    @Override
    public void onStop() {
        super.onStop();
        releaseVideoPlayer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseVideoPlayer();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(VIDEO_POSITION_KEY, mCurrentVideoPosition);
        outState.putBoolean(VIDEO_PLAYING_KEY, mPlayVideoWhenReady);
    }

    private void initializeVideoPlayer() {
        if (null == mVideoPlayer && null != mVideoUrl && null != mVideoPlayerView) {
            final BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            final TrackSelection.Factory videoTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(bandwidthMeter);
            final TrackSelector trackSelector =
                    new DefaultTrackSelector(videoTrackSelectionFactory);
            final Context context = getContext();
            mVideoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector);

            mVideoPlayerView.setPlayer(mVideoPlayer);

            final DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
                    Util.getUserAgent(context, getString(R.string.application_name_video_player)));
            final ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            final Uri videoUri = Uri.parse(mVideoUrl);
            final MediaSource videoSource = new ExtractorMediaSource(videoUri,
                    dataSourceFactory, extractorsFactory, null, null);
            mVideoPlayer.prepare(videoSource);
            mVideoPlayer.setPlayWhenReady(mPlayVideoWhenReady);
            mVideoPlayer.seekTo(mCurrentVideoPosition);
            mVideoPlayerView.requestFocus();
        }

    }

    private void releaseVideoPlayer() {
        if (null != mVideoPlayerView) {
            mVideoPlayerView.setPlayer(null);
        }

        if (null != mVideoPlayer) {
            mCurrentVideoPosition = mVideoPlayer.getCurrentPosition();
            mPlayVideoWhenReady = mVideoPlayer.getPlayWhenReady();
            mVideoPlayer.release();
            mVideoPlayer = null;
        }
    }

    private void hideImageAndVideoViews() {
        if (null != mVideoPlayerView) {
            mVideoPlayerView.setVisibility(View.GONE);
        }
        if (null != mThumbnailImageView) {
            mThumbnailImageView.setVisibility(View.GONE);
        }
    }

    private void showVideoViewOnly() {
        hideImageAndVideoViews();
        if (null != mVideoPlayerView) {
            mVideoPlayerView.setVisibility(View.VISIBLE);
        }
    }

    private void showImageViewOnly() {
        hideImageAndVideoViews();
        if (null != mThumbnailImageView) {
            mThumbnailImageView.setVisibility(View.VISIBLE);
        }
    }
}