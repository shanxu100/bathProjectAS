package luluteam.bath.bathprojectas.fragment.statistics;

import android.view.View;

import java.util.ArrayList;

import luluteam.bath.bathprojectas.R;
import luluteam.bath.bathprojectas.view.mpchart.listviewitems.MyLineChartView;

/**
 * Created by Administrator on 2018/3/20 0020.
 * 统计分析中的次数统计fragment
 */

public class CountStatisticFragment extends BaseStasFragment {

    @Override
    protected void initData() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_usage2;
    }

    @Override
    protected void initSpecial(View view) {
        chartTitles = new String[]{"一键冲洗", "风幕机"};
        deviceTypes = new String[]{"H", "I"};
        labels = new String[]{"使用次数"};
        MyLineChartView view1 = (MyLineChartView) view.findViewById(R.id.my_line_chart_view_item1);
        MyLineChartView view2 = (MyLineChartView) view.findViewById(R.id.my_line_chart_view_item2);
        chartViews = new ArrayList<>();
        chartViews.add(view1);
        chartViews.add(view2);
        type = StatisticHelper.ONLY_TIMES;
    }
}
