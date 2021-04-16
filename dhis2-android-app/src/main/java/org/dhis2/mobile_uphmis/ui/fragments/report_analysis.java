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

package org.dhis2.mobile_uphmis.ui.fragments;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import org.dhis2.mobile_uphmis.R;
import org.dhis2.mobile_uphmis.io.json.JsonHandler;
import org.dhis2.mobile_uphmis.io.json.ParsingException;
import org.dhis2.mobile_uphmis.io.models.OrganizationUnit;
import org.dhis2.mobile_uphmis.utils.PrefUtils;
import org.dhis2.mobile_uphmis.utils.TextFileUtils;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class report_analysis extends AppCompatActivity {
    private LinearLayout periodPickerLinearLayout;
    private TextView periodPickerTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Handler handler = new Handler();
        setContentView(R.layout.reportanalysis);

        String jSourceUnits = TextFileUtils.readTextFile(getBaseContext(),
                TextFileUtils.Directory.ROOT,
                TextFileUtils.FileNames.ORG_UNITS_WITH_DATASETS);
        ArrayList<OrganizationUnit> units = null;
        try {
            JsonArray jUnits = JsonHandler.buildJsonArray(jSourceUnits);
            Type type = new TypeToken<ArrayList<OrganizationUnit>>() {
                // capturing type
            }.getType();
            units = JsonHandler.fromJson(jUnits, type);
        } catch (ParsingException e) {
            e.printStackTrace();
        }

        String lang = PrefUtils.getCredentials(getBaseContext());
        String ouuid = PrefUtils.getOrg(getBaseContext());
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        String yy = dateFormat.format(date).substring(0, 4);
        Integer mm = Integer.parseInt(dateFormat.format(date).substring(5, 7));
        String day = dateFormat.format(date).substring(8, 10);
        if (Integer.parseInt(day) <= 16) {
            mm = mm - 1;
        }
        String credentials = PrefUtils.getCredentials(getBaseContext());
        final String server = PrefUtils.getServerURL(getBaseContext());

        Map extraHeaders = new HashMap<>();
        extraHeaders.put("Authorization:", "Basic " + credentials);//the value is Casually set.
        String period = yy + mm;
        WebView mywebview = (WebView) findViewById(R.id.webView);

        WebSettings settings = mywebview.getSettings();
        settings.setDomStorageEnabled(true);
        settings.setDomStorageEnabled(true);

        mywebview.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedLoginRequest(WebView view, String realm, @Nullable String account, String args) {
                super.onReceivedLoginRequest(view, realm, account, args);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return true;
            }

        });
        mywebview.loadUrl("analytics_url", extraHeaders);


    }

}

