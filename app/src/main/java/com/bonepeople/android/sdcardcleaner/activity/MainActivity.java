package com.bonepeople.android.sdcardcleaner.activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Pair;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.bonepeople.android.sdcardcleaner.Global;
import com.bonepeople.android.sdcardcleaner.R;
import com.bonepeople.android.sdcardcleaner.basic.BaseAppCompatActivity;
import com.bonepeople.android.sdcardcleaner.service.FileManager;

/**
 * APP主界面
 *
 * @author bonepeople
 */
public class MainActivity extends BaseAppCompatActivity implements View.OnClickListener {
    private static final int STATE_READY = 0, STATE_SHOWING = 1, STATE_SHOWN = 2, STATE_QUIT = 3, STATE_LIVING = 4;
    private static final int MSG_CANCEL = 0;
    private CardView cardView_scan, cardView_white, cardView_black, cardView_set;
    private Handler handler = createHandler();
    private int state = STATE_READY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cardView_scan = findViewById(R.id.cardView_scan);
        cardView_white = findViewById(R.id.cardView_white);
        cardView_black = findViewById(R.id.cardView_black);
        cardView_set = findViewById(R.id.cardView_set);
        findViewById(R.id.cardView_home).setOnClickListener(this);

        cardView_scan.setOnClickListener(this);

        Global.init(getApplicationContext());
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (state == STATE_READY) {
            state = STATE_SHOWING;
            startAnimation();
        }
    }

    @Override
    public void onBackPressed() {
        if (state == STATE_SHOWN) {
            state = STATE_QUIT;
            Toast.makeText(this, R.string.toast_quitConfirm, Toast.LENGTH_SHORT).show();
            handler.sendEmptyMessageDelayed(MSG_CANCEL, 2 * 1000);

        } else if (state == STATE_QUIT) {
            state = STATE_LIVING;
            finishApp();
            quitAnimation();
        }
    }

    @Override
    protected void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (msg.what == MSG_CANCEL)
            state = STATE_SHOWN;
    }

    private void startAnimation() {
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getRealSize(size);
        int height = size.y;

        TranslateAnimation anim_scan = new TranslateAnimation(0, 0, height, 0);
        anim_scan.setDuration(500);
        cardView_scan.startAnimation(anim_scan);
        TranslateAnimation anim_white = new TranslateAnimation(0, 0, height, 0);
        anim_white.setDuration(500);
        anim_white.setStartOffset(100);
        cardView_white.startAnimation(anim_white);
        TranslateAnimation anim_black = new TranslateAnimation(0, 0, height, 0);
        anim_black.setDuration(500);
        anim_black.setStartOffset(200);
        cardView_black.startAnimation(anim_black);
        TranslateAnimation anim_set = new TranslateAnimation(0, 0, height, 0);
        anim_set.setDuration(500);
        anim_set.setStartOffset(300);
        cardView_set.startAnimation(anim_set);
        anim_set.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                state = STATE_SHOWN;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void quitAnimation() {
        cardView_scan.startAnimation(createQuitAnimation(cardView_scan, 0));
        cardView_white.startAnimation(createQuitAnimation(cardView_white, 80));
        cardView_black.startAnimation(createQuitAnimation(cardView_black, 160));
        AnimationSet lastAnim = createQuitAnimation(cardView_set, 240);
        cardView_set.startAnimation(lastAnim);
        lastAnim.setAnimationListener(new Animation.AnimationListener() {
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

    private AnimationSet createQuitAnimation(View view, long offSet) {
        AnimationSet animationSet = new AnimationSet(true);

        animationSet.addAnimation(new AlphaAnimation(1, 0));
        animationSet.addAnimation(new ScaleAnimation(1, 2, 1, 2));
        animationSet.addAnimation(new TranslateAnimation(0, -view.getWidth() / 2, 0, -view.getHeight() / 2));

        animationSet.setDuration(150);
        animationSet.setStartOffset(offSet);
        animationSet.setFillAfter(true);
        return animationSet;
    }

    private void finishApp() {
        FileManager.stopScan();
        FileManager.stopClean();
        Global.destroy();
    }

    @Override
    public void onClick(View v) {
        if (state == STATE_SHOWN || state == STATE_QUIT) {
            Pair<View, String> title, body;
            Bundle bundle;
            switch (v.getId()) {
                case R.id.cardView_scan:
                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        title = Pair.create(findViewById(R.id.textView_scan), "transition_title");
                        body = Pair.create((View) cardView_scan, "transition_body");
                        bundle = ActivityOptions.makeSceneTransitionAnimation(this, title, body).toBundle();
                        startActivity(new Intent(this, ScanActivity.class), bundle);
                    } else
                        Toast.makeText(this, R.string.toast_sdcard_error, Toast.LENGTH_SHORT).show();
                    break;
                case R.id.cardView_home:
                    HomeActivity.Companion.open(this);
                    break;
            }
        }
    }
}
