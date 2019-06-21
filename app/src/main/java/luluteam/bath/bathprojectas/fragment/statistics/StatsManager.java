package luluteam.bath.bathprojectas.fragment.statistics;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import luluteam.bath.bathprojectas.constants.WebConstant;
import luluteam.bath.bathprojectas.model.StatisticAnalysis.RecordInfoItem;
import luluteam.bath.bathprojectas.model.StatisticAnalysis.RecordInfoResult;
import luluteam.bath.bathprojectas.utils.okhttp.OkHttpManager;
import luluteam.bath.bathprojectas.view.mpchart.listviewitems.BarChartItem;
import luluteam.bath.bathprojectas.view.mpchart.listviewitems.ChartItem;
import luluteam.bath.bathprojectas.view.mpchart.listviewitems.CombinedChartItem;
import luluteam.bath.bathprojectas.view.mpchart.listviewitems.LineChartItem;

/**
 * Created by Guan on 2017/12/11.
 */

@Deprecated
public class StatsManager {

    private String toiletId = "12345678";
    private String usage = "1";
    private Context context;
    private ArrayList<ChartItem> chartItemList;
    private String[] deviceTypes;
    private String[] chartTitles;
    private ResultCallBack callBack;

    private String labelLine, labelBar;
    private ChartAdapter chartAdapter;


    private ChartItem.ChartType chartType;

    private AtomicInteger count;

    private List<RecordInfoResult> resultList;

    public StatsManager(Context context, String toiletId, String usage, String[] deviceTypes, String[] chartTitles, ChartItem.ChartType chartType) {
        this.context = context;
        this.toiletId = toiletId;
        this.usage = usage;
        this.deviceTypes = deviceTypes;
        this.chartTitles = chartTitles;
        this.chartType = chartType;


        count = new AtomicInteger(0);
        resultList = new ArrayList<>(deviceTypes.length);

        getData(deviceTypes);
        chartItemList = new ArrayList<>();

    }


    public void setLabelLine(String labelLine) {
        this.labelLine = labelLine;
    }

    public void setLabelBar(String labelBar) {
        this.labelBar = labelBar;
    }

    public void setCallBack(ResultCallBack callBack) {
        this.callBack = callBack;
    }

    /**
     * 通过接口，根据设备类型从服务器端获得图表数据字符串,并且用Gson解析成device对象
     *
     * @param deviceTypes
     * @return
     */
    private void getData(String[] deviceTypes) {
        if (StringUtils.isEmpty(toiletId) || StringUtils.isEmpty(usage)) {
            return;
        }
        count = new AtomicInteger(0);
        HashMap<String, String> params = new HashMap<>();
        params.put("toiletId", toiletId);
        params.put("usage", usage);
        for (String type : deviceTypes) {
            params.put("deviceType", type);
            OkHttpManager.CommonPostAsyn(WebConstant.GET_USE_TIME, params, new OkHttpManager.ResultCallback() {
                @Override
                public void onCallBack(OkHttpManager.State state, String result) {
                    System.out.println("图表统计 statsManager getDate: state=" + state + "  Result=" + result);
                    if (state == OkHttpManager.State.SUCCESS) {
                        Gson gson = new Gson();
                        RecordInfoResult recordInfoResult = gson.fromJson(result, RecordInfoResult.class);
                        resultList.add(recordInfoResult);
                    } else {
                        //如果请求失败，也要加一个null占位置，表示此处是空
                        resultList.add(null);
                    }
                    count.addAndGet(1);

                    //当全部的线程已经完成了，就调用消息机制提醒主线程，执行画图功能
                    if (count.get() == deviceTypes.length) {
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                sortResultListByUsage();
                                setChart();
                            }
                        });
                    }

                }
            });
        }
    }

    public void setChart() {
        List<RecordInfoResult> recordInfoResultList = resultList;

        System.out.println("setChart() recordInfoResultList.size():" + recordInfoResultList.size());
        if (recordInfoResultList.size() == chartTitles.length) {
            if (chartType == ChartItem.ChartType.COMBINED_CHART) {
                for (int i = 0; i < chartTitles.length; i++) {
                    CombinedData data = getCombinedData(recordInfoResultList, i, labelLine, labelBar);
                    chartItemList.add(new CombinedChartItem(chartTitles[i], data));
                }
            } else if (chartType == ChartItem.ChartType.BAR_CHART) {
                for (int i = 0; i < chartTitles.length; i++) {
                    BarData data = getBarData(recordInfoResultList, i, labelBar);
                    chartItemList.add(new BarChartItem(chartTitles[i], data));
                }
            } else if (chartType == ChartItem.ChartType.LINE_CHART) {
                for (int i = 0; i < chartTitles.length; i++) {
                    LineData data = getLineData(recordInfoResultList, i, labelLine);
                    chartItemList.add(new LineChartItem(chartTitles[i], data));
                }
            } else if (chartType == ChartItem.ChartType.LINES_CHART) {
                for (int i = 0; i < chartTitles.length; i++) {
                    LineData data = getLineData(recordInfoResultList, i, labelLine, labelBar);
                    chartItemList.add(new LineChartItem(chartTitles[i], data));
                }
            }
            if (callBack != null) {
                chartAdapter = new ChartAdapter(context, getChartItemList());
                callBack.onResult(chartAdapter);
            }

        }
    }

    /**
     * 对返回的结果进行排序
     */
    private void sortResultListByUsage() {
        Collections.sort(resultList, new Comparator<RecordInfoResult>() {
            @Override
            public int compare(RecordInfoResult o1, RecordInfoResult o2) {
                if (o1 == null || o2 == null) {
                    return 0;
                }
                if (o1.getDeviceType().charAt(0) < o2.getDeviceType().charAt(0)) {
                    return -1;
                } else {
                    return 1;
                }
//                return 0;
            }
        });
    }

    /**
     * 返回ChartItemList,用来设置Chart
     *
     * @return
     */
    public ArrayList<ChartItem> getChartItemList() {
        return chartItemList;
    }

    /**
     * 回调函数
     */
    public interface ResultCallBack {
        void onResult(ChartAdapter adapter);
    }

    private CombinedData getCombinedData(List<RecordInfoResult> recordInfoResultList, int index, String labelLine, String labelBar) {
        LineData lineData = getLineData(recordInfoResultList, index, labelLine);
        BarData barData = getBarData(recordInfoResultList, index, labelBar);
        CombinedData data = new CombinedData();
        if (lineData != null)
            data.setData(lineData);
        if (barData != null)
            data.setData(barData);
        return data;
    }

    private LineData getLineData(List<RecordInfoResult> recordInfoResultList, int index, String label) {
        ArrayList<Entry> entries = new ArrayList<>();
        if (recordInfoResultList.get(index) != null && recordInfoResultList.get(index).isResult()) {
            List<RecordInfoItem> dataList = recordInfoResultList.get(index).getDataList();
            for (int i = 0; i < dataList.size(); i++) {
                entries.add(new Entry(i, dataList.get(i).getTimes()));
            }

            LineDataSet dataSet = new LineDataSet(entries, label);
            dataSet.setLineWidth(2.5f);
            dataSet.setCircleRadius(4.5f);
            dataSet.setHighLightColor(Color.rgb(244, 117, 117));
            dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
            dataSet.setDrawValues(true);
            dataSet.setColor(Color.BLUE);
            dataSet.setCircleColor(Color.BLUE);
            //实心
            dataSet.setDrawCircleHole(false);
            ArrayList<ILineDataSet> sets = new ArrayList<>();
            sets.add(dataSet);

            return new LineData(sets);
        }
        return null;
    }

    private LineData getLineData(List<RecordInfoResult> recordInfoResultList, int index, String label1, String label2) {
        ArrayList<Entry> entries1 = new ArrayList<>();
        ArrayList<Entry> entries2 = new ArrayList<>();
        if (recordInfoResultList.get(index) != null && recordInfoResultList.get(index).isResult()) {
            List<RecordInfoItem> dataList = recordInfoResultList.get(index).getDataList();
            for (int i = 0; i < dataList.size(); i++) {
                entries1.add(new Entry(i, dataList.get(i).getTimes()));
                entries2.add(new Entry(i, dataList.get(i).getValue()));
            }

            LineDataSet dataSet = new LineDataSet(entries1, label1);
            dataSet.setLineWidth(2.5f);
            dataSet.setCircleRadius(4.5f);
            dataSet.setHighLightColor(Color.rgb(244, 117, 117));
            dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
            dataSet.setDrawValues(true);
            dataSet.setColor(Color.BLUE);
            dataSet.setCircleColor(Color.BLUE);
            //实心
            dataSet.setDrawCircleHole(false);

            LineDataSet dataSet2 = new LineDataSet(entries2, label2);
            dataSet2.setLineWidth(2.5f);
            dataSet2.setCircleRadius(4.5f);
            dataSet2.setHighLightColor(Color.rgb(244, 117, 117));
            dataSet2.setAxisDependency(YAxis.AxisDependency.LEFT);
            dataSet2.setDrawValues(true);
            dataSet2.setColor(Color.GRAY);
            dataSet2.setCircleColor(Color.GRAY);
            //实心
            dataSet2.setDrawCircleHole(false);

            ArrayList<ILineDataSet> sets = new ArrayList<>();
            sets.add(dataSet);
            sets.add(dataSet2);

            return new LineData(sets);
        }
        return null;
    }

    private BarData getBarData(List<RecordInfoResult> recordInfoResultList, int index, String label) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        if (recordInfoResultList.get(index) != null && recordInfoResultList.get(index).isResult()) {
            List<RecordInfoItem> dataList = recordInfoResultList.get(index).getDataList();
            for (int i = 0; i < dataList.size(); i++) {
//                System.out.println("value"+i+":"+dataList.get(i).getValue());
                entries.add(new BarEntry(i, dataList.get(i).getValue()));
            }

            BarDataSet dataSet = new BarDataSet(entries, label);
            dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
            dataSet.setHighLightAlpha(255);
            dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

            BarData cd = new BarData(dataSet);
            cd.setBarWidth(0.9f);
            return cd;
        }
        return null;
    }

    private List<String> getXaxis(int index) {
        List<RecordInfoResult> recordInfoResultList = resultList;
        List<String> list = new ArrayList<>();

        if (recordInfoResultList.get(index) == null || !recordInfoResultList.get(index).isResult()) {
            return list;
        }

        System.out.println("getXaxis() recordInfoResultList.size():" + recordInfoResultList.size());
        //处理下标越界
        if (index < recordInfoResultList.size()) {
            for (RecordInfoItem data : recordInfoResultList.get(index).getDataList()) {
                list.add(data.getDate());
            }
        }
        return list;
    }


    public class ChartAdapter extends ArrayAdapter<ChartItem> {

        public ChartAdapter(@NonNull Context context, @NonNull List<ChartItem> objects) {
            super(context, 0, objects);
        }

        @Override
        public int getItemViewType(int position) {
            // return the views type
            return position;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (chartType == ChartItem.ChartType.COMBINED_CHART) {
                return ((CombinedChartItem) getItem(i)).getView(i, view, getContext(), getXaxis(i));
            } else if (chartType == ChartItem.ChartType.BAR_CHART) {
                return ((BarChartItem) getItem(i)).getView(i, view, getContext(), getXaxis(i));
            } else if (chartType == ChartItem.ChartType.LINE_CHART) {
                LineChartItem item = (LineChartItem) getItem(i);
                return item.getView(i, view, getContext(), getXaxis(i));
            } else if (chartType == ChartItem.ChartType.LINES_CHART) {
                LineChartItem item = (LineChartItem) getItem(i);
                return item.getView(i, view, getContext(), getXaxis(i));
            }
            return null;
        }

    }


}
