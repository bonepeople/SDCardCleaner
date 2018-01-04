package com.bonepeople.android.sdcardcleaner.thread;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.SparseArray;

import com.bonepeople.android.sdcardcleaner.Global;
import com.bonepeople.android.sdcardcleaner.models.SDFile;
import com.bonepeople.android.sdcardcleaner.service.FileManager;


/**
 * 删除文件的线程
 * <p>
 * Created by bonepeople on 2017/12/21.
 */

public class DeleteFileThread extends Thread {
    public static final String ACTION_DELETE = "DeleteFileThread:delete successful";
    public static final String ACTION_FINISH = "DeleteFileThread:delete finish";
    private SparseArray<SDFile> _files;

    public DeleteFileThread(@NonNull SparseArray<SDFile> _files) {
        this._files = _files;
    }

    @Override
    public void run() {
        LocalBroadcastManager _manager = LocalBroadcastManager.getInstance(Global.get_applicationContext());
        SDFile _file;
        for (int _temp_i = 0; _temp_i < _files.size(); _temp_i++) {
            _file = _files.valueAt(_temp_i);
            _file.delete(false);
            if (FileManager.get_state() == FileManager.STATE_DELETE_EXECUTING) {
                Intent _delete = new Intent(ACTION_DELETE);
                _delete.putExtra("index", _files.keyAt(_temp_i));
                _manager.sendBroadcast(_delete);
            } else
                break;
        }
        FileManager.finishDelete();
        Intent _finish = new Intent(ACTION_FINISH);
        _manager.sendBroadcast(_finish);
    }
}
