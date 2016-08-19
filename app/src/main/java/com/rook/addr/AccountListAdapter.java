package com.rook.addr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

/**
 * Created by Austin on 8/12/2016.
 */
public class AccountListAdapter extends BaseAdapter {
    private final Context context;
    private final String[] data;
    private static LayoutInflater inflater = null;
    private final CallbackManager callbackManager;
    private final FacebookCallback<LoginResult> facebookCallback;

    public AccountListAdapter(Context context, CallbackManager callbackManager,
                              FacebookCallback<LoginResult> fbCallback, String[] data) {
        this.context = context;
        this.data = data;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.callbackManager = callbackManager;
        this.facebookCallback = fbCallback;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.account_list_item, null);
        TextView text = (TextView) vi.findViewById(R.id.text);
        text.setText(data[position]);
        LoginButton loginButton = (LoginButton) vi.findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");
        loginButton.registerCallback(callbackManager, facebookCallback);
        return vi;
    }
}
