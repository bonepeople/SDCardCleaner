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

public class ScanActivity extends BaseAppCompatActivity implements View.OnClickListener {
    private static final String ACTION_START_SCAN = "startScan";
    private static final String ACTION_STOP_SCAN = "stopScan";
    private static final String ACTION_START_CLEAN = "startClean";
    private static final String ACTION_STOP_CLEAN = "stopClean";
    private static final String ACTION_VIEW_FILE = "viewFile";
    private static final int MSG_REFRESH = 1;
    private static final int REQUEST_FILE = 0;
    private ProgressBar _progressBar;
    private TextView _text_state;
    private SDCardPercent _percent;
    private Button _button_middle, _button_left, _button_right;
    private BaseHandler _handler = createHandler();
    private int _state = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        TitleBar _titleBar = (TitleBar) findViewById(R.id.titleBar);
        _progressBar = (ProgressBar) findViewById(R.id.progressBar);
        _text_state = (TextView) findViewById(R.id.textView_state);
        _percent = (SDCardPercent) findViewById(R.id.SDCardPercent);
        _button_middle = (Button) findViewById(R.id.button_stop);
        _button_left = (Button) findViewById(R.id.button_clean);
        _button_right = (Button) findViewById(R.id.button_show);

        _titleBar.setTitle(R.string.caption_text_mine);
        _button_middle.setOnClickListener(this);
        _button_left.setOnClickListener(this);
        _button_right.setOnClickListener(this);

        _handler.sendEmptyMessage(MSG_REFRESH);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_FILE) {
            _handler.sendEmptyMessage(MSG_REFRESH);
        }
    }

    @Override
    protected void handleMessage(Message _msg) {
        super.handleMessage(_msg);
        switch (_msg.what) {
            case MSG_REFRESH:
                if (_state != FileManager.get_state()) {
                    _state = FileManager.get_state();
                    updateState();
                }
                _percent.refresh();
                if (_state == FileManager.STATE_READY)
                    return;
                if (_state == FileManager.STATE_SCAN_FINISH)
                    return;
                if (_state == FileManager.STATE_CLEAN_FINISH)
                    return;
                if (_state == FileManager.STATE_DELETE_FINISH)
                    return;
                _handler.sendEmptyMessageDelayed(MSG_REFRESH, 500);
                break;
        }
    }

    private void updateState() {
        switch (_state) {
            case FileManager.STATE_READY:
                _text_state.setText(R.string.state_ready);
                _button_middle.setText(R.string.caption_button_startScan);
                _button_middle.setTag(new String[]{ACTION_START_SCAN});
                _button_middle.setVisibility(Button.VISIBLE);
                _button_left.setVisibility(Button.GONE);
                _button_right.setVisibility(Button.GONE);
                break;
            case FileManager.STATE_SCAN_EXECUTING:
                _progressBar.setVisibility(ProgressBar.VISIBLE);
                _text_state.setText(getString(R.string.state_scan_executing));
                _button_middle.setText(R.string.caption_button_stopScan);
                _button_middle.setTag(new String[]{ACTION_STOP_SCAN});
                _button_middle.setVisibility(Button.VISIBLE);
                _button_left.setVisibility(Button.GONE);
                _button_right.setVisibility(Button.GONE);
                break;
            case FileManager.STATE_SCAN_STOP:
                _progressBar.setVisibility(ProgressBar.VISIBLE);
                _text_state.setText(getString(R.string.state_scan_stopping));
                _button_middle.setVisibility(Button.GONE);
                _button_left.setVisibility(Button.GONE);
                _button_right.setVisibility(Button.GONE);
                break;
            case FileManager.STATE_SCAN_FINISH:
                _progressBar.setVisibility(ProgressBar.GONE);
                _text_state.setText(getString(R.string.state_scan_finish));
                _button_middle.setText(R.string.caption_button_reScan);
                _button_left.setText(R.string.caption_button_startClean);
                _button_right.setText(R.string.caption_button_viewFiles);
                _button_middle.setTag(new String[]{ACTION_START_SCAN});
                _button_left.setTag(new String[]{ACTION_START_CLEAN});
                _button_right.setTag(new String[]{ACTION_VIEW_FILE});
                _button_middle.setVisibility(Button.VISIBLE);
                _button_left.setVisibility(Button.VISIBLE);
                _button_right.setVisibility(Button.VISIBLE);
                break;
            case FileManager.STATE_CLEAN_EXECUTING:
                _progressBar.setVisibility(ProgressBar.VISIBLE);
                _text_state.setText(getString(R.string.state_clean_executing));
                _button_middle.setText(R.string.caption_button_stopClean);
                _button_middle.setTag(new String[]{ACTION_STOP_CLEAN});
                _button_middle.setVisibility(Button.VISIBLE);
                _button_left.setVisibility(Button.GONE);
                _button_right.setVisibility(Button.GONE);
                break;
            case FileManager.STATE_CLEAN_STOP:
                _progressBar.setVisibility(ProgressBar.VISIBLE);
                _text_state.setText(getString(R.string.state_clean_stopping));
                _button_middle.setVisibility(Button.GONE);
                _button_left.setVisibility(Button.GONE);
                _button_right.setVisibility(Button.GONE);
                break;
            case FileManager.STATE_CLEAN_FINISH:
                _progressBar.setVisibility(ProgressBar.GONE);
                _text_state.setText(getString(R.string.state_clean_finish));
                _button_middle.setText(R.string.caption_button_reScan);
                _button_left.setText(R.string.caption_button_startClean);
                _button_right.setText(R.string.caption_button_viewFiles);
                _button_middle.setTag(new String[]{ACTION_START_SCAN});
                _button_left.setTag(new String[]{ACTION_START_CLEAN});
                _button_right.setTag(new String[]{ACTION_VIEW_FILE});
                _button_middle.setVisibility(Button.VISIBLE);
                _button_left.setVisibility(Button.VISIBLE);
                _button_right.setVisibility(Button.VISIBLE);
                break;
            default:
                _progressBar.setVisibility(ProgressBar.GONE);
                _text_state.setText(getString(R.string.state_scan_finish));
                _button_middle.setText(R.string.caption_button_reScan);
                _button_left.setText(R.string.caption_button_startClean);
                _button_right.setText(R.string.caption_button_viewFiles);
                _button_middle.setTag(new String[]{ACTION_START_SCAN});
                _button_left.setTag(new String[]{ACTION_START_CLEAN});
                _button_right.setTag(new String[]{ACTION_VIEW_FILE});
                _button_middle.setVisibility(Button.VISIBLE);
                _button_left.setVisibility(Button.VISIBLE);
                _button_right.setVisibility(Button.VISIBLE);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        String[] _tag = (String[]) v.getTag();
        switch (_tag[0]) {
            case ACTION_START_SCAN:
                FileManager.startScan();
                _handler.sendEmptyMessageDelayed(MSG_REFRESH, 150);
                break;
            case ACTION_STOP_SCAN:
                FileManager.stopScan();
                break;
            case ACTION_START_CLEAN:
                FileManager.startClean();
                _handler.sendEmptyMessageDelayed(MSG_REFRESH, 150);
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
