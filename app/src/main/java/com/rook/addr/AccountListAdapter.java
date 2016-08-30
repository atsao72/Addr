package com.rook.addr;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.TwitterSession;

/**
 * Created by Austin on 8/12/2016.
 */
public class AccountListAdapter extends BaseAdapter {
    private final Context context;
    private final String[] data;
    private static LayoutInflater inflater = null;
    private final CallbackManager callbackManager;
    private final FacebookCallback<LoginResult> facebookCallback;
    private final Callback<TwitterSession> twitterCallback;
    public static CustomTwitterLoginButton loginButton;
    public static Button twitterLogout;

    public AccountListAdapter(Context context, CallbackManager callbackManager,
                              FacebookCallback<LoginResult> fbCallback, Callback<TwitterSession> twitterCallback,
                              String[] data) {
        this.context = context;
        this.data = data;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.callbackManager = callbackManager;
        this.facebookCallback = fbCallback;
        this.twitterCallback = twitterCallback;
    }

    @Override
    public int getCount() {
        return data.length;
    }

    @Override
    public Object getItem(int position) {
        return data[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        int type = getItemViewType(position);
        if (vi == null) {
            if (type == 0) {
                vi = inflater.inflate(R.layout.facebook_list_item, null);
                LoginButton loginButton = (LoginButton) vi.findViewById(R.id.fb_login_button);
                loginButton.setReadPermissions("email");
                loginButton.registerCallback(callbackManager, facebookCallback);
            } else if (type == 1) {
                vi = inflater.inflate(R.layout.instagram_list_item, null);
                Button igLogin = (Button) vi.findViewById(R.id.ig_login_button);
                igLogin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        signInWithInstagram();
                    }
                });
            } else if (type == 2) {
                vi = inflater.inflate(R.layout.twitter_list_item, null);

                loginButton = (CustomTwitterLoginButton) vi.findViewById(R.id.twitter_login_button);
//                twitterLogout = (Button) vi.findViewById(R.id.twitter_logout_button);
                loginButton.setCallback(twitterCallback);
//                loginButton.setVisibility(View.GONE);
//                twitterLogout.setVisibility(View.VISIBLE);
//                loginButton.setCallback(new Callback<TwitterSession>() {
//                    @Override
//                    public void success(Result<TwitterSession> result) {
//                        final BackendlessUser user = Backendless.UserService.CurrentUser();
//                        // The TwitterSession is also available through:
//                        // Twitter.getInstance().core.getSessionManager().getActiveSession()
//                        TwitterSession session = result.data;
//                        user.setProperty("twitter_user_id", Long.toString(session.getUserId()));
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Backendless.UserService.update(user);
//                            }
//                        }).start();
//
//                        String msg = "Logged in as " + "@" + session.getUserName();
//                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
//                    }
//
//                    @Override
//                    public void failure(TwitterException exception) {
//                        Toast.makeText(context, "Log in failed", Toast.LENGTH_LONG).show();
//                        Log.d("TwitterKit", "Login with Twitter failure", exception);
//                    }
//                });
            }
        }
        return vi;
    }

    private void updateTwitterButtons() {
        twitterLogout.setVisibility(View.VISIBLE);
        loginButton.setVisibility(View.GONE);
    }

    private void signInWithInstagram() {
        final Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.scheme("https")
                .authority("api.instagram.com")
                .appendPath("oauth")
                .appendPath("authorize")
                .appendQueryParameter("client_id", context.getString(R.string.instagram_client_id))
                .appendQueryParameter("redirect_uri", "http://redirect")
                .appendQueryParameter("response_type", "token");
        final Intent browser = new Intent(Intent.ACTION_VIEW, uriBuilder.build());
        context.startActivity(browser);
    }

}
