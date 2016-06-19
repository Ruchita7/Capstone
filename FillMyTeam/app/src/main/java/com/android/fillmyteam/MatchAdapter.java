package com.android.fillmyteam;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.fillmyteam.model.Match;
import com.android.fillmyteam.util.Utility;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by dgnc on 6/19/2016.
 */
public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.ViewHolder> {
    List<Match> matchesList;
    Context mContext;

    public MatchAdapter(Context context, List<Match> matches) {
        matchesList = matches;
        mContext = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.playing_time)
        TextView playingTime;
        @BindView(R.id.playing_place)
        TextView playingPlace;
        @BindView(R.id.playing_with)
        TextView player;
        @BindView(R.id.game_image_view)
        ImageView sport;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Match match = matchesList.get(position);
        holder.player.setText(match.getPlayingWith());
        holder.playingTime.setText(match.getPlayingDate() + " " + match.getPlayingTime());
        holder.playingPlace.setText(match.getPlayingPlace());
        int sportDrawable= Utility.retrieveSportsIcon(match.getSport());
        if(sportDrawable!=0)
        {
            holder.sport.setImageDrawable(mContext.getDrawable(sportDrawable));
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.match_detail_list_item, parent, false);
        view.setFocusable(true);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return matchesList.size();
    }
}
