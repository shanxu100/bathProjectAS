package luluteam.bath.bathprojectas.fragment.statistics;

import android.view.View;

import java.util.ArrayList;

import luluteam.bath.bathprojectas.R;
import luluteam.bath.bathprojectas.view.mpchart.listviewitems.MyLineChartView;

public class HumanFlowStatsFragment extends BaseStasFragment {


    @Override
    protected void initData() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_human_flow_stats;
    }

    @Override
    protected void initSpecial(View view) {
        chartTitles = new String[]{"人流量统计"};
        deviceTypes = new String[]{"K"};
        labels = new String[]{"人流量"};
        MyLineChartView view1 = (MyLineChartView) view.findViewById(R.id.my_line_chart_view_item1);
        chartViews = new ArrayList<>();
        chartViews.add(view1);
        type = StatisticHelper.ONLY_VALUES;
    }
}
