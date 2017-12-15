package com.bonepeople.android.sdcardcleaner.thread;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.bonepeople.android.sdcardcleaner.Global;

public class Service_fileManager extends Service {
    public static final int STATE_SCAN_EXECUTING = 1;
    public static final int STATE_SCAN_STOP = 2;
    public static final int STATE_SCAN_FINISH = 3;
    private static int _scanState = STATE_SCAN_FINISH;

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
}
