package luluteam.bath.bathprojectas.view.mpchart.listviewitems;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.formatter.AxisValueFormatter;

import java.util.List;

import luluteam.bath.bathprojectas.R;
import luluteam.bath.bathprojectas.utils.CalendarUtil;

/**
 * Created by Administrator on 2018/3/18 0018.
 */

public class LineChartItem extends ChartItem {

    public LineChartItem(String chartTitle, ChartData<?> mChartData) {
        super(chartTitle, mChartData);
    }

    @Override
    public ChartType getItemType() {
        return ChartType.LINE_CHART;
    }

    @Override
    public View getView(int position, View convertView, Context c) {
        return null;
    }

    public View getView(int position, View convertView, Context c, final List<String> xValues) {

        LineChartItem.ViewHolder holder = null;
        if (convertView == null) {
            holder = new LineChartItem.ViewHolder();
            convertView = LayoutInflater.from(c).inflate(R.layout.line_chart_item, null);
            holder.chart = (LineChart) convertView.findViewById(R.id.line_chart);
            holder.title = (TextView) convertView.findViewById(R.id.line_chart_title);
            holder.refreshTime = (TextView) convertView.findViewById(R.id.line_chart_refreshtime);
            holder.tabLayout = (TabLayout) convertView.findViewById(R.id.line_chart_choices);
            convertView.setTag(holder);
        } else {
            holder = (LineChartItem.ViewHolder) convertView.getTag();
        }

        if (holder != null) {
            holder.title.setText(chartTitle);
            holder.refreshTime.setText(CalendarUtil.getDateTimeString("/", " "));

            holder.chart.setDescription("");
            holder.chart.setNoDataText("暂时没有数据！");
//            holder.chart.setDrawValueAboveBar(true);
//            holder.chart.setMaxVisibleValueCount(5);
            holder.chart.setDrawGridBackground(false);         // 是否显示表格颜色
            holder.chart.setPinchZoom(true);//设置按比例放缩柱状图
            holder.chart.setScaleEnabled(false);//禁止缩放
            holder.chart.setDoubleTapToZoomEnabled(false);//设置为false以禁止通过在其上双击缩放图表
            holder.chart.setDragEnabled(true);

            //设置X轴
            XAxis xAxis = holder.chart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);
            xAxis.setLabelRotationAngle(-40);
            xAxis.setGranularity(1f);//设置最小间隔，防止当放大时，出现重复标签。
            xAxis.setYOffset(20);
            xAxis.setLabelCount(5, false);
            xAxis.setValueFormatter(new AxisValueFormatter() {
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

            //设置Y轴
            YAxis leftAxis = holder.chart.getAxisLeft();
            leftAxis.setLabelCount(5, false);
            leftAxis.setAxisMinValue(0f);
            leftAxis.setDrawAxisLine(false);//将坐标轴隐藏

            YAxis rightAxis = holder.chart.getAxisRight();
            rightAxis.setEnabled(false);

            Legend legend = holder.chart.getLegend();
            legend.setForm(Legend.LegendForm.LINE);


            // set data
            holder.chart.setData((LineData) mChartData);

            // do not forget to refresh the chart
            // holder.chart.invalidate();
            holder.chart.animateX(750);
//            holder.chart.setTouchEnabled(false);
        }
        return convertView;
    }

    private static class ViewHolder {
        LineChart chart;
        TextView title;
        TextView refreshTime;
        TabLayout tabLayout;
    }
}
