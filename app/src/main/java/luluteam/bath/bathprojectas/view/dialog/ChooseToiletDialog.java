package luluteam.bath.bathprojectas.view.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import luluteam.bath.bathprojectas.R;
import luluteam.bath.bathprojectas.constants.APPConstant;
import luluteam.bath.bathprojectas.model.ToiletInfo;

/**
 * Created by TC on 2018/4/15.
 */

public class ChooseToiletDialog extends Dialog {
    private RadioGroup radioGroup;
    private List<ToiletInfo.ToiletItem> toiletList;
    private Button sure_btn;
    private TextView toiletAddress;
    private Context context;
    private RadioButton preRb;

    public ChooseToiletDialog(@NonNull Context context, ToiletInfo toiletInfo) {
        super(context, R.style.MyDialog);
        this.context = context;
        if (toiletInfo != null) {
            this.toiletList = toiletInfo.getToiletList();
        }
        setCustomDialog();
        getWindow().setLayout((int) (APPConstant.SCREEN_WIDTH * 0.8), ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @SuppressLint("SetTextI18n")
    private void setCustomDialog() {
        View mView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_choose_toilet, null);
        radioGroup = (RadioGroup) mView.findViewById(R.id.toiletId_radioGroup);
        sure_btn = (Button) mView.findViewById(R.id.sure_btn);
        toiletAddress = (TextView) mView.findViewById(R.id.toiletAddress);
        if (toiletList == null || toiletList.size() == 0) {
            toiletList = new ArrayList<>();
        } else {
            toiletAddress.setText(toiletList.get(0).getProvince()
                    + "--" + toiletList.get(0).getCity()
                    + "--" + toiletList.get(0).getCounty());
            Log.i("选择厕所toiletId dialog：", toiletAddress.getText().toString());
        }
        for (int i = 0; i < toiletList.size(); i++) {
            RadioButton radioButton = new RadioButton(context);
            //设置RadioButton背景
            //radioButton.setBackgroundResource(R.drawable.xx);
            //设置RadioButton的样式
            //radioButton.setButtonDrawable(R.drawable.radio_bg);
            //设置文字距离四周的距离
            //radioButton.setPadding(80, 0, 0, 0);
            //设置文字
            radioButton.setText(toiletList.get(i).getToiletId() + "\t\t" + toiletList.get(i).getCompanyName());
            radioButton.setId(i);
            radioButton.setTextSize(20);
            if (i == 0) {
                radioButton.setChecked(true);
                radioButton.setTextColor(getContext().getResources().getColor(R.color.primaryGreen, null));
                preRb = radioButton;
            }
            //将radioButton添加到radioGroup中
            radioGroup.addView(radioButton);
        }
        this.setContentView(mView);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton rb = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());
                rb.setTextColor(getContext().getResources().getColor(R.color.primaryGreen, null));
                preRb.setTextColor(getContext().getResources().getColor(R.color.black, null));
                preRb = rb;
            }
        });
        this.setCanceledOnTouchOutside(false);
    }

    public String getCheckedToiletId() {
        if (toiletList.size() == 0) {
//            return APPConstant.TOILETID;
            return "";

        }
        return toiletList.get(radioGroup.getCheckedRadioButtonId()).getToiletId();
    }

    public void setClickListener(View.OnClickListener listener) {
        sure_btn.setOnClickListener(listener);
    }
}
