package com.bonepeople.android.sdcardcleaner.models;

import com.bonepeople.android.sdcardcleaner.utils.FileScanUtil;

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
    private String _name;//文件名
    private String _path;//文件路径
    private long _size = 0;//文件大小
    private boolean _shouldBeDelete = false;//是否需要清理
    private SDFile _parent;//父目录

    private ArrayList<SDFile> _children = new ArrayList<>();//子目录

    public SDFile(SDFile _parent, File _file) {
        if (_file.isDirectory()) {
            File[] _files = sortFile(_file.listFiles());
            if (_files != null)
                for (File _child : _files) {
                    if (_child != null)
                        if (FileScanUtil.get_state() == FileScanUtil.STATE_SCANING)
                            _children.add(new SDFile(this, _child));
                        else
                            break;
                }
            if (_parent != null)
                _parent.updateSize(_size);
        } else {
            FileScanUtil.addFile();
            _size = _file.length();
            if (_parent != null)
                _parent.updateSize(_size);
        }
        this._parent = _parent;
        _name = _file.getName();
        _path = _file.getAbsolutePath();
    }

    private File[] sortFile(File[] _files) {
        if (_files != null) {
            Arrays.sort(_files, new Comparator<File>() {
                @Override
                public int compare(File _file1, File _file2) {
                    if (_file1.isDirectory() && _file2.isDirectory()) {
                        return _file1.getName().compareToIgnoreCase(_file2.getName());
                    } else {
                        if (_file1.isDirectory() && _file2.isFile()) {
                            return -1;
                        } else if (_file1.isFile() && _file2.isDirectory()) {
                            return 1;
                        } else {
                            return _file1.getName().compareToIgnoreCase(_file2.getName());
                        }
                    }
                }
            });
        }
        return _files;
    }

    public void updateSize(long _fileSize) {
        _size += _fileSize;
    }

    public String get_name() {
        return _name;
    }

    public long get_size() {
        return _size;
    }

    public ArrayList<SDFile> get_children() {
        return _children;
    }
}
