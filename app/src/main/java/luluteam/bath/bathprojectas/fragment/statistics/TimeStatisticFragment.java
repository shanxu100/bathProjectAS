package luluteam.bath.bathprojectas.fragment.statistics;

import android.view.View;

import java.util.ArrayList;

import luluteam.bath.bathprojectas.R;
import luluteam.bath.bathprojectas.view.mpchart.listviewitems.MyLineChartView;

/**
 * Created by Administrator on 2018/3/20 0020.
 * 统计分析中的时间统计fragment
 */

public class TimeStatisticFragment extends BaseStasFragment {

    @Override
    protected void initData() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_usage2;
    }

    @Override
    protected void initSpecial(View view) {
        chartTitles = new String[]{
                "主灯", "辅灯", "抽空风机", "音响", "消毒灯"
        };
        deviceTypes = new String[]{"A", "B", "C", "D", "E"};
        labels = new String[]{"使用时间", "使用次数"};
        MyLineChartView view1 = (MyLineChartView) view.findViewById(R.id.my_line_chart_view_item1);
        MyLineChartView view2 = (MyLineChartView) view.findViewById(R.id.my_line_chart_view_item2);
        MyLineChartView view3 = (MyLineChartView) view.findViewById(R.id.my_line_chart_view_item3);
        MyLineChartView view4 = (MyLineChartView) view.findViewById(R.id.my_line_chart_view_item4);
        MyLineChartView view5 = (MyLineChartView) view.findViewById(R.id.my_line_chart_view_item5);
        chartViews = new ArrayList<>();
        chartViews.add(view1);
        chartViews.add(view2);
        chartViews.add(view3);
        chartViews.add(view4);
        chartViews.add(view5);
        type = StatisticHelper.BOTH_TIMES_VLAUES;
    }
}
