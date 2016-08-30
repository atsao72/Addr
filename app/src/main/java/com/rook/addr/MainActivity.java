package com.rook.addr;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.google.zxing.qrcode.QRCodeWriter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends BaseClass
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private Button cameraButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get number of installs for Facebook insights
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.app_bar_main, frameLayout);
        cameraButton = (Button) findViewById(R.id.camera_image);
        cameraButton.setOnClickListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            Bitmap bitmap = generateQRCodeBitmap();
            final ImageView qrImage = (ImageView) findViewById(R.id.qrImage);
            qrImage.setImageBitmap(bitmap);
        } catch (WriterException e1) {
            e1.printStackTrace();
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
    }

    private Bitmap generateQRCodeBitmap() throws WriterException, JSONException {
        final BitMatrix result;
        final int WIDTH = 600;

        QRCodeWriter writer = new QRCodeWriter();
        JSONObject jsonObject = new JSONObject();
        BackendlessUser currentUser = Backendless.UserService.CurrentUser();
        if (currentUser.getProperty("fb_user_id") != null) {
            String fbUserId = currentUser.getProperty("fb_user_id").toString();
            jsonObject.put("fb_user_id", fbUserId);
        }
        if (currentUser.getProperty("twitter_user_id") != null) {
            String twitterUserId = currentUser.getProperty("twitter_user_id").toString();
            jsonObject.put("twitter_user_id", twitterUserId);
        }
        String phoneNumber = currentUser.getProperty("phone").toString();
        String name = currentUser.getProperty("name").toString();
        jsonObject.put("phone_num", phoneNumber);
        jsonObject.put("name", name);
        result = writer.encode(jsonObject.toString(),
                BarcodeFormat.QR_CODE, WIDTH, WIDTH);

        Bitmap bitmap = Bitmap.createBitmap(WIDTH, WIDTH, Bitmap.Config.ARGB_8888);
        for (int i = 0; i < WIDTH; i++) {//width
            for (int j = 0; j < WIDTH; j++) {//height
                bitmap.setPixel(i, j, result.get(i, j) ? Color.BLACK : Color.WHITE);
            }
        }
        return bitmap;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
        integrator.initiateScan();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == RESULT_OK) {
            //get the extras that are returned from the intent
            IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
            String contents = scanResult.getContents();
            try {
                JSONObject jsonObject = new JSONObject(contents);
                String userIdStr = "/" + jsonObject.getString("fb_user_id");
                final String name = jsonObject.getString("name");
                final String phoneNum = jsonObject.getString("phone_num");
                final String twitterUserId = jsonObject.getString("twitter_user_id");
                /* make the API call */
                new GraphRequest(AccessToken.getCurrentAccessToken(), userIdStr, null, HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(final GraphResponse response) {
                                JSONObject object = response.getJSONObject();
                                try {
                                    final String id = object.getString("id");
                                    String fbLink = "https://www.facebook.com/" + id;
                                    PackageManager pm = getApplicationContext().getPackageManager();

                                    try {
                                        ApplicationInfo applicationInfo = pm.getApplicationInfo("com.facebook.katana", 0);
                                        if (applicationInfo.enabled) {
                                            fbLink = "fb://facewebmodal/f?href=" + fbLink;
                                        }
                                    } catch (PackageManager.NameNotFoundException e) {
                                        System.out.println("package not found");
                                    }
                                    String twitterLink = "twitter.com";
                                    try {
                                        pm.getApplicationInfo("com.twitter.android", 0);
                                        twitterLink = "twitter://user?user_id=" + twitterUserId;
                                    } catch (PackageManager.NameNotFoundException e) {
                                        Log.d("Package Manager error", "package not found", e);
                                    }
                                    Intent intent = new Intent(getApplicationContext(), MetFriendActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("name", name);
                                    bundle.putString("phone_num", phoneNum);
                                    ArrayList<String> links = new ArrayList<String>();
                                    links.add(fbLink);
                                    links.add(twitterLink);
                                    bundle.putStringArrayList("links", links);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                ).executeAsync();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
