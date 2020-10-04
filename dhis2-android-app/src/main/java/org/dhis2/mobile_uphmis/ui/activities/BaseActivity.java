package org.dhis2.mobile_uphmis.ui.activities;

import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;

import org.dhis2.mobile_uphmis.NetworkStateReceiver;
import org.dhis2.mobile_uphmis.R;
import org.dhis2.mobile_uphmis.utils.CustomTypefaceSpan;
import org.dhis2.mobile_uphmis.utils.PrefUtils;
import org.dhis2.mobile_uphmis.utils.TypefaceManager;

import java.util.Locale;

public class BaseActivity extends AppCompatActivity {
    private Typeface mCustomTypeFace;
    private NetworkStateReceiver mNetworkStateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCustomTypeFace = getTypeFace();
        String lang = PrefUtils.getLocale(getBaseContext());
        if (lang != null && lang.equals("hi")) {
            Locale locale = new Locale("hi");
            Locale.setDefault(locale);
            Configuration config = getBaseContext().getResources().getConfiguration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());
        }
    }

    @Override
    public void setTitle(CharSequence sequence) {
        if (getSupportActionBar() == null) {
            return;
        }

        if (sequence != null && mCustomTypeFace != null) {
            CustomTypefaceSpan typefaceSpan = new CustomTypefaceSpan(mCustomTypeFace);
            SpannableString title = new SpannableString(sequence);
            title.setSpan(typefaceSpan, 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            getSupportActionBar().setTitle(title);
        } else {
            getSupportActionBar().setTitle(sequence);
        }
    }

    private Typeface getTypeFace() {
        AssetManager manager = getAssets();
        String fontName = getString(R.string.regular_font_name);
        if (manager != null && !TextUtils.isEmpty(fontName)) {
            return TypefaceManager.getTypeface(getAssets(), fontName);
        } else {
            return null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mNetworkStateReceiver = new NetworkStateReceiver();
        registerReceiver(mNetworkStateReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mNetworkStateReceiver != null) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            unregisterReceiver(mNetworkStateReceiver);
        }
    }
}
