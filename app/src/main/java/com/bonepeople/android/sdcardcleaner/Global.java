package com.bonepeople.android.sdcardcleaner;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bonepeople.android.sdcardcleaner.models.SDFile;
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
    private static Context _applicationContext;
    private static SDFile _rootFile;
    private static long _fileCount_all = 0;//所有文件总数，包含文件夹
    private static long _fileSize_all = 0;//所有文件总大小
    private static long _fileCount_rubbish = 0;//待清理文件总数，包含文件夹
    private static long _fileSize_rubbish = 0;//待清理文件总大小
    private static ArrayList<String> _saveList = new ArrayList<>();//保留列表
    private static ArrayList<String> _cleanList = new ArrayList<>();//清理列表

    /**
     * 初始化全局变量
     *
     * @param _context 该Context会被储存复用，推荐使用ApplicationContext
     */
    public static void init(@NonNull Context _context) {
        Global._applicationContext = _context;
        reset();
    }

    public static void reset() {
        //重置变量
        _rootFile = null;
        _fileCount_all = 0;
        _fileSize_all = 0;
        _fileCount_rubbish = 0;
        _fileSize_rubbish = 0;
        //从配置文件中初始化保留列表
        Set<String> _set_save = ConfigUtil.getSaveList(_applicationContext);
        _saveList.clear();
        if (_set_save != null) {
            _saveList.addAll(_set_save);
            sortList(_saveList);
        }
        //从配置文件中初始化清理列表
        Set<String> _set_clean = ConfigUtil.getCleanList(_applicationContext);
        _cleanList.clear();
        if (_set_clean != null) {
            _cleanList.addAll(_set_clean);
            sortList(_cleanList);
        }
    }

    /**
     * 对列表进行排序
     */
    private static void sortList(@NonNull ArrayList<String> _list) {
        Collections.sort(_list, new Comparator<String>() {
            @Override
            public int compare(String _str1, String _str2) {
                return _str1.compareToIgnoreCase(_str2);
            }
        });
    }

    public static void set_rootFile(SDFile _rootFile) {
        Global._rootFile = _rootFile;
    }

    /**
     * 统计一个已扫描文件
     */
    public static void add_fileCount_all() {
        _fileCount_all++;
    }

    /**
     * 添加一个已统计文件的大小
     *
     * @param _fileSize 文件大小
     */
    public static void add_fileSize_all(long _fileSize) {
        Global._fileSize_all += _fileSize;
    }

    /**
     * 统计一个待清理文件
     */
    public static void add_fileCount_rubbish() {
        _fileCount_rubbish++;
    }

    /**
     * 添加一个待清理文件的大小
     *
     * @param _fileSize 文件大小
     */
    public static void add_fileSize_rubbish(long _fileSize) {
        Global._fileSize_rubbish += _fileSize;
    }

    /**
     * 向保留列表里添加数据
     */
    public static void add_saveList(@NonNull ArrayList<String> _newList) {
        for (String _path : _newList) {
            if (_cleanList.contains(_path))
                _cleanList.remove(_path);
            if (!_saveList.contains(_path))
                _saveList.add(_path);
        }
        sortList(_saveList);
        HashSet<String> _saveSet = new HashSet<>(_saveList.size());
        _saveSet.addAll(_saveList);
        ConfigUtil.putSaveList(_applicationContext, _saveSet);
        HashSet<String> _cleanSet = new HashSet<>(_cleanList.size());
        _cleanSet.addAll(_cleanList);
        ConfigUtil.putCleanList(_applicationContext, _cleanSet);
    }

    /**
     * 向清理列表里添加数据
     */
    public static void add_cleanList(@NonNull ArrayList<String> _newList) {
        for (String _path : _newList) {
            if (_saveList.contains(_path))
                _saveList.remove(_path);
            if (!_cleanList.contains(_path))
                _cleanList.add(_path);
        }
        sortList(_cleanList);
        HashSet<String> _saveSet = new HashSet<>(_saveList.size());
        _saveSet.addAll(_saveList);
        ConfigUtil.putSaveList(_applicationContext, _saveSet);
        HashSet<String> _cleanSet = new HashSet<>(_cleanList.size());
        _cleanSet.addAll(_cleanList);
        ConfigUtil.putCleanList(_applicationContext, _cleanSet);
    }

    /**
     * 移除保留列表中的数据
     */
    public static void remove_saveList(@NonNull ArrayList<String> _removeList) {
        for (String _path : _removeList) {
            if (_saveList.contains(_path))
                _saveList.remove(_path);
        }
        HashSet<String> _saveSet = new HashSet<>(_saveList.size());
        _saveSet.addAll(_saveList);
        ConfigUtil.putSaveList(_applicationContext, _saveSet);
    }

    /**
     * 移除清理列表中的数据
     */
    public static void remove_cleanList(@NonNull ArrayList<String> _removeList) {
        for (String _path : _removeList) {
            if (_cleanList.contains(_path))
                _cleanList.remove(_path);
        }
        HashSet<String> _cleanSet = new HashSet<>(_cleanList.size());
        _cleanSet.addAll(_cleanList);
        ConfigUtil.putCleanList(_applicationContext, _cleanSet);
    }

    /**
     * 判断指定路径是否需要保留
     */
    public static boolean isSave(String _path) {
        return _saveList.contains(_path);
    }

    /**
     * 判断指定路径是否需要清理
     */
    public static boolean isClean(String _path) {
        return _cleanList.contains(_path);
    }

    public static Context get_applicationContext() {
        return _applicationContext;
    }

    public static SDFile get_rootFile() {
        return _rootFile;
    }

    public static long get_fileCount_all() {
        return _fileCount_all;
    }

    public static long get_fileSize_all() {
        return _fileSize_all;
    }

    public static long get_fileCount_rubbish() {
        return _fileCount_rubbish;
    }

    public static long get_fileSize_rubbish() {
        return _fileSize_rubbish;
    }

    public static ArrayList<String> get_saveList() {
        return _saveList;
    }

    public static ArrayList<String> get_cleanList() {
        return _cleanList;
    }
}
