package com.bonepeople.android.sdcardcleaner;

import android.content.Context;

import androidx.annotation.NonNull;

import com.bonepeople.android.sdcardcleaner.models.SDFile;
import com.bonepeople.android.sdcardcleaner.utils.CommonUtil;
import com.bonepeople.android.sdcardcleaner.utils.ConfigUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

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
    private static ArrayList<String> saveList = new ArrayList<>();//保留列表
    private static ArrayList<String> cleanList = new ArrayList<>();//清理列表

    /**
     * 初始化全局变量
     *
     * @param context 该Context会被储存复用，推荐使用ApplicationContext
     */
    public static void init(@NonNull Context context) {
        Global.applicationContext = context;
        //从配置文件中初始化保留列表
        Set<String> set_save = ConfigUtil.getSaveList(applicationContext);
        saveList.clear();
        if (set_save != null) {
            saveList.addAll(set_save);
            sortList(saveList);
        }
        //从配置文件中初始化清理列表
        Set<String> set_clean = ConfigUtil.getCleanList(applicationContext);
        cleanList.clear();
        if (set_clean != null) {
            cleanList.addAll(set_clean);
            sortList(cleanList);
        }
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
        saveList.clear();
        cleanList.clear();
    }

    /**
     * 对列表进行排序
     */
    private static void sortList(@NonNull ArrayList<String> list) {
        Collections.sort(list, new Comparator<String>() {
            @Override
            public int compare(String str1, String str2) {
                return CommonUtil.comparePath(str1, str2);
            }
        });
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

    /**
     * 向保留列表里添加数据
     */
    public static void add_saveList(@NonNull ArrayList<String> newList) {
        for (String path : newList) {
            if (cleanList.contains(path))
                cleanList.remove(path);
            if (!saveList.contains(path))
                saveList.add(path);
        }
        sortList(saveList);
        HashSet<String> saveSet = new HashSet<>(saveList.size());
        saveSet.addAll(saveList);
        ConfigUtil.putSaveList(applicationContext, saveSet);
        HashSet<String> cleanSet = new HashSet<>(cleanList.size());
        cleanSet.addAll(cleanList);
        ConfigUtil.putCleanList(applicationContext, cleanSet);
    }

    /**
     * 向清理列表里添加数据
     */
    public static void add_cleanList(@NonNull ArrayList<String> newList) {
        for (String path : newList) {
            if (saveList.contains(path))
                saveList.remove(path);
            if (!cleanList.contains(path))
                cleanList.add(path);
        }
        sortList(cleanList);
        HashSet<String> saveSet = new HashSet<>(saveList.size());
        saveSet.addAll(saveList);
        ConfigUtil.putSaveList(applicationContext, saveSet);
        HashSet<String> cleanSet = new HashSet<>(cleanList.size());
        cleanSet.addAll(cleanList);
        ConfigUtil.putCleanList(applicationContext, cleanSet);
    }

    /**
     * 移除保留列表中的数据
     */
    public static void remove_saveList(int index) {
        saveList.remove(index);
        HashSet<String> saveSet = new HashSet<>(saveList.size());
        saveSet.addAll(saveList);
        ConfigUtil.putSaveList(applicationContext, saveSet);
    }

    /**
     * 移除清理列表中的数据
     */
    public static void remove_cleanList(int index) {
        cleanList.remove(index);
        HashSet<String> cleanSet = new HashSet<>(cleanList.size());
        cleanSet.addAll(cleanList);
        ConfigUtil.putCleanList(applicationContext, cleanSet);
    }

    /**
     * 判断指定路径是否需要保留
     */
    public static boolean isSave(String path) {
        return saveList.contains(path);
    }

    /**
     * 判断指定路径是否需要清理
     */
    public static boolean isClean(String path) {
        return cleanList.contains(path);
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

    public static ArrayList<String> getSaveList() {
        return saveList;
    }

    public static ArrayList<String> getCleanList() {
        return cleanList;
    }
}
