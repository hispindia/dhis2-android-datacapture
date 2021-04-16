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

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.dhis2.mobile_uphmis.R;
import org.dhis2.mobile_uphmis.ui.activities.MenuActivity;
import org.dhis2.mobile_uphmis.utils.PrefUtils;
import org.dhis2.mobile_uphmis.utils.TextFileUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class report_analysis_offline extends AppCompatActivity {
    private static JSONObject datavalue_json = null;
    private static JSONObject periodLabel_dslabel = null;
    private static String lang = "";
    private static String block_parent = "";
    private static String periodlabel = "";
    private static String periodlabel_ = "";
    private static String perioddate = "";
    private static Button delete;
    private static ListView simpleList;
    private static final String OFFLINE_REPORTS_INFO = "OFFLINE_REPORTS_INFO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reportanalysis_offline_listview);

        delete = (Button) findViewById(R.id.button_delete);
        final ArrayList<String> countryList = new ArrayList<String>();
        final ArrayList<String> countryList_down = new ArrayList<String>();
        lang = PrefUtils.getLocale(getBaseContext());
        block_parent = PrefUtils.getBlockParent(getBaseContext());
        if (lang != null && lang.equals("hi")) {
            Locale locale = new Locale("hi");
            Locale.setDefault(locale);
            Configuration config = getBaseContext().getResources().getConfiguration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());
        }
        String path = TextFileUtils.getDirectoryPath(getBaseContext(), TextFileUtils.Directory.OFFLINE_DATASETS_);
//        String path = TextFileUtils.getDirectoryPath(getBaseContext(), TextFileUtils.Directory.OFFLINE_DATASETS);
        File directory = new File(path);
        if (!directory.exists()) {
            return;
        }
        File[] reportFiles = directory.listFiles();
        Gson gson = new Gson();
        if (reportFiles != null && reportFiles.length > 0) {
            for (File reportFile : reportFiles) {
//                String jsonDatasetInfo = PrefUtils.getOfflineReportInfo(getBaseContext(),
//                        reportFile.getName());
                String jsonDatasetInfo = getApplicationContext().getSharedPreferences(OFFLINE_REPORTS_INFO, Context.MODE_PRIVATE).getString(reportFile.getName(), null);
                try {

                    if (jsonDatasetInfo != null) {
                        periodLabel_dslabel = new JSONObject(jsonDatasetInfo);
                        periodlabel = periodLabel_dslabel.getString("periodLabel");
                        periodlabel_ = block_parent + "/" + periodLabel_dslabel.getString("periodLabel");

                        countryList.add(periodlabel);
                        countryList_down.add(periodlabel_);

                    } else {
                        String des_down = block_parent + "/" + reportFile.getName().substring(reportFile.getName().length() - 6);
                        countryList.add(reportFile.getName().substring(reportFile.getName().length() - 6));
                        countryList_down.add(des_down);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            simpleList = (ListView) findViewById(R.id.simpleListView);
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(report_analysis_offline.this, R.layout.activity_listview, R.id.textView, countryList);
            simpleList.setAdapter(arrayAdapter);

            simpleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent offline_report = new Intent(report_analysis_offline.this, report_analysis_offline_data.class);
                    offline_report.putExtra("REPORT_ID", position);
                    offline_report.putExtra("DOWN_PERIOD", countryList_down.get(position));
                    offline_report.putExtra("PERIOD", countryList.get(position));
                    startActivity(offline_report);

                }
            });

        }
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(report_analysis_offline.this);

                builder.setTitle(R.string.delete_offline);
                builder.setMessage(R.string.delete_offline_confirm);

                builder.setPositiveButton(R.string.dialog_exit_survey_yes,
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                String path = TextFileUtils.getDirectoryPath(getBaseContext(), TextFileUtils.Directory.OFFLINE_DATASETS_);
                                File directory = new File(path);
                                if (!directory.exists()) {
                                    return;
                                }
                                File[] reportFiles = directory.listFiles();
                                if (reportFiles != null && reportFiles.length > 0) {
                                    for (File reportFile : reportFiles) {
                                        TextFileUtils.removeFile(reportFile);
                                        PrefUtils.removeOfflineReportInfo(getBaseContext(), reportFile.getName());
                                    }
                                }

                                Toast.makeText(getBaseContext(), R.string.offline_reports_delete_msg,
                                        Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(report_analysis_offline.this, MenuActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        });

                builder.setNegativeButton(R.string.dialog_exit_survey_no,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                AlertDialog alert = builder.create();
                alert.show();

            }
        });

    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(report_analysis_offline.this, MenuActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }
}