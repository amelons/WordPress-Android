package org.wordpress.android.ui.accounts.login.nav;

import java.util.Stack;

public class LoginNavController implements LoginStateGetter {

    private LoginNavHandler mLoginNavHandler;

    public LoginNavController(Class<? extends LoginNav> initialLoginNav) {
        mLoginNavStack.push(initialLoginNav);
    }

    public LoginNavController setLoginNavHandler(LoginNavHandler loginNavHandler) {
        mLoginNavHandler = loginNavHandler;

        return this;
    }

    private LoginNav newNavHandler(Class<? extends LoginNav> loginNav) {
        if (loginNav.isAssignableFrom(LoginNav.Prologue.class)) {
            return new PrologueHandler();
        }

        if (loginNav.isAssignableFrom(LoginNav.InputEmail.class)) {
            return new InputEmailHandler();
        }

        if (loginNav.isAssignableFrom(LoginNav.RequestMagicLink.class)) {
            return new RequestMagicLinkHandler();
        }

        if (loginNav.isAssignableFrom(LoginNav.SentMagicLink.class)) {
            return new SentMagicLinkHandler();
        }

        if (loginNav.isAssignableFrom(LoginNav.InputSiteAddress.class)) {
            return new InputSiteAddressHandler();
        }

        throw new RuntimeException("Unsupported login state " + loginNav.getSimpleName());
    }

    private Stack<Class<? extends LoginNav>> mLoginNavStack = new Stack<>();

    private boolean isInState(Class<? extends LoginNav> loginNav) {
        return !mLoginNavStack.empty() && loginNav.equals(mLoginNavStack.peek());
    }

    public void ensureState(Class<? extends LoginNav> loginNav) {
        if (!isInState(loginNav)) {
            throw new RuntimeException("Not in state " + loginNav.getSimpleName());
        }
    }

    private void gotoState(Class<? extends LoginNav> loginNav) {
        if (!isInState(loginNav)) {
            mLoginNavStack.push(loginNav);
        }
    }

    public void goBack() {
        if (mLoginNavStack.isEmpty()) {
            throw new RuntimeException("Navigation stack is empty! Can't go back.");
        }

        mLoginNavStack.pop();
    }

    public boolean isNavStackEmpty() {
        return mLoginNavStack.isEmpty();
    }

    // available for testing purposes. Don't use otherwise
    public void force(Class<? extends LoginNav> loginNav) {
        mLoginNavStack.push(loginNav);
    }

    private class PrologueHandler implements LoginNav.Prologue {
        @Override
        public void doStartLogin() {
            ensureState(LoginNav.Prologue.class);
            gotoState(LoginNav.InputEmail.class);

            mLoginNavHandler.showEmailLoginScreen();
        }

        @Override
        public void doStartSignup() {
            ensureState(LoginNav.Prologue.class);
            gotoState(LoginNav.Prologue.class);

            mLoginNavHandler.toast("Signup is not implemented yet");
        }
    }

    private class InputEmailHandler implements LoginNav.InputEmail {
        @Override
        public void gotEmail(String email) {
            ensureState(LoginNav.InputEmail.class);
            gotoState(LoginNav.RequestMagicLink.class);

            mLoginNavHandler.showMagicLinkRequestScreen(email);
        }

        @Override
        public void loginViaUsernamePassword() {
            ensureState(LoginNav.InputEmail.class);
            gotoState(LoginNav.InputEmail.class);

            mLoginNavHandler.toast("Fall back to username/password is not implemented yet.");
        }

        @Override
        public void help() {
            ensureState(LoginNav.InputEmail.class);
            gotoState(LoginNav.InputEmail.class);

            mLoginNavHandler.toast("Help is not implemented yet.");
        }
    }

    private class RequestMagicLinkHandler implements LoginNav.RequestMagicLink {
        @Override
        public void magicLinkRequestSent(String email) {
            ensureState(LoginNav.RequestMagicLink.class);
            gotoState(LoginNav.SentMagicLink.class);

            mLoginNavHandler.showMagicLinkSentScreen(email);
        }

        @Override
        public void usePasswordInstead(String email) {
            ensureState(LoginNav.RequestMagicLink.class);
            gotoState(LoginNav.RequestMagicLink.class);

            mLoginNavHandler.toast("Fall back to password is not implemented yet. Email: " + email);
        }

        @Override
        public void help() {
            ensureState(LoginNav.RequestMagicLink.class);
            gotoState(LoginNav.RequestMagicLink.class);

            mLoginNavHandler.toast("Help is not implemented yet.");
        }
    }

    private class SentMagicLinkHandler implements LoginNav.SentMagicLink {
        @Override
        public void openEmailClient() {
            ensureState(LoginNav.SentMagicLink.class);
            gotoState(LoginNav.SentMagicLink.class);

            mLoginNavHandler.toast("Open email client is not implemented yet.");
        }

        @Override
        public void usePasswordInstead(String email) {
            ensureState(LoginNav.SentMagicLink.class);
            gotoState(LoginNav.SentMagicLink.class);

            mLoginNavHandler.toast("Fall back to password is not implemented yet. Email: " + email);
        }

        @Override
        public void help() {
            ensureState(LoginNav.SentMagicLink.class);
            gotoState(LoginNav.SentMagicLink.class);

            mLoginNavHandler.toast("Help is not implemented yet.");
        }
    }

    private class InputSiteAddressHandler implements LoginNav.InputSiteAddress {
        @Override
        public void gotSiteAddress(String siteAddress) {
            ensureState(LoginNav.InputSiteAddress.class);
            gotoState(LoginNav.InputSiteAddress.class);

            mLoginNavHandler.toast("Input site address is not implemented yet. Input site address: " + siteAddress);
        }
    }

    @Override
    public LoginNav.Prologue getLoginNavPrologue() {
        try {
            return (LoginNav.Prologue) newNavHandler(mLoginNavStack.peek());
        } catch (ClassCastException cce) {
            throw new RuntimeException("Not in state " + LoginNav.Prologue.class.getSimpleName());
        }
    }

    @Override
    public LoginNav.InputEmail getLoginNavInputEmail() {
        try {
            return (LoginNav.InputEmail) newNavHandler(mLoginNavStack.peek());
        } catch (ClassCastException cce) {
            throw new RuntimeException("Not in state " + LoginNav.InputEmail.class.getSimpleName());
        }
    }

    @Override
    public LoginNav.RequestMagicLink getLoginNavRequestMagicLink() {
        try {
            return (LoginNav.RequestMagicLink) newNavHandler(mLoginNavStack.peek());
        } catch (ClassCastException cce) {
            throw new RuntimeException("Not in state " + LoginNav.RequestMagicLink.class.getSimpleName());
        }
    }

    @Override
    public LoginNav.SentMagicLink getLoginNavSentMagicLink() {
        try {
            return (LoginNav.SentMagicLink) newNavHandler(mLoginNavStack.peek());
        } catch (ClassCastException cce) {
            throw new RuntimeException("Not in state " + LoginNav.SentMagicLink.class.getSimpleName());
        }
    }

    @Override
    public LoginNav.InputSiteAddress getLoginNavInputSiteAddress() {
        try {
            return (LoginNav.InputSiteAddress) newNavHandler(mLoginNavStack.peek());
        } catch (ClassCastException cce) {
            throw new RuntimeException("Not in state " + LoginNav.InputSiteAddress.class.getSimpleName());
        }
    }
}
