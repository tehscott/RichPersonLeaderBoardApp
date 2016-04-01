package com.mattandmikeandscott.richpersonleaderboard.domain;

public class InAppPurchase {
    /*
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

    private String sku;
    private String purchaseData;
    private String signature;

    public InAppPurchase(String sku, String purchaseData, String signature) {
        this.sku = sku;
        this.purchaseData = purchaseData;
        this.signature = signature;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getPurchaseData() {
        return purchaseData;
    }

    public void setPurchaseData(String purchaseData) {
        this.purchaseData = purchaseData;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
