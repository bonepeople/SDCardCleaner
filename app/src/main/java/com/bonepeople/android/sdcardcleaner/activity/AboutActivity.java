package com.bonepeople.android.sdcardcleaner.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bonepeople.android.sdcardcleaner.R;
import com.bonepeople.android.sdcardcleaner.widget.TitleBar;

/**
 * 关于页面
 *
 * @author bonepeople
 */
public class AboutActivity extends AppCompatActivity {

    public static void open(@NonNull Context context) {
        context.startActivity(new Intent(context, AboutActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        initView();
        initData();
    }

    private void initView() {
        TitleBar titleBar = findViewById(R.id.titleBar);
        titleBar.setTitle(R.string.caption_text_about);
    }

    private void initData() {

    }
}
