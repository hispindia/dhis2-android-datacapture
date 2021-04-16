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

package org.dhis2.mobile_uphmis.ui.adapters.dataEntry;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.dhis2.mobile_uphmis.io.json.JsonHandler;
import org.dhis2.mobile_uphmis.io.json.ParsingException;
import org.dhis2.mobile_uphmis.io.models.Field;
import org.dhis2.mobile_uphmis.io.models.Group;
import org.dhis2.mobile_uphmis.io.models.OptionSet;
import org.dhis2.mobile_uphmis.ui.adapters.dataEntry.rows.AutoCompleteRow;
import org.dhis2.mobile_uphmis.ui.adapters.dataEntry.rows.BooleanRow;
import org.dhis2.mobile_uphmis.ui.adapters.dataEntry.rows.AdequateRow;
import org.dhis2.mobile_uphmis.ui.adapters.dataEntry.rows.CheckBoxRow;
import org.dhis2.mobile_uphmis.ui.adapters.dataEntry.rows.DatePickerRow;
import org.dhis2.mobile_uphmis.ui.adapters.dataEntry.rows.GenderRow;
import org.dhis2.mobile_uphmis.ui.adapters.dataEntry.rows.IntegerRow;
import org.dhis2.mobile_uphmis.ui.adapters.dataEntry.rows.LongTextRow;
import org.dhis2.mobile_uphmis.ui.adapters.dataEntry.rows.NegativeIntegerRow;
import org.dhis2.mobile_uphmis.ui.adapters.dataEntry.rows.NotSupportedRow;
import org.dhis2.mobile_uphmis.ui.adapters.dataEntry.rows.NumberRow;
import org.dhis2.mobile_uphmis.ui.adapters.dataEntry.rows.PosIntegerRow;
import org.dhis2.mobile_uphmis.ui.adapters.dataEntry.rows.PosOrZeroIntegerRow;
import org.dhis2.mobile_uphmis.ui.adapters.dataEntry.rows.Row;
import org.dhis2.mobile_uphmis.ui.adapters.dataEntry.rows.RowRadio;
import org.dhis2.mobile_uphmis.ui.adapters.dataEntry.rows.RowTypes;
import org.dhis2.mobile_uphmis.ui.adapters.dataEntry.rows.TextRow;
import org.dhis2.mobile_uphmis.utils.PrefUtils;
import org.dhis2.mobile_uphmis.utils.TextFileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class FieldAdapter extends BaseAdapter {
    public static final String FORM_WITHOUT_SECTION = "default";
    private ArrayList<Row> rows = new ArrayList<Row>();
    private ArrayList<RowRadio> rows_ = new ArrayList<RowRadio>();
    private final String adapterLabel;
    private final Group group;
    private Row row;
    //    private ImageButton swipe_right;
    private static String flag = "false";
    private static String still_fresh = "";
    private static String still_mas = "";
    private static String still_hmis = "";
    private Boolean mm_ignore = true;
    private static String minimum = "";
    private static String maximum = "";
    private static String minMaxDataElements = "minMaxDataElements";
    private static String minimum_value = "";
    private static String maximum_value = "";
    private static JSONObject minimum_json = null;
    private static JSONObject maximum_json = null;
    private static JSONObject default_min = new JSONObject();
    private static JSONObject default_max = new JSONObject();
    private ListView mListView;
    private static String scroll_detect = "false";

    public FieldAdapter(Group group, Context context, ListView listView, boolean readOnly) {
        still_fresh = "";
        still_mas = "";
        still_hmis = "";
        ArrayList<Field> fields = group.getFields();
        this.group = group;
        this.rows = new ArrayList<Row>();
        this.row = new Row() {
            @Override
            public View getView(View convertView) {
                return null;
            }

            @Override
            public int getViewType() {
                return 0;
            }

            @Override
            public void setReadOnly(boolean value) {

            }
        };
        this.adapterLabel = group.getLabel();
        minimum = PrefUtils.getMinmaxMinimum(context);
        maximum = PrefUtils.getMinmaxMaximum(context);

        if (minimum != null) {
            try {
                minimum_json = new JSONObject(minimum);

                try {
                    JSONArray jsonArray = minimum_json.getJSONArray("minMaxDataElements");
                    for (int index = 0; index < jsonArray.length(); ++index) {
                        JSONObject currentFriend = jsonArray.getJSONObject(index);

                        default_min.put(currentFriend.getString("dataElement").substring(7, 18) + "." + currentFriend.getString("optionCombo").substring(7, 18), currentFriend.get("min"));
                        default_max.put(currentFriend.getString("dataElement").substring(7, 18) + "." + currentFriend.getString("optionCombo").substring(7, 18), currentFriend.get("max"));

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        CustomOnEditorActionListener customOnEditorActionListener =
                new CustomOnEditorActionListener();

        mListView = listView;
        LayoutInflater inflater = LayoutInflater.from(context);

        if (group.getLabel().equals(FORM_WITHOUT_SECTION)) {

            Collections.sort(fields, new Comparator<Field>() {
                @Override
                public int compare(Field o1, Field o2) {
                    return o1.getLabel().toLowerCase().compareTo(o2.getLabel().toLowerCase());
                }
            });
        }

        for (int i = 0; i < fields.size(); i++) {
            if (fields.get(i).getDataElement().equals("OrMq254iPQ2") && fields.get(i).getCategoryOptionCombo().equals("LeWpv23NQE0")) {
                still_fresh = fields.get(i).getValue().trim();
            }
            if (fields.get(i).getDataElement().equals("OrMq254iPQ2") && fields.get(i).getCategoryOptionCombo().equals("ocOywnb7dim")) {
                still_mas = fields.get(i).getValue().trim();

                if (still_mas.length() > 0 || still_fresh.length() > 0) {
                    if ("".equals(still_fresh) && !"".equals(still_mas)) {
                        Integer still_total = Integer.parseInt(still_mas);
                        fields.get(3).setValue(still_total.toString());
                        still_hmis = still_total.toString();
                    } else if ("".equals(still_mas) && !"".equals(still_fresh)) {
                        Integer still_total = Integer.parseInt(still_fresh);
                        fields.get(3).setValue(still_total.toString());
                        still_hmis = still_total.toString();
                    } else if (!"".equals(still_fresh) && !"".equals(still_mas) && !" ".equals(still_fresh) && !" ".equals(still_mas)) {
                        Integer still_total = Integer.parseInt(still_fresh) + Integer.parseInt(still_mas);
                        fields.get(3).setValue(still_total.toString());
                        still_hmis = still_total.toString();
                    } else {

                    }

                } else if ("".equals(still_mas) && "".equals(still_fresh)) {
                    fields.get(3).setValue("");
                }
            }
//            if (fields.get(i).getDataElement().equals("mexWK5BLs5H")&&fields.get(i).getCategoryOptionCombo().equals("Ti9FJqkSK6J"))
//            {
//                fields.get(i).setValue(still_hmis);
//
//            }
//            PrefUtils.initScrollData(context,"true");
            Field field = fields.get(i);

            if (field.hasOptionSet()) {

                if (field.getDataElement().matches("o6w26EvZlrw|PJqKcSfFPQV|mqecOwi4pRT|Y3wDADQUeEi|Ih5cGEEYr31|idS5zFSaJm9|S8afDnqM2l0|YS6P8lhh1uY|ajAwdn8mfy6|gAuDQh6aCEI|aryxc3XswQs|IXPTgyIdWf1|A457aStVYuU|To77va58TTR|TvhNc9gXgHx|ns4g8gTFWim")) {
                    rows.add(new AdequateRow(inflater, field));
                } else {
                    OptionSet optionSet = getOptionSet(context, field.getOptionSet());
                    AutoCompleteRow autoCompleteRow = new AutoCompleteRow(inflater, field, optionSet, context);

                    autoCompleteRow.setOnEditorActionListener(customOnEditorActionListener);
                    autoCompleteRow.setReadOnly(readOnly);
                    rows.add(autoCompleteRow);

                }


            } else if (field.getType().equals(RowTypes.TEXT.name())) {

                if (field.getDataElement().matches("o6w26EvZlrw|PJqKcSfFPQV|mqecOwi4pRT|Y3wDADQUeEi|Ih5cGEEYr31|idS5zFSaJm9|S8afDnqM2l0|YS6P8lhh1uY|ajAwdn8mfy6|gAuDQh6aCEI|aryxc3XswQs|IXPTgyIdWf1|A457aStVYuU|To77va58TTR|TvhNc9gXgHx|ns4g8gTFWim")) {
                    rows.add(new AdequateRow(inflater, field));
                } else {

                    TextRow textRow = new TextRow(inflater, field);
                    textRow.setOnEditorActionListener(customOnEditorActionListener);
                    textRow.setReadOnly(readOnly);
                    rows.add(textRow);
                }

            } else if (field.getType().equals(RowTypes.LONG_TEXT.name())) {
                LongTextRow longTextRow = new LongTextRow(inflater, field);
                longTextRow.setReadOnly(readOnly);
                rows.add(longTextRow);
            } else if (field.getType().equals(RowTypes.NUMBER.name())) {
                NumberRow numberRow = new NumberRow(inflater, field);
                numberRow.setOnEditorActionListener(customOnEditorActionListener);
                numberRow.setReadOnly(readOnly);
                rows.add(numberRow);
            } else if (field.getType().equals(RowTypes.INTEGER.name())) {
                if (field.getDataElement().equals("W7I9VJKykwv") && field.getCategoryOptionCombo().equals("udd2MF7Odu0")) {

                } else {
//                            if(field.getDataElement().matches("qiRWCmiE6K4|pg5aJ8UcVKt|tb6oVuRxuAf|ohTVakZvt02|sRpoLSnh6Bl|HwC8Sg6R6tl")&&field.getCategoryOptionCombo().equals("aRNxGm8EkXJ"))
//                            {
                    if (field.getDataElement().equals("tN6dkfe6JLE") && field.getCategoryOptionCombo().equals("aRNxGm8EkXJ")) {
                        String label = field.getLabel();
                        field.setLabel(label);
                        IntegerRow integerRow = new IntegerRow(inflater, field);
                        integerRow.setOnEditorActionListener(customOnEditorActionListener);
                        field.setLabel(label);
                        rows.add(integerRow);


                        if (field.getLabel().contains("बीसीजी")) {
                            Field f1 = new Field();
                            f1.setLabel("6.1.3 बाल प्रतिरक्षण - डीपीटी 1");
                            f1.setType("INTEGER");
                            f1.setDataElement("tN6dkfe6JLE");
                            f1.setCategoryOptionCombo("aRNxGm8EkXJ");
                            IntegerRow integerRow1 = new IntegerRow(inflater, f1);
                            if (isTablet(context)) {
                                integerRow1.setReadOnly_tablet(true);
                            } else {
                                integerRow1.setReadOnly(true);
                            }

                            rows.add(integerRow1);

                            Field f2 = new Field();
                            f2.setLabel("6.1.4 बाल प्रतिरक्षण - डीपीटी 2");
                            f2.setType("INTEGER");
                            f2.setDataElement("tN6dkfe6JLE");
                            f2.setCategoryOptionCombo("aRNxGm8EkXJ");
                            IntegerRow IntegerRow = new IntegerRow(inflater, f2);
                            if (isTablet(context)) {
                                IntegerRow.setReadOnly_tablet(true);
                            } else {
                                IntegerRow.setReadOnly(true);
                            }
                            rows.add(IntegerRow);

                            Field f3 = new Field();
                            f3.setLabel("6.1.5 बाल प्रतिरक्षण - डीपीटी 3");
                            f3.setDataElement("tN6dkfe6JLE");
                            f3.setType("INTEGER");
                            f3.setCategoryOptionCombo("aRNxGm8EkXJ");
                            IntegerRow integerRow3 = new IntegerRow(inflater, f3);
                            if (isTablet(context)) {
                                integerRow3.setReadOnly_tablet(true);
                            } else {
                                integerRow3.setReadOnly(true);
                            }
                            rows.add(integerRow3);
                        } else {
                            Field f1 = new Field();
                            f1.setLabel("6.1.3 DPT1 dose");
                            f1.setDataElement("disable");
                            f1.setCategoryOptionCombo("disable");
                            f1.setType("INTEGER");
                            IntegerRow integerRow1 = new IntegerRow(inflater, f1);
                            if (isTablet(context)) {
                                integerRow1.setReadOnly_tablet(true);
                            } else {
                                integerRow1.setReadOnly(true);
                            }
                            rows.add(integerRow1);

                            Field f2 = new Field();
                            f2.setLabel("6.1.4 DPT2 dose");
                            f2.setDataElement("disable");
                            f2.setType("INTEGER");
                            f2.setCategoryOptionCombo("disable");
                            IntegerRow IntegerRow = new IntegerRow(inflater, f2);
                            if (isTablet(context)) {
                                IntegerRow.setReadOnly_tablet(true);
                            } else {
                                IntegerRow.setReadOnly(true);
                            }
                            rows.add(IntegerRow);

                            Field f3 = new Field();
                            f3.setLabel("6.1.5 DPT3 dose");
                            f3.setType("INTEGER");
                            f3.setDataElement("disable");
                            f3.setCategoryOptionCombo("disable");
                            IntegerRow integerRow3 = new IntegerRow(inflater, f3);
                            if (isTablet(context)) {
                                integerRow3.setReadOnly_tablet(true);
                            } else {
                                integerRow3.setReadOnly(true);
                            }
                            rows.add(integerRow3);
                        }

                    } else if (field.getDataElement().equals("ZrNicVLgWiz") && field.getCategoryOptionCombo().equals("Ti9FJqkSK6J")) {
                        NumberRow integerRow = new NumberRow(inflater, field);
                        integerRow.setOnEditorActionListener(customOnEditorActionListener);
                        integerRow.setReadOnly(readOnly);
                        rows.add(integerRow);

                        if (field.getLabel().contains("6.13 बाल")) {

                            Field f1 = new Field();
                            f1.setLabel("6.1.14 बाल प्रतिरक्षण - हेपेटाइटिस-बी 1");
                            f1.setDataElement("disable");
                            f1.setType("INTEGER");
                            f1.setCategoryOptionCombo("disable");
                            IntegerRow integerRow1 = new IntegerRow(inflater, f1);
                            if (isTablet(context)) {
                                integerRow1.setReadOnly_tablet(true);
                            } else {
                                integerRow1.setReadOnly(true);
                            }
                            rows.add(integerRow1);

                            Field f2 = new Field();
                            f2.setLabel("6.1.15 बाल प्रतिरक्षण - हेपेटाइटिस-बी 2");
                            f2.setDataElement("disable");
                            f2.setType("INTEGER");
                            f2.setCategoryOptionCombo("disable");
                            IntegerRow IntegerRow = new IntegerRow(inflater, f2);
                            if (isTablet(context)) {
                                IntegerRow.setReadOnly_tablet(true);
                            } else {
                                IntegerRow.setReadOnly(true);
                            }
                            rows.add(IntegerRow);

                            Field f3 = new Field();
                            f3.setLabel("6.1.16 बाल प्रतिरक्षण - हेपेटाइटिस-बी 3");
                            f3.setDataElement("disable");
                            f3.setType("INTEGER");
                            f3.setCategoryOptionCombo("disable");
                            IntegerRow integerRow3 = new IntegerRow(inflater, f3);
                            if (isTablet(context)) {
                                integerRow3.setReadOnly_tablet(true);
                            } else {
                                integerRow3.setReadOnly(true);
                            }
                            rows.add(integerRow3);
                        } else if (field.getLabel().contains("6.1.13 Child")) {
                            Field f1 = new Field();
                            f1.setLabel("6.1.14 Hepatitis B1 dose");
                            f1.setDataElement("disable");
                            f1.setCategoryOptionCombo("disable");
                            f1.setType("INTEGER");
                            IntegerRow integerRow1 = new IntegerRow(inflater, f1);
                            if (isTablet(context)) {
                                integerRow1.setReadOnly_tablet(true);
                            } else {
                                integerRow1.setReadOnly(true);
                            }
                            rows.add(integerRow1);

                            Field f2 = new Field();
                            f2.setLabel("6.1.15 Hepatitis B2 dose");
                            f2.setDataElement("disable");
                            f2.setCategoryOptionCombo("disable");
                            f2.setType("INTEGER");
                            IntegerRow IntegerRow = new IntegerRow(inflater, f2);
                            if (isTablet(context)) {
                                IntegerRow.setReadOnly_tablet(true);
                            } else {
                                IntegerRow.setReadOnly(true);
                            }
                            rows.add(IntegerRow);

                            Field f3 = new Field();
                            f3.setLabel("6.1.16 Hepatitis B3 dose");
                            f3.setDataElement("disable");
                            f3.setType("INTEGER");
                            f3.setCategoryOptionCombo("disable");
                            IntegerRow integerRow3 = new IntegerRow(inflater, f3);
                            if (isTablet(context)) {
                                integerRow3.setReadOnly_tablet(true);
                            } else {
                                integerRow3.setReadOnly(true);
                            }
                            rows.add(integerRow3);
                        }

                    } else if (field.getDataElement().equals("xVx6Fo0MVH2") && field.getCategoryOptionCombo().equals("Ti9FJqkSK6J")) {
                        String label = field.getLabel();
                        field.setLabel(label);
                        IntegerRow integerRow = new IntegerRow(inflater, field);
                        integerRow.setOnEditorActionListener(customOnEditorActionListener);
                        integerRow.setReadOnly(readOnly);
                        field.setLabel(label);
                        rows.add(integerRow);

                        if (field.getLabel().contains("6.4.4 बाल प्रतिरक्षण")) {
                            Field f1 = new Field();
                            f1.setLabel("6.4.5 बाल प्रतिरक्षण - खसरा, मम्प्स, रूबेला (एमएमआर) वैक्सीन");
                            f1.setDataElement("disable");
                            f1.setType("INTEGER");
                            f1.setCategoryOptionCombo("disable");
                            IntegerRow integerRow1 = new IntegerRow(inflater, f1);
                            if (isTablet(context)) {
                                integerRow1.setReadOnly_tablet(true);
                            } else {
                                integerRow1.setReadOnly(true);
                            }
                            rows.add(integerRow1);

                        } else if (field.getLabel().contains("6.4.4 Child")) {
                            Field f1 = new Field();
                            f1.setLabel("6.4.5 Child immunisation-Measles, Mumps, Rubella (MMR) Vaccine");
                            f1.setDataElement("disable");
                            f1.setCategoryOptionCombo("disable");
                            f1.setType("INTEGER");
                            IntegerRow integerRow1 = new IntegerRow(inflater, f1);
                            if (isTablet(context)) {
                                integerRow1.setReadOnly_tablet(true);
                            } else {
                                integerRow1.setReadOnly(true);
                            }
                            rows.add(integerRow1);
                        }

                    } else {
                        IntegerRow integerRow = new IntegerRow(inflater, field);
                        integerRow.setOnEditorActionListener(customOnEditorActionListener);
                        integerRow.setReadOnly(readOnly);
                        rows.add(integerRow);
                    }
                }

            } else if (field.getType().equals(RowTypes.INTEGER_ZERO_OR_POSITIVE.name())) {
                if (field.getDataElement().equals("W7I9VJKykwv") && field.getCategoryOptionCombo().equals("udd2MF7Odu0")) {

                } else {
//                            if(field.getDataElement().matches("qiRWCmiE6K4|pg5aJ8UcVKt|tb6oVuRxuAf|ohTVakZvt02|sRpoLSnh6Bl|HwC8Sg6R6tl")&&field.getCategoryOptionCombo().equals("aRNxGm8EkXJ"))
//                            {
                    if (field.getDataElement().equals("tN6dkfe6JLE") && field.getCategoryOptionCombo().equals("aRNxGm8EkXJ")) {
                        String label = field.getLabel();
                        field.setLabel(label);
                        PosOrZeroIntegerRow integerRow = new PosOrZeroIntegerRow(inflater, field);
                        integerRow.setOnEditorActionListener(customOnEditorActionListener);
                        integerRow.setReadOnly(readOnly);
                        field.setLabel(label);
                        rows.add(integerRow);


                        if (field.getLabel().contains("बीसीजी")) {
                            Field f1 = new Field();
                            f1.setLabel("6.1.3 बाल प्रतिरक्षण - डीपीटी 1");
                            f1.setDataElement("disable");
                            f1.setType("INTEGER");
                            f1.setCategoryOptionCombo("disable");
                            PosOrZeroIntegerRow integerRow1 = new PosOrZeroIntegerRow(inflater, f1);
                            if (isTablet(context)) {
                                integerRow1.setReadOnly_tablet(true);
                            } else {
                                integerRow1.setReadOnly(true);
                            }
                            rows.add(integerRow1);

                            Field f2 = new Field();
                            f2.setLabel("6.1.4 बाल प्रतिरक्षण - डीपीटी 2");
                            f2.setDataElement("disable");
                            f2.setType("INTEGER");
                            f2.setCategoryOptionCombo("disable");
                            PosOrZeroIntegerRow IntegerRow = new PosOrZeroIntegerRow(inflater, f2);
                            if (isTablet(context)) {
                                IntegerRow.setReadOnly_tablet(true);
                            } else {
                                IntegerRow.setReadOnly(true);
                            }
                            rows.add(IntegerRow);

                            Field f3 = new Field();
                            f3.setLabel("6.1.5 बाल प्रतिरक्षण - डीपीटी 3");
                            f3.setDataElement("disable");
                            f3.setType("INTEGER");
                            f3.setCategoryOptionCombo("disable");
                            PosOrZeroIntegerRow integerRow3 = new PosOrZeroIntegerRow(inflater, f3);
                            if (isTablet(context)) {
                                integerRow3.setReadOnly_tablet(true);
                            } else {
                                integerRow3.setReadOnly(true);
                            }
                            rows.add(integerRow3);
                        } else {
                            Field f1 = new Field();
                            f1.setLabel("6.1.3 DPT1 dose");
                            f1.setDataElement("disable");
                            f1.setType("INTEGER");
                            f1.setCategoryOptionCombo("disable");
                            PosOrZeroIntegerRow integerRow1 = new PosOrZeroIntegerRow(inflater, f1);
                            if (isTablet(context)) {
                                integerRow1.setReadOnly_tablet(true);
                            } else {
                                integerRow1.setReadOnly(true);
                            }
                            rows.add(integerRow1);

                            Field f2 = new Field();
                            f2.setLabel("6.1.4 DPT2 dose");
                            f2.setDataElement("disable");
                            f2.setType("INTEGER");
                            f2.setCategoryOptionCombo("disable");
                            PosOrZeroIntegerRow IntegerRow = new PosOrZeroIntegerRow(inflater, f2);
                            if (isTablet(context)) {
                                IntegerRow.setReadOnly_tablet(true);
                            } else {
                                IntegerRow.setReadOnly(true);
                            }
                            rows.add(IntegerRow);

                            Field f3 = new Field();
                            f3.setLabel("6.1.5 DPT3 dose");
                            f3.setType("INTEGER");
                            f3.setDataElement("disable");
                            f3.setCategoryOptionCombo("disable");
                            PosOrZeroIntegerRow integerRow3 = new PosOrZeroIntegerRow(inflater, f3);
                            if (isTablet(context)) {
                                integerRow3.setReadOnly_tablet(true);
                            } else {
                                integerRow3.setReadOnly(true);
                            }
                            rows.add(integerRow3);
                        }

                    } else if (field.getDataElement().equals("ZrNicVLgWiz") && field.getCategoryOptionCombo().equals("Ti9FJqkSK6J")) {
                        NumberRow posOrZeroIntegerRow = new NumberRow(inflater, field);
                        posOrZeroIntegerRow.setOnEditorActionListener(customOnEditorActionListener);
                        posOrZeroIntegerRow.setReadOnly(readOnly);
                        rows.add(posOrZeroIntegerRow);

                        if (field.getLabel().contains("6.1.13 बाल")) {
                            Field f1 = new Field();
                            f1.setLabel("6.1.14 बाल प्रतिरक्षण - हेपेटाइटिस-बी 1");
                            f1.setDataElement("disable");
                            f1.setCategoryOptionCombo("disable");
                            f1.setType("INTEGER");
                            PosOrZeroIntegerRow integerRow1 = new PosOrZeroIntegerRow(inflater, f1);
                            if (isTablet(context)) {
                                integerRow1.setReadOnly_tablet(true);
                            } else {
                                integerRow1.setReadOnly(true);
                            }
                            rows.add(integerRow1);

                            Field f2 = new Field();
                            f2.setLabel("6.1.15 बाल प्रतिरक्षण - हेपेटाइटिस-बी 2");
                            f2.setDataElement("disable");
                            f2.setCategoryOptionCombo("disable");
                            f2.setType("INTEGER");
                            PosOrZeroIntegerRow IntegerRow = new PosOrZeroIntegerRow(inflater, f2);
                            if (isTablet(context)) {
                                IntegerRow.setReadOnly_tablet(true);
                            } else {
                                IntegerRow.setReadOnly(true);
                            }
                            rows.add(IntegerRow);

                            Field f3 = new Field();
                            f3.setLabel("6.1.16 बाल प्रतिरक्षण - हेपेटाइटिस-बी 3");
                            f3.setDataElement("disable");
                            f3.setType("INTEGER");
                            f3.setCategoryOptionCombo("disable");
                            PosOrZeroIntegerRow integerRow3 = new PosOrZeroIntegerRow(inflater, f3);
                            if (isTablet(context)) {
                                integerRow3.setReadOnly_tablet(true);
                            } else {
                                integerRow3.setReadOnly(true);
                            }
                            rows.add(integerRow3);
                        } else if (field.getLabel().contains("6.1.13 Child")) {
                            Field f1 = new Field();
                            f1.setLabel("6.1.14 Hepatitis B1 dose");
                            f1.setDataElement("disable");
                            f1.setCategoryOptionCombo("disable");
                            f1.setType("INTEGER");
                            PosOrZeroIntegerRow integerRow1 = new PosOrZeroIntegerRow(inflater, f1);
                            if (isTablet(context)) {
                                integerRow1.setReadOnly_tablet(true);
                            } else {
                                integerRow1.setReadOnly(true);
                            }
                            rows.add(integerRow1);

                            Field f2 = new Field();
                            f2.setLabel("6.1.15 Hepatitis B2 dose");
                            f2.setDataElement("disable");
                            f2.setType("INTEGER");
                            f2.setCategoryOptionCombo("disable");
                            PosOrZeroIntegerRow IntegerRow = new PosOrZeroIntegerRow(inflater, f2);
                            if (isTablet(context)) {
                                IntegerRow.setReadOnly_tablet(true);
                            } else {
                                IntegerRow.setReadOnly(true);
                            }
                            rows.add(IntegerRow);

                            Field f3 = new Field();
                            f3.setLabel("6.1.16 Hepatitis B3 dose");
                            f3.setDataElement("disable");
                            f3.setType("INTEGER");
                            f3.setCategoryOptionCombo("disable");
                            PosOrZeroIntegerRow integerRow3 = new PosOrZeroIntegerRow(inflater, f3);
                            if (isTablet(context)) {
                                integerRow3.setReadOnly_tablet(true);
                            } else {
                                integerRow3.setReadOnly(true);
                            }
                            rows.add(integerRow3);
                        }

                    } else if (field.getDataElement().equals("xVx6Fo0MVH2") && field.getCategoryOptionCombo().equals("Ti9FJqkSK6J")) {
                        String label = field.getLabel();
                        field.setLabel(label);
                        PosOrZeroIntegerRow integerRow = new PosOrZeroIntegerRow(inflater, field);
                        integerRow.setOnEditorActionListener(customOnEditorActionListener);
                        integerRow.setReadOnly(readOnly);
                        field.setLabel(label);
                        rows.add(integerRow);

                        if (field.getLabel().contains("6.4.4 बाल प्रतिरक्षण")) {
                            Field f1 = new Field();
                            f1.setLabel("6.4.5 बाल प्रतिरक्षण - खसरा, मम्प्स, रूबेला (एमएमआर) वैक्सीन");
                            f1.setDataElement("disable");
                            f1.setType("INTEGER");
                            f1.setCategoryOptionCombo("disable");
                            PosOrZeroIntegerRow integerRow1 = new PosOrZeroIntegerRow(inflater, f1);
                            if (isTablet(context)) {
                                integerRow1.setReadOnly_tablet(true);
                            } else {
                                integerRow1.setReadOnly(true);
                            }
                            rows.add(integerRow1);

                        } else if (field.getLabel().contains("6.4.4 Child")) {
                            Field f1 = new Field();
                            f1.setLabel("6.4.5 Child immunisation-Measles, Mumps, Rubella (MMR) Vaccine");
                            f1.setDataElement("disable");
                            f1.setCategoryOptionCombo("disable");
                            f1.setType("INTEGER");
                            PosOrZeroIntegerRow integerRow1 = new PosOrZeroIntegerRow(inflater, f1);
                            if (isTablet(context)) {
                                integerRow1.setReadOnly_tablet(true);
                            } else {
                                integerRow1.setReadOnly(true);
                            }
                            rows.add(integerRow1);
                        }

                    } else {
                        PosOrZeroIntegerRow posOrZeroIntegerRow = new PosOrZeroIntegerRow(inflater, field);
                        posOrZeroIntegerRow.setOnEditorActionListener(customOnEditorActionListener);
                        posOrZeroIntegerRow.setReadOnly(readOnly);
                        rows.add(posOrZeroIntegerRow);
                    }
                }


            } else if (field.getType().equals(RowTypes.INTEGER_POSITIVE.name())) {


                PosIntegerRow posIntegerRow = new PosIntegerRow(inflater, field);
                posIntegerRow.setOnEditorActionListener(customOnEditorActionListener);
                posIntegerRow.setReadOnly(readOnly);
                rows.add(posIntegerRow);
            } else if (field.getType().equals(RowTypes.INTEGER_NEGATIVE.name())) {
                NegativeIntegerRow negativeIntegerRow = new NegativeIntegerRow(inflater, field);
                negativeIntegerRow.setOnEditorActionListener(customOnEditorActionListener);
                negativeIntegerRow.setReadOnly(readOnly);
                rows.add(negativeIntegerRow);
            } else if (field.getType().equals(RowTypes.BOOLEAN.name())) {
                BooleanRow booleanRow = new BooleanRow(inflater, field);
                booleanRow.setReadOnly(readOnly);
                rows.add(booleanRow);
            } else if (field.getType().equals(RowTypes.TRUE_ONLY.name())) {
                rows.add(new CheckBoxRow(inflater, field));
            } else if (field.getType().equals(RowTypes.DATE.name())) {
                rows.add(new DatePickerRow(inflater, field, this, context));
            } else if (field.getType().equals(RowTypes.GENDER.name())) {
                rows.add(new GenderRow(inflater, field));
            } else {

                rows.add(new NotSupportedRow(inflater, field));
            }
        }

    }

    @Override
    public int getCount() {
        return rows.size();
    }

    @Override
    public int getViewTypeCount() {
        return RowTypes.values().length;
    }

    @Override
    public int getItemViewType(int position) {
        return rows.get(position).getViewType();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        return rows.get(position).getView(convertView);
    }

    public String getLabel() {
        return adapterLabel;
    }

    public Group getGroup() {
        return group;
    }

    public Row getRow() {
        return row;
    }

    public void setRows(ArrayList<Row> rows) {
        this.rows = rows;
    }

    public ArrayList<Row> getRows() {
        return rows;
    }

    private static OptionSet getOptionSet(Context context, String id) {
        String source = TextFileUtils.readTextFile(context, TextFileUtils.Directory.OPTION_SETS, id);
        try {
            JsonObject jOptionSet = JsonHandler.buildJsonObject(source);
            Gson gson = new Gson();
            return gson.fromJson(jOptionSet, OptionSet.class);
        } catch (ParsingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public class CustomOnEditorActionListener implements TextView.OnEditorActionListener {

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            final TextView currentField = (TextView) v.findFocus();
            final ArrayList<Field> fields = group.getFields();
            int de_position = mListView.getPositionForView(v);

            if (group.getLabel().contains("M6.Child") || group.getLabel().contains("M6. बाल")) {

                if (de_position > 1 && de_position < 13) {

                    de_position = de_position - 3;
                } else if (de_position > 13 && de_position < 21) {

                    de_position = de_position - 6;
                }
            }
            if (group.getLabel().contains("M6.E")) {
                if (de_position == 5) {

                    de_position = de_position - 1;
                }
            }
            // java.lang.IndexOutOfBoundsException: Index: 16, Size: 15
            final Field df = fields.get(de_position);

            if (df.getDataElement().equals("OrMq254iPQ2") && df.getCategoryOptionCombo().equals("LeWpv23NQE0")) {

                still_fresh = df.getValue().trim();
            }


            String de_cat = df.getDataElement() + "." + df.getCategoryOptionCombo();
            if (de_cat != null) {

                if (default_min != null && default_max != null) {

                    if (default_min.has(de_cat) && default_max.has(de_cat)) {
                        try {
                            minimum_value = default_min.getString(de_cat);
                            maximum_value = default_max.getString(de_cat);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mm_ignore = false;
                    } else {
                        mm_ignore = true;

                    }
                }


                if (mm_ignore == false && minimum_value.length() > 0 && maximum_value.length() > 0 && minimum_value != null && maximum_value != null && !minimum_value.equals(" ") && !maximum_value.equals(" ") && df.getValue().trim() != null && !df.getValue().trim().equals(" ") && !df.getValue().trim().isEmpty() && df.getValue().trim().length() > 0) {


                    if (Integer.parseInt(df.getValue().trim()) < Integer.parseInt(minimum_value) || Integer.parseInt(df.getValue().trim()) > Integer.parseInt(maximum_value)) {
                        flag = "true";
                    } else if (Integer.parseInt(df.getValue().trim()) > Integer.parseInt(minimum_value) || Integer.parseInt(df.getValue().trim()) < Integer.parseInt(maximum_value)) {
                        flag = "false";
                    }

                }
            }

            final TextView view = v;
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                final int position = mListView.getPositionForView(v);
//                mListView.getAdapter().
                mListView.smoothScrollToPosition(position + 1);
                if (df.getDataElement().equals("OrMq254iPQ2") && df.getCategoryOptionCombo().equals("LeWpv23NQE0")) {

                    mListView.invalidate();

//                EditText editText = (EditText) rowRoot.findViewById(R.id.edit_integer_row);

                    still_fresh = df.getValue().trim();
                }

                mListView.postDelayed(new Runnable() {
                    public void run() {

                        final TextView currentField = (TextView) view.findFocus();
                        scroll_detect = "false";
                        scroll_detect = PrefUtils.getScroll(view.getContext());
                        if (scroll_detect.equals("false") && flag.equals("true") && mm_ignore == false && df.getValue().trim().length() > 0) {

                            currentField.setTextColor(Color.RED);


                            TextView nextField = (TextView) view.focusSearch(View.FOCUS_DOWN);
                            if (nextField != null) {
                                nextField.requestFocus();
                            }

                        } else {
                            currentField.setTextColor(Color.BLACK);

                            TextView nextField = (TextView) view.focusSearch(View.FOCUS_DOWN);
                            if (nextField != null) {
                                nextField.requestFocus();
                            }

//                            if (position+1==fields.size())
//                            {
//
//                                swipe_right.requestFocus();
//
//                            }
                        }


                    }
                }, 0);
                return true;
            }
            return false;
        }


    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
}
