package luluteam.bath.bathprojectas.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import luluteam.bath.bathprojectas.R;
import luluteam.bath.bathprojectas.constants.APPConstant;
import luluteam.bath.bathprojectas.utils.SharedPreferencesUtil;

/**
 * Created by Guan on 2017/12/11.
 */

public class ChooseUsageDialog extends Dialog {

    private Context context;
    private RadioGroup usage_radiogroup;
    private Button sure_btn, cancle_btn;
    private View.OnClickListener listener;
    private TextView titleTv;


    public ChooseUsageDialog(@NonNull Context context) {
        super(context, R.style.MyDialog);
        this.context = context;
        setCustomDialog();
        getWindow().setLayout((int) (APPConstant.SCREEN_WIDTH * 0.8), ViewGroup.LayoutParams.WRAP_CONTENT);

    }

    private void setCustomDialog() {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_choose_usage, null);
        usage_radiogroup = (RadioGroup) view.findViewById(R.id.usage_radiogroup);
        sure_btn = (Button) view.findViewById(R.id.sure_btn);
        cancle_btn = (Button) view.findViewById(R.id.cancle_btn);
        titleTv = (TextView) view.findViewById(R.id.title);

        cancle_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        this.setContentView(view);

        //显示上次存储的值
        String usage = SharedPreferencesUtil.getString(context, "usage");
        if (usage != null) {
            switch (usage) {
                case "1":
                    usage_radiogroup.check(R.id.bathMan);
                    break;
                case "2":
                    usage_radiogroup.check(R.id.bathWomen);
                    break;
                case "3":
                    usage_radiogroup.check(R.id.bathThird);
                    break;
            }

        }
        String toiletId = SharedPreferencesUtil.getString(context, APPConstant.USERNAME + "_toiletId");
        String toiletName = SharedPreferencesUtil.getString(context, APPConstant.USERNAME + "_toiletNickname");
        if (toiletId != null && toiletName != null) {
            titleTv.setText(toiletId + " " + toiletName);
        } else {
            titleTv.setText("还未选择厕所");
        }

    }

    public void setClickSureBtnListener(View.OnClickListener listener) {
        this.listener = listener;
        sure_btn.setOnClickListener(listener);
    }

    public String getCheckedUsage() {
        String usage = "1";
        switch (usage_radiogroup.getCheckedRadioButtonId()) {
            case R.id.bathMan:
                usage = "1";
                break;
            case R.id.bathWomen:
                usage = "2";
                break;
            case R.id.bathThird:
                usage = "3";
        }
        return usage;
    }

}
