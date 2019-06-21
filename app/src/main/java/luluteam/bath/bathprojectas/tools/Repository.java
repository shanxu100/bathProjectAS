package luluteam.bath.bathprojectas.tools;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.List;

import luluteam.bath.bathprojectas.constants.APPConstant;
import luluteam.bath.bathprojectas.constants.WebConstant;
import luluteam.bath.bathprojectas.model.ToiletInfo;
import luluteam.bath.bathprojectas.model.pit.Bus485Pit;
import luluteam.bath.bathprojectas.model.pit.ToiletDevice;
import luluteam.bath.bathprojectas.model.pit.ToiletDeviceResult;
import luluteam.bath.bathprojectas.utils.GsonUtil;
import luluteam.bath.bathprojectas.utils.ToastUtil;
import luluteam.bath.bathprojectas.utils.okhttp.OkHttpManager;

/**
 * @author Guan
 * @date Created on 2018/9/21
 */
public class Repository {
    private static final String TAG = "Repository";
    private static final Handler handler = new Handler(Looper.getMainLooper());

    /**
     * 请求公共数据，更新 Appconstant.itemCommon
     */
    public static void getItemCommon(Activity activity, String toiletId, DataCallback dataCallback) {
        HashMap<String, String> params = new HashMap<>();
        params.put("toiletId", toiletId);
        OkHttpManager.CommonPostAsyn(WebConstant.GET_TOILET_DEVICE_NUM, params, new OkHttpManager.ResultCallback() {
            @Override
            public void onCallBack(OkHttpManager.State state, String result) {
                Log.i(TAG, "刷新公共数据:Params=" + params + "  result=" + result);
                if (state == OkHttpManager.State.SUCCESS) {
                    ToiletDeviceResult deviceResult = GsonUtil.fromJson(result, ToiletDeviceResult.class);
                    List<ToiletDevice> deviceList = deviceResult.getDataList();

                    onDataCallback(activity, dataCallback, deviceList.get(0).getItemCommon());
                }
            }
        });
    }

    /**
     * 请求厕所坑位的信息
     *
     * @param toiletId
     * @param index    index=0 : 请求公共数据
     *                 index=1 ： 56+56 有人无人、正常异常、清洁度等级、气味等级
     *                 index=2  ：蹲位详情
     *                 index=3 ： 气味详情
     */
    public static void requestPit(Context context, String toiletId, int index) {
        requestPitWithDelay(context, toiletId, index, 0);
    }

    public static void requestPitWithDelay(Context context, String toiletId, int index, int delay) {
        HashMap<String, String> params = new HashMap<>();
        params.put("toiletId", toiletId);
        if (index != -1) {
            params.put("index", index + "");
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                OkHttpManager.CommonPostAsyn(WebConstant.REQUEST_PIT, params, new OkHttpManager.ResultCallback() {
                    @Override
                    public void onCallBack(OkHttpManager.State state, String result) {
                        Log.i(TAG, "请求坑位数据:" + WebConstant.REQUEST_PIT + " params:" + params.toString());
                        if (state != OkHttpManager.State.SUCCESS) {
                            ToastUtil.logAndToast(context, "发送失败：" + result);
                        } else {
                            ToastUtil.showShortToast(context, "发送成功");
                        }
                    }
                });
            }
        }, delay * 1000);

    }

    public static void sendWeatherInfo(String toiletId, int delay) {
        HashMap<String, String> params = new HashMap<>();
        params.put("toiletId", toiletId);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                OkHttpManager.CommonPostAsyn(WebConstant.SEND_WEATHER_INFO, params, new OkHttpManager.ResultCallback() {
                    @Override
                    public void onCallBack(OkHttpManager.State state, String result) {
                        Log.i(TAG, "发送天气数据:" + WebConstant.SEND_WEATHER_INFO
                                + " params:" + params.toString() + " result:" + result);
                    }
                });
            }
        }, delay * 1000);
    }

    public static void sendToiletBasicInfo(String toiletId, int delay) {
        HashMap<String, String> params = new HashMap<>();
        params.put("toiletId", toiletId);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                OkHttpManager.CommonPostAsyn(WebConstant.SEND_TOILET_BASIC_INFO, params, new OkHttpManager.ResultCallback() {
                    @Override
                    public void onCallBack(OkHttpManager.State state, String result) {
                        Log.i(TAG, "发送天气厕所基础数据:" + WebConstant.SEND_TOILET_BASIC_INFO
                                + " params:" + params.toString() + " result:" + result);
                    }
                });
            }
        }, delay * 1000);
    }

    public static void getToiletItemInfoByToiletId(Activity activity, String toiletId, DataCallback callback) {
        HashMap<String, String> params = new HashMap<>();
        params.put("toiletId", toiletId);
        OkHttpManager.CommonPostAsyn(WebConstant.GET_TOILET_INFO_BY_TOILET_ID, params, new OkHttpManager.ResultCallback() {
            @Override
            public void onCallBack(OkHttpManager.State state, String result) {
                if (state == OkHttpManager.State.SUCCESS) {
                    Log.i(TAG, "根据toiletId查询厕所的信息+" + toiletId + "   查询到的json数据为：" + result);
                    ToiletInfo toiletInfo = GsonUtil.fromJson(result, ToiletInfo.class);
                    if (toiletInfo != null && toiletInfo.getToiletList() != null && toiletInfo.getToiletList().size() == 1
                            && StringUtils.isNotEmpty(toiletInfo.getToiletList().get(0).getToiletId())) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                callback.onData(toiletInfo.getToiletList().get(0));
                            }
                        });
                    }
                }
            }
        });
    }


    /**
     * 向采集卡发送命令，请求设备状态
     *
     * @param context
     * @param toiletId
     */
    public static void refreshDeviceState(Context context, String toiletId) {
        HashMap<String, String> params = new HashMap<>();
        params.put("toiletId", toiletId);
        OkHttpManager.CommonPostAsyn(WebConstant.GET_DEVICE_STATE, params, new OkHttpManager.ResultCallback() {
            @Override
            public void onCallBack(OkHttpManager.State state, String result) {
                Log.i(TAG, "向采集卡发送命令，请求设备状态:" + WebConstant.GET_DEVICE_STATE + " params:" + params.toString());
                if (state != OkHttpManager.State.SUCCESS) {
                    ToastUtil.logAndToast(context, "发送失败：" + result);
                } else {
                    ToastUtil.showShortToast(context, "发送成功");
                }
            }
        });
    }

    private static void onDataCallback(Activity activity, DataCallback dataCallback, Object... args) {
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    APPConstant.itemCommon = (Bus485Pit.ItemCommon) args[0];

                    if (dataCallback != null) {
                        dataCallback.onData(args);
                    }
                }
            });
        }
    }


    public interface DataCallback {
        /**
         * @param args
         */
        void onData(Object... args);
    }

}
