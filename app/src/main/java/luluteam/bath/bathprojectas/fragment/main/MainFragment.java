package luluteam.bath.bathprojectas.fragment.main;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lljjcoder.Interface.OnCityItemClickListener;
import com.lljjcoder.bean.CityBean;
import com.lljjcoder.bean.DistrictBean;
import com.lljjcoder.bean.ProvinceBean;
import com.lljjcoder.citywheel.CityConfig;
import com.lljjcoder.style.citypickerview.CityPickerView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import luluteam.bath.bathprojectas.R;
import luluteam.bath.bathprojectas.activity.SearchBathIdAty;
import luluteam.bath.bathprojectas.constants.APPConstant;
import luluteam.bath.bathprojectas.constants.WebConstant;
import luluteam.bath.bathprojectas.fragment.BaseFragment;
import luluteam.bath.bathprojectas.model.AllDeviceWorkInfo;
import luluteam.bath.bathprojectas.model.ToiletInfo;
import luluteam.bath.bathprojectas.tools.EventBusManager;
import luluteam.bath.bathprojectas.tools.JumpHelper;
import luluteam.bath.bathprojectas.tools.Repository;
import luluteam.bath.bathprojectas.utils.SharedPreferencesUtil;
import luluteam.bath.bathprojectas.utils.okhttp.OkHttpManager;
import luluteam.bath.bathprojectas.view.dialog.LoadingDialog;

/**
 * A simple {@link Fragment} subclass.
 *
 * @author Guan
 */
public class MainFragment extends BaseFragment {


    private ListView errorDevice_lv;

    private Button select_city_btn;
    private TextView currentCity_tv;

    private TableLayout hotCity_tablelayout;
    private ErrorDeviceAdapter errorDeviceAdapter;

    private Button refreshToiletBtn;
    private Button showDeviceDetailBtn;


    /**
     * 申明对象
     */
    private CityPickerView mPicker = new CityPickerView();

    private static final String TAG = "MainFragment";

    private List<AllDeviceWorkInfo> list = new ArrayList<>();
    private List<String> onLineToiletIdList = new ArrayList<>();


    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        initUI(view);
        initData();
        setEventBus(this, true);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        setEventBus(this, false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 100)
    public void onEventBusMessage(EventBusManager.EventBusMsg msg) {
        AllDeviceWorkInfo errorWorkInfo = msg.getAllDeviceWorkInfo();
//        System.out.println(errorWorkInfo.toString());
        if (errorWorkInfo != null) {
            for (int i = 0; i < list.size(); i++) {
                AllDeviceWorkInfo info = list.get(i);
                if (info.getToiletId().equals(errorWorkInfo.getToiletId())) {
                    list.remove(i);
                    list.add(i, errorWorkInfo);
                    errorDeviceAdapter.notifyDataSetChanged();
                }
            }
        }
    }


    private void initUI(View view) {
        errorDevice_lv = (ListView) view.findViewById(R.id.errorDevice_lv);
        select_city_btn = (Button) view.findViewById(R.id.select_city_btn);
        currentCity_tv = (TextView) view.findViewById(R.id.currentCity_tv);
        refreshToiletBtn = (Button) view.findViewById(R.id.deviceRun_refresh_btn);
        hotCity_tablelayout = (TableLayout) view.findViewById(R.id.hotCity_tablelayout);
        showDeviceDetailBtn = (Button) view.findViewById(R.id.deviceRun_test_btn);
        Drawable drawableRight = getResources().getDrawable(R.drawable.search_bath_id);
        drawableRight.setBounds(0, 0, 100, 100);
        currentCity_tv.setCompoundDrawables(null, null, drawableRight, null);
        currentCity_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), SearchBathIdAty.class);
                intent.putExtra("onlineToiletIdList", (Serializable) onLineToiletIdList);
                startActivityForResult(intent, 1);
            }
        });


        select_city_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPicker.showCityPicker();
            }
        });

        refreshToiletBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshToiletList();
            }
        });
        showDeviceDetailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (AllDeviceWorkInfo info : list) {
                    if (info.getToiletId().equals(APPConstant.TOILETID)) {
                        JumpHelper.gotoDeviceRunDetailActivity(getContext(), info);
                    }
                }
            }
        });

    }

    private void refreshToiletList() {
        LoadingDialog loadingDialog = new LoadingDialog(getContext());
        loadingDialog.showWithTimeout(2000);
        HashMap<String, String> params = new HashMap<>();
        params.put("username", APPConstant.USERNAME);
        OkHttpManager.CommonPostAsyn(WebConstant.GET_ALL_ONLINE_TOILET_STATE_WORK, params, new OkHttpManager.ResultCallback() {
            @Override
            public void onCallBack(OkHttpManager.State state, String result) {
                //刚刚发现，该方法可以在任意线程调用
//                loadingDialog.dismiss();
                if (state == OkHttpManager.State.SUCCESS) {
                    Log.e(TAG, "获取在线厕所状态信息成功！");
                    Log.i(TAG, "result:" + result);
                    Type type = new TypeToken<List<AllDeviceWorkInfo>>() {
                    }.getType();
                    list.clear();
                    list.addAll(new Gson().fromJson(result, type));
                    onLineToiletIdList.clear();
                    for (AllDeviceWorkInfo info : list) {
                        onLineToiletIdList.add(info.getToiletId());
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            errorDeviceAdapter.notifyDataSetChanged();
                        }
                    });
                } else {
                    Log.e(TAG, "获取在线厕所状态信息失败！");
                }
            }
        });
    }

    private void initData() {
        errorDeviceAdapter = new ErrorDeviceAdapter(getContext(), R.layout.cell_error_device_item, list);
        errorDevice_lv.setAdapter(errorDeviceAdapter);
        refreshToiletList();

        errorDevice_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            ErrorDeviceAdapter.ViewHolder lastViewHolder;

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ErrorDeviceAdapter.ViewHolder viewHolder = (ErrorDeviceAdapter.ViewHolder) view.getTag();
                if (lastViewHolder == null) {
                    viewHolder.indicate_view.setBackgroundColor(getResources().getColor(R.color.primaryGreen));
                    viewHolder.toiletLocation_tv.setTextColor(getResources().getColor(R.color.primaryGreen));
                } else if (viewHolder != lastViewHolder) {
                    viewHolder.indicate_view.setBackgroundColor(getResources().getColor(R.color.primaryGreen));
                    viewHolder.toiletLocation_tv.setTextColor(getResources().getColor(R.color.primaryGreen));
                    lastViewHolder.indicate_view.setBackgroundColor(getResources().getColor(R.color.lightgrey));
                    lastViewHolder.toiletLocation_tv.setTextColor(getResources().getColor(R.color.black));
                }
                lastViewHolder = viewHolder;
                String toiletId = list.get(position).getToiletId();
                // 更新公共数据
                Repository.getItemCommon(getActivity(), toiletId, null);
                Repository.getToiletItemInfoByToiletId(getActivity(), toiletId, new Repository.DataCallback() {
                    @Override
                    public void onData(Object... args) {

                        if (args.length == 1) {
                            onToiletSelected((ToiletInfo.ToiletItem) args[0]);
//                            showSelectedCity();
                        }

                    }
                });

            }
        });

        setupCityPicker();
        showSelectedCity();
        loadHotCityInfo();
        Repository.getItemCommon(getActivity(), APPConstant.TOILETID, null);

    }

    /**
     * 设置城市选择器
     */
    private void setupCityPicker() {
        //初始化--加载城市列表
        mPicker.init(getContext());
        //添加默认的配置，不需要自己定义
        CityConfig cityConfig = new CityConfig.Builder()
                .province("广东省").city("全部").district("全部")
                .build();
        mPicker.setConfig(cityConfig);
        mPicker.setOnCityItemClickListener(new OnCityItemClickListener() {
            @Override
            public void onSelected(ProvinceBean province, CityBean city, DistrictBean district) {
                super.onSelected(province, city, district);
                String provinceStr = province.getName().trim();
                String cityStr = city.getName().trim().equals("全部") ? "" : city.getName().trim();
                String districtStr = (district == null || district.getName().trim().equals("全部")) ? "" : district.getName().trim();
                onToiletLocationSelected(provinceStr, cityStr, districtStr);
            }

            @Override
            public void onCancel() {
                super.onCancel();
                System.out.println("选择城市-----已取消");
            }
        });

    }


    /**
     * 设置热门城市
     */
    private void loadHotCityInfo() {

        Button[] btn_hotcity = new Button[6];
        btn_hotcity[0] = (Button) hotCity_tablelayout.findViewById(R.id.btn_hotcity_0);
        btn_hotcity[1] = (Button) hotCity_tablelayout.findViewById(R.id.btn_hotcity_1);
        btn_hotcity[2] = (Button) hotCity_tablelayout.findViewById(R.id.btn_hotcity_2);
        btn_hotcity[3] = (Button) hotCity_tablelayout.findViewById(R.id.btn_hotcity_3);
        btn_hotcity[4] = (Button) hotCity_tablelayout.findViewById(R.id.btn_hotcity_4);
        btn_hotcity[5] = (Button) hotCity_tablelayout.findViewById(R.id.btn_hotcity_5);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] location = ((String) v.getTag()).split("-");
                onToiletLocationSelected(location[0], location[1], "");
            }
        };

        for (Button btn : btn_hotcity) {
            btn.setOnClickListener(listener);
        }


    }


    @MainThread
    private void showSelectedCity() {
        currentCity_tv.setText(APPConstant.PROVIENCE + "--" + APPConstant.CITY + "--" + APPConstant.COUNTY);
    }

    private void saveSelectedCity(String province, String city, String district, String detail) {
        province = province == null ? "" : province.trim();
        city = city == null ? "" : city.trim();
        district = district == null ? "" : district.trim();
        detail = detail == null ? "" : detail.trim();
        currentCity_tv.setText(province + "--" + city + "--" + district);
        APPConstant.PROVIENCE = province;
        APPConstant.CITY = city;
        APPConstant.COUNTY = district;
        APPConstant.DETAIL = detail;
        SharedPreferencesUtil.putString(getContext(), "province", APPConstant.PROVIENCE);
        SharedPreferencesUtil.putString(getContext(), "city", APPConstant.CITY);
        SharedPreferencesUtil.putString(getContext(), "county", APPConstant.COUNTY);
        SharedPreferencesUtil.putString(getContext(), "detail", APPConstant.DETAIL);

    }

    /**
     * 选择 省-市-区 后，查询所在地区的厕所信息
     *
     * @param province
     * @param city
     * @param district
     */
    private void onToiletLocationSelected(String province, String city, String district) {
        Intent intent = new Intent(getContext(), SearchBathIdAty.class);
        intent.putExtra("province", province);
        intent.putExtra("city", city);
        intent.putExtra("district", district);
        intent.putExtra("onlineToiletIdList", (Serializable) onLineToiletIdList);
        startActivityForResult(intent, 1);

    }


    /**
     * 确定toiletId后，调用
     *
     * @param item
     */
    private void onToiletSelected(ToiletInfo.ToiletItem item) {
        if (item == null) {
            return;
        }
        Log.i(TAG, "切换Toilet，获得新的Item: " + item.toString());
        Activity activity = getActivity();
        if (activity != null && activity instanceof InfoCallback) {
            ((InfoCallback) activity).onToiletItem(item);
        }
        saveSelectedCity(item.getProvince(), item.getCity(), item.getCounty(), item.getDetail());
        showSelectedCity();
    }

    public void onNewToiletItem(Intent data) {
        if (data == null) {
            return;
        }
        ToiletInfo.ToiletItem item = (ToiletInfo.ToiletItem) data.getSerializableExtra("toiletItem");
        if (item != null) {
            onToiletSelected(item);
        }
    }


    /**
     * （取消）注册 EventBus
     *
     * @param context
     * @param action
     */
    private void setEventBus(Object context, boolean action) {
        if (action) {
            if (!EventBusManager.getInstance(EventBusManager.INSTANCE_NAME.work_485).isRegistered(context)) {
                EventBusManager.getInstance(EventBusManager.INSTANCE_NAME.work_485).register(context);
            }
            if (!EventBusManager.getInstance(EventBusManager.INSTANCE_NAME.Broadcast).isRegistered(context)) {
                EventBusManager.getInstance(EventBusManager.INSTANCE_NAME.Broadcast).register(context);
            }
        } else {
            EventBusManager.getInstance(EventBusManager.INSTANCE_NAME.work_485).unregister(context);
            EventBusManager.getInstance(EventBusManager.INSTANCE_NAME.Broadcast).unregister(context);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 2) {
            onNewToiletItem(data);
        }
    }

    /**
     * 设备状态信息list的adapter
     */
    private static class ErrorDeviceAdapter extends ArrayAdapter<AllDeviceWorkInfo> {
        private Context context;
        private List<AllDeviceWorkInfo> list;

        public ErrorDeviceAdapter(@NonNull Context context, int resource, @NonNull List<AllDeviceWorkInfo> objects) {
            super(context, resource, objects);
            this.context = context;
            this.list = objects;
        }


        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.cell_error_device_item, null);
                viewHolder = new ViewHolder();
                viewHolder.toiletLocation_tv = (TextView) convertView.findViewById(R.id.toiletLocation_tv);
                viewHolder.indicate_view = convertView.findViewById(R.id.indicate_view);
                viewHolder.flag_img = (ImageView) convertView.findViewById(R.id.flag_img);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.toiletLocation_tv.setText(list.get(position).getToiletId() + " " + list.get(position).getToiletName());
            viewHolder.indicate_view.setBackgroundColor(context.getResources().getColor(R.color.lightgrey));
            viewHolder.toiletLocation_tv.setTextColor(context.getResources().getColor(R.color.black));
            if (list.get(position).isStateFlag()) {
                viewHolder.flag_img.setBackgroundResource(R.mipmap.ic_warning);
            } else {
                viewHolder.flag_img.setBackgroundResource(R.mipmap.ic_no_warning);
            }

            return convertView;
        }

        public class ViewHolder {
            TextView toiletLocation_tv;
            View indicate_view;
            ImageView flag_img;
        }
    }


}
