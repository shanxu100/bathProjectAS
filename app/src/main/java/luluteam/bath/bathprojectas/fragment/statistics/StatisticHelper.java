package luluteam.bath.bathprojectas.fragment.statistics;

import android.graphics.Color;
import android.util.Log;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.gson.Gson;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import luluteam.bath.bathprojectas.constants.APPConstant;
import luluteam.bath.bathprojectas.constants.WebConstant;
import luluteam.bath.bathprojectas.model.StatisticAnalysis.RecordInfoItem;
import luluteam.bath.bathprojectas.model.StatisticAnalysis.RecordInfoResult;
import luluteam.bath.bathprojectas.utils.okhttp.OkHttpManager;

/**
 * Created by Administrator on 2018/3/20 0020.
 */

public class StatisticHelper {
    private String toiletId = "00000001";
    private String usage = "1";

    public static final String TAG = "StatisticHelper";

    public void setLineChartView(String deviceType, String type, ResultCallback callback) {
        toiletId = APPConstant.TOILETID;
        if ("Q".equals(deviceType) || "R".equals(deviceType)) {
            usage = APPConstant.UsageMap.get("公用");
        } else if ("K".equals(deviceType)) {
            usage = "-1";
        } else {
            usage = APPConstant.USAG;
        }
        if (StringUtils.isEmpty(toiletId) || StringUtils.isEmpty(usage)) {
            return;
        }
        HashMap<String, String> params = new HashMap<>();
        params.put("toiletId", toiletId);
        if (!"K".equals(deviceType)) {
            params.put("usage", usage);
        }
        params.put("deviceType", deviceType);
        params.put("type", type);
        OkHttpManager.CommonPostAsyn(WebConstant.GET_USE_TIME, params, new OkHttpManager.ResultCallback() {
            @Override
            public void onCallBack(OkHttpManager.State state, String result) {
                Log.i(TAG, "图表统计 statsManager getDate: url=" + WebConstant.GET_USE_TIME + "  params=" + params);
                Log.i(TAG, "图表统计 statsManager getDate: state=" + state + "  Result=" + result);
                if (state == OkHttpManager.State.SUCCESS) {
                    Gson gson = new Gson();
                    RecordInfoResult infoResult = gson.fromJson(result, RecordInfoResult.class);
                    if (!infoResult.isResult()) {
                        infoResult = null;
                    }
                    if (callback != null) {
                        callback.onResult(infoResult);
                    }
                }
            }
        });
    }

    public interface ResultCallback {
        void onResult(RecordInfoResult recordInfoResult);
    }

    public static final int ONLY_TIMES = 0;
    public static final int ONLY_VALUES = 1;
    public static final int BOTH_TIMES_VLAUES = 2;

    public LineData getLineData(RecordInfoResult mResult, String[] labels, int type) {
        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<Entry> entries2 = new ArrayList<>();
        if (mResult != null) {
            List<RecordInfoItem> dataList = mResult.getDataList();
            for (int i = 0; i < dataList.size(); i++) {
                switch (type) {
                    case ONLY_TIMES:
                        entries.add(new Entry(i, dataList.get(i).getTimes()));
                        break;
                    case ONLY_VALUES:
                        entries.add(new Entry(i, dataList.get(i).getValue()));
                        break;
                    case BOTH_TIMES_VLAUES:
                        entries.add(new Entry(i, dataList.get(i).getTimes()));
                        entries2.add(new Entry(i, dataList.get(i).getValue()));
                        break;
                }

            }
            ArrayList<ILineDataSet> sets = new ArrayList<>();
            if (labels != null) {
                switch (labels.length) {
                    case 1:
                        sets.add(getDataSet(entries, labels[0], Color.BLUE));
                        break;
                    case 2:
                        sets.add(getDataSet(entries, labels[0], Color.BLUE));
                        sets.add(getDataSet(entries2, labels[1], Color.GRAY));

                }
            }
            return new LineData(sets);
        }
        return null;
    }

    private LineDataSet getDataSet(List<Entry> entries, String label, int color) {
        LineDataSet dataSet = new LineDataSet(entries, label);
        dataSet.setLineWidth(2.5f);
        dataSet.setCircleRadius(4.5f);
        dataSet.setHighLightColor(Color.rgb(244, 117, 117));
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet.setDrawValues(true);
        dataSet.setCircleColor(color);
        dataSet.setColor(color);
        //实心
        dataSet.setDrawCircleHole(false);
        return dataSet;
    }
}
