package com.example.usermanager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.usermanager.helpers.StringHelper;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class SignInActivity extends AppCompatActivity {

    private Button signInButton;
    private EditText emailEditText, passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Initialize UI elements
        signInButton = findViewById(R.id.sign_in_btn);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);

        // Set click listener for sign in button
        signInButton.setOnClickListener(view -> authenticateUser());
    }

    private void authenticateUser() {
        if (!validateEmail() || !validatePassword()) {
            return;
        }

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor(message -> Log.d("OkHttp", message))
                        .setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", emailEditText.getText().toString());
            jsonObject.put("password", passwordEditText.getText().toString());
        } catch (JSONException e) {
            Log.e("SignInActivity", "JSON Exception: " + e.getMessage());
            return;
        }

        RequestBody body = RequestBody.create(JSON, jsonObject.toString());

        Request request = new Request.Builder()
                .url("http://192.168.0.58:9080/api/v1/user/login")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                String errorMessage;
                if (e instanceof ConnectException) {
                    errorMessage = "Connection Error: Please check your internet connection and try again.";
                } else if (e instanceof SocketTimeoutException) {
                    errorMessage = "Connection Timeout: Please try again later.";
                } else {
                    errorMessage = "Login Failed: " + e.getMessage();
                }
                Log.e("SignInActivity", errorMessage);
                showToastOnUiThread(errorMessage);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                assert response.body() != null;
                final String responseBody = response.body().string();
                // Always close the response body to avoid leaking resources
                response.close();

                if (response.isSuccessful()) {
                    try {
                        // Attempt to parse the response as a JSON object
                        JSONObject jsonObject = new JSONObject(responseBody);
                        Log.d("SignInActivity", "Response: " + jsonObject.toString());
                        String firstName = jsonObject.optString("first_name", "");
                        String lastName = jsonObject.optString("last_name", "");
                        String email = jsonObject.optString("email", "");

                        if (!firstName.isEmpty() && !lastName.isEmpty() && !email.isEmpty()) {
                            navigateToProfileActivity(firstName, lastName, email);
                        } else {
                            Log.e("SignInActivity", "Missing user information in response: " + responseBody);
                        }
                    } catch (JSONException e) {
                        // The response is not in JSON format, handle plain text response here
                        Log.e("SignInActivity", "Response is not in JSON format: " + responseBody);
                        showToastOnUiThread("Login Failed: Unexpected response format");
                    }
                } else {
                    Log.e("SignInActivity", "Failed to login. Response: " + responseBody);
                    showToastOnUiThread("Login Failed: " + response.code());
                }
            }
        });
    }


            private void navigateToProfileActivity(String firstName, String lastName, String email) {
        runOnUiThread(() -> {
            Intent intent = new Intent(SignInActivity.this, ProfileActivity.class);
            intent.putExtra("first_name", firstName);
            intent.putExtra("last_name", lastName);
            intent.putExtra("email", email);
            startActivity(intent);
            finish();
        });
    }

    private void showToastOnUiThread(final String message) {
        runOnUiThread(() -> Toast.makeText(SignInActivity.this, message, Toast.LENGTH_LONG).show());
    }

    public void goToHome(View view) {
        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void goToSignUpAct(View view) {
        Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean validateEmail() {
        String email = emailEditText.getText().toString();
        if (email.isEmpty()) {
            emailEditText.setError("Email cannot be empty!");
            return false;
        } else if (!StringHelper.validateEmail(email)) {
            emailEditText.setError("Please enter a valid email");
            return false;
        } else {
            emailEditText.setError(null);
            return true;
        }
    }

    private boolean validatePassword() {
        String password = passwordEditText.getText().toString();
        if (password.isEmpty()) {
            passwordEditText.setError("Password cannot be empty!");
            return false;
        } else {
            passwordEditText.setError(null);
            return true;
        }
    }
}

