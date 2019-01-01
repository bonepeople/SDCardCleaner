package com.bonepeople.android.sdcardcleaner.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bonepeople.android.sdcardcleaner.BuildConfig;
import com.bonepeople.android.sdcardcleaner.R;
import com.bonepeople.android.sdcardcleaner.widget.TitleBar;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView textView_version;

    public static void open(Context context) {
        context.startActivity(new Intent(context, SettingActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
        initData();
    }

    private void initView() {
        TitleBar titleBar = findViewById(R.id.titleBar);
        titleBar.setTitle(R.string.caption_text_set);
        findViewById(R.id.textView_about).setOnClickListener(this);
        textView_version = findViewById(R.id.textView_version);
    }

    private void initData() {
        textView_version.setText(getString(R.string.state_version, BuildConfig.VERSION_NAME));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textView_about:

                break;
        }
    }
}
