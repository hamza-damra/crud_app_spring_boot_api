package com.example.usermanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.usermanager.helpers.StringHelper;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignUpActivity extends AppCompatActivity {

    // UI elements
    private EditText firstNameEditText, lastNameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    private Button signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize UI elements
        firstNameEditText = findViewById(R.id.first_name);
        lastNameEditText = findViewById(R.id.last_name);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        confirmPasswordEditText = findViewById(R.id.confirm);
        signUpButton = findViewById(R.id.sign_up_btn);

        // Set click listener for sign up button
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });
    }

    // Method to register the user
    private void registerUser() {
        if (!validateForm()) {
            return;
        }

        // URL for registration endpoint
        String registrationUrl = "http://192.168.0.58:9080/api/v1/user/register";

        // Create OkHttpClient instance
        OkHttpClient client = new OkHttpClient();

        // Build request body
        RequestBody requestBody = new FormBody.Builder()
                .add("first_name", firstNameEditText.getText().toString())
                .add("last_name", lastNameEditText.getText().toString())
                .add("email", emailEditText.getText().toString())
                .add("password", passwordEditText.getText().toString())
                .build();

        // Create request
        Request request = new Request.Builder()
                .url(registrationUrl)
                .post(requestBody)
                .build();

        // Enqueue the request
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                showToastOnUiThread("Registration failed");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    showToastOnUiThread("Registration successful");
                    clearFormFieldsOnUiThread();
                } else {
                    showToastOnUiThread("Registration failed");
                }
            }
        });
    }

    // Method to validate form fields
    private boolean validateForm() {
        if (!StringHelper.validateNotEmpty(firstNameEditText.getText().toString())) {
            firstNameEditText.setError("First name cannot be empty");
            return false;
        }

        if (!StringHelper.validateNotEmpty(lastNameEditText.getText().toString())) {
            lastNameEditText.setError("Last name cannot be empty");
            return false;
        }

        if (!validateEmail(emailEditText.getText().toString())) {
            emailEditText.setError("Invalid email");
            return false;
        }

        if (!StringHelper.validateNotEmpty(passwordEditText.getText().toString())) {
            passwordEditText.setError("Password cannot be empty");
            return false;
        }

        if (!StringHelper.validateNotEmpty(confirmPasswordEditText.getText().toString())) {
            confirmPasswordEditText.setError("Confirm password cannot be empty");
            return false;
        }

        if (!passwordEditText.getText().toString().equals(confirmPasswordEditText.getText().toString())) {
            confirmPasswordEditText.setError("Passwords do not match");
            return false;
        }

        return true;
    }

    // Method to validate email
    private boolean validateEmail(String email) {
        // You can call the StringHelper method here if needed or use a built-in method for email validation
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Method to show toast message on UI thread
    private void showToastOnUiThread(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to clear form fields on UI thread
    private void clearFormFieldsOnUiThread() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                firstNameEditText.setText("");
                lastNameEditText.setText("");
                emailEditText.setText("");
                passwordEditText.setText("");
                confirmPasswordEditText.setText("");
            }
        });
    }

    // Method to navigate to home activity
    public void goToHome(View view) {
        startActivity(new Intent(SignUpActivity.this, MainActivity.class));
        finish();
    }

    // Method to navigate to sign in activity
    public void goToSignInActivity(View view) {
        startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
        finish();
    }
}
