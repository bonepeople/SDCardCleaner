package com.bonepeople.android.sdcardcleaner.activity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Pair;
import android.util.SparseArray;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.bonepeople.android.sdcardcleaner.Global;
import com.bonepeople.android.sdcardcleaner.R;
import com.bonepeople.android.sdcardcleaner.adapter.FileExplorerAdapter;
import com.bonepeople.android.sdcardcleaner.models.SDFile;
import com.bonepeople.android.sdcardcleaner.service.FileManager;
import com.bonepeople.android.sdcardcleaner.thread.DeleteFileThread;
import com.bonepeople.android.sdcardcleaner.thread.UpdateRubbishThread;
import com.bonepeople.android.sdcardcleaner.widget.TitleBar;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 文件浏览界面
 */
public class FileExplorerActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {
    private static final String ACTION_DELETE = "delete";
    private static final String ACTION_CLEAN = "clean";
    private static final String ACTION_HOLD = "hold";
    private static final int REQUEST_FILE = 0;
    private static HashMap<String, SDFile> files = new HashMap<>();//以路径为key，保存文件信息，用于传递至下一级页面
    private TitleBar titleBar;
    private TextView textView_path;
    private RecyclerView recyclerView;
    private LinearLayout linearLayout_buttonBar;
    private CheckBox checkBox_all;
    private SDFile file;
    private FileExplorerAdapter adapter;
    private ProgressDialog progressDialog;
    private LocalBroadcastManager broadcastManager;
    private ArrayList<Integer> notifyItems = new ArrayList<>();

    /**
     * 开启一个文件浏览界面
     *
     * @param filePath 所开界面所展示的文件路径，无路径则展示根目录
     */
    public static void call(@Nullable Activity activity, @NonNull String filePath, int requestCode) {
        if (activity == null)
            return;
        Intent intent = new Intent(activity, FileExplorerActivity.class);
        intent.putExtra("path", filePath);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 开启一个文件浏览界面
     *
     * @param filePath 所开界面所展示的文件路径，无路径则展示根目录
     */
    public static void call(@Nullable Activity activity, @NonNull String filePath, int requestCode, Bundle bundle) {
        if (activity == null)
            return;
        Intent intent = new Intent(activity, FileExplorerActivity.class);
        intent.putExtra("path", filePath);
        activity.startActivityForResult(intent, requestCode, bundle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_explorer);
        initView();
        initData();
    }

    private void initView() {
        titleBar = findViewById(R.id.titleBar);
        textView_path = findViewById(R.id.textView_path);
        recyclerView = findViewById(R.id.recyclerView);
        linearLayout_buttonBar = findViewById(R.id.linearLayout_buttonBar);
        checkBox_all = findViewById(R.id.checkBox_all);
        findViewById(R.id.textView_delete).setOnClickListener(this);
        findViewById(R.id.textView_clean).setOnClickListener(this);
        findViewById(R.id.textView_hold).setOnClickListener(this);
        findViewById(R.id.imageView_close).setOnClickListener(this);
        checkBox_all.setOnClickListener(this);
    }

    private void initData() {
        String path = getIntent().getStringExtra("path");
        if (!path.isEmpty())
            file = files.get(path);
        if (file == null) {
            titleBar.setTitle(R.string.caption_text_mine);
            if (Global.getRootFile() == null) {
                FileManager.reset();
                finish();
                return;
            }
            file = Global.getRootFile();
        } else {
            titleBar.setTitle(file.getName());
        }
        textView_path.setText(file.getPath().replace(Global.getRootFile().getPath(), getString(R.string.str_path_rootFile)));

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new FileExplorerAdapter(this, this);
        adapter.setData(file.getChildren());
        adapter.setLoading(false);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        broadcastManager = LocalBroadcastManager.getInstance(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_FILE) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        if (adapter.isMultiSelect()) {
            setMultiSelect(false);
        } else
            super.onBackPressed();
    }

    private void setMultiSelect(boolean selecting) {
        adapter.setMultiSelect(selecting);
        //经测试，只刷新部分条目会出现bug @ recyclerview-v7:25.3.1'
        adapter.notifyItemRangeChanged(0, adapter.getItemCount(), FileExplorerAdapter.PART_CHECKBOX);
        if (selecting) {
            linearLayout_buttonBar.setVisibility(LinearLayout.VISIBLE);
        } else {
            linearLayout_buttonBar.setVisibility(LinearLayout.GONE);
        }
    }


    /**
     * 删除所选文件
     */
    private void deleteFiles() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.dialog_list_file_delete_title));
        builder.setMessage(getString(R.string.dialog_list_file_delete_message));
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.caption_button_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SparseArray<SDFile> deleteFiles = new SparseArray<>(adapter.getCheckedSet().size());
                for (int position : adapter.getCheckedSet()) {
                    deleteFiles.put(position, file.getChildren().get(position));
                }
                showProgress(ACTION_DELETE, deleteFiles.size());
                FileManager.startDelete(deleteFiles);
            }
        });
        builder.setNegativeButton(R.string.caption_button_negative, null);
        builder.create().show();
    }

    /**
     * 将所选文件添加到清理列表中
     */
    private void cleanFiles() {
        ArrayList<String> cleanPathList = new ArrayList<>(adapter.getCheckedSet().size());
        SparseArray<SDFile> cleanFiles = new SparseArray<>(adapter.getCheckedSet().size());
        for (int position : adapter.getCheckedSet()) {
            cleanPathList.add(file.getChildren().get(position).getPath());
            cleanFiles.put(position, file.getChildren().get(position));
        }
        Global.add_cleanList(cleanPathList);
        showProgress(ACTION_CLEAN, cleanFiles.size());
        new UpdateRubbishThread(cleanFiles).start();
    }

    /**
     * 将所选文件添加到保存列表中
     */
    private void saveFiles() {
        ArrayList<String> savePathList = new ArrayList<>(adapter.getCheckedSet().size());
        SparseArray<SDFile> saveFiles = new SparseArray<>(adapter.getCheckedSet().size());
        for (int position : adapter.getCheckedSet()) {
            savePathList.add(file.getChildren().get(position).getPath());
            saveFiles.put(position, file.getChildren().get(position));
        }
        Global.add_saveList(savePathList);
        showProgress(ACTION_HOLD, saveFiles.size());
        new UpdateRubbishThread(saveFiles).start();
    }

    private void showProgress(String action, int count) {
        progressDialog = new ProgressDialog(this, R.style.DialogTheme);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        switch (action) {
            case ACTION_DELETE:
                progressDialog.setMessage(getString(R.string.dialog_list_file_deleting));
                progressDialog.setButton(ProgressDialog.BUTTON_NEGATIVE, getString(R.string.caption_button_negative), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FileManager.stopDelete();
                    }
                });
                break;
            case ACTION_CLEAN:
                progressDialog.setMessage(getString(R.string.dialog_list_file_cleaning));
                break;
            case ACTION_HOLD:
                progressDialog.setMessage(getString(R.string.dialog_list_file_saving));
                break;
        }
        progressDialog.setMax(count);
        progressDialog.show();
        notifyItems.clear();

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action == null)
                    return;
                int index;
                switch (action) {
                    case DeleteFileThread.ACTION_DELETE:
                        index = intent.getIntExtra("index", -1);
                        if (index != -1) {
                            notifyItems.add(index - progressDialog.getProgress());
                            progressDialog.incrementProgressBy(1);
                        }
                        break;
                    case DeleteFileThread.ACTION_FINISH:
                        progressDialog.dismiss();
                        progressDialog = null;
                        broadcastManager.unregisterReceiver(this);
                        for (int index_change : notifyItems) {
                            adapter.notifyItemRemoved(index_change);
                        }
                        adapter.notifyItemRangeChanged(0, adapter.getItemCount());
                        break;
                    case UpdateRubbishThread.ACTION_UPDATE:
                        index = intent.getIntExtra("index", -1);
                        if (index != -1) {
                            notifyItems.add(index);
                            progressDialog.incrementProgressBy(1);
                        }
                        break;
                    case UpdateRubbishThread.ACTION_FINISH:
                        progressDialog.dismiss();
                        progressDialog = null;
                        broadcastManager.unregisterReceiver(this);
                        for (int index_change : notifyItems) {
                            adapter.notifyItemChanged(index_change);
                        }
                        break;
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(DeleteFileThread.ACTION_DELETE);
        filter.addAction(DeleteFileThread.ACTION_FINISH);
        filter.addAction(UpdateRubbishThread.ACTION_UPDATE);
        filter.addAction(UpdateRubbishThread.ACTION_FINISH);
        broadcastManager.registerReceiver(receiver, filter);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textView_delete:
                setMultiSelect(false);
                if (adapter.getCheckedSet().size() > 0)
                    deleteFiles();
                break;
            case R.id.textView_clean:
                setMultiSelect(false);
                if (adapter.getCheckedSet().size() > 0)
                    cleanFiles();
                break;
            case R.id.textView_hold:
                setMultiSelect(false);
                if (adapter.getCheckedSet().size() > 0)
                    saveFiles();
                break;
            case R.id.checkBox_all:
                adapter.setCheckedSet(-1);
                //经测试，只刷新部分条目会出现bug @ recyclerview-v7:25.3.1'
                adapter.notifyItemRangeChanged(0, adapter.getItemCount(), FileExplorerAdapter.PART_CHECKBOX);
                break;
            case R.id.imageView_close:
                setMultiSelect(false);
                break;
            default: {
                String[] tags = (String[]) v.getTag(R.id.tags);
                if (tags == null)
                    return;
                switch (tags[0]) {
                    case FileExplorerAdapter.ACTION_CLICK: {
                        int position = Integer.parseInt(tags[1]);
                        if (adapter.isMultiSelect()) {//处于多选状态
                            checkBox_all.setChecked(adapter.setCheckedSet(position));
                            adapter.notifyItemChanged(position, FileExplorerAdapter.PART_CHECKBOX);
                        } else {//处于浏览状态
                            SDFile clickFile = file.getChildren().get(position);
                            if (clickFile.isDirectory()) {
                                files.put(clickFile.getPath(), clickFile);

                                View view = recyclerView.findViewHolderForAdapterPosition(position).itemView;
                                View textView_name = view.findViewById(R.id.textView_name);
                                Pair<View, String> list = new Pair<>(view, "transition_body");
                                Pair<View, String> name = new Pair<>(textView_name, "transition_title");
                                Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(this, list, name).toBundle();
                                FileExplorerActivity.call(this, clickFile.getPath(), REQUEST_FILE, bundle);
                            }
                        }
                        break;
                    }
                }
            }
        }
    }

    @Override
    public boolean onLongClick(View v) {
        String[] tags = (String[]) v.getTag(R.id.tags);
        if (tags == null)
            return false;
        switch (tags[0]) {
            case FileExplorerAdapter.ACTION_CLICK:
                int position = Integer.parseInt(tags[1]);
                if (adapter.isMultiSelect()) {//处于多选状态
                    checkBox_all.setChecked(adapter.setCheckedSet(position));
                    adapter.notifyItemChanged(position, FileExplorerAdapter.PART_CHECKBOX);
                } else {//处于浏览状态
                    setMultiSelect(true);
                    checkBox_all.setChecked(adapter.setCheckedSet(position));
                    adapter.notifyItemChanged(position, FileExplorerAdapter.PART_CHECKBOX);
                }
                break;
        }
        return true;
    }
}
