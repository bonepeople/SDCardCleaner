package com.bonepeople.android.sdcardcleaner.thread;

import android.content.Intent;
import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bonepeople.android.sdcardcleaner.Global;
import com.bonepeople.android.sdcardcleaner.models.SDFile;

/**
 * 更新文件清理标记的线程
 * <p>
 * Created by bonepeople on 2017/12/23.
 */

public class UpdateRubbishThread extends Thread {
    public static final String ACTION_UPDATE = "UpdateRubbishThread:update successful";
    public static final String ACTION_FINISH = "UpdateRubbishThread:update finish";
    private SparseArray<SDFile> files;

    public UpdateRubbishThread(@NonNull SparseArray<SDFile> files) {
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
            file.updateRubbish();
            Intent update = new Intent(ACTION_UPDATE);
            update.putExtra("index", files.keyAt(temp_i));
            manager.sendBroadcast(update);
        }
        Intent finish = new Intent(ACTION_FINISH);
        manager.sendBroadcast(finish);
    }
}
