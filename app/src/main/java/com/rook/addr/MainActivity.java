package com.rook.addr;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

public class MainActivity extends BaseClass
        implements NavigationView.OnNavigationItemSelectedListener {

//    protected static DrawerLayout drawer;
    private static ActionBarDrawerToggle toggle;
//    private static NavigationView navigationView;
    private static boolean isFirst = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get number of installs for Facebook insights
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(getApplication());

        Backendless.initApp(getBaseContext(), getString(R.string.app_id), getString(R.string.secret_key), BuildConfig.VERSION_NAME);

        if (Backendless.UserService.CurrentUser() == null) {
            registerUser();
        } else {
            loginUser();
        }

        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.app_bar_main, frameLayout);
//        setContentView(R.layout.drawer_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        if(isFirst){
            setSupportActionBar(toolbar);
//            isFirst = false;
//        }

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

//        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.addDrawerListener(toggle);
//        toggle.syncState();
//
//        navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);
    }

//    @Override
//    protected void onPostCreate(Bundle savedInstanceState) {
//        super.onPostCreate(savedInstanceState);
//        toggle.syncState();
//    }

    final AsyncCallback<BackendlessUser> loginCallback = new AsyncCallback<BackendlessUser>() {
        @Override
        public void handleResponse(BackendlessUser registeredUser) {
            System.out.println("User has been logged in - " + registeredUser.getObjectId());
            View headerLayout = navigationView.getHeaderView(0);
            TextView nameText = (TextView) headerLayout.findViewById(R.id.name);
            TextView emailText = (TextView) headerLayout.findViewById(R.id.email);
            BackendlessUser user = Backendless.UserService.CurrentUser();
            nameText.setText(user.getProperty("name").toString());
            emailText.setText(user.getEmail());
        }

        @Override
        public void handleFault(BackendlessFault backendlessFault) {
            System.out.println("Server reported an error - " + backendlessFault.getMessage());
        }
    };

    public void loginUser() {
        Backendless.UserService.login("test@gmail.com", "pass1", loginCallback);
    }

    public void registerUser() {
        BackendlessUser user = new BackendlessUser();
        user.setEmail("test@gmail.com");
        user.setPassword("pass1");
        user.setProperty("name", "Tester1");


        AsyncCallback<BackendlessUser> callback = new AsyncCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser registeredUser) {
                System.out.println("User has been registered - " + registeredUser.getObjectId());
                Backendless.UserService.login(registeredUser.getEmail(), registeredUser.getPassword(), loginCallback);
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                System.out.println("Server reported an error - " + backendlessFault.getMessage());
                if(backendlessFault.getMessage().contains("User already exists")){
                    loginUser();
                }
            }
        };

        Backendless.UserService.register(user, callback);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            finish();
            drawer.closeDrawers();
        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
