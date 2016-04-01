package com.mattandmikeandscott.richpersonleaderboard.helpers;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.mattandmikeandscott.richpersonleaderboard.MainActivity;
import com.mattandmikeandscott.richpersonleaderboard.MainFragment;
import com.mattandmikeandscott.richpersonleaderboard.R;
import com.mattandmikeandscott.richpersonleaderboard.adapters.SectionsPagerAdapter;
import com.mattandmikeandscott.richpersonleaderboard.domain.Constants;
import com.mattandmikeandscott.richpersonleaderboard.domain.PeopleQueryType;
import com.mattandmikeandscott.richpersonleaderboard.domain.RankType;
import com.mattandmikeandscott.richpersonleaderboard.network.Repository;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import java.security.MessageDigest;
import java.util.ArrayList;
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
                    //findMe();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Repository repo = new Repository(mainActivity.getResources());

                            ArrayList<NameValuePair> params = new ArrayList<>();
                            params.add(new BasicNameValuePair("RESPONSE_CODE", "0"));
                            params.add(new BasicNameValuePair("INAPP_PURCHASE_DATA", "{\"orderId\":\"GPA.1300-2936-1155-52109\",\"packageName\":\"com.mattandmikeandscott.richpersonleaderboard\",\"productId\":\"com.mattandmikeandscott.richpersonleaderboard.purchaseone\",\"purchaseTime\":1459135485760,\"purchaseState\":0,\"developerPayload\":\"{'googleId': '108548037662688301318'}\",\"purchaseToken\":\"nghdfnifojifhnijpecknkeo.AO-J1Ozi56BN8xfKJABer8OsFwxurDK6TApP5bhKzrkAinOUOV2Nuv5yYm5nByn4YkdzqyQPyGzfmFRcXIJ3dHXij3CsCRS_koicXQn8oIGpqgMmxSRtUFzKVBcoHzbYvMDDstUvFFCOzZGTHBTSaXwhKywjLS2aFIDyl2oHsNrg9NRC5oiMI2gB_63nkYZWnkmwxuG6wp6ILwpOG2QltbD0SO_YnEu6aA\"}"));
                            params.add(new BasicNameValuePair("INAPP_DATA_SIGNATURE", "bko5q5OFWd/KRF3nCUKDl3PSwR79OD7uwNBf5BQ/CDN+0tR0/G3FsmdEHD7zbkndb492mL4lfh50app7jOPyMEvT42uaElzVpoHhN7s57odzsF1zuMV0knoNVoWadfOkDtvAvKNpa4Cw+EqqOWYC5JzLahR8LsT4X6nWOHlM7ZGrTDw+gqLElZ0K6NRuv3Ro9qXj4RV568WXPJl5VIlw3l529vUPoLnRwrvLxkm1nG5IV/bK1MOu1hsvSJpt++XwnjfmLSJEi8Hrg3nRutvOgtLgzb7rbhXM25O6Wxcsq9RTW1MNCpt2+KwxM//EXVjFXrHkBjpvolKvUXRGmmQ8ow=="));

                            final JSONArray results = repo.recordPurchase(params);

                            mainActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mainActivity, results.toString(), Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }).start();

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

        final Spinner spinner = (Spinner) layout.findViewById(R.id.purchaseTypeSpinner);

        layout.findViewById(R.id.purchase_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String purchasePackageName = Constants.PURCHASE_PACKAGE_NAMES[spinner.getSelectedItemPosition()];
                mainActivity.getInAppPurchaseHelper().buyMoney(mainActivity.getBillingService(), dialog, purchasePackageName);

                dialog.dismiss();
            }
        });

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
