package com.liubowang.photoretouch.Main;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewConfiguration;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.lafonapps.common.ad.AdManager;
import com.lafonapps.common.ad.AdSize;
import com.lafonapps.common.ad.adapter.nativead.NativeAdAdapterView;
import com.lafonapps.common.utils.ViewUtil;
import com.liubowang.photoretouch.Base.EIBaseActiviry;
import com.liubowang.photoretouch.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends EIBaseActiviry implements SettingsAdapter.SettingsInterface{
    private static final String TAG = SettingsActivity.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private LinearLayout nativeViewContainer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initUI();
        setOverflowShowingAlways();
    }

    private void initUI(){
        Toolbar myToolbar = (Toolbar) findViewById(R.id.tb_set);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ImageView imageView = (ImageView) findViewById(R.id.iv_top_image_set);
        setStatusBar(imageView);
        setTitle(null);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_settings);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setHasFixedSize(true);
        SettingsAdapter adapter = new SettingsAdapter(this);
        mRecyclerView.setAdapter(adapter);


        LinearLayout native250HContainer = (LinearLayout) findViewById(R.id.adNavi_container_set);
        NativeAdAdapterView nativeAdView250H = AdManager.getSharedAdManager().getNativeAdAdapterViewFor250H(new AdSize(320, 250), this, this);
        ViewUtil.addView(native250HContainer, nativeAdView250H);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemClick(String title) {
        Log.d(TAG,title);
        if (title.equals(getString(R.string.setting_comment)))
        {
            shareAppShop(getPackageName());
        }
        else if (title.equals(getString(R.string.setting_suggections)))
        {
            sendEmail();
        }
    }

    @Override
    public List<String> configItemList() {
        ArrayList<String> list = new ArrayList<>();
        list.add(getString(R.string.setting_suggections));
        list.add(getString(R.string.setting_comment));
        return list;
    }


    private   void shareAppShop(String packageName) {
        try {
//            packageName = getPackageName();
            Uri uri = Uri.parse("market://details?id="+ packageName);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            if (intent.resolveActivity(getPackageManager()) != null)
//            {
            startActivity(intent);
//            }

        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.setting_no_app_market), Toast.LENGTH_SHORT).show();
        }
    }

    private void sendEmail(){
        String appName = getString(R.string.app_name);
        String version = getAppVersionNameAndCode(this);
        String sdk = getSdkVersion();
        String model = getPhoneModel();
        String country = getCountry(this);
        String info = appName + "-(V" + version + ")," + country + "," + model + ",Android " + sdk;
        Intent email = new Intent(Intent.ACTION_SEND);
        email.setType("message/rfc822");
        email.putExtra(Intent.EXTRA_EMAIL, new String[] {"np2016.ant@gmail.com"});
        email.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.setting_suggections) + ":" + info);
        email.putExtra(Intent.EXTRA_TEXT, "");
        if (email.resolveActivity(getPackageManager()) != null) {
            startActivity(Intent.createChooser(email,getString(R.string.setting_send_email)));
        }
    }

    @Override
    protected boolean shouldShowBannerView() {
        return false;
    }

    @Override
    protected boolean shouldShowNativeView() {
        return true;
    }

}
