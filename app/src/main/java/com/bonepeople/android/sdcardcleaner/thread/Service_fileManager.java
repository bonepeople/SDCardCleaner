package com.bonepeople.android.sdcardcleaner.thread;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.SparseArray;

import com.bonepeople.android.sdcardcleaner.Global;
import com.bonepeople.android.sdcardcleaner.models.SDFile;

public class Service_fileManager extends Service {
    public static final int STATE_SCAN_EXECUTING = 1;
    public static final int STATE_SCAN_STOP = 2;
    public static final int STATE_SCAN_FINISH = 3;
    public static final int STATE_CLEAN_EXECUTING = 4;
    public static final int STATE_CLEAN_STOP = 5;
    public static final int STATE_CLEAN_FINISH = 6;
    public static final int STATE_DELETE_EXECUTING = 7;
    public static final int STATE_DELETE_STOP = 8;
    public static final int STATE_DELETE_FINISH = 9;
    private static int _scanState = STATE_SCAN_FINISH;
    private static int _cleanState = STATE_CLEAN_FINISH;
    private static int _deleteState = STATE_DELETE_FINISH;

    /**
     * 开始扫描文件
     */
    public static void startScan() {
        if (_scanState == STATE_SCAN_FINISH) {
            _scanState = STATE_SCAN_EXECUTING;
            Global.reset();
            new Thread_scan().start();
        }
    }

    /**
     * 停止扫描文件
     */
    public static void stopScan() {
        if (_scanState == STATE_SCAN_EXECUTING) {
            _scanState = STATE_SCAN_STOP;
        }
    }

    /**
     * 扫描文件结束，该方法仅由扫描的线程调用
     */
    public static void finishScan() {
        _scanState = STATE_SCAN_FINISH;
    }

    /**
     * 开始清理文件
     */
    public static void startClean() {
        if (_cleanState == STATE_CLEAN_FINISH) {
            _cleanState = STATE_CLEAN_EXECUTING;
            new Thread_clean().start();
        }
    }

    /**
     * 停止清理文件
     */
    public static void stopClean() {
        if (_cleanState == STATE_CLEAN_EXECUTING) {
            _cleanState = STATE_CLEAN_STOP;
        }
    }

    /**
     * 清理文件结束，该方法仅由清理的线程调用
     */
    public static void finishClean() {
        _cleanState = STATE_CLEAN_FINISH;
    }

    /**
     * 开始删除文件
     */
    public static void startDelete(SparseArray<SDFile> _files) {
        if (_deleteState == STATE_DELETE_FINISH) {
            _deleteState = STATE_DELETE_EXECUTING;
            new Thread_delete(_files).start();
        }
    }

    /**
     * 停止删除文件
     */
    public static void stopDelete() {
        if (_deleteState == STATE_DELETE_EXECUTING) {
            _deleteState = STATE_DELETE_STOP;
        }
    }

    /**
     * 删除文件结束，该方法仅由删除的线程调用
     */
    public static void finishDelete() {
        _deleteState = STATE_DELETE_FINISH;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {

    }

    public static int get_scanState() {
        return _scanState;
    }

    public static int get_cleanState() {
        return _cleanState;
    }

    public static int get_deleteState() {
        return _deleteState;
    }
}
