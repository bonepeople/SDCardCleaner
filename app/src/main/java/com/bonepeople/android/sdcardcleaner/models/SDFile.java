package com.bonepeople.android.sdcardcleaner.models;

import com.bonepeople.android.sdcardcleaner.utils.FileScanUtil;

import java.io.File;
import java.util.ArrayList;

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
            File[] _files = _file.listFiles();
            if (_files != null)
                for (File _child : _files) {
                    if (_child == null) {
                    } else if (FileScanUtil.get_state() == FileScanUtil.STATE_SCANING)
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

    public void updateSize(long _fileSize) {
        _size += _fileSize;
    }

    public long get_size() {
        return _size;
    }

    public ArrayList<SDFile> get_children() {
        return _children;
    }
}
