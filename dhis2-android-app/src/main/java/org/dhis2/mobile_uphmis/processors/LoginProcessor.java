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

package org.dhis2.mobile_uphmis.processors;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.webkit.URLUtil;

import org.json.JSONObject;
import org.json.JSONException;
import org.dhis2.mobile_uphmis.network.HTTPClient;
import org.dhis2.mobile_uphmis.network.Response;
import org.dhis2.mobile_uphmis.network.URLConstants;
import org.dhis2.mobile_uphmis.ui.activities.LoginActivity;
import org.dhis2.mobile_uphmis.utils.PrefUtils;
import org.dhis2.mobile_uphmis.utils.TextFileUtils;

import java.net.HttpURLConnection;

public class LoginProcessor {
    private static final String HTTP = "http://";
    private static final String HTTPS = "https://";
    private static String user_lang = null;
    private static String minimum = null;
    private static String maximum = null;
    private static String ou_uid = null;


    public static void loginUser(Context context, String server,
                                 String creds, String username, String locale) {

        if (context == null || server == null
                || creds == null || username == null) {
            Log.i(LoginActivity.TAG, "Login failed");
            return;
        }
        String url = prepareUrl(server, creds);
        Response resp = tryToLogIn(url, creds);
        String change_locale = username + "&value=";
        if (locale.equals("Hindi")) {
            change_locale = change_locale + "hi";
        } else {
            change_locale = change_locale + "en";
        }

        //@Sou change user locale using post api
        Response resp_user_locale = updateLocale(url, creds, change_locale);
        //@Sou changes to save user-locale
        Response resp_user = userSettings(url, creds);
        Response ou_me = ou_me(url, creds);
        String locale_user = resp_user.getBody();
        String ou_assigned = ou_me.getBody();
        if (ou_assigned.length() > 1) {
            ou_uid = ou_assigned.substring(29, 40);
        }
        Response min_values = minvalues(url, creds);
        Response max_values = maxvalues(url, creds);
        try {
            JSONObject object = new JSONObject(max_values.getBody());
            maximum = object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            JSONObject object1 = new JSONObject(min_values.getBody());

            minimum = object1.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (locale_user != null) {
            if (locale_user.length() > 18) {
                user_lang = locale_user.substring(16, 18);
            }

        }

        // Checking validity of server URL
        if (!URLUtil.isValidUrl(url)) {
            Intent result = new Intent(LoginActivity.TAG);
            result.putExtra(Response.CODE, HttpURLConnection.HTTP_NOT_FOUND);
            LocalBroadcastManager.getInstance(context).sendBroadcast(result);
            return;
        }


        // If credentials and address is correct,
        // user information will be saved to internal storage
        if (!HTTPClient.isError(resp.getCode())) {
            PrefUtils.initAppData(context, creds, username, url, user_lang, minimum, maximum, ou_uid);
            TextFileUtils.writeTextFile(context, TextFileUtils.Directory.ROOT,
                    TextFileUtils.FileNames.ACCOUNT_INFO, resp.getBody());
        }
        if (!HTTPClient.isError(resp.getCode())) {
            PrefUtils.initAppData(context, creds, username, url, user_lang, minimum, maximum, ou_uid);
            TextFileUtils.writeTextFile(context, TextFileUtils.Directory.ROOT,
                    TextFileUtils.FileNames.ACCOUNT_INFO, resp.getBody());
        }

        // If credentials and address is correct,
        // user information will be saved to internal storage
        if (!HTTPClient.isError(resp.getCode()) && !ServerInfoProcessor.pullServerInfo(context, server, creds)) {

            PrefUtils.initAppData(context, creds, username, url, user_lang, minimum, maximum, ou_uid);
            TextFileUtils.writeTextFile(context, TextFileUtils.Directory.ROOT,
                    TextFileUtils.FileNames.ACCOUNT_INFO, resp.getBody());
        }

        // Sending result back to activity
        // through Broadcast android API
        Intent result = new Intent(LoginActivity.TAG);
        result.putExtra(Response.CODE, resp.getCode());
        LocalBroadcastManager.getInstance(context).sendBroadcast(result);
    }

    private static void gerServerVersion() {
    }

    private static String prepareUrl(String initialUrl, String creds) {
        if (initialUrl.contains(HTTPS) || initialUrl.contains(HTTP)) {
            return initialUrl;
        }

        // try to use https
        Response response = tryToLogIn(HTTPS + initialUrl, creds);
        if (response.getCode() != HttpURLConnection.HTTP_MOVED_PERM) {
            return HTTPS + initialUrl;
        } else {
            return HTTP + initialUrl;
        }
    }

    private static Response tryToLogIn(String server, String creds) {
        String url = server + URLConstants.API_USER_ACCOUNT_URL;
        return HTTPClient.get(url, creds);
    }

    //@Sou changes to save user-locale
    private static Response userSettings(String server, String creds) {
        String url = server + URLConstants.API_USER_SETTINGS;
        return HTTPClient.get(url, creds);
    }

    private static Response updateLocale(String server, String creds, String locale) {
        String url = server + URLConstants.API_UPDATE_LOCALE + locale;
        return HTTPClient.post(url, creds, locale);
    }

    //@Sou changes to save ou-minmax
    private static Response minvalues(String server, String creds) {
//        String url = server + URLConstants.API_MIN+ ou_uid;
        String url = server + URLConstants.API_MIN_DEFAULT + "?filter=source.id:eq:" + ou_uid + "&paging=false";
        return HTTPClient.get(url, creds);
    }

    private static Response maxvalues(String server, String creds) {
        String url = server + URLConstants.API_MIN_DEFAULT + "?filter=source.id:eq:" + ou_uid + "&paging=false";
        return HTTPClient.get(url, creds);
    }

    //@Sou changes to save ou-me
    private static Response ou_me(String server, String creds) {
        String url = server + URLConstants.API_ME_ORG;
        return HTTPClient.get(url, creds);
    }
}
