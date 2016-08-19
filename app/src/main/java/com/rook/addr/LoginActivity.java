package com.rook.addr;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.local.UserIdStorageFactory;
import com.backendless.persistence.local.UserTokenStorageFactory;
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

        String userToken = UserTokenStorageFactory.instance().getStorage().get();

        if (userToken != null && !userToken.equals("")) {
            Backendless.UserService.isValidLogin(new AsyncCallback<Boolean>() {
                @Override
                public void handleResponse(Boolean response) {
                    if (response) {
                        String currentUserObjectId = UserIdStorageFactory.instance().getStorage().get();
                        Backendless.Data.of(BackendlessUser.class).findById(currentUserObjectId, new AsyncCallback<BackendlessUser>() {
                            @Override
                            public void handleResponse(BackendlessUser response) {
                                Backendless.UserService.setCurrentUser(response);
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            }

                            @Override
                            public void handleFault(BackendlessFault fault) {
                                Toast toast = Toast.makeText(getApplicationContext(), "Session timed out. Please login again", Toast.LENGTH_SHORT);
                                toast.show();
                                updateUi();
                            }
                        });
                    } else {
                        Toast toast = Toast.makeText(getApplicationContext(), "Session timed out. Please login again", Toast.LENGTH_SHORT);
                        toast.show();
                        updateUi();
                    }
                }

                @Override
                public void handleFault(BackendlessFault fault) {
                    updateUi();
                }
            });
        } else {
            updateUi();
        }
    }

    private void updateUi() {
        setContentView(R.layout.login_layout);
        emailText = (EditText) findViewById(R.id.emailText);
        passwordText = (EditText) findViewById(R.id.passwordText);
        Button loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        loginUser(emailText.getText().toString(), passwordText.getText().toString());
    }

    public void loginUser(String email, String password) {
        Backendless.UserService.login(email, password, new AsyncCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser response) {
                System.out.println("User has been logged in - " + response.getObjectId());
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                System.out.println("Server reported an error - " + fault.getMessage());
                Toast toast = Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_SHORT);
                toast.show();
            }
        }, true);
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
                Backendless.UserService.login(registeredUser.getEmail(), registeredUser.getPassword());
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                System.out.println("Server reported an error - " + backendlessFault.getMessage());
                if (backendlessFault.getMessage().contains("User already exists")) {
                    loginUser(emailText.getText().toString(), passwordText.getText().toString());
                }
            }
        };

        Backendless.UserService.register(user, callback);
    }

}
