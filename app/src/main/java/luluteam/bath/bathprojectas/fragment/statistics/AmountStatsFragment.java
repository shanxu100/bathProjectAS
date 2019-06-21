package luluteam.bath.bathprojectas.fragment.statistics;

import android.view.View;

import java.util.ArrayList;

import luluteam.bath.bathprojectas.R;
import luluteam.bath.bathprojectas.view.mpchart.listviewitems.MyLineChartView;


/**
 * 用量统计：水电表
 */
public class AmountStatsFragment extends BaseStasFragment {

    @Override
    protected void initData() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_amount_stats;
    }

    @Override
    protected void initSpecial(View view) {
        chartTitles = new String[]{"水表", "电表"};
        deviceTypes = new String[]{"Q", "R"};
        labels = new String[]{"使用量"};
        MyLineChartView view1 = (MyLineChartView) view.findViewById(R.id.my_line_chart_view_item1);
        MyLineChartView view2 = (MyLineChartView) view.findViewById(R.id.my_line_chart_view_item2);
        chartViews = new ArrayList<>();
        chartViews.add(view1);
        chartViews.add(view2);
        type = StatisticHelper.ONLY_VALUES;
    }


}
