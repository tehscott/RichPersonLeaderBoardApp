package com.mattandmikeandscott.richpersonleaderboard;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.gson.Gson;
import com.mattandmikeandscott.richpersonleaderboard.adapters.PersonListAdapter;
import com.mattandmikeandscott.richpersonleaderboard.adapters.SectionsPagerAdapter;
import com.mattandmikeandscott.richpersonleaderboard.domain.Constants;
import com.mattandmikeandscott.richpersonleaderboard.domain.GetPeopleResponse;
import com.mattandmikeandscott.richpersonleaderboard.domain.MainActivityHandlerResult;
import com.mattandmikeandscott.richpersonleaderboard.helpers.InAppPurchaseHelper;
import com.mattandmikeandscott.richpersonleaderboard.helpers.MainHelper;
import com.mattandmikeandscott.richpersonleaderboard.helpers.SignInHelper;
import com.mattandmikeandscott.richpersonleaderboard.network.Repository;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.ArrayList;

public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private MainHelper mainHelper;
    private InAppPurchaseHelper mInAppPurchaseHelper;
    private Repository mRepository;

    private SignInHelper signInHelper;
    private ViewPager mViewPager;

    private IInAppBillingService mBillingService;
    public ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBillingService = null;
            handler.sendEmptyMessage(MainActivityHandlerResult.BILLING_SERVICE_CONNECTED.ordinal());
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBillingService = IInAppBillingService.Stub.asInterface(service);
            handler.sendEmptyMessage(MainActivityHandlerResult.BILLING_SERVICE_CONNECTED.ordinal());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        mainHelper = new MainHelper(this);
        signInHelper = new SignInHelper(this);
        mInAppPurchaseHelper = new InAppPurchaseHelper(this);
        mRepository = new Repository(getResources());

        MainHelper.IS_DEBUG = !mainHelper.isAppSigned();

        if(MainHelper.IS_DEBUG) {
            Toast.makeText(MainActivity.this, "Running in debug mode.", Toast.LENGTH_LONG).show();
            handler.sendEmptyMessage(MainActivityHandlerResult.BILLING_SERVICE_CONNECTED.ordinal());
        }

        mainHelper.setupActionBar();
        mainHelper.setupButtons();
        mainHelper.setupViewPager();

        signInHelper.setupSignIn();

        mInAppPurchaseHelper.initService();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SignInHelper.RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            signInHelper.handleSignInResult(result);
        } else if(requestCode == Constants.ACTIVITY_RESULT_PURCHASE) {
            final int responseCode = data.getIntExtra("RESPONSE_CODE", 6);
            final String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
            final String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");

            if (resultCode == RESULT_OK && responseCode == 0) {
                try {
                    JSONObject jo = new JSONObject(purchaseData);
                    String productId = jo.getString("productId");

                    Toast.makeText(MainActivity.this, "Purchase complete!", Toast.LENGTH_SHORT).show();

                    final ArrayList<NameValuePair> params = new ArrayList<>();
                    params.add(new BasicNameValuePair("RESPONSE_CODE", String.valueOf(responseCode)));
                    params.add(new BasicNameValuePair("INAPP_PURCHASE_DATA", purchaseData));
                    params.add(new BasicNameValuePair("INAPP_DATA_SIGNATURE", dataSignature));

                    Log.i(getString(R.string.app_short_name), new Gson().toJson(params));

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jsonObject = new JSONObject(purchaseData);
                                String purchasesToken = jsonObject.getString("purchaseToken");
                                mInAppPurchaseHelper.consumePurchase(mBillingService, purchasesToken);
                            } catch(JSONException|RemoteException e) {
                                Toast.makeText(MainActivity.this, "Error consuming purchase. Login again to fix.", Toast.LENGTH_LONG).show();
                            }

                            final JSONArray results = mRepository.recordPurchase(params);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, results.toString(), Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }).start();
                } catch (JSONException e) {
                    //alert("Failed to parse purchase data.");
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(MainActivity.this, "Purchase failed (response code " + responseCode + "). Please try again.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBillingService != null) {
            unbindService(mServiceConn);
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
    public InAppPurchaseHelper getInAppPurchaseHelper() {
        return mInAppPurchaseHelper;
    }
    public Repository getRepository() {
        return mRepository;
    }

    public IInAppBillingService getBillingService() {
        return mBillingService;
    }

    public SectionsPagerAdapter getSectionsPagerAdapter() {
        return mSectionsPagerAdapter;
    }

    public MainHelper getMainHelper() {
        return mainHelper;
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
            } else if(msg.what == MainActivityHandlerResult.BILLING_SERVICE_CONNECTED.ordinal()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(mBillingService != null || MainHelper.IS_DEBUG) {
                            getSupportActionBar().show();
                            findViewById(R.id.activity_main_container).setVisibility(View.VISIBLE);
                            findViewById(R.id.activity_main_loading_container).setVisibility(View.GONE);
                            //Toast.makeText(MainActivity.this, "Connected to Play Services", Toast.LENGTH_LONG).show();
                        } else {
                            // TODO: Show an error
                            Toast.makeText(MainActivity.this, "Error connecting to Play Services", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            } else if(msg.what == MainActivityHandlerResult.SIGNED_IN.ordinal()) {
                mInAppPurchaseHelper.consumeConsumables();
            }
        }
    };
}