package luluteam.bath.bathprojectas.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import luluteam.bath.bathprojectas.R;
import luluteam.bath.bathprojectas.constants.APPConstant;

/**
 * Created by luluteam on 2017/10/30.
 */

public class LoadingDialog extends Dialog {

    private TextView loadingText;

    public LoadingDialog(@NonNull Context context) {
        super(context, R.style.MyDialogNODim);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null);
        loadingText = (TextView) view.findViewById(R.id.loadingtxt);
        setContentView(view);
        getWindow().setLayout((int) (APPConstant.SCREEN_WIDTH * 0.5), (int) (APPConstant.SCREEN_WIDTH * 0.5));
    }

    /**
     * 带定时效果显示dialog
     *
     * @param timeout
     */
    public void showWithTimeout(long timeout) {
        if (timeout < 0) {
            timeout = 3000;
        }
        this.show();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                dismiss();
            }
        }, timeout);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
    }

    public void setLoadingText(String text) {
        loadingText.setText(text);
    }
}
