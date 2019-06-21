package luluteam.bath.bathprojectas.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.commons.lang.StringUtils;

import luluteam.bath.bathprojectas.R;
import luluteam.bath.bathprojectas.constants.APPConstant;

public class ChangePwdDialog extends Dialog {

    private Context context;
    private EditText oldPwd_et, newPwd_repeat_et, newPwd_et;
    private Button sure_btn, cancle_btn;
    private TextView info_tv;

    private View.OnClickListener positiveListener, negativeListener;

    private ChangePwdCallback callback;
    private ChangePwdDialog dialog;
    private String realOldPwd;


    public ChangePwdDialog(@NonNull Context context) {
        super(context, R.style.MyDialog);
        this.context = context;
        dialog = this;
        setCustomDialog();
        this.setCanceledOnTouchOutside(false);
        getWindow().setLayout((int) (APPConstant.SCREEN_WIDTH * 0.8), ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void setCustomDialog() {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_change_pwd, null);

        oldPwd_et = (EditText) view.findViewById(R.id.oldPwd_et);
        newPwd_et = (EditText) view.findViewById(R.id.newPwd_et);
        newPwd_repeat_et = (EditText) view.findViewById(R.id.newPwd_repeat_et);

        sure_btn = (Button) view.findViewById(R.id.sure_btn);
        cancle_btn = (Button) view.findViewById(R.id.cancle_btn);

        info_tv = (TextView) view.findViewById(R.id.info_tv);

        sure_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StringUtils.isEmpty(realOldPwd) || !realOldPwd.equals(getOldPwd())) {
                    info_tv.setText("旧密码不正确，请检查");
                    return;
                }
                callback.onChange(getOldPwd(), getNewPwd());
            }
        });
        cancle_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        this.setContentView(view);
    }

    public void setChangePwdCallback(ChangePwdCallback callback) {
        this.callback = callback;
    }


    public void setRealOldPwd(String realOldPwd) {
        this.realOldPwd = realOldPwd;
    }

    public String getOldPwd() {
        String oldPwd = oldPwd_et.getText().toString();
        if (StringUtils.isEmpty(oldPwd)) {
            info_tv.setText("请输入旧密码");
            return "";
        }
        return oldPwd;
    }

    public String getNewPwd() {
        String newPwd = newPwd_et.getText().toString();
        String newPwdRepeat = newPwd_repeat_et.getText().toString();
        if (!newPwd.equals(newPwdRepeat)) {
            info_tv.setText("两次输入的新密码不一致，请检查");
            return "";
        } else {
            return newPwd;
        }

    }

    public interface ChangePwdCallback {
        void onChange(String oldPwd, String newPwd);
    }

}
