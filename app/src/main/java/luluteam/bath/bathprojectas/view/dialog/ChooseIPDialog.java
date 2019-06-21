package luluteam.bath.bathprojectas.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import luluteam.bath.bathprojectas.R;
import luluteam.bath.bathprojectas.constants.APPConstant;


/**
 * Created by JJY on 2016/9/14.
 */
public class ChooseIPDialog extends Dialog {
    private RadioGroup radioGroup;
    private RadioButton testServer_rb, caludyServer_rb, aliCloudServer_rb, tencentCloudServer_rb;
    private Button positiveButton, negativeButton;
    private TextView title;

    public ChooseIPDialog(Context context) {
        super(context, R.style.MyDialog);
        setCustomDialog();
        getWindow().setLayout((int) (APPConstant.SCREEN_WIDTH * 0.8), ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void setCustomDialog() {
        View mView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_chooseip, null);
        title = (TextView) mView.findViewById(R.id.title);
        radioGroup = (RadioGroup) mView.findViewById(R.id.radiogroup);
//        testServer_rb = (RadioButton) mView.findViewById(R.id.testServer_rb);
//        caludyServer_rb = (RadioButton) mView.findViewById(R.id.claudyServer_rb);
//        aliCloudServer_rb = (RadioButton) mView.findViewById(R.id.aliCloudServer_rb);
//        tencentCloudServer_rb = (RadioButton) mView.findViewById(R.id.tencentCloudServer_rb);
        positiveButton = (Button) mView.findViewById(R.id.acceptbtn);
        //ViewGroup.LayoutParams params=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        String ipserver = SharedPreferencesUtil.getString(getContext(), APPConstant.SharedPreferenceStr.CHECKED_SERVER);
//        System.out.println("SharedPreferencesUtil.getString()==" + SharedPreferencesUtil.getString(getContext(), APPConstant.SharedPreferenceStr.CHECKED_SERVER));

        this.setContentView(mView);
    }

    public void setCheckedId(int id) {
        radioGroup.check(id);
    }

    public int getCheckedId() {
        return radioGroup.getCheckedRadioButtonId();
    }


    /**
     * 确定键监听器
     *
     * @param listener
     */
    public void setOnPositiveListener(View.OnClickListener listener) {
        positiveButton.setOnClickListener(listener);
    }


}