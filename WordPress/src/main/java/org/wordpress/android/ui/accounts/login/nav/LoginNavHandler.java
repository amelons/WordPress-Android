package org.wordpress.android.ui.accounts.login.nav;

public interface LoginNavHandler {
    void toast(String message);

    void showEmailLoginScreen();
    void showMagicLinkRequestScreen(String email);
    void showMagicLinkSentScreen(String email);
}