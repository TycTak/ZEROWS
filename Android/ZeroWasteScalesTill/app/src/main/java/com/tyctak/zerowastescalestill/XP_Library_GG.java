package com.tyctak.zerowastescalestill;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.Nullable;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import org.json.JSONException;
import java.util.List;
import static com.android.billingclient.api.BillingClient.SkuType.*;

public class XP_Library_GG implements PurchasesUpdatedListener {

    private String TAG = "XP_Library_GG";

    private BillingClient mBillingClient;
    private Activity parent;
    private PurchaseResponseListener listener;

    public boolean isReady() {
        return (mBillingClient != null ? mBillingClient.isReady() : false);
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {

    }

    public Boolean isPurchased(String skuId) {
        Purchase.PurchasesResult result = mBillingClient.queryPurchases(SUBS);
        List<Purchase> purchases = result.getPurchasesList();

        boolean isPurchased = false;

        if (isReady()) {
            if (result.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                for (int i = 0; i < purchases.size(); ++i) {
                    String sku = purchases.get(i).getSku();
                    if (sku.equals(skuId)) {
                        isPurchased = true;
                        break;
                    }
                }
            }
        }

        return isPurchased;
    }

    public interface PurchaseResponseListener {
        void onPurchaseResponse(final int responseCode);
    }

    public boolean isNull() {
        return (mBillingClient == null);
    }

    public void endConnection() {
        if (mBillingClient != null) {
            if (mBillingClient.isReady()) mBillingClient.endConnection();
            mBillingClient = null;
        }
    }

    public XP_Library_GG(Activity pParent) {
        parent = pParent;

        mBillingClient = BillingClient.newBuilder(MyApp.getAppContext()).setListener(this).enablePendingPurchases().build();

        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        });
    }

    public BillingResult buyListener(Context parent, String skuDetailsId, PurchaseResponseListener pListener) throws JSONException {
        listener = pListener;

        SkuDetails skuDetails = new SkuDetails(skuDetailsId);

        BillingFlowParams purchaseParams = BillingFlowParams.newBuilder().setSkuDetails(skuDetails).build();
        BillingResult responseCode = mBillingClient.launchBillingFlow((Activity) parent, purchaseParams);

        return responseCode;
    }

    public void buyProduct(final String skuDetailsId, final Activity parent, final Runnable successCallback, final Runnable failureCallback) throws JSONException {
        BillingResult responseCode = buyListener(parent, skuDetailsId, new PurchaseResponseListener() {
            @Override
            public void onPurchaseResponse(final int responseCode) {
                if (responseCode == BillingClient.BillingResponseCode.OK) {
                    // buy worked correctly
                    if (successCallback != null) successCallback.run();
                } else {
                    // buy failed to work
                    if (failureCallback != null) failureCallback.run();
                }
            }
        });
    }
}