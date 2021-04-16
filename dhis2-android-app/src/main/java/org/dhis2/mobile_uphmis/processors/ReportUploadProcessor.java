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
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.dhis2.mobile_uphmis.io.Constants;
import org.dhis2.mobile_uphmis.io.handlers.DialogHandler;
import org.dhis2.mobile_uphmis.io.handlers.ImportSummariesHandler;
import org.dhis2.mobile_uphmis.io.holders.DatasetInfoHolder;
import org.dhis2.mobile_uphmis.io.models.CategoryOption;
import org.dhis2.mobile_uphmis.io.models.Field;
import org.dhis2.mobile_uphmis.io.models.Group;
import org.dhis2.mobile_uphmis.network.HTTPClient;
import org.dhis2.mobile_uphmis.network.Response;
import org.dhis2.mobile_uphmis.network.URLConstants;
import org.dhis2.mobile_uphmis.ui.fragments.AggregateReportFragment;
import org.dhis2.mobile_uphmis.utils.NotificationBuilder;
import org.dhis2.mobile_uphmis.utils.PrefUtils;
import org.dhis2.mobile_uphmis.utils.SyncLogger;
import org.dhis2.mobile_uphmis.utils.TextFileUtils;
import org.joda.time.LocalDate;
import org.dhis2.mobile_uphmis.network.NetworkUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ReportUploadProcessor {
    public static final String TAG = ReportUploadProcessor.class.getSimpleName();
    private static JSONObject json_parent = null;
    private static String server_date = "";
    private static int dev_val = 0;
    private static String dev_val_uid = "";

    private ReportUploadProcessor() {
    }

    public static void upload(Context context, DatasetInfoHolder info, ArrayList<Group> groups) {
        dev_val_uid = PrefUtils.getDeviceDetails(context);
        dev_val = 0;
        String url_ = PrefUtils.getServerURL(context) + URLConstants.SYSTEM_INFO;
        String creds_ = PrefUtils.getCredentials(context);
        String parent_dis_ = PrefUtils.getDstrictParent(context);
        Response response_ = HTTPClient.get(url_, creds_, parent_dis_);

        String system_date = response_.getBody();
        if (NetworkUtils.checkConnection(context)) {
            try {
                json_parent = new JSONObject(system_date);
                String system_date_ = json_parent.getString("serverDate");
                int iend = system_date_.indexOf("T");
                server_date = system_date_.substring(0, iend);
                Log.d("system_date_", server_date);
            } catch (
                    JSONException e) {
                e.printStackTrace();
            }
        }

        String data = prepareContent(info, groups);

        String data_offline = prepareContent_report(info, groups);

        saveDatasetOfflineR(context, data_offline, info);
        String parent_dis = PrefUtils.getDstrictParent(context);
        if (!NetworkUtils.checkConnection(context)) {
            saveDataset(context, data, info);
            return;
        }

        String url = PrefUtils.getServerURL(context) + URLConstants.DATASET_UPLOAD_URL;
        String creds = PrefUtils.getCredentials(context);
        Log.i(TAG, data);
        Response response = HTTPClient.postdv(url, creds, data, parent_dis);

        String log = String.format("[%s] %s", response.getCode(), response.getBody());
        Log.i(TAG, log);

        if (!HTTPClient.isError(response.getCode())) {
            SyncLogger.log(context, response, info, false);

            if (ImportSummariesHandler.isSuccess(response.getBody())) {


                NotificationBuilder.fireNotification(context,
                        SyncLogger.getResponseDescription(context, response),
                        SyncLogger.getNotification(info));
                sendBroadcastCorrectlyUpload(info, context);
            } else {

                DialogHandler dialogHandler = new DialogHandler(SyncLogger.getResponseDescription(context, response));
                dialogHandler.showMessage();

            }


        }

    }

    private static String prepareContent(DatasetInfoHolder info, ArrayList<Group> groups) {
        JsonObject content = new JsonObject();
        JsonArray values = putFieldValuesInJson(groups);
        // Retrieve current date
        LocalDate currentDate = new LocalDate();
        String completeDate = currentDate.toString(Constants.DATE_FORMAT);
        Log.d("completeDate--", completeDate);
        content.addProperty(Constants.ORG_UNIT_ID, info.getOrgUnitId());
//        content.addProperty(Constants.ORG_UNIT_ID, "bptHYMPdxEj");
        content.addProperty(Constants.DATA_SET_ID, info.getFormId());
        content.addProperty(Constants.PERIOD, info.getPeriod());
        if (server_date != null) {
            content.addProperty(Constants.COMPLETE_DATE, server_date);
        } else {
            content.addProperty(Constants.COMPLETE_DATE, completeDate);
        }
        content.add(Constants.DATA_VALUES, values);

        JsonArray categoryOptions = putCategoryOptionsInJson(info.getCategoryOptions());
        if (categoryOptions != null) {
            content.add(Constants.ATTRIBUTE_CATEGORY_OPTIONS, categoryOptions);
        }

        return content.toString();
    }

    private static String prepareContent_report(DatasetInfoHolder info, ArrayList<Group> groups) {
        JsonObject content = new JsonObject();
        JsonArray values = putFieldValuesInJson_report(groups);

        // Retrieve current date
        LocalDate currentDate = new LocalDate();
        String completeDate = currentDate.toString(Constants.DATE_FORMAT);

        content.addProperty(Constants.ORG_UNIT_ID, info.getOrgUnitId());
        content.addProperty(Constants.DATA_SET_ID, info.getFormId());
        content.addProperty(Constants.PERIOD, info.getPeriod());
        content.addProperty(Constants.COMPLETE_DATE, completeDate);
        content.add(Constants.DATA_VALUES, values);

        JsonArray categoryOptions = putCategoryOptionsInJson(info.getCategoryOptions());
        if (categoryOptions != null) {
            content.add(Constants.ATTRIBUTE_CATEGORY_OPTIONS, categoryOptions);
        }

        return content.toString();
    }

    private static JsonArray putCategoryOptionsInJson(List<CategoryOption> categoryOptions) {
        if (categoryOptions != null && !categoryOptions.isEmpty()) {
            JsonArray jsonOptions = new JsonArray();

            // processing category options
            for (CategoryOption categoryOption : categoryOptions) {
                jsonOptions.add(categoryOption.getId());
            }

            return jsonOptions;
        }

        return null;
    }


    private static JsonArray putFieldValuesInJson(ArrayList<Group> groups) {
        JsonArray jFields = new JsonArray();
        for (Group group : groups) {
            for (Field field : group.getFields()) {
                Log.d("dev_val--before", String.valueOf(dev_val));
                JsonObject jField = new JsonObject();
//                JsonObject jField1 = new JsonObject();
//                dev_val++;


                jField.addProperty(Field.DATA_ELEMENT, field.getDataElement());
                jField.addProperty(Field.CATEGORY_OPTION_COMBO, field.getCategoryOptionCombo());
                jField.addProperty(Field.VALUE, field.getValue());


                if (field.getValue().length() > 0) {

                    jFields.add(jField);
                }

            }

        }
        JsonObject jField1 = new JsonObject();
        jField1.addProperty(Field.DATA_ELEMENT, dev_val_uid);
        jField1.addProperty(Field.VALUE, getDeviceName());
        jFields.add(jField1);
        Log.d("jField1---", jField1.toString());
        return jFields;
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;

        StringBuilder phrase = new StringBuilder();
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase.append(c);
        }

        return phrase.toString();
    }

    private static JsonArray putFieldValuesInJson_report(ArrayList<Group> groups) {
        JsonArray jFields = new JsonArray();
        for (Group group : groups) {
            for (Field field : group.getFields()) {
                JsonObject jField = new JsonObject();
                jField.addProperty(Field.DATA_ELEMENT, field.getDataElement());
                jField.addProperty(Field.CATEGORY_OPTION_COMBO, field.getCategoryOptionCombo());
                jField.addProperty(Field.VALUE, field.getValue());
                jField.addProperty(Field.COMMENT, getDeviceName());

                jFields.add(jField);


            }
        }
        return jFields;
    }

    private static void saveDatasetOfflineR(Context context, String data, DatasetInfoHolder info) {
        String key = DatasetInfoHolder.buildKey(info);
        Gson gson = new Gson();
        String jsonReportInfo = gson.toJson(info);
        PrefUtils.saveOfflineReportInfo(context, key, jsonReportInfo);
        TextFileUtils.writeTextFile(context, TextFileUtils.Directory.OFFLINE_DATASETS_, key, data);
        sendBroadcastSavedOffline(info, context);

    }

    private static void saveDataset(Context context, String data, DatasetInfoHolder info) {
        String key = DatasetInfoHolder.buildKey(info);
        Gson gson = new Gson();
        String jsonReportInfo = gson.toJson(info);
        PrefUtils.saveOfflineReportInfo(context, key, jsonReportInfo);
        TextFileUtils.writeTextFile(context, TextFileUtils.Directory.OFFLINE_DATASETS, key, data);
        sendBroadcastSavedOffline(info, context);
    }

    private static void sendBroadcastSavedOffline(DatasetInfoHolder info, Context context) {
        Intent intent = new Intent(AggregateReportFragment.SAVED_OFFLINE_ACTION);
        intent.putExtra(DatasetInfoHolder.TAG, info);
        context.sendBroadcast(intent);
    }

    private static void sendBroadcastCorrectlyUpload(DatasetInfoHolder info, Context context) {
        Intent intent = new Intent(AggregateReportFragment.SAVED_ONLINE_ACTION);
        intent.putExtra(DatasetInfoHolder.TAG, info);
        context.sendBroadcast(intent);
    }
}
