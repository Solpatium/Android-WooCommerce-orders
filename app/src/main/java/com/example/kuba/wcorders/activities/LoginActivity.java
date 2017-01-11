package com.example.kuba.wcorders.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.example.kuba.wcorders.R;
import com.example.kuba.wcorders.WcOrders;
import com.example.kuba.wcorders.woocommerce.oauth.WcOAuth;
/*
    Class that handles login
 */
public class LoginActivity extends AppCompatActivity {

    private static final String PREFERENCES_NAME = "WcOrdersCredentials";

    // UI references.
    private EditText consumerKeyField;
    private EditText secretKeyField;
    private EditText urlField;
    private Button authorizeButton;

    // Error flag
    private boolean isError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        checkCredentials();
        urlField = (EditText) findViewById(R.id.siteURL);
        secretKeyField = (EditText) findViewById(R.id.secretKey);
        consumerKeyField = (EditText) findViewById(R.id.consumerKey);
        authorizeButton = (Button) findViewById(R.id.authorize_button);
        setListeners();
    }

    private void setListeners() {
        // After editing last field action "authorize" should click button below it
        secretKeyField.setOnEditorActionListener((v, id, keyEvent) -> {
                    attemptAuthorize();
                    return true;
                }
        );
        authorizeButton.setOnClickListener((v -> attemptAuthorize()));
    }

    private void filledCheck(EditText field ) {
        if( field.getText().toString().isEmpty() ) {
            field.setError("Must be filled");
            isError = true;
        }
    }

    private void attemptAuthorize() {

        preAuthorize();
        if( isError ) {
            return;
        }

        // Get values
        String url = urlField.getText().toString();
        String consumerKey = consumerKeyField.getText().toString();
        String secretKey = secretKeyField.getText().toString();

        authorize(url, consumerKey, secretKey);

    }

    private void preAuthorize() {
        // Reset errors.
        isError = false;
        consumerKeyField.setError(null);
        secretKeyField.setError(null);
        urlField.setError(null);

        filledCheck(urlField);
        filledCheck(consumerKeyField);
        filledCheck(secretKeyField);
    }


    private void authorize( String url, String consumerKey, String secretKey ) {
        showProgressLoading();

        WcOAuth auth = new WcOAuth(consumerKey, secretKey);
        WcOrders.authenticate(url, auth, r->authorizeSuccess(), e->authorizeFail());
    }

    private void authorizeFail() {
        hideProgressLoading();
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);

        dlgAlert.setMessage("Authentication failed");
        dlgAlert.setTitle("Failure");
        dlgAlert.setPositiveButton("OK", null);
        dlgAlert.create().show();
    }

    private void authorizeSuccess() {
        // Save credentials
        saveCredentials();

        Intent intent = new Intent(this, OrderListActivity.class);
        this.startActivity(intent);
        finish();
    }

    private void hideProgressLoading() {
        findViewById(R.id.progressBarHolder).setVisibility(View.GONE);
    }

    private void showProgressLoading() {
        findViewById(R.id.progressBarHolder).setVisibility(View.VISIBLE);
    }

    private void saveCredentials() {
        SharedPreferences settings = getSharedPreferences(PREFERENCES_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("url", urlField.getText().toString());
        editor.putString("consumerKey", consumerKeyField.getText().toString());
        editor.putString("secretKey", secretKeyField.getText().toString());

        editor.commit();
    }

    private boolean checkCredentials() {
        SharedPreferences settings = getSharedPreferences(PREFERENCES_NAME, 0);
        String url = settings.getString("url","");
        String consumerKey = settings.getString("consumerKey","");
        String secretKey = settings.getString("secretKey","");

        if( url.length() > 0 && consumerKey.length() > 0 && secretKey.length() > 0 ) {
            authorize(url, consumerKey, secretKey);
            return true;
        }
        return false;
    }
}

