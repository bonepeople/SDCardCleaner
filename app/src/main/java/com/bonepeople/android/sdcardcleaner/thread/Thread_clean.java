package com.bonepeople.android.sdcardcleaner.thread;

import com.bonepeople.android.sdcardcleaner.Global;

import java.io.File;

/**
 * 清理文件的线程
 * <p>
 * Created by bonepeople on 2017/12/6.
 */

public class Thread_clean extends Thread {

    @Override
    public void run() {
        if (Global.get_rootFile() != null)
            Global.get_rootFile().delete(true);
        Service_fileManager.finishClean();
    }
}
