package luluteam.bath.bathprojectas.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import luluteam.bath.bathprojectas.R;
import luluteam.bath.bathprojectas.adapter.GridViewAdapter;
import luluteam.bath.bathprojectas.constants.APPConstant;
import luluteam.bath.bathprojectas.constants.WebConstant;
import luluteam.bath.bathprojectas.model.result.FindFileResult;
import luluteam.bath.bathprojectas.model.result.OpeResult;
import luluteam.bath.bathprojectas.utils.ImageUtil;
import luluteam.bath.bathprojectas.utils.ToastUtil;
import luluteam.bath.bathprojectas.utils.UriUtil;
import luluteam.bath.bathprojectas.utils.okhttp.OkHttpManager;
import luluteam.bath.bathprojectas.view.dialog.LoadingDialog;

/**
 * 展示图片列表
 * Created by TC on 2018/4/10.
 */
public class DisplayImgsAty extends BaseActivity {

    private GridViewAdapter adapter;
    private GridView img_GridView;
    private Button choosePic_btn, takePhoto_btn;
    private Toolbar include_toolbar;

    private FindFileResult findFileResult;
    private ArrayList<String> urlList;
    //    private List<String> urlList;
    private String businessType;
    private File photoFile;
    private String savePath;

    private static final int REQ_CODE_CHOOSE_PIC = 100;
    private static final int REQ_CODE_TAKE_PHOTO = 101;
    private static final int REQ_CODE_PERMMISION_CAMERA = 200;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_imgs);

        initUI();
        initData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadImgsUrl();
    }

    private void initUI() {
        img_GridView = (GridView) findViewById(R.id.img_GridView);
        choosePic_btn = (Button) findViewById(R.id.choosePic_btn);
        takePhoto_btn = (Button) findViewById(R.id.takePhoto_btn);
        include_toolbar = (Toolbar) findViewById(R.id.include_toolbar);
        include_toolbar.setTitle("图片预览");
        this.setSupportActionBar(include_toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //点击事件
        img_GridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context, DisplayImgActivity.class);
                intent.putStringArrayListExtra("urls", urlList);
                intent.putExtra("position", position);
                context.startActivity(intent);
            }
        });
        //长按事件 放到了这这里
        img_GridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "GridView===长按==" + position);
                new AlertDialog.Builder(context).setTitle("确定删除这张照片吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (findFileResult != null) {
                                    String fileId = findFileResult.getDataList().get(position).getFileId();
                                    deletePic(fileId);
                                }
                                dialog.dismiss();
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
                return true;
            }
        });

        choosePic_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePic();
            }
        });
        takePhoto_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initData() {
        savePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                .getAbsolutePath() + File.separator + "Camera";

        urlList = new ArrayList<>();
        adapter = new GridViewAdapter(new ArrayList<>(), context);
//        adapter.setItemHeight((DisplayUtil.getScreenWidth() - 30) / 3);
        businessType = getIntent().getStringExtra("businessType");
        if (businessType == null) {
            businessType = "";
        }

    }

    /**
     * 获取一组图片的url
     */
    private void loadImgsUrl() {
        HashMap<String, String> params = new HashMap<>();
        params.put("toiletId", APPConstant.TOILETID);
        params.put("businessType", businessType);
        OkHttpManager.CommonPostAsyn(WebConstant.GET_IMGS_URLS_BY_TOILETID, params, new OkHttpManager.ResultCallback() {
            @Override
            public void onCallBack(OkHttpManager.State state, String result) {
                if (state == OkHttpManager.State.SUCCESS) {
                    findFileResult = new Gson().fromJson(result, FindFileResult.class);
                    urlList.clear();
                    if (findFileResult.isResult()) {
                        for (FindFileResult.FileInfo fileInfo : findFileResult.getDataList()) {
                            String url = WebConstant.DOWNLOAD_IMG_BY_FILEID + "?fileId=" + fileInfo.getFileId();
                            urlList.add(url);
                        }
                        Log.i(TAG, "获取照片列表：urlList.size()=" + urlList.size());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showImgs(urlList);
                            }
                        });
                    }
                }
            }
        });

    }

    @MainThread
    private void showImgs(List<String> urlList) {
        if (urlList != null) {
            adapter.notifyDataChange(urlList);
            img_GridView.setAdapter(adapter);
        }
    }

    private boolean checkAndRequestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //没有写数据的权限
            ToastUtil.showLongToast(context, "未获取写数据的权限");
            Log.e(TAG, "未获取写数据的权限");
            return false;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            //没有写数据的权限
            ToastUtil.showLongToast(context, "未获取照相机的权限");
            Log.e(TAG, "未获取照相机的权限");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}
                    , REQ_CODE_PERMMISION_CAMERA);
            return false;
        }
        return true;
    }

    /**
     * 拍照
     */
    private void takePhoto() {

        if (!checkAndRequestPermission()) {
            return;
        }
        //实例化Intent对象,使用MediaStore的ACTION_IMAGE_CAPTURE常量调用系统相机
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            //创建一个File
            photoFile = ImageUtil.createImageFile(savePath);
            Uri photoURI;
            if (photoFile != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    //如果是7.0及以上的系统使用FileProvider的方式创建一个Uri
                    Log.e(TAG, "Build.VERSION_2.SDK_INT >= Build.VERSION_CODES.N");
                    photoURI = FileProvider.getUriForFile(this, "luluteam.bath.bathprojectas.fileProvider", photoFile);
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                } else {
                    //7.0以下使用这种方式创建一个Uri
                    photoURI = Uri.fromFile(photoFile);
                }
                //将Uri传递给系统相机
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                startActivityForResult(intent, REQ_CODE_TAKE_PHOTO);
            }
        }
    }

    /**
     * 选择本地文件
     */
    private void choosePic() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        //设置类型，我这里是任意类型，任意后缀的可以这样写。
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "choose local pic..."), REQ_CODE_CHOOSE_PIC);
    }

    /**
     * 上传文件
     */
    private void uploadPic(Uri uri) {
        if (uri == null) {
            return;
        }
        String fileFullName = UriUtil.getFilePathFromUri(context, uri);
        Log.i(TAG, "uri.getPath()===" + uri.getPath() + "  fullName==" + fileFullName);
        File file = new File(fileFullName);

        uploadPic(file);

    }

    /**
     * 重载：上传文件
     *
     * @param file
     */
    private void uploadPic(File file) {
        Log.e(TAG, "upload===" + file.getAbsolutePath());
        if (file != null && file.isFile()) {
            HashMap<String, String> params = new HashMap<>();
            params.put("toiletId", APPConstant.TOILETID);
            params.put("businessType", businessType);
            params.put("username", APPConstant.USERNAME);
            LoadingDialog loadingDialog = new LoadingDialog(context);
            loadingDialog.show();
            loadingDialog.setCanceledOnTouchOutside(false);
            OkHttpManager.upload(WebConstant.UPLOAD_IMG_TOILET, new File[]{file}, new String[]{"file"}
                    , params, new OkHttpManager.ProgressListener() {
                        @Override
                        public void onProgress(long totalSize, long currSize, boolean done, int id) {
                            if (done) {
                                try {
                                    //暂停1.5s，否则刚刚上传完毕，查找不到新的照片信息
                                    Thread.sleep(1500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        loadingDialog.dismiss();
                                        loadImgsUrl();
                                    }
                                });
                            }
                        }
                    }, new OkHttpManager.ResultCallback() {
                        @Override
                        public void onCallBack(OkHttpManager.State state, String result) {
                            if (state == OkHttpManager.State.SUCCESS) {
                                OpeResult opeResult = new Gson().fromJson(result, OpeResult.class);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (opeResult.isResult() == true) {
                                            ToastUtil.showShortToast(context, "上传成功");
                                            Log.i(TAG, "图片上传成功：" + params.toString() + " " + result);
                                        } else {
                                            ToastUtil.showShortToast(context, "上传失败：" + opeResult.getReason());
                                        }
                                    }
                                });

                            }
                        }
                    });
        }
    }

    private void deletePic(String fileId) {
        HashMap<String, String> params = new HashMap<>();
        params.put("fileId", fileId);
        params.put("username", APPConstant.USERNAME);
        params.put("toiletId", APPConstant.TOILETID);
        LoadingDialog loadingDialog = new LoadingDialog(context);
        loadingDialog.show();
        loadingDialog.setCanceledOnTouchOutside(false);
        OkHttpManager.CommonPostAsyn(WebConstant.DELETE_IMG_TOILET, params, new OkHttpManager.ResultCallback() {
            @Override
            public void onCallBack(OkHttpManager.State state, String result) {
                loadingDialog.dismiss();
//                Log.i(TAG, "删除图片===" + result);
                if (state == OkHttpManager.State.SUCCESS) {
                    OpeResult opeResult = new Gson().fromJson(result, OpeResult.class);
                    Log.i(TAG, "删除图片：" + params + " " + result);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (opeResult.isResult()) {
                                loadImgsUrl();
                            } else {
                                ToastUtil.showLongToast(context, "操作失败：" + opeResult.getReason());
                            }
                        }
                    });

                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_CODE_PERMMISION_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                ToastUtil.showLongToast(context, "Camera权限申请成功");
            } else {
                // Permission Denied
                ToastUtil.showLongToast(context, "很遗憾你把相机权限禁用了");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "requestCode" + requestCode + "  resultCode" + resultCode);
        if (requestCode == REQ_CODE_CHOOSE_PIC && resultCode == RESULT_OK) {
            //获得图片后
            Uri uri = data.getData();
            uploadPic(uri);
        } else if (requestCode == REQ_CODE_TAKE_PHOTO && resultCode == RESULT_OK) {
            // 扫描单个媒体文件，注意是文件，不是文件夹
            new SingleMediaScanner(this, photoFile);
            uploadPic(photoFile);
        }
    }

    /**
     * 及时更新相册的显示
     */
    public static class SingleMediaScanner implements MediaScannerConnection.MediaScannerConnectionClient {

        private MediaScannerConnection mMs;
        private File mFile;

        public SingleMediaScanner(Context context, File f) {
            mFile = f;
            mMs = new MediaScannerConnection(context, this);
            mMs.connect();
        }

        @Override
        public void onMediaScannerConnected() {
            mMs.scanFile(mFile.getAbsolutePath(), null);
        }

        @Override
        public void onScanCompleted(String path, Uri uri) {
            mMs.disconnect();
        }

    }
}
