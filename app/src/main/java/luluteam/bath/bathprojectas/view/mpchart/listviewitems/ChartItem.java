package luluteam.bath.bathprojectas.view.mpchart.listviewitems;

import android.content.Context;
import android.view.View;

import com.github.mikephil.charting.data.ChartData;

/**
 * Created by luluteam on 2017/10/27.
 */

public abstract class ChartItem {

    //LINES_CHART 是指一个表中有多条
    public enum ChartType {
        BAR_CHART, COMBINED_CHART, LINE_CHART, LINES_CHART
    }


    protected String chartTitle;
    protected ChartData<?> mChartData;

    public ChartItem(String chartTitle, ChartData<?> mChartData) {
        this.chartTitle = chartTitle;
        this.mChartData = mChartData;
    }

    public String getChartTitle() {
        return chartTitle;
    }

    public void setChartTitle(String chartTitle) {
        this.chartTitle = chartTitle;
    }

    public abstract ChartType getItemType();

    public abstract View getView(int position, View convertView, Context c);


}
