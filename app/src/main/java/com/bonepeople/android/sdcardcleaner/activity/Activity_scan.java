package com.bonepeople.android.sdcardcleaner.activity;

import android.content.Intent;
import android.os.Message;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bonepeople.android.sdcardcleaner.Global;
import com.bonepeople.android.sdcardcleaner.R;
import com.bonepeople.android.sdcardcleaner.basic.Basic_appCompatActivity;
import com.bonepeople.android.sdcardcleaner.basic.Basic_handler;
import com.bonepeople.android.sdcardcleaner.thread.Service_fileManager;

public class Activity_scan extends Basic_appCompatActivity implements View.OnClickListener {
    private static final String ACTION_START_SCAN = "startScan";
    private static final String ACTION_STOP_SCAN = "stopScan";
    private static final String ACTION_START_CLEAN = "startClean";
    private static final String ACTION_STOP_CLEAN = "stopClean";
    private static final String ACTION_VIEW_FILE = "viewFile";
    private static final int MSG_REFRESH = 1;
    private static final int REQUEST_FILE = 0;
    private ProgressBar _progressBar;
    private TextView _text_state, _text_count_all, _text_count_rubbish, _text_size_rubbish;
    private Button _button_middle, _button_left, _button_right;
    private Basic_handler _handler = createHandler();
    private int _state = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        setTitle(getString(R.string.caption_text_mine));

        _progressBar = (ProgressBar) findViewById(R.id.progressBar);
        _text_state = (TextView) findViewById(R.id.textView_state);
        _text_count_all = (TextView) findViewById(R.id.textView_count_all);
        _text_count_rubbish = (TextView) findViewById(R.id.textView_count_rubbish);
        _text_size_rubbish = (TextView) findViewById(R.id.textView_size_rubbish);
        _button_middle = (Button) findViewById(R.id.button_stop);
        _button_left = (Button) findViewById(R.id.button_clean);
        _button_right = (Button) findViewById(R.id.button_show);

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
        switch (_msg.what) {
            case MSG_REFRESH:
                if (_state != Service_fileManager.get_state()) {
                    _state = Service_fileManager.get_state();
                    updateState();
                }
                updateData();
                if (_state == Service_fileManager.STATE_READY)
                    return;
                if (_state == Service_fileManager.STATE_SCAN_FINISH)
                    return;
                if (_state == Service_fileManager.STATE_CLEAN_FINISH)
                    return;
                if (_state == Service_fileManager.STATE_DELETE_FINISH)
                    return;
                _handler.sendEmptyMessageDelayed(MSG_REFRESH, 350);
                break;
        }
    }

    private void updateState() {
        switch (_state) {
            case Service_fileManager.STATE_READY:
                _text_state.setText(R.string.state_ready);
                _button_middle.setText(R.string.caption_button_startScan);
                _button_middle.setTag(new String[]{ACTION_START_SCAN});
                _button_middle.setVisibility(Button.VISIBLE);
                _button_left.setVisibility(Button.GONE);
                _button_right.setVisibility(Button.GONE);
                break;
            case Service_fileManager.STATE_SCAN_EXECUTING:
                _progressBar.setVisibility(ProgressBar.VISIBLE);
                _text_state.setText(getString(R.string.state_scan_executing));
                _button_middle.setText(R.string.caption_button_stopScan);
                _button_middle.setTag(new String[]{ACTION_STOP_SCAN});
                _button_middle.setVisibility(Button.VISIBLE);
                _button_left.setVisibility(Button.GONE);
                _button_right.setVisibility(Button.GONE);
                break;
            case Service_fileManager.STATE_SCAN_STOP:
                _progressBar.setVisibility(ProgressBar.VISIBLE);
                _text_state.setText(getString(R.string.state_scan_stopping));
                _button_middle.setVisibility(Button.GONE);
                _button_left.setVisibility(Button.GONE);
                _button_right.setVisibility(Button.GONE);
                break;
            case Service_fileManager.STATE_SCAN_FINISH:
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
            case Service_fileManager.STATE_CLEAN_EXECUTING:
                _progressBar.setVisibility(ProgressBar.VISIBLE);
                _text_state.setText(getString(R.string.state_clean_executing));
                _button_middle.setText(R.string.caption_button_stopClean);
                _button_middle.setTag(new String[]{ACTION_STOP_CLEAN});
                _button_middle.setVisibility(Button.VISIBLE);
                _button_left.setVisibility(Button.GONE);
                _button_right.setVisibility(Button.GONE);
                break;
            case Service_fileManager.STATE_CLEAN_STOP:
                _progressBar.setVisibility(ProgressBar.VISIBLE);
                _text_state.setText(getString(R.string.state_clean_stopping));
                _button_middle.setVisibility(Button.GONE);
                _button_left.setVisibility(Button.GONE);
                _button_right.setVisibility(Button.GONE);
                break;
            case Service_fileManager.STATE_CLEAN_FINISH:
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

    private void updateData() {
        long _fileCount_all = Global.get_fileCount_all();
        long _fileCount_rubbish = Global.get_fileCount_rubbish();
        long _fileSize_rubbish = Global.get_fileSize_rubbish();
        _text_count_all.setText(getString(R.string.state_fileCount_all, _fileCount_all));
        _text_count_rubbish.setText(getString(R.string.state_fileCount_rubbish, _fileCount_rubbish));
        _text_size_rubbish.setText(getString(R.string.state_fileSize_rubbish, Formatter.formatFileSize(this, _fileSize_rubbish)));
    }

    @Override
    public void onClick(View v) {
        String[] _tag = (String[]) v.getTag();
        switch (_tag[0]) {
            case ACTION_START_SCAN:
                Service_fileManager.startScan();
                _handler.sendEmptyMessageDelayed(MSG_REFRESH, 150);
                break;
            case ACTION_STOP_SCAN:
                Service_fileManager.stopScan();
                break;
            case ACTION_START_CLEAN:
                Service_fileManager.startClean();
                _handler.sendEmptyMessageDelayed(MSG_REFRESH, 150);
                break;
            case ACTION_STOP_CLEAN:
                Service_fileManager.stopClean();
                break;
            case ACTION_VIEW_FILE:
                startActivityForResult(new Intent(getApplicationContext(), Activity_list_file.class), REQUEST_FILE);
                break;
        }
    }
}
