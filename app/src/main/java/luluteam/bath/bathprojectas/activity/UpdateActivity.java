package luluteam.bath.bathprojectas.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.google.gson.Gson;

import java.io.File;

import luluteam.bath.bathprojectas.R;
import luluteam.bath.bathprojectas.app.App;
import luluteam.bath.bathprojectas.constants.WebConstant;
import luluteam.bath.bathprojectas.model.result.CheckUpdateResult;
import luluteam.bath.bathprojectas.utils.ChmodUtil;
import luluteam.bath.bathprojectas.utils.ToastUtil;
import luluteam.bath.bathprojectas.utils.okhttp.OkHttpManager;
import luluteam.bath.bathprojectas.view.dialog.LoadingDialog;

public class UpdateActivity extends BaseActivity {

    private String downloadFilePath;
    private File directory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        directory = context.getCacheDir();
        checkUpdate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "UpdateActivity==onDestroy");
    }

    /**
     * 发起检查更新的请求
     * <p>
     * VersionCode：2    给开发者比较版本是否有升级用的
     * VersionName：1.1  展示给用户看的
     */
    private void checkUpdate() {
        LoadingDialog loadingDialog = new LoadingDialog(context);
        loadingDialog.setLoadingText("正在检查更新");
        loadingDialog.show();
        Log.e(TAG, " App.getVersionCode()=" + App.getVersionCode());
        OkHttpManager.CommonGetAsyn(WebConstant.APP_CHECK_UPDATE, null, new OkHttpManager.ResultCallback() {
            @Override
            public void onCallBack(OkHttpManager.State state, String result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadingDialog.dismiss();
                        if (state == OkHttpManager.State.SUCCESS) {
                            CheckUpdateResult checkUpdateResult = new Gson().fromJson(result, CheckUpdateResult.class);
                            if (checkUpdateResult.isResult() && checkUpdateResult.getVersionCode() > App.getVersionCode()) {
                                needUpdate(checkUpdateResult);
                            } else {
                                UpdateActivity.this.finish();
                            }
                        } else {
                            ToastUtil.showLongToast(context, "操作失败：" + result);
                            UpdateActivity.this.finish();
                        }
                    }
                });

            }
        });

    }


    /**
     * 检测更新完成后，需要更新
     */
    @MainThread
    private void needUpdate(CheckUpdateResult checkUpdateResult) {
        AlertDialog alertDialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle("更新提示")
                .setMessage("检测到新版本，是否更新")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doUpdate(checkUpdateResult);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        UpdateActivity.this.finish();
                    }
                });
        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

    }

    /**
     * 执行更新的动作
     */
    @MainThread
    private void doUpdate(CheckUpdateResult checkUpdateResult) {
        ToastUtil.showLongToast(context, "开始下载");
        LoadingDialog loadingDialog = new LoadingDialog(context);
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.show();
        String url = WebConstant.WEBHOST + checkUpdateResult.getPostfixURL();
        String fileName = url.substring(url.lastIndexOf("/") + 1);
        downloadFilePath = directory + File.separator + fileName;
        Log.e(TAG, "update App===========download =" + downloadFilePath);
        OkHttpManager.download(url, directory.getAbsolutePath(), fileName,
                new OkHttpManager.ProgressListener() {
                    @Override
                    public void onProgress(long totalSize, long currSize, boolean done, int id) {
                        if (done) {
                            UpdateActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    loadingDialog.dismiss();
                                    installAPK(downloadFilePath);
                                }
                            });
                        }
                    }
                }, new OkHttpManager.ResultCallback() {
                    @Override
                    public void onCallBack(OkHttpManager.State state, String result) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadingDialog.dismiss();
                                if (state != OkHttpManager.State.SUCCESS) {
                                    ToastUtil.logAndToast(context, "下载失败：" + result);
                                    finish();
                                }
                            }
                        });

                    }
                });
    }

    /**
     * 安装APK---未完成
     *
     * @param fileFillPath
     */
    @MainThread
    private void installAPK(String fileFillPath) {
        Intent install = new Intent(Intent.ACTION_VIEW);
        File apkFile = new File(fileFillPath);
        Uri uri;
        //对Android 版本判断
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            install.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            // context.getPackageName() + ".fileprovider"  是配置中的authorities
            uri = FileProvider.getUriForFile(context, "luluteam.bath.bathprojectas.fileProvider", apkFile);
            install.setDataAndType(uri, "application/vnd.android.package-archive");
        } else {
            uri = Uri.fromFile(apkFile);
            //由于是放在了cache目录，所以没有执行的权限------一个坑
            ChmodUtil.chmod("777", apkFile.getAbsolutePath());
            install.setDataAndType(uri, "application/vnd.android.package-archive");
        }
        install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(install);
        UpdateActivity.this.finish();
    }


}
