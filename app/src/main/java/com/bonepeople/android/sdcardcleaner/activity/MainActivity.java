package com.bonepeople.android.sdcardcleaner.activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.CardView;
import android.util.Pair;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

import com.bonepeople.android.sdcardcleaner.Global;
import com.bonepeople.android.sdcardcleaner.R;
import com.bonepeople.android.sdcardcleaner.basic.BaseAppCompatActivity;
import com.bonepeople.android.sdcardcleaner.service.FileManager;

public class MainActivity extends BaseAppCompatActivity implements View.OnClickListener {
    private static final int STATE_READY = 0, STATE_SHOWING = 1, STATE_SHOWN = 2, STATE_QUIT = 3, STATE_LIVING = 4;
    private static final int MSG_CANCEL = 0;
    private CardView _card_scan, _card_white, _card_black, _card_set;
    private Handler _handler = createHandler();
    private int _state = STATE_READY;

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
        if (_state == STATE_READY) {
            _state = STATE_SHOWING;
            startAnimation();
        }
    }

    @Override
    public void onBackPressed() {
        if (_state == STATE_SHOWN) {
            _state = STATE_QUIT;
            Toast.makeText(this, R.string.toast_quitConfirm, Toast.LENGTH_SHORT).show();
            _handler.sendEmptyMessageDelayed(MSG_CANCEL, 2 * 1000);

        } else if (_state == STATE_QUIT) {
            _state = STATE_LIVING;
            finishApp();
            quitAnimation();
        }
    }

    @Override
    protected void handleMessage(Message _msg) {
        super.handleMessage(_msg);
        if (_msg.what == MSG_CANCEL)
            _state = STATE_SHOWN;
    }

    private void startAnimation() {
        Point _size = new Point();
        getWindowManager().getDefaultDisplay().getRealSize(_size);
        int _height = _size.y;

        TranslateAnimation _anim_scan = new TranslateAnimation(0, 0, _height, 0);
        _anim_scan.setDuration(500);
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
        _anim_set.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                _state = STATE_SHOWN;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void quitAnimation() {
        _card_scan.startAnimation(createQuitAnimation(_card_scan, 0));
        _card_white.startAnimation(createQuitAnimation(_card_white, 80));
        _card_black.startAnimation(createQuitAnimation(_card_black, 160));
        AnimationSet _lastAnim = createQuitAnimation(_card_set, 240);
        _card_set.startAnimation(_lastAnim);
        _lastAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private AnimationSet createQuitAnimation(View _view, long _offSet) {
        AnimationSet _set = new AnimationSet(true);

        _set.addAnimation(new AlphaAnimation(1, 0));
        _set.addAnimation(new ScaleAnimation(1, 2, 1, 2));
        _set.addAnimation(new TranslateAnimation(0, -_view.getWidth() / 2, 0, -_view.getHeight() / 2));

        _set.setDuration(150);
        _set.setStartOffset(_offSet);
        _set.setFillAfter(true);
        return _set;
    }

    private void finishApp() {
        FileManager.stopScan();
        FileManager.stopClean();
        Global.destroy();
    }

    @Override
    public void onClick(View v) {
        if (_state == STATE_SHOWN || _state == STATE_QUIT) {
            Pair<View, String> _title, _body;
            Bundle _bundle;
            switch (v.getId()) {
                case R.id.cardview_scan:
                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        _title = Pair.create(findViewById(R.id.textView_scan), "transition_title");
                        _body = Pair.create((View) _card_scan, "transition_body");
                        _bundle = ActivityOptions.makeSceneTransitionAnimation(this, _title, _body).toBundle();
                        startActivity(new Intent(getApplicationContext(), ScanActivity.class), _bundle);
                    } else
                        Toast.makeText(this, R.string.toast_sdcard_error, Toast.LENGTH_SHORT).show();
                    break;
                case R.id.cardview_white:
                    _title = Pair.create(findViewById(R.id.textView_white), "transition_title");
                    _body = Pair.create((View) _card_white, "transition_body");
                    _bundle = ActivityOptions.makeSceneTransitionAnimation(this, _title, _body).toBundle();
                    startActivity(new Intent(getApplicationContext(), PathListActivity.class).putExtra("mode", PathListActivity.MODE_SAVE), _bundle);
                    break;
                case R.id.cardview_black:
                    _title = Pair.create(findViewById(R.id.textView_black), "transition_title");
                    _body = Pair.create((View) _card_black, "transition_body");
                    _bundle = ActivityOptions.makeSceneTransitionAnimation(this, _title, _body).toBundle();
                    startActivity(new Intent(getApplicationContext(), PathListActivity.class).putExtra("mode", PathListActivity.MODE_CLEAN), _bundle);
                    break;
                case R.id.cardview_set:
                    Toast.makeText(this, R.string.toast_comingSoon, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
