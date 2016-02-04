package com.mattandmikeandscott.richpersonleaderboard.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mattandmikeandscott.richpersonleaderboard.R;
import com.mattandmikeandscott.richpersonleaderboard.domain.Achievement;
import com.mattandmikeandscott.richpersonleaderboard.domain.Rank;

import java.text.NumberFormat;
import java.util.List;

public class AchievementsListAdapter extends BaseAdapter {
    private Context context;
    private NumberFormat formatter = NumberFormat.getCurrencyInstance();
    private List<Achievement> achievements;

    public AchievementsListAdapter(Context context, List<Achievement> achievements) {
        this.context = context;
        this.achievements = achievements;
    }

    public int getCount() {
        return achievements.size();
    }

    public Object getItem(int position) {
        return achievements.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup viewGroup) {
        final Achievement achievement = achievements.get(position);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.achievement_list_item, null);

        if(position % 2 == 0) {
            convertView.setBackgroundResource(R.color.colorLightBlue);
        } else {
            convertView.setBackgroundResource(android.R.color.white);
        }

        ((TextView) convertView.findViewById(R.id.achievement_name_text)).setText(achievement.getName());
        ((TextView) convertView.findViewById(R.id.achievement_description_text)).setText(achievement.getDescription());

        return convertView;
    }
}