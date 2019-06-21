package luluteam.bath.bathprojectas.view.mpchart.listviewitems;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.formatter.AxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

import luluteam.bath.bathprojectas.R;
import luluteam.bath.bathprojectas.fragment.statistics.StatisticHelper;
import luluteam.bath.bathprojectas.model.StatisticAnalysis.RecordInfoItem;
import luluteam.bath.bathprojectas.model.StatisticAnalysis.RecordInfoResult;
import luluteam.bath.bathprojectas.utils.TimeUtil;

/**
 * Created by Administrator on 2018/3/20 0020.
 */

public class MyLineChartView extends LinearLayout {
    public static final String TYPE_DAY = "day";
    public static final String TYPE_WEEK = "week";
    public static final String TYPE_MONTH = "month";

    private LineChart mChart;
    private TextView mTitle;
    private TextView mRefreshTime;
    private TabLayout mTabLayout;

    private String deviceType;

    private String dateType = TYPE_DAY;

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDateType() {
        return dateType;
    }

    public void setDateType(String dateType) {
        this.dateType = dateType;
    }

    public MyLineChartView(Context context) {
        super(context);
        initView(context);
    }

    public MyLineChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public MyLineChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public void setChartXAxisValue(RecordInfoResult recordInfoResult) {
        List<String> xValues = new ArrayList<>();
        if (recordInfoResult != null) {
            StringBuilder builder = new StringBuilder();
            for (RecordInfoItem item : recordInfoResult.getDataList()) {
                xValues.add(item.getDate());
                builder.append(item.getDate() + ",");
            }
            mChart.getXAxis().setValueFormatter(new AxisValueFormatter() {
                @Override
                public String getFormattedValue(float v, AxisBase axisBase) {
                    if (v < 0 || v > (xValues.size() - 1))//使得两侧柱子完全显示
                        return "";
                    return xValues.get((int) v);
                }

                @Override
                public int getDecimalDigits() {
                    return 0;
                }
            });
        }
    }

    public LineChart getChart() {
        return mChart;
    }

    public void setChartData(LineData data) {
        mChart.setData(data);
        mChart.notifyDataSetChanged();
        mChart.postInvalidate();
    }

    public void setTitle(String title) {
        mTitle.setText(title);
    }

    public void setRefreshTime() {
        mRefreshTime.post(new Runnable() {
            @Override
            public void run() {
                mRefreshTime.setText(TimeUtil.getCurrentTime("yyyy/MM/dd HH:mm:ss"));
            }
        });

    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.line_chart_item, this, true);
        mChart = (LineChart) findViewById(R.id.line_chart);
        mTitle = (TextView) findViewById(R.id.line_chart_title);
        mRefreshTime = (TextView) findViewById(R.id.line_chart_refreshtime);
        mTabLayout = (TabLayout) findViewById(R.id.line_chart_choices);
        setCustomChart();
        setSomeListener();
    }

    private void setCustomChart() {
        mChart.setDescription("");
        mChart.setNoDataText("暂时没有数据！");
        mChart.setDrawGridBackground(false);         // 是否显示表格颜色
        mChart.setPinchZoom(true);//设置按比例放缩柱状图
        mChart.setScaleEnabled(false);//禁止缩放
        mChart.setDoubleTapToZoomEnabled(false);//设置为false以禁止通过在其上双击缩放图表
        mChart.setDragEnabled(true);

        //设置X轴
        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
//        xAxis.setLabelRotationAngle(-40);
        xAxis.setGranularity(1f);//设置最小间隔，防止当放大时，出现重复标签。
        xAxis.setYOffset(20);
        xAxis.setLabelCount(5, false);

        //设置Y轴
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setLabelCount(5, false);
        leftAxis.setAxisMinValue(0f);
        leftAxis.setDrawAxisLine(false);//将坐标轴隐藏
        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);
    }

    private void setSomeListener() {
        StatisticHelper helper = new StatisticHelper();
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (mTabLayout.getSelectedTabPosition()) {
                    case 0:
                        dateType = TYPE_DAY;
                        break;
                    case 1:
                        dateType = TYPE_WEEK;
                        break;
                    case 2:
                        dateType = TYPE_MONTH;
                        break;
                }
                helper.setLineChartView(deviceType, dateType, new StatisticHelper.ResultCallback() {
                    @Override
                    public void onResult(RecordInfoResult recordInfoResult) {
                        if ("F".equals(deviceType) || "G".equals(deviceType)) {
                            setChartData(helper.getLineData(recordInfoResult, new String[]{"使用次数"}, StatisticHelper.ONLY_TIMES));

                        } else if ("Q".equals(deviceType) || "R".equals(deviceType)) {
                            setChartData(helper.getLineData(recordInfoResult, new String[]{"使用量"}, StatisticHelper.ONLY_VALUES));
                        } else if ("K".equals(deviceType)) {
                            setChartData(helper.getLineData(recordInfoResult, new String[]{"人流量"}, StatisticHelper.ONLY_VALUES));
                        } else {
                            setChartData(helper.getLineData(recordInfoResult, new String[]{"使用时间", "使用次数"}, StatisticHelper.BOTH_TIMES_VLAUES));
                        }
                        setChartXAxisValue(recordInfoResult);
                        setRefreshTime();

                    }
                });
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        System.out.println("MyLineChartView onDraw()");
    }
}
