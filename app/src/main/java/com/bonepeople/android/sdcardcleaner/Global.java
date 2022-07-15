package com.bonepeople.android.sdcardcleaner;

import android.content.Context;

import androidx.annotation.NonNull;

import com.bonepeople.android.sdcardcleaner.models.SDFile;

/**
 * 全局变量
 * <p>
 * Created by bonepeople on 2017/11/29.
 */

public class Global {
    private static Context applicationContext;
    private static SDFile rootFile = null;
    private static long fileCount_all = 0;//所有文件总数，包含文件夹
    private static long fileSize_all = 0;//所有文件总大小
    private static long fileCount_rubbish = 0;//待清理文件总数，包含文件夹
    private static long fileSize_rubbish = 0;//待清理文件总大小

    /**
     * 初始化全局变量
     *
     * @param context 该Context会被储存复用，推荐使用ApplicationContext
     */
    public static void init(@NonNull Context context) {
        Global.applicationContext = context;
    }

    public static void reset() {
        //重置变量
        rootFile = null;
        fileCount_all = 0;
        fileSize_all = 0;
        fileCount_rubbish = 0;
        fileSize_rubbish = 0;
    }

    public static void destroy() {
        applicationContext = null;
        rootFile = null;
    }

    public static void setRootFile(SDFile rootFile) {
        if (applicationContext != null)
            Global.rootFile = rootFile;
    }

    /**
     * 统计已扫描文件个数
     */
    public static void setFileCount_all(int count) {
        fileCount_all += count;
    }

    /**
     * 统计总文件大小
     *
     * @param fileSize 文件大小
     */
    public static void setFileSize_all(long fileSize) {
        fileSize_all += fileSize;
    }

    /**
     * 统计待清理文件个数
     */
    public static void setFileCount_rubbish(int count) {
        fileCount_rubbish += count;
    }

    /**
     * 统计待清理文件的大小
     *
     * @param fileSize 文件大小
     */
    public static void setFileSize_rubbish(long fileSize) {
        fileSize_rubbish += fileSize;
    }

    public static Context getApplicationContext() {
        return applicationContext;
    }

    public static SDFile getRootFile() {
        return rootFile;
    }

    public static long getFileCount_all() {
        return fileCount_all;
    }

    public static long getFileSize_all() {
        return fileSize_all;
    }

    public static long getFileCount_rubbish() {
        return fileCount_rubbish;
    }

    public static long getFileSize_rubbish() {
        return fileSize_rubbish;
    }
}
