package luluteam.bath.bathprojectas.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import luluteam.bath.bathprojectas.R;
import luluteam.bath.bathprojectas.constants.APPConstant;

/**
 * Created by Guan on 2018/1/8.
 */

public class TipDialog extends Dialog {
    private TextView message_tv;
    private Button sure_btn;

    public TipDialog(@NonNull Context context) {
        super(context, R.style.MyDialog);
        setCustomDialog();
        getWindow().setLayout((int) (APPConstant.SCREEN_WIDTH * 0.8), ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void setCustomDialog() {
        View mView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_tip, null);
        message_tv = (TextView) mView.findViewById(R.id.message_tv);
        sure_btn = (Button) mView.findViewById(R.id.sure_btn);
        this.setContentView(mView);
    }

    public void setMessage(String message) {
        this.message_tv.setText(message);
    }

    public void setClickListener(View.OnClickListener listener) {
        sure_btn.setOnClickListener(listener);
    }
}
