package com.rook.addr;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.backendless.Backendless;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends BaseClass
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private Button cameraButton;
    private TextView editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get number of installs for Facebook insights
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.app_bar_main, frameLayout);
        cameraButton = (Button) findViewById(R.id.camera_image);
        cameraButton.setOnClickListener(this);
        editText = (TextView) findViewById(R.id.editText);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Backendless.UserService.CurrentUser().getProperty("fb_user_id") != null) {
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
    }

    private Bitmap generateQRCodeBitmap() throws WriterException, JSONException {
        final BitMatrix result;
        final int WIDTH = 600;

        QRCodeWriter writer = new QRCodeWriter();
        JSONObject jsonObject = new JSONObject();
        String userId = Backendless.UserService.CurrentUser().getProperty("fb_user_id").toString();
        jsonObject.put("fb_user_id", userId);
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
        String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
        Intent intent = new Intent(ACTION_SCAN);
        intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
        startActivityForResult(intent, 0);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                //get the extras that are returned from the intent
                String contents = intent.getStringExtra("SCAN_RESULT");
                try {
                    JSONObject jsonObject = new JSONObject(contents);
                    String userIdStr = "/" + jsonObject.getString("fb_user_id");
                /* make the API call */
                    new GraphRequest(AccessToken.getCurrentAccessToken(), userIdStr, null, HttpMethod.GET,
                            new GraphRequest.Callback() {
                                public void onCompleted(final GraphResponse response) {
                                    JSONObject object = response.getJSONObject();
                                    try {
                                        final String name = object.getString("name");
                                        final String id = object.getString("id");
                                        runOnUiThread(new Runnable() {
                                            String str = "You just met " + name + " on Facebook! Visit their profile!";
                                            String link = "https://www.facebook.com/" + id;
                                            String htmlText = "<a href=\"" + link + "\">" + str + "</a>";

                                            @Override
                                            public void run() {
                                                editText.setText(Html.fromHtml(htmlText));
                                                editText.setClickable(true);
                                                editText.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        PackageManager pm = getApplicationContext().getPackageManager();
                                                        Uri uri = Uri.parse(link);
                                                        try {
                                                            ApplicationInfo applicationInfo = pm.getApplicationInfo("com.facebook.katana", 0);
                                                            if (applicationInfo.enabled) {
                                                                uri = Uri.parse("fb://facewebmodal/f?href=" + link);
                                                            }
                                                        } catch (PackageManager.NameNotFoundException e) {
                                                            System.out.println("package not found");
                                                        }
                                                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                                        startActivity(intent);
                                                    }
                                                });
                                            }
                                        });
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

}
