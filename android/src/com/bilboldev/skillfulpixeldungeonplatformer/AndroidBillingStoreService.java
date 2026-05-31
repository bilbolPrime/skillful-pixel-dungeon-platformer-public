package com.bilboldev.skillfulpixeldungeonplatformer;

import android.app.Activity;
import android.content.res.Resources;
import android.util.Log;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;
import com.badlogic.gdx.Gdx;
import com.bilboldev.skillfulpixeldungeonplatformer.achievements.AchievementManager;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RatKingSupportHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.WindowHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.platform.StoreService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

public class AndroidBillingStoreService implements StoreService, PurchasesUpdatedListener {
    private static final String TAG = "AndroidBillingStore";
    private static final String RESOURCE_TYPE = "string";
    private static final String RAT_KING_PRODUCT_RESOURCE = "rat_king_donation_product_id";

    private final Activity activity;
    private final List<String> ratKingProductIds;
    private BillingClient billingClient;
    private boolean ready;
    private boolean purchaseLaunchPending;
    private boolean ratKingDonationOwned;
    private boolean pendingOwnershipGrant;
    private boolean pendingOwnershipFeedback;

    public AndroidBillingStoreService(Activity activity) {
        this.activity = activity;
        this.ratKingProductIds = resolveConfiguredProductIds();
        billingClient = BillingClient.newBuilder(activity)
                .setListener(this)
                .enablePendingPurchases()
                .build();
        connect();
    }

    @Override
    public boolean supportsRatKingDonation() {
        return !ratKingProductIds.isEmpty();
    }

    @Override
    public boolean isRatKingDonationOwned() {
        return ratKingDonationOwned || RatKingSupportHelper.getInstance().isMobileDonationUnlocked();
    }

    @Override
    public void syncRatKingDonationOwnership() {
        if (!supportsRatKingDonation()) {
            return;
        }

        if (!ready) {
            connect();
            return;
        }

        QueryPurchasesParams params = QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build();

        billingClient.queryPurchasesAsync(params, (billingResult, purchases) -> {
            if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK || purchases == null) {
                Log.w(TAG, "Failed to query purchases: " + billingResult.getResponseCode()
                        + " (" + billingResult.getDebugMessage() + ")");
                return;
            }

            for (Purchase purchase : purchases) {
                handlePurchase(purchase, false);
            }
        });
    }

    @Override
    public void purchaseRatKingDonation() {
        if (!supportsRatKingDonation()) {
            Log.w(TAG, "Rat King donation product ID is not configured.");
            return;
        }

        if (!ready) {
            purchaseLaunchPending = true;
            connect();
            return;
        }

        List<QueryProductDetailsParams.Product> products = new ArrayList<>();
        for (String productId : ratKingProductIds) {
            QueryProductDetailsParams.Product product = QueryProductDetailsParams.Product.newBuilder()
                .setProductId(productId)
                .setProductType(BillingClient.ProductType.INAPP)
                .build();
            products.add(product);
        }

        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
            .setProductList(products)
                .build();

        billingClient.queryProductDetailsAsync(params, (billingResult, productDetailsList) -> {
            if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK || productDetailsList == null || productDetailsList.isEmpty()) {
                Log.w(TAG, "Unable to load product details for Rat King donation: "
                        + billingResult.getResponseCode() + " (" + billingResult.getDebugMessage() + ")"
                        + " for IDs " + ratKingProductIds);
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    showBillingMessage(Messages.get("custom.ui.rat_king.billing_offer_unavailable"));
                } else {
                    showBillingMessage(Messages.get(
                            "custom.ui.rat_king.billing_purchase_failed",
                            new Object[]{billingResult.getResponseCode()}));
                }
                return;
            }

            ProductDetails details = findSupportedProductDetails(productDetailsList);
            if (details == null) {
                Log.w(TAG, "Play returned product details, but none matched configured Rat King IDs: " + ratKingProductIds);
                showBillingMessage(Messages.get("custom.ui.rat_king.billing_offer_unavailable"));
                return;
            }

            BillingFlowParams.ProductDetailsParams productDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(details)
                    .build();
            BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(Collections.singletonList(productDetailsParams))
                    .build();
            BillingResult launchResult = billingClient.launchBillingFlow(activity, flowParams);
            if (launchResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
                Log.i(TAG, "Rat King donation is already owned; refreshing local ownership state.");
                syncRatKingDonationOwnership();
            } else if (launchResult.getResponseCode() != BillingClient.BillingResponseCode.OK) {
                Log.w(TAG, "Failed to launch Rat King donation flow: " + launchResult.getResponseCode()
                        + " (" + launchResult.getDebugMessage() + ")");
                showBillingMessage(Messages.get(
                        "custom.ui.rat_king.billing_purchase_failed",
                        new Object[]{launchResult.getResponseCode()}));
            }
        });
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            Log.i(TAG, "Rat King donation was already owned during purchase flow; refreshing local ownership state.");
            syncRatKingDonationOwnership();
            return;
        }

        if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK || purchases == null) {
            Log.d(TAG, "Purchases update ignored: " + billingResult.getResponseCode());
            return;
        }

        for (Purchase purchase : purchases) {
            handlePurchase(purchase, true);
        }
    }

    @Override
    public void update() {
        applyPendingOwnershipGrant();
    }

    @Override
    public void onDispose() {
        if (billingClient != null) {
            billingClient.endConnection();
            billingClient = null;
        }
        ready = false;
    }

    private void connect() {
        if (billingClient == null || ready) {
            return;
        }

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                ready = billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK;
                if (!ready) {
                    Log.w(TAG, "Billing setup failed: " + billingResult.getResponseCode()
                            + " (" + billingResult.getDebugMessage() + ")");
                    if (purchaseLaunchPending) {
                        purchaseLaunchPending = false;
                        showBillingMessage(Messages.get(
                                "custom.ui.rat_king.billing_purchase_failed",
                                new Object[]{billingResult.getResponseCode()}));
                    }
                    return;
                }

                syncRatKingDonationOwnership();
                if (purchaseLaunchPending) {
                    purchaseLaunchPending = false;
                    purchaseRatKingDonation();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                ready = false;
            }
        });
    }

    private void handlePurchase(Purchase purchase, boolean showFeedback) {
        String matchedProductId = findOwnedRatKingProductId(purchase);
        if (purchase == null || matchedProductId == null) {
            return;
        }

        if (purchase.getPurchaseState() != Purchase.PurchaseState.PURCHASED) {
            Log.d(TAG, "Rat King donation purchase is pending for product " + matchedProductId + ".");
            return;
        }

        ratKingDonationOwned = true;
        grantOwnedDonation(showFeedback);

        if (!purchase.isAcknowledged()) {
            AcknowledgePurchaseParams acknowledgeParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.getPurchaseToken())
                    .build();
            billingClient.acknowledgePurchase(acknowledgeParams,
                    billingResult -> Log.d(TAG, "Acknowledge result: " + billingResult.getResponseCode()));
        }
    }

    private List<String> resolveConfiguredProductIds() {
        Resources resources = activity.getResources();
        int resId = resources.getIdentifier(RAT_KING_PRODUCT_RESOURCE, RESOURCE_TYPE, activity.getPackageName());
        if (resId == 0) {
            Log.w(TAG, "Missing store resource: " + RAT_KING_PRODUCT_RESOURCE);
            return Collections.emptyList();
        }

        String configuredIds = resources.getString(resId);
        if (configuredIds == null) {
            return Collections.emptyList();
        }

        LinkedHashSet<String> productIds = new LinkedHashSet<>();
        for (String rawProductId : configuredIds.split(",")) {
            String productId = rawProductId.trim();
            if (productId.isEmpty() || productId.startsWith("REPLACE_WITH_")) {
                continue;
            }
            productIds.add(productId);
        }

        return new ArrayList<>(productIds);
    }

    private ProductDetails findSupportedProductDetails(List<ProductDetails> productDetailsList) {
        for (String configuredProductId : ratKingProductIds) {
            for (ProductDetails productDetails : productDetailsList) {
                if (configuredProductId.equals(productDetails.getProductId())) {
                    return productDetails;
                }
            }
        }

        return null;
    }

    private String findOwnedRatKingProductId(Purchase purchase) {
        if (purchase == null || purchase.getProducts() == null) {
            return null;
        }

        for (String configuredProductId : ratKingProductIds) {
            if (purchase.getProducts().contains(configuredProductId)) {
                return configuredProductId;
            }
        }

        return null;
    }

    private void grantOwnedDonation(boolean showFeedback) {
        if (Gdx.app == null) {
            pendingOwnershipGrant = true;
            pendingOwnershipFeedback |= showFeedback;
            return;
        }

        RatKingSupportHelper.getInstance().setMobileDonationUnlocked(true);
        final boolean feedback = showFeedback;
        Gdx.app.postRunnable(() -> AchievementManager.getInstance().applySupporterOwnership(feedback));
    }

    private void applyPendingOwnershipGrant() {
        if (!pendingOwnershipGrant || Gdx.app == null) {
            return;
        }

        boolean showFeedback = pendingOwnershipFeedback;
        pendingOwnershipGrant = false;
        pendingOwnershipFeedback = false;
        grantOwnedDonation(showFeedback);
    }

    private void showBillingMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            return;
        }

        if (Gdx.app == null) {
            Log.w(TAG, "Unable to show billing message because Gdx.app is not ready: " + message);
            return;
        }

        Gdx.app.postRunnable(() -> WindowHelper.getInstance().addWindow(1100f, 180f, message));
    }
}