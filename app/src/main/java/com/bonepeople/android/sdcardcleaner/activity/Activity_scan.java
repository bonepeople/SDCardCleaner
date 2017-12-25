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
    public static final int MSG_SCAN = 1;
    public static final int MSG_CLEAN = 2;
    private ProgressBar _progressBar;
    private TextView _text_state, _text_count_all, _text_count_rubbish, _text_size_rubbish;
    private Button _button_stop, _button_clean, _button_show;
    private Basic_handler _handler = createHandler();

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
        _button_stop = (Button) findViewById(R.id.button_stop);
        _button_clean = (Button) findViewById(R.id.button_clean);
        _button_show = (Button) findViewById(R.id.button_show);

        _button_stop.setOnClickListener(this);
        _button_clean.setOnClickListener(this);
        _button_show.setOnClickListener(this);

        //待完善
        _handler.sendEmptyMessageDelayed(MSG_SCAN, 150);
        Service_fileManager.startScan();
    }

    @Override
    protected void handleMessage(Message _msg) {
        long _fileCount_all, _fileCount_rubbish, _fileSize_rubbish;
        switch (_msg.what) {
            case MSG_SCAN:
                int _scanState = Service_fileManager.get_scanState();
                String _scanStateStr = "";
                switch (_scanState) {
                    case Service_fileManager.STATE_SCAN_EXECUTING:
                        _scanStateStr = getString(R.string.state_scan_executing);
                        break;
                    case Service_fileManager.STATE_SCAN_STOP:
                        _scanStateStr = getString(R.string.state_scan_stopping);
                        break;
                    case Service_fileManager.STATE_SCAN_FINISH:
                        _scanStateStr = getString(R.string.state_scan_finish);
                        break;
                }
                _text_state.setText(_scanStateStr);
                _fileCount_all = Global.get_fileCount_all();
                _fileCount_rubbish = Global.get_fileCount_rubbish();
                _fileSize_rubbish = Global.get_fileSize_rubbish();
                _text_count_all.setText(getString(R.string.state_fileCount_all, _fileCount_all));
                _text_count_rubbish.setText(getString(R.string.state_fileCount_rubbish, _fileCount_rubbish));
                _text_size_rubbish.setText(getString(R.string.state_fileSize_rubbish, Formatter.formatFileSize(this, _fileSize_rubbish)));
                if (_scanState != Service_fileManager.STATE_SCAN_FINISH)
                    _handler.sendEmptyMessageDelayed(MSG_SCAN, 350);
                else {
                    _progressBar.setVisibility(ProgressBar.GONE);
                    _button_stop.setVisibility(Button.GONE);
                    _button_clean.setVisibility(Button.VISIBLE);
                    _button_show.setVisibility(Button.VISIBLE);
                }
                break;
            case MSG_CLEAN:
                int _cleanState = Service_fileManager.get_cleanState();
                String _cleanStateStr = "";
                switch (_cleanState) {
                    case Service_fileManager.STATE_CLEAN_EXECUTING:
                        _cleanStateStr = getString(R.string.state_clean_executing);
                        break;
                    case Service_fileManager.STATE_CLEAN_STOP:
                        _cleanStateStr = getString(R.string.state_clean_stopping);
                        break;
                    case Service_fileManager.STATE_CLEAN_FINISH:
                        _cleanStateStr = getString(R.string.state_clean_finish);
                        break;
                }
                _text_state.setText(_cleanStateStr);
                _fileCount_all = Global.get_fileCount_all();
                _fileCount_rubbish = Global.get_fileCount_rubbish();
                _fileSize_rubbish = Global.get_fileSize_rubbish();
                _text_count_all.setText(getString(R.string.state_fileCount_all, _fileCount_all));
                _text_count_rubbish.setText(getString(R.string.state_fileCount_rubbish, _fileCount_rubbish));
                _text_size_rubbish.setText(getString(R.string.state_fileSize_rubbish, Formatter.formatFileSize(this, _fileSize_rubbish)));
                if (_cleanState != Service_fileManager.STATE_CLEAN_FINISH)
                    _handler.sendEmptyMessageDelayed(MSG_CLEAN, 350);
                else {
                    _progressBar.setVisibility(ProgressBar.GONE);
                    _button_stop.setVisibility(Button.GONE);
                    _button_clean.setVisibility(Button.VISIBLE);
                    _button_show.setVisibility(Button.VISIBLE);
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_stop:
                Service_fileManager.stopScan();
                break;
            case R.id.button_clean:
                _progressBar.setVisibility(ProgressBar.VISIBLE);
                _button_stop.setVisibility(Button.VISIBLE);
                _button_clean.setVisibility(Button.GONE);
                _button_show.setVisibility(Button.GONE);
                _handler.sendEmptyMessageDelayed(MSG_CLEAN, 150);
                Service_fileManager.startClean();
                break;
            case R.id.button_show:
                startActivity(new Intent(getApplicationContext(), Activity_list_file.class));
                finish();
                break;
        }
    }
}
