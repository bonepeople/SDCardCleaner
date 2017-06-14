package com.bonepeople.android.sdcardcleaner.thread;

import android.os.Environment;

import com.bonepeople.android.sdcardcleaner.models.SDFile;
import com.bonepeople.android.sdcardcleaner.utils.FileScanUtil;

import java.io.File;

/**
 * 扫描文件的线程
 * <p>
 * Created by bonepeople on 2017/6/9.
 */

public class Thread_scan extends Thread {

    @Override
    public void run() {
        File _file = Environment.getExternalStorageDirectory();
        SDFile _root = new SDFile(null, _file);
        FileScanUtil.set_rootFile(_root);
        FileScanUtil.over();
    }
}
