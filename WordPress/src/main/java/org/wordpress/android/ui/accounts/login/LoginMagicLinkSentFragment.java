package org.wordpress.android.ui.accounts.login;

import android.content.Context;
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

import org.wordpress.android.R;
import org.wordpress.android.ui.accounts.login.nav.LoginNav;
import org.wordpress.android.ui.accounts.login.nav.LoginStateGetter;

public class LoginMagicLinkSentFragment extends Fragment {
    public static final String TAG = "login_magic_link_sent_fragment_tag";

    private static final String ARG_EMAIL_ADDRESS = "arg_email_address";

    private LoginNav.SentMagicLink mLoginNavSentMagicLink;

    private String mEmail;

    public static LoginMagicLinkSentFragment newInstance(String email) {
        LoginMagicLinkSentFragment fragment = new LoginMagicLinkSentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EMAIL_ADDRESS, email);
        fragment.setArguments(args);
        return fragment;
    }

    public LoginMagicLinkSentFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mEmail = getArguments().getString(ARG_EMAIL_ADDRESS);
        }

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_magic_link_sent_fragment, container, false);

        view.findViewById(R.id.login_open_email_client).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLoginNavSentMagicLink != null) {
                    mLoginNavSentMagicLink.openEmailClient();
                }
            }
        });

        view.findViewById(R.id.login_enter_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoginNavSentMagicLink.usePasswordInstead(mEmail);
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
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LoginStateGetter.FsmGetter) {
            mLoginNavSentMagicLink = ((LoginStateGetter.FsmGetter) context).getLoginStateGetter()
                    .getLoginNavSentMagicLink();
        } else {
            throw new RuntimeException(context.toString() + " must implement LoginStateGetter.FsmGetter");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mLoginNavSentMagicLink = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_login, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.help) {
            mLoginNavSentMagicLink.help();
            return true;
        }

        return false;
    }
}
