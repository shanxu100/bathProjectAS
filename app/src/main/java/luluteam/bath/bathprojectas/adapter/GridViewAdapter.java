package luluteam.bath.bathprojectas.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import luluteam.bath.bathprojectas.R;
import luluteam.bath.bathprojectas.view.dialog.LoadingDialog;

/**
 * 图片显示
 * Created by TC on 2018/4/10.
 */

public class GridViewAdapter extends BaseAdapter {

    private int itemWidth = 0;
    private ArrayList<Integer> resoucesList;
    private ArrayList<String> urlList = new ArrayList<>();
    private LoadingDialog loadingDialog;//用于加载数据时候的一个缓冲
    private Context context;

    private static final String TAG = "GridViewAdapter";


    public GridViewAdapter(List<String> urlList, Context context) {
//        this.urlList = (ArrayList<String>) urlList;
        this.urlList.clear();
        this.urlList.addAll(urlList);
        this.context = context;
        loadingDialog = new LoadingDialog(context);
    }

    public void setItemHeight(int itemWidth) {
        this.itemWidth = itemWidth;
    }

    /**
     * 通知数据实时改变
     */
    public void notifyDataChange(List<String> urlList) {
        this.urlList.clear();
        this.urlList.addAll(urlList);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return urlList.size();
//        return resoucesList.size();
    }

    @Override
    public Object getItem(int position) {
        return urlList.get(position);
//        return resoucesList.get(position);
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
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_gridview_item, null);
            holder.itemImg = (ImageView) convertView.findViewById(R.id.tabItemIcon_iv);
            holder.itemImg.setScaleType(ImageView.ScaleType.FIT_CENTER);
            convertView.setTag(holder);
//            int length = DisplayUtil.getScreenHeight() < DisplayUtil.getScreenWidth()
//                    ? DisplayUtil.getScreenHeight() : DisplayUtil.getScreenWidth();
//            if (length != 0) {
//                length = (length - 30) / 3;
//                Log.e(TAG, "convertView==null  length=" + length);
//                convertView.setMinimumWidth(length);
//                convertView.setMinimumHeight(length);
//            }
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Glide.with(context)
                .load(urlList.get(position))
                .into(holder.itemImg)
        ;

        return convertView;
    }

    /**
     * 图片列表显示的ViewHolder 内部类
     */
    public class ViewHolder {
        public ImageView itemImg;
    }
}
