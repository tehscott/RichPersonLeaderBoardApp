package com.mattandmikeandscott.richpersonleaderboard.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mattandmikeandscott.richpersonleaderboard.R;
import com.mattandmikeandscott.richpersonleaderboard.domain.Person;
import com.mattandmikeandscott.richpersonleaderboard.domain.Rank;

import java.text.NumberFormat;
import java.util.List;

public class RankingListAdapter extends BaseAdapter {
    private Context context;
    private NumberFormat formatter = NumberFormat.getCurrencyInstance();
    private List<Rank> ranks;

    public RankingListAdapter(Context context, List<Rank> ranks) {
        this.context = context;
        this.ranks = ranks;
    }

    public int getCount() {
        return ranks.size();
    }

    public Object getItem(int position) {
        return ranks.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup viewGroup) {
        final Rank currentRank = ranks.get(position);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.ranking_list_item, null);

        if(position % 2 == 0) {
            convertView.setBackgroundResource(R.color.colorLightBlue);
        } else {
            convertView.setBackgroundResource(android.R.color.white);
        }

        TextView gameName = (TextView) convertView.findViewById(R.id.ranking_category_text);
        gameName.setText(currentRank.getRankType().getName());

        TextView gameInfo = (TextView) convertView.findViewById(R.id.ranking_rank_and_wealth_text);
        gameInfo.setText("Rank: " + currentRank.getRank() + "  Wealth: " + formatter.format(currentRank.getWealth()));

        return convertView;
    }
}