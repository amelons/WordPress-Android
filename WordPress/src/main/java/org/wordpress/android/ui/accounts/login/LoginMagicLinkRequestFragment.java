package org.wordpress.android.ui.accounts.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.wordpress.android.R;
import org.wordpress.android.WordPress;
import org.wordpress.android.fluxc.Dispatcher;
import org.wordpress.android.fluxc.generated.AuthenticationActionBuilder;
import org.wordpress.android.fluxc.store.AccountStore;
import org.wordpress.android.ui.accounts.login.nav.LoginNav;
import org.wordpress.android.ui.accounts.login.nav.LoginStateGetter;
import org.wordpress.android.util.AppLog;
import org.wordpress.android.util.NetworkUtils;
import org.wordpress.android.util.ToastUtils;

import javax.inject.Inject;

public class LoginMagicLinkRequestFragment extends Fragment {
    public static final String TAG = "login_magic_link_request_fragment_tag";

    private static final String KEY_IN_PROGRESS = "KEY_IN_PROGRESS";
    private static final String ARG_EMAIL_ADDRESS = "arg_email_address";

    private LoginNav.RequestMagicLink mLoginNavRequestMagicLink;

    private String mEmail;

    private Button mRequestMagicLinkButton;
    private ProgressDialog mProgressDialog;

    protected @Inject Dispatcher mDispatcher;

    public static LoginMagicLinkRequestFragment newInstance(String email) {
        LoginMagicLinkRequestFragment fragment = new LoginMagicLinkRequestFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EMAIL_ADDRESS, email);
        fragment.setArguments(args);
        return fragment;
    }

    public LoginMagicLinkRequestFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((WordPress) getActivity().getApplication()).component().inject(this);

        if (getArguments() != null) {
            mEmail = getArguments().getString(ARG_EMAIL_ADDRESS);
        }

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_magic_link_request_screen, container, false);
        mRequestMagicLinkButton = (Button) view.findViewById(R.id.login_request_magic_link);
        mRequestMagicLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLoginNavRequestMagicLink != null) {
                    if (!NetworkUtils.isNetworkAvailable(getActivity())) {
                        ToastUtils.showToast(getActivity(), R.string.no_network_message, ToastUtils.Duration.LONG);
                        return;
                    }

                    showMagiclinkRequestProgressDialog();
                    mDispatcher.dispatch(AuthenticationActionBuilder.newSendAuthEmailAction(mEmail));
                }
            }
        });

        view.findViewById(R.id.login_enter_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoginNavRequestMagicLink.usePasswordInstead(mEmail);
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState != null) {
            boolean isInProgress = savedInstanceState.getBoolean(KEY_IN_PROGRESS);
            if (isInProgress) {
                showMagiclinkRequestProgressDialog();
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LoginStateGetter.FsmGetter) {
            mLoginNavRequestMagicLink = ((LoginStateGetter.FsmGetter) context).getLoginStateGetter()
                    .getLoginNavRequestMagicLink();
        } else {
            throw new RuntimeException(context.toString() + " must implement LoginStateGetter.FsmGetter");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mLoginNavRequestMagicLink = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(KEY_IN_PROGRESS, mProgressDialog != null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_login, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.help) {
            mLoginNavRequestMagicLink.help();
            return true;
        }

        return false;
    }

    @Override
    public void onStart() {
        super.onStart();
        mDispatcher.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        mDispatcher.unregister(this);
    }

    private void showMagiclinkRequestProgressDialog() {
        startProgress(getString(R.string.login_magic_link_email_requesting));
    }

    protected void startProgress(String message) {
        mRequestMagicLinkButton.setEnabled(false);
        mProgressDialog = ProgressDialog.show(getActivity(), "", message, true, true,
                new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        mRequestMagicLinkButton.setEnabled(true);
                    }
                });
    }

    protected void endProgress() {
        if (mProgressDialog != null) {
            mProgressDialog.cancel();
        }

        // nullify the reference to denote there is no operation in progress
        mProgressDialog = null;

        mRequestMagicLinkButton.setEnabled(true);
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAuthEmailSent(AccountStore.OnAuthEmailSent event) {
        endProgress();

        if (event.isError()) {
            AppLog.e(AppLog.T.API, "OnAuthEmailSent has error: " + event.error.type + " - " + event.error.message);
            ToastUtils.showToast(getActivity(), R.string.magic_link_unavailable_error_message, ToastUtils.Duration.LONG);
        } else {
            mLoginNavRequestMagicLink.magicLinkRequestSent(mEmail);
        }
    }
}
