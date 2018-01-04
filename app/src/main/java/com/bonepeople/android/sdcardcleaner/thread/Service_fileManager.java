package com.bonepeople.android.sdcardcleaner.thread;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.SparseArray;

import com.bonepeople.android.sdcardcleaner.Global;
import com.bonepeople.android.sdcardcleaner.models.SDFile;

public class Service_fileManager extends Service {
    public static final int STATE_READY = 0;
    public static final int STATE_SCAN_EXECUTING = 1;
    public static final int STATE_SCAN_STOP = 2;
    public static final int STATE_SCAN_FINISH = 3;
    public static final int STATE_CLEAN_EXECUTING = 4;
    public static final int STATE_CLEAN_STOP = 5;
    public static final int STATE_CLEAN_FINISH = 6;
    public static final int STATE_DELETE_EXECUTING = 7;
    public static final int STATE_DELETE_STOP = 8;
    public static final int STATE_DELETE_FINISH = 9;
    private static int _state = STATE_READY;

    /**
     * 开始扫描文件
     */
    public static void startScan() {
        if (_state == STATE_READY || _state == STATE_SCAN_FINISH || _state == STATE_CLEAN_FINISH || _state == STATE_DELETE_FINISH) {
            _state = STATE_SCAN_EXECUTING;
            Global.reset();
            new ScanFileThread().start();
        }
    }

    /**
     * 停止扫描文件
     */
    public static void stopScan() {
        if (_state == STATE_SCAN_EXECUTING) {
            _state = STATE_SCAN_STOP;
        }
    }

    /**
     * 扫描文件结束，该方法仅由扫描的线程调用
     */
    public static void finishScan() {
        _state = STATE_SCAN_FINISH;
    }

    /**
     * 开始清理文件
     */
    public static void startClean() {
        if (_state == STATE_SCAN_FINISH || _state == STATE_CLEAN_FINISH || _state == STATE_DELETE_FINISH) {
            _state = STATE_CLEAN_EXECUTING;
            new CleanFileThread().start();
        }
    }

    /**
     * 停止清理文件
     */
    public static void stopClean() {
        if (_state == STATE_CLEAN_EXECUTING) {
            _state = STATE_CLEAN_STOP;
        }
    }

    /**
     * 清理文件结束，该方法仅由清理的线程调用
     */
    public static void finishClean() {
        _state = STATE_CLEAN_FINISH;
    }

    /**
     * 开始删除文件
     */
    public static void startDelete(SparseArray<SDFile> _files) {
        if (_state == STATE_SCAN_FINISH || _state == STATE_CLEAN_FINISH || _state == STATE_DELETE_FINISH) {
            _state = STATE_DELETE_EXECUTING;
            new DeleteFileThread(_files).start();
        }
    }

    /**
     * 停止删除文件
     */
    public static void stopDelete() {
        if (_state == STATE_DELETE_EXECUTING) {
            _state = STATE_DELETE_STOP;
        }
    }

    /**
     * 删除文件结束，该方法仅由删除的线程调用
     */
    public static void finishDelete() {
        _state = STATE_DELETE_FINISH;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {

    }

    public static int get_state() {
        return _state;
    }
}
