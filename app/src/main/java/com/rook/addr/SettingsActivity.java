package com.rook.addr;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;

/**
 * Created by Austin on 8/12/2016.
 */
public class SettingsActivity extends BaseClass
        implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemClickListener {
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.app_bar_settings, frameLayout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.callbackManager = CallbackManager.Factory.create();

        FacebookCallback<LoginResult> facebookCallback = new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                final BackendlessUser user = Backendless.UserService.CurrentUser();
                if (user != null) {
                    user.setProperty("fb_access_token", loginResult.getAccessToken().getToken());
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

        ListView listView = (ListView) findViewById(R.id.accountsListView);
        listView.setOnItemClickListener(this);
        listView.setAdapter(new AccountListAdapter(this, callbackManager, facebookCallback, new String[]{"Facebook", "Instagram"}));
    }
//
//    private void signInWithInstagram() {
//        final Uri.Builder uriBuilder = new Uri.Builder();
//        uriBuilder.scheme("https")
//                .authority("api.instagram.com")
//                .appendPath("oauth")
//                .appendPath("authorize")
//                .appendQueryParameter("client_id", getString(R.string.instagram_client_id))
//                .appendQueryParameter("redirect_uri", "http://redirect")
//                .appendQueryParameter("response_type", "token");
//        final Intent browser = new Intent(Intent.ACTION_VIEW, uriBuilder.build());
//        startActivity(browser);
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0: //Facebook

            case 1: //Instagram
                Snackbar.make(view, "Add Instagram Account", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                break;
            case 2: //Twitter
                Snackbar.make(view, "Add Twitter Account", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                break;
        }
    }
}
