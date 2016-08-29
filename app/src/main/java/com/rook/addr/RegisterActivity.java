package com.rook.addr;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

/**
 * Created by Austin on 8/28/2016.
 */
public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText nameET;
    private EditText emailET;
    private EditText numberET;
    private EditText passwordET;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        nameET = (EditText) findViewById(R.id.nameEditText);
        emailET = (EditText) findViewById(R.id.emailEditText);
        numberET = (EditText) findViewById(R.id.phoneEditText);
        passwordET = (EditText) findViewById(R.id.passwordEditText);
        Button register = (Button) findViewById(R.id.bRegister);
        register.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Editable name = nameET.getText();
        Editable email = emailET.getText();
        Editable phone = numberET.getText();
        Editable password = passwordET.getText();
        if (name.equals(null) || email.equals(null) || phone.equals(null) || password.equals(null)) {
            Toast toast = Toast.makeText(getApplicationContext(), "All fields are required", Toast.LENGTH_SHORT);
            toast.show();
        } else {
            registerUser(email.toString(), password.toString(), name.toString(), phone.toString());
        }
    }

    public void registerUser(String email, String password, String name, String phoneNumber) {
        BackendlessUser user = new BackendlessUser();
        user.setEmail(email);
        user.setPassword(password);
        user.setProperty("name", name);
        user.setProperty("phone", phoneNumber);

        AsyncCallback<BackendlessUser> callback = new AsyncCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser registeredUser) {
                System.out.println("User has been registered - " + registeredUser.getObjectId());
                loginUser(registeredUser.getEmail(), registeredUser.getPassword());
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                System.out.println("Server reported an error - " + backendlessFault.getMessage());
                if (backendlessFault.getMessage().contains("User already exists")) {
                    loginUser(emailET.getText().toString(), passwordET.getText().toString());
                }
            }
        };

        Backendless.UserService.register(user, callback);
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
}
