package luluteam.bath.bathprojectas.fragment.wash;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import luluteam.bath.bathprojectas.R;
import luluteam.bath.bathprojectas.adapter.WashGridAdapter;
import luluteam.bath.bathprojectas.constants.APPConstant;
import luluteam.bath.bathprojectas.constants.WebConstant;
import luluteam.bath.bathprojectas.databinding.FragmentWashBinding;
import luluteam.bath.bathprojectas.filters.InputFilterMinMax;
import luluteam.bath.bathprojectas.fragment.BaseFragment;
import luluteam.bath.bathprojectas.fragment.setting.WashSettingFragment;
import luluteam.bath.bathprojectas.listener.WashSetParamsListener;
import luluteam.bath.bathprojectas.model.pit.Bus485Pit;
import luluteam.bath.bathprojectas.model.pit.ToiletDeviceNum;
import luluteam.bath.bathprojectas.model.wash.WashParams;
import luluteam.bath.bathprojectas.tools.Repository;
import luluteam.bath.bathprojectas.utils.ToastUtil;
import luluteam.bath.bathprojectas.utils.okhttp.OkHttpManager;
import luluteam.bath.bathprojectas.view.dialog.LoadingDialog;

/**
 * 冲水分段参数
 */
public class WashFragment extends BaseFragment implements WashSetParamsListener {
    private static final String BUNDLE_KEY_USAGE = "bundle_key_pit_type";
    public static final String USAGE_MAN = "type_man";
    public static final String USAGE_WOMAN = "type_woman";
    public static final String USAGE_DISABLED = "type_disabled";

    public static final String TAG = "WashFragment";

    private FragmentWashBinding mBinding;
    private WashGridAdapter adapter;

    private String toiletUsage = "";
    private String usage = "1";
    private int numSit = 0;
    private int numWash = 0;
    //    private int sitStartIndex = 0;
//    private int roadStartIndex = 60;
    private int requestIndex = 0x21;

    private Context mContext;

    private List<WashParams.Group> itemList = new ArrayList<>();

    public WashFragment() {

    }

    public static WashFragment getWashFragment(String type) {
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_KEY_USAGE, type);
        WashFragment fragment = new WashFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        Fragment fragment = getParentFragment();
        if (fragment != null && fragment instanceof WashSettingFragment) {
            long delay = (requestIndex - 0x20) * 3000 + 5000;
            //自动请求两次，没办法的事。因为下位机处理性能有限，有时候会不响应
//            ((WashSettingFragment) fragment).refreshWashParams(requestIndex, delay);
            ((WashSettingFragment) fragment).refreshWashParams(requestIndex, delay);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_wash, container, false);
        mContext = getContext();
//        UpdateGridUI(mContext);
        adapter = new WashGridAdapter(mContext, itemList, new ToiletDeviceNum(numSit, 0, 0, numWash));
        mBinding.imgGridView.setAdapter(adapter);
        setListeners();
        mBinding.includeView.washRunTime.setFilters(new InputFilter[]{new InputFilterMinMax(0, 15)});
        mBinding.includeView.washDelayTime.setFilters(new InputFilter[]{new InputFilterMinMax(0, 15)});
        return mBinding.getRoot();
    }


    private void setListeners() {
        //填充默认值
        mBinding.defaultSetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (WashParams.Group model : itemList) {
                    String washRunStr = mBinding.includeView.washRunTime.getText().toString();
                    if (StringUtils.isNotEmpty(washRunStr) && StringUtils.isNumeric(washRunStr)) {
                        int washRunTime = Integer.parseInt(washRunStr);
                        if (washRunTime >= 0 && washRunTime <= WashParams.MAX_TIME) {
                            model.runningTime = washRunTime;
                        }
                    }
                    String washDelayStr = mBinding.includeView.washDelayTime.getText().toString();
                    if (StringUtils.isNotEmpty(washDelayStr) && StringUtils.isNumeric(washDelayStr)) {
                        int washDelayTime = Integer.parseInt(washDelayStr);
                        if (washDelayTime >= 0 && washDelayTime <= WashParams.MAX_TIME) {
                            model.intervalTime = washDelayTime;
                        }
                    }
                    model.selected = mBinding.includeView.washSelected.isChecked();
                }
                adapter.notifyDataSetChanged();
            }
        });
        //设置参数
        mBinding.paramsSetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBinding.includeView.washRunTime.requestFocus();
                uploadParams(usage, itemList);
            }
        });
        //刷新参数
        mBinding.refreshSetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadingDialog loadingDialog = new LoadingDialog(mContext);
                loadingDialog.showWithTimeout(2000);
                ((WashSettingFragment) getParentFragment()).refreshWashParams(requestIndex);
            }
        });
        Drawable drawableRight = mContext.getResources().getDrawable(R.drawable.selector_iv_up);
        drawableRight.setBounds(0, 0, 156, 75);
        mBinding.defaultShowTv.setCompoundDrawables(null, null, drawableRight, null);
        mBinding.defaultShowTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBinding.defaultSetLl.getVisibility() == View.GONE) {
                    Drawable drawableRight = mContext.getResources().getDrawable(R.drawable.selector_iv_up);
                    drawableRight.setBounds(0, 0, 156, 75);
                    mBinding.defaultShowTv.setCompoundDrawables(null, null, drawableRight, null);
                    mBinding.defaultSetLl.setVisibility(View.VISIBLE);
                } else {
                    Drawable drawableRight = mContext.getResources().getDrawable(R.drawable.selector_iv_down);
                    drawableRight.setBounds(0, 0, 156, 75);
                    mBinding.defaultShowTv.setCompoundDrawables(null, null, drawableRight, null);
                    mBinding.defaultSetLl.setVisibility(View.GONE);
                }
            }
        });

        //发送index=0的请求，获取公共部分数据
        mBinding.requestAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(mContext)
                        .setMessage("点击“确定”重新获取参数")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //请求冲水设备的数量
                                LoadingDialog loadingDialog = new LoadingDialog(getContext());
                                loadingDialog.showWithTimeout(2000);
                                Repository.requestPit(getContext(), APPConstant.TOILETID, 0);
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
        });

        mBinding.imgGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                switch (i) {
                    //滑动停止
                    case SCROLL_STATE_IDLE:
                        Log.d(TAG, "滑动停止");
                        break;
                    //正在滑动
                    case SCROLL_STATE_TOUCH_SCROLL:
                        Log.d(TAG, "正在滑动");
                        mBinding.includeView.washRunTime.requestFocus();
                        break;
                    //惯性继续滚动
                    case SCROLL_STATE_FLING:
                        Log.d(TAG, "滑行继续滑动");
                        break;
                    default:
                        Log.d(TAG, "list滑动--default：" + i);

                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });
    }

    /**
     * TODO
     * 更新gridView 的显示
     */
    public void UpdateGridUI(Context context) {
        Log.e(TAG, "---------UpdateGridUI-----" + APPConstant.itemCommon.toString());
        this.toiletUsage = getArguments().getString(BUNDLE_KEY_USAGE);
        setNums();
        itemList.clear();
        for (int i = 0; i < numSit; i++) {
//            itemList.add(WashParams.Group.getDefaultGroup(sitStartIndex + i));
            itemList.add(WashParams.Group.getDefaultGroup(i));

        }
        for (int i = 0; i < numWash; i++) {
//            itemList.add(WashParams.Group.getDefaultGroup(roadStartIndex + i));
            itemList.add(WashParams.Group.getDefaultGroup(i));

        }
        if (adapter != null) {
            adapter.getDeviceNum().numSit = numSit;
            adapter.getDeviceNum().numWash = numWash;
            adapter.notifyDataSetChanged();
        }
    }


    /**
     * 点击“设置参数”按钮后，向服务器发送参数数据，设置采集卡冲水参数
     *
     * @param usage
     * @param itemList 这个List的元素个数==页面上显示的个数，因此需要补全个数再上传到服务器（男女厕70、残卫20）
     */
    private void uploadParams(String usage, List<WashParams.Group> itemList) {
        LoadingDialog loadingDialog = new LoadingDialog(mContext);
        loadingDialog.showWithTimeout(3000);
        WashParams washParams = new WashParams();
        washParams.setToiletId(APPConstant.TOILETID);
        washParams.setUsage(usage);
        washParams.setDeviceType("H");
        washParams.setList(fillDataList(itemList, usage, this.numSit, this.numWash));
//        Log.i(TAG, "设置冲水参数：" + washParams.toJson() );
        OkHttpManager.CommonPostJson(WebConstant.SET_WASH_PARAMS, washParams.toJson(), new OkHttpManager.ResultCallback() {
            @Override
            public void onCallBack(OkHttpManager.State state, String result) {
                Log.i(TAG, "设置冲水参数：" + washParams.toJson() + " result=" + result);
                if (state == OkHttpManager.State.SUCCESS) {
//                    ToastUtil.logAndToast(mContext, "设置冲水参数成功！");
                } else {
                    ToastUtil.logAndToast(mContext, "设置冲水参数失败！");
                }
            }
        });


    }

    /**
     * 补全itemList
     *
     * @param itemList
     * @param usage
     * @param numSit
     * @param numWash
     * @return
     */
    public List<WashParams.Group> fillDataList(List<WashParams.Group> itemList, String usage, int numSit, int numWash) {
        List<WashParams.Group> list = new ArrayList<>(numSit + numWash);
        int maxNumSit, maxNumWash;
        maxNumSit = "3".equals(usage) ? 10 : 60;
        maxNumWash = 10;

        Iterator<WashParams.Group> it = itemList.iterator();
        while (it.hasNext()) {
            if (list.size() < numSit) {
                list.add(it.next());
                continue;
            }
            while (list.size() < maxNumSit) {
                list.add(WashParams.Group.getDefaultGroup(-1));
            }
            list.add(it.next());
        }
        while (list.size() < maxNumSit + maxNumWash) {
            list.add(WashParams.Group.getDefaultGroup(-1));
        }
        return list;
    }


    /**
     * 初始化设备数量
     */
    private void setNums() {
        switch (toiletUsage) {
            case USAGE_MAN:
                numSit = Math.min(APPConstant.itemCommon.numManPitWash, Bus485Pit.MAX_MAN_WOMAN_PIT);
                numWash = Math.min(APPConstant.itemCommon.numManRoadWash, Bus485Pit.MAX_WASH);
//                sitStartIndex = 0;
//                roadStartIndex = 60;
                usage = "1";
                requestIndex = 0x21;
                break;
            case USAGE_WOMAN:
                numSit = Math.min(APPConstant.itemCommon.numWomanPitWash, Bus485Pit.MAX_MAN_WOMAN_PIT);
                numWash = Math.min(APPConstant.itemCommon.numWomanRoadWash, Bus485Pit.MAX_WASH);
//                sitStartIndex = 70;
//                roadStartIndex = 130;
                usage = "2";
                requestIndex = 0x22;
                break;
            case USAGE_DISABLED:
                numSit = Math.min(APPConstant.itemCommon.numDisabledPitWash, Bus485Pit.MAX_DISABLED_PIT);
                numWash = Math.min(APPConstant.itemCommon.numDisabledRoadWash, Bus485Pit.MAX_WASH);
//                sitStartIndex = 140;
//                roadStartIndex = 150;
                usage = "3";
                requestIndex = 0x23;
                break;
            default:
                ToastUtil.showShortToast(mContext, "参数无效 Usage=" + toiletUsage);
        }
        Log.i(TAG, "toiletUsage=" + toiletUsage + " fragment.numSit=" + numSit
                + " numWash=" + numWash + " usage=" + usage);
    }

    @Override
    public void onPitWashSetReceived(List<WashParams.Group> groups, int frameIndex) {
//        //坑位冲水设施的组数
//        int groupNums = numSit / WashParams.FIXED_LIST_SIZE;
//        //坑位最后一组设施的数量，1-9
//        int lastGroupNum = numSit % WashParams.FIXED_LIST_SIZE;
//        if (frameIndex < groupNums) {
//            changeItemList(itemList, groups, frameIndex * WashParams.FIXED_LIST_SIZE, WashParams.FIXED_LIST_SIZE);
//        } else if (frameIndex == groupNums) {
//            if (lastGroupNum == 0) {
//                //证明每个组中的所有值都是有效的
//                changeItemList(itemList, groups, (groupNums - 1) * WashParams.FIXED_LIST_SIZE, WashParams.FIXED_LIST_SIZE);
//            } else {
//                changeItemList(itemList, groups, groupNums * WashParams.FIXED_LIST_SIZE, lastGroupNum);
//            }
//        }
        changeItemList(itemList, groups, 0, numSit);
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onRoadWashSetReceived(List<WashParams.Group> groups, int frameIndex) {
//        //坑位冲水的组数
//        int numSitGroup = numSit / WashParams.FIXED_LIST_SIZE;
//        int lastGroupNum = numSit%WashParams.FIXED_LIST_SIZE;
//        numSitGroup = lastGroupNum == 0 ? numSitGroup : numSitGroup+1;
        //过道冲水，只有一组

        changeItemList(itemList, groups, numSit, numWash);
        adapter.notifyDataSetChanged();
    }

    private void changeItemList(List<WashParams.Group> models, List<WashParams.Group> groups, int start, int length) {
        int index = start;
        for (int i = 0; i < length; i++) {
            WashParams.Group.deepCopy(models.get(index), groups.get(i));
            index++;
        }
    }

}
