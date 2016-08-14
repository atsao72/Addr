package com.rook.addr;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntegerRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

/**
 * Created by Austin on 8/13/2016.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText emailText;
    private EditText passwordText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(getApplication());
        Backendless.initApp(getBaseContext(), getString(R.string.app_id), getString(R.string.secret_key), BuildConfig.VERSION_NAME);

        setContentView(R.layout.login_layout);
        emailText = (EditText) findViewById(R.id.emailText);
        passwordText = (EditText) findViewById(R.id.passwordText);
        Button loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        loginUser();

    }


    final AsyncCallback<BackendlessUser> loginCallback = new AsyncCallback<BackendlessUser>() {
        @Override
        public void handleResponse(BackendlessUser registeredUser) {
            System.out.println("User has been logged in - " + registeredUser.getObjectId());
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }

        @Override
        public void handleFault(BackendlessFault backendlessFault) {
            System.out.println("Server reported an error - " + backendlessFault.getMessage());
            Toast toast = Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_SHORT);
            toast.show();
        }
    };

    public void loginUser() {
        Backendless.UserService.login(emailText.getText().toString(), passwordText.getText().toString(), loginCallback);
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
                if (backendlessFault.getMessage().contains("User already exists")) {
                    loginUser();
                }
            }
        };

        Backendless.UserService.register(user, callback);
    }

}
