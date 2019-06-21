package luluteam.bath.bathprojectas.video.other;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;

import luluteam.bath.bathprojectas.utils.SharedPreferencesUtil;
import luluteam.bath.bathprojectas.utils.ToastUtil;
import luluteam.bath.bathprojectas.utils.okhttp.OkHttpManager;
import luluteam.bath.bathprojectas.video.model.AccessTokenResult;


/**
 * @author Guan
 * @date Created on 2018/6/3
 */
public class AccessTokenUtil {

    private static final String TAG = "EZOPENUtil";

    /**
     * 距离超时前 FINAL_TIME 时间
     * 单位：小时
     */
    private static final int FINAL_TIME = 3;


    public static void initAccessToken(Context context, AccessTokenCallback callback) {
        String savedAccessToken = getSavedAccessToken(context);
        if (StringUtils.isEmpty(savedAccessToken)) {
            getAccessTokenByNetwork(context, callback);
        } else {
            callback.onSuccess(savedAccessToken);
        }
    }

    /**
     * 获取存储的accessToken
     * 如果accessToken
     *
     * @return
     */
    public static String getSavedAccessToken(Context context) {
        String savedJson = SharedPreferencesUtil.getString(context, AccessTokenResult.SAVED_ACCESSTOKEN);
        Log.i(TAG, "获取本地保存的accessToken===" + savedJson);
        if (StringUtils.isNotEmpty(savedJson)) {
            AccessTokenResult accessTokenResult = new Gson().fromJson(savedJson, AccessTokenResult.class);
            long now = System.currentTimeMillis();
            if (accessTokenResult.getData().getExpireTime() - now > FINAL_TIME * 60 * 60 * 1000) {
                return accessTokenResult.getData().getAccessToken();
            } else {
                //TODO toast
                Log.i(TAG, "accessToken距离过期时间不足 " + FINAL_TIME + " 小时。请刷新更新accessToken");
            }
        }
        return "";
    }

    /**
     * 通过网络获取accessToken
     */
    public static void getAccessTokenByNetwork(Context context, AccessTokenCallback callback) {
        //通过网络获取AccessToken
        Log.i(TAG, "通过网络获取最新accessToken====getAccessTokenByNetwork");
        HashMap<String, String> params = new HashMap<>();
        params.put("appKey", VideoConstant.Config.APPKEY);
        params.put("appSecret", VideoConstant.Config.SECRET);
        OkHttpManager.CommonPostAsyn(VideoConstant.getAccessToken(), params, new OkHttpManager.ResultCallback() {
            @Override
            public void onCallBack(final OkHttpManager.State state, final String result) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (state == OkHttpManager.State.SUCCESS) {
                            AccessTokenResult accessTokenResult = new Gson().fromJson(result, AccessTokenResult.class);
                            if (accessTokenResult.getData() != null) {
                                //保存
                                SharedPreferencesUtil.putString(context, AccessTokenResult.SAVED_ACCESSTOKEN, result);
                                if (callback != null) {
                                    callback.onSuccess(accessTokenResult.getData().getAccessToken());
                                }
                                Log.i(TAG, "获取accessToken成功:" + result);
                            }
                        } else {
                            ToastUtil.logAndToast(context, "获取AccessToken失败:" + result);
                        }
                    }
                });

            }
        });

    }

    public interface AccessTokenCallback {
        void onSuccess(String accessToken);
    }
}
