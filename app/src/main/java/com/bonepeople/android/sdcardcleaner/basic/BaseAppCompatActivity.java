package com.bonepeople.android.sdcardcleaner.basic;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bonepeople.android.sdcardcleaner.R;

import java.util.LinkedList;

/**
 * 集成对Handler控制的基类
 * <p>
 * Created by bonepeople on 2017/12/25.
 */

public abstract class BaseAppCompatActivity extends AppCompatActivity {
    private LinkedList<BaseHandler> handlers = null;

    protected final BaseHandler createHandler() {
        if (handlers == null)
            handlers = new LinkedList<>();
        BaseHandler handler = new BaseHandler(this);
        handlers.add(handler);
        return handler;
    }

    protected void handleMessage(Message msg) {
    }

    @Override
    public final void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        onRequestPermission(requestCode, grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED);
    }

    /**
     * 权限申请的回调函数
     *
     * @param requestCode 权限申请的请求ID
     * @param granted     权限请求的结果 true-获得权限，false-拒绝权限
     */
    protected void onRequestPermission(int requestCode, boolean granted) {
    }

    /**
     * 检查权限是否已被授权
     *
     * @param permission android.Manifest.permission
     * @return boolean 是否拥有对应权限
     */
    protected boolean checkPermission(@NonNull String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 申请指定权限
     * <p>
     * 申请某一权限，如需提示用户会创建AlertDialog对用户进行提示，权限申请的结果需要重写{@link #onRequestPermission(int, boolean)}接收
     * </p>
     *
     * @param permission android.Manifest.permission
     * @param rationale  提示信息
     */
    protected void requestPermission(@NonNull final String permission, @Nullable String rationale, final int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(rationale);
            builder.setPositiveButton(R.string.caption_button_positive, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(BaseAppCompatActivity.this, new String[]{permission}, requestCode);
                }
            });
            builder.setNegativeButton(R.string.caption_button_negative, null);
            builder.create().show();
        } else
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
    }

    @Override
    protected void onDestroy() {
        if (handlers != null) {
            for (BaseHandler handler : handlers) {
                handler.destroy();
            }
            handlers.clear();
            handlers = null;
        }
        super.onDestroy();
    }
}
