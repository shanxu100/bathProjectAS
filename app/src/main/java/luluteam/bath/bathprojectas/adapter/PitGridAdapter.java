package luluteam.bath.bathprojectas.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import luluteam.bath.bathprojectas.R;
import luluteam.bath.bathprojectas.databinding.ViewGridPitBinding;
import luluteam.bath.bathprojectas.model.pit.Bus485Pit;
import luluteam.bath.bathprojectas.model.pit.ToiletDeviceNum;

public class PitGridAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater inflater;
    private List<Bus485Pit.Item> itemList;
    private ViewGridPitBinding mBinding;
    private ToiletDeviceNum deviceNum;

    public PitGridAdapter(Context mContext, List<Bus485Pit.Item> itemList, ToiletDeviceNum deviceNum) {
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
    public Object getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            mBinding = DataBindingUtil.inflate(inflater, R.layout.view_grid_pit, parent, false);
            convertView = mBinding.getRoot();
            convertView.setTag(mBinding);
        } else {
            mBinding = (ViewGridPitBinding) convertView.getTag();
        }
        Bus485Pit.Item item = itemList.get(position);
        if (item.type == 0) {
            mBinding.viewGrid.setBackground(mContext.getResources().getDrawable(R.drawable.selector_pit_state, null));
            mBinding.tvGrid.setText((position + 1) + "");
            mBinding.tvCleanessGas.setVisibility(View.VISIBLE);
            mBinding.tvCleanessGas.setText("等级:" + ((item.cleaness == -1) ? "无" : level2Str(item.cleaness, 0)));
            mBinding.viewGrid.setBackgroundResource(item.work ? R.drawable.selector_pit_state : R.drawable.sit_r);
            //清洁度等级报警：0表示最干净
            mBinding.tvWarning.setVisibility(item.cleaness < 2 ? View.GONE : View.VISIBLE);


        } else if (item.type == 1) {
            mBinding.viewGrid.setBackground(mContext.getResources().getDrawable(R.drawable.selector_pit_stand_state, null));
            mBinding.tvGrid.setText((position + 1 - deviceNum.numSit) + "");
            mBinding.tvCleanessGas.setVisibility(View.INVISIBLE);
            mBinding.viewGrid.setBackgroundResource(item.work ? R.drawable.selector_pit_stand_state : R.drawable.man_r);
            mBinding.tvWarning.setVisibility(View.GONE);
        } else if (item.type == 2) {
            mBinding.viewGrid.setBackground(mContext.getResources().getDrawable(R.drawable.selector_wash_state, null));
            mBinding.tvGrid.setText((position + 1 - deviceNum.numSit - deviceNum.numStand) + "");
            mBinding.tvCleanessGas.setVisibility(View.INVISIBLE);
            mBinding.viewGrid.setBackgroundResource(item.work ? R.drawable.selector_wash_state : R.drawable.wash_r);
            mBinding.tvWarning.setVisibility(View.GONE);

        } else if (item.type == 3) {
            mBinding.viewGrid.setBackground(mContext.getResources().getDrawable(R.drawable.selector_gas_state, null));
            mBinding.tvGrid.setText((position + 1 - deviceNum.numSit - deviceNum.numStand - deviceNum.numWash) + "");
            mBinding.tvCleanessGas.setVisibility(View.VISIBLE);
            mBinding.tvCleanessGas.setText("等级:" + ((item.gasLevel == -1) ? "无" : level2Str(item.gasLevel, 1)));
            mBinding.viewGrid.setBackgroundResource(item.work ? R.drawable.selector_gas_state : R.drawable.gas_r);
            //气味等级报警：0表示气味最小
            mBinding.tvWarning.setVisibility(item.gasLevel < 2 ? View.GONE : View.VISIBLE);

        }
        mBinding.viewGrid.setSelected(item.used);

        return convertView;
    }

    private String[] levelStrs = new String[]{"优", "良", "中", "差"};

    /**
     * 将等级数字转变成"优良中差"文字
     * TODO 核对这个规则是否合适
     *
     * @param level 值为0,1,2,3不等，分别表示0%，25%，50%，75%
     * @param type  0 代表蹲位清洁度，其他值代表气味
     * @return
     */
    private String level2Str(int level, int type) {
        if (type == 0) {
            // 对于蹲位，level=3表示最干净
            //return levelStrs[levelStrs.length - level - 1];
            return levelStrs[level];
        } else if (type == 3) {
            // 对于气体探测器，level=3表示气味最大
            return levelStrs[level];
        }
        //其他默认
        return levelStrs[level];

    }

    /**
     * 更新itemList中，从start开始的位置
     *
     * @param itemList
     */
    public void updateData(List<Bus485Pit.Item> itemList, int start) {
        int index = start;
        for (int i = 0; i < itemList.size(); i++) {
//            deepCopy(this.itemList.get(index), itemList.get(i));
            this.itemList.set(index, itemList.get(i));
            index++;
        }
        this.notifyDataSetChanged();
    }

    /**
     * TODO 是否需要这样
     *
     * @param item1
     * @param item2
     */
    public static void deepCopy(Bus485Pit.Item item1, Bus485Pit.Item item2) {
        item1.used = item2.used;
        item1.work = item2.work;
        item1.save = item2.save;

    }

    /**
     * 更新单个值
     *
     * @param itemPit
     * @param position
     */
    @Deprecated
    public void updateData(Bus485Pit.ItemPit itemPit, int position) {
        if (position < itemList.size()) {
//            itemList.get(position).update(itemPit);
            this.notifyDataSetChanged();
        }
    }

}
