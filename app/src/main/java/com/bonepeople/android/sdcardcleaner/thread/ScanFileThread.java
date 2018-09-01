package com.bonepeople.android.sdcardcleaner.thread;

import android.os.Environment;

import com.bonepeople.android.sdcardcleaner.Global;
import com.bonepeople.android.sdcardcleaner.models.SDFile;
import com.bonepeople.android.sdcardcleaner.service.FileManager;

import java.io.File;

/**
 * 扫描文件的线程
 * <p>
 * Created by bonepeople on 2017/6/9.
 */

public class ScanFileThread extends Thread {

    @Override
    public void run() {
        File file = Environment.getExternalStorageDirectory();
        SDFile root = new SDFile(null, file);
        Global.setRootFile(root);
        FileManager.finishScan();
    }
}
