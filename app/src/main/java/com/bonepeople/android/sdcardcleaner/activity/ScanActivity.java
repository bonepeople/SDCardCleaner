package com.bonepeople.android.sdcardcleaner.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Message;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
    private static final int PERMISSION_STORAGE = 1;
    private static final int MSG_REFRESH = 1;
    private static final int REQUEST_FILE = 0;
    private TextView textView_state, textView_time;
    private SDCardPercent percent;
    private Button button_middle, button_left, button_right;
    private BaseHandler handler = createHandler();
    private int state = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        TitleBar titleBar = findViewById(R.id.titleBar);
        textView_time = findViewById(R.id.textView_time);
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
    protected void onRequestPermission(int requestCode, boolean granted) {
        switch (requestCode) {
            case PERMISSION_STORAGE:
                if (granted)
                    startScan();
                else
                    Toast.makeText(this, "没有权限，无法进行后续操作", Toast.LENGTH_SHORT).show();
                break;
        }
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
                //更新时间
                switch (state) {
                    case FileManager.STATE_SCAN_EXECUTING:
                    case FileManager.STATE_SCAN_STOP:
                    case FileManager.STATE_SCAN_FINISH:
                    case FileManager.STATE_CLEAN_EXECUTING:
                    case FileManager.STATE_CLEAN_STOP:
                    case FileManager.STATE_CLEAN_FINISH:
                        textView_time.setText(FileManager.getProgressTimeString());
                        break;
                    default:
                        textView_time.setText("");
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
                textView_state.setText(getString(R.string.state_scan_executing));
                button_middle.setText(R.string.caption_button_stopScan);
                button_middle.setTag(new String[]{ACTION_STOP_SCAN});
                button_middle.setVisibility(Button.VISIBLE);
                button_left.setVisibility(Button.GONE);
                button_right.setVisibility(Button.GONE);
                break;
            case FileManager.STATE_SCAN_STOP:
                textView_state.setText(getString(R.string.state_scan_stopping));
                button_middle.setVisibility(Button.GONE);
                button_left.setVisibility(Button.GONE);
                button_right.setVisibility(Button.GONE);
                break;
            case FileManager.STATE_SCAN_FINISH:
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
                textView_state.setText(getString(R.string.state_clean_executing));
                button_middle.setText(R.string.caption_button_stopClean);
                button_middle.setTag(new String[]{ACTION_STOP_CLEAN});
                button_middle.setVisibility(Button.VISIBLE);
                button_left.setVisibility(Button.GONE);
                button_right.setVisibility(Button.GONE);
                break;
            case FileManager.STATE_CLEAN_STOP:
                textView_state.setText(getString(R.string.state_clean_stopping));
                button_middle.setVisibility(Button.GONE);
                button_left.setVisibility(Button.GONE);
                button_right.setVisibility(Button.GONE);
                break;
            case FileManager.STATE_CLEAN_FINISH:
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
            default://手动删除文件会导致状态值的变化，在手动删除文件后依旧显示扫描结束的提示信息
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

    private void startScan() {
        FileManager.startScan();
        handler.sendEmptyMessageDelayed(MSG_REFRESH, 150);
    }

    @Override
    public void onClick(View v) {
        String[] tags = (String[]) v.getTag();
        switch (tags[0]) {
            case ACTION_START_SCAN:
                if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                    startScan();
                else
                    requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, "需要存储空间的权限才能扫描文件", PERMISSION_STORAGE);
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
                startActivityForResult(new Intent(this, FileListActivity.class), REQUEST_FILE);
                break;
        }
    }
}
