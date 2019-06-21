package luluteam.bath.bathprojectas.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import java.util.Arrays;

import luluteam.bath.bathprojectas.R;
import luluteam.bath.bathprojectas.constants.APPConstant;
import luluteam.bath.bathprojectas.view.spinner.JJYSpinner;

/**
 * Created by Guan on 2017/12/9.
 */
@Deprecated
public class ChooseToiletIdDialog extends Dialog {

    private Context context;
    private JJYSpinner toiletId_sp;
    private AutoCompleteTextView toiletId_autotv;

    private Button sure_btn, cancle_btn;
    private View.OnClickListener listener;

    public ChooseToiletIdDialog(@NonNull Context context) {
        super(context, R.style.MyDialog);
        this.context = context;
        setCustomDialog();
        loadToiletIds();
        getWindow().setLayout((int) (APPConstant.SCREEN_WIDTH * 0.8), ViewGroup.LayoutParams.WRAP_CONTENT);

    }

    private void setCustomDialog() {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_choose_toiletid, null);
        toiletId_sp = (JJYSpinner) view.findViewById(R.id.toiletId_sp);
        toiletId_autotv = (AutoCompleteTextView) view.findViewById(R.id.toiletId_autotv);
        sure_btn = (Button) view.findViewById(R.id.sure_btn);
        cancle_btn = (Button) view.findViewById(R.id.cancle_btn);
        cancle_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        this.setContentView(view);
    }

    private void loadToiletIds() {
//        SharedPreferencesUtil
        toiletId_sp.additem(APPConstant.TOILETIDS);
        ArrayAdapter arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, Arrays.asList(APPConstant.TOILETIDS));
        toiletId_autotv.setAdapter(arrayAdapter);
    }

    public void setClickSureBtnListener(View.OnClickListener listener) {
        this.listener = listener;
        sure_btn.setOnClickListener(listener);
    }

    public String getSelectedToiletId() {
//        return APPConstant.TOILETIDS[toiletId_sp.getSelectedposition()];
        return toiletId_autotv.getText().toString();
    }


}
