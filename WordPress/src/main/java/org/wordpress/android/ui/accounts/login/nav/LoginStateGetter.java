package org.wordpress.android.ui.accounts.login.nav;

public interface LoginStateGetter {
    interface FsmGetter {
        LoginStateGetter getLoginStateGetter();
    }

    LoginNav.Prologue getLoginNavPrologue();
    LoginNav.InputEmail getLoginNavInputEmail();
    LoginNav.RequestMagicLink getLoginNavRequestMagicLink();
    LoginNav.SentMagicLink getLoginNavSentMagicLink();
    LoginNav.InputSiteAddress getLoginNavInputSiteAddress();
}
