package com.android.fillmyteam;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.fillmyteam.data.SportsColumns;
import com.android.fillmyteam.data.SportsProvider;
import com.android.fillmyteam.util.Constants;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 *
 * @author Ruchita_Maheshwary
 * Fragment for showing sports detail
 */
public class SportsDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    static final String DETAIL_TRANSITION_ANIMATION = "DTA";
    static final String DETAIL_URI = "URI";

    Uri mUri;
    public static final String LOG_TAG = SportsDetailFragment.class.getSimpleName();
    public static final String POSITION = "position";

    @BindView(R.id.sports_name_text)
    TextView mSportNameTextView;
    @BindView(R.id.objective_text)
    TextView mObjectiveTextView;
    @BindView(R.id.players_text)
    TextView mPlayersTextView;
    @BindView(R.id.rules_text)
    TextView mRulesTextView;
    @BindView(R.id.sport_poster)
    ImageView mSportsImageView;
    @BindView(R.id.playVideo)
    ImageView mVideoPlayImageView;
    String sportsName;
    String mVideoKey;
    Context mContext;
    CollapsingToolbarLayout collapsingToolbar;

    String mSportId;
    public static final int DETAIL_LOADER = 0;

    int mPosition;
    public SportsDetailFragment() {
    }


    @Override
    public String toString() {
        return super.toString();
    }

    public static SportsDetailFragment newInstance(String sportId,int cursorPosition) {
        SportsDetailFragment fragment = new SportsDetailFragment();
        Bundle args = new Bundle();
        args.putString(Constants.SPORT_ID, sportId);
        args.putInt(POSITION,cursorPosition);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (getArguments() != null) {
            mSportId = getArguments().getString(Constants.SPORT_ID);
            Log.v(LOG_TAG, "sport id is ::" + mSportId);
            mPosition=getArguments().getInt(POSITION);
        }

        mContext = getActivity();
        View view = inflater.inflate(R.layout.fragment_sports_detail, container, false);
        AppCompatActivity activity = (AppCompatActivity) getActivity();

        final Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_layout);
        if (toolbar != null) {
            toolbar.setNavigationIcon(R.drawable.ic_action_ic_arrow_back);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.v(LOG_TAG, "on click clicked");
                   /* Intent intent = new Intent(mContext,SportsInfoFragment.class);
                    intent.putExtra(POSITION,mPosition);
                    startActivity(intent);*/
                    getFragmentManager().popBackStackImmediate();
                }
            });
        }

        collapsingToolbar = (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);
        mUri = SportsProvider.Sports.CONTENT_URI;
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        final ActionBar ab = ((MainActivity) getActivity()).getSupportActionBar();
        ab.hide();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (null != mUri) {
            String selectionClause = SportsColumns._ID + "=?";
            String[] selectionArgs = {""};
            selectionArgs[0] = mSportId;

            return new CursorLoader(getActivity(), mUri,
                    null,
                    selectionClause,
                    selectionArgs,
                    null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) {
            return;
        }
        if (data != null && data.moveToFirst()) {
            sportsName = data.getString(data.getColumnIndex(SportsColumns.SPORTS_NAME));
            if (collapsingToolbar != null) {
                collapsingToolbar.setTitle(sportsName);
            }
            String players = data.getString(data.getColumnIndex(SportsColumns.PLAYERS));
            String imageUrl = data.getString(data.getColumnIndex(SportsColumns.POSTER_IMAGE));
            String objective = data.getString(data.getColumnIndex(SportsColumns.OBJECTIVE));
            String rules = data.getString(data.getColumnIndex(SportsColumns.RULES));
          //  String videoReference = data.getString(data.getColumnIndex(SportsColumns.VIDEO_URL));
            mVideoKey = data.getString(data.getColumnIndex(SportsColumns.VIDEO_URL));
            mObjectiveTextView.setText(objective);
            mPlayersTextView.setText(players);
            mRulesTextView.setText(rules);
            mSportNameTextView.setText(sportsName);
            Picasso.with(getActivity()).load(imageUrl).into(mSportsImageView);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @OnClick(R.id.playVideo)
    public void playVideo() {
        Intent intent = YouTubeStandalonePlayer.createVideoIntent(
                getActivity(), Constants.YOUTUBE_KEY, mVideoKey, 0, true, false);
        if (intent != null) {
            if (canResolveIntent(intent)) {
                startActivityForResult(intent, Constants.REQ_START_STANDALONE_PLAYER);
            } else {
                // Could not resolve the intent - must need to install or update the YouTube API service.
                YouTubeInitializationResult.SERVICE_MISSING
                        .getErrorDialog(getActivity(), Constants.REQ_RESOLVE_SERVICE_MISSING).show();
            }
        }
    }

    @Override
    public void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v(LOG_TAG, "In on activity result");
        if (requestCode == Constants.REQ_START_STANDALONE_PLAYER && resultCode != Activity.RESULT_OK) {
            YouTubeInitializationResult errorReason =
                    YouTubeStandalonePlayer.getReturnedInitializationResult(data);
            if (errorReason.isUserRecoverableError()) {
                errorReason.getErrorDialog(getActivity(), 0).show();
            } else {
                String errorMessage =
                        String.format(getString(R.string.error_player), errorReason.toString());
                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
            }
        }
    }


    private boolean canResolveIntent(Intent intent) {
        List<ResolveInfo> resolveInfo = getActivity().getPackageManager().queryIntentActivities(intent, 0);
        return resolveInfo != null && !resolveInfo.isEmpty();
    }


}
