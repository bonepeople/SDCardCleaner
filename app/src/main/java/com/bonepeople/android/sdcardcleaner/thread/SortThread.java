package com.bonepeople.android.sdcardcleaner.thread;

import com.bonepeople.android.sdcardcleaner.models.SDFile;
import com.bonepeople.android.sdcardcleaner.utils.CommonUtil;

import java.util.Collections;
import java.util.Comparator;

/**
 * 为文件夹中文件排序的线程
 */
public class SortThread extends Thread {
    private SDFile file;

    public SortThread(SDFile file) {
        this.file = file;
    }

    @Override
    public void run() {
        Collections.sort(file.getChildren(), new Comparator<SDFile>() {
            @Override
            public int compare(SDFile file1, SDFile file2) {
                if (file1.isDirectory() && file2.isDirectory()) {
                    return CommonUtil.comparePath(file1.getName(), file2.getName());
                } else if (file1.isDirectory() && !file2.isDirectory()) {
                    return -1;
                } else if (!file1.isDirectory() && file2.isDirectory()) {
                    return 1;
                } else {
                    return CommonUtil.comparePath(file1.getName(), file2.getName());
                }
            }
        });
    }
}
