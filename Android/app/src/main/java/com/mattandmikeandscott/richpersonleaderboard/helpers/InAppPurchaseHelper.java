package com.mattandmikeandscott.richpersonleaderboard.helpers;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.mattandmikeandscott.richpersonleaderboard.MainActivity;
import com.mattandmikeandscott.richpersonleaderboard.R;
import com.mattandmikeandscott.richpersonleaderboard.domain.Constants;
import com.mattandmikeandscott.richpersonleaderboard.domain.InAppPurchase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
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

    public void buyMoney(IInAppBillingService service, Dialog dialog, String purchasePackageName) {
        if(Constants.IS_DEBUG) {
            dialog.dismiss();
        } else {
            final String developerPayload = "{'googleId': '" + mMainActivity.getSignInHelper().getId() + "'}";

            try {
                Bundle buyIntentBundle = service.getBuyIntent(3, mMainActivity.getPackageName(), purchasePackageName, "inapp", developerPayload);
                PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");

                if(pendingIntent != null) {
                    mMainActivity.startIntentSenderForResult(pendingIntent.getIntentSender(),
                        Constants.ACTIVITY_RESULT_PURCHASE, new Intent(), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0));
                } else {
                    dialog.dismiss();
                    Toast.makeText(mMainActivity, "Error purchasing (duplicate purchase)", Toast.LENGTH_LONG).show();
                }
            } catch (RemoteException e) {
                e.printStackTrace(); // TODO:
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace(); // TODO:
            }
        }
    }

    /*
     * All calls will give a response code with the following possible values
     * RESULT_OK = 0 - success
     * RESULT_USER_CANCELED = 1 - user pressed back or canceled a dialog
     * RESULT_BILLING_UNAVAILABLE = 3 - this billing API version is not supported for the type requested
     * RESULT_ITEM_UNAVAILABLE = 4 - requested SKU is not available for purchase
     * RESULT_DEVELOPER_ERROR = 5 - invalid arguments provided to the API
     * RESULT_ERROR = 6 - Fatal error during the API action
     * RESULT_ITEM_ALREADY_OWNED = 7 - Failure to purchase since item is already owned
     * RESULT_ITEM_NOT_OWNED = 8 - Failure to consume since item is not owned
     *
     *     * @return Bundle containing the following key-value pairs
     *         "RESPONSE_CODE" with int value, RESULT_OK(0) if success, other response codes on
     *              failure as listed above.
     *         "INAPP_PURCHASE_ITEM_LIST" - StringArrayList containing the list of SKUs
     *         "INAPP_PURCHASE_DATA_LIST" - StringArrayList containing the purchase information
     *         "INAPP_DATA_SIGNATURE_LIST"- StringArrayList containing the signatures
     *                                      of the purchase information
     *         "INAPP_CONTINUATION_TOKEN" - String containing a continuation token for the
     *                                      next set of in-app purchases. Only set if the
     *                                      user has more owned skus than the current list.
     */
    public ArrayList<InAppPurchase> getConsumables(IInAppBillingService service, ArrayList<InAppPurchase> consumables, String continuationToken) throws RemoteException {
        Bundle purchases = service.getPurchases(3, "com.mattandmikeandscott.richpersonleaderboard", "inapp", continuationToken);

        int responseCode = purchases.getInt("RESPONSE_CODE");

        if(responseCode == 0) {
            continuationToken = purchases.getString("INAPP_CONTINUATION_TOKEN");
            ArrayList<String> items = purchases.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
            ArrayList<String> data = purchases.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
            ArrayList<String> signatures = purchases.getStringArrayList("INAPP_DATA_SIGNATURE_LIST");

            for(int i = 0; i < items.size(); i++) {
                consumables.add(new InAppPurchase(items.get(i), data.get(i), signatures.get(i)));
            }

            if(continuationToken != null && !continuationToken.equals("")) {
                getConsumables(service, consumables, continuationToken);
            }

            return consumables;
        }

        throw new RemoteException("Failed to get consumables with response code " + responseCode + ".");
    }

    public int consumePurchase(IInAppBillingService service, String purchaseToken) throws RemoteException {
        Log.i(mMainActivity.getString(R.string.app_short_name), "Consuming " + purchaseToken);
        //return 0;
        return service.consumePurchase(3, "com.mattandmikeandscott.richpersonleaderboard", purchaseToken);
    }

    public void consumeConsumables() {
        Log.i(mMainActivity.getString(R.string.app_short_name), "Checking for consumables.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<InAppPurchase> consumables = new ArrayList<>();
                try {
                    consumables = mMainActivity.getInAppPurchaseHelper().getConsumables(mMainActivity.getBillingService(), consumables, null);

                    for(InAppPurchase consumable : consumables) {
                        try {
                            JSONObject jsonObject = new JSONObject(consumable.getPurchaseData());
                            String purchasesToken = jsonObject.getString("purchaseToken");

                            mMainActivity.getInAppPurchaseHelper().consumePurchase(mMainActivity.getBillingService(), purchasesToken);
                        } catch(JSONException e) {
                            Log.e(mMainActivity.getString(R.string.app_short_name), e.toString());
                        }
                    }
                } catch (RemoteException e) {
                    e.printStackTrace(); // TODO: Handle this
                    Log.e(mMainActivity.getString(R.string.app_short_name), e.toString());
                }
            }
        }).start();
    }
}
