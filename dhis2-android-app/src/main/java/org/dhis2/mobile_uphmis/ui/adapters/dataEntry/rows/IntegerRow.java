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

package org.dhis2.mobile_uphmis.ui.adapters.dataEntry.rows;

import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.dhis2.mobile_uphmis.R;
import org.dhis2.mobile_uphmis.io.models.Field;
import org.dhis2.mobile_uphmis.utils.PrefUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextWatcher;

public class IntegerRow extends  EditTextRow implements Row {
    private SwipeRefreshLayout swipeRefreshLayout;
    private final LayoutInflater inflater;
    private final Field field;
    private  String flag="false";
    private  Boolean mm_ignore=true;
    private  Boolean english_locale=true;
    public boolean readOnly = false;
    public boolean readOnly_tablet = false;
    private static String minimum="";
    private static String maximum="";
    private static String minimum_value="";
    private static String de_minimum_value="";
    private static String maximum_value="";
    private static String de_maximum_value="";
    private static String still_fresh="";
    private static String still_mas="";
    private static String still_hmis="";
    private static String scroll_detect="false";
    private static JSONObject minimum_json=null ;
    private static JSONObject maximum_json=null ;
    private static JSONObject default_min=new JSONObject() ;
    private static JSONObject default_max=new JSONObject() ;
    public IntegerRow(LayoutInflater inflater, Field field) {
        this.inflater = inflater;
        this.field = field;

    }

    @Override
    public View getView(View convertView) {
        final View view;
        final EditTextHolder holder;

        if (convertView == null) {
            ViewGroup rowRoot = (ViewGroup) inflater.inflate(R.layout.listview_row_integer, null);
            TextView label = (TextView) rowRoot.findViewById(R.id.text_label);
            EditText editText = (EditText) rowRoot.findViewById(R.id.edit_integer_row);

            EditTextWatcher watcher = new EditTextWatcher(field);
            editText.addTextChangedListener(watcher);
            holder = new EditTextHolder(label, editText, watcher);
            rowRoot.setTag(holder);
            view = rowRoot;

        } else {
            view = convertView;
            holder = (EditTextHolder) view.getTag();
        }

        RowCosmetics.setTextLabel(field, holder.textLabel);
            //@Sou few of the labels defined below as not possible to set through default de-cat combo
            if (field.getDataElement().equals("PgCK0srUhVZ"))
            {
                if (field.getCategoryOptionCombo().equals("uXSGoAqZ47g"))
                {
                    holder.textLabel.setText("U3.9.1 "+holder.textLabel.getText());
                }
                else if (field.getCategoryOptionCombo().equals("uUXmboutton"))
                {
                    holder.textLabel.setText("U3.9.2 "+holder.textLabel.getText());
                }
                else if (field.getCategoryOptionCombo().equals("yYVLFvBgeK4"))
                {
                    holder.textLabel.setText("U3.9.3 "+holder.textLabel.getText());
                }
                else if (field.getCategoryOptionCombo().equals("f1DO4jy18Jo"))
                {
                    holder.textLabel.setText("U3.9.4 "+holder.textLabel.getText());
                }
                else if (field.getCategoryOptionCombo().equals("N11qQhEpjYr"))
                {
                    holder.textLabel.setText("U3.9.5 "+holder.textLabel.getText());
                }

            }

            if (field.getDataElement().equals("Lb2DzKeoiV4"))
            {
                if (field.getCategoryOptionCombo().equals("uXSGoAqZ47g"))
                {
                    holder.textLabel.setText("U3.10.1 "+holder.textLabel.getText());
                }
                else if (field.getCategoryOptionCombo().equals("uUXmboutton"))
                {
                    holder.textLabel.setText("U3.10.2 "+holder.textLabel.getText());
                }
                else if (field.getCategoryOptionCombo().equals("yYVLFvBgeK4"))
                {
                    holder.textLabel.setText("U3.10.3 "+holder.textLabel.getText());
                }
                else if (field.getCategoryOptionCombo().equals("f1DO4jy18Jo"))
                {
                    holder.textLabel.setText("U3.10.4 "+holder.textLabel.getText());
                }
                else if (field.getCategoryOptionCombo().equals("N11qQhEpjYr"))
                {
                    holder.textLabel.setText("U3.10.5 "+holder.textLabel.getText());
                }

            }
        if (field.getCategoryOptionCombo().contains("aRNxGm8EkXJ"))
        {
           String temp_1year= field.getLabel().replace("<1year","");
            holder.textLabel.setText(temp_1year);
        }
        if (field.getCategoryOptionCombo().contains("WlFfEfA9qiv"))
        {
           String temp_1year= field.getLabel().replace("Diarrhoea","");
            holder.textLabel.setText(temp_1year);
        }
        if (field.getCategoryOptionCombo().contains("am4rOt2jrXK"))
        {
           String temp_1year= field.getLabel().replace("By ANM","");
            holder.textLabel.setText(temp_1year);
        }
        if (field.getCategoryOptionCombo().contains("xgy9uLcxLiH"))
        {
           String temp_1year= field.getLabel().replace("Pneumonia","");
            holder.textLabel.setText(temp_1year);
        }

                if (field.getDataElement().equals("i4FPzOZoySP")&&field.getLabel().contains("Pregnant"))
                {
                    if (field.getCategoryOptionCombo().equals("JQZYTwtWe9F"))
                    {
                        holder.textLabel.setText("U1.1.1 Haemoglobin (Hb )");
                    }
                    else if (field.getCategoryOptionCombo().equals("Jrg2sxz2fFR"))
                    {
                        holder.textLabel.setText("U1.1.2 Blood pressure");
                    }
                    else if (field.getCategoryOptionCombo().equals("N2WiGW4eJMI"))
                    {
                        holder.textLabel.setText("U1.1.3 Urine albumin");
                    }
                    else if (field.getCategoryOptionCombo().equals("Gv4pdY5V5fu"))
                    {
                        holder.textLabel.setText("U1.1.4 Abdominal check");
                    }
                    else if (field.getCategoryOptionCombo().equals("B6SZw6rCwqH"))
                    {
                        holder.textLabel.setText("U1.1.5 Weight");
                    }

                }

                if (field.getDataElement().equals("i4FPzOZoySP")&&field.getLabel().contains("Pregnant"))
                {
                    if (field.getCategoryOptionCombo().equals("JQZYTwtWe9F"))
                    {
                        holder.textLabel.setText("U1.1.1 Haemoglobin (Hb )");
                    }
                    else if (field.getCategoryOptionCombo().equals("Jrg2sxz2fFR"))
                    {
                        holder.textLabel.setText("U1.1.2 Blood pressure");
                    }
                    else if (field.getCategoryOptionCombo().equals("N2WiGW4eJMI"))
                    {
                        holder.textLabel.setText("U1.1.3 Urine albumin");
                    }
                    else if (field.getCategoryOptionCombo().equals("Gv4pdY5V5fu"))
                    {
                        holder.textLabel.setText("U1.1.4 Abdominal check");
                    }
                    else if (field.getCategoryOptionCombo().equals("B6SZw6rCwqH"))
                    {
                        holder.textLabel.setText("U1.1.5 Weight");
                    }

                }

                if (field.getDataElement().equals("OrMq254iPQ2")&&field.getLabel().contains("birth"))
                {
                    if (field.getCategoryOptionCombo().equals("LeWpv23NQE0"))
                    {
                        holder.textLabel.setText("U2.1.1 Fresh still birth ");
                    }
                    else if (field.getCategoryOptionCombo().equals("ocOywnb7dim"))
                    {
                        holder.textLabel.setText("U2.1.2 Macerated still birth");
                    }

                }
                if (field.getDataElement().equals("HOrR1amEU6x")&&field.getLabel().contains("Home"))
                {
                    if (field.getCategoryOptionCombo().equals("WVDe9q7ihBV"))
                    {
                        holder.textLabel.setText("2.1.1.a Number of Home Deliveries attended by Skill Birth Attendant(SBA) (Doctor/Nurse/ANM)");
                    }
                    else if (field.getCategoryOptionCombo().equals("YOjJNrvr2j2"))
                    {
                        holder.textLabel.setText("2.1.1.b Number of Home Deliveries attended by Non SBA (Trained Birth Attendant(TBA) /Relatives/etc.)");
                    }

                }
                if (field.getDataElement().equals("OrMq254iPQ2")&&field.getLabel().contains("मृत"))
                {
                    if (field.getCategoryOptionCombo().equals("LeWpv23NQE0"))
                    {
                        holder.textLabel.setText("U2.1.1 मृत जन्म (फ्रेश)");
                    }
                    else if (field.getCategoryOptionCombo().equals("ocOywnb7dim"))
                    {
                        holder.textLabel.setText("U2.1.2 मृत जन्म (मैसरेटेड)");
                    }

                }


            if (field.getDataElement().equals("mexWK5BLs5H")&&field.getCategoryOptionCombo().equals("Ti9FJqkSK6J"))
        {

            holder.editText.setFocusable(false);

        }


        if (field.getDataElement().equals("GQHVHVJFf3g"))
        {
            if (field.getCategoryOptionCombo().equals("iRNhRMvoSCx"))
            {
                holder.textLabel.setText("5.14.1 "+holder.textLabel.getText());
            }
            else if (field.getCategoryOptionCombo().equals("wb51FJHqHxp"))
            {
                holder.textLabel.setText("5.14.2 "+holder.textLabel.getText());
            }


        }
        if (field.getDataElement().equals("GJKYhq2wR9L"))
        {
            if (field.getCategoryOptionCombo().equals("iRNhRMvoSCx"))
            {
                holder.textLabel.setText("6.2.4.a "+holder.textLabel.getText());
            }
            else if (field.getCategoryOptionCombo().equals("wb51FJHqHxp"))
            {
                holder.textLabel.setText("6.2.4.b "+holder.textLabel.getText());
            }


        }
        if (field.getDataElement().equals("aknlXIekL1Z"))
        {
            if (field.getLabel().contains("जीवित"))
            {
                if (field.getCategoryOptionCombo().equals("iRNhRMvoSCx"))
                {
                    holder.textLabel.setText("3.1.1.a जीवित जन्म पुरुष");
                }
                else if (field.getCategoryOptionCombo().equals("wb51FJHqHxp"))
                {
                    holder.textLabel.setText("3.1.1.b जीवित जन्म महिला");
                }
            }
            else
            {
                if (field.getCategoryOptionCombo().equals("iRNhRMvoSCx"))
                {
                    holder.textLabel.setText("3.1.1.a Live Birth - Male");
                }
                else if (field.getCategoryOptionCombo().equals("wb51FJHqHxp"))
                {
                    holder.textLabel.setText("3.1.1.b Live Birth - Female");
                }
            }



        }
        if (field.getDataElement().equals("NeNWp698eve")&&field.getLabel().contains("DMPA"))
        {

                if (field.getCategoryOptionCombo().equals("e4vOZhidnTl"))
                {
                    holder.textLabel.setText("5.5 Injectable Contraceptive-Antara Program- First Dose");
                }
                else if (field.getCategoryOptionCombo().equals("JD3TbSsgoKa"))
                {
                    holder.textLabel.setText("5.6 Injectable Contraceptive-Antara Program- Second Dose");
                }
                else if (field.getCategoryOptionCombo().equals("gZiING8oxqo"))
                {
                    holder.textLabel.setText("5.7 Injectable Contraceptive-Antara Program- Third Dose");
                }
                else if (field.getCategoryOptionCombo().equals("uBLH63dNSeY"))
                {
                    holder.textLabel.setText("5.8 Injectable Contraceptive-Antara Program- Fourth or more than fourth");
                }

        }
        if (field.getDataElement().equals("W7I9VJKykwv"))
        {
        if (field.getLabel().contains("Child"))
            {
    if (field.getCategoryOptionCombo().equals("KsGXFEh1Rt2"))
    {
        holder.textLabel.setText("7.1 Childhood Diseases - Tuberculosis (TB)");
    }
    else if (field.getCategoryOptionCombo().equals("TlWl1dykAT6"))
    {
        holder.textLabel.setText("7.2 Childhood Diseases - Acute Flaccid Paralysis(AFP)");
    }
    else if (field.getCategoryOptionCombo().equals("M1cHSRnXxnZ"))
    {
        holder.textLabel.setText("7.3 Childhood Diseases - Measles");
    }
    else if (field.getCategoryOptionCombo().equals("YjTqbjZbwk7"))
    {
        holder.textLabel.setText("7.4 Childhood Diseases - Malaria");
    }
}

        }

        if (field.getDataElement().equals("xfFUwIeiPY1")&&field.getLabel().contains("Outpatient"))
        {

                if (field.getCategoryOptionCombo().equals("EnQPoVNAi2p"))
                {
                    holder.textLabel.setText("9.1.1 Outpatient - Diabetes");
                }
                else if (field.getCategoryOptionCombo().equals("Mmdd2bEh3Yp"))
                {
                    holder.textLabel.setText("9.1.2 Outpatient - Hypertension");
                }
                else if (field.getCategoryOptionCombo().equals("vE5EsFqAEI2"))
                {
                    holder.textLabel.setText("9.1.3 Outpatient - Stroke (Paralysis)");
                }
                else if (field.getCategoryOptionCombo().equals("MqKow2RWPVy"))
                {
                    holder.textLabel.setText("9.1.4 Outpatient - Acute Heart Diseases");
                }
                else if (field.getCategoryOptionCombo().equals("srquQZDqZof"))
                {
                    holder.textLabel.setText("9.1.5 Outpatient - Mental illness");
                }
                else if (field.getCategoryOptionCombo().equals("X4UVVhdHhjq"))
                {
                    holder.textLabel.setText("9.1.6 Outpatient - Epilepsy");
                }
                else if (field.getCategoryOptionCombo().equals("NbjYj1XvveT"))
                {
                    holder.textLabel.setText("9.1.7 Outpatient - Ophthalmic Related");
                }
                else if (field.getCategoryOptionCombo().equals("W1M1x4zDNZm"))
                {
                    holder.textLabel.setText("9.1.8 Outpatient - Dental");
                }
        }
        if (field.getDataElement().equals("eBtyQP09Rgr")&&field.getLabel().contains("Infant"))
        {

                if (field.getCategoryOptionCombo().equals("UJ0sOkgjk9z"))
                {
                    holder.textLabel.setText("12.2.1 Infant Deaths up to 4 weeks due to Sepsis");
                }
                else if (field.getCategoryOptionCombo().equals("JXCJchaWpaO"))
                {
                    holder.textLabel.setText("12.2.2 Infant Deaths up to 4 weeks due to Asphyxia");
                }
                else if (field.getCategoryOptionCombo().equals("OTMSwDAdh0X"))
                {
                    holder.textLabel.setText("12.2.3 Infant Deaths up to 4 weeks due to Other causes");
                }
        }
        if (field.getDataElement().equals("r6Pc8UKQWel")&&field.getLabel().contains("Infant"))
        {

                if (field.getCategoryOptionCombo().equals("DQ8rocRNUvB"))
                {
                    holder.textLabel.setText("12.3.1 Number of Infant Deaths (1 -12 months) due to Pneumonia");
                }
                else if (field.getCategoryOptionCombo().equals("MIAeghFCobW"))
                {
                    holder.textLabel.setText("12.3.2 Number of Infant Deaths (1 -12 months) due to Diarrhoea");
                }
                else if (field.getCategoryOptionCombo().equals("uqNDgFiY3VA"))
                {
                    holder.textLabel.setText("12.3.3 Number of Infant Deaths (1 -12 months) due to Fever related");
                }
                else if (field.getCategoryOptionCombo().equals("pmUrHNUQG5I"))
                {
                    holder.textLabel.setText("12.3.4 Number of Infant Deaths (1 -12 months) due to Measles");
                }
                else if (field.getCategoryOptionCombo().equals("iyrx3OktTcp"))
                {
                    holder.textLabel.setText("12.3.5 Number of Infant Deaths (1 -12 months) due to Others");
                }
        }
        if (field.getDataElement().equals("F7obafTy4O4")&&field.getLabel().contains("Child"))
        {

                if (field.getCategoryOptionCombo().equals("aIDgj40tNAU"))
                {
                    holder.textLabel.setText("12.4.1 Number of Child Deaths (1 -5 years) due to Pneumonia");
                }
                else if (field.getCategoryOptionCombo().equals("fDl9q7Z74LG"))
                {
                    holder.textLabel.setText("12.4.2 Number of Child Deaths (1 -5 years) due to Diarrhoea");
                }
                else if (field.getCategoryOptionCombo().equals("T5uieqjIR3C"))
                {
                    holder.textLabel.setText("12.4.3 Number of Child Deaths (1 -5 years) due to Fever related");
                }
                else if (field.getCategoryOptionCombo().equals("xrdxHeoBFxV"))
                {
                    holder.textLabel.setText("12.4.4 Number of Child Deaths (1 -5 years) due to Measles");
                }
                else if (field.getCategoryOptionCombo().equals("D80VASkIs6m"))
                {
                    holder.textLabel.setText("12.4.5 Number of Child Deaths (1 -5 years) due to Others");
                }
        }
        if (field.getDataElement().equals("KFyojgVb8Go")&&field.getLabel().contains("Maternal"))
        {
                if (field.getCategoryOptionCombo().equals("hbUkGAlIwu8"))
                {
                    holder.textLabel.setText("12.5.1 Number of Maternal Deaths due to Bleeding");
                }
                else if (field.getCategoryOptionCombo().equals("PWn0jSvveTs"))
                {
                    holder.textLabel.setText("12.5.2 Number of Maternal Deaths due to High fever");
                }
                else if (field.getCategoryOptionCombo().equals("bg50uiDLvlP"))
                {
                    holder.textLabel.setText("12.5.3 Number of Maternal Deaths due to Abortion");
                }
                else if (field.getCategoryOptionCombo().equals("mrHdT99cCnv"))
                {
                    holder.textLabel.setText("12.5.4 Number of Maternal Deaths due to Obstructed/prolonged labour");
                }
                else if (field.getCategoryOptionCombo().equals("v3ourGcXDlN"))
                {
                    holder.textLabel.setText("12.5.5 Number of Maternal Deaths due to Severe hypertesnion/fits");
                }
                else if (field.getCategoryOptionCombo().equals("zJCowC71NtF"))
                {
                    holder.textLabel.setText("12.5.6 Number of Maternal Deaths due to Other Causes (including causes Not Known)");
                }
        }
        if (field.getDataElement().equals("NbxHvSiZYyA")&&field.getLabel().contains("Adol"))
        {
                if (field.getCategoryOptionCombo().equals("FktbJ7yFsse"))
                {
                    holder.textLabel.setText("12.6.1 Number of Adolscent / Adult Deaths due to Diarrhoeal diseases");
                }
                else if (field.getCategoryOptionCombo().equals("kC2hbiJtBcR"))
                {
                    holder.textLabel.setText("12.6.2 Number of Adolscent / Adult Deaths due to Tuberculosis");
                }
                else if (field.getCategoryOptionCombo().equals("RxHfyTB6IN3"))
                {
                    holder.textLabel.setText("12.6.3 Number of Adolscent / Adult Deaths due to Respiratory diseases including infections (other than TB)");
                }
                else if (field.getCategoryOptionCombo().equals("SUusts83kz5"))
                {
                    holder.textLabel.setText("12.6.4 Number of Adolscent / Adult Deaths due to Other Fever Related");
                }
                else if (field.getCategoryOptionCombo().equals("ig3ZBsF2AQS"))
                {
                    holder.textLabel.setText("12.6.5 Number of Adolscent / Adult Deaths due to HIV/AIDS");
                }
                else if (field.getCategoryOptionCombo().equals("Qu2SnqP7uDc"))
                {
                    holder.textLabel.setText("12.6.6 Number of Adolscent / Adult Deaths due to Heart disease/Hypertension related");
                }
                else if (field.getCategoryOptionCombo().equals("KoIuSxCUlOt"))
                {
                    holder.textLabel.setText("12.6.7 Number of Adolscent / Adult Deaths due to Cancer");
                }
                else if (field.getCategoryOptionCombo().equals("wPuxGm0H9B1"))
                {
                    holder.textLabel.setText("12.6.8 Number of Adolscent / Adult Deaths due to Neurological disease including strokes");
                }
                else if (field.getCategoryOptionCombo().equals("t7hLquJcQx6"))
                {
                    holder.textLabel.setText("12.6.9 Number of Adolscent / Adult Deaths due to Accidents/Burn cases");
                }
                else if (field.getCategoryOptionCombo().equals("qU3MDwWhTSO"))
                {
                    holder.textLabel.setText("12.6.10 Number of Adolscent / Adult Deaths due to Suicide");
                }
                else if (field.getCategoryOptionCombo().equals("SGz3Yjh5Poz"))
                {
                    holder.textLabel.setText("12.6.11 Number of Adolscent / Adult deaths due to Animal bites and stings");
                }
                else if (field.getCategoryOptionCombo().equals("EHwU077SibV"))
                {
                    holder.textLabel.setText("12.6.12 Number of Adolscent / Adult deaths due to Known Acute Disease");

                }
                else if (field.getCategoryOptionCombo().equals("PuwbCFzmkKp"))
                {
                    holder.textLabel.setText("12.6.13 Number of Adolscent / Adult deaths due to Known Chronic Disease");
                }
                else if (field.getCategoryOptionCombo().equals("CslMXpOUXWt"))
                {
                    holder.textLabel.setText("12.6.14 Number of Adolscent / Adult deaths due to Causes Not Known");
                }
        }

//        else
//        {
            holder.textWatcher.setField(field);
            holder.editText.addTextChangedListener(holder.textWatcher);
            holder.editText.setText(field.getValue().trim());
            holder.editText.clearFocus();
            holder.editText.setOnEditorActionListener(mOnEditorActionListener);
//        }


        minimum= PrefUtils.getMinmaxMinimum(view.getContext());
        maximum= PrefUtils.getMinmaxMaximum(view.getContext());
        if(minimum!=null)
        {
            try {
                minimum_json = new JSONObject(minimum);
                try
                {
                    JSONArray jsonArray = minimum_json.getJSONArray("minMaxDataElements");
                    Log.d("jsonArray--",jsonArray.toString());
                    for (int index=0; index<jsonArray.length(); ++index){
                        JSONObject currentFriend = jsonArray.getJSONObject(index);
                        default_min.put(currentFriend.getString("dataElement").substring(7,18)+"."+currentFriend.getString("optionCombo").substring(7,18),currentFriend.get("min"));
                        default_max.put(currentFriend.getString("dataElement").substring(7,18)+"."+currentFriend.getString("optionCombo").substring(7,18),currentFriend.get("max"));

                    }

                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        holder.editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String de_cat=field.getDataElement()+"."+field.getCategoryOptionCombo();
                    if (de_cat!=null)
                    {
                        if(default_min!=null&& default_max!=null)
                        {
                            if (default_min.has(de_cat)&&default_max.has(de_cat))
                            {
                                try {
                                    minimum_value=default_min.getString(de_cat);
                                    de_minimum_value=de_cat;
                                    maximum_value=default_max.getString(de_cat);
                                    de_maximum_value=de_cat;
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                mm_ignore=false;
                            }
                            else
                            {
                                mm_ignore=true;

                            }
                        }


                    }

                    if (mm_ignore==false&&minimum_value.length()>0&&maximum_value.length()>0&&minimum_value!=null&&maximum_value!=null&&!minimum_value.equals(" ") && !maximum_value.equals(" ")&& field.getValue().trim()!=null&&!field.getValue().trim().equals(" ") && !field.getValue().trim().isEmpty() && field.getValue().trim().length()>0)
                    {
                        try{
                            if (Integer.parseInt(field.getValue().trim())<Integer.parseInt(minimum_value)||Integer.parseInt(field.getValue().trim())>Integer.parseInt(maximum_value))
                            {
                                flag="true";
                            }
                            else if (Integer.parseInt(field.getValue().trim())>Integer.parseInt(minimum_value)||Integer.parseInt(field.getValue().trim())<Integer.parseInt(maximum_value))
                            {
                                flag="false";
                            }
                        } catch(NumberFormatException ex) {

                        }

                    }
                    scroll_detect = PrefUtils.getScroll(view.getContext());
                    if (flag.equals("true")&&mm_ignore==false)
                    {
                        String currrentde=field.getDataElement()+"."+field.getCategoryOptionCombo();
                        if (scroll_detect.equals("false") && currrentde.equals(de_cat))
                        {
                            String title=view.getContext().getString(R.string.minax_title);
                            AlertDialog.Builder builder = new AlertDialog.Builder(holder.editText.getContext());
                            builder.setMessage(holder.editText.getContext().getString(R.string.minimum_v)+" "+minimum_value+"  "+holder.editText.getContext().getString(R.string.maximum_v)+" " +maximum_value);
                            builder.setTitle(title+"("+field.getLabel()+")");
                            builder.setCancelable(false);
                            builder.setNegativeButton(R.string.dialog_outlier,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            holder.editText.setTextColor(Color.RED);

                                            dialog.dismiss();
                                            dialog.cancel();

                                        }
                                    });

                            AlertDialog alert = builder.create();
                            alert.show();
                            builder.setTitle("");
                            builder.setMessage("");
                            flag="false";
                            holder.editText.setTextColor(Color.RED);
                        }

                    }
                    else
                    {
                        holder.editText.setTextColor(Color.BLACK);
                    }


                }
            }
        });

        holder.editText.addTextChangedListener(new TextWatcher() {
            boolean ignoreChange = false;
            boolean beforechange = false;
            boolean afterchange = false;
            @Override
            public void afterTextChanged(Editable s) {
                if (beforechange==true&&field.getValue().length()==0)
                {
                    field.setValue(" ");
                }
                afterchange=true;

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                if (field.getValue().length()>0)
                {
                    beforechange=true;
                }

            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

                if(!ignoreChange){
                    ignoreChange = !ignoreChange;
                }

            }

        });
        if(readOnly){
            holder.editText.setEnabled(false);
        } else {
            holder.editText.setEnabled(true);

        }

        if (readOnly_tablet)
        {
            holder.editText.setFocusable(false);
        }
        else
        {
            holder.editText.setFocusable(true);
        }
        return view;
    }

    @Override
    public int getViewType() {
        return RowTypes.INTEGER.ordinal();
    }

    @Override
    public void setReadOnly(boolean value) {
        readOnly = value;
    }

    public void setReadOnly_tablet(boolean value) {
        readOnly_tablet = value;
    }


}
