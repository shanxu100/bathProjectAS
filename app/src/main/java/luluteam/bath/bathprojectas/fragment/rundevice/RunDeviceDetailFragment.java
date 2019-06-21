package luluteam.bath.bathprojectas.fragment.rundevice;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import luluteam.bath.bathprojectas.R;
import luluteam.bath.bathprojectas.model.RunDeviceDetail;

/**
 * 显示男厕、女厕或者残卫设备运行状态详情
 *
 * @author
 */
public class RunDeviceDetailFragment extends Fragment {

    private ListView runDetailLv;
    private RunDeviceAdapter adapter;


    public void setDeviceAdapter(RunDeviceAdapter deviceAdapter) {
        this.adapter = deviceAdapter;
        this.adapter.notifyDataSetChanged();
    }

    public void refreshData(List<RunDeviceDetail> runDeviceDetailList) {
        adapter.runDeviceDetailList = runDeviceDetailList;
        adapter.notifyDataSetChanged();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.run_device_detail_fragment, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        runDetailLv = (ListView) view.findViewById(R.id.run_device_detail_lv);
        if (adapter != null) {
            runDetailLv.setAdapter(adapter);
        }
    }

    /**
     * 显示详情list的adapter
     */
    public static class RunDeviceAdapter extends ArrayAdapter<RunDeviceDetail> {

        private List<RunDeviceDetail> runDeviceDetailList;
        private Context context;

        public RunDeviceAdapter(@NonNull Context context, int resource, List<RunDeviceDetail> runDeviceDetailList) {
            super(context, resource, runDeviceDetailList);
            this.runDeviceDetailList = runDeviceDetailList;
            this.context = context;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.run_device_item, null);
                viewHolder = new ViewHolder();
                viewHolder.runDeviceName = (TextView) convertView.findViewById(R.id.run_device_name);
                viewHolder.runDeviceStateImg = (ImageView) convertView.findViewById(R.id.run_device_state_img);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.runDeviceName.setText(runDeviceDetailList.get(position).getDeviceName());
            if (runDeviceDetailList.get(position).isWork()) {
                viewHolder.runDeviceStateImg.setImageResource(R.mipmap.ic_no_warning);
            } else {
                viewHolder.runDeviceStateImg.setImageResource(R.mipmap.ic_warning);
            }

            return convertView;
        }

        public class ViewHolder {
            TextView runDeviceName;
            ImageView runDeviceStateImg;
        }
    }
}
