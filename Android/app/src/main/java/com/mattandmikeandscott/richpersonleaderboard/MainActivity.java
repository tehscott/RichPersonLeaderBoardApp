package com.mattandmikeandscott.richpersonleaderboard;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.mattandmikeandscott.richpersonleaderboard.adapters.PersonListAdapter;
import com.mattandmikeandscott.richpersonleaderboard.adapters.SectionsPagerAdapter;
import com.mattandmikeandscott.richpersonleaderboard.domain.GetPeopleResponse;
import com.mattandmikeandscott.richpersonleaderboard.domain.MainActivityHandlerResult;
import com.mattandmikeandscott.richpersonleaderboard.helpers.MainHelper;
import com.mattandmikeandscott.richpersonleaderboard.helpers.SignInHelper;

public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private MainHelper mainHelper;

    private SignInHelper signInHelper;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainHelper = new MainHelper(this);
        signInHelper = new SignInHelper(this);

        MainHelper.IS_DEBUG = !mainHelper.isAppSigned();

        if(MainHelper.IS_DEBUG) {
            Toast.makeText(MainActivity.this, "Running in debug mode.", Toast.LENGTH_LONG).show();
        }

        mainHelper.setupActionBar();
        mainHelper.setupButtons();
        mainHelper.setupViewPager();

        signInHelper.setupSignIn();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SignInHelper.RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            signInHelper.handleSignInResult(result);
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }
    @Override public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {}
    @Override public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {}

    public ViewPager getViewPager() {
        return mViewPager;
    }
    public void setSectionsPagerAdapter(SectionsPagerAdapter sectionsPagerAdapter) {
        this.mSectionsPagerAdapter = sectionsPagerAdapter;
    }
    public void setViewPager(ViewPager viewPager) {
        this.mViewPager = viewPager;
    }
    public SignInHelper getSignInHelper() {
        return signInHelper;
    }
    public SectionsPagerAdapter getSectionsPagerAdapter() {
        return mSectionsPagerAdapter;
    }

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == MainActivityHandlerResult.PEOPLE_INFO_AQUIRED.ordinal()) {
                GetPeopleResponse response = (GetPeopleResponse) msg.obj;

                response.getList().setAdapter(new PersonListAdapter(response.getContext(), response.getPeople(), response.getPeopleQueryType(), response.getRankType()));

                final SwipeRefreshLayout swipeLayout = (SwipeRefreshLayout) response.getList().getParent();
                swipeLayout.setRefreshing(false);
                swipeLayout.setEnabled(true);
            }
        }
    };
}