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
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.brsatalay.htmltablebuilder.HtmlTableBuilder;
import com.brsatalay.htmltablebuilder.model.HtmlTableBuilderListener;
import com.brsatalay.htmltablebuilder.model.enumeration.enmAlignment;
import com.brsatalay.htmltablebuilder.model.enumeration.enmCellValueType;
import com.brsatalay.htmltablebuilder.model.mdlFooterCell;
import com.brsatalay.htmltablebuilder.model.mdlGridCell;
import com.brsatalay.htmltablebuilder.model.mdlHeaderCell;
import com.google.gson.Gson;

import org.dhis2.mobile_uphmis.R;

import org.dhis2.mobile_uphmis.io.holders.DatasetInfoHolder;
import org.dhis2.mobile_uphmis.utils.PrefUtils;
import org.dhis2.mobile_uphmis.utils.TextFileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;


public class report_analysis_offline_data extends AppCompatActivity {
    private TextView mytextview1;
    private TextView mytextview2;
    private TextView mytextview3;
    private ImageButton button_left;
    private WebView mywebview;
    private static JSONObject datavalue_json = null;
    private static JSONObject json_form = null;
    private static JSONObject datavalue = null;
    private static JSONObject jobj = new JSONObject();
    private static String lang = "";
    private static Integer report_no = 0;
    private static String period = "";
    private static String birth_defect = "uXSGoAqZ47g";
    private static String weight_less_25 = "uUXmboutton";
    private static String weight_less_than_18 = "yYVLFvBgeK4";
    private static String referred = "f1DO4jy18Jo";
    private static String deaths = "N11qQhEpjYr";
    List<ReportData> result = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reportanalysis_offline);
        Bundle extras = getIntent().getExtras();


        if (extras != null) {
            period = extras.getString("PERIOD");
            report_no = extras.getInt("REPORT_ID");
        }

        lang = PrefUtils.getLocale(getBaseContext());
        if (lang != null && lang.equals("hi")) {
            Locale locale = new Locale("hi");
            Locale.setDefault(locale);
            Configuration config = getBaseContext().getResources().getConfiguration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());
        }


        setContentView(R.layout.reportanalysis_offline);
        button_left = findViewById(R.id.button_left);
        mywebview = findViewById(R.id.webViewoffline);
        mytextview1 = findViewById(R.id.mytextview1);
        mytextview2 = findViewById(R.id.mytextview2);
        mytextview3 = findViewById(R.id.mytextview3);
        String path = TextFileUtils.getDirectoryPath(getBaseContext(), TextFileUtils.Directory.OFFLINE_DATASETS_);
        button_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(report_analysis_offline_data.this, report_analysis_offline.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        String jForm = TextFileUtils.readTextFile(
                getBaseContext(), TextFileUtils.Directory.DATASETS, "gQVY1wTuiIU");
        try {
            json_form = new JSONObject(jForm);
            JSONArray arr = json_form.getJSONArray("groups");
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                String id = o.getString("fields");
                String section = o.getString("label");
                JSONArray jsonArr = new JSONArray(id);
                //@Sou to do for section show on report
                for (int j = 0; j < jsonArr.length(); j++) {
                    JSONObject jsonObj = jsonArr.getJSONObject(j);
                    jobj.put(jsonObj.get("dataElement") + "." + jsonObj.get("categoryOptionCombo"), jsonObj.get("label"));

                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        File directory = new File(path);
        if (!directory.exists()) {
            return;
        }
        File[] reportFiles = directory.listFiles();
        Gson gson = new Gson();
        String report = TextFileUtils.readTextFile(reportFiles[report_no]);
        JsonParser jsonParser = new JsonParser();
        JsonElement element = jsonParser.parse(report);
        try {
            datavalue_json = new JSONObject(report);
            JSONArray arr = datavalue_json.getJSONArray("dataValues");
            ReportData report_data = new ReportData();
            report_data.setName("test");
            report_data.setId("test");
            report_data.setPrice("test");
            mytextview1.setText(period);
            mytextview2.setText(getString(R.string.name));
            mytextview3.setText(getString(R.string.price));
            //Designed specifically for Integrated dataSet. 
            for (int i = 0; i < arr.length(); i++) {

                if (i == 133) {

                } else {
                    JSONObject o = arr.getJSONObject(i);
                    Log.d("de-list----", o.get("dataElement").toString());
                    String decat = o.get("dataElement").toString() + "." + o.get("categoryOptionCombo").toString();
                    String label = jobj.getString(decat);
                    Log.d("decat--" + i, decat);
                    Log.d("label--" + i, label);

                    report_data = new ReportData();
                    if (label.contains(" ")) {
                        if ((Character.isWhitespace(label.charAt(0)))) {
                            String w_label = label.replaceFirst("^ *", "");
                            report_data.setId(w_label.substring(0, w_label.indexOf(" ")));
                            report_data.setName(w_label.substring(w_label.indexOf(" "), w_label.length()));
                        } else {

                            report_data.setId(label.substring(0, label.indexOf(" ")));
                            report_data.setName(label.substring(label.indexOf(" "), label.length()));
                        }
                        if (o.get("dataElement").toString().equals("PgCK0srUhVZ")) {
                            if (o.get("categoryOptionCombo").toString().equals(birth_defect)) {

                                report_data.setId("U3.9.1");
                                report_data.setName(label);
                            } else if (o.get("categoryOptionCombo").toString().equals(weight_less_than_18)) {
                                report_data.setId("U3.9.3");
                                report_data.setName(label);
                            }

                            if (o.get("categoryOptionCombo").toString().equals(weight_less_25)) {
                                report_data.setId("U3.9.2");
                                report_data.setName(label);
                            }
                            if (o.get("categoryOptionCombo").toString().equals(referred)) {
                                report_data.setId("U3.9.4");
                                report_data.setName(label);
                            }
                            if (o.get("categoryOptionCombo").toString().equals(deaths)) {
                                report_data.setId("U3.9.5");
                                report_data.setName(label);
                            }

                        }
                        if (o.get("dataElement").toString().equals("i4FPzOZoySP") && label.contains("Pregnant")) {
                            if (o.get("categoryOptionCombo").toString().equals("JQZYTwtWe9F")) {
                                report_data.setId("U1.1.1");
                                report_data.setName("Haemoglobin (Hb )");
                            } else if (o.get("categoryOptionCombo").toString().equals("Jrg2sxz2fFR")) {
                                report_data.setId("U1.1.2");
                                report_data.setName("Blood pressure");
                            } else if (o.get("categoryOptionCombo").toString().equals("N2WiGW4eJMI")) {
                                report_data.setId("U1.1.3");
                                report_data.setName("Urine albumin");
                            } else if (o.get("categoryOptionCombo").toString().equals("Gv4pdY5V5fu")) {
                                report_data.setId("U1.1.4");
                                report_data.setName("Abdominal check");
                            } else if (o.get("categoryOptionCombo").toString().equals("B6SZw6rCwqH")) {
                                report_data.setId("U1.1.5");
                                report_data.setName("Weight");
                            }

                        }
                        if (o.get("dataElement").toString().equals("OrMq254iPQ2") && label.contains("Still")) {
                            if (o.get("categoryOptionCombo").toString().equals("LeWpv23NQE0")) {
                                report_data.setId("U2.1.1");
                                report_data.setName("Fresh still birth");
                            } else if (o.get("categoryOptionCombo").toString().equals("ocOywnb7dim")) {
                                report_data.setId("U2.1.2");
                                report_data.setName("Macerated still birth");
                            }

                        }
                        if (o.get("dataElement").toString().equals("OrMq254iPQ2") && label.contains("मृत")) {
                            if (o.get("categoryOptionCombo").toString().equals("LeWpv23NQE0")) {
                                report_data.setId("U2.1.1");
                                report_data.setName("मृत जन्म (फ्रेश)");
                            } else if (o.get("categoryOptionCombo").toString().equals("ocOywnb7dim")) {
                                report_data.setId("U2.1.2");
                                report_data.setName("मृत जन्म (मैसरेटेड)");
                            }

                        }

                        if (o.get("dataElement").toString().equals("NeNWp698eve") && label.contains("DMPA")) {
                            if (o.get("categoryOptionCombo").toString().equals("e4vOZhidnTl")) {
                                report_data.setId("5.5");
                                report_data.setName("Injectable Contraceptive-Antara Program- First Dose");
                            } else if (o.get("categoryOptionCombo").toString().equals("JD3TbSsgoKa")) {
                                report_data.setId("5.6");
                                report_data.setName("Injectable Contraceptive-Antara Program- Second Dose");
                            } else if (o.get("categoryOptionCombo").toString().equals("gZiING8oxqo")) {
                                report_data.setId("5.7");
                                report_data.setName("Injectable Contraceptive-Antara Program- Third Dose");
                            } else if (o.get("categoryOptionCombo").toString().equals("uBLH63dNSeY")) {
                                report_data.setId("5.8");
                                report_data.setName("Injectable Contraceptive-Antara Program- Fourth or more than fourth");
                            }


                        }
                        if (o.get("dataElement").toString().equals("W7I9VJKykwv") && label.contains("Child")) {
                            if (o.get("categoryOptionCombo").toString().equals("KsGXFEh1Rt2")) {
                                report_data.setId("7.1");
                                report_data.setName("Childhood Diseases - Tuberculosis (TB)");
                            } else if (o.get("categoryOptionCombo").toString().equals("TlWl1dykAT6")) {
                                report_data.setId("7.2");
                                report_data.setName("7.2 Childhood Diseases - Acute Flaccid Paralysis(AFP)");
                            } else if (o.get("categoryOptionCombo").toString().equals("M1cHSRnXxnZ")) {
                                report_data.setId("7.3");
                                report_data.setName("7.3 Childhood Diseases - Measles");
                            } else if (o.get("categoryOptionCombo").toString().equals("YjTqbjZbwk7")) {
                                report_data.setId("7.4");
                                report_data.setName("7.4 Childhood Diseases - Malaria");
                            }

                        }

                        if (o.get("dataElement").toString().equals("xfFUwIeiPY1") && label.contains("Outpatient")) {
                            if (o.get("categoryOptionCombo").toString().equals("EnQPoVNAi2p")) {
                                report_data.setId("9.1.1");
                                report_data.setName("Outpatient - Diabetes");
                            } else if (o.get("categoryOptionCombo").toString().equals("Mmdd2bEh3Yp")) {
                                report_data.setId("9.1.2");
                                report_data.setName("Outpatient - Hypertension");
                            } else if (o.get("categoryOptionCombo").toString().equals("vE5EsFqAEI2")) {
                                report_data.setId("9.1.3");
                                report_data.setName("Outpatient - Stroke (Paralysis)");
                            } else if (o.get("categoryOptionCombo").toString().equals("MqKow2RWPVy")) {
                                report_data.setId("9.1.4");
                                report_data.setName("Outpatient - Acute Heart Diseases");
                            } else if (o.get("categoryOptionCombo").toString().equals("srquQZDqZof")) {
                                report_data.setId("9.1.5");
                                report_data.setName("Outpatient - Mental illness");
                            } else if (o.get("categoryOptionCombo").toString().equals("X4UVVhdHhjq")) {
                                report_data.setId("9.1.6");
                                report_data.setName("Outpatient - Epilepsy");
                            } else if (o.get("categoryOptionCombo").toString().equals("NbjYj1XvveT")) {
                                report_data.setId("9.1.7");
                                report_data.setName("Outpatient - Ophthalmic Related");
                            } else if (o.get("categoryOptionCombo").toString().equals("W1M1x4zDNZm")) {
                                report_data.setId("9.1.8");
                                report_data.setName("Outpatient - Dental");
                            }

                        }

                        if (o.get("dataElement").toString().equals("eBtyQP09Rgr") && label.contains("Infant")) {
                            if (o.get("categoryOptionCombo").toString().equals("UJ0sOkgjk9z")) {
                                report_data.setId("12.2.1");
                                report_data.setName("Infant Deaths up to 4 weeks due to Sepsis");
                            } else if (o.get("categoryOptionCombo").toString().equals("JXCJchaWpaO")) {
                                report_data.setId("12.2.2");
                                report_data.setName("Infant Deaths up to 4 weeks due to Asphyxia");
                            } else if (o.get("categoryOptionCombo").toString().equals("OTMSwDAdh0X")) {
                                report_data.setId("12.2.3");
                                report_data.setName("Infant Deaths up to 4 weeks due to Other causes");
                            }

                        }
                        if (o.get("dataElement").toString().equals("HOrR1amEU6x")) {
                            if (o.get("categoryOptionCombo").toString().equals("WVDe9q7ihBV")) {
                                report_data.setId("2.1.1.a");

                            } else if (o.get("categoryOptionCombo").toString().equals("YOjJNrvr2j2")) {
                                report_data.setId("2.1.1.b");

                            }

                        }
                        if (o.get("dataElement").toString().equals("r6Pc8UKQWel") && label.contains("Infant")) {
                            if (o.get("categoryOptionCombo").toString().equals("DQ8rocRNUvB")) {
                                report_data.setId("12.3.1");
                                report_data.setName("Number of Infant Deaths (1 -12 months) due to Pneumonia");
                            } else if (o.get("categoryOptionCombo").toString().equals("MIAeghFCobW")) {
                                report_data.setId("12.3.2 ");
                                report_data.setName("Number of Infant Deaths (1 -12 months) due to Diarrhoea");
                            } else if (o.get("categoryOptionCombo").toString().equals("uqNDgFiY3VA")) {
                                report_data.setId("12.3.3 ");
                                report_data.setName("Number of Infant Deaths (1 -12 months) due to Fever related");
                            } else if (o.get("categoryOptionCombo").toString().equals("pmUrHNUQG5I")) {
                                report_data.setId("12.3.4");
                                report_data.setName("Number of Infant Deaths (1 -12 months) due to Measles");
                            } else if (o.get("categoryOptionCombo").toString().equals("iyrx3OktTcp")) {
                                report_data.setId("12.3.5");
                                report_data.setName("Number of Infant Deaths (1 -12 months) due to Others");
                            }

                        }
                        if (o.get("dataElement").toString().equals("F7obafTy4O4") && label.contains("Child")) {
                            if (o.get("categoryOptionCombo").toString().equals("aIDgj40tNAU")) {
                                report_data.setId("12.4.1");
                                report_data.setName("Number of Child Deaths (1 -5 years) due to Pneumonia");
                            } else if (o.get("categoryOptionCombo").toString().equals("fDl9q7Z74LG")) {
                                report_data.setId("12.4.2");
                                report_data.setName("Number of Child Deaths (1 -5 years) due to Diarrhoea");
                            } else if (o.get("categoryOptionCombo").toString().equals("T5uieqjIR3C")) {
                                report_data.setId("12.4.3");
                                report_data.setName("Number of Child Deaths (1 -5 years) due to Fever related");
                            } else if (o.get("categoryOptionCombo").toString().equals("xrdxHeoBFxV")) {
                                report_data.setId("12.4.4");
                                report_data.setName("Number of Child Deaths (1 -5 years) due to Measles");
                            } else if (o.get("categoryOptionCombo").toString().equals("D80VASkIs6m")) {
                                report_data.setId("12.4.5");
                                report_data.setName("Number of Child Deaths (1 -5 years) due to Others");
                            }

                        }
                        if (o.get("dataElement").toString().equals("KFyojgVb8Go") && label.contains("Maternal")) {
                            if (o.get("categoryOptionCombo").toString().equals("hbUkGAlIwu8")) {
                                report_data.setId("12.5.1");
                                report_data.setName("Number of Maternal Deaths due to Bleeding");
                            } else if (o.get("categoryOptionCombo").toString().equals("PWn0jSvveTs")) {
                                report_data.setId("12.5.2");
                                report_data.setName("Number of Maternal Deaths due to High fever");
                            } else if (o.get("categoryOptionCombo").toString().equals("bg50uiDLvlP")) {
                                report_data.setId("12.5.3");
                                report_data.setName("Number of Maternal Deaths due to Abortion");
                            } else if (o.get("categoryOptionCombo").toString().equals("mrHdT99cCnv")) {
                                report_data.setId("12.5.4");
                                report_data.setName("Number of Maternal Deaths due to Obstructed/prolonged labour");
                            } else if (o.get("categoryOptionCombo").toString().equals("v3ourGcXDlN")) {
                                report_data.setId("12.5.5");
                                report_data.setName("Number of Maternal Deaths due to Severe hypertesnion/fits");
                            } else if (o.get("categoryOptionCombo").toString().equals("zJCowC71NtF")) {
                                report_data.setId("12.5.6");
                                report_data.setName("Number of Maternal Deaths due to Other Causes (including causes Not Known)");
                            }

                        }

                        if (o.get("dataElement").toString().equals("NbxHvSiZYyA") && label.contains("Adol")) {
                            if (o.get("categoryOptionCombo").toString().equals("FktbJ7yFsse")) {
                                report_data.setId("12.6.1");
                                report_data.setName("Number of Adolscent / Adult Deaths due to Diarrhoeal diseases");
                            } else if (o.get("categoryOptionCombo").toString().equals("kC2hbiJtBcR")) {
                                report_data.setId("12.6.2");
                                report_data.setName("Number of Adolscent / Adult Deaths due to Tuberculosis");
                            } else if (o.get("categoryOptionCombo").toString().equals("RxHfyTB6IN3")) {
                                report_data.setId("12.6.3");
                                report_data.setName("Number of Adolscent / Adult Deaths due to Respiratory diseases including infections (other than TB)");
                            } else if (o.get("categoryOptionCombo").toString().equals("SUusts83kz5")) {
                                report_data.setId("12.6.4");
                                report_data.setName("Number of Adolscent / Adult Deaths due to Other Fever Related");
                            } else if (o.get("categoryOptionCombo").toString().equals("ig3ZBsF2AQS")) {
                                report_data.setId("12.6.5");
                                report_data.setName("Number of Adolscent / Adult Deaths due to HIV/AIDS");
                            } else if (o.get("categoryOptionCombo").toString().equals("Qu2SnqP7uDc")) {
                                report_data.setId("12.6.6");
                                report_data.setName("Number of Adolscent / Adult Deaths due to Heart disease/Hypertension related");
                            } else if (o.get("categoryOptionCombo").toString().equals("KoIuSxCUlOt")) {
                                report_data.setId("12.6.7");
                                report_data.setName("Number of Adolscent / Adult Deaths due to Cancer");
                            } else if (o.get("categoryOptionCombo").toString().equals("wPuxGm0H9B1")) {
                                report_data.setId("12.6.8");
                                report_data.setName("Number of Adolscent / Adult Deaths due to Neurological disease including strokes");
                            } else if (o.get("categoryOptionCombo").toString().equals("t7hLquJcQx6")) {
                                report_data.setId("12.6.9");
                                report_data.setName("Number of Adolscent / Adult Deaths due to Accidents/Burn cases");
                            } else if (o.get("categoryOptionCombo").toString().equals("qU3MDwWhTSO")) {
                                report_data.setId("12.6.10");
                                report_data.setName("Number of Adolscent / Adult Deaths due to Suicide");
                            } else if (o.get("categoryOptionCombo").toString().equals("SGz3Yjh5Poz")) {
                                report_data.setId("12.6.11");
                                report_data.setName("Number of Adolscent / Adult deaths due to Animal bites and stings");
                            } else if (o.get("categoryOptionCombo").toString().equals("EHwU077SibV")) {
                                report_data.setId("12.6.12");
                                report_data.setName("Number of Adolscent / Adult deaths due to Known Acute Disease");
                            } else if (o.get("categoryOptionCombo").toString().equals("PuwbCFzmkKp")) {
                                report_data.setId("12.6.13");
                                report_data.setName("Number of Adolscent / Adult deaths due to Known Chronic Disease");
                            } else if (o.get("categoryOptionCombo").toString().equals("CslMXpOUXWt")) {
                                report_data.setId("12.6.14");
                                report_data.setName("Number of Adolscent / Adult deaths due to Causes Not Known");
                            }

                        }
                        if (o.get("dataElement").toString().equals("Lb2DzKeoiV4")) {
                            if (o.get("categoryOptionCombo").toString().equals(birth_defect)) {

                                report_data.setId("U3.10.1");
                                report_data.setName(label);
                            } else if (o.get("categoryOptionCombo").toString().equals(weight_less_than_18)) {
                                report_data.setId("U3.10.3");
                                report_data.setName(label);
                            }
                            if (o.get("categoryOptionCombo").toString().equals(weight_less_25)) {
                                report_data.setId("U3.10.2");
                                report_data.setName(label);
                            }
                            if (o.get("categoryOptionCombo").toString().equals(referred)) {
                                report_data.setId("U3.10.4");
                                report_data.setName(label);
                            }
                            if (o.get("categoryOptionCombo").toString().equals(deaths)) {
                                report_data.setId("U3.10.5");
                                report_data.setName(label);
                            }

                        }
                        if (o.get("dataElement").toString().equals("GQHVHVJFf3g")) {
                            if (o.get("categoryOptionCombo").toString().equals("iRNhRMvoSCx")) {

                                report_data.setId("5.14.1");
                                report_data.setName(label);
                            } else if (o.get("categoryOptionCombo").toString().equals("wb51FJHqHxp")) {
                                report_data.setId("5.14.2");
                                report_data.setName(label);
                            }

                        }
                        if (o.get("dataElement").toString().equals("GJKYhq2wR9L")) {
                            if (o.get("categoryOptionCombo").toString().equals("iRNhRMvoSCx")) {

                                report_data.setId("6.2.4.a");
                                report_data.setName(label);
                            } else if (o.get("categoryOptionCombo").toString().equals("wb51FJHqHxp")) {
                                report_data.setId("6.2.4.b");
                                report_data.setName(label);
                            }

                        }
                        if (o.get("dataElement").toString().equals("aknlXIekL1Z")) {
                            if (o.get("categoryOptionCombo").toString().equals("iRNhRMvoSCx")) {

                                report_data.setId("3.1.1.a");
                                report_data.setName(label);
                            } else if (o.get("categoryOptionCombo").toString().equals("wb51FJHqHxp")) {
                                report_data.setId("3.1.1.b");
                                report_data.setName(label);
                            }

                        }

                    }


                    if (o.get("value").toString().matches("Adequate|adequate") && lang.equals("hi")) {
                        report_data.setPrice("पर्याप्त");
                    } else if (o.get("value").toString().matches("Inadequate|inadequate") && lang.equals("hi")) {
                        report_data.setPrice("अपर्याप्त");
                    } else if (o.get("value").toString().matches("Novalue|novalue") && lang.equals("hi")) {
                        report_data.setPrice("कोई नहीं");
                    } else if (o.get("value").toString().equals("true") && lang.equals("hi")) {
                        report_data.setPrice("हाँ");
                    } else if (o.get("value").toString().equals("false") && lang.equals("hi")) {
                        report_data.setPrice("नहीं");
                    } else if (o.get("dataElement").toString().equals("YoIJ4GhA4En") || o.get("dataElement").toString().equals("xdnUfX8AVc2")) {
                        String startTime = o.get("value").toString();
                        SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
                        SimpleDateFormat df3 = new SimpleDateFormat("dd-MM-yyyy");

                        try {
                            Date dateValue = df2.parse(startTime);
                            report_data.setPrice(df3.format(dateValue));
                        } catch (ParseException e) {              // Insert this block.
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }


                    } else {

                        report_data.setPrice(o.get("value").toString());
                    }
                    result.add(report_data);
                }


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String jsonDatasetInfo = PrefUtils.getOfflineReportInfo(getBaseContext(),
                reportFiles[report_no].getName());
        DatasetInfoHolder info = gson.fromJson(jsonDatasetInfo, DatasetInfoHolder.class);

//            }
//        }
        mywebview = findViewById(R.id.webViewoffline);
        HtmlTableBuilder tableBuilder = new HtmlTableBuilder(getApplicationContext(), true);
        tableBuilder.setNoDataText("No Data");
        tableBuilder.setOutOfMemoryText("Due to excessive data, report can not be displayed. Please try again with more applicable search criteria.");
        tableBuilder.setHeaderInfos(prepareHeaders());
        tableBuilder.setFooterInfos(prepareFooterList());
        tableBuilder.setDataSet(result);
        tableBuilder.setListener(new HtmlTableBuilderListener() {
            @Override
            public void onDrawCell(int dataIndex, mdlGridCell cell) {
                boolean isMoneyCol = cell.getValueType() == enmCellValueType.Decimal;
                if (dataIndex != 0 && (dataIndex % 2) == 0) {
                    cell.setBackgroundColor(!isMoneyCol ? "#F2F7FF" : "#B3D1B3");
                    cell.setFontSize(25);
                } else if (!isMoneyCol) {
                    cell.setBackgroundColor("white");
                    cell.setFontSize(25);

                } else
                    cell.setBackgroundColor("#C0DCC0");
            }

            @Override
            public void onDrawColumnHeader(int dataIndex, mdlGridCell cell) {
            }

            @Override
            public void onCalcFields(int rowIndex, LinkedHashMap<String, mdlGridCell> rowData) {

            }
        });
        String htmlPath = tableBuilder.build("table.html");
        mywebview.getSettings().setLoadWithOverviewMode(true);
        mywebview.getSettings().setUseWideViewPort(true);
        mywebview.getSettings().setBuiltInZoomControls(true);
        //@Sou set zoom in level tabular report
        mywebview.setInitialScale(80);
        mywebview.getSettings().setDisplayZoomControls(true);
        mywebview.getSettings().setJavaScriptEnabled(true);
        mywebview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        mywebview.loadUrl("file:///" + htmlPath);
        findViewById(R.id.fab).setVisibility(View.VISIBLE);
    }

    private void createWebPrintJob(WebView webView) {

//        create object of print manager in your device
        PrintManager printManager = (PrintManager) this.getSystemService(Context.PRINT_SERVICE);

        PrintDocumentAdapter printAdapter;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            printAdapter = mywebview.createPrintDocumentAdapter(period + ".pdf");
        } else {
            printAdapter = mywebview.createPrintDocumentAdapter();
        }

        String jobName = getString(R.string.app_name) + " Document";

        PrintAttributes attributes = new PrintAttributes.Builder()
                .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                .setResolution(new PrintAttributes.Resolution("id", Context.PRINT_SERVICE, 200, 200))
                .setColorMode(PrintAttributes.COLOR_MODE_COLOR)
                .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                .build();

        printManager.print(jobName, printAdapter, attributes);


    }

    //perform click pdf creation operation on click of print button click
    public void printPDF(View view) {
        createWebPrintJob(mywebview);
    }

    private LinkedHashMap<String, mdlFooterCell> prepareFooterList() {
        LinkedHashMap<String, mdlFooterCell> footerInfos = new LinkedHashMap<>();

        footerInfos.put("id", new mdlFooterCell.Builder()
                .value("")
                .backgroundColor("#ffa500")
                .build());

        footerInfos.put("price", new mdlFooterCell.Builder()
                .value("")
                .backgroundColor("#ffa500")
                .build());
        footerInfos.put("name", new mdlFooterCell.Builder()
//                .value(getString(R.string.report_heading)+" -")
                .value("")
                .backgroundColor("#ffa500")
                .build());
        return footerInfos;
    }

    private LinkedHashMap<String, mdlHeaderCell> prepareHeaders() {
        LinkedHashMap<String, mdlHeaderCell> headerInfos = new LinkedHashMap<>();
        headerInfos.put("id", new mdlHeaderCell.Builder()
                .value("")
                .backgroundColor("#ffa500")
                .fontSize(1)
                .alignment(enmAlignment.Center)
                .valueType(enmCellValueType.Integer)
                .width(200)
                .build());

        headerInfos.put("name", new mdlHeaderCell.Builder()
//                .value(getString(R.string.report_heading)+" -")
                .value("")
                .alignment(enmAlignment.LeftJustify)
                .backgroundColor("#ffa500")
                .valueType(enmCellValueType.String)
                .fontSize(1)
                .width(600)
                .build());

        headerInfos.put("price", new mdlHeaderCell.Builder()
                .value("")
                .alignment(enmAlignment.Center)
                .backgroundColor("#ffa500")
                .valueType(enmCellValueType.String)
                .fontSize(1)
                .width(170)
                .build());
        return headerInfos;
    }

    private int getScale() {
        Display display = ((WindowManager) getSystemService(getBaseContext().WINDOW_SERVICE)).getDefaultDisplay();
        int width = display.getWidth();
        Double val = new Double(width);
        val = val * 100d;
        return val.intValue();
    }
}
