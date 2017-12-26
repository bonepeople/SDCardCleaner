package com.bonepeople.android.sdcardcleaner.activity;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.animation.TranslateAnimation;

import com.bonepeople.android.sdcardcleaner.Global;
import com.bonepeople.android.sdcardcleaner.R;
import com.bonepeople.android.sdcardcleaner.basic.Basic_appCompatActivity;

public class Activity_main extends Basic_appCompatActivity implements View.OnClickListener {
    private CardView _card_scan, _card_white, _card_black, _card_set;
    private boolean _shown = false;

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
    public void onWindowFocusChanged(boolean hasFocus) {
        if (!_shown)
            startAnimation();
    }

    private void startAnimation() {
        Point _size = new Point();
        getWindowManager().getDefaultDisplay().getRealSize(_size);
        int _height = _size.y;

        TranslateAnimation _anim_scan = new TranslateAnimation(0, 0, _height, 0);
        _anim_scan.setDuration(500);
        _anim_scan.setStartOffset(0);
        _card_scan.startAnimation(_anim_scan);
        TranslateAnimation _anim_white = new TranslateAnimation(0, 0, _height, 0);
        _anim_white.setDuration(500);
        _anim_white.setStartOffset(100);
        _card_white.startAnimation(_anim_white);
        TranslateAnimation _anim_black = new TranslateAnimation(0, 0, _height, 0);
        _anim_black.setDuration(500);
        _anim_black.setStartOffset(200);
        _card_black.startAnimation(_anim_black);
        TranslateAnimation _anim_set = new TranslateAnimation(0, 0, _height, 0);
        _anim_set.setDuration(500);
        _anim_set.setStartOffset(300);
        _card_set.startAnimation(_anim_set);
        _shown = true;
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
