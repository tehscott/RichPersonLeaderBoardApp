package com.mattandmikeandscott.richpersonleaderboard.helpers;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.RemoteException;

import com.android.vending.billing.IInAppBillingService;
import com.mattandmikeandscott.richpersonleaderboard.MainActivity;
import com.mattandmikeandscott.richpersonleaderboard.domain.Constants;

import java.util.UUID;

public class InAppPurchaseHelper {
    private MainActivity mMainActivity;

    public InAppPurchaseHelper(MainActivity context) {
        mMainActivity = context;
    }

    public void initService() {
        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        mMainActivity.startService(serviceIntent);
        mMainActivity.bindService(serviceIntent, mMainActivity.mServiceConn, Context.BIND_AUTO_CREATE);
    }

    public void buyPro(IInAppBillingService service, Dialog dialog) {
        if(Constants.IS_DEBUG) {
            dialog.dismiss();
            mMainActivity.onActivityResult(Constants.ACTIVITY_RESULT_PURCHASE_DEBUG, Constants.ACTIVITY_RESULT_PURCHASE_DEBUG, null);
        } else {
            final String  uniqueID = UUID.randomUUID().toString();

            try {
                Bundle buyIntentBundle = service.getBuyIntent(3, mMainActivity.getPackageName(), Constants.PURCHASE_ONE_PACKAGE_NAME, "inapp", uniqueID);
                PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
                mMainActivity.startIntentSenderForResult(pendingIntent.getIntentSender(),
                        Constants.ACTIVITY_RESULT_PURCHASE, new Intent(), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0));
            } catch (RemoteException e) {
                e.printStackTrace(); // TODO:
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace(); // TODO:
            }
        }
    }
}
