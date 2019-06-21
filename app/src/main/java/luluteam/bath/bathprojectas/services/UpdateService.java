package luluteam.bath.bathprojectas.services;

import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.google.gson.Gson;

import java.io.File;

import luluteam.bath.bathprojectas.app.App;
import luluteam.bath.bathprojectas.app.AppManager;
import luluteam.bath.bathprojectas.constants.WebConstant;
import luluteam.bath.bathprojectas.model.result.CheckUpdateResult;
import luluteam.bath.bathprojectas.utils.ChmodUtil;
import luluteam.bath.bathprojectas.utils.ToastUtil;
import luluteam.bath.bathprojectas.utils.okhttp.OkHttpManager;
import luluteam.bath.bathprojectas.view.dialog.LoadingDialog;


/**
 *
 */
public class UpdateService extends Service {

    private Handler handler;
    private static String TAG = "UpdateService";
    private String downloadFilePath;
    private File directory;

    /**
     * 第一次启动该Service的时候，延迟10s再检查更新
     */
    private long requestDelay = 10000;

    public UpdateService() {
        handler = new Handler();
        directory = AppManager.getAppManager().currentActivity().getCacheDir();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler.removeMessages(0, null);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkUpdate();
            }
        }, requestDelay);
        requestDelay = 1000;
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 发起检查更新的请求
     * <p>
     * VersionCode：2    给开发者比较版本是否有升级用的
     * VersionName：1.1  展示给用户看的
     */
    private void checkUpdate() {
        Log.e(TAG, " App.getVersionCode()=" + App.getVersionCode());
//        ToastUtil.logAndToast(getApplicationContext(), "正在检查更新...");
        OkHttpManager.CommonGetAsyn(WebConstant.APP_CHECK_UPDATE, null, new OkHttpManager.ResultCallback() {
            @Override
            public void onCallBack(OkHttpManager.State state, String result) {
                if (state == OkHttpManager.State.SUCCESS) {
//                    Log.e(TAG, "update=====" + result);
                    CheckUpdateResult checkUpdateResult = new Gson().fromJson(result, CheckUpdateResult.class);
                    if (checkUpdateResult.isResult() && checkUpdateResult.getVersionCode() > App.getVersionCode()) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                needUpdate(checkUpdateResult);
                            }
                        });
                    } else {
                    }
                } else {
                    ToastUtil.logAndToast(getApplicationContext(), "操作失败：" + result);
                }
            }
        });

    }


    /**
     * 检测更新完成后，需要更新
     */
    private void needUpdate(CheckUpdateResult checkUpdateResult) {
        AlertDialog alertDialog;
        Log.e(TAG, "currentActivity()=" + AppManager.getAppManager().currentActivity());
        AlertDialog.Builder builder = new AlertDialog.Builder(AppManager.getAppManager().currentActivity())
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
                    }
                });
        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

    }

    /**
     * 执行更新的动作
     */
    private void doUpdate(CheckUpdateResult checkUpdateResult) {
        ToastUtil.logAndToast(getApplicationContext(), "开始下载更新...");
        LoadingDialog loadingDialog = new LoadingDialog(AppManager.getAppManager().currentActivity());
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.show();
        String url = WebConstant.WEBHOST + checkUpdateResult.getPostfixURL();
        String fileName = url.substring(url.lastIndexOf("/") + 1);
        downloadFilePath = directory + File.separator + fileName;
        Log.e(TAG, "update App===========download =" + downloadFilePath);
        OkHttpManager.download(url, directory.getAbsolutePath(), fileName, new OkHttpManager.ProgressListener() {
            @Override
            public void onProgress(long totalSize, long currSize, boolean done, int id) {
//                System.out.println("download=====" + currSize);
                if (done) {
                    handler.post(new Runnable() {
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
                if (state != OkHttpManager.State.SUCCESS) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            loadingDialog.dismiss();
                            ToastUtil.logAndToast(getApplicationContext(), "下载更新失败..." + result);
                        }
                    });

                }
            }
        });

    }


    @MainThread
    private void installAPK(String fileFillPath) {
        Intent install = new Intent(Intent.ACTION_VIEW);
        File apkFile = new File(fileFillPath);
        Uri uri;
        //对Android 版本判断
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            install.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            // context.getPackageName() + ".fileprovider"  是配置中的authorities
            uri = FileProvider.getUriForFile(getApplicationContext(), "luluteam.bath.bathprojectas.fileProvider", apkFile);
            install.setDataAndType(uri, "application/vnd.android.package-archive");
        } else {
            uri = Uri.fromFile(apkFile);
            //由于是放在了cache目录，所以没有执行的权限------一个坑
            ChmodUtil.chmod("777", apkFile.getAbsolutePath());
            install.setDataAndType(uri, "application/vnd.android.package-archive");
        }
        install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(install);
    }

}
