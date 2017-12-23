package com.bonepeople.android.sdcardcleaner.models;

import com.bonepeople.android.sdcardcleaner.Global;
import com.bonepeople.android.sdcardcleaner.thread.Service_fileManager;
import com.bonepeople.android.sdcardcleaner.utils.CommonUtil;
import com.bonepeople.android.sdcardcleaner.utils.NumberUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

/**
 * SD卡中文件的数据模型
 * <p>
 * Created by bonepeople on 2017/6/9.
 */

public class SDFile {
    private static final String FILE_ADD = "add_file";
    private static final String FILE_DELETE = "delete_file";
    private static final String FILE_CHANGE = "change_file";
    private String _name;//文件名
    private String _path;//文件路径
    private long _size = 0;//文件大小
    private boolean directory = false;//是否是文件夹
    private boolean rubbish = false;//是否需要清理
    private SDFile _parent;//父目录
    private SDFile _largestChild = null;//文件夹内最大的文件

    private ArrayList<SDFile> _children = new ArrayList<>();//子文件列表

    public SDFile(SDFile _parent, File _file) {
        this._parent = _parent;
        _name = _file.getName();
        _path = _file.getAbsolutePath();

        Global.set_fileCount_all(1);
        if (Global.isSave(_path)) {
            rubbish = false;
        } else if (Global.isClean(_path)) {
            rubbish = true;
            Global.set_fileCount_rubbish(1);
        } else if (_parent != null && _parent.isRubbish()) {
            rubbish = true;
            Global.set_fileCount_rubbish(1);
        } else
            rubbish = false;

        if (_file.isDirectory()) {
            directory = true;
            File[] _files = sortFile(_file.listFiles());
            if (_files != null)
                for (File _child : _files) {
                    if (_child != null)
                        if (Service_fileManager.get_scanState() == Service_fileManager.STATE_SCAN_EXECUTING)
                            new SDFile(this, _child);
                        else
                            break;
                }
            if (_parent != null)
                _parent.updateSize(this, FILE_ADD);
        } else {
            directory = false;
            _size = _file.length();
            Global.set_fileSize_all(_size);
            if (rubbish)
                Global.set_fileSize_rubbish(_size);
            if (_parent != null)
                _parent.updateSize(this, FILE_ADD);
        }
    }

    /**
     * 对文件进行排序
     */
    private File[] sortFile(File[] _files) {
        if (_files != null) {
            Arrays.sort(_files, new Comparator<File>() {
                @Override
                public int compare(File _file1, File _file2) {
                    if (_file1.isDirectory() && _file2.isDirectory()) {
                        return CommonUtil.comparePath(_file1.getName(), _file2.getName());
                    } else if (_file1.isDirectory() && _file2.isFile()) {
                        return -1;
                    } else if (_file1.isFile() && _file2.isDirectory()) {
                        return 1;
                    } else {
                        return CommonUtil.comparePath(_file1.getName(), _file2.getName());
                    }
                }
            });
        }
        return _files;
    }

    /**
     * 更新文件夹大小
     * <p>
     * 该函数由子文件调用，用于获取子文件的大小及引用
     *
     * @param _file 子文件
     * @param _type 文件大小变动的原因
     */
    private void updateSize(SDFile _file, String _type) {
        switch (_type) {
            case FILE_ADD:
                _size += _file.get_size();
                if (_largestChild != null) {
                    if (_file.get_size() > _largestChild.get_size())
                        _largestChild = _file;
                } else
                    _largestChild = _file;
                _children.add(_file);
                break;
            case FILE_DELETE:
                if (_parent != null)
                    _parent.updateSize(_file, FILE_CHANGE);
                _size -= _file.get_size();
                _children.remove(_file);
                _largestChild = null;
                break;
            case FILE_CHANGE:
                if (_parent != null)
                    _parent.updateSize(_file, FILE_CHANGE);
                _size -= _file.get_size();
                _largestChild = null;
                break;
        }
    }

    private void findLargestChild() {
        for (SDFile _child : _children) {
            if (_largestChild != null) {
                if (_child.get_size() > _largestChild.get_size())
                    _largestChild = _child;
            } else
                _largestChild = _child;
        }
    }

    public String get_name() {
        return _name;
    }

    public String get_path() {
        return _path;
    }

    public long get_size() {
        return _size;
    }

    private SDFile get_largestChild() {
        if (_largestChild == null)
            findLargestChild();
        return _largestChild;
    }

    public boolean isDirectory() {
        return directory;
    }

    public boolean isRubbish() {
        return rubbish;
    }

    public ArrayList<SDFile> get_children() {
        return _children;
    }

    /**
     * 获取本文件在文件夹中所占的比重
     * <p>
     * 以文件夹中最大的文件作为100%，按比例计算自身比重
     */
    public int get_sizePercent() {
        if (_parent != null) {
            SDFile _largestChild = _parent.get_largestChild();
            if (_largestChild != null && _largestChild.get_size() != 0) {
                double _percent = NumberUtil.div(_size, _largestChild.get_size(), 2);
                return (int) (_percent * 100);
            } else
                return 0;
        } else
            return 100;
    }

    /**
     * 更新该文件及子文件的待清理状态
     */
    public void updateRubbish() {
        if (rubbish) {//之前需要被清理-true
            if (Global.isSave(_path)) {
                rubbish = false;
                Global.set_fileCount_rubbish(-1);
                if (!directory)
                    Global.set_fileSize_rubbish(-_size);
            } else if (Global.isClean(_path)) {
                rubbish = true;
            } else if (_parent != null && _parent.isRubbish()) {
                rubbish = true;
            } else {
                rubbish = false;
                Global.set_fileCount_rubbish(-1);
                if (!directory)
                    Global.set_fileSize_rubbish(-_size);
            }
        } else {//之前需要被保留-false
            if (Global.isSave(_path)) {
                rubbish = false;
            } else if (Global.isClean(_path)) {
                rubbish = true;
                Global.set_fileCount_rubbish(1);
                if (!directory)
                    Global.set_fileSize_rubbish(_size);
            } else if (_parent != null && _parent.isRubbish()) {
                rubbish = true;
                Global.set_fileCount_rubbish(1);
                if (!directory)
                    Global.set_fileSize_rubbish(_size);
            } else
                rubbish = false;
        }
        for (SDFile _child : _children) {
            _child.updateRubbish();
        }
    }

    /**
     * 删除自身
     *
     * @param _auto 是否由APP自动清理
     */
    public void delete(boolean _auto) {
        if (directory) {
            ArrayList<SDFile> _deleteList = new ArrayList<>(_children.size());
            _deleteList.addAll(_children);
            for (SDFile _item : _deleteList) {
                _item.delete(_auto);
            }
        }
        if (_children.size() == 0) {
            if (_auto) {
                if (rubbish && Service_fileManager.get_cleanState() == Service_fileManager.STATE_CLEAN_EXECUTING) {
                    File _file = new File(_path);
                    if (_file.delete()) {
                        Global.set_fileCount_all(-1);
                        Global.set_fileSize_all(-_size);
                        Global.set_fileCount_rubbish(-1);
                        Global.set_fileSize_rubbish(-_size);
                        if (_parent != null)
                            _parent.updateSize(this, FILE_DELETE);
                        _largestChild = null;
                        _parent = null;
                    }
                }
            } else {
                if (Service_fileManager.get_deleteState() == Service_fileManager.STATE_DELETE_EXECUTING) {
                    File _file = new File(_path);
                    if (_file.delete()) {
                        Global.set_fileCount_all(-1);
                        Global.set_fileSize_all(-_size);
                        if (rubbish) {
                            Global.set_fileCount_rubbish(-1);
                            Global.set_fileSize_rubbish(-_size);
                        }
                        if (_parent != null)
                            _parent.updateSize(this, FILE_DELETE);
                        _largestChild = null;
                        _parent = null;
                    }
                }
            }
        }
    }
}
