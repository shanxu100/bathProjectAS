package luluteam.bath.bathprojectas.view.mpchart.listviewitems;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.formatter.AxisValueFormatter;

import java.util.List;

import luluteam.bath.bathprojectas.R;

/**
 * Created by luluteam on 2017/10/28.
 */

public class BarChartItem extends ChartItem {

    public BarChartItem(String chartTitle, ChartData<?> mChartData) {
        super(chartTitle, mChartData);
    }

    @Override
    public ChartType getItemType() {
        return ChartType.BAR_CHART;
    }

    @Override
    public View getView(int position, View convertView, Context c) {
        return null;
    }


    public View getView(int position, View convertView, Context c, final List<String> xValues) {

        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(c).inflate(R.layout.bar_chart_item, null);
            holder.chart = (BarChart) convertView.findViewById(R.id.bar_chart);
            holder.title = (TextView) convertView.findViewById(R.id.bar_chart_title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (holder != null) {

            holder.title.setText(chartTitle);

            holder.chart.setDescription("");
            holder.chart.setNoDataText("暂时没有数据！");
//            holder.chart.setDrawValueAboveBar(true);
//            holder.chart.setMaxVisibleValueCount(5);
            holder.chart.setDrawGridBackground(false);         // 是否显示表格颜色
            holder.chart.setDrawBarShadow(false);
            holder.chart.setPinchZoom(true);//设置按比例放缩柱状图
            holder.chart.setScaleEnabled(false);//禁止缩放
            holder.chart.setDoubleTapToZoomEnabled(false);//设置为false以禁止通过在其上双击缩放图表
            holder.chart.setDragEnabled(true);
            holder.chart.setFitBars(true);

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

            YAxis rightAxis = holder.chart.getAxisRight();
            rightAxis.setLabelCount(5, false);
            rightAxis.setAxisMinValue(0f);
//            rightAxis.setDrawGridLines(false);

            // set data
            holder.chart.setData((BarData) mChartData);

            // do not forget to refresh the chart
            // holder.chart.invalidate();
            holder.chart.animateX(750);
//            holder.chart.setTouchEnabled(false);
        }
        return convertView;
    }

    private static class ViewHolder {
        BarChart chart;
        TextView title;
    }
}
