package org.dhis2.mobile_uphmis.ui.fragments;

public interface IOnBackPressed {
    /**
     * If you return true the back press will not be taken into account, otherwise the activity will act naturally
     *
     * @return true if your processing has priority if not false
     */
    boolean onBackPressed();
}