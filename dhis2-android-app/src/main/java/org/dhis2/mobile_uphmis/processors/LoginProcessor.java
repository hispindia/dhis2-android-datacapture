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

import org.json.JSONArray;
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
    private static String dis_org = null;
    private static String dis_org_id = null;
    private static String block_org_id = null;
    private static String block_org = null;
    private static  String parent_dis = "";
    private static JSONObject json_parent = null;
    private static JSONObject block_parent = null;


    public static void loginUser(Context context, String server,
                                 String creds, String username, String locale) {
        parent_dis = PrefUtils.getDstrictParent(context);
        if (context == null || server == null
                || creds == null || username == null) {
            Log.i(LoginActivity.TAG, "Login failed");
            return;
        }
//        String ou=TextFileUtils.readTextFile(context,
//                TextFileUtils.Directory.ROOT,
//                TextFileUtils.FileNames.ORG_UNITS_WITH_DATASETS);
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
        Response ou_parent = parent_sql(url, creds);
        Response block_parent_ = block_sql(url, creds);
        String locale_user = resp_user.getBody();
        String ou_assigned = ou_me.getBody();
        String dis_assigned = ou_parent.getBody();
        String block_assigned = block_parent_.getBody();

        try {
            json_parent = new JSONObject(dis_assigned);
            JSONArray arr_par = json_parent.getJSONArray("sqlViews");
            for (int i = 0; i < arr_par.length(); i++) {
                JSONObject o = arr_par.getJSONObject(i);
                dis_org = o.getString("id");

                Log.d("id------", dis_org);
            }
        }
     catch(
     JSONException e)
     {
        e.printStackTrace();
     }

    try {
            block_parent = new JSONObject(block_assigned);
            JSONArray arr_par = block_parent.getJSONArray("sqlViews");
            for (int i = 0; i < arr_par.length(); i++) {
                JSONObject o = arr_par.getJSONObject(i);
                block_org = o.getString("id");
                Log.d("block_org---", block_org);
            }
        }
     catch(
     JSONException e)
     {
        e.printStackTrace();
     }


        if(ou_assigned.length()>1)

    {
        ou_uid = ou_assigned.substring(29, 40);
    }
        if(dis_assigned.length()>1)

    {
        Log.d("dis_assigned", dis_assigned);
        ou_uid = ou_assigned.substring(29, 40);
    }

        Response ou_parent_id = parent_sql_id(url, creds);
        String dis_assigned_id = ou_parent_id.getBody();
        try {
            json_parent = new JSONObject(dis_assigned_id);
            JSONObject getSth = json_parent.getJSONObject("listGrid");
//            JSONObject getSth_ = getSth.getJSONObject("rows");
            JSONArray arr_par_ = getSth.getJSONArray("rows");
            String dhis_parente=arr_par_.toString();
            int first_index=dhis_parente.indexOf("[")+3;
            int lastindex=dhis_parente.indexOf("]")-1;
            dis_org_id=dhis_parente.substring(first_index,lastindex);
            Log.d("dis_org_id", dis_org_id);
        }
        catch(
                JSONException e)
        {
            e.printStackTrace();
        }
        Response ou_parent_id_ = block_sql_id(url, creds);
        String dis_assigned_id_ = ou_parent_id_.getBody();
        try {
            block_parent = new JSONObject(dis_assigned_id_);
            JSONObject getSth = block_parent.getJSONObject("listGrid");
//            JSONObject getSth_ = getSth.getJSONObject("rows");
            JSONArray arr_par_ = getSth.getJSONArray("rows");
            String dhis_parente=arr_par_.toString();
            int first_index=dhis_parente.indexOf("[")+3;
            int lastindex=dhis_parente.indexOf("]")-1;
            block_org_id=dhis_parente.substring(first_index,lastindex);
            Log.d("block_org_id", block_org_id);
        }
        catch(
                JSONException e)
        {
            e.printStackTrace();
        }


    Response min_values = minvalues(url, creds);
    Response max_values = maxvalues(url, creds);
        try

    {
        JSONObject object = new JSONObject(max_values.getBody());
        maximum = object.toString();
//                textView.setText(object.toString());
    } catch(
    JSONException e)

    {
        e.printStackTrace();
//                textView.setText(e.getMessage());
    }
        try

    {
        JSONObject object1 = new JSONObject(min_values.getBody());

        minimum = object1.toString();
//                textView.setText(object.toString());
    } catch(
    JSONException e)

    {
        e.printStackTrace();
//                textView.setText(e.getMessage());QQssH9MWMn0&paging=false
    }
        if(locale_user!=null)

    {
        if (locale_user.length() > 18) {
            user_lang = locale_user.substring(16, 18);
        }

    }

    // Checking validity of server URL
        if(!URLUtil.isValidUrl(url))

    {
        Intent result = new Intent(LoginActivity.TAG);
        result.putExtra(Response.CODE, HttpURLConnection.HTTP_NOT_FOUND);
        LocalBroadcastManager.getInstance(context).sendBroadcast(result);
        return;
    }


    // If credentials and address is correct,
    // user information will be saved to internal storage
        if(!HTTPClient.isError(resp.getCode()))
    {
        PrefUtils.initAppData(context, creds, username, url, user_lang, minimum, maximum, ou_uid, dis_org_id,block_org_id);
        TextFileUtils.writeTextFile(context, TextFileUtils.Directory.ROOT,
                TextFileUtils.FileNames.ACCOUNT_INFO, resp.getBody());
    }
        if(!HTTPClient.isError(resp.getCode()))

    {
        PrefUtils.initAppData(context, creds, username, url, user_lang, minimum, maximum, ou_uid, dis_org_id,block_org_id);
        TextFileUtils.writeTextFile(context, TextFileUtils.Directory.ROOT,
                TextFileUtils.FileNames.ACCOUNT_INFO, resp.getBody());
    }

    // If credentials and address is correct,
    // user information will be saved to internal storage
        if(!HTTPClient.isError(resp.getCode())&&!ServerInfoProcessor.pullServerInfo(context,server,creds))

    {

        PrefUtils.initAppData(context, creds, username, url, user_lang, minimum, maximum, ou_uid, dis_org_id,block_org_id);
        TextFileUtils.writeTextFile(context, TextFileUtils.Directory.ROOT,
                TextFileUtils.FileNames.ACCOUNT_INFO, resp.getBody());
    }

    // Sending result back to activity
    // through Broadcast android API
    Intent result = new Intent(LoginActivity.TAG);
        result.putExtra(Response.CODE,resp.getCode());
        LocalBroadcastManager.getInstance(context).

    sendBroadcast(result);

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
        return HTTPClient.get(url, creds,parent_dis);
    }

    //@Sou changes to save user-locale
    private static Response userSettings(String server, String creds) {
        String url = server + URLConstants.API_USER_SETTINGS;
        return HTTPClient.get(url, creds,parent_dis);
    }

    private static Response updateLocale(String server, String creds, String locale) {
        String url = server + URLConstants.API_UPDATE_LOCALE + locale;
        return HTTPClient.post(url, creds, locale);
    }

    //@Sou changes to save ou-minmax
    private static Response minvalues(String server, String creds) {
//        String url = server + URLConstants.API_MIN+ ou_uid;
        String url = server + URLConstants.API_MIN_DEFAULT + "?filter=source.id:eq:" + ou_uid + "&paging=false";
        return HTTPClient.get(url, creds,parent_dis);
    }

    private static Response maxvalues(String server, String creds) {
        String url = server + URLConstants.API_MIN_DEFAULT + "?filter=source.id:eq:" + ou_uid + "&paging=false";
        return HTTPClient.get(url, creds,parent_dis);
    }

    //@Sou changes to save ou-me
    private static Response ou_me(String server, String creds) {
        String url = server + URLConstants.API_ME_ORG;
        return HTTPClient.get(url, creds,parent_dis);
    }

    private static Response parent_sql(String server, String creds) {
        String url = server + URLConstants.PARENT_SQLVIEW;
        return HTTPClient.get(url, creds,parent_dis);
    }

    private static Response parent_sql_id(String server, String creds) {
//        String url = server + URLConstants.API_MIN+ ou_uid;
        String url = server + URLConstants.SQLVIEW_API + dis_org+"/data?var=uid:" + ou_uid ;
        return HTTPClient.get(url, creds,parent_dis);
    }
    private static Response block_sql(String server, String creds) {
        String url = server + URLConstants.BLOCK_SQLVIEW;
        return HTTPClient.get(url, creds,parent_dis);
    }

    private static Response block_sql_id(String server, String creds) {
        String url = server + URLConstants.SQLVIEW_API + block_org+"/data?var=uid:" + ou_uid ;
        return HTTPClient.get(url, creds,parent_dis);
    }
}
