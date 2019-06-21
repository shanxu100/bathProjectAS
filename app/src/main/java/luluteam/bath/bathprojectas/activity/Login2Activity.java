package luluteam.bath.bathprojectas.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.Gson;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;

import luluteam.bath.bathprojectas.R;
import luluteam.bath.bathprojectas.constants.APPConstant;
import luluteam.bath.bathprojectas.constants.WebConstant;
import luluteam.bath.bathprojectas.model.result.LoginResult;
import luluteam.bath.bathprojectas.model.result.UserResult;
import luluteam.bath.bathprojectas.utils.SharedPreferencesUtil;
import luluteam.bath.bathprojectas.utils.ToastUtil;
import luluteam.bath.bathprojectas.utils.okhttp.OkHttpManager;
import luluteam.bath.bathprojectas.view.dialog.ChooseIPDialog;
import luluteam.bath.bathprojectas.view.dialog.LoadingDialog;
import luluteam.bath.bathprojectas.view.dialog.TipDialog;

/**
 * A login screen that offers login via email/password.
 */
public class Login2Activity extends BaseActivity {


    // UI references.
    private EditText nickname_et;
    private EditText password_et;
    private CheckBox cb_rememberPwd;
    private Button login_btn;
    private ImageView setting_btn;

    private boolean singleLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        singleLogin = getIntent().getBooleanExtra("singleLogin", false);
        Log.e(TAG, "singleLogin=" + singleLogin);
        initUI();

        initIP();
        loadSavedNicknamePwd();

    }

    private void initUI() {
        nickname_et = (EditText) findViewById(R.id.username_et);
        password_et = (EditText) findViewById(R.id.password_et);
        cb_rememberPwd = (CheckBox) findViewById(R.id.cb_rememberPwd);
        login_btn = (Button) findViewById(R.id.login_btn);
        setting_btn = (ImageView) findViewById(R.id.setting_btn);

        login_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin(nickname_et.getText().toString().trim(), password_et.getText().toString().trim());
            }
        });
        setting_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseIPDialog dialog = new ChooseIPDialog(context);
                String checkedId = SharedPreferencesUtil.getString(context, APPConstant.SharedPreferenceStr.CHECKED_SERVER);
                if (!StringUtils.isEmpty(checkedId)) {
                    dialog.setCheckedId(Integer.parseInt(checkedId));
                }
                dialog.setOnPositiveListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int checkedId = dialog.getCheckedId();
                        updateIP(checkedId + "");
                        dialog.dismiss();
                    }
                });
                dialog.show();

            }
        });

        if (singleLogin) {
            TipDialog tipDialog = new TipDialog(context);
            tipDialog.setMessage("该账号已经下线");
            tipDialog.setClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tipDialog.dismiss();
                }
            });
            tipDialog.show();
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin(String nickname, String password) {
        if (StringUtils.isEmpty(nickname) || StringUtils.isEmpty(password)) {
            ToastUtil.showLongToast(context, "用户名和密码不能为空");
            return;
        }
        if (!WebConstant.isHostAvailable()) {
            ToastUtil.showLongToast(context, "请选择服务器");
            return;
        }

        LoadingDialog loadingDialog = new LoadingDialog(context);
        loadingDialog.setLoadingText("正在登录……");
        loadingDialog.show();

        HashMap<String, String> params = new HashMap<>();
        params.put("nickname", nickname);
        params.put("password", password);
        params.put("token", APPConstant.ANDROID_ID);
//        String url = "http://125.216.242.147:8080/bathProject/doLogin";
        OkHttpManager.CommonPostAsyn(WebConstant.LOGIN, params, new OkHttpManager.ResultCallback() {
            @Override
            public void onCallBack(OkHttpManager.State state, String result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadingDialog.dismiss();
                        if (state == OkHttpManager.State.SUCCESS) {
                            LoginResult loginResult = new Gson().fromJson(result, LoginResult.class);
                            if (loginResult.isResult()) {
                                succeedLogin(nickname, password);
                            } else {
                                ToastUtil.showLongToast(context, loginResult.getReason());
                            }
                        } else {
                            ToastUtil.showLongToast(context, "登录失败:" + result);
                        }
                    }
                });
            }
        });


    }

    private void initIP() {
        String checkedId = SharedPreferencesUtil.getString(context, APPConstant.SharedPreferenceStr.CHECKED_SERVER);
        if (StringUtils.isEmpty(checkedId)) {
            setting_btn.performClick();
        } else {
            updateIP(checkedId);
        }

    }

    private void updateIP(String checkedId) {
        SharedPreferencesUtil.putString(context, APPConstant.SharedPreferenceStr.CHECKED_SERVER, checkedId);
        int id = Integer.parseInt(checkedId);
        if (id == R.id.testServer_rb) {
            WebConstant.WEBHOST = WebConstant.ServerSet.testWebHost;
            WebConstant.WEB_SOCKET = WebConstant.ServerSet.testWebSocket;
            WebConstant.MQTT_BROKER = WebConstant.ServerSet.testMqttBroker;

        } else if (id == R.id.claudyServer_rb) {
            WebConstant.WEBHOST = WebConstant.ServerSet.claudyWebHost;
            WebConstant.WEB_SOCKET = WebConstant.ServerSet.claudyWebSocket;
            WebConstant.MQTT_BROKER = WebConstant.ServerSet.claudyMqttBroker;

        } else if (id == R.id.aliCloudServer_rb) {
            WebConstant.WEBHOST = WebConstant.ServerSet.aliWebHost;
            WebConstant.WEB_SOCKET = WebConstant.ServerSet.aliWebSocket;
            WebConstant.MQTT_BROKER = WebConstant.ServerSet.aliMqttBroker;
        } else if (id == R.id.testServer_rb2) {
            WebConstant.WEBHOST = WebConstant.ServerSet.testWebHost2;
            WebConstant.WEB_SOCKET = WebConstant.ServerSet.testWebSocket2;
            WebConstant.MQTT_BROKER = WebConstant.ServerSet.testMqttBroker;

        }
        WebConstant.update();
        ToastUtil.showLongToast(context, "当前服务器：" + WebConstant.WEBHOST + "  " + WebConstant.WEB_SOCKET, Gravity.BOTTOM);
    }


    private void loadSavedNicknamePwd() {
        String savedNickname = SharedPreferencesUtil.getString(context, APPConstant.SharedPreferenceStr.SAVED_NICKNAME);
        String savedPwd = SharedPreferencesUtil.getString(context, APPConstant.SharedPreferenceStr.SAVED_PWD);
        if (StringUtils.isNotEmpty(savedNickname) && StringUtils.isNotEmpty(savedPwd)) {
            nickname_et.setText(savedNickname);
            password_et.setText(savedPwd);
        }
    }

    private void saveNicknamePwd(String nickname, String password) {
        SharedPreferencesUtil.putString(context, APPConstant.SharedPreferenceStr.SAVED_NICKNAME, nickname);
        SharedPreferencesUtil.putString(context, APPConstant.SharedPreferenceStr.SAVED_PWD, password);

    }


    private void succeedLogin(String nickname, String password) {

        //TODO 1、加载用户信息，并保存；2、打开新的Activity
        //        APPConstant. = username;
        if (cb_rememberPwd.isChecked()) {
            saveNicknamePwd(nickname, password);
        }
        LoadingDialog loadingDialog = new LoadingDialog(context);
        loadingDialog.setLoadingText("正在加载用户信息……");
        loadingDialog.show();

        HashMap<String, String> params = new HashMap<>();
        params.put("nickname", nickname);
        OkHttpManager.CommonPostAsyn(WebConstant.GET_USER_INFO_BY_NICKNAME, params, new OkHttpManager.ResultCallback() {
            @Override
            public void onCallBack(OkHttpManager.State state, String result) {
                Log.i(TAG, "加载用户信息: " + result);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadingDialog.dismiss();
                        if (state == OkHttpManager.State.SUCCESS) {
                            Log.d(TAG, result);
                            UserResult userResult = new Gson().fromJson(result, UserResult.class);
                            if (userResult.isResult() && userResult.getUserInfoList() != null && userResult.getUserInfoList().size() > 0) {
                                UserResult.UserInfo userInfo = userResult.getUserInfoList().get(0);

                                // 登录期限少于5天，弹框提醒
                                if (userInfo.getExpirationTime() != -1 && userInfo.getExpirationTime() - System.currentTimeMillis() < 5 * 24 * 3600 * 1000) {
                                    ToastUtil.logAndToast(getApplicationContext(), "该用户有效时间不足5天！");
                                }

                                //TODO 保留该字段，因为使用该字段的场景太多，无法全部修改
                                APPConstant.USERNAME = userInfo.getUsername();
                                APPConstant.USERINFO = userInfo;
                                ToastUtil.showLongToast(context, "加载用户信息成功");
                                loadPersonalInfo(userInfo.getUsername());
                                Intent intent = new Intent(context, Main2Activity.class);
                                startActivity(intent);
                                Login2Activity.this.finish();

                            } else {
                                ToastUtil.logAndToast(context, "加载用户信息失败");
                            }
                        }
                    }
                });

            }
        });


    }

    private void loadPersonalInfo(String username) {
        String toiletId = SharedPreferencesUtil.getString(this, username + "_toiletId");
        String detail = SharedPreferencesUtil.getString(this, username + "_detail");
        String toiletVersion = SharedPreferencesUtil.getString(this, username + "_toiletVersion");
        String toiletNickname = SharedPreferencesUtil.getString(this, username + "_toiletNickname");


        APPConstant.TOILETID = (StringUtils.isEmpty(toiletId)) ? "" : toiletId;
        APPConstant.DETAIL = (StringUtils.isEmpty(detail)) ? "" : detail;
        APPConstant.TOILET_VERSION = (StringUtils.isEmpty(toiletVersion)) ? 1 : Integer.parseInt(toiletVersion);
        APPConstant.TOILET_NICKNAME = (StringUtils.isEmpty(toiletNickname)) ? "" : toiletNickname;
    }


}

