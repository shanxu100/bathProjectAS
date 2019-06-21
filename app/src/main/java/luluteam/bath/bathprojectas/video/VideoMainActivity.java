package luluteam.bath.bathprojectas.video;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.gson.Gson;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import luluteam.bath.bathprojectas.R;
import luluteam.bath.bathprojectas.activity.BaseActivity;
import luluteam.bath.bathprojectas.utils.ToastUtil;
import luluteam.bath.bathprojectas.utils.okhttp.OkHttpManager;
import luluteam.bath.bathprojectas.video.model.CameraListResult;
import luluteam.bath.bathprojectas.video.model.SourceListResult;
import luluteam.bath.bathprojectas.video.other.AccessTokenUtil;
import luluteam.bath.bathprojectas.video.other.EZOPENUtil;
import luluteam.bath.bathprojectas.video.other.VideoConstant;
import luluteam.bath.bathprojectas.view.dialog.LoadingDialog;

/**
 * 加载视频列表：
 * 可选参数：toiletId——只显示与当前厕所匹配的监控信息，如果不填，则默认显示全部监控信息
 * channelName 字段命名规则： ToiletId-名称-男/女/残卫
 */
public class VideoMainActivity extends BaseActivity {

    private ListView lv_videoList;
    private ImageView iv_video_setting;

    private ArrayAdapter<String> videListAdapter;
    private CameraListResult cameraListResult;
    private SwipeRefreshLayout refreshLayout;
    private Toolbar toolbar_video_main;

    private String toiletId = null;

    public static final String EXTRA_NAME_TOILETID = "extra_name_toilet_id";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_main);
        initUI();
        initData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //加载并刷新accessToken
        AccessTokenUtil.initAccessToken(context, new AccessTokenUtil.AccessTokenCallback() {
            @Override
            public void onSuccess(String accessTokenJson) {
                loadVideoList(accessTokenJson);
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

    private void initUI() {
        lv_videoList = (ListView) findViewById(R.id.lv_videoList);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        iv_video_setting = (ImageView) findViewById(R.id.iv_video_setting);
        toolbar_video_main = (Toolbar) findViewById(R.id.toolbar_video_main);
        toolbar_video_main.setTitle("视频监控");
        this.setSupportActionBar(toolbar_video_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //加载并刷新accessToken
                AccessTokenUtil.initAccessToken(context, new AccessTokenUtil.AccessTokenCallback() {
                    @Override
                    public void onSuccess(String accessTokenJson) {
                        loadVideoList(accessTokenJson);
                    }
                });
                refreshLayout.setRefreshing(false);
            }
        });

        iv_video_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSettingDialog();
            }
        });

    }

    private void initData() {
        toiletId = getIntent().getStringExtra(EXTRA_NAME_TOILETID);
        videListAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1);
        lv_videoList.setAdapter(videListAdapter);
        lv_videoList.setOnItemClickListener(new MyOnItemClickListener());
    }


    /**
     * 加载摄像头列表
     */
    private void loadVideoList(String accessToken) {
        LoadingDialog loadingDialog = new LoadingDialog(context);
        loadingDialog.show();
        HashMap<String, String> params = new HashMap<>();
        params.put("accessToken", accessToken);
        OkHttpManager.CommonPostAsyn(VideoConstant.getCameraList(), params, new OkHttpManager.ResultCallback() {
            @Override
            public void onCallBack(final OkHttpManager.State state, final String result) {
                Log.i(TAG, "获取Camera列表： " + VideoConstant.getCameraList() + " params:" + params
                        + "  result:" + result);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadingDialog.dismiss();
                        if (state == OkHttpManager.State.SUCCESS) {
                            //TODO tmpRes --> result
                            cameraListResult = new Gson().fromJson(result, CameraListResult.class);
                            refreshVideoList(cameraListResult);
                        } else {
                            ToastUtil.logAndToast(context, "获取设备列表失败：" + result);
                        }
                    }
                });

            }
        });
    }


    /**
     * 更新列表的显示
     *
     * @param cameraListResult
     */
    private void refreshVideoList(CameraListResult cameraListResult) {
        videListAdapter.clear();
        List<CameraListResult.Item> list = cameraListResult.getData();
        if (StringUtils.isNotEmpty(toiletId)) {
            Iterator<CameraListResult.Item> it = list.iterator();
            while (it.hasNext()) {
                CameraListResult.Item item = it.next();
                if (!item.getChannelName().contains(toiletId)) {
                    it.remove();
                }
            }
        }
        videListAdapter.addAll(cameraListResult.getFormattedInfoList());
        videListAdapter.notifyDataSetChanged();
    }

    /**
     * 获取RTMP、HLS播放源，
     * 用于H5页面的播放
     *
     * @param item
     */
    @Deprecated
    private void loadPlayUrl(CameraListResult.Item item) {
        String GET_LIVE_ADDRESS = "https://open.ys7.com/api/lapp/live/address/get";
        HashMap<String, String> params = new HashMap<>();
        params.put("accessToken", "");
        params.put("source", item.getDeviceSerial() + ":" + item.getChannelNo());
        OkHttpManager.CommonPostAsyn(GET_LIVE_ADDRESS, params, new OkHttpManager.ResultCallback() {
            @Override
            public void onCallBack(final OkHttpManager.State state, final String result) {
                Log.i(TAG, "获取RTMP、HLS播放源: " + result);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (state == OkHttpManager.State.SUCCESS) {

                            SourceListResult sourceListResult = new Gson().fromJson(result, SourceListResult.class);
                            if (sourceListResult.getData() != null && sourceListResult.getData().size() > 0) {
                                showPrePlayDialog(sourceListResult.getData().get(0));
                            } else {
                                //toast 获取播放源失败
                            }

                        } else {
                            //toast 获取播放源失败
                        }
                    }
                });
            }
        });


    }

    /**
     * 弹出dialog，提示是否播放
     * RTMP\HLS 播放源
     */
    @Deprecated
    private void showPrePlayDialog(SourceListResult.Item item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        if (item.getStatus() == 1) {
            builder.setTitle("观看直播")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

        } else {
            builder.setTitle("设备异常，请检查:" + item.getStatus());
        }

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();


    }


    /**
     * 弹出提示框
     *
     * @param msg
     */
    private void showTipDialog(String msg) {
        new AlertDialog.Builder(context)
                .setTitle("提示")
                .setMessage(msg + "\n")
                .show();
    }

    /**
     * 点击设置按钮
     * 目前仅仅显示accessToken
     */
    private void showSettingDialog() {
        new AlertDialog.Builder(context)
                .setTitle("AccessToken")
                .setMessage(AccessTokenUtil.getSavedAccessToken(context) + "\n")
                .setPositiveButton("遇到问题，刷新一下", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AccessTokenUtil.getAccessTokenByNetwork(context, new AccessTokenUtil.AccessTokenCallback() {
                            @Override
                            public void onSuccess(String accessToken) {
                                ToastUtil.logAndToast(context, "刷新成功");
                                dialog.dismiss();
                            }
                        });
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }


    /**
     * 点击ListView，弹出Dialog
     */
    private class MyOnItemClickListener implements AdapterView.OnItemClickListener {

        private static final String offlineMsg = "设备已离线，请检查摄像头网络连接。\n\n刷新后重试。";
        private static final String isEncryptMsg = "设备已加密，请通知管理员关闭加密选项再观看。";

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if (cameraListResult == null) {
                return;
            }
            CameraListResult.Item item = cameraListResult.getData().get(i);
            if (item.getStatus() != 1) {
                showTipDialog(offlineMsg);
                return;
            }
            if (item.getIsEncrypt() != 0) {
                showTipDialog(isEncryptMsg);
                return;
            }

            //检查完毕，可以播放视频。
            // 必须使用EZOPEN协议
            PlayActivity.startPlayActivity(context, VideoConstant.Config.APPKEY, AccessTokenUtil.getSavedAccessToken(context),
                    EZOPENUtil.getLiveUrl(item.getDeviceSerial(), item.getChannelNo(), false));

//            PlayBackActivity.startPlayBackActivity(context,VideoConstant.Config.APPKEY, AccessTokenUtil.getSavedAccessToken(context),
//                    EZOPENUtil.getRecUrl(item.getDeviceSerial(), item.getChannelNo()));
        }
    }


}
