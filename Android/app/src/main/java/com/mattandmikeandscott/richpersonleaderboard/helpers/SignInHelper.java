package com.mattandmikeandscott.richpersonleaderboard.helpers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.mattandmikeandscott.richpersonleaderboard.MainActivity;
import com.mattandmikeandscott.richpersonleaderboard.R;

public class SignInHelper implements GoogleApiClient.OnConnectionFailedListener {
    private MainActivity mainActivity;
    private GoogleApiClient mGoogleApiClient;
    public static int RC_SIGN_IN = 1000;

    public SignInHelper(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void setupSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(mainActivity)
                .enableAutoManage(mainActivity, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    public void signIn() {
        if(getId() == null) {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            mainActivity.startActivityForResult(signInIntent, RC_SIGN_IN);
        }
    }

    public boolean isSignedIn() {
        return getId() != null;
    }

    public void handleSignInResult(GoogleSignInResult result) {
        Log.d(mainActivity.getString(R.string.app_short_name), "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            setId(acct.getId());

            Toast.makeText(mainActivity, "Signed in!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mainActivity, "Failed to sign in!", Toast.LENGTH_SHORT).show();
        }
    }

    public String getId() {
        if(MainHelper.IS_DEBUG) {
            return "115945239127508307789";
        }

        SharedPreferences settings = mainActivity.getSharedPreferences(mainActivity.getString(R.string.app_short_name), Context.MODE_PRIVATE);

        return settings.getString("personId", null);
    }

    public void setId(String id) {
        SharedPreferences sharedPreferences = mainActivity.getSharedPreferences(mainActivity.getString(R.string.app_short_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("personId", id);
        editor.commit();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}
}
