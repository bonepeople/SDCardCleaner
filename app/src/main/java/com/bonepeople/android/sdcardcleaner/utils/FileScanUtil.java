package com.bonepeople.android.sdcardcleaner.utils;

import android.os.Handler;
import android.os.Message;

import com.bonepeople.android.sdcardcleaner.Activity_scan;
import com.bonepeople.android.sdcardcleaner.models.SDFile;
import com.bonepeople.android.sdcardcleaner.thread.Thread_scan;

/**
 * 文件扫描的管理类
 * <p>
 * Created by bonepeople on 2017/6/9.
 */

public class FileScanUtil {
    public static final int STATE_READY = 0;
    public static final int STATE_SCANING = 1;
    public static final int STATE_STOP = 2;
    public static final int STATE_OVER = 3;
    private static SDFile _rootFile;
    private static long _fileCount = 0;
    private static Handler _handler;
    private static int _state = STATE_READY;

    public static void start(Handler _handler) {
        if (_state == STATE_SCANING || _state == STATE_STOP)
            return;
        _rootFile = null;
        _fileCount = 0;
        FileScanUtil._handler = _handler;
        _state = STATE_SCANING;
        new Thread_scan().start();
        update_number();
    }

    public static void stop() {
        if (_state != STATE_SCANING)
            return;
        _state = STATE_STOP;
        update_state("正在停止扫描...", 0);
    }

    public static void exit() {
        if (_state != STATE_SCANING)
            return;
        _state = STATE_OVER;
        _handler = null;
    }

    public static void over() {
        if (_state == STATE_OVER)
            return;
        _state = STATE_OVER;
        update_number();
        update_state("扫描已结束", 700);
        notify_over();
        _handler = null;
    }

    public static void addFile() {
        _fileCount++;
        if (_fileCount % 100 == 0) {
            update_number();
        }
    }

    public static void set_rootFile(SDFile _rootFile) {
        FileScanUtil._rootFile = _rootFile;
    }

    private static void update_state(String _txt, long _delay) {
        if (_handler != null) {
            Message _message_state = _handler.obtainMessage();
            _message_state.what = Activity_scan.MSG_STATE;
            _message_state.obj = _txt;
            _handler.sendMessageDelayed(_message_state, _delay);
        }
    }

    private static void update_number() {
        if (_handler != null) {
            Message _message_num = _handler.obtainMessage();
            _message_num.what = Activity_scan.MSG_NUMBER;
            _message_num.obj = _fileCount;
            _handler.sendMessage(_message_num);
        }
    }

    private static void notify_over() {
        if (_handler != null) {
            Message _message_over = _handler.obtainMessage();
            _message_over.what = Activity_scan.MSG_OVER;
            _handler.sendMessage(_message_over);
        }
    }

    public static SDFile get_rootFile() {
        return _rootFile;
    }

    public static int get_state() {
        return _state;
    }
}
