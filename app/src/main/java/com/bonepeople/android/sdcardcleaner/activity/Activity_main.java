package com.bonepeople.android.sdcardcleaner.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;

import com.bonepeople.android.sdcardcleaner.Global;
import com.bonepeople.android.sdcardcleaner.R;
import com.bonepeople.android.sdcardcleaner.basic.Basic_appCompatActivity;

public class Activity_main extends Basic_appCompatActivity implements View.OnClickListener {
    private CardView _card_scan, _card_white, _card_black, _card_set;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        _card_scan = (CardView) findViewById(R.id.cardview_scan);
        _card_white = (CardView) findViewById(R.id.cardview_white);
        _card_black = (CardView) findViewById(R.id.cardview_black);
        _card_set = (CardView) findViewById(R.id.cardview_set);

        _card_scan.setOnClickListener(this);
        _card_white.setOnClickListener(this);
        _card_black.setOnClickListener(this);
        _card_set.setOnClickListener(this);

        Global.init(getApplicationContext());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cardview_scan:
                startActivity(new Intent(getApplicationContext(), Activity_scan.class));
                break;
            case R.id.cardview_white:
                startActivity(new Intent(getApplicationContext(), Activity_list_path.class).putExtra("mode", Activity_list_path.MODE_SAVE));
                break;
            case R.id.cardview_black:
                startActivity(new Intent(getApplicationContext(), Activity_list_path.class).putExtra("mode", Activity_list_path.MODE_CLEAN));
                break;
            case R.id.cardview_set:

                break;
        }
    }
}
