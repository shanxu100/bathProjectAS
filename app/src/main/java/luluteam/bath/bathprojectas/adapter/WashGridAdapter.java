package luluteam.bath.bathprojectas.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import luluteam.bath.bathprojectas.R;
import luluteam.bath.bathprojectas.databinding.ViewGridWashBinding;
import luluteam.bath.bathprojectas.model.pit.ToiletDeviceNum;
import luluteam.bath.bathprojectas.model.wash.WashParams;
import luluteam.bath.bathprojectas.view.setting.WashSetView;

/**
 * 冲水分段参数页面List的Adapter
 */
public class WashGridAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater inflater;
    private ViewGridWashBinding mBinding;
    private List<WashParams.Group> itemList;

    private ToiletDeviceNum deviceNum;


    private static final String TAG = "WashGridAdapter";

    public ToiletDeviceNum getDeviceNum() {
        return deviceNum;
    }

    public void setNumSit(int numSit) {
        deviceNum.numSit = numSit;
    }

    public void setNumWash(int numWash) {
        deviceNum.numWash = numWash;
    }

    public void setDeviceNum(ToiletDeviceNum deviceNum) {
        this.deviceNum = deviceNum;
    }

    public List<WashParams.Group> getItemList() {
        return itemList;
    }

    public WashGridAdapter(Context mContext, List<WashParams.Group> itemList, ToiletDeviceNum deviceNum) {
        this.mContext = mContext;
        this.itemList = itemList;
        this.inflater = LayoutInflater.from(mContext);
        this.deviceNum = deviceNum;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int i) {
        return itemList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup viewGroup) {
        if (convertView == null) {
            mBinding = DataBindingUtil.inflate(inflater, R.layout.view_grid_wash, viewGroup, false);
            convertView = mBinding.getRoot();
            convertView.setTag(mBinding);
        } else {
            mBinding = (ViewGridWashBinding) convertView.getTag();
        }
        mBinding.gridWashSetView.setWashSetGroup(itemList.get(i), i);
        if ((i + 1) <= deviceNum.numSit) {
            mBinding.gridWashIndex.setText("坑位" + (i + 1));
        } else {
            mBinding.gridWashIndex.setText("过道" + (i + 1 - deviceNum.numSit));
        }
        Log.d(TAG, "冲水参数数据，单元 " + i);
        mBinding.gridWashSetView.setUpdateParamsListener(new WashSetView.UpdateParamsListener() {
            @Override
            public void onLostFocus(int viewIndex, int runTime, int delayTime, boolean selected) {
                itemList.get(viewIndex).runningTime = runTime;
                itemList.get(viewIndex).intervalTime = delayTime;
                itemList.get(viewIndex).selected = selected;
                Log.d(TAG, "冲水参数数据，单元 " + viewIndex + " : " + itemList.get(i).toString());

            }
        });

//        mBinding.gridWashSetView.
        return convertView;
    }


}
