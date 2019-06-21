package luluteam.bath.bathprojectas.view.setting;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.apache.commons.lang.StringUtils;

import luluteam.bath.bathprojectas.R;
import luluteam.bath.bathprojectas.filters.InputFilterMinMax;
import luluteam.bath.bathprojectas.model.wash.WashParams;

public class WashSetView extends LinearLayout {

    //    private WashSetViewBinding mBinding;
    private WashParams.Group washSetGroup;
    private EditText washRunTime;
    private EditText washDelayTime;
    private CheckBox washSelected;
    private static Handler handler;

    private UpdateParamsListener updateParamsListener;

    private static final String TAG = "WashSetView";

    public void setUpdateParamsListener(UpdateParamsListener updateParamsListener) {
        this.updateParamsListener = updateParamsListener;
    }

    public WashSetView(Context context) {
        super(context);
        init(context);
    }

    public WashSetView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public WashSetView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
//        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.wash_set_view,this,true);
        View view = LayoutInflater.from(context).inflate(R.layout.wash_set_view, this, true);
        washRunTime = (EditText) view.findViewById(R.id.wash_run_time);
        washDelayTime = (EditText) view.findViewById(R.id.wash_delay_time);
        washSelected = (CheckBox) view.findViewById(R.id.wash_selected);

        washRunTime.setFilters(new InputFilter[]{new InputFilterMinMax(0, 15)});
        washDelayTime.setFilters(new InputFilter[]{new InputFilterMinMax(0, 15)});

        setListeners();

    }


    public void setWashSetGroup(WashParams.Group washSetGroup, int viewIndex) {
        this.washSetGroup = washSetGroup;
        setWashRunTime(viewIndex);
        setWashDelayTime(viewIndex);
        setWashSelected(viewIndex);
    }

    /**
     * 添加监听器，监听 焦点 的变化。
     * 失去焦点时进行赋值。
     */
    private void setListeners() {
        washRunTime.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b && !washDelayTime.isFocused()) {
                    if (updateParamsListener != null) {
                        updateParamsListener.onLostFocus((int) view.getTag(), getEditTextString2Int(washRunTime)
                                , getEditTextString2Int(washDelayTime)
                                , washSelected.isChecked());
                    }
                }
            }
        });
        washDelayTime.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b && !washRunTime.isFocused()) {
                    if (updateParamsListener != null) {
                        updateParamsListener.onLostFocus((int) view.getTag(), getEditTextString2Int(washRunTime)
                                , getEditTextString2Int(washDelayTime)
                                , washSelected.isChecked());
                    }
                }
            }
        });

        washSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (updateParamsListener != null) {
                    updateParamsListener.onLostFocus((int) buttonView.getTag(), getEditTextString2Int(washRunTime)
                            , getEditTextString2Int(washDelayTime)
                            , washSelected.isChecked());
                }
            }
        });
    }

    private void setWashRunTime(int viewIndex) {
        washRunTime.setTag(viewIndex);
        if (washSetGroup.runningTime >= 0 && washSetGroup.runningTime <= WashParams.MAX_TIME) {
            washRunTime.setText(washSetGroup.runningTime + "");
        } else {
            washRunTime.setText(WashParams.DEFAULT_RUNNING_TIME + "");
        }

    }

    private void setWashDelayTime(int viewIndex) {
        washDelayTime.setTag(viewIndex);
        if (washSetGroup.intervalTime >= 0 && washSetGroup.intervalTime <= WashParams.MAX_TIME) {
            washDelayTime.setText(washSetGroup.intervalTime + "");
        } else {
            washDelayTime.setText(WashParams.DEFAULT_RUNNING_TIME + "");
        }

    }

    private void setWashSelected(int viewIndex) {
        washSelected.setTag(viewIndex);
        washSelected.setChecked(washSetGroup.selected);
    }

    private int getEditTextString2Int(EditText editText) {
        String str = editText.getText().toString();
        if (StringUtils.isNotEmpty(str) && StringUtils.isNumeric(str)) {
            int time = Integer.parseInt(str);
            editText.setText((time >= 0 && time <= WashParams.MAX_TIME) ? time + "" : WashParams.DEFAULT_RUNNING_TIME + "");
            return (time >= 0 && time <= WashParams.MAX_TIME) ? time : WashParams.DEFAULT_RUNNING_TIME;
        } else {
            editText.setText(WashParams.DEFAULT_RUNNING_TIME + "");
        }
        return WashParams.DEFAULT_RUNNING_TIME;
    }

    public interface UpdateParamsListener {
        void onLostFocus(int viewIndex, int runTime, int delayTime, boolean selected);
    }
}
