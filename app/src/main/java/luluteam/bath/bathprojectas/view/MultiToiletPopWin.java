package luluteam.bath.bathprojectas.view;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;

import luluteam.bath.bathprojectas.R;
import luluteam.bath.bathprojectas.activity.BaseActivity;
import luluteam.bath.bathprojectas.activity.SearchBathIdAty;


/**
 * Created by Guan on 2018/3/16.
 */

public class MultiToiletPopWin extends PopupWindow {

    BaseActivity mContext;
    private View mContentView;
    private Button single_toilet_btn, multi_toilet_btn;

    private int width, height;

    public static final String FROM_MULTI_POP = "multi_pop";


    public MultiToiletPopWin(BaseActivity context) {
        super(context);
        this.mContext = context;
        //打气
        mContentView = LayoutInflater.from(mContext).inflate(R.layout.popwin_multi_toilet, null);
        //设置View
        setContentView(mContentView);

        /**
         * 设置进出动画
         */
//        setAnimationStyle(R.style.popupViewAnimationStyle);
        /**
         * 设置背景只有设置了这个才可以点击外边和BACK消失
         */
        setBackgroundDrawable(new ColorDrawable());
        /**
         * 设置可以获取集点
         */
        setFocusable(true);
        /**
         * 设置点击外边可以消失
         */
        setOutsideTouchable(true);

        /**
         *设置可以触摸
         */
        setTouchable(true);


        /**
         * 初始化View与监听器
         */
        initView();

//        initListener();
    }

    private void initView() {
        multi_toilet_btn = (Button) mContentView.findViewById(R.id.multi_toilet_btn);
        single_toilet_btn = (Button) mContentView.findViewById(R.id.single_toilet_btn);

        multi_toilet_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SearchBathIdAty.class);
                intent.putExtra("from", FROM_MULTI_POP);
                mContext.startActivityForResult(intent, 1);
                dismissSelf();
            }
        });
        single_toilet_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissSelf();
            }
        });
    }

    public void show(View v) {
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        int measuredWidth = getWidth();
        int measuredHeight = getHeight();
        //显示在正上方
        showAtLocation(v, Gravity.NO_GRAVITY, (location[0] + v.getWidth() / 2) - measuredWidth / 2, location[1] - measuredHeight);
    }

    public void dismissSelf() {
        this.dismiss();
    }

}
