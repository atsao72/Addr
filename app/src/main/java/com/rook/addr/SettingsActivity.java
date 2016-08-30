package com.rook.addr;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.internal.CallbackManagerImpl;
import com.facebook.login.LoginResult;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;

/**
 * Created by Austin on 8/12/2016.
 */
public class SettingsActivity extends BaseClass
        implements NavigationView.OnNavigationItemSelectedListener {
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.app_bar_settings, frameLayout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.callbackManager = CallbackManager.Factory.create();
        FacebookCallback<LoginResult> facebookCallback = getFacebookCallback();
        Callback<TwitterSession> twitterCallback = getTwitterCallback();

        ListView listView = (ListView) findViewById(R.id.accountsListView);
        listView.setAdapter(new AccountListAdapter(this, callbackManager, facebookCallback, twitterCallback, new String[]{"Facebook", "Instagram", "Twitter"}));
    }

    private FacebookCallback<LoginResult> getFacebookCallback() {
        return new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                final BackendlessUser user = Backendless.UserService.CurrentUser();
                if (user != null) {
//                    user.setProperty("fb_access_token", loginResult.getAccessToken().getToken());
                    user.setProperty("fb_user_id", loginResult.getAccessToken().getUserId());
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Backendless.UserService.update(user);
                        }
                    }).start();
                }
            }

            @Override
            public void onCancel() {
                // App code
                System.out.println("Login cancelled");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                System.out.println("Error: " + exception.getMessage());
            }
        };
    }

    private Callback<TwitterSession> getTwitterCallback() {
        return new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                final BackendlessUser user = Backendless.UserService.CurrentUser();
                // The TwitterSession is also available through:
                // Twitter.getInstance().core.getSessionManager().getActiveSession()
                TwitterSession session = result.data;
                user.setProperty("twitter_user_id", Long.toString(session.getUserId()));
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Backendless.UserService.update(user);
                    }
                }).start();
                String msg = "Logged in as " + "@" + session.getUserName();
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }

            @Override
            public void failure(TwitterException exception) {
                Toast.makeText(getApplicationContext(), "Log in failed", Toast.LENGTH_LONG).show();
                Log.d("TwitterKit", "Login with Twitter failure", exception);
            }
        };
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode()) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        } else if (requestCode == TwitterAuthConfig.DEFAULT_AUTH_REQUEST_CODE) {
            AccountListAdapter.loginButton.onActivityResult(requestCode, resultCode, data);
        }
    }
}
