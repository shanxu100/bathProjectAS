package luluteam.bath.bathprojectas.view.dialog.datePickDialog;


import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import luluteam.bath.bathprojectas.R;

/**
 * Created by luluteam on 2017/11/30.
 */

public class DatePickDialog extends Dialog {

    private static LinearLayout datePickLayout;
    private static DatePick pick;
    private static TextView title;
    private static DatePickDialog dialog;
    private static Button okBtn;
    private static Button cancelBtn;

    private static View view;


    public DatePickDialog(@NonNull Context context) {
        super(context, R.style.MyDialogDim);
        view = LayoutInflater.from(context).inflate(R.layout.dialog_datapick, null);
        datePickLayout = (LinearLayout) view.findViewById(R.id.layout_datepick_choice);
        pick = getDatePickWithType(context, DatePick.TYPE_ALL);
        datePickLayout.addView(pick);
        okBtn = (Button) view.findViewById(R.id.dialog_ok_btn);
        cancelBtn = (Button) view.findViewById(R.id.dialog_cancel_btn);
        title = (TextView) view.findViewById(R.id.dialog_datepick_title);
//        datePickLayout.addView(view);
        setContentView(view);
//        getWindow().setLayout((int)(APPConstant.SCREEN_WIDTH*0.8),(int)(APPConstant.SCREEN_WIDTH*0.5));
    }

    @Override
    public void setContentView(@NonNull View view) {
        super.setContentView(view);
    }

    public DatePick getPick() {
        return pick;
    }

    public static class Builder {
        private Context mContext;

        public Builder(Context context) {
            mContext = context;
            dialog = new DatePickDialog(context);
        }

        public Builder setTitle(String titleStr) {
            title.setText(titleStr);
            return Builder.this;
        }

        public Builder setPositiveButton(String content, View.OnClickListener listener) {
            okBtn.setText(content);
            okBtn.setOnClickListener(listener);
            return Builder.this;
        }

        public Builder setDatePickType(int type) {
            pick = getDatePickWithType(mContext, type);
            datePickLayout.removeAllViews();
            datePickLayout.addView(pick);
            dialog.setContentView(view);

            return Builder.this;
        }

        public Builder setCancelButton(String content, View.OnClickListener listener) {
            cancelBtn.setText(content);
            cancelBtn.setOnClickListener(listener);
            return Builder.this;
        }

        public DatePickDialog build() {
            return dialog;
        }

        public void show() {
            dialog.show();
        }
    }

    private static DatePick getDatePickWithType(Context context, int type) {
        View view = null;
        switch (type) {
            case DatePick.TYPE_ONLYDATE:
                view = LayoutInflater.from(context).inflate(R.layout.datepick_onlydate, null);
                break;
            case DatePick.TYPE_ONLYTIME:
                view = LayoutInflater.from(context).inflate(R.layout.datepick_onlytime, null);
                break;
            case DatePick.TYPE_ALL:
                view = LayoutInflater.from(context).inflate(R.layout.datepick_all, null);
                break;
        }

        return (DatePick) view;
    }
}
