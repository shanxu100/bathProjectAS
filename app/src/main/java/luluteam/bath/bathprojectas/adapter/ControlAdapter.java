package luluteam.bath.bathprojectas.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import luluteam.bath.bathprojectas.R;
import luluteam.bath.bathprojectas.constants.APPConstant;
import luluteam.bath.bathprojectas.constants.WebConstant;
import luluteam.bath.bathprojectas.fragment.main.ControlFragment;
import luluteam.bath.bathprojectas.model.RemoteControl.Devices;
import luluteam.bath.bathprojectas.utils.ToastUtil;
import luluteam.bath.bathprojectas.utils.okhttp.OkHttpManager;
import luluteam.bath.bathprojectas.view.dialog.LoadingDialog;

import static luluteam.bath.bathprojectas.fragment.main.ControlFragment.DIALOG_TIME_DELAY;


/**
 * Created by Guan on 2018/1/5.
 */

public class ControlAdapter extends BaseAdapter {

    private Devices devices;
    private boolean allDevicesIsCtrl = false;//远程控制或者禁止远程控制的总开关

    private static String TAG = "ControlAdapter";

    /**
     * 用于加载数据时候的一个缓冲
     */
    private LoadingDialog loadingDialog;

    /**
     * 专门用于等待一键冲洗 true-->false后，等待 门设备 记录的
     */
    private LoadingDialog floorWashLoadingDialog;


    private Context context;

    public boolean isAllDevicesIsCtrl() {
        return allDevicesIsCtrl;
    }

    public void setAllDevicesIsCtrl(boolean allDevicesIsCtrl) {
        this.allDevicesIsCtrl = allDevicesIsCtrl;
    }

    public ControlAdapter(Devices devices, Context context) {
        this.devices = devices;
        this.context = context;
        loadingDialog = new LoadingDialog(context);
        floorWashLoadingDialog = new LoadingDialog(context);
        floorWashLoadingDialog.setLoadingText("等待门设备状态，请稍后...");
        floorWashLoadingDialog.setCanceledOnTouchOutside(false);
    }

    /**
     * 通知数据实时改变
     *
     * @param allControlled 是否远程控制
     */
    public void notifyDataChange(Devices devices, boolean allControlled) {
        this.allDevicesIsCtrl = allControlled;
        this.devices = devices;
//        System.err.println("===notifyDataChange===");
        this.notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return devices.getDevices().size();
    }

    @Override
    public Object getItem(int position) {
        return devices.getDevices().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_remote_control_item, null);
            holder.deviceName_tv = (TextView) convertView.findViewById(R.id.deviceName_tv);
            holder.switch1_tb = (ToggleButton) convertView.findViewById(R.id.switch1_tb);
            holder.switch2_tb = (ToggleButton) convertView.findViewById(R.id.switch2_tb);
            holder.meterValue_tv = (TextView) convertView.findViewById(R.id.meterValue_tv);
            holder.switch1_ll = (LinearLayout) convertView.findViewById(R.id.switch1_ll);
            holder.switch2_ll = (LinearLayout) convertView.findViewById(R.id.switch2_ll);
            holder.flushBath_btn = (Button) convertView.findViewById(R.id.flushBath_btn);
            holder.flushBath_ll = convertView.findViewById(R.id.flushBath_ll);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        initViews(holder, position);
        return convertView;
    }

    /**
     * 远程控制的ViewHolder 内部类
     */
    public class ViewHolder {
        public TextView deviceName_tv;
        public ToggleButton switch1_tb;
        public ToggleButton switch2_tb;
        public TextView meterValue_tv;
        public Button flushBath_btn;
        public View flushBath_ll;

        public LinearLayout switch1_ll, switch2_ll;

    }

    private void initViews(ViewHolder holder, int position) {
        String deviceName = devices.getDevices().get(position).getName();
        holder.deviceName_tv.setText(deviceName);
//        Log.e(TAG, "更新设备：" + deviceName+"position:"+position);

        if (deviceName.equals("光照(LUX)")) {
            holder.switch1_ll.setVisibility(View.GONE);
            holder.switch2_ll.setVisibility(View.GONE);
            holder.meterValue_tv.setVisibility(View.VISIBLE);
            holder.flushBath_ll.setVisibility(View.GONE);
            int meterValue = devices.getDevices().get(position).getValue();
            if (meterValue >= 0 && meterValue <= 999999) {
                holder.meterValue_tv.setText(meterValue + "");
//                Log.e(TAG, "设置光照探头值:" + meterValue + "");
            }
        } else if (deviceName.equals("一键冲洗")) {
            holder.switch1_ll.setVisibility(View.GONE);
            holder.switch2_ll.setVisibility(View.GONE);
            holder.meterValue_tv.setVisibility(View.GONE);
            holder.flushBath_ll.setVisibility(View.VISIBLE);
            if (devices.getDevices().get(position).isAction()) {
                holder.flushBath_btn.setText("正在运行...点击停止");
            } else {
                holder.flushBath_btn.setText("点击启动");
            }
            holder.flushBath_btn.setOnClickListener(new View.OnClickListener() {//一键冲洗
                @Override
                public void onClick(View view) {
                    if (allDevicesIsCtrl) {
                        new AlertDialog.Builder(context)
                                .setMessage("操作前请先刷新整体状态，并确认厕所是否有人")
                                .setTitle("危险")
                                .setPositiveButton("确认操作", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
//                                        view.setClickable(true);
                                        if (!checkWholeState()) {
                                            return;
                                        }
                                        loadingDialog.showWithTimeout(DIALOG_TIME_DELAY);
                                        String cmd = ("点击启动".equals(holder.flushBath_btn.getText()) ? "true" : "false");
                                        HashMap<String, String> params = new HashMap<>();
                                        params.put("toiletId", APPConstant.TOILETID);
                                        params.put("usage", APPConstant.UsageMap.get(devices.getUsage()));
                                        params.put("deviceType", APPConstant.deviceTypeMap.get(devices.getDevices().get(position).getName()));
                                        params.put("cmd", cmd);
                                        params.put("username", APPConstant.USERNAME);
                                        //当一键冲洗设备处于running状态的时候，开启一个加载对话框，5秒超时取消
                                        //或者门设备处于开启状态时取消
                                        if (cmd.equals("false")) {
                                            floorWashLoadingDialog.showWithTimeout(6000);
                                        }
                                        OkHttpManager.CommonPostAsyn(WebConstant.SEND_CMD, params, new OkHttpManager.ResultCallback() {
                                            @Override
                                            public void onCallBack(OkHttpManager.State state, String result) {
                                                try {
                                                    JSONObject jsonObject = new JSONObject(result);
                                                    if (!(Boolean) jsonObject.get("result")) {
                                                        String reason = (String) jsonObject.get("reason");
                                                        ((Activity) context).runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                ToastUtil.showShortToast(context, reason);
                                                            }
                                                        });
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                        dialog.dismiss();
                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).create().show();
//
                    } else {
                        ToastUtil.showShortToast(view.getContext(), "请打开远程控制总开关!");
                    }
                }
            });
        } else {
            holder.meterValue_tv.setVisibility(View.GONE);
            holder.flushBath_ll.setVisibility(View.GONE);
            holder.switch1_ll.setVisibility(View.VISIBLE);
            holder.switch2_ll.setVisibility(View.VISIBLE);
//                setIconOff_ON(getName(position), holder.item_icon, getStatus(position) == 1 ? true : false);//设置ImageView的状态，设备状态
            holder.switch1_tb.setChecked(devices.getDevices().get(position).isAction() ? true : false);
            holder.switch1_tb.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        if (allDevicesIsCtrl) {
                            loadingDialog.showWithTimeout(DIALOG_TIME_DELAY);
                            HashMap<String, String> params = new HashMap<>();
                            params.put("toiletId", APPConstant.TOILETID);
                            params.put("usage", APPConstant.UsageMap.get(devices.getUsage()));
                            params.put("deviceType", APPConstant.deviceTypeMap.get(devices.getDevices().get(position).getName()));
                            params.put("cmd", devices.getDevices().get(position).isAction() ? "false" : "true");
                            params.put("username", APPConstant.USERNAME);
                            OkHttpManager.CommonPostAsyn(WebConstant.SEND_CMD, params, new OkHttpManager.ResultCallback() {
                                @Override
                                public void onCallBack(OkHttpManager.State state, String result) {
                                    if (state != OkHttpManager.State.SUCCESS) {
                                        ((Activity) context).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                ToastUtil.showShortToast(context, "操作失败：" + result);

                                            }
                                        });
                                    } else {
                                        try {
                                            JSONObject jsonObject = new JSONObject(result);
                                            if ((Boolean) jsonObject.get("result") == false) {
                                                String reason = (String) jsonObject.get("reason");
                                                ((Activity) context).runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        ToastUtil.showShortToast(context, reason);
                                                    }
                                                });
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            });
//                            new Timer().schedule(new TimerTask() {
//                                @Override
//                                public void run() {
//                                    loadingDialog.dismiss();
//                                }
//                            }, DIALOG_TIME_DELAY);

                        } else {
                            ToastUtil.showShortToast(view.getContext(), "请打开远程控制总开关!");
                        }
                    }
                    return true;
                }
            });
            holder.switch2_tb.setChecked(devices.getDevices().get(position).isControlled());//设置ToggleButton的状态，远程控制子开关
            holder.switch2_tb.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        if (allDevicesIsCtrl) {
                            loadingDialog.showWithTimeout(DIALOG_TIME_DELAY);
                            HashMap<String, String> params = new HashMap<>();
                            params.put("toiletId", APPConstant.TOILETID);
                            params.put("usage", APPConstant.UsageMap.get(devices.getUsage()));
                            params.put("deviceType", APPConstant.deviceTypeMap.get(devices.getDevices().get(position).getName()));
                            params.put("action", devices.getDevices().get(position).isControlled() ? "false" : "true");
                            params.put("username", APPConstant.USERNAME);
                            OkHttpManager.CommonPostAsyn(WebConstant.INTO_OR_EXIT_REMOTE_CTRL, params, new OkHttpManager.ResultCallback() {
                                @Override
                                public void onCallBack(OkHttpManager.State state, String result) {
                                    if (state != OkHttpManager.State.SUCCESS) {
                                        ((Activity) context).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                ToastUtil.showShortToast(context, "操作失败：" + result);

                                            }
                                        });
                                    } else {
                                        try {
                                            JSONObject jsonObject = new JSONObject(result);
                                            if ((Boolean) jsonObject.get("result") == false) {
                                                String reason = (String) jsonObject.get("reason");
                                                ((Activity) context).runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        ToastUtil.showShortToast(context, reason);
                                                    }
                                                });
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            });
                        } else {
                            ToastUtil.showShortToast(view.getContext(), "请打开远程控制总开关!");
                        }
                    }
                    return true;
                }
            });

            //当收到 门设备 的消息时，如果floorWashLoadingDialog正在show，则取消该dialog
            if ("门设备".equals(deviceName) && floorWashLoadingDialog != null && floorWashLoadingDialog.isShowing()) {
                floorWashLoadingDialog.dismiss();
            }
        }

    }

    /**
     * 检查整体状态是否处于最新状态，如果上次刷新是在45秒之内，那么就判定为最新状态
     * 为了保持上位机和下位机的设备状态一致
     *
     * @return
     */
    private boolean checkWholeState() {
        if (System.currentTimeMillis() - ControlFragment.wholeStateTimestamp < ControlFragment.INTERVAL) {
            return true;
        } else {
            ToastUtil.showLongToast(context, "为保证数据一致性，请先刷新整体状态，再操作");
            return false;
        }
    }

}
