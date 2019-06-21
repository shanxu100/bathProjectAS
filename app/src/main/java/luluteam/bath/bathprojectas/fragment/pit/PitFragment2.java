package luluteam.bath.bathprojectas.fragment.pit;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

import luluteam.bath.bathprojectas.R;
import luluteam.bath.bathprojectas.activity.Pit2Activity;
import luluteam.bath.bathprojectas.adapter.PitGridAdapter;
import luluteam.bath.bathprojectas.constants.APPConstant;
import luluteam.bath.bathprojectas.databinding.FragmentPitBinding;
import luluteam.bath.bathprojectas.model.pit.Bus485Pit;
import luluteam.bath.bathprojectas.model.pit.ToiletDeviceNum;
import luluteam.bath.bathprojectas.utils.ToastUtil;
import luluteam.bath.bathprojectas.view.dialog.CleanessDialog;

public class PitFragment2 extends Fragment implements Pit2Activity.PitDataListener {
    private static final String BUNDLE_KEY_USAGE = "bundle_key_pit_type";
    public static final String USAGE_MAN = "type_man";
    public static final String USAGE_WOMAN = "type_woman";
    public static final String USAGE_DISABLED = "type_disabled";

    private FragmentPitBinding mBinding;

    /**
     * 记录了每一个蹲位数据的List。adapter中保存了对这个实例的引用
     * 每次收到新的数据，在adapter中更新该list的值，而不是直接指向一个新的地址。
     * 所以，这个list和adapter显示的list，是同一个数据
     */
    private List<Bus485Pit.Item> itemList = new ArrayList<>();

    private List<Bus485Pit.ItemGas> itemGasDetailList;
    private List<Bus485Pit.ItemPit> itemPitDetailList;
    private PitGridAdapter adapter;
    private String toiletUsage = "";

    private CleanessDialog dialog;

    public static final String TAG = "PitFragment2";

    private int numSit;
    private int numStand;
    private int numWash;
    private int numGas;

    public PitFragment2() {
    }

    private void setNums() {
        switch (toiletUsage) {
            case USAGE_MAN:
                numSit = Math.min(APPConstant.itemCommon.numManSit, Bus485Pit.MAX_MAN_WOMAN_PIT);
                numGas = Math.min(APPConstant.itemCommon.numManGas, Bus485Pit.MAX_MAN_GAS);
                numStand = Math.min(APPConstant.itemCommon.numManStand, Bus485Pit.MAX_MAN_STAND);
                numWash = Math.min(APPConstant.itemCommon.numManRoadWash, Bus485Pit.MAX_WASH);
                break;
            case USAGE_WOMAN:
                numSit = Math.min(APPConstant.itemCommon.numWomanSit, Bus485Pit.MAX_MAN_WOMAN_PIT);
                numGas = Math.min(APPConstant.itemCommon.numWomanGas, Bus485Pit.MAX_WOMAN_GAS);
                numStand = 0;
                numWash = Math.min(APPConstant.itemCommon.numWomanRoadWash, Bus485Pit.MAX_WASH);
                break;
            case USAGE_DISABLED:
                numSit = Math.min(APPConstant.itemCommon.numDisabledSit, Bus485Pit.MAX_DISABLED_PIT);
                numGas = Math.min(APPConstant.itemCommon.numDisabledGas, Bus485Pit.MAX_DISABLED_GAS);
                numStand = Math.min(APPConstant.itemCommon.numDisabledStand, Bus485Pit.MAX_DISABLED_PIT);
                numWash = Math.min(APPConstant.itemCommon.numDisabledRoadWash, Bus485Pit.MAX_WASH);
                break;
            default:
        }
        Log.e(TAG, "fragment.numsit======" + numSit);
    }

    /**
     * 获取坑位实例Fragment
     */
    public static PitFragment2 getPitFragment(String pitType) {
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_KEY_USAGE, pitType);
        PitFragment2 fragment = new PitFragment2();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_pit, container, false);
        this.toiletUsage = getArguments().getString(BUNDLE_KEY_USAGE);
        Log.i(TAG, "PitFragment===onCreateView: + pitType" + toiletUsage);
        setNums();
        if (APPConstant.itemCommon != null) {
            for (int i = 0; i < numSit; i++) {
                itemList.add(Bus485Pit.ItemPit.newEmptyInstance(0));
            }
            for (int i = 0; i < numStand; i++) {
                itemList.add(Bus485Pit.ItemPit.newEmptyInstance(1));
            }
            for (int i = 0; i < numWash; i++) {
                itemList.add(Bus485Pit.Item.newEmptyInstance(2));
            }
            for (int i = 0; i < numGas; i++) {
                itemList.add(Bus485Pit.ItemGas.newEmptyInstance(3));
            }
        }
        itemGasDetailList = new ArrayList<>(numGas);
        itemPitDetailList = new ArrayList<>(numSit);

        adapter = new PitGridAdapter(getContext(), itemList, new ToiletDeviceNum(numSit, numStand, numGas, numWash));
        mBinding.imgGridView.setAdapter(adapter);
        mBinding.imgGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int position = 0;
                if ((position = isPitDetailPosition(i)) != -1) {
                    //坑位详情
                    if (position < itemPitDetailList.size()) {
                        showCleanessDialog(itemPitDetailList.get(position), position, CleanessDialog.TYPE_PIT);
                    } else {
                        ToastUtil.showShortToast(getContext(), "没有坑位详细数据，稍后重试：position=" + position);
                        ((Pit2Activity) getActivity()).refreshPit(2);
                    }
                } else if ((position = isGasDetailPosition(i)) != -1) {
                    //气味探测器详情
                    if (position < itemGasDetailList.size()) {
                        showCleanessDialog(itemGasDetailList.get(position), position, CleanessDialog.TYPE_GAS);
                    } else {
                        ToastUtil.showShortToast(getContext(), "没有气体详细数据，稍后重试：position=" + position);
                        ((Pit2Activity) getActivity()).refreshPit(3);
                    }
                }
            }
        });
        return mBinding.getRoot();
    }

    /**
     * 显示 清洁度 的dialog
     *
     * @param item
     * @param position 在坑位详情的list或者气体详情的list中的position
     */
    private void showCleanessDialog(Bus485Pit.Item item, int position, int type) {
        dialog = null;
        dialog = new CleanessDialog(getContext(), item, position, type);
        dialog.setSinglePitRefreshListener(new CleanessDialog.BtnClickCallback() {
            @Override
            public void onItemPosition(int position) {
                if (item instanceof Bus485Pit.ItemPit) {
                    ((Pit2Activity) getActivity()).refreshPit(2);
                    dialog.freshItemInfo(itemPitDetailList.get(position));
                } else if (item instanceof Bus485Pit.ItemGas) {
                    ((Pit2Activity) getActivity()).refreshPit(3);
                    dialog.freshItemInfo(itemGasDetailList.get(position));
                }

            }
        });
        dialog.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
    }


    @Override
    public void onPitData(List<Bus485Pit.Item> itemList) {
        if (adapter == null) {
            ToastUtil.showLongToast(getContext(), "页面初始化失败，请重新打开页面");
            return;
        }
        List<Bus485Pit.Item> adapterItemList = new ArrayList<>(numSit + numStand);
        Log.i(TAG, "++++++++++onPitData() itemList.size()===========" + itemList.size());
        adapterItemList.addAll(itemList.subList(0, numSit));
        if ((toiletUsage.equals(USAGE_MAN)) && numStand != 0) {
            //不需要减一
            adapterItemList.addAll(itemList.subList(Bus485Pit.MAX_MAN_WOMAN_PIT, Bus485Pit.MAX_MAN_WOMAN_PIT + numStand));
        }
        if (toiletUsage.equals(USAGE_DISABLED) && numStand != 0) {
            adapterItemList.addAll(itemList.subList(Bus485Pit.MAX_DISABLED_PIT, Bus485Pit.MAX_DISABLED_PIT + numStand));
        }
        adapter.updateData(adapterItemList, 0);
    }

    @Override
    public void onWaterData(List<Bus485Pit.Item> itemList) {
        if (adapter == null) {
            ToastUtil.showLongToast(getContext(), "页面初始化失败，请重新打开页面");
            return;
        }
        Log.i(TAG, "++++++++++onWaterData() itemList.size()===========" + itemList.size());
        adapter.updateData(itemList.subList(0, numWash), numSit + numStand);
    }

    @Override
    public void onGasData(List<Bus485Pit.Item> itemList) {
        if (adapter == null) {
            ToastUtil.showLongToast(getContext(), "页面初始化失败，请重新打开页面");
            return;
        }
        Log.i(TAG, "++++++++++onGasData() itemList.size()===========" + itemList.size());
        adapter.updateData(itemList.subList(0, numGas), numSit + numStand + numWash);
    }

    @Override
    public void onGasDetailData(List<Bus485Pit.ItemGas> itemGasList) {
        itemGasDetailList.clear();
        for (int i = 0; i < numGas; i++) {
            itemGasDetailList.add(itemGasList.get(i));
        }
        if (dialog != null && dialog.getType() == CleanessDialog.TYPE_GAS && dialog.isShowing()) {
            dialog.freshItemInfo(itemGasDetailList.get(dialog.getPosition()));
            ToastUtil.logAndToast(getContext(), dialog.getPosition() + "");
        }

    }

    @Override
    public void onPitDetailData(List<Bus485Pit.ItemPit> itemPitList) {
        itemPitDetailList.clear();
        for (int i = 0; i < numSit; i++) {
            itemPitDetailList.add(itemPitList.get(i));
        }
        if (dialog != null && dialog.getType() == CleanessDialog.TYPE_PIT
                && dialog.isShowing()) {
            dialog.freshItemInfo(itemPitDetailList.get(dialog.getPosition()));
            ToastUtil.logAndToast(getContext(), dialog.getPosition() + "");
        }
    }

    /**
     * 判断所点击的图表是否是坑位。
     * 如果是，则返回该图标对应的itemPitDetailList中的position；
     * 反之，则返回-1。
     *
     * @param position
     * @return
     */
    public int isPitDetailPosition(int position) {
        int index = -1;
        if (position < numSit) {
            index = position;
        }
        return index;
    }

    /**
     * 判断所点击的图表是否是气体。
     * 如果是，则返回该图标对应的itemGasDetailList中的position；
     * 反之，则返回-1。
     *
     * @param position
     * @return
     */
    public int isGasDetailPosition(int position) {
        int index = -1;
        if (position >= itemList.size() - numGas && position < itemList.size()) {
            index = position - numSit - numStand - numWash;
        }
        return index;
    }

}
