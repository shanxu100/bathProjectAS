package luluteam.bath.bathprojectas.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import luluteam.bath.bathprojectas.R;
import luluteam.bath.bathprojectas.constants.APPConstant;
import luluteam.bath.bathprojectas.databinding.DialogCleanessBinding;
import luluteam.bath.bathprojectas.databinding.DialogGasBinding;
import luluteam.bath.bathprojectas.model.pit.Bus485Pit;

/**
 * @author Guan
 */
public class CleanessDialog extends Dialog {

    private Context context;
    private Bus485Pit.Item item;
    private int position;

    private DialogCleanessBinding mBinding;
    private DialogGasBinding gasBinding;

    private int type;
    public static final int TYPE_PIT = 0;
    public static final int TYPE_GAS = 1;

    public int getType() {
        return type;
    }

    public CleanessDialog(@NonNull Context context, Bus485Pit.Item item, int position, int type) {
        super(context);
        this.context = context;
        this.position = position;
        this.type = type;
        this.item = item;
        setCustomDialog(item);
        //
        getWindow().setLayout((int) (APPConstant.SCREEN_WIDTH * 0.8), ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public void setSinglePitRefreshListener(BtnClickCallback callback) {
        if (mBinding != null) {
            mBinding.btnRefreshSinglePit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onItemPosition(position);
                }
            });
        }
        if (gasBinding != null) {
            gasBinding.btnRefreshSingleGas.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onItemPosition(position);
                }
            });
        }
    }

    private void setCustomDialog(Bus485Pit.Item item) {
        if (item == null) {
            return;
        }
        if (this.item instanceof Bus485Pit.ItemPit) {
            mBinding = DataBindingUtil
                    .inflate(LayoutInflater.from(context), R.layout.dialog_cleaness, null, false);
            setContentView(mBinding.getRoot());
        } else if (this.item instanceof Bus485Pit.ItemGas) {
            gasBinding = DataBindingUtil
                    .inflate(LayoutInflater.from(context), R.layout.dialog_gas, null, false);
            setContentView(gasBinding.getRoot());
        }
        freshItemInfo(item);

    }

    public void freshItemInfo(Bus485Pit.Item item) {
        this.item = item;
        if (this.item instanceof Bus485Pit.ItemPit) {
            setPitSitDetail((Bus485Pit.ItemPit) item);
        } else if (this.item instanceof Bus485Pit.ItemGas) {
            setGasDetail((Bus485Pit.ItemGas) item);
        }
    }

    private void setPitSitDetail(Bus485Pit.ItemPit itemPit) {
        String position = "";
        mBinding.tvOrdurePosition.setTextColor(ContextCompat.getColor(context, R.color.red));
        switch (itemPit.ordurePoision) {
            case 0:
                position = "无污物";
                mBinding.tvOrdurePosition.setTextColor(ContextCompat.getColor(context, R.color.green));
                break;
            case 1:
                position = "坑内有污物";
                break;
            case 2:
                position = "坑外有污物";
                break;
            default:
                position = "未知:" + itemPit.ordurePoision;
        }
        mBinding.tvOrdurePosition.setText(position);
        String type = "";
        mBinding.tvOrdureType.setTextColor(ContextCompat.getColor(context, R.color.red));
        switch (itemPit.ordureType) {
            case 0:
                type = "无污物";
                mBinding.tvOrdureType.setTextColor(ContextCompat.getColor(context, R.color.green));
                break;
            case 1:
                type = "纸屑";
                break;
            case 2:
                type = "烟头";
                break;
            default:
                type = "未知:" + itemPit.ordureType;
        }
        mBinding.tvOrdureType.setText(type);
    }

    private void setGasDetail(Bus485Pit.ItemGas itemGas) {
        gasBinding.tvGasDetail.setText("" +
                "温度：" + itemGas.temperature + "" +
                "\n湿度：" + itemGas.humidity + "" +
                "\nNH3气体：" + itemGas.NH3 + "" +
                "\nH2S气体：" + itemGas.H2S + "" +
                "\n气体异味综合指数：" + itemGas.value);
    }


    public int getPosition() {
        return position;
    }

    public interface BtnClickCallback {
        void onItemPosition(int position);
    }


}
