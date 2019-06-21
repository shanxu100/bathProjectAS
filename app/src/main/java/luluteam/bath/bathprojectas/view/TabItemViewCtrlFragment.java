package luluteam.bath.bathprojectas.view;

/**
 * Created by Guan on 2018/3/14.
 */

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import luluteam.bath.bathprojectas.R;

/**
 * TabItem的实体类
 */
public class TabItemViewCtrlFragment extends LinearLayout {

    public int iconDrawable;
    public int bgDrawable;

    public ImageView ivIcon;

    public TabItemViewCtrlFragment(Context context, int iconDrawable,
                                   int bgDrawable) {
        super(context);
        this.iconDrawable = iconDrawable;
        this.bgDrawable = bgDrawable;
        init();
    }

    /**
     * 初始化
     */
    public void init() {
        View view = LayoutInflater.from(super.getContext()).inflate(R.layout.view_tab_item_ctrl, this);
        ivIcon = (ImageView) view.findViewById(R.id.tabItemIcon_iv);

        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        view.setLayoutParams(layoutParams);

        if (iconDrawable != 0) {
            ivIcon.setImageResource(iconDrawable);
        }
        if (bgDrawable != 0) {
            this.setBackgroundResource(bgDrawable);
        }
    }

    /**
     * 包含了一组Fragment数据：fragmentTag、fragment、tabItemView
     */
    public static class ItemHolder {
        public String fragmentTag;
        public Fragment fragment;
        public TabItemViewCtrlFragment tabItemView;

        public ItemHolder(String fragmentTag, Fragment fragment, TabItemViewCtrlFragment tabItemView) {
            this.fragmentTag = fragmentTag;
            this.fragment = fragment;
            this.tabItemView = tabItemView;
        }
    }
}
