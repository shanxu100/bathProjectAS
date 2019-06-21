package luluteam.bath.bathprojectas.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import luluteam.bath.bathprojectas.R;
import luluteam.bath.bathprojectas.app.App;
import luluteam.bath.bathprojectas.constants.APPConstant;
import luluteam.bath.bathprojectas.constants.WebConstant;
import luluteam.bath.bathprojectas.model.result.UserResult;
import luluteam.bath.bathprojectas.tools.JumpHelper;
import luluteam.bath.bathprojectas.utils.ToastUtil;
import luluteam.bath.bathprojectas.utils.okhttp.OkHttpManager;
import luluteam.bath.bathprojectas.view.dialog.ChangePwdDialog;
import luluteam.bath.bathprojectas.view.dialog.LoadingDialog;

public class AboutActivity extends BaseActivity {

    private LinearLayout personalInfo_ll;
    private LinearLayout changePwd_ll;
    private LinearLayout checkUpdate_ll;

    private TextView version_tv;
    private Toolbar include_toolbar;

    private UserResult userResult = null;
    private UserResult.UserInfo userInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        initUI();
        initData();

    }


    private void initUI() {
        personalInfo_ll = (LinearLayout) findViewById(R.id.personalInfo_ll);
        changePwd_ll = (LinearLayout) findViewById(R.id.changePwd_ll);
        checkUpdate_ll = (LinearLayout) findViewById(R.id.checkUpdate_ll);
        version_tv = (TextView) findViewById(R.id.version_tv);
        include_toolbar = (Toolbar) findViewById(R.id.include_toolbar);
        include_toolbar.setTitle("关于");
        this.setSupportActionBar(include_toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        version_tv.setText("version name: " + App.getVersionName() + "\n\nversion code: " + App.getVersionCode());

        personalInfo_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.showLongToast(context, userInfo.toString());
            }
        });

        changePwd_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userInfo != null) {
                    attempChangePwd();
                }
            }
        });

        checkUpdate_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(context, UpdateActivity.class);
//                startActivity(intent);
                JumpHelper.gotoUpdateActivity(context);

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


        HashMap<String, String> params = new HashMap<>();
        if (APPConstant.USERINFO == null) {
            LoadingDialog dialog = new LoadingDialog(context);
            dialog.show();
            //从网络加载用户信息
            params.put("username", APPConstant.USERNAME);
            OkHttpManager.CommonPostAsyn(WebConstant.GET_USER_INFO, params, new OkHttpManager.ResultCallback() {
                @Override
                public void onCallBack(OkHttpManager.State state, String result) {
                    dialog.dismiss();
                    Log.i(TAG, "加载用户信息: " + result);
                    if (state == OkHttpManager.State.SUCCESS) {
                        userResult = new Gson().fromJson(result, UserResult.class);
                        if (userResult.isResult()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    userInfo = userResult.getUserInfoList().get(0);
                                    ToastUtil.showLongToast(context, "加载用户信息成功");
                                }
                            });
                        }
                    }
                }
            });
        } else {
            userInfo = APPConstant.USERINFO;
        }

    }


    private void attempChangePwd() {
        ChangePwdDialog dialog = new ChangePwdDialog(context);
        dialog.setRealOldPwd(userInfo.getPassword());
        dialog.setChangePwdCallback(new ChangePwdDialog.ChangePwdCallback() {
            @Override
            public void onChange(String oldPwd, String newPwd) {
                changePwd(dialog.getOldPwd(), dialog.getNewPwd());
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    /**
     * 更新密码
     *
     * @param oldPwd
     * @param newPwd
     */
    private boolean changePwd(String oldPwd, String newPwd) {
        if (StringUtils.isEmpty(oldPwd) || StringUtils.isEmpty(newPwd)) {
            ToastUtil.showLongToast(context, "输入不合法，请检查");
            return false;
        }
        LoadingDialog dialog = new LoadingDialog(context);
        dialog.show();
        HashMap<String, String> params = new HashMap<>();
        params.put("username", userInfo.getUsername());
        params.put("password", newPwd);
        OkHttpManager.CommonPostAsyn(WebConstant.UPDATE_USER_PWD, params, new OkHttpManager.ResultCallback() {
            @Override
            public void onCallBack(OkHttpManager.State state, String result) {
                dialog.dismiss();
                if (state == OkHttpManager.State.SUCCESS) {
                    try {
                        JSONObject object = new JSONObject(result);
                        if (object.getBoolean("result")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    userInfo = userResult.getUserInfoList().get(0);
                                    ToastUtil.showLongToast(context, "密码修改成功");
                                    userInfo.setPassword(newPwd);
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return true;


    }


}
