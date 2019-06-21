package luluteam.bath.bathprojectas.utils;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import luluteam.bath.bathprojectas.view.dialog.datePickDialog.DatePick;
import luluteam.bath.bathprojectas.view.dialog.datePickDialog.DatePickDialog;

public class DatePickDialogUtil {
    public static void setEditTextFocus(Context context, EditText editText, int type) {
        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    editText.requestFocus();
                    if (editText.isFocused()) {
//                    editText.clearFocus();
                        showDatePickDialog(context, editText, type);
                    }
                    editText.clearFocus();
                }
                return false;
            }
        });
    }

    public static void showDatePickDialog(Context mContext, EditText editText, int type) {
        DatePickDialog.Builder builder = new DatePickDialog.Builder(mContext);
        DatePickDialog dialog = builder.build();
        builder.setTitle("日期时间选择框")
                .setDatePickType(type)
                .setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatePick pick = dialog.getPick();
                        editText.setText(pick.getResultDate());
                        dialog.dismiss();
                    }
                })
                .setCancelButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
        dialog.show();
    }
}
