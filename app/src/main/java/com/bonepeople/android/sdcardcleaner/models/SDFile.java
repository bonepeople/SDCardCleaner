package com.bonepeople.android.sdcardcleaner.models;

import com.bonepeople.android.sdcardcleaner.Global;
import com.bonepeople.android.sdcardcleaner.global.CleanPathManager;
import com.bonepeople.android.sdcardcleaner.service.FileManager;
import com.bonepeople.android.sdcardcleaner.utils.NumberUtil;

import java.io.File;
import java.util.ArrayList;

/**
 * SD卡中文件的数据模型
 * <p>
 * Created by bonepeople on 2017/6/9.
 */

public class SDFile {
    private static final String FILE_ADD = "add_file";
    private static final String FILE_DELETE = "delete_file";
    private static final String FILE_CHANGE = "change_file";
    private String name;//文件名
    private String path;//文件路径
    private long size = 0;//文件大小
    private int fileCount = 0;//文件夹内文件的数量
    private boolean directory = false;//是否是文件夹
    private boolean rubbish = false;//是否需要清理
    private boolean sorted = false;//是否已排序
    private boolean checked = false;//是否被选中，用于多选
    private SDFile parent;//父目录
    private SDFile largestChild = null;//文件夹内最大的文件

    private ArrayList<SDFile> children = new ArrayList<>();//子文件列表

    public SDFile(SDFile parent, File file) {
        this.parent = parent;
        name = file.getName();
        path = file.getAbsolutePath();

        Global.setFileCount_all(1);
        if (CleanPathManager.INSTANCE.getWhiteList().contains(path)) {
            rubbish = false;
        } else if (CleanPathManager.INSTANCE.getBlackList().contains(path)) {
            rubbish = true;
            Global.setFileCount_rubbish(1);
        } else if (parent != null && parent.isRubbish()) {
            rubbish = true;
            Global.setFileCount_rubbish(1);
        } else
            rubbish = false;

        if (file.isDirectory()) {
            directory = true;
            File[] files = file.listFiles();
            if (files != null)
                for (File child : files) {
                    if (child != null)
                        if (FileManager.getState() == FileManager.STATE_SCAN_EXECUTING)
                            new SDFile(this, child);
                        else
                            break;
                }
            if (parent != null)
                parent.updateSize(this, FILE_ADD);
            FileManager.executeSort(this);
        } else {
            directory = false;
            size = file.length();
            Global.setFileSize_all(size);
            if (rubbish)
                Global.setFileSize_rubbish(size);
            if (parent != null)
                parent.updateSize(this, FILE_ADD);
        }
    }

    /**
     * 更新文件夹大小
     * <p>
     * 该函数由子文件调用，用于获取子文件的大小及引用
     *
     * @param file 子文件
     * @param type 文件大小变动的原因
     */
    private void updateSize(SDFile file, String type) {
        switch (type) {
            case FILE_ADD:
                size += file.getSize();
                if (file.isDirectory())
                    fileCount += file.getFileCount();
                fileCount += 1;
                if (largestChild != null) {
                    if (file.getSize() > largestChild.getSize())
                        largestChild = file;
                } else
                    largestChild = file;
                children.add(file);
                break;
            case FILE_DELETE:
                if (parent != null)
                    parent.updateSize(file, FILE_CHANGE);
                size -= file.getSize();
                fileCount -= 1;
                largestChild = null;
                children.remove(file);
                break;
            case FILE_CHANGE:
                if (parent != null)
                    parent.updateSize(file, FILE_CHANGE);
                size -= file.getSize();
                fileCount -= 1;
                largestChild = null;
                break;
        }
    }

    private void findLargestChild() {
        for (SDFile child : children) {
            if (largestChild != null) {
                if (child.getSize() > largestChild.getSize())
                    largestChild = child;
            } else
                largestChild = child;
        }
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public long getSize() {
        return size;
    }

    public int getFileCount() {
        return fileCount;
    }

    private SDFile getLargestChild() {
        if (largestChild == null)
            findLargestChild();
        return largestChild;
    }

    public boolean isDirectory() {
        return directory;
    }

    public boolean isRubbish() {
        return rubbish;
    }

    public ArrayList<SDFile> getChildren() {
        return children;
    }

    /**
     * 获取本文件在文件夹中所占的比重
     * <p>
     * 以文件夹中最大的文件作为100%，按比例计算自身比重
     */
    public int get_sizePercent() {
        if (parent != null) {
            SDFile _largestChild = parent.getLargestChild();
            if (_largestChild != null && _largestChild.getSize() != 0) {
                double _percent = NumberUtil.div(size, _largestChild.getSize(), 2);
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
            if (CleanPathManager.INSTANCE.getWhiteList().contains(path)) {
                rubbish = false;
                Global.setFileCount_rubbish(-1);
                if (!directory)
                    Global.setFileSize_rubbish(-size);
            } else if (CleanPathManager.INSTANCE.getBlackList().contains(path)) {
                rubbish = true;
            } else if (parent != null && parent.isRubbish()) {
                rubbish = true;
            } else {
                rubbish = false;
                Global.setFileCount_rubbish(-1);
                if (!directory)
                    Global.setFileSize_rubbish(-size);
            }
        } else {//之前需要被保留-false
            if (CleanPathManager.INSTANCE.getWhiteList().contains(path)) {
                rubbish = false;
            } else if (CleanPathManager.INSTANCE.getBlackList().contains(path)) {
                rubbish = true;
                Global.setFileCount_rubbish(1);
                if (!directory)
                    Global.setFileSize_rubbish(size);
            } else if (parent != null && parent.isRubbish()) {
                rubbish = true;
                Global.setFileCount_rubbish(1);
                if (!directory)
                    Global.setFileSize_rubbish(size);
            } else
                rubbish = false;
        }
        for (SDFile _child : children) {
            _child.updateRubbish();
        }
    }

    /**
     * 删除自身
     *
     * @param auto 是否由APP自动清理
     */
    public void delete(boolean auto) {
        if (directory) {
            ArrayList<SDFile> deleteList = new ArrayList<>(children.size());
            deleteList.addAll(children);
            for (SDFile item : deleteList) {
                item.delete(auto);
            }
        }
        if (children.size() == 0) {
            if (auto) {
                if (rubbish && FileManager.getState() == FileManager.STATE_CLEAN_EXECUTING) {
                    File file = new File(path);
                    if (!file.exists() || file.delete()) {
                        Global.setFileCount_all(-1);
                        Global.setFileSize_all(-size);
                        Global.setFileCount_rubbish(-1);
                        Global.setFileSize_rubbish(-size);
                        if (parent != null)
                            parent.updateSize(this, FILE_DELETE);
                        largestChild = null;
                        parent = null;
                    }
                }
            } else {
                if (FileManager.getState() == FileManager.STATE_DELETE_EXECUTING) {
                    File file = new File(path);
                    if (!file.exists() || file.delete()) {
                        Global.setFileCount_all(-1);
                        Global.setFileSize_all(-size);
                        if (rubbish) {
                            Global.setFileCount_rubbish(-1);
                            Global.setFileSize_rubbish(-size);
                        }
                        if (parent != null)
                            parent.updateSize(this, FILE_DELETE);
                        largestChild = null;
                        parent = null;
                    }
                }
            }
        }
    }
}
