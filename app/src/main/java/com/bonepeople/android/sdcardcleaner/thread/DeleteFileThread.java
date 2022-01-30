package com.bonepeople.android.sdcardcleaner.thread;

import android.content.Intent;
import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

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
    private SparseArray<SDFile> files;

    public DeleteFileThread(@NonNull SparseArray<SDFile> files) {
        this.files = files;
    }

    @Override
    public void run() {
        try {
            sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(Global.getApplicationContext());
        SDFile file;
        for (int temp_i = 0; temp_i < files.size(); temp_i++) {
            file = files.valueAt(temp_i);
            file.delete(false);
            if (FileManager.getState() == FileManager.STATE_DELETE_EXECUTING) {
                Intent intent_delete = new Intent(ACTION_DELETE);
                intent_delete.putExtra("index", files.keyAt(temp_i));
                manager.sendBroadcast(intent_delete);
            } else
                break;
        }
        FileManager.finishDelete();
        Intent finish = new Intent(ACTION_FINISH);
        manager.sendBroadcast(finish);
    }
}
