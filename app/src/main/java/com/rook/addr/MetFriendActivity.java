package com.rook.addr;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.HashMap;

/**
 * Created by Austin on 8/26/2016.
 */
public class MetFriendActivity extends BaseClass {
    private String name;
    private String phoneNumber;
    private Uri fbLink;
    private Uri twitterLink;
    private TextView nameTv;
    private TextView phoneTv;
    private ListView accountsLv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.app_bar_meeting, frameLayout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle bundle = getIntent().getExtras();
        name = bundle.getString("name");
        phoneNumber = bundle.getString("phone_num");
        String facebook = bundle.getString("fbLink");
        if (facebook != null) {
            fbLink = Uri.parse(facebook);
        }
        String twitter = bundle.getString("twitterLink");
        if (twitter != null) {
            twitterLink = Uri.parse(twitter);
        }
        nameTv = (TextView) findViewById(R.id.introTextView);
        phoneTv = (TextView) findViewById(R.id.phoneTextView);
        accountsLv = (ListView) findViewById(R.id.accountsListView);
        updateUi();
    }

    private void updateUi() {
        nameTv.setText(name);
        String newText = phoneTv.getText().toString() + phoneNumber;
        phoneTv.setText(newText);
//        Uri[] accountLinks = new Uri[3];
        HashMap<String, Uri> accountLinks = new HashMap<>();
        if (fbLink != null && !fbLink.toString().isEmpty()) {
//            accountLinks[0] = fbLink;
            accountLinks.put(getString(R.string.facebook), fbLink);
        }
        if (twitterLink != null && !twitterLink.toString().isEmpty()) {
            accountLinks.put(getString(R.string.twitter), twitterLink);
        }
        accountsLv.setAdapter(new ProfileListAdapter(this, accountLinks));

    }
}
