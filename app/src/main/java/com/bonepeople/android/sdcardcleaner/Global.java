package com.bonepeople.android.sdcardcleaner;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bonepeople.android.sdcardcleaner.utils.ConfigUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;

/**
 * 全局变量
 * <p>
 * Created by bonepeople on 2017/11/29.
 */

public class Global {
    private static ArrayList<String> _saveList = new ArrayList<>();//保留列表
    private static ArrayList<String> _cleanList = new ArrayList<>();//清理列表

    /**
     * 初始化全局变量
     */
    public static void init(@NonNull Context _context) {
        //从配置文件中初始化保留列表
        Set<String> _set_save = ConfigUtil.getSaveList(_context);
        _saveList.clear();
        if (_set_save != null) {
            _saveList.addAll(_set_save);
            Collections.sort(_saveList, new Comparator<String>() {
                @Override
                public int compare(String _str1, String _str2) {
                    return _str1.compareToIgnoreCase(_str2);
                }
            });
        }
        //从配置文件中初始化清理列表
        Set<String> _set_clean = ConfigUtil.getCleanList(_context);
        _cleanList.clear();
        if (_set_clean != null) {
            _cleanList.addAll(_set_clean);
            Collections.sort(_cleanList, new Comparator<String>() {
                @Override
                public int compare(String _str1, String _str2) {
                    return _str1.compareToIgnoreCase(_str2);
                }
            });
        }
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
}
