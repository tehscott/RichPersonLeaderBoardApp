package com.mattandmikeandscott.richpersonleaderboard;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.mattandmikeandscott.richpersonleaderboard.domain.GetPeopleResponse;
import com.mattandmikeandscott.richpersonleaderboard.domain.MainActivityHandlerResult;
import com.mattandmikeandscott.richpersonleaderboard.domain.PeopleQueryType;
import com.mattandmikeandscott.richpersonleaderboard.domain.Person;
import com.mattandmikeandscott.richpersonleaderboard.domain.RankType;
import com.mattandmikeandscott.richpersonleaderboard.network.Repository;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

public class MainFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";

    public static MainFragment newInstance(int sectionNumber) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public MainFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View listLayout = null;
        int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);

        listLayout = inflater.inflate(R.layout.fragment_list, container, false);

        RankType rankType = RankType.AllTime;

        switch (sectionNumber) {
            case 0:
                rankType = RankType.AllTime;

                break;
            case 1:
                rankType = RankType.Day;

                break;
            case 2:
                rankType = RankType.Week;

                break;
            case 3:
                rankType = RankType.Month;

                break;
            case 4:
                rankType = RankType.Year;

                break;
        }

        ((RelativeLayout) listLayout).setTag(rankType.getName());
        container.setTag(rankType.getName());

        Map<String, String> parameters = new Hashtable<>();
        parameters.put("offset", "0");
        parameters.put("perpage", "100");
        parameters.put("ranktype", String.valueOf(rankType.getValue()));

        refreshListAsync(getResources(), (MainActivity) getActivity(), listLayout, PeopleQueryType.Persons, rankType, parameters);
        setupSwipeLayout(listLayout, PeopleQueryType.Persons, rankType, parameters);

        return listLayout;
    }

    private void setupSwipeLayout(final View listLayout, final PeopleQueryType queryType, final RankType rankType, final Map<String, String> parameters) {
        final SwipeRefreshLayout swipeLayout = (SwipeRefreshLayout) listLayout.findViewById(R.id.list_container);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshListAsync(getResources(), (MainActivity) getActivity(), listLayout, queryType, rankType, parameters);
            }
        });
    }

    public static void refreshListAsync(final Resources resources, final MainActivity activity, final View listLayout, final PeopleQueryType queryType, final RankType rankType, final Map<String, String> parameters) {
        final SwipeRefreshLayout swipeLayout = (SwipeRefreshLayout) listLayout.findViewById(R.id.list_container);
        swipeLayout.setRefreshing(true);
        swipeLayout.setEnabled(false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Repository repository = new Repository(resources);
                ArrayList<Person> people = repository.getPeople(queryType, parameters);
                ListView list = (ListView) listLayout.findViewById(R.id.list);
                GetPeopleResponse response = new GetPeopleResponse(activity, list, people, queryType, rankType, true);

                Message message = new Message();
                message.what = MainActivityHandlerResult.PEOPLE_INFO_AQUIRED.ordinal();
                message.obj = response;

                activity.handler.sendMessage(message);
            }
        }).start();
    }
}