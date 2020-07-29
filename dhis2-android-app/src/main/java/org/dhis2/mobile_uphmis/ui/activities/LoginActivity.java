/*
 * Copyright (c) 2014, Araz Abishov
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package org.dhis2.mobile_uphmis.ui.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.facebook.stetho.Stetho;

import org.dhis2.mobile_uphmis.R;
import org.dhis2.mobile_uphmis.WorkService;
import org.dhis2.mobile_uphmis.network.HTTPClient;
import org.dhis2.mobile_uphmis.network.NetworkUtils;
import org.dhis2.mobile_uphmis.network.Response;
import org.dhis2.mobile_uphmis.utils.PrefUtils;
import org.dhis2.mobile_uphmis.utils.ToastManager;
import org.dhis2.mobile_uphmis.utils.ViewUtils;

import java.util.Locale;

public class LoginActivity extends AppCompatActivity {
    public static final String TAG = LoginActivity.class.getSimpleName();
    public static final String USERNAME = "username";
    public static final String SERVER = "server";
    public static final String LOCALE = "locale";
    public static final String CREDENTIALS = "creds";
    public static final String IS_FIRST_PULL = "isfirstpull";

    private Button mLoginButton;
    private EditText mUsername;
    private EditText mPassword;
    private ImageView mDhis2Logo;
    private ImageView muplogo;
    private ImageView mnhmlogo;
    private Spinner mlanguage;

    // Disabled serverUrl EditText in order to allow
    // developers to build app with custom server address
    private EditText mServerUrl;
    private ProgressBar mProgressBar;
    private LoginActivity mLoginActivity;

    // BroadcastReceiver which aim is to listen
    // for network response on login post request
    BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            int code = intent.getExtras().getInt(Response.CODE);
            Boolean isFirstPull = intent.getExtras().getBoolean(LoginActivity.IS_FIRST_PULL);

            // If response code is 200, then MenuActivity is started
            // If not, user is notified with error message
            if (!HTTPClient.isError(code)) {

                if(PrefUtils.getServerVersion(context)!=null && !PrefUtils.getServerVersion(context).equals("")) {

                    // Prepare Intent and start service
                    if(isFirstPull == null || !isFirstPull) {
                        intent = new Intent(mLoginActivity, WorkService.class);
                        intent.putExtra(WorkService.METHOD, WorkService.METHOD_FIRST_PULL_DATASETS);
                        mLoginActivity.startService(intent);
                    }else {
                        finish();
                        Intent menuActivity = new Intent(LoginActivity.this, MenuActivity.class);
                        startActivity(menuActivity);
                        overridePendingTransition(R.anim.activity_open_enter,
                                R.anim.activity_open_exit);
                    }
                }else{
                    hideProgress();
                    String message = context.getString(R.string.server_error);
                    showMessage(message);
                }
            } else {
                hideProgress();
                String message = HTTPClient.getErrorMessage(LoginActivity.this, code);
                mServerUrl.setVisibility(View.GONE);
                showMessage(message);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String lang=PrefUtils.getLocale(getBaseContext());
        if (lang!=null&&lang.equals("hi"))
        {
            Locale locale = new Locale("hi");
            Locale.setDefault(locale);
            Configuration config = getBaseContext().getResources().getConfiguration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());
        }
        //@Sou_ stetho initialization
        Stetho.initializeWithDefaults(this);
        setContentView(R.layout.activity_login);
        mLoginActivity = this;
        mDhis2Logo = (ImageView) findViewById(R.id.dhis2_logo);
        muplogo = (ImageView) findViewById(R.id.imageView);
        mnhmlogo = (ImageView) findViewById(R.id.imageView2);
        mLoginButton = (Button) findViewById(R.id.login_button);

        mServerUrl = (EditText) findViewById(R.id.server_url);
        mUsername = (EditText) findViewById(R.id.username);
        //@Sou language selection
        mlanguage = (Spinner) findViewById(R.id.spinner1);
        mPassword = (EditText) findViewById(R.id.password);

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mProgressBar.setVisibility(View.GONE);
        // textwatcher is responsible for watching
        // after changes in all fields
        final TextWatcher textWatcher = new TextWatcher() {

            @Override
            public void afterTextChanged(Editable edit) {
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                checkEditTextFields();
            }
        };

        mServerUrl.addTextChangedListener(textWatcher);
        mUsername.addTextChangedListener(textWatcher);
        mPassword.addTextChangedListener(textWatcher);

        // Call method in order to check the fields
        // and change state of login button
        checkEditTextFields();

        mLoginButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                loginUser();
            }
        });

        // Restoring state of activity from saved bundle
        if (savedInstanceState != null) {
            boolean loginInProcess = savedInstanceState.getBoolean(TAG, false);

            if (loginInProcess) {
                ViewUtils.hideAndDisableViews(mDhis2Logo, mnhmlogo,muplogo,mServerUrl, mUsername, mPassword, mlanguage,mLoginButton);
                showProgress();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Registering BroadcastReceiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter(TAG));
    }

    @Override
    public void onPause() {

        // Unregistering BroadcastReceiver in
        // onPause() in order to prevent leaks
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Saving state of activity
        if (mProgressBar != null) {
            outState.putBoolean(TAG, mProgressBar.isShown());
        }
        super.onSaveInstanceState(outState);
    }

    // Activates *login button*,
    // if all necessary fields are full
    private void checkEditTextFields() {
        String tempUrl = mServerUrl.getText().toString();
        //Server address will be retrieved from .xml resources
        //String tempUrl = getString(R.string.default_server_url);
        String tempUsername = mUsername.getText().toString();
        String tempPassword = mPassword.getText().toString();

        if (tempUrl.equals("") || tempUsername.equals("") || tempPassword.equals("")) {
            mLoginButton.setEnabled(false);
        } else {
            mLoginButton.setEnabled(true);
        }
    }

    // loginUser() is called when user clicks *LoginButton*
    private void loginUser() {
        String tmpServer = mServerUrl.getText().toString();
        //Server address will be retrieved from .xml resources
        //String tmpServer = getString(R.string.default_server_url);

        String user = mUsername.getText().toString();
        String pass = mPassword.getText().toString();
        String pair = String.format("%s:%s", user, pass);
        String language=String.valueOf(mlanguage.getSelectedItem());
//        String language=String.valueOf("test");
        if (NetworkUtils.checkConnection(LoginActivity.this)) {
            showProgress();

            String server = tmpServer + (tmpServer.endsWith("/") ? "" : "/");
            String creds = Base64.encodeToString(pair.getBytes(), Base64.NO_WRAP);

            // Preparing data to be sent to WorkService
            Intent intent = new Intent(LoginActivity.this, WorkService.class);
            intent.putExtra(WorkService.METHOD, WorkService.METHOD_LOGIN_USER);
            intent.putExtra(SERVER, server);
            intent.putExtra(LOCALE, language);
            intent.putExtra(USERNAME, user);
            intent.putExtra(CREDENTIALS, creds);

            // Starting WorkService
            startService(intent);
        } else {
            showMessage(getString(R.string.check_connection));
        }
    }

    private void showMessage(String message) {
        ToastManager.makeToast(this, message, Toast.LENGTH_LONG).show();
    }

    private void showProgress() {
        ViewUtils.perfomOutAnimation(this, R.anim.out_up, true,
                mDhis2Logo, mnhmlogo,muplogo,mServerUrl, mUsername, mPassword,mlanguage, mLoginButton);
        ViewUtils.enableViews(mProgressBar);
    }

    private void hideProgress() {
        ViewUtils.perfomInAnimation(this, R.anim.in_down,
                mDhis2Logo,mnhmlogo,muplogo, mServerUrl, mUsername, mPassword,mlanguage, mLoginButton);
        ViewUtils.hideAndDisableViews(mProgressBar);
    }

}
