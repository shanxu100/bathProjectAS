package luluteam.bath.bathprojectas.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import luluteam.bath.bathprojectas.R;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;


/**
 * 用于申请权限
 * 将该Activity继承Activity，并设置成透明主题
 */
@RuntimePermissions
public class PermissionActivity extends BaseActivity {
    private String TAG = "PermissionActivity";
    private AlertDialog permissionAlertDialog;
    private PermissionRequest request;


    /**
     * 如何定义真正的常量数数组
     */
    //static final String[] PERMISSIONLIST = new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);
        init();

    }

    private void init() {
        /**
         * 设置“提示重新申请权限”的对话框
         */
        permissionAlertDialog = new AlertDialog.Builder(this)
                .setMessage("为了保证应用的正常运行，我们需要获取一些权限，是否愿意？")
                .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.proceed();
                        permissionAlertDialog.dismiss();
                    }
                })
                .setNegativeButton("不给", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        request.cancel();
                        permissionAlertDialog.dismiss();
                        PermissionActivity.this.finish();
                    }
                })
                .create();

        //handlerThread.

        /**
         * 这个方法要在 编译 之后才会出现 —— 委托
         */
        // NOTE: delegate the permission handling to generated method
        PermissionActivityPermissionsDispatcher.showMultiPermissionWithPermissionCheck(this);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        PermissionActivityPermissionsDispatcher.onActivityResult(PermissionActivity.this, requestCode);
    }
//======================================================================================

    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE, Manifest.permission.WAKE_LOCK})
    void showMultiPermission() {
        Log.e(TAG, "权限申请成功");
        PermissionActivity.this.finish();
//        PermissionActivityPermissionsDispatcher.needSystemAlertWindowPermissinWithPermissionCheck(PermissionActivity.this);


    }

    @OnShowRationale({Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE, Manifest.permission.WAKE_LOCK})
    void showRationale(final PermissionRequest request) {
        /**
         * 向用户解释，需要重新申请权限
         */
        this.request = request;
        permissionAlertDialog.show();
    }

    /**
     * 申请这个“SYSTEM_ALERT_WINDOW”悬浮窗的权限，会通过Intent打开系统设置界面，然后手动赋予。
     * 如果赋予成功，则调用这里
     */
    @NeedsPermission(Manifest.permission.SYSTEM_ALERT_WINDOW)
    protected void needSystemAlertWindowPermissin() {
        Log.e(TAG, "悬浮窗权限申请成功");
        PermissionActivity.this.finish();
    }


    @OnShowRationale(Manifest.permission.SYSTEM_ALERT_WINDOW)
    protected void onShowRationale(final PermissionRequest request) {
        //request.proceed();
        /**
         * 向用户解释，需要重新申请权限
         */
        this.request = request;
        permissionAlertDialog.show();
    }


    //=============================================
    @OnPermissionDenied({Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.SYSTEM_ALERT_WINDOW, Manifest.permission.WAKE_LOCK})
    void showDenied() {
        /**
         * 在多个权限申请过程中，有一个或多个申请失败，就会调用这里“一次”
         */
        Log.e(TAG, "拒绝权限申请 ……");
        PermissionActivity.this.finish();

    }

    @OnNeverAskAgain({Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.SYSTEM_ALERT_WINDOW, Manifest.permission.WAKE_LOCK})
    void showNever() {
        Log.e(TAG, "不再提示申请权限 ……");
        PermissionActivity.this.finish();

    }


}
