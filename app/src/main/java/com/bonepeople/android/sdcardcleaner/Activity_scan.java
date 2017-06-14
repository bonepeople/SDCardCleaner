package com.bonepeople.android.sdcardcleaner;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bonepeople.android.sdcardcleaner.utils.FileScanUtil;

public class Activity_scan extends AppCompatActivity {
    public static final int MSG_STATE = 0;
    public static final int MSG_NUMBER = 1;
    public static final int MSG_OVER = 2;
    private TextView _text_state, _text_number;
    private Button _button_stop;
    private Handler _handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Activity_scan.this.isDestroyed())
                return;
            switch (msg.what) {
                case MSG_STATE:
                    _text_state.setText((String) msg.obj);
                    break;
                case MSG_NUMBER:
                    _text_number.setText("文件数量：" + (long) msg.obj);
                    break;
                case MSG_OVER:
                    long _size = FileScanUtil.get_rootFile().get_size();
                    System.out.println("all files size is " + Formatter.formatFileSize(Activity_scan.this, _size));
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        setTitle("文件扫描");

        _text_state = (TextView) findViewById(R.id.textView_state);
        _text_number = (TextView) findViewById(R.id.textView_scan_file);
        _button_stop = (Button) findViewById(R.id.button_stop);

        _button_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileScanUtil.stop();
            }
        });

        FileScanUtil.start(_handler);
    }

    @Override
    public void onBackPressed() {
        FileScanUtil.exit();
        super.onBackPressed();
    }
}
