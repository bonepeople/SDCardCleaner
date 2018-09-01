package com.bonepeople.android.sdcardcleaner.activity;

import android.content.Intent;
import android.os.Message;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bonepeople.android.sdcardcleaner.R;
import com.bonepeople.android.sdcardcleaner.basic.BaseAppCompatActivity;
import com.bonepeople.android.sdcardcleaner.basic.BaseHandler;
import com.bonepeople.android.sdcardcleaner.service.FileManager;
import com.bonepeople.android.sdcardcleaner.widget.SDCardPercent;
import com.bonepeople.android.sdcardcleaner.widget.TitleBar;

/**
 * 文件扫描界面
 *
 * @author bonepeople
 */
public class ScanActivity extends BaseAppCompatActivity implements View.OnClickListener {
    private static final String ACTION_START_SCAN = "startScan";
    private static final String ACTION_STOP_SCAN = "stopScan";
    private static final String ACTION_START_CLEAN = "startClean";
    private static final String ACTION_STOP_CLEAN = "stopClean";
    private static final String ACTION_VIEW_FILE = "viewFile";
    private static final int MSG_REFRESH = 1;
    private static final int REQUEST_FILE = 0;
    private ProgressBar progressBar;
    private TextView textView_state;
    private SDCardPercent percent;
    private Button button_middle, button_left, button_right;
    private BaseHandler handler = createHandler();
    private int state = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        TitleBar titleBar = findViewById(R.id.titleBar);
        progressBar = findViewById(R.id.progressBar);
        textView_state = findViewById(R.id.textView_state);
        percent = findViewById(R.id.SDCardPercent);
        button_middle = findViewById(R.id.button_stop);
        button_left = findViewById(R.id.button_clean);
        button_right = findViewById(R.id.button_show);

        titleBar.setTitle(R.string.caption_text_mine);
        button_middle.setOnClickListener(this);
        button_left.setOnClickListener(this);
        button_right.setOnClickListener(this);

        handler.sendEmptyMessage(MSG_REFRESH);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_FILE) {
            handler.sendEmptyMessage(MSG_REFRESH);
        }
    }

    @Override
    protected void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case MSG_REFRESH:
                if (state != FileManager.getState()) {
                    state = FileManager.getState();
                    updateState();
                }
                percent.refresh();
                if (state == FileManager.STATE_READY)
                    return;
                if (state == FileManager.STATE_SCAN_FINISH)
                    return;
                if (state == FileManager.STATE_CLEAN_FINISH)
                    return;
                if (state == FileManager.STATE_DELETE_FINISH)
                    return;
                handler.sendEmptyMessageDelayed(MSG_REFRESH, 500);
                break;
        }
    }

    private void updateState() {
        switch (state) {
            case FileManager.STATE_READY:
                textView_state.setText(R.string.state_ready);
                button_middle.setText(R.string.caption_button_startScan);
                button_middle.setTag(new String[]{ACTION_START_SCAN});
                button_middle.setVisibility(Button.VISIBLE);
                button_left.setVisibility(Button.GONE);
                button_right.setVisibility(Button.GONE);
                break;
            case FileManager.STATE_SCAN_EXECUTING:
                progressBar.setVisibility(ProgressBar.VISIBLE);
                textView_state.setText(getString(R.string.state_scan_executing));
                button_middle.setText(R.string.caption_button_stopScan);
                button_middle.setTag(new String[]{ACTION_STOP_SCAN});
                button_middle.setVisibility(Button.VISIBLE);
                button_left.setVisibility(Button.GONE);
                button_right.setVisibility(Button.GONE);
                break;
            case FileManager.STATE_SCAN_STOP:
                progressBar.setVisibility(ProgressBar.VISIBLE);
                textView_state.setText(getString(R.string.state_scan_stopping));
                button_middle.setVisibility(Button.GONE);
                button_left.setVisibility(Button.GONE);
                button_right.setVisibility(Button.GONE);
                break;
            case FileManager.STATE_SCAN_FINISH:
                progressBar.setVisibility(ProgressBar.GONE);
                textView_state.setText(getString(R.string.state_scan_finish));
                button_middle.setText(R.string.caption_button_reScan);
                button_left.setText(R.string.caption_button_startClean);
                button_right.setText(R.string.caption_button_viewFiles);
                button_middle.setTag(new String[]{ACTION_START_SCAN});
                button_left.setTag(new String[]{ACTION_START_CLEAN});
                button_right.setTag(new String[]{ACTION_VIEW_FILE});
                button_middle.setVisibility(Button.VISIBLE);
                button_left.setVisibility(Button.VISIBLE);
                button_right.setVisibility(Button.VISIBLE);
                break;
            case FileManager.STATE_CLEAN_EXECUTING:
                progressBar.setVisibility(ProgressBar.VISIBLE);
                textView_state.setText(getString(R.string.state_clean_executing));
                button_middle.setText(R.string.caption_button_stopClean);
                button_middle.setTag(new String[]{ACTION_STOP_CLEAN});
                button_middle.setVisibility(Button.VISIBLE);
                button_left.setVisibility(Button.GONE);
                button_right.setVisibility(Button.GONE);
                break;
            case FileManager.STATE_CLEAN_STOP:
                progressBar.setVisibility(ProgressBar.VISIBLE);
                textView_state.setText(getString(R.string.state_clean_stopping));
                button_middle.setVisibility(Button.GONE);
                button_left.setVisibility(Button.GONE);
                button_right.setVisibility(Button.GONE);
                break;
            case FileManager.STATE_CLEAN_FINISH:
                progressBar.setVisibility(ProgressBar.GONE);
                textView_state.setText(getString(R.string.state_clean_finish));
                button_middle.setText(R.string.caption_button_reScan);
                button_left.setText(R.string.caption_button_startClean);
                button_right.setText(R.string.caption_button_viewFiles);
                button_middle.setTag(new String[]{ACTION_START_SCAN});
                button_left.setTag(new String[]{ACTION_START_CLEAN});
                button_right.setTag(new String[]{ACTION_VIEW_FILE});
                button_middle.setVisibility(Button.VISIBLE);
                button_left.setVisibility(Button.VISIBLE);
                button_right.setVisibility(Button.VISIBLE);
                break;
            default:
                progressBar.setVisibility(ProgressBar.GONE);
                textView_state.setText(getString(R.string.state_scan_finish));
                button_middle.setText(R.string.caption_button_reScan);
                button_left.setText(R.string.caption_button_startClean);
                button_right.setText(R.string.caption_button_viewFiles);
                button_middle.setTag(new String[]{ACTION_START_SCAN});
                button_left.setTag(new String[]{ACTION_START_CLEAN});
                button_right.setTag(new String[]{ACTION_VIEW_FILE});
                button_middle.setVisibility(Button.VISIBLE);
                button_left.setVisibility(Button.VISIBLE);
                button_right.setVisibility(Button.VISIBLE);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        String[] tags = (String[]) v.getTag();
        switch (tags[0]) {
            case ACTION_START_SCAN:
                FileManager.startScan();
                handler.sendEmptyMessageDelayed(MSG_REFRESH, 150);
                break;
            case ACTION_STOP_SCAN:
                FileManager.stopScan();
                break;
            case ACTION_START_CLEAN:
                FileManager.startClean();
                handler.sendEmptyMessageDelayed(MSG_REFRESH, 150);
                break;
            case ACTION_STOP_CLEAN:
                FileManager.stopClean();
                break;
            case ACTION_VIEW_FILE:
                startActivityForResult(new Intent(getApplicationContext(), FileListActivity.class), REQUEST_FILE);
                break;
        }
    }
}
