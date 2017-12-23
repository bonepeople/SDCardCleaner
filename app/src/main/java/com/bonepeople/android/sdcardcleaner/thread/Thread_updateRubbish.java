package com.bonepeople.android.sdcardcleaner.thread;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.SparseArray;

import com.bonepeople.android.sdcardcleaner.Global;
import com.bonepeople.android.sdcardcleaner.models.SDFile;

/**
 * 更新文件清理标记的线程
 * <p>
 * Created by bonepeople on 2017/12/23.
 */

public class Thread_updateRubbish extends Thread {
    public static final String ACTION_UPDATE = "Thread_updateRubbish:update successful";
    public static final String ACTION_FINISH = "Thread_updateRubbish:update finish";
    private SparseArray<SDFile> _files;

    public Thread_updateRubbish(@NonNull SparseArray<SDFile> _files) {
        this._files = _files;
    }

    @Override
    public void run() {
        LocalBroadcastManager _manager = LocalBroadcastManager.getInstance(Global.get_applicationContext());
        SDFile _file;
        for (int _temp_i = 0; _temp_i < _files.size(); _temp_i++) {
            _file = _files.valueAt(_temp_i);
            _file.updateRubbish();
            Intent _update = new Intent(ACTION_UPDATE);
            _update.putExtra("index", _files.keyAt(_temp_i));
            _manager.sendBroadcast(_update);
        }
        Intent _finish = new Intent(ACTION_FINISH);
        _manager.sendBroadcast(_finish);
    }
}
