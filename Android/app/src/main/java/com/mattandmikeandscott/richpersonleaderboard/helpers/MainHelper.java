package com.mattandmikeandscott.richpersonleaderboard.helpers;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.mattandmikeandscott.richpersonleaderboard.MainActivity;
import com.mattandmikeandscott.richpersonleaderboard.MainFragment;
import com.mattandmikeandscott.richpersonleaderboard.R;
import com.mattandmikeandscott.richpersonleaderboard.adapters.SectionsPagerAdapter;
import com.mattandmikeandscott.richpersonleaderboard.domain.PeopleQueryType;
import com.mattandmikeandscott.richpersonleaderboard.domain.RankType;

import java.security.MessageDigest;
import java.util.Hashtable;
import java.util.Map;

public class MainHelper {
    private MainActivity mainActivity;

    private static final String UNSIGNED_SIGNATURE = "NaiuHKGQ8+GVtsbqXUKZ47XW2k4=";
    private static final String SIGNED_SIGNATURE = "kao+M4xQqNos2r7KVuj5d2LHONk=";
    public static boolean IS_DEBUG = false;

    public MainHelper(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void setupButtons() {
        mainActivity.findViewById(R.id.find_me_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mainActivity.getSignInHelper().isSignedIn()) {
                    findMe();
                } else {
                    mainActivity.getSignInHelper().signIn();
                }
            }
        });

        mainActivity.findViewById(R.id.raise_rank_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mainActivity.getSignInHelper().isSignedIn()) {
                    showBuyDialog(mainActivity.getSignInHelper().getId());
                } else {
                    mainActivity.getSignInHelper().signIn();
                }
            }
        });
    }

    public void setupViewPager() {
        mainActivity.setSectionsPagerAdapter(new SectionsPagerAdapter(mainActivity.getSupportFragmentManager()));
        mainActivity.setViewPager((ViewPager) mainActivity.findViewById(R.id.pager));
        mainActivity.getViewPager().setAdapter(mainActivity.getSectionsPagerAdapter());
        mainActivity.getViewPager().setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mainActivity.getSupportActionBar().setSelectedNavigationItem(position);
            }
        });

        for (int i = 0; i < mainActivity.getSectionsPagerAdapter().getCount(); i++) {
            mainActivity.getSupportActionBar().addTab(
                    mainActivity.getSupportActionBar().newTab()
                            .setText(mainActivity.getSectionsPagerAdapter().getPageTitle(i))
                            //.setIcon(mSectionsPagerAdapter.getPageIcon(i))
                            .setTabListener(mainActivity));
        }
    }

    private void findMe() {
        Map<String, String> parameters = new Hashtable<>();
        parameters.put("id", mainActivity.getSignInHelper().getId());
        parameters.put("range", "5");

        int currentPosition = mainActivity.getViewPager().getCurrentItem();
        String tag = "";
        RankType rankType = RankType.AllTime;

        switch(currentPosition) {
            case 0:
                rankType = RankType.AllTime;
                tag = rankType.getName();
                break;
            case 1:
                rankType = RankType.Day;
                tag = rankType.getName();
                break;
            case 2:
                rankType = RankType.Week;
                tag = rankType.getName();
                break;
            case 3:
                rankType = RankType.Month;
                tag = rankType.getName();
                break;
            case 4:
                rankType = RankType.Year;
                tag = rankType.getName();
                break;
        }

        View fragmentList = mainActivity.getViewPager().findViewWithTag(tag);
        MainFragment.refreshListAsync(mainActivity.getResources(), mainActivity, fragmentList, PeopleQueryType.Myself, rankType, parameters);
    }

    public void setupActionBar() {
        // Set up the action bar.
        final ActionBar actionBar = mainActivity.getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        hideActionBarTitle(actionBar);
    }

    private void hideActionBarTitle(ActionBar actionBar) {
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
    }

    public void showBuyDialog(final String personId) {
        final Dialog dialog = new Dialog(mainActivity, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog.setCancelable(true);

        LayoutInflater inflater = (LayoutInflater) mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.buy_dialog, null);

        layout.findViewById(R.id.buy_dialog_back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setContentView(layout);
        dialog.show();
    }

    public boolean isAppSigned() {
        try {
            PackageInfo packageInfo = mainActivity.getPackageManager().getPackageInfo(mainActivity.getPackageName(), PackageManager.GET_SIGNATURES);

            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                final String currentSignature = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                //Log.d("REMOVE_ME", "Include this string as a value for SIGNATURE:" + currentSignature);
                //Log.d("REMOVE_ME", "Include this string as a value for SIGNATURE:" + SIGNED_SIGNATURE);
                if (SIGNED_SIGNATURE.equals(currentSignature.trim())){
                    //Log.d("REMOVE_ME", "It's signed eh");
                    return true;
                };
            }
        } catch (Exception e) {
            //Log.d("REMOVE_ME", e.getMessage());
        }

        //Log.d("REMOVE_ME", "It's not signed eh");
        return false;
    }
}
