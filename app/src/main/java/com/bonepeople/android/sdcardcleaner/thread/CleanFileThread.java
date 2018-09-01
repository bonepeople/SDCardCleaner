package com.bonepeople.android.sdcardcleaner.thread;

import com.bonepeople.android.sdcardcleaner.Global;
import com.bonepeople.android.sdcardcleaner.service.FileManager;

/**
 * 清理文件的线程
 * <p>
 * Created by bonepeople on 2017/12/6.
 */

public class CleanFileThread extends Thread {

    @Override
    public void run() {
        if (Global.getRootFile() != null)
            Global.getRootFile().delete(true);
        else
            FileManager.reset();
        FileManager.finishClean();
    }
}
