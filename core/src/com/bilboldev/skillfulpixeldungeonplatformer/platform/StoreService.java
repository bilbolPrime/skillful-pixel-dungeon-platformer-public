package com.bilboldev.skillfulpixeldungeonplatformer.platform;

public interface StoreService {

    default boolean supportsRatKingDonation() {
        return false;
    }

    default boolean isRatKingDonationOwned() {
        return false;
    }

    default void syncRatKingDonationOwnership() {
    }

    default void purchaseRatKingDonation() {
    }

    default void update() {
    }

    default void onDispose() {
    }
}