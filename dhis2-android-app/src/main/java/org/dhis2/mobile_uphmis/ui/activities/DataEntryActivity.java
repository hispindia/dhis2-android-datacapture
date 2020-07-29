package org.dhis2.mobile_uphmis.ui.activities;

import static android.text.TextUtils.isEmpty;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.dhis2.mobile_uphmis.R;
import org.dhis2.mobile_uphmis.WorkService;
import org.dhis2.mobile_uphmis.io.Constants;
import org.dhis2.mobile_uphmis.io.holders.DatasetInfoHolder;
import org.dhis2.mobile_uphmis.io.json.JsonHandler;
import org.dhis2.mobile_uphmis.io.json.ParsingException;
import org.dhis2.mobile_uphmis.io.models.Field;
import org.dhis2.mobile_uphmis.io.models.Form;
import org.dhis2.mobile_uphmis.io.models.Group;
import org.dhis2.mobile_uphmis.network.HTTPClient;
import org.dhis2.mobile_uphmis.network.NetworkUtils;
import org.dhis2.mobile_uphmis.network.Response;
import org.dhis2.mobile_uphmis.ui.adapters.dataEntry.FieldAdapter;
import org.dhis2.mobile_uphmis.ui.adapters.dataEntry.rows.KeyboardUtils;
import org.dhis2.mobile_uphmis.utils.PrefUtils;
import org.dhis2.mobile_uphmis.utils.TextFileUtils;
import org.dhis2.mobile_uphmis.utils.ToastManager;
import org.dhis2.mobile_uphmis.utils.ViewUtils;
import org.dhis2.mobile_uphmis.utils.date.expiryday.ExpiryDayValidator;
import org.dhis2.mobile_uphmis.utils.date.expiryday.ExpiryDayValidatorFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DataEntryActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Form>, SimpleGestureFilter.SimpleGestureListener {
    public static final String TAG = DataEntryActivity.class.getSimpleName();
    private SimpleGestureFilter detector;
    // state keys
    private static final String STATE_REPORT = "state:report";
    private static final String STATE_DOWNLOAD_ATTEMPTED = "state:downloadAttempted";
    private static final String STATE_DOWNLOAD_IN_PROGRESS = "state:downloadInProgress";
    private static final String STATE_SHOW_MENU_ITEM = "state_showMenuItem";
    private static String hiv_found_positive_anc = "";
    private static String hiv_positive_test_conducted = "";
    private static String anc_registrations = "";
    private static String pw_visited_anc_checkup = "";
    private static String measles_rubella_1st_dose = "";
    private static String fully_immunized_911_male = "";
    private static String fully_immunized_911__female = "";
    private static String live_birth_male = "";
    private static String live_birth_female = "";
    private static String still_birth_fresh = "";
    private static String still_birth_macerated = "";
    private static String institutional_delivery = "";
    private static String lang = "";
    private static String section_complete = "true";
    private static String newborns_breast_fed = "";
    private static String newborns_less_weight = "";
    private static String lbw_received = "";
    private static String nb_weight_1800 = "";
    private static String asha_submitted_hbnc = "";
    private static String asha_working = "";
    private static String asha_present = "";
    private static String immunisation_sessions_held = "";
    private static String children_died_29days = "";
    private static String pnd_right_1 = "";
    private static String pnd_right_2 = "";
    private static String pnd_right_3 = "";
    private static String pnd_right_4 = "";
    private static String pnd_right_5 = "";
    private static String preterm_new_born = "";
    private static String preterm_new_born_gestation = "";
    private static String syphilis_poc = "";
    private static String syphilis_pw = "";
    private static String pw_tested_hb = "";
    private static String anc_4 = "";
    private static String anc_1st_tri = "";
    private static String hb_7 = "";
    private static String hb_conducted = "";
    private static String pw_anaemia = "";
    private static String preterm_labour = "";
    private static Integer validation_errors_found = 0;
    private static String error_message = "";
    private static String cred = "";
    private static String server = "";
    private static Boolean NO_SECTIONS = false;

    private static String hypertension_detected = "";
    private static String hypertension_detected_institution = "";
    private static String misoprostol_hd = "";
    private static String sba = "";
    private static String non_sba = "";
    private static String hbnc = "";
    private static String nb_hbnc = "";
    private static String still_total_agg = "";
    private static String nb_weighted_birth = "";
    private static String pnc_48 = "";
    private static String IFA_180 = "";
    private static String IFA_180_24 = "";
    private static String cal_360 = "";
    private static String cal_360_25 = "";
    private static String iucd = "";
    private static String vitk1 = "";
    private static String nvbdcp_malaria = "";
    private static String nvbdcp_rdt = "";
    private static String pf_malaria = "";
    private static String opd_alo = "";
    private static String opd_all = "";
    private static String op_diabaties = "";
    private static String op_hypertension = "";
    private static String op_stroke = "";
    private static String op_acute_heart_disease = "";
    private static String op_Mental_illness = "";
    private static String op_Epilepsy = "";
    private static String op_Opthalamic = "";
    private static String op_dental = "";

    private static String pwa_hb = "";
    private static String pwa_bp = "";
    private static String pwa_urine_albumin = "";
    private static String pwa_abdominal_check = "";
    private static String pwa_weight = "";
    private static String NB_ASHA_Female_1800 = "";
    private static String NB_ASHA_Female_2500 = "";
    private static String NB_ASHA_male_1800 = "";
    private static String NB_ASHA_male_2500 = "";
    private static String opv0 = "";
    private static String hepi_birth = "";
    private static String institutional_delivery_48 = "";
    private static String institutional_delivery_conducted = "";
    private static final int LOADER_FORM_ID = 896927645;

    // views
    private RelativeLayout progressBarLayout;
    private AppCompatSpinner formGroupSpinner;
    private Form currentForm;

    // data entry view
    private static ListView dataEntryListView;
    private List<FieldAdapter> adapters;

    MenuItem saveMenuItem;

    // state
    private boolean downloadAttempted;
    private String mPeriod;
    private boolean showSaveMenuItem;

    private DatasetInfoHolder datasetInfoHolder;
    private ImageButton swipe_right;
    private ImageButton swipe_left;
    private Integer current_position = 0;

    final static Handler mHandler = new Handler(Looper.getMainLooper());
    private TextView currentField;
    private TextView disabled_fields;
    private TextView currentField1;

    public static void navigateTo(Activity activity, DatasetInfoHolder info) {
        if (info != null && activity != null) {
            Intent intent = new Intent(activity, DataEntryActivity.class);
            intent.putExtra(DatasetInfoHolder.TAG, info);

            activity.startActivity(intent);
            activity.overridePendingTransition(
                    R.anim.slide_up, R.anim.activity_open_exit);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PrefUtils.initScrollData(getBaseContext(), "false");
        super.onCreate(savedInstanceState);
        lang = PrefUtils.getLocale(getBaseContext());

        if (lang != null && lang.equals("hi")) {
            Locale locale = new Locale("hi");
            Locale.setDefault(locale);
            Configuration config = getBaseContext().getResources().getConfiguration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());
        }

        Bundle bundle = savedInstanceState;
        if (bundle == null) {
            bundle = getIntent().getExtras();
        }
        setContentView(R.layout.activity_data_entry);
        swipe_right = (ImageButton) findViewById(R.id.swipe_button);
        swipe_left = (ImageButton) findViewById(R.id.swipe_button_left);
        detector = new SimpleGestureFilter(this, this);
        setupToolbar(bundle);
        setupFormSpinner();
        setupProgressBar(savedInstanceState);
        setupListView();
        attemptToDownloadReport(savedInstanceState);
        buildReportDataEntryForm(savedInstanceState);

        initPeriod(bundle);
    }

    private void initPeriod(Bundle bundle) {
        datasetInfoHolder = bundle.getParcelable(DatasetInfoHolder.TAG);
        mPeriod = datasetInfoHolder.getPeriod();
    }


    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(RECEIVER);

        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(this)
                .registerReceiver(RECEIVER, new IntentFilter(TAG));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (adapters != null) {
            ArrayList<Group> groups = new ArrayList<>();
            for (FieldAdapter adapter : adapters) {
                groups.add(adapter.getGroup());
            }

            outState.putParcelableArrayList(STATE_REPORT, groups);
            outState.putBoolean(STATE_DOWNLOAD_ATTEMPTED, downloadAttempted);
            outState.putBoolean(STATE_DOWNLOAD_IN_PROGRESS, isProgressBarVisible());
            outState.putParcelable(DatasetInfoHolder.TAG, datasetInfoHolder);
            outState.putBoolean(STATE_SHOW_MENU_ITEM, showSaveMenuItem);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case org.dhis2.mobile_uphmis.R.id.action_save_data_set:
                upload();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.activity_close_enter, R.anim.slide_down);
    }

    @Override
    public Loader<Form> onCreateLoader(int id, Bundle args) {
        DatasetInfoHolder info = getIntent().getExtras()
                .getParcelable(DatasetInfoHolder.TAG);

        if (id == LOADER_FORM_ID && info != null) {
            return new DataLoader(DataEntryActivity.this, info);
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Form> loader, Form form) {
        if (loader != null && loader.getId() == LOADER_FORM_ID) {
            currentForm = form;
            loadGroupsIntoAdapters(form.getGroups(), form);
        }
    }

    @Override
    public void onLoaderReset(Loader<Form> loader) {
        System.out.println("loader reset");
    }

    private void setupToolbar(Bundle bundle) {
        showSaveMenuItem = bundle.getBoolean(STATE_SHOW_MENU_ITEM, false);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    private void setToolbarTitle(String title) {
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(title);
    }

    private void setupFormSpinner() {
        formGroupSpinner = (AppCompatSpinner) findViewById(R.id.spinner_drop_down);

        if (formGroupSpinner != null) {
            formGroupSpinner.setVisibility(View.GONE);
        }
    }

    private void setupProgressBar(Bundle savedInstanceState) {
        progressBarLayout = (RelativeLayout) findViewById(
                R.id.relativelayout_progress_bar);

        if (savedInstanceState != null) {
            boolean downloadInProgress = savedInstanceState
                    .getBoolean(STATE_DOWNLOAD_IN_PROGRESS, false);

            if (downloadInProgress) {
                showProgressBar();
            } else {
                hideProgressBar();
            }
        } else {
            hideProgressBar();
        }
    }

    private void setupListView() {
        PrefUtils.initScrollData(getBaseContext(), "false");
        dataEntryListView = (ListView) findViewById(R.id.list_of_fields);
        dataEntryListView.setRecyclerListener(new AbsListView.RecyclerListener() {
            @Override
            public void onMovedToScrapHeap(View view) {
                if (view.hasFocus()) {
                    view.clearFocus();
                    if (view instanceof EditText) {
                        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
            }
        });
        KeyboardUtils.addKeyboardToggleListener(this, new KeyboardUtils.SoftKeyboardToggleListener() {
            @Override
            public void onToggleSoftKeyboard(boolean isVisible) {
                Log.d("keyboard--", "keyboard visible: " + isVisible);
            }
        });


        dataEntryListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int lastPosition = -1;


            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {


            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (lastPosition == firstVisibleItem) {
                    PrefUtils.initScrollData(getBaseContext(), "false");
                    return;
                }
                if (firstVisibleItem > lastPosition) {
                    PrefUtils.initScrollData(getBaseContext(), "true");

                } else if (firstVisibleItem < lastPosition) {
                    PrefUtils.initScrollData(getBaseContext(), "true");
                } else {
                }
                lastPosition = firstVisibleItem;

            }


        });
    }


    private void uploadButtonEnabled(boolean active) {
        if (saveMenuItem != null) {
            saveMenuItem.setVisible(active);
        }
        showSaveMenuItem = active;
    }

    private void attemptToDownloadReport(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            downloadAttempted = savedInstanceState
                    .getBoolean(STATE_DOWNLOAD_ATTEMPTED, false);
        }

        if (!downloadAttempted && !isProgressBarVisible()) {
            downloadAttempted = true;
            if (NetworkUtils.checkConnection(this)) {
                getLatestValues();
            }
        }
    }

    private void buildReportDataEntryForm(Bundle savedInstanceState) {
        if (!isProgressBarVisible()) {
            List<Group> dataEntryGroups = null;

            if (savedInstanceState != null &&
                    savedInstanceState.containsKey(STATE_REPORT)) {
                dataEntryGroups = savedInstanceState
                        .getParcelableArrayList(STATE_REPORT);
            }

            // we did not load form before,
            // so we need to do so now
            if (dataEntryGroups == null || currentForm == null) {
                getSupportLoaderManager().restartLoader(LOADER_FORM_ID, null, this).forceLoad();
            } else {
                loadGroupsIntoAdapters(dataEntryGroups, currentForm);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(org.dhis2.mobile_uphmis.R.menu.menu_data_entry, menu);

        saveMenuItem = (MenuItem) menu.findItem(R.id.action_save_data_set);
        saveMenuItem.setVisible(showSaveMenuItem);

        return true;
    }

    private void showProgressBar() {
        ViewUtils.hideAndDisableViews(dataEntryListView);
        ViewUtils.enableViews(progressBarLayout);
    }

    private void hideProgressBar() {
        ViewUtils.enableViews(dataEntryListView);
        ViewUtils.hideAndDisableViews(progressBarLayout);
    }

    private boolean isProgressBarVisible() {
        return progressBarLayout.getVisibility() == View.VISIBLE;
    }

    private void loadGroupsIntoAdapters(List<Group> groups, Form form) {
        if (groups != null) {
            List<FieldAdapter> adapters = new ArrayList<>();

            boolean readOnly = false;
            if (form.getOptions().getexpiryDays() > 0) {
                int expiringDay = form.getOptions().getexpiryDays();
                String periodType = form.getOptions().getPeriodType();
                ExpiryDayValidator expiryDayValidator = ExpiryDayValidatorFactory.getExpiryDay(
                        periodType, expiringDay, mPeriod);
                if (!expiryDayValidator.canEdit()) {
                    readOnly = true;

                    showToast(R.string.dataset_readonly_by_expiry_days);
                }
            }
            if (form.isApproved()) {
                readOnly = true;

                showToast(R.string.dataset_readonly_by_approve);

            }

            uploadButtonEnabled(!readOnly);

            try {
                for (Group group : groups) {
                    adapters.add(new FieldAdapter(group, this, dataEntryListView, readOnly));
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            setupAdapters(adapters);
        }
    }

    private void setupAdapters(final List<FieldAdapter> adapters) {
        this.adapters = adapters;

        if (adapters.size() == 1) {
            formGroupSpinner.setVisibility(View.GONE);
            dataEntryListView.setAdapter(adapters.get(0));
            if (adapters.get(0).getLabel() != null /*&& !adapters.get(0).getLabel().equals(
                    FieldAdapter.FORM_WITHOUT_SECTION)*/) {
                setToolbarTitle(adapters.get(0).getLabel());
            }
            return;
        }

        List<String> formGroupLabels = new ArrayList<>();
        for (FieldAdapter fieldAdapter : adapters) {
            formGroupLabels.add(fieldAdapter.getLabel());
        }

        // Initializing an ArrayAdapter
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, R.layout.spinner_item, formGroupLabels) {
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                //@Sou change section color here
                //@Sou need to skip few autocalculated data-elements against below uid's
                Boolean Section_Complete = true;
                for (int i = 0; i < adapters.get(position).getGroup().getFields().size(); i++) {
                    if ("".equals(adapters.get(position).getGroup().getFields().get(i).getValue()) || " ".equals(adapters.get(position).getGroup().getFields().get(i).getValue()) || adapters.get(position).getGroup().getFields().get(i).getValue().equals(null)) {
                        if (adapters.get(position).getGroup().getFields().get(i).getDataElement().equals("W7I9VJKykwv") && adapters.get(position).getGroup().getFields().get(i).getCategoryOptionCombo().equals("udd2MF7Odu0")) {

                        } else {
                            Section_Complete = false;
                        }

                    }
                }
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (Section_Complete == true) {
                    // Set the item text color
                    tv.setTextColor(Color.parseColor("#f5f0f0"));
                    // Set the item background color
                    tv.setTextColor(Color.parseColor("#075E54"));
                } else {
                    // Set the alternate item text color
                    tv.setTextColor(Color.parseColor("#FFFFFF"));
                    // Set the alternate item background color
                    tv.setTextColor(Color.parseColor("#E57373"));
                }
                return view;
            }
        };

        adapter.setDropDownViewResource(R.layout.dropdown_spinner_item);

        formGroupSpinner.setVisibility(View.VISIBLE);
        formGroupSpinner.setAdapter(adapter);
        formGroupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dataEntryListView.setAdapter(adapters.get(position));

                current_position = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // stub implementation
            }
        });

        swipe_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current_position = current_position + 1;
                if (current_position != adapters.size() && current_position < adapters.size()) {
                    dataEntryListView.setAdapter(adapters.get(current_position));
                    formGroupSpinner.setSelection(current_position);
                } else if (current_position == adapters.size()) {
                    upload();
                    current_position = current_position - 1;
                }
            }
        });

        swipe_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (current_position != 0) {
                    current_position = current_position - 1;
                    if (current_position != adapters.size() && current_position < adapters.size()) {
                        dataEntryListView.setAdapter(adapters.get(current_position));
                        formGroupSpinner.setSelection(current_position);
                    }

                }

            }
        });


    }

    private void upload() {
        if (adapters == null) {
            ToastManager.makeToast(this, getString(R.string.something_went_wrong),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<Group> groups = new ArrayList<>();
        for (FieldAdapter adapter : adapters) {
            groups.add(adapter.getGroup());
        }
        if (currentForm.isFieldCombinationRequired() && !validateFieldsCombined(groups)) {
            ToastManager.makeToast(this, getString(R.string.all_questions_compulsories_error),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!validateFields(groups)) {
            ToastManager.makeToast(this, getString(R.string.compulsory_empty_error),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (!validateData(groups)) {

            return;
        }

        DatasetInfoHolder info = getIntent().getExtras()
                .getParcelable(DatasetInfoHolder.TAG);

        if (validation_errors_found == 0) {
            String still_fresh = "";
            String still_mas = "";
            //@Sou hack to delete values
            for (Group group : groups) {
                for (Field field : group.getFields()) {

                    if (field.getDataElement().equals("mexWK5BLs5H") && field.getCategoryOptionCombo().equals("Ti9FJqkSK6J")) {

                        if (group.getFields().size() == 5) {
                            still_fresh = group.getFields().get(3).getValue().trim();
                            still_mas = group.getFields().get(4).getValue().trim();
                            if ("".equals(still_fresh) && !"".equals(still_mas)) {
                                Integer still_total = Integer.parseInt(still_mas);
                                field.setValue(still_total.toString());

                            } else if ("".equals(still_mas) && !"".equals(still_fresh)) {
                                Integer still_total = Integer.parseInt(still_fresh);
                                field.setValue(still_total.toString());

                            } else if (!"".equals(still_fresh) && !"".equals(still_mas)) {
                                Integer still_total = Integer.parseInt(still_fresh) + Integer.parseInt(still_mas);
                                field.setValue(still_total.toString());

                            } else if ("".equals(still_fresh) && "".equals(still_mas)) {
                                field.setValue("");
                            }
                        }


                    }
                }
            }
            Intent intent = new Intent(this, WorkService.class);
            intent.putExtra(WorkService.METHOD, WorkService.METHOD_UPLOAD_DATASET);
            intent.putExtra(DatasetInfoHolder.TAG, info);
            intent.putExtra(Group.TAG, groups);

            startService(intent);
            finish();

        }


    }

    private boolean validateFieldsCombined(ArrayList<Group> groups) {
        for (Group group : groups) {
            for (Field field : group.getFields()) {
                if (field.getValue() == null || field.getValue().isEmpty()) {
                    for (Field fieldCompare : group.getFields()) {
                        if (field.getDataElement().equals(fieldCompare.getDataElement())
                                && fieldCompare.getValue() != null
                                && !fieldCompare.getValue().isEmpty()) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean validateFields(ArrayList<Group> groups) {
        for (Group group : groups) {
            for (Field field : group.getFields()) {
                if (field.isCompulsory() && (field.getValue() == null || field.getValue().equals(
                        ""))) {
                    return false;
                }
            }

        }
        return true;
    }


    private boolean validateData(ArrayList<Group> groups) {
        for (Group group : groups) {
            for (Field field : group.getFields()) {
                if (field.isCompulsory() && (field.getValue() == null || field.getValue().equals(
                        ""))) {
                    return false;
                }

                //@Sou all uid's defined below for in-app validations
                if (field.getDataElement().equals("Qk9Pc1jV9ck")) {
                    hiv_found_positive_anc = field.getValue();

                }
                if (field.getDataElement().equals("NI0EC5no6PO")) {
                    hiv_positive_test_conducted = field.getValue();

                }
                if (field.getDataElement().equals("qYpFbVo8WsL")) {
                    anc_registrations = field.getValue();

                }
                if (field.getDataElement().equals("sNqvJFIliYY")) {
                    pw_visited_anc_checkup = field.getValue();

                }
                if (field.getDataElement().equals("M34FAL69Kw4")) {
                    measles_rubella_1st_dose = field.getValue();

                }
                if (field.getDataElement().equals("GJKYhq2wR9L") && field.getCategoryOptionCombo().equals("iRNhRMvoSCx")) {
                    fully_immunized_911_male = field.getValue();

                }
                if (field.getDataElement().equals("GJKYhq2wR9L") && field.getCategoryOptionCombo().equals("wb51FJHqHxp")) {
                    fully_immunized_911__female = field.getValue();

                }
                if (field.getDataElement().equals("aknlXIekL1Z") && field.getCategoryOptionCombo().equals("iRNhRMvoSCx")) {
                    live_birth_male = field.getValue();

                }
                if (field.getDataElement().equals("aknlXIekL1Z") && field.getCategoryOptionCombo().equals("wb51FJHqHxp")) {
                    live_birth_female = field.getValue();
                }
                if (field.getDataElement().equals("OrMq254iPQ2") && field.getCategoryOptionCombo().equals("LeWpv23NQE0")) {
                    still_birth_fresh = field.getValue();
                }
                if (field.getDataElement().equals("OrMq254iPQ2") && field.getCategoryOptionCombo().equals("ocOywnb7dim")) {
                    still_birth_macerated = field.getValue();
                }
                if (field.getDataElement().equals("aRueVYr35yM")) {
                    institutional_delivery = field.getValue();
                }

                if (field.getDataElement().equals("VBqOqVKNQCr")) {
                    newborns_breast_fed = field.getValue();
                }
                if (field.getDataElement().equals("z7g5PpytzLV") && field.getCategoryOptionCombo().equals("Ti9FJqkSK6J")) {
                    //Newborns having weight less than 2.5 kg
                    newborns_less_weight = field.getValue();
                }
                if (field.getDataElement().equals("ofE1l4Y2mI6") && field.getCategoryOptionCombo().equals("Ti9FJqkSK6J")) {
                    lbw_received = field.getValue();
                }
                if (field.getDataElement().equals("qOsZZxXONmg") && field.getCategoryOptionCombo().equals("Ti9FJqkSK6J")) {
                    //#{qOsZZxXONmg.Ti9FJqkSK6J}
                    nb_weight_1800 = field.getValue();
                }
                if (field.getDataElement().equals("BEJiFwZDANQ") && field.getCategoryOptionCombo().equals("Ti9FJqkSK6J")) {
                    //#{BEJiFwZDANQ.Ti9FJqkSK6J}
                    asha_submitted_hbnc = field.getValue();
                }
                if (field.getDataElement().equals("C0g3dTjIJvy") && field.getCategoryOptionCombo().equals("Ti9FJqkSK6J")) {
                    asha_working = field.getValue();
                }
                if (field.getDataElement().equals("VW0l9mc6sAn") && field.getCategoryOptionCombo().equals("Ti9FJqkSK6J")) {
                    //##{VW0l9mc6sAn.Ti9FJqkSK6J}
                    asha_present = field.getValue();
                }
                if (field.getDataElement().equals("AoBmcUTJp4u") && field.getCategoryOptionCombo().equals("Ti9FJqkSK6J")) {
                    //##{VW0l9mc6sAn.Ti9FJqkSK6J}
                    immunisation_sessions_held = field.getValue();
                }
                if (field.getDataElement().equals("sIaJMceFvrN") && field.getCategoryOptionCombo().equals("NHTFXPXDdum")) {
                    //Children died 29days-1year(post natal death)
                    children_died_29days = field.getValue();
                }
                //#{r6Pc8UKQWel.DQ8rocRNUvB}+#{r6Pc8UKQWel.MIAeghFCobW}+#{r6Pc8UKQWel.uqNDgFiY3VA}+#{r6Pc8UKQWel.iyrx3OktTcp}+#{r6Pc8UKQWel.pmUrHNUQG5I}
                if (field.getDataElement().equals("r6Pc8UKQWel") && field.getCategoryOptionCombo().equals("DQ8rocRNUvB")) {
                    //Children died 29days-1year(post natal death)
                    pnd_right_1 = field.getValue();
                }
                if (field.getDataElement().equals("r6Pc8UKQWel") && field.getCategoryOptionCombo().equals("MIAeghFCobW")) {
                    //Children died 29days-1year(post natal death)
                    pnd_right_2 = field.getValue();
                }
                if (field.getDataElement().equals("r6Pc8UKQWel") && field.getCategoryOptionCombo().equals("uqNDgFiY3VA")) {
                    //Children died 29days-1year(post natal death)
                    pnd_right_3 = field.getValue();
                }
                if (field.getDataElement().equals("r6Pc8UKQWel") && field.getCategoryOptionCombo().equals("iyrx3OktTcp")) {
                    //Children died 29days-1year(post natal death)
                    pnd_right_4 = field.getValue();
                }
                if (field.getDataElement().equals("r6Pc8UKQWel") && field.getCategoryOptionCombo().equals("pmUrHNUQG5I")) {
                    //Children died 29days-1year(post natal death)
                    pnd_right_5 = field.getValue();
                }
                if (field.getDataElement().equals("HZ2jTqwQD6v") && field.getCategoryOptionCombo().equals("Ti9FJqkSK6J")) {
                    //#{HZ2jTqwQD6v.Ti9FJqkSK6J}
                    preterm_new_born = field.getValue();
                }
                if (field.getDataElement().equals("dzUXzVuhEeM") && field.getCategoryOptionCombo().equals("Ti9FJqkSK6J")) {
                    //#{#{dzUXzVuhEeM.Ti9FJqkSK6J}
                    preterm_new_born_gestation = field.getValue();
                }
                if (field.getDataElement().equals("kNXdBWNyt18") && field.getCategoryOptionCombo().equals("Ti9FJqkSK6J")) {
                    //##{kNXdBWNyt18.Ti9FJqkSK6J}
                    syphilis_poc = field.getValue();
                }
                if (field.getDataElement().equals("U7edhKWSPfA") && field.getCategoryOptionCombo().equals("Ti9FJqkSK6J")) {
                    //##{#{U7edhKWSPfA.Ti9FJqkSK6J}}
                    syphilis_pw = field.getValue();
                }
                if (field.getDataElement().equals("ui45G8KwpzU") && field.getCategoryOptionCombo().equals("Ti9FJqkSK6J")) {
                    //##{ui45G8KwpzU.Ti9FJqkSK6J} PW tested for Hb(4 or >4 times in ANCs)
                    pw_tested_hb = field.getValue();
                }
                if (field.getDataElement().equals("ZpgnTGpSkeg") && field.getCategoryOptionCombo().equals("Ti9FJqkSK6J")) {
                    //##{#{ZpgnTGpSkeg.Ti9FJqkSK6J}} PW tested for Hb(4 or >4 times in ANCs)
                    anc_4 = field.getValue();
                }
                if (field.getDataElement().equals("JY4UIeCYK00") && field.getCategoryOptionCombo().equals("Ti9FJqkSK6J")) {
                    //##{#{JY4UIeCYK00.Ti9FJqkSK6J}} PW tested for Hb(4 or >4 times in ANCs)
                    anc_1st_tri = field.getValue();
                }
                if (field.getDataElement().equals("YQImImjmZ73") && field.getCategoryOptionCombo().equals("Ti9FJqkSK6J")) {
                    //##{#{YQImImjmZ73.Ti9FJqkSK6J}} PW tested for Hb(4 or >4 times in ANCs)
                    hb_7 = field.getValue();
                }
                if (field.getDataElement().equals("m3bNQQa4kSe") && field.getCategoryOptionCombo().equals("Ti9FJqkSK6J")) {
                    //#{#{YQImImjmZ73.Ti9FJqkSK6J}} PW tested for Hb(4 or >4 times in ANCs)
                    hb_conducted = field.getValue();
                }
                if (field.getDataElement().equals("n5bZeD7f63Q") && field.getCategoryOptionCombo().equals("Ti9FJqkSK6J")) {
                    //#{#{YQImImjmZ73.Ti9FJqkSK6J}} PW tested for Hb(4 or >4 times in ANCs)
                    pw_anaemia = field.getValue();
                }
                if (field.getDataElement().equals("GH52XF190co") && field.getCategoryOptionCombo().equals("Ti9FJqkSK6J")) {
                    //#{#{YQImImjmZ73.Ti9FJqkSK6J}} PW tested for Hb(4 or >4 times in ANCs)
                    preterm_labour = field.getValue();
                }
                //r4heiTV2ATg-Ti9FJqkSK6J
                if (field.getDataElement().equals("r4heiTV2ATg") && field.getCategoryOptionCombo().equals("Ti9FJqkSK6J")) {
                    //#{#{YQImImjmZ73.Ti9FJqkSK6J}} PW tested for Hb(4 or >4 times in ANCs)
                    hypertension_detected_institution = field.getValue();
                }
                if (field.getDataElement().equals("OVGFcjdSMO0") && field.getCategoryOptionCombo().equals("Ti9FJqkSK6J")) {
                    //#{#{YQImImjmZ73.Ti9FJqkSK6J}} PW tested for Hb(4 or >4 times in ANCs)
                    hypertension_detected = field.getValue();
                }

                if (field.getDataElement().equals("fEvPhtVkNih") && field.getCategoryOptionCombo().equals("Ti9FJqkSK6J")) {
                    //#{#{YQImImjmZ73.Ti9FJqkSK6J}} PW tested for Hb(4 or >4 times in ANCs)
                    misoprostol_hd = field.getValue();
                }

                if (field.getDataElement().equals("HOrR1amEU6x") && field.getCategoryOptionCombo().equals("WVDe9q7ihBV")) {
                    //#{#{YQImImjmZ73.Ti9FJqkSK6J}} PW tested for Hb(4 or >4 times in ANCs)
                    sba = field.getValue();
                }
                if (field.getDataElement().equals("HOrR1amEU6x") && field.getCategoryOptionCombo().equals("YOjJNrvr2j2")) {
                    //#{#{YQImImjmZ73.Ti9FJqkSK6J}} PW tested for Hb(4 or >4 times in ANCs)
                    non_sba = field.getValue();
                }
                if (field.getDataElement().equals("dBjHW8OZJo8") && field.getCategoryOptionCombo().equals("Ti9FJqkSK6J")) {
                    //#{#{YQImImjmZ73.Ti9FJqkSK6J}} PW tested for Hb(4 or >4 times in ANCs)
                    hbnc = field.getValue();
                }
                if (field.getDataElement().equals("Nsll4RxgNlo") && field.getCategoryOptionCombo().equals("Ti9FJqkSK6J")) {
                    //#{#{YQImImjmZ73.Ti9FJqkSK6J}} PW tested for Hb(4 or >4 times in ANCs)
                    institutional_delivery_48 = field.getValue();
                }
                if (field.getDataElement().equals("aRueVYr35yM") && field.getCategoryOptionCombo().equals("Ti9FJqkSK6J")) {
                    //#{#{YQImImjmZ73.Ti9FJqkSK6J}} PW tested for Hb(4 or >4 times in ANCs)
                    institutional_delivery_conducted = field.getValue();
                }
                if (field.getDataElement().equals("lf5fsWhrqEv") && field.getCategoryOptionCombo().equals("Ti9FJqkSK6J")) {
                    //#{#{YQImImjmZ73.Ti9FJqkSK6J}} PW tested for Hb(4 or >4 times in ANCs)
                    nb_hbnc = field.getValue();
                }
                if (field.getDataElement().equals("mexWK5BLs5H") && field.getCategoryOptionCombo().equals("Ti9FJqkSK6J")) {
                    //#{#{YQImImjmZ73.Ti9FJqkSK6J}} PW tested for Hb(4 or >4 times in ANCs)
                    still_total_agg = field.getValue();
                }
                if (field.getDataElement().equals("qyhm2Egx86Z") && field.getCategoryOptionCombo().equals("Ti9FJqkSK6J")) {
                    //#{#{YQImImjmZ73.Ti9FJqkSK6J}} PW tested for Hb(4 or >4 times in ANCs)
                    nb_weighted_birth = field.getValue();
                }
                if (field.getDataElement().equals("a1Itlc1Um1p") && field.getCategoryOptionCombo().equals("Ti9FJqkSK6J")) {
                    //#{#{YQImImjmZ73.Ti9FJqkSK6J}} PW tested for Hb(4 or >4 times in ANCs)
                    pnc_48 = field.getValue();
                }
                if (field.getDataElement().equals("K57GqAPepet") && field.getCategoryOptionCombo().equals("Ti9FJqkSK6J")) {
                    //#{#{YQImImjmZ73.Ti9FJqkSK6J}} PW tested for Hb(4 or >4 times in ANCs)
                    IFA_180 = field.getValue();
                }
                if (field.getDataElement().equals("Seg3NZfY93d") && field.getCategoryOptionCombo().equals("Ti9FJqkSK6J")) {
                    //#{#{YQImImjmZ73.Ti9FJqkSK6J}} PW tested for Hb(4 or >4 times in ANCs)
                    IFA_180_24 = field.getValue();
                }
                if (field.getDataElement().equals("aoQXEbrGmSD") && field.getCategoryOptionCombo().equals("Ti9FJqkSK6J")) {
                    //#{#{YQImImjmZ73.Ti9FJqkSK6J}} PW tested for Hb(4 or >4 times in ANCs)
                    cal_360 = field.getValue();
                }
                if (field.getDataElement().equals("hOrwy47o7mE") && field.getCategoryOptionCombo().equals("Ti9FJqkSK6J")) {
                    //#{#{YQImImjmZ73.Ti9FJqkSK6J}} PW tested for Hb(4 or >4 times in ANCs)
                    cal_360_25 = field.getValue();
                }
                if (field.getDataElement().equals("YyUc4SanMDt") && field.getCategoryOptionCombo().equals("Ti9FJqkSK6J")) {
                    //#{#{YQImImjmZ73.Ti9FJqkSK6J}} PW tested for Hb(4 or >4 times in ANCs)
                    iucd = field.getValue();
                }
                if (field.getDataElement().equals("FKYjvP13UuP") && field.getCategoryOptionCombo().equals("Ti9FJqkSK6J")) {
                    //#{#{YQImImjmZ73.Ti9FJqkSK6J}} PW tested for Hb(4 or >4 times in ANCs)
                    vitk1 = field.getValue();
                }
                if (field.getDataElement().equals("micSbSDTlSC") && field.getCategoryOptionCombo().equals("Ti9FJqkSK6J")) {
                    //#{#{YQImImjmZ73.Ti9FJqkSK6J}} PW tested for Hb(4 or >4 times in ANCs)
                    opv0 = field.getValue();
                }
                if (field.getDataElement().equals("ZrNicVLgWiz") && field.getCategoryOptionCombo().equals("Ti9FJqkSK6J")) {
                    //#{#{YQImImjmZ73.Ti9FJqkSK6J}} PW tested for Hb(4 or >4 times in ANCs)
                    hepi_birth = field.getValue();
                }
                if (field.getDataElement().equals("G3EkrXkmwVj") && field.getCategoryOptionCombo().equals("Ti9FJqkSK6J")) {
                    //#{#{YQImImjmZ73.Ti9FJqkSK6J}} PW tested for Hb(4 or >4 times in ANCs)
                    nvbdcp_malaria = field.getValue();
                }
                if (field.getDataElement().equals("tvADxnVnjHz") && field.getCategoryOptionCombo().equals("Ti9FJqkSK6J")) {
                    //#{#{YQImImjmZ73.Ti9FJqkSK6J}} PW tested for Hb(4 or >4 times in ANCs)
                    nvbdcp_rdt = field.getValue();
                }

                if (field.getDataElement().equals("bPG9QePOOSD") && field.getCategoryOptionCombo().equals("Ti9FJqkSK6J")) {
                    //#{#{YQImImjmZ73.Ti9FJqkSK6J}} PW tested for Hb(4 or >4 times in ANCs)
                    pf_malaria = field.getValue();
                }
                if (field.getDataElement().equals("YEbwZRpntxW") && field.getCategoryOptionCombo().equals("Ti9FJqkSK6J")) {
                    //#{#{YQImImjmZ73.Ti9FJqkSK6J}} PW tested for Hb(4 or >4 times in ANCs)
                    opd_alo = field.getValue();
                }
                if (field.getDataElement().equals("jWnx3ILZDQ7") && field.getCategoryOptionCombo().equals("Ti9FJqkSK6J")) {
                    opd_all = field.getValue();
                }
                if (field.getDataElement().equals("xfFUwIeiPY1")) {

                    if (field.getCategoryOptionCombo().equals("EnQPoVNAi2p")) {
                        op_diabaties = field.getValue();
                    }
                    if (field.getCategoryOptionCombo().equals("Mmdd2bEh3Yp")) {
                        op_hypertension = field.getValue();
                    }
                    if (field.getCategoryOptionCombo().equals("vE5EsFqAEI2")) {
                        op_stroke = field.getValue();
                    }
                    if (field.getCategoryOptionCombo().equals("MqKow2RWPVy")) {
                        op_acute_heart_disease = field.getValue();
                    }
                    if (field.getCategoryOptionCombo().equals("srquQZDqZof")) {
                        op_Mental_illness = field.getValue();
                    }
                    if (field.getCategoryOptionCombo().equals("X4UVVhdHhjq")) {
                        op_Epilepsy = field.getValue();
                    }
                    if (field.getCategoryOptionCombo().equals("NbjYj1XvveT")) {
                        op_Opthalamic = field.getValue();
                    }
                    if (field.getCategoryOptionCombo().equals("W1M1x4zDNZm")) {
                        op_dental = field.getValue();
                    }

                }

                if (field.getDataElement().equals("i4FPzOZoySP")) {

                    if (field.getCategoryOptionCombo().equals("JQZYTwtWe9F")) {
                        pwa_hb = field.getValue();
                    }
                    if (field.getCategoryOptionCombo().equals("Jrg2sxz2fFR")) {
                        pwa_bp = field.getValue();
                    }
                    if (field.getCategoryOptionCombo().equals("N2WiGW4eJMI")) {
                        pwa_urine_albumin = field.getValue();
                    }
                    if (field.getCategoryOptionCombo().equals("Gv4pdY5V5fu")) {
                        pwa_abdominal_check = field.getValue();
                    }
                    if (field.getCategoryOptionCombo().equals("B6SZw6rCwqH")) {
                        pwa_weight = field.getValue();
                    }

                }

                if (field.getDataElement().equals("Lb2DzKeoiV4")) {

                    if (field.getCategoryOptionCombo().equals("yYVLFvBgeK4")) {
                        NB_ASHA_Female_1800 = field.getValue();
                    }
                    if (field.getCategoryOptionCombo().equals("uUXmboutton")) {
                        NB_ASHA_Female_2500 = field.getValue();
                    }

                }

                if (field.getDataElement().equals("PgCK0srUhVZ")) {

                    if (field.getCategoryOptionCombo().equals("yYVLFvBgeK4")) {
                        NB_ASHA_male_1800 = field.getValue();
                    }
                    if (field.getCategoryOptionCombo().equals("uUXmboutton")) {
                        NB_ASHA_male_2500 = field.getValue();
                    }

                }

            }
        }

        //Rule: Number of PW screened positive for HIV <=
        if (!"".equals(hiv_found_positive_anc) && !"".equals(hiv_positive_test_conducted) && !" ".equals(hiv_positive_test_conducted) && !" ".equals(hiv_found_positive_anc)) {

            if (Integer.parseInt(hiv_found_positive_anc) > Integer.parseInt(hiv_positive_test_conducted)) {
                if (lang != null && lang.equals("hi")) {
                    validation_errors_found++;
                    error_message += validation_errors_found + "। M10.B 10.2.1.b एचआईवी +ve पाया गया मामला(" + hiv_found_positive_anc + ")--- M10.B 10.2.1.a एचआईवी के लिए जांच की गयी गर्भवती महिलाओं की संख्या से कम या बराबर होना चाहिए (" + hiv_positive_test_conducted + ")" + "\n\n";

                } else {
                    validation_errors_found++;
                    error_message += validation_errors_found + ". M10.B 10.2.1.b HIV found +ve (" + hiv_found_positive_anc + ") should be less than or equal to M10.B 10.2.1.a Number of pregnant women screened(" + hiv_positive_test_conducted + ")" + "\n\n";

                }

            }
        }


        //Rule: Child immunisation (9-11months) - Measles & Rubella (MR)- 1st Dose 1st dose >=
        if (!"".equals(measles_rubella_1st_dose) && !" ".equals(measles_rubella_1st_dose) && !"".equals(fully_immunized_911_male) && !" ".equals(fully_immunized_911_male) && !"".equals(fully_immunized_911__female) && !" ".equals(fully_immunized_911__female)) {
            if (Integer.parseInt(measles_rubella_1st_dose) < (Integer.parseInt(fully_immunized_911_male) + Integer.parseInt(fully_immunized_911__female))) {
                int total = Integer.parseInt(fully_immunized_911_male) + Integer.parseInt(fully_immunized_911__female);
                if (lang != null && lang.equals("hi")) {
//                    int total=Integer.parseInt(fully_immunized_911_male)+Integer.parseInt(fully_immunized_911__female);
                    validation_errors_found++;
                    error_message += validation_errors_found + "। M6.B 6.2.1 बाल प्रतिरक्षण - खसरा और रूबेला (एमआर) - प्रथम खुराक (" + measles_rubella_1st_dose + ") M6.C 6.2.4.a + 6.2.4.b - 9 से 11 महीनों के बीच आयु वर्ग के बच्चों की संख्या जिन्हे पूर्ण प्रतिरक्षित किया- (पुरुष + महिला)से अधिक या बराबर होना चाहिए। (" + total + ")(" + fully_immunized_911_male + " + " + fully_immunized_911__female + ")" + "\n\n";

                } else {
                    validation_errors_found++;
                    error_message += validation_errors_found + ". M6.B 6.2.1 Measles & Rubella (MR)- 1st dose (" + measles_rubella_1st_dose + ") should be greater than or equal to M6.C (6.2.4.a + 6.2.4.b ) Full Immunized (male + female) (" + total + ")(" + fully_immunized_911_male + " + " + fully_immunized_911__female + ")" + "\n\n";

                }

            }
        }

        //Rule: Live Births  (M4 - 4.1.1(a) + 4.1.1(b))+ Still Birth (M4 - 4.1.3) >= Total Deliveries conducted at  institutions (M2 - 2.1)
        if (!"".equals(live_birth_male) && !" ".equals(live_birth_male) && !"".equals(live_birth_female) && !" ".equals(live_birth_female) && !"".equals(still_birth_fresh) && !" ".equals(still_birth_fresh) && !"".equals(still_birth_macerated) && !" ".equals(still_birth_macerated) && !"".equals(institutional_delivery) && !" ".equals(institutional_delivery)) {
            if ((Integer.parseInt(live_birth_male) + Integer.parseInt(live_birth_female) + Integer.parseInt(still_birth_fresh) + Integer.parseInt(still_birth_macerated)) < Integer.parseInt(institutional_delivery)) {
                int total = Integer.parseInt(live_birth_male) + Integer.parseInt(live_birth_female) + Integer.parseInt(still_birth_fresh) + Integer.parseInt(still_birth_macerated);
                if (lang != null && lang.equals("hi")) {
                    validation_errors_found++;
                    error_message += validation_errors_found + "। M2.B 2.2 कुल प्रसव आयोजित (" + institutional_delivery + ") M3- 3.3.1.a+3.1.1.b (जीवित जन्म (पुरुष + महिला) + U2.1.1+U2.1.2 स्टिल बर्थ (फ्रेश + मैकरेटेड) से कम या बराबर होना चाहिए। (" + total + ")(" + live_birth_male + " + " + live_birth_female + "+" + still_birth_fresh + "+" + still_birth_macerated + ")" + "\n\n";

                } else {
                    validation_errors_found++;
                    error_message += validation_errors_found + ". M2.B 2.2  Total Deliveries conducted (" + institutional_delivery + ") should be less than or equal to M3- 3.3.1.a+3.1.1.b(Live births(male+female) + U2.1.1+U2.1.2  Still births(Fresh+Macerated)) (" + total + ")(" + live_birth_male + " + " + live_birth_female + "+" + still_birth_fresh + "+" + still_birth_macerated + ")" + "\n\n";

                }

            }
        }


        if (!"".equals(newborns_less_weight) && !" ".equals(newborns_less_weight) && !"".equals(nb_weight_1800) && !" ".equals(nb_weight_1800)) {
            if (Integer.parseInt(newborns_less_weight) < Integer.parseInt(nb_weight_1800)) {
                if (lang != null && lang.equals("hi")) {
                    validation_errors_found++;
                    error_message += validation_errors_found + "। M3.B- 3.3.2 नवजात शिशुओं की संख्या जिनका वजन 2.5 किलो से कम रहा (" + newborns_less_weight + ") M3.B-U2.2 नवजात शिशुओं की संख्या जिनका वजन 1800 ग्राम से कम रहा से अधिक या बराबर होना चाहिए - (" + nb_weight_1800 + ")" + "\n\n";

                } else {
                    validation_errors_found++;
                    error_message += validation_errors_found + ". M3.B-3.3.2  2.5 Newborns having weight less than 2.5 kg (" + newborns_less_weight + ") should be greater than or equal to  M3.B-U2.2  Newborn weighted at birth less than 1800 gram (" + nb_weight_1800 + ")" + "\n\n";

                }
            }
        }

        //Rule 6
        if (!"".equals(asha_present) && !" ".equals(asha_present) && !"".equals(immunisation_sessions_held) && !" ".equals(immunisation_sessions_held)) {
            if (Integer.parseInt(asha_present) > Integer.parseInt(immunisation_sessions_held)) {
                if (lang != null && lang.equals("hi")) {
                    validation_errors_found++;
                    error_message += validation_errors_found + "। M6.H-6.7.3 सत्रों की संख्या  जिनमें आशा कार्यकर्ता उपस्थित थीं (" + asha_present + ") M6.H- 6.7.2 प्रतिरक्षण आयोजित सत्र से कम या बराबर होना चाहिए(" + immunisation_sessions_held + ")" + "\n\n";

                } else {
                    validation_errors_found++;
                    error_message += validation_errors_found + ". M6.H- 6.7.3 Number of Immunisation sessions where ASHAs were present (" + asha_present + ") should be less than or equal to M6.H- 6.7.2 Immunisation sessions held  (" + immunisation_sessions_held + ")" + "\n\n";

                }

            }
        }

        if (!"".equals(syphilis_poc) && !" ".equals(syphilis_poc) && !"".equals(syphilis_pw) && !" ".equals(syphilis_pw)) {
            if (Integer.parseInt(syphilis_poc) > Integer.parseInt(syphilis_pw)) {
                if (lang != null && lang.equals("hi")) {
                    validation_errors_found++;
                    error_message += validation_errors_found + "। M1.D- 1.5.1.b उपरोक्त में, गर्भवती महिला की संख्या जो सिफलिस के लिए सेरो पॉजिटिव पायी गयी(" + syphilis_poc + ") M1.D- 1.5.1.a गर्भवती महिलाओं की संख्या जिनका सिफ़िलिस के लिए पीओसी टेस्ट किया गयासे कम या बराबर होना चाहिए(" + syphilis_pw + ")" + "\n\n";

                } else {
                    validation_errors_found++;
                    error_message += validation_errors_found + ". M1.D-1.5.1.b Out of above, pregnant women found sero positive for Syphilis (" + syphilis_poc + ") should be less than or equal to M1.D-1.5.1.a  Number of pregnant women tested for Syphilis (" + syphilis_pw + ")" + "\n\n";

                }

            }
        }


        //Rule:Number of PW tested for Haemoglobin (Hb ) 4 or more than 4 times for respective ANCs <= Number of PW received 4 or more ANC check ups
        if (!"".equals(pw_tested_hb) && !" ".equals(pw_tested_hb) && !"".equals(anc_4) && !" ".equals(anc_4)) {
            if (Integer.parseInt(pw_tested_hb) > Integer.parseInt(anc_4)) {
                if (lang != null && lang.equals("hi")) {
                    validation_errors_found++;
                    error_message += validation_errors_found + "। M1.B-1.4.1 4 या अधिक बार एचबी जांच की गयी गर्भवती महिलाओं की संख्या (सापेक्षिक प्रसव पूर्व देखभाल के तहत)(" + pw_tested_hb + ") M1.B- 1.2.7 गर्भवती महिलाओं की संख्या जिन्हे 4 या अधिक बार एएनसी जांच मिली होसे कम या बराबर होना चाहिए (" + anc_4 + ")" + "\n\n";

                } else {
                    validation_errors_found++;
                    error_message += validation_errors_found + ". M1.B-1.4.1 Pregnant women tested for Haemoglobin (Hb ) 4 or more than 4 times for respective ANCs (" + pw_tested_hb + ") should be less than or equal to M1.B- 1.2.7  4 ANC checkups  (" + anc_4 + ")" + "\n\n";

                }

            }
        }
        //@Rule 11 M1.B. 1.2.7 <=  M1.A 1.1
        if (!"".equals(anc_4) && !" ".equals(anc_4) && !"".equals(anc_registrations) && !" ".equals(anc_registrations)) {
            if (Integer.parseInt(anc_4) > Integer.parseInt(anc_registrations)) {
                if (lang != null && lang.equals("hi")) {
                    validation_errors_found++;
                    error_message += validation_errors_found + "। M1.B- 1.2.7 गर्भवती महिलाओं की संख्या जिन्हे 4 या अधिक बार एएनसी जांच मिली हो (" + anc_4 + ") M1.A- 1.1 एएनसी के लिए पंजीकृत गर्भवती महिलाओं की कुल संख्या से कम या बराबर होना चाहिए ((" + anc_registrations + ")" + "\n\n";

                } else {
                    validation_errors_found++;
                    error_message += validation_errors_found + ". M1.B-1.2.7 Number of PW received 4 or more ANC check ups (" + anc_4 + ") should be less than or equal to M1.A- 1.1 Total number of pregnant women registered for ANC  (" + anc_registrations + ")" + "\n\n";

                }

            }
        }

        //@Rule 12 M1.C
        if (!"".equals(hypertension_detected) && !" ".equals(hypertension_detected) && !"".equals(hypertension_detected_institution) && !" ".equals(hypertension_detected_institution)) {
            if (Integer.parseInt(hypertension_detected_institution) > Integer.parseInt(hypertension_detected)) {
                if (lang != null && lang.equals("hi")) {
                    validation_errors_found++;
                    error_message += validation_errors_found + "| M1.C- 1.3.1a उच्च रक्तचाप वाली गर्भवती महिलाओं के नए मामलों में, स्वास्थ्य केंद्र पर प्रबंधन किए गए मामलों की संख्या(" + hypertension_detected_institution + ") M1.C- 1.3.1 उच्च रक्तचाप वाली गर्भवती महिलाओं के नए मामलों की संख्या से कम या बराबर होना चाहिए ((" + hypertension_detected + ")" + "\n\n";

                } else {
                    validation_errors_found++;
                    error_message += validation_errors_found + ". M1.C-1.3.1a New hypertension cases managed at institution(PW)" + hypertension_detected_institution + ") should be less than or equal to M1.C- 1.3.1 New Cases of pregnant women with hypertension  (" + hypertension_detected + ")" + "\n\n";

                }

            }
        }

        //@Rule 13  syphilis_poc
        if (!"".equals(syphilis_pw) && !" ".equals(syphilis_pw) && !"".equals(anc_registrations) && !" ".equals(anc_registrations)) {
            if (Integer.parseInt(syphilis_pw) > Integer.parseInt(anc_registrations)) {
                if (lang != null && lang.equals("hi")) {
                    validation_errors_found++;
                    error_message += validation_errors_found + "| M1.D-1.5.1.a गर्भवती महिलाओं की संख्या जिनका सिफ़िलिस के लिए पीओसी टेस्ट किया गया (" + syphilis_pw + ") M1.A- 1.1 एएनसी के लिए पंजीकृत गर्भवती महिलाओं की कुल संख्या से कम या बराबर होना चाहिए (" + anc_registrations + ")" + "\n\n";

                } else {
                    validation_errors_found++;
                    error_message += validation_errors_found + ". M1.D-1.5.1.a PW found Seropositive for syphilis(POC)" + syphilis_pw + ") should be less than or equal to M1-1.1 Pregnant women visited for ANC check up  (" + anc_registrations + ")" + "\n\n";

                }

            }
        }

        //@Rule 14
        if (!"".equals(misoprostol_hd) && !" ".equals(misoprostol_hd) && !"".equals(sba) && !" ".equals(sba) && !"".equals(non_sba) && !" ".equals(non_sba)) {
            if (Integer.parseInt(misoprostol_hd) > (Integer.parseInt(sba) + Integer.parseInt(non_sba))) {
                int total = Integer.parseInt(sba) + Integer.parseInt(non_sba);
                if (lang != null && lang.equals("hi")) {
                    validation_errors_found++;
                    error_message += validation_errors_found + "| M2.A-2.1.2 गर्भवती महिलाओं की संख्या जिन्हे गृह प्रसव के दौरान मीसोप्रोस्टोल का टैब्लेट दिया गया (" + misoprostol_hd + ") M2.A-(2.A-2.1.1.a +2.1.1.b)  जो एस॰बी॰ए॰ प्रशिक्षित (डॉक्टर / नर्स / एएनएम) द्वारा कराया गया + 2.1.1.b जो गैर एस॰बी॰ए॰ (प्रशिक्षित बर्थ अटेंडेंट (टीबीए) / रिलेटिव / आदि) द्वारा कराया गया से कम या बराबर होना चाहिए (" + total + ")(" + sba + "+" + non_sba + ")" + "\n\n";

                } else {
                    validation_errors_found++;
                    error_message += validation_errors_found + ". M2.A- 2.1.2 PW given Misoprostol home delivery(" + misoprostol_hd + ") should be less than or equal to sum of M2.A-(2.A-2.1.1.a +2.1.1.b) Home Deliveries Attended by SBA+ Non-SBA  (" + total + ")(" + sba + " + " + non_sba + ")" + "\n\n";

                }

            }
        }

        //@Rule 15
//        if( !"".equals(hbnc)&& !" ".equals(hbnc) && !"".equals(sba) && !" ".equals(sba)&& !"".equals(non_sba) && !" ".equals(non_sba))
//        {
//            if (Integer.parseInt(hbnc)>(Integer.parseInt(sba)+Integer.parseInt(non_sba)))
//            {
//                if (lang!=null&&lang.equals("hi"))
//                {
//                    validation_errors_found++;
//                    error_message += validation_errors_found +"M4.A- 2.1.3 गृह प्रसव के मामले में नवजात शिशुओं की संख्या जिन्हें 7 गृह आधारित नवजात शिशु देखभाल (एचबीएनसी) मिली हो ("+ hbnc+") M2.A- 2.1.1.a  जो एस॰बी॰ए॰ प्रशिक्षित (डॉक्टर / नर्स / एएनएम) द्वारा कराया गया + 2.1.1.b जो गैर एस॰बी॰ए॰ (प्रशिक्षित बर्थ अटेंडेंट (टीबीए) / रिलेटिव / आदि) द्वारा कराया गया से कम या बराबर होना चाहिए ("+sba+ "+"+non_sba +")"+"\n\n";
//
//                }
//                else
//                {
//                    validation_errors_found++;
//                    error_message += validation_errors_found +"M4.A 2.1.3 NB received 7 HBNC"+ hbnc+") should be less than or equal to sum of M2.A-2.1 Home Deliveries Attended by SBA+ Non-SBA  ("+sba+" + "+non_sba+ ")"+"\n\n";
//
//                }
//
//            }
//        }

        //@Rule 16
        if (!"".equals(institutional_delivery_48) && !" ".equals(institutional_delivery_48) && !"".equals(institutional_delivery_conducted) && !" ".equals(institutional_delivery_conducted)) {
            if (Integer.parseInt(institutional_delivery_48) > (Integer.parseInt(institutional_delivery_conducted))) {
                if (lang != null && lang.equals("hi")) {
                    validation_errors_found++;
                    error_message += validation_errors_found + "| M2.B- 2.2.1 कुल संस्थागत प्रसव में, प्रसव के 48 घंटों के भीतर छुट्टी दे दी गई महिलाओं की संख्या (" + institutional_delivery_48 + ") M2.B- 2.2 संस्थागत प्रसव की संख्या से कम या बराबर होना चाहिए (" + institutional_delivery_conducted + ")" + "\n\n";

                } else {
                    validation_errors_found++;
                    error_message += validation_errors_found + ". M2.B-2.2.1 Women discharged under 48 hours of delivery(" + institutional_delivery_48 + ") should be less than or equal to M2.B-2.2 Institutional delivery(including c-section)  (" + institutional_delivery_conducted + ")" + "\n\n";

                }

            }
        }

        //@Rule 17
//        if( !"".equals(nb_hbnc)&& !" ".equals(nb_hbnc) && !"".equals(institutional_delivery_conducted) && !" ".equals(institutional_delivery_conducted))
//        {
//            if (Integer.parseInt(nb_hbnc)>(Integer.parseInt(institutional_delivery_conducted)))
//            {
//                if (lang!=null&&lang.equals("hi"))
//                {
//                    validation_errors_found++;
//                    error_message += validation_errors_found +"M4.A-2.2.2 संस्थागत प्रसव पश्चात नवजात शिशुओं की संख्या जिन्हें 6 एचबीएनसी विज़िट मिली हो ("+ nb_hbnc+") M2.B- 2.2 संस्थागत प्रसव की संख्या से कम या बराबर होना चाहिए ("+institutional_delivery_conducted+ ")"+"\n\n";
//
//                }
//                else
//                {
//                    validation_errors_found++;
//                    error_message += validation_errors_found +"M4.A-2.2.2 NB received 6 HBNC after institutional delivery"+ nb_hbnc+") should be less than or equal to M2.B-2.2 Institutional delivery(including c-section)  ("+institutional_delivery_conducted+ ")"+"\n\n";
//
//                }
//
//            }
//        }

        //@Rule 18 @Sou need to change message
        if (!"".equals(live_birth_male) && !" ".equals(live_birth_male) && !"".equals(live_birth_female) && !" ".equals(live_birth_female) && !"".equals(still_birth_fresh) && !" ".equals(still_birth_fresh) && !"".equals(still_birth_macerated) && !" ".equals(still_birth_macerated) && !"".equals(sba) && !" ".equals(sba) && !"".equals(non_sba) && !" ".equals(non_sba) && !"".equals(institutional_delivery_conducted) && !" ".equals(institutional_delivery_conducted)) {
            if ((Integer.parseInt(live_birth_male) + Integer.parseInt(live_birth_female) + Integer.parseInt(still_birth_macerated) + Integer.parseInt(still_birth_fresh)) < (Integer.parseInt(sba) + Integer.parseInt(non_sba) + Integer.parseInt(institutional_delivery_conducted))) {
                int total1 = Integer.parseInt(live_birth_male) + Integer.parseInt(live_birth_female) + Integer.parseInt(still_birth_fresh) + Integer.parseInt(still_birth_macerated);
                int total2 = Integer.parseInt(sba) + Integer.parseInt(non_sba) + Integer.parseInt(institutional_delivery_conducted);
                int total_live = Integer.parseInt(sba) + Integer.parseInt(non_sba) + Integer.parseInt(institutional_delivery_conducted);
                if (lang != null && lang.equals("hi")) {

                    validation_errors_found++;
                    error_message += validation_errors_found + "| M3-3.3.1.a+3.1.1.b (जीवित जन्म (पुरुष + महिला) + U2.1.1 + U2.1.2 (फ्रेश+मैसरेटेड) मृत जन्म (" + total1 + ")(" + live_birth_male + " + " + live_birth_female + " + " + Integer.parseInt(still_birth_macerated) + "+" + Integer.parseInt(still_birth_fresh) + ")--- M2.A-2.1.1.a +2.1.1.b(एस॰बी॰ए + गैर एस॰बी॰ए॰) + M2.B-2.2 संस्थागत प्रसव की संख्या से अधिक या बराबर होना चाहिए(" + total2 + ")(" + sba + " + " + non_sba + " + " + institutional_delivery_conducted + ")" + "\n\n";

                } else {
                    validation_errors_found++;
                    error_message += validation_errors_found + ". M3-3.3.1.a+3.1.1.b Live births(Male + Female) + U2.1.1 + U2.1.2 (Fresh+Macerated) still birth(" + total1 + ")(" + live_birth_male + " + " + live_birth_female + " + " + Integer.parseInt(still_birth_macerated) + "+" + Integer.parseInt(still_birth_fresh) + ") should be less than or equal to sum of M2.A-2.1.1.a +2.1.1.b Home Deliveries Attended by SBA+ Non-SBA + M2.B-2.2 Institutional delivery(including c-section) (" + total2 + ")(" + sba + " + " + non_sba + " + " + institutional_delivery_conducted + ")" + "\n\n";

                }

            }
        }

        //@Rule 19
        if (!"".equals(preterm_new_born) && !" ".equals(preterm_new_born) && !"".equals(live_birth_male) && !" ".equals(live_birth_male) && !"".equals(live_birth_female) && !" ".equals(live_birth_female)) {
            if (Integer.parseInt(preterm_new_born) > (Integer.parseInt(live_birth_male) + Integer.parseInt(live_birth_female))) {
                int total = Integer.parseInt(live_birth_male) + Integer.parseInt(live_birth_female);
                if (lang != null && lang.equals("hi")) {
                    validation_errors_found++;
                    error_message += validation_errors_found + "| M3- 3.1.2 प्री-टर्म नवजात शिशुओं की संख्या (<37 सप्ताह की गर्भावस्था (" + preterm_new_born + ") M3-3.3.1.a+3.1.1.b (जीवित जन्म (पुरुष + महिला)से कम या बराबर होना चाहिए (" + total + ")(" + live_birth_male + " + " + live_birth_female + ")" + "\n\n";

                } else {
                    validation_errors_found++;
                    error_message += validation_errors_found + ". M3- 3.1.2 Preterm new borns(<37 wks)" + preterm_new_born + ") should be less than or equal to sum of M3-3.3.1.a+3.1.1.b Live births (Male+ Female)  (" + total + ")(" + live_birth_male + " + " + live_birth_female + ")" + "\n\n";

                }
            }
        }
        //@Rule 20
        if (!"".equals(nb_weighted_birth) && !" ".equals(nb_weighted_birth) && !"".equals(live_birth_male) && !" ".equals(live_birth_male) && !"".equals(live_birth_female) && !" ".equals(live_birth_female)) {
            if (Integer.parseInt(nb_weighted_birth) > (Integer.parseInt(live_birth_male) + Integer.parseInt(live_birth_female))) {
                int total = Integer.parseInt(live_birth_male) + Integer.parseInt(live_birth_female);
                if (lang != null && lang.equals("hi")) {
                    validation_errors_found++;
                    error_message += validation_errors_found + "| M3.B- 3.3.1 नवजात शिशुओं की संख्या जिनका वजन किया गया(" + nb_weighted_birth + ") M3-3.3.1.a+3.1.1.b (जीवित जन्म (पुरुष + महिला) से कम या बराबर होना चाहिए (" + total + ")(" + live_birth_male + " + " + live_birth_female + ")" + "\n\n";

                } else {
                    validation_errors_found++;
                    error_message += validation_errors_found + ". M3.B- 3.3.1 Newborns weighed at birth(" + nb_weighted_birth + ") should be less than or equal to sum of M3-3.3.1.a+3.1.1.b Live births Male+ Female(" + total + ")(" + live_birth_male + " + " + live_birth_female + ")" + "\n\n";

                }

            }
        }

        //@Rule 21
        if (!"".equals(newborns_less_weight) && !" ".equals(newborns_less_weight) && !"".equals(nb_weighted_birth) && !" ".equals(nb_weighted_birth)) {
            if (Integer.parseInt(newborns_less_weight) > (Integer.parseInt(nb_weighted_birth))) {
                if (lang != null && lang.equals("hi")) {
                    validation_errors_found++;
                    error_message += validation_errors_found + "| M3.B -3.3.2 नवजात शिशुओं की संख्या जिनका वजन 2.5 किलो से कम रहा (" + newborns_less_weight + ") M3.B- 3.3.1 नवजात शिशुओं की संख्या जिनका वजन किया गया से कम या बराबर होना चाहिए(" + nb_weighted_birth + ")" + "\n\n";

                } else {
                    validation_errors_found++;
                    error_message += validation_errors_found + ". M3.B- 3.3.2 Newborns having weight less than 2.5 kg(" + newborns_less_weight + ") should be less than or equal to M3.B- 3.3.1 Newborns weighed at birth (" + nb_weighted_birth + ")" + "\n\n";

                }

            }
        }

        //@Rule 22
        if (!"".equals(newborns_breast_fed) && !" ".equals(newborns_breast_fed) && !"".equals(live_birth_male) && !" ".equals(live_birth_male) && !"".equals(live_birth_female) && !" ".equals(live_birth_female)) {
            if (Integer.parseInt(newborns_breast_fed) > (Integer.parseInt(live_birth_male) + Integer.parseInt(live_birth_female))) {
                int total = Integer.parseInt(live_birth_male) + Integer.parseInt(live_birth_female);
                if (lang != null && lang.equals("hi")) {
                    validation_errors_found++;
                    error_message += validation_errors_found + "| M3.B 3.3.3 नवजात शिशुओं की संख्या जिन्हें जन्म के 1 घंटे के भीतर स्तनपान कराया गया (" + newborns_breast_fed + ")M3-3.3.1.a+3.1.1.b (जीवित जन्म (पुरुष + महिला)से कम या बराबर होना चाहिए (" + total + ")(" + live_birth_male + " + " + live_birth_female + ")" + "\n\n";

                } else {
                    validation_errors_found++;
                    error_message += validation_errors_found + ". M3.B- 3.3.3 Newborns breast fed within 1 hour of birth(" + newborns_breast_fed + ") should be less than or equal to sum of M3-3.3.1.a+3.1.1.b  Live births Male+ Female(" + total + ")(" + live_birth_male + " + " + live_birth_female + ")" + "\n\n";

                }

            }
        }

        //@Rule 23
        if (!"".equals(pnc_48) && !" ".equals(pnc_48) && !"".equals(sba) && !" ".equals(sba) && !"".equals(non_sba) && !" ".equals(non_sba)) {
            if (Integer.parseInt(pnc_48) > (Integer.parseInt(sba) + Integer.parseInt(non_sba))) {
                int total = Integer.parseInt(sba) + Integer.parseInt(non_sba);
                if (lang != null && lang.equals("hi")) {
                    validation_errors_found++;
                    error_message += validation_errors_found + "| M4- 4.1 घरेलू प्रसव के 48 घंटों के भीतर प्रथम प्रसव पश्चात चेकअप प्राप्त करने वाली महिलाओं की संख्या (" + pnc_48 + ") M2.A- 2.1.1.a  जो एस॰बी॰ए॰ प्रशिक्षित (डॉक्टर / नर्स / एएनएम) द्वारा कराया गया + 2.1.1.b जो गैर एस॰बी॰ए॰ (प्रशिक्षित बर्थ अटेंडेंट (टीबीए) / रिलेटिव / आदि) द्वारा कराया गया से कम या बराबर होना चाहिए (" + total + ")(" + sba + "+" + non_sba + ")" + "\n\n";

                } else {
                    validation_errors_found++;
                    error_message += validation_errors_found + ". M4- 4.1 Women receiving post partum checkup within 48 hours after delivery(" + pnc_48 + ") should be less than or equal to M2.A-2.1 + 2.1.1.b Home Deliveries Attended by SBA+ Non-SBA (" + total + ")(" + sba + " + " + non_sba + ")" + "\n\n";

                }

            }
        }

        //@Rule 24
        if (!"".equals(IFA_180) && !" ".equals(IFA_180) && !"".equals(sba) && !" ".equals(sba) && !"".equals(non_sba) && !" ".equals(non_sba) && !"".equals(institutional_delivery_conducted) && !" ".equals(institutional_delivery_conducted)) {
            if (Integer.parseInt(IFA_180) > (Integer.parseInt(sba) + Integer.parseInt(non_sba) + Integer.parseInt(institutional_delivery_conducted))) {
                int total = Integer.parseInt(sba) + Integer.parseInt(non_sba) + Integer.parseInt(institutional_delivery_conducted);
                if (lang != null && lang.equals("hi")) {
                    validation_errors_found++;
                    error_message += validation_errors_found + "| M4-4.3 प्रसव पश्चात माताओं की संख्या जिन्हें 180 आईएफए गोली दी गयी" + IFA_180 + ") M2.A- 2.1.1.a जो एस॰बी॰ए॰ प्रशिक्षित (डॉक्टर / नर्स / एएनएम) द्वारा कराया गया + 2.1.1.b जो गैर एस॰बी॰ए॰ (प्रशिक्षित बर्थ अटेंडेंट (टीबीए) / रिलेटिव / आदि) द्वारा कराया गया  + 2.2 संस्थागत प्रसव की संख्यासे कम या बराबर होना चाहिए (" + total + ")(" + sba + "+" + non_sba + "+" + institutional_delivery_conducted + ")" + "\n\n";

                } else {
                    validation_errors_found++;
                    error_message += validation_errors_found + ". M4- 4.3 provided full course of 180 IFA tablets (" + IFA_180 + ") should be less than or equal to M2.A-2.1 Home Deliveries Attended by SBA+ Non-SBA + 2.2 Institutional delivery(including c-section)(" + total + ")(" + sba + " + " + non_sba + "+" + institutional_delivery_conducted + ")" + "\n\n";

                }

            }
        }

        //@Rule 25
        if (!"".equals(cal_360) && !" ".equals(cal_360) && !"".equals(sba) && !" ".equals(sba) && !"".equals(non_sba) && !" ".equals(non_sba) && !"".equals(institutional_delivery_conducted) && !" ".equals(institutional_delivery_conducted)) {
            if (Integer.parseInt(cal_360) > (Integer.parseInt(sba) + Integer.parseInt(non_sba) + Integer.parseInt(institutional_delivery_conducted))) {
                int total = Integer.parseInt(sba) + Integer.parseInt(non_sba) + Integer.parseInt(institutional_delivery_conducted);
                if (lang != null && lang.equals("hi")) {
                    validation_errors_found++;
                    error_message += validation_errors_found + "| M4- 4.4 प्रसव पश्चात माताओं की संख्या जिन्हें 360 कैल्शियम की गोलियां प्रदान की गयी (" + cal_360 + ") M2.A- 2.1.1.a  जो एस॰बी॰ए॰ प्रशिक्षित (डॉक्टर / नर्स / एएनएम) द्वारा कराया गया + 2.1.1.b जो गैर एस॰बी॰ए॰ (प्रशिक्षित बर्थ अटेंडेंट (टीबीए) / रिलेटिव / आदि) द्वारा कराया गया + 2.2 संस्थागत प्रसव की संख्या से कम या बराबर होना चाहिए (" + total + ")(" + sba + "+" + non_sba + " + " + institutional_delivery_conducted + ")" + "\n\n";

                } else {
                    validation_errors_found++;
                    error_message += validation_errors_found + ". M4- 4.4 PW given 360 Calcium carbonate with D3 tablet(" + cal_360 + ") should be less than or equal to M2.A-2.1 Home Deliveries Attended by SBA+ Non-SBA + M2.B-2.2 Institutional delivery(including c-section) (" + total + ")(" + sba + " + " + non_sba + "+" + institutional_delivery_conducted + ")" + "\n\n";

                }

            }
        }

        //@Rule 26
        if (!"".equals(iucd) && !" ".equals(iucd) && !"".equals(sba) && !" ".equals(sba) && !"".equals(non_sba) && !" ".equals(non_sba) && !"".equals(institutional_delivery_conducted) && !" ".equals(institutional_delivery_conducted)) {
            if (Integer.parseInt(iucd) > (Integer.parseInt(sba) + Integer.parseInt(non_sba) + Integer.parseInt(institutional_delivery_conducted))) {
                int total = Integer.parseInt(sba) + Integer.parseInt(non_sba) + Integer.parseInt(institutional_delivery_conducted);
                if (lang != null && lang.equals("hi")) {
                    validation_errors_found++;
                    error_message += validation_errors_found + "| M5- 5.2 प्रसव पश्चात आईयूसीडी सन्निवेशनों की संख्या (प्रसव के 48 घंटों के भीतर) (" + iucd + ")  M2.A- 2.1.1.a  जो एस॰बी॰ए॰ प्रशिक्षित (डॉक्टर / नर्स / एएनएम) द्वारा कराया गया + M2.B-2.1.1.b जो गैर एस॰बी॰ए॰ (प्रशिक्षित बर्थ अटेंडेंट (टीबीए) / रिलेटिव / आदि) द्वारा कराया गया + 2.2 संस्थागत प्रसव की संख्या से कम या बराबर होना चाहिए (" + total + ")(" + sba + "+" + non_sba + " + " + institutional_delivery_conducted + ")" + "\n\n";

                } else {
                    validation_errors_found++;
                    error_message += validation_errors_found + ". M5- 5.2 Post partum IUCD insertions (within 48 hours of delivery)(" + iucd + ") should be less than or equal to M2.A-2.1 Home Deliveries Attended by SBA+ Non-SBA + M2.B-2.2 Institutional delivery(including c-section) (" + total + ")(" + sba + " + " + non_sba + "+" + institutional_delivery_conducted + ")" + "\n\n";

                }

            }
        }

        //Rule 27
        if (!"".equals(vitk1) && !" ".equals(vitk1) && !"".equals(live_birth_male) && !" ".equals(live_birth_male) && !"".equals(live_birth_female) && !" ".equals(live_birth_female)) {
            if (Integer.parseInt(vitk1) > (Integer.parseInt(live_birth_male) + Integer.parseInt(live_birth_female))) {
                int total = Integer.parseInt(live_birth_male) + Integer.parseInt(live_birth_female);
                if (lang != null && lang.equals("hi")) {
                    validation_errors_found++;
                    error_message += validation_errors_found + "| M6- 6.1.1 बाल प्रतिरक्षण - विटामिन K1 (जन्म के समय दी गयी खुराक) (" + vitk1 + ") M3-3.3.1.a+3.1.1.b (जीवित जन्म (पुरुष + महिला) से कम या बराबर होना चाहिए (" + total + ")(" + live_birth_male + " + " + live_birth_female + ")" + "\n\n";

                } else {
                    validation_errors_found++;
                    error_message += validation_errors_found + ". M6- 6.1.1 NB received vitamin K1 Birth dose(" + vitk1 + ") should be less than or equal to Live births (M3-3.3.1.a+3.1.1.b ) (" + total + ")(" + live_birth_male + " + " + live_birth_female + ")" + "\n\n";

                }
            }
        }

        //Rule 28
//        if( !"".equals(opv0)  && !" ".equals(opv0)  && !"".equals(live_birth_male)&& !" ".equals(live_birth_male)  &&  !"".equals(live_birth_female)&& !" ".equals(live_birth_female))
//        {
//            if (Integer.parseInt(opv0)>(Integer.parseInt(live_birth_male)+Integer.parseInt(live_birth_female)))
//            {
//                int total=Integer.parseInt(live_birth_male)+Integer.parseInt(live_birth_female);
//                if (lang!=null&&lang.equals("hi"))
//                {
//                    validation_errors_found++;
//                    error_message += validation_errors_found +"। M6-6.1.9 बाल प्रतिरक्षण - ओपीवी 0 (जन्म के समय दी गयी खुराक) ("+ opv0+") M3-3.3.1.a+3.1.1.b (जीवित जन्म (पुरुष + महिला)  से कम या बराबर होना चाहिए ("+total+")("+live_birth_male+" + "+live_birth_female+  ")"+"\n\n";
//
//                }
//                else
//                {
//                    validation_errors_found++;
//                    error_message += validation_errors_found +" M6- 6.1.9 OPV 0 (birth dose)("+ opv0+") should be less than or equal to Live births M3-3.3.1.a+3.1.1.b ("+total+")("+live_birth_male+" + "+live_birth_female+  ")"+"\n\n";
//
//                }
//            }
//        }

        //Rule 29
        if (!"".equals(hepi_birth) && !" ".equals(hepi_birth) && !"".equals(live_birth_male) && !" ".equals(live_birth_male) && !"".equals(live_birth_female) && !" ".equals(live_birth_female)) {
            if (Integer.parseInt(hepi_birth) > (Integer.parseInt(live_birth_male) + Integer.parseInt(live_birth_female))) {
                int total = Integer.parseInt(live_birth_male) + Integer.parseInt(live_birth_female);
                if (lang != null && lang.equals("hi")) {
                    validation_errors_found++;
                    error_message += validation_errors_found + "। M6- 6.1.13 बाल प्रतिरक्षण - हेपेटाइटिस-बी 0 (जन्म के समय दी गयी खुराक) (" + hepi_birth + ") M3-3.1.1.a+3.1.1.b (जीवित जन्म (पुरुष + महिला)  से कम या बराबर होना चाहिए (" + total + ")(" + live_birth_male + " + " + live_birth_female + ")" + "\n\n";

                } else {
                    validation_errors_found++;
                    error_message += validation_errors_found + ". M6- 6.1.13 Hepatitis birth dose(" + hepi_birth + ") should be less than or equal to Live births M3-3.1.1.a+3.1.1.b (" + total + ")(" + live_birth_male + " + " + live_birth_female + ")" + "\n\n";

                }
            }
        }

        //Rule 31
        if (!"".equals(nvbdcp_malaria) && !" ".equals(nvbdcp_malaria) && !"".equals(nvbdcp_rdt) && !" ".equals(nvbdcp_rdt)) {
            if ((Integer.parseInt(nvbdcp_malaria) > Integer.parseInt(nvbdcp_rdt))) {
                if (lang != null && lang.equals("hi")) {
                    validation_errors_found++;
                    error_message += validation_errors_found + "। M8- 8.1.1.b मलेरिया (आरडीटी) - प्लास्मोडियम वाईवैक्स जाँच सकारात्मक(" + nvbdcp_malaria + ") M8- 8.1.1.a मलेरिया जाँच के लिए की गयी आरडीटी की संख्या से कम या बराबर होना होना चाहिए (" + nvbdcp_rdt + ")" + "\n\n";

                } else {
                    validation_errors_found++;
                    error_message += validation_errors_found + ". M8- 8.1.1.b PV positive malaria(" + nvbdcp_malaria + ") should be less than or equal to M8- 8.1.1.a RDT(malaria) (" + nvbdcp_rdt + ")" + "\n\n";

                }
            }
        }

        //Rule 32
        if (!"".equals(pf_malaria) && !" ".equals(pf_malaria) && !"".equals(nvbdcp_rdt) && !" ".equals(nvbdcp_rdt)) {
            if ((Integer.parseInt(nvbdcp_rdt) < Integer.parseInt(pf_malaria))) {
                if (lang != null && lang.equals("hi")) {
                    validation_errors_found++;
                    error_message += validation_errors_found + "। M8- 8.1.1.c मलेरिया (आरडीटी) - प्लास्मोडियम फाल्सीपरम जाँच सकारात्मक (" + pf_malaria + ") M8- 8.1.1.a मलेरिया जाँच के लिए की गयी आरडीटी की संख्या से कम या बराबर होना होना चाहिए (" + nvbdcp_rdt + ")" + "\n\n";

                } else {
                    validation_errors_found++;
                    error_message += validation_errors_found + ". M8- 8.1.1.c PF positive malaria(" + pf_malaria + ") should be less than or equal to M8- 8.1.1.a RDT(malaria) (" + nvbdcp_rdt + ")" + "\n\n";

                }
            }
        }

        //Rule 33
        if (!"".equals(opd_alo) && !" ".equals(opd_alo) && !"".equals(opd_all) && !" ".equals(opd_all)) {
            if (!"".equals(op_diabaties) && !" ".equals(op_diabaties)) {
                if ((Integer.parseInt(opd_alo) + Integer.parseInt(opd_all)) < Integer.parseInt(op_diabaties)) {
                    int total = Integer.parseInt(opd_alo) + Integer.parseInt(opd_all);
                    if (lang != null && lang.equals("hi")) {
                        validation_errors_found++;
                        error_message += validation_errors_found + "। M9.B- 9.2.1 एलोोपैथिक- आउट पेशेंट उपस्थिति + M9.B-9.2.2 आयुष - आउट पेशेंट उपस्थिति (" + total + ")(" + opd_alo + " + " + opd_all + ") M9- 9.1.1 आउट पेशेंट - मधुमेह से अधिक या बराबर होना चाहिए (" + op_diabaties + ")" + "\n\n";

                    } else {
                        validation_errors_found++;
                        error_message += validation_errors_found + ". M9.B- 9.2.1 Allopathic attendance OPD + M9.B-9.2.2 Ayush OPD All(" + total + ")(" + opd_alo + "+" + opd_all + ") should be greater than or equal to M9-9.1.1 Outpatient Diabetics(" + op_diabaties + ")" + "\n\n";

                    }
                }
            }
            if (!"".equals(op_hypertension) && !" ".equals(op_hypertension)) {
                if ((Integer.parseInt(opd_alo) + Integer.parseInt(opd_all)) < Integer.parseInt(op_hypertension)) {
                    int total = Integer.parseInt(opd_alo) + Integer.parseInt(opd_all);
                    if (lang != null && lang.equals("hi")) {
                        validation_errors_found++;
                        error_message += validation_errors_found + "। M9.B- 9.2.1 एलोोपैथिक- आउट पेशेंट उपस्थिति + M9.B-9.2.2 आयुष - आउट पेशेंट उपस्थिति ( (" + total + ")(" + opd_alo + "+" + opd_all + ") M9- 9.1.2 आउट पेशेंट - उच्च रक्तचाप से अधिक या बराबर होना चाहिए(" + op_hypertension + ")" + "\n\n";

                    } else {
                        validation_errors_found++;
                        error_message += validation_errors_found + ". M9.B- 9.2.1 Allopathic attendance OPD + M9.B-9.2.2 Ayush OPD All(" + total + ")(" + opd_alo + "+" + opd_all + ") should be greater than or equal to M9-9.1.2 Outpatient Hypertension(" + op_hypertension + ")" + "\n\n";

                    }
                }
            }
            if (!"".equals(op_stroke) && !" ".equals(op_stroke)) {
                if ((Integer.parseInt(opd_alo) + Integer.parseInt(opd_all)) < Integer.parseInt(op_stroke)) {
                    int total = Integer.parseInt(opd_alo) + Integer.parseInt(opd_all);
                    if (lang != null && lang.equals("hi")) {
                        validation_errors_found++;
                        error_message += validation_errors_found + "। M9.B- 9.2.1 एलोोपैथिक- आउट पेशेंट उपस्थिति + M9.B-9.2.2 आयुष - आउट पेशेंट उपस्थिति ( (" + total + ")(" + opd_alo + "+" + opd_all + ") M9- 9.1.3 आउट पेशेंट - स्ट्रोक (पक्षाघात) से अधिक या बराबर होना चाहिए(" + op_stroke + ")" + "\n\n";

                    } else {
                        validation_errors_found++;
                        error_message += validation_errors_found + ". M9.B- 9.2.1 Allopathic attendance OPD + M9.B-9.2.2 Ayush OPD All(" + total + ")(" + opd_alo + "+" + opd_all + ") should be greater than or equal to M9-9.1.3 Outpatient Stroke(" + op_stroke + ")" + "\n\n";

                    }
                }
            }
            if (!"".equals(op_acute_heart_disease) && !" ".equals(op_acute_heart_disease)) {
                if ((Integer.parseInt(opd_alo) + Integer.parseInt(opd_all)) < Integer.parseInt(op_acute_heart_disease)) {
                    int total = Integer.parseInt(opd_alo) + Integer.parseInt(opd_all);
                    if (lang != null && lang.equals("hi")) {
                        validation_errors_found++;
                        error_message += validation_errors_found + "। M9.B- 9.2.1 एलोोपैथिक- आउट पेशेंट उपस्थिति + M9.B-9.2.2 आयुष - आउट पेशेंट उपस्थिति ( (" + total + ")(" + opd_alo + "+" + opd_all + ") M9- 9.1.4 आउट पेशेंट - तीव्र हृदय रोग से अधिक या बराबर होना चाहिए(" + op_acute_heart_disease + ")" + "\n\n";

                    } else {
                        validation_errors_found++;
                        error_message += validation_errors_found + ". M9.B- 9.2.1 Allopathic attendance OPD + M9.B-9.2.2 Ayush OPD All(" + total + ")(" + opd_alo + "+" + opd_all + ") should be greater than or equal to M9-9.1.4 Outpatient heart_disease(" + op_acute_heart_disease + ")" + "\n\n";

                    }
                }
            }
            if (!"".equals(op_Mental_illness) && !" ".equals(op_Mental_illness)) {
                if ((Integer.parseInt(opd_alo) + Integer.parseInt(opd_all)) < Integer.parseInt(op_Mental_illness)) {
                    int total = Integer.parseInt(opd_alo) + Integer.parseInt(opd_all);
                    if (lang != null && lang.equals("hi")) {
                        validation_errors_found++;
                        error_message += validation_errors_found + "। M9.B- 9.2.1 एलोोपैथिक- आउट पेशेंट उपस्थिति + M9.B-9.2.2 आयुष - आउट पेशेंट उपस्थिति ( (" + total + ")(" + opd_alo + "+" + opd_all + ") M9- 9.1.5 आउट पेशेंट - मानसिक बीमारी से अधिक या बराबर होना चाहिए(" + op_Mental_illness + ")" + "\n\n";

                    } else {
                        validation_errors_found++;
                        error_message += validation_errors_found + ". M9.B- 9.2.1 Allopathic attendance OPD + M9.B-9.2.2 Ayush OPD All(" + total + ")(" + opd_alo + "+" + opd_all + ") should be greater than or equal to M9-9.1.5 Outpatient Mental_illness(" + op_Mental_illness + ")" + "\n\n";

                    }
                }
            }
            if (!"".equals(op_Epilepsy) && !" ".equals(op_Epilepsy)) {
                if ((Integer.parseInt(opd_alo) + Integer.parseInt(opd_all)) < Integer.parseInt(op_Epilepsy)) {
                    int total = Integer.parseInt(opd_alo) + Integer.parseInt(opd_all);
                    if (lang != null && lang.equals("hi")) {
                        validation_errors_found++;
                        error_message += validation_errors_found + "। M9.B- 9.2.1 एलोोपैथिक- आउट पेशेंट उपस्थिति + M9.B-9.2.2 आयुष - आउट पेशेंट उपस्थिति ( (" + total + ")(" + opd_alo + "+" + opd_all + ") M9- 9.1.6 आउट पेशेंट - मिर्गी (Epilepsy) से अधिक या बराबर होना चाहिए(" + op_Epilepsy + ")" + "\n\n";

                    } else {
                        validation_errors_found++;
                        error_message += validation_errors_found + ". M9.B- 9.2.1 Allopathic attendance OPD + M9.B-9.2.2 Ayush OPD All(" + total + ")(" + opd_alo + "+" + opd_all + ") should be greater than or equal to M9-9.1.6 Outpatient Epilepsy(" + op_Epilepsy + ")" + "\n\n";

                    }
                }
            }
            if (!"".equals(op_Opthalamic) && !" ".equals(op_Opthalamic)) {
                if ((Integer.parseInt(opd_alo) + Integer.parseInt(opd_all)) < Integer.parseInt(op_Opthalamic)) {
                    int total = Integer.parseInt(opd_alo) + Integer.parseInt(opd_all);
                    if (lang != null && lang.equals("hi")) {
                        validation_errors_found++;
                        error_message += validation_errors_found + "। M9.B- 9.2.1 एलोोपैथिक- आउट पेशेंट उपस्थिति + M9.B-9.2.2 आयुष - आउट पेशेंट उपस्थिति ( (" + total + ")(" + opd_alo + "+" + opd_all + ") M9- 9.1.7 आउट पेशेंट - नेत्र संबंधित से अधिक या बराबर होना चाहिए(" + op_Opthalamic + ")" + "\n\n";

                    } else {
                        validation_errors_found++;
                        error_message += validation_errors_found + ". M9.B- 9.2.1 Allopathic attendance OPD + M9.B-9.2.2 Ayush OPD All(" + total + ")(" + opd_alo + "+" + opd_all + ") should be greater than or equal to M9-9.1.7  Outpatient Opthalamic(" + op_Opthalamic + ")" + "\n\n";

                    }
                }
            }
            if (!"".equals(op_dental) && !" ".equals(op_dental)) {
                if ((Integer.parseInt(opd_alo) + Integer.parseInt(opd_all)) < Integer.parseInt(op_dental)) {
                    int total = Integer.parseInt(opd_alo) + Integer.parseInt(opd_all);
                    if (lang != null && lang.equals("hi")) {
                        validation_errors_found++;
                        error_message += validation_errors_found + "। M9.B- 9.2.1 एलोोपैथिक- आउट पेशेंट उपस्थिति + M9.B-9.2.2 आयुष - आउट पेशेंट उपस्थिति ( (" + total + ")(" + opd_alo + "+" + opd_all + ") M9- 9.1.8 आउट पेशेंट - दन्त संबन्धित (Dental) से अधिक या बराबर होना चाहिए(" + op_dental + ")" + "\n\n";

                    } else {
                        validation_errors_found++;
                        error_message += validation_errors_found + ". M9.B- 9.2.1 Allopathic attendance OPD + M9.B-9.2.2 Ayush OPD All(" + total + ")(" + opd_alo + "+" + opd_all + ") should be greater than or equal to M9- 9.1.8 Outpatient Dental(" + op_dental + ")" + "\n\n";

                    }
                }
            }

        }

        //Rule 34
        if (!"".equals(pw_visited_anc_checkup) && !" ".equals(pw_visited_anc_checkup)) {
            if (!"".equals(pwa_hb) && !" ".equals(pwa_hb)) {
                if ((Integer.parseInt(pwa_hb)) > Integer.parseInt(pw_visited_anc_checkup)) {
                    if (lang != null && lang.equals("hi")) {
                        validation_errors_found++;
                        error_message += validation_errors_found + "। M1.A- U1.1.1 हीमोग्लोबिन (Hb)(" + pwa_hb + ") M1-U1.1 गर्भवती महिलाओं की कुल संख्या जो एएनसी जांच (ANC check up) के लिए स्वास्थ्य केंद्र / वी.एच.एन.डी. पर दौरा किया हो से कम या बराबर होना चाहिए (" + pw_visited_anc_checkup + ")" + "\n\n";

                    } else {
                        validation_errors_found++;
                        error_message += validation_errors_found + ". M1.A- U1.1.1 Pregnant women examined HB(" + pwa_hb + ") should be less than or equal to M1- U1.1 Pregnant women visited for ANC check up (" + pw_visited_anc_checkup + ")" + "\n\n";

                    }
                }
            }
            if (!"".equals(pwa_bp) && !" ".equals(pwa_bp)) {
                if ((Integer.parseInt(pwa_bp)) > Integer.parseInt(pw_visited_anc_checkup)) {
                    if (lang != null && lang.equals("hi")) {
                        validation_errors_found++;
                        error_message += validation_errors_found + "। M1.A-U1.1.2 रक्त चाप(" + pwa_bp + ") M1-U1.1 गर्भवती महिलाओं की कुल संख्या जो एएनसी जांच (ANC check up) के लिए स्वास्थ्य केंद्र / वी.एच.एन.डी. पर दौरा किया हो से कम या बराबर होना चाहिए (" + pw_visited_anc_checkup + ")" + "\n\n";

                    } else {
                        validation_errors_found++;
                        error_message += validation_errors_found + ". M1.A- U1.1.2 Pregnant women examined BP(" + pwa_bp + ") should be less than or equal to M1- U1.1 Pregnant women visited for ANC check up\t(" + pw_visited_anc_checkup + ")" + "\n\n";

                    }
                }
            }

            if (!"".equals(pwa_urine_albumin) && !" ".equals(pwa_urine_albumin)) {
                if ((Integer.parseInt(pwa_urine_albumin)) > Integer.parseInt(pw_visited_anc_checkup)) {
                    if (lang != null && lang.equals("hi")) {
                        validation_errors_found++;
                        error_message += validation_errors_found + "। M1.A- U1.1.3 यूरिन अल्बूमिन (" + pwa_urine_albumin + ") M1-U1.1 गर्भवती महिलाओं की कुल संख्या जो एएनसी जांच (ANC check up) के लिए स्वास्थ्य केंद्र / वी.एच.एन.डी. पर दौरा किया हो से कम या बराबर होना चाहिए (" + pw_visited_anc_checkup + ")" + "\n\n";

                    } else {
                        validation_errors_found++;
                        error_message += validation_errors_found + ". M1.A- U1.1.3  Pregnant women examined urine_albumin(" + pwa_urine_albumin + ") should be less than or equal to M1- U1.1 Pregnant women visited for ANC check up\t(" + pw_visited_anc_checkup + ")" + "\n\n";

                    }
                }
            }

            if (!"".equals(pwa_abdominal_check) && !" ".equals(pwa_abdominal_check)) {
                if ((Integer.parseInt(pwa_abdominal_check)) > Integer.parseInt(pw_visited_anc_checkup)) {
                    if (lang != null && lang.equals("hi")) {
                        validation_errors_found++;
                        error_message += validation_errors_found + "। M1.A- U1.1.4 पेट की जांच(" + pwa_abdominal_check + ") M1-U1.1 गर्भवती महिलाओं की कुल संख्या जो एएनसी जांच (ANC check up) के लिए स्वास्थ्य केंद्र / वी.एच.एन.डी. पर दौरा किया हो से कम या बराबर होना चाहिए (" + pw_visited_anc_checkup + ")" + "\n\n";

                    } else {
                        validation_errors_found++;
                        error_message += validation_errors_found + ". M1.A- U1.1.4 Pregnant women examined abdominal_check(" + pwa_abdominal_check + ") should be less than or equal to M1- U1.1 Pregnant women visited for ANC check up\t(" + pw_visited_anc_checkup + ")" + "\n\n";

                    }
                }
            }

            if (!"".equals(pwa_weight) && !" ".equals(pwa_weight)) {
                if ((Integer.parseInt(pwa_weight)) > Integer.parseInt(pw_visited_anc_checkup)) {
                    if (lang != null && lang.equals("hi")) {
                        validation_errors_found++;
                        error_message += validation_errors_found + "। M1.A- U1.1.5 वजन (" + pwa_weight + ")  M1-U1.1 गर्भवती महिलाओं की कुल संख्या जो एएनसी जांच (ANC check up) के लिए स्वास्थ्य केंद्र / वी.एच.एन.डी. पर दौरा किया हो से कम या बराबर होना चाहिए (" + pw_visited_anc_checkup + ")" + "\n\n";

                    } else {
                        validation_errors_found++;
                        error_message += validation_errors_found + ". M1.A-U1.1.5 Pregnant women examined Weight(" + pwa_weight + ") should be less than or equal to M1- U1.1 Pregnant women visited for ANC check up\t(" + pw_visited_anc_checkup + ")" + "\n\n";

                    }
                }
            }

            if (!"".equals(anc_registrations) && !" ".equals(anc_registrations)) {
                if ((Integer.parseInt(anc_registrations)) >= Integer.parseInt(pw_visited_anc_checkup)) {
                    if (lang != null && lang.equals("hi")) {
                        validation_errors_found++;
                        error_message += validation_errors_found + "। M1.A- 1.1 एएनसी के लिए पंजीकृत गर्भवती महिलाओं की कुल संख्या(" + anc_registrations + ") M1-U1.1 गर्भवती महिलाओं की कुल संख्या जो एएनसी जांच (ANC check up) के लिए स्वास्थ्य केंद्र / वी.एच.एन.डी. पर दौरा किया हो से कम  होना चाहिए (" + pw_visited_anc_checkup + ")" + "\n\n";

                    } else {
                        validation_errors_found++;
                        error_message += validation_errors_found + ". M1.A- 1.1 ANC registration(" + anc_registrations + ") should be less than M1- U1.1 Pregnant women visited for ANC check up\t(" + pw_visited_anc_checkup + ")" + "\n\n";

                    }
                }
            }

        }

        //Rule 47
        if (!"".equals(NB_ASHA_Female_1800) && !" ".equals(NB_ASHA_Female_1800) && !"".equals(NB_ASHA_Female_2500) && !" ".equals(NB_ASHA_Female_2500)) {
            if ((Integer.parseInt(NB_ASHA_Female_1800) > Integer.parseInt(NB_ASHA_Female_2500))) {
                if (lang != null && lang.equals("hi")) {
                    validation_errors_found++;
                    error_message += validation_errors_found + "| M4.A- U3.10.2 नवजात फीमेल  (Female) की संख्या जिनका वजन 1800 ग्राम से कम हो (" + NB_ASHA_Female_1800 + ") M4.A-U3.10.3 नवजात फीमेल (Female) की संख्या जिनका वजन 2500 ग्राम से कम हो से कम या बराबर होना चाहिए (" + NB_ASHA_Female_2500 + ")" + "\n\n";

                } else {
                    validation_errors_found++;
                    error_message += validation_errors_found + ". M4.A-U3.10" +
                            ".2 NB identified by ASHA - Female Weight <1800" + NB_ASHA_Female_1800 + ") should be less than or equal to M4.A-U3.10.3 NB identified by ASHA - Female Weight <2500 (" + NB_ASHA_Female_2500 + ")" + "\n\n";

                }
            }
        }


        //Rule 48
        if (!"".equals(NB_ASHA_male_1800) && !" ".equals(NB_ASHA_male_1800) && !"".equals(NB_ASHA_male_2500) && !" ".equals(NB_ASHA_male_2500)) {
            if ((Integer.parseInt(NB_ASHA_male_1800) > Integer.parseInt(NB_ASHA_male_2500))) {
                if (lang != null && lang.equals("hi")) {
                    validation_errors_found++;
                    error_message += validation_errors_found + "| M4.A-U3.9.2 नवजात मेल (Male) की संख्या जिनका वजन 1800 ग्राम से कम हो  (" + NB_ASHA_male_1800 + ") M4.A- U3.9.3 नवजात मेल (Male) की संख्या जिनका वजन 2500 ग्राम से कम हो से कम या बराबर होना चाहिए (" + NB_ASHA_male_2500 + ")" + "\n\n";
                } else {
                    validation_errors_found++;
                    error_message += validation_errors_found + ". M4.A- U3.9.2 NB identified by ASHA - Male Weight <1800" + NB_ASHA_male_1800 + ") should be less than or equal to M4.A-U3.9.3  NB identified by ASHA - Male Weight <2500 (" + NB_ASHA_male_2500 + ")" + "\n\n";

                }
            }
        }


        //Rule:Out of the total ANC registered, number registered within 1st trimester (within 12 weeks) <= Total number of pregnant women registered for ANC
        if (!"".equals(anc_1st_tri) && !" ".equals(anc_1st_tri) && !"".equals(anc_registrations) && !" ".equals(anc_registrations)) {
            if (Integer.parseInt(anc_1st_tri) > Integer.parseInt(anc_registrations)) {
                if (lang != null && lang.equals("hi")) {
                    validation_errors_found++;
                    error_message += validation_errors_found + "। M1.A-1.1.1 एएनसी के लिए पंजीकृत कुल संख्या मे से प्रथम तिमाही के भीतर पंजीकृत गर्भवती महिलाओं की संख्या (12 सप्ताह के भीतर)(" + anc_1st_tri + ") M1.A-1.1 एएनसी के लिए पंजीकृत गर्भवती महिलाओं की कुल संख्या से कम या बराबर होना चाहिए (" + anc_registrations + ")" + "\n\n";

                } else {
                    validation_errors_found++;
                    error_message += validation_errors_found + ". M1.A-1.1.1 ANC registration within 1st trimester (" + anc_1st_tri + ") should be less than or equal to M1.A-1.1 ANC registration  (" + anc_registrations + ")" + "\n\n";

                }

            }
        }

        //Rule:Out of the total number of Hb tests done, Number having Hb < 7 mg <= Number of Hb tests conducted
        if (!"".equals(hb_7) && !" ".equals(hb_7) && !"".equals(hb_conducted) && !" ".equals(hb_conducted)) {
            if (Integer.parseInt(hb_7) > Integer.parseInt(hb_conducted)) {
                if (lang != null && lang.equals("hi")) {
                    validation_errors_found++;
                    error_message += validation_errors_found + "। M10-10.1.2 एचबी जाँचों की कुल संख्या में से, 7 मिलीग्राम से कम वाले एचबी जाँचों की संख्या(" + hb_7 + ") M10.A-10.1.1 किए गए एचबी जाँचों की संख्यासे कम या उसके बराबर होना चाहिए(" + hb_conducted + ")" + "\n\n";

                } else {
                    validation_errors_found++;
                    error_message += validation_errors_found + ". M10- 10.1.2 Number having Hb < 7 mg (" + hb_7 + ") should be less than or equal to M10.A-10.1.1 Hb tests conducted  (" + hb_conducted + ")" + "\n\n";

                }

            }
        }

        if (validation_errors_found > 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(error_message);
            builder.setTitle(R.string.title_validation);
            builder.setCancelable(false);
            builder.setNegativeButton(R.string.dialog_validation,
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            error_message = "";
                            validation_errors_found = 0;
                            dialog.dismiss();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
        return true;
    }

    private void getLatestValues() {
        // this should be one operation (instead of two)
        showProgressBar();

        DatasetInfoHolder info = getIntent().getExtras()
                .getParcelable(DatasetInfoHolder.TAG);

        Intent intent = new Intent(this, WorkService.class);
        intent.putExtra(WorkService.METHOD,
                WorkService.METHOD_DOWNLOAD_LATEST_DATASET_VALUES);
        intent.putExtra(DatasetInfoHolder.TAG, info);
        startService(intent);
    }

    private final BroadcastReceiver RECEIVER = new BroadcastReceiver() {

        @Override
        public void onReceive(Context cxt, Intent intent) {
            hideProgressBar();

            int code = intent.getExtras().getInt(Response.CODE);
            int parsingStatusCode = intent.getExtras().getInt(JsonHandler.PARSING_STATUS_CODE);

            if (HTTPClient.isError(code) || parsingStatusCode != JsonHandler.PARSING_OK_CODE) {
                // load form from disk
                getSupportLoaderManager().restartLoader(LOADER_FORM_ID, null,
                        DataEntryActivity.this).forceLoad();
                return;
            }

            if (intent.getExtras().containsKey(Response.BODY)) {
                Form form = intent.getExtras().getParcelable(Response.BODY);
                currentForm = form;

                if (form != null) {
                    loadGroupsIntoAdapters(form.getGroups(), currentForm);
                }
            }
        }
    };

    private static class DataLoader extends AsyncTaskLoader<Form> {
        private final DatasetInfoHolder infoHolder;

        public DataLoader(Context context, DatasetInfoHolder infoHolder) {
            super(context);
            this.infoHolder = infoHolder;
        }

        @Override
        public Form loadInBackground() {
            if (infoHolder.getFormId() != null && TextFileUtils.doesFileExist(
                    getContext(), TextFileUtils.Directory.DATASETS, infoHolder.getFormId())) {
                Form form = loadForm();

                // try to fit values
                // from storage into form
                loadValuesIntoForm(form);

                return form;
            }
            return null;
        }

        private Form loadForm() {
            String jForm = TextFileUtils.readTextFile(
                    getContext(), TextFileUtils.Directory.DATASETS, infoHolder.getFormId());
            try {
                JsonObject jsonForm = JsonHandler.buildJsonObject(jForm);
                return JsonHandler.fromJson(jsonForm, Form.class);
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (ParsingException e) {
                e.printStackTrace();
            }

            return null;
        }

        private void loadValuesIntoForm(Form form) {
            if (form == null || form.getGroups() == null || form.getGroups().isEmpty()) {
                return;
            }

            String reportKey = DatasetInfoHolder.buildKey(infoHolder);
            if (isEmpty(reportKey)) {
                return;
            }

            String report = loadReport(reportKey);
            if (isEmpty(report)) {
                return;
            }

            Map<String, String> fieldMap = new HashMap<>();

            try {
                JsonObject jsonReport = JsonHandler.buildJsonObject(report);
                JsonArray jsonElements = jsonReport.getAsJsonArray(Constants.DATA_VALUES);

                fieldMap = buildFieldMap(jsonElements);
            } catch (ParsingException e) {
                e.printStackTrace();
            }

            if (!fieldMap.keySet().isEmpty()) {
                // fill form with values

                for (Group group : form.getGroups()) {
                    if (group.getFields() == null || group.getFields().isEmpty()) {
                        continue;
                    }

                    for (Field field : group.getFields()) {
                        String key = buildFieldKey(field.getDataElement(),
                                field.getCategoryOptionCombo());

                        String value = fieldMap.get(key);
                        if (!isEmpty(value)) {
                            field.setValue(value);
                        }
                    }
                }
            }
        }

        private String loadReport(String reportKey) {
            if (isEmpty(reportKey)) {
                return null;
            }

            //@Sou 26-07 load from new offline report
            if (TextFileUtils.doesFileExist(
                    getContext(), TextFileUtils.Directory.OFFLINE_DATASETS_, reportKey)) {
                String report = TextFileUtils.readTextFile(
                        getContext(), TextFileUtils.Directory.OFFLINE_DATASETS_, reportKey);

                if (!isEmpty(report)) {
                    return report;
                }
            }

            return null;
        }

        private Map<String, String> buildFieldMap(JsonArray jsonFields) {
            Map<String, String> fieldMap = new HashMap<>();
            if (jsonFields == null) {
                return fieldMap;
            }

            for (JsonElement jsonElement : jsonFields) {
                if (jsonElement instanceof JsonObject) {
                    JsonElement jsonDataElement = (jsonElement.getAsJsonObject())
                            .get(Field.DATA_ELEMENT);
                    JsonElement jsonCategoryCombination = (jsonElement.getAsJsonObject())
                            .get(Field.CATEGORY_OPTION_COMBO);
                    JsonElement jsonValue = (jsonElement.getAsJsonObject())
                            .get(Field.VALUE);

                    String fieldKey = buildFieldKey(jsonDataElement.getAsString(),
                            jsonCategoryCombination.getAsString());
                    String value = jsonValue != null ? jsonValue.getAsString() : "";

                    fieldMap.put(fieldKey, value);
                }
            }

            return fieldMap;
        }

        private String buildFieldKey(String dataElement, String categoryOptionCombination) {
            if (!isEmpty(dataElement) && !isEmpty(categoryOptionCombination)) {
                return String.format(Locale.getDefault(), "%s.%s",
                        dataElement, categoryOptionCombination);
            }

            return null;
        }
    }


    @Override
    public void onBackPressed() {
        if (anyFieldEdited()) {
            showAlertDialogExit();
        } else {
            super.onBackPressed();
        }
    }

    private boolean anyFieldEdited() {
        ArrayList<Group> groups = new ArrayList<>();
        if (adapters == null) {
            return false;
        }
        for (FieldAdapter adapter : adapters) {
            groups.add(adapter.getGroup());
        }
        for (Group group : groups) {
            for (Field field : group.getFields()) {
                if (field.isEdited()) {
                    return true;
                }
            }
        }
        return false;
    }


    private void showAlertDialogExit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.dialog_exit_survey_title);
        builder.setMessage(R.string.dialog_exit_survey_message);

        builder.setPositiveButton(R.string.dialog_exit_survey_yes,
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
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

    private void showToast(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_LONG).show();
    }

    public static void runInHandler(final Runnable action) {
        mHandler.post(action);
    }

    @Override
    public void onSwipe(int direction) {
        String str = "";

        switch (direction) {

            case SimpleGestureFilter.SWIPE_RIGHT:
                str = "Swipe Right";
                break;
            case SimpleGestureFilter.SWIPE_LEFT:
                str = "Swipe Left";
                break;
            case SimpleGestureFilter.SWIPE_DOWN:
                str = "Swipe Down";
                break;
            case SimpleGestureFilter.SWIPE_UP:
                str = "Swipe Up";
                break;

        }
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDoubleTap() {

    }
}
