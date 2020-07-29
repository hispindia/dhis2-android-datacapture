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

import java.util.ArrayList;
import java.util.List;

public class ReportUploadProcessor {
    public static final String TAG = ReportUploadProcessor.class.getSimpleName();

    private ReportUploadProcessor() {
    }

    public static void upload(Context context, DatasetInfoHolder info, ArrayList<Group> groups) {
        String data = prepareContent(info, groups);
        //@Sou to save all values to local dataSet
        String data_offline = prepareContent_report(info, groups);
//        if (data.length() > 4000) {
//            Log.v(TAG, "sb.length = " + data.length());
//            int chunkCount = data.length() / 4000;     // integer division
//            for (int i = 0; i <= chunkCount; i++) {
//                int max = 4000 * (i + 1);
//                if (max >= data.length()) {
//                    Log.v(TAG, "chunk " + i + " of " + chunkCount + ":" + data.substring(4000 * i));
//                } else {
//                    Log.v(TAG, "chunk " + i + " of " + chunkCount + ":" + data.substring(4000 * i, max));
//                }
//            }
//        } else {
//            Log.v(TAG, data.toString());
//        }
//        Log.d("data--",data);
        //ToDo @Sou tabular report save datga offline
        saveDatasetOfflineR(context, data_offline, info);
        saveDataset(context, data, info);
//        if (!NetworkUtils.checkConnection(context)) {
//            saveDataset(context, data, info);
//            return;
//        }

        String url = PrefUtils.getServerURL(context) + URLConstants.DATASET_UPLOAD_URL;
        String creds = PrefUtils.getCredentials(context);
        Log.i(TAG, data);
        Response response = HTTPClient.post(url, creds, data);

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
//                sendBroadcastSavedOffline(info, context);
                DialogHandler dialogHandler = new DialogHandler(SyncLogger.getResponseDescription(context, response));
                dialogHandler.showMessage();
//                SyncLogger.logNetworkError(context, response, info, false);
            }


        }
        //@Sou temperory fox for network error
//        } else {
//            DialogHandler dialogHandler = new DialogHandler(SyncLogger.getErrorMessage(context, info, response, true));
//            dialogHandler.showMessage();
//
//            SyncLogger.logNetworkError(context, response, info, false);
//
//            saveDataset(context, data, info);
//        }
    }

    private static String prepareContent(DatasetInfoHolder info, ArrayList<Group> groups) {
        JsonObject content = new JsonObject();
        JsonArray values = putFieldValuesInJson(groups);

        // Retrieve current date
        LocalDate currentDate = new LocalDate();
        String completeDate = currentDate.toString(Constants.DATE_FORMAT);

        content.addProperty(Constants.ORG_UNIT_ID, info.getOrgUnitId());
//        content.addProperty(Constants.ORG_UNIT_ID, "bptHYMPdxEj");
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
                JsonObject jField = new JsonObject();
                jField.addProperty(Field.DATA_ELEMENT, field.getDataElement());
                jField.addProperty(Field.CATEGORY_OPTION_COMBO, field.getCategoryOptionCombo());
                jField.addProperty(Field.VALUE, field.getValue());

                //@Sou reduce the payload
//                field.getValue().replaceAll(" ", "");
//                if (field.getValue().length()>0 && !field.getValue().contains(" "))
                if (field.getValue().length()>0 )
                {

                    jFields.add(jField);
                }

            }
        }
        return jFields;
    }
    private static JsonArray putFieldValuesInJson_report(ArrayList<Group> groups) {
        JsonArray jFields = new JsonArray();
        for (Group group : groups) {
            for (Field field : group.getFields()) {
                JsonObject jField = new JsonObject();
                jField.addProperty(Field.DATA_ELEMENT, field.getDataElement());
                jField.addProperty(Field.CATEGORY_OPTION_COMBO, field.getCategoryOptionCombo());
                jField.addProperty(Field.VALUE, field.getValue());

                //@Sou reduce the payload
//                field.getValue().replaceAll(" ", "");
//                if (field.getValue().length()>0 )
//                {

                    jFields.add(jField);
//                }

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
//        TextFileUtils.writeTextFile(context, TextFileUtils.Directory.OFFLINE_DATASETS, key, data);
//        sendBroadcastSavedOffline(info, context);
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
