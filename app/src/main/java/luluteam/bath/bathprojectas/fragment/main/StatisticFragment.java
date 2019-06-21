package luluteam.bath.bathprojectas.fragment.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import luluteam.bath.bathprojectas.R;
import luluteam.bath.bathprojectas.fragment.BaseFragment;
import luluteam.bath.bathprojectas.fragment.statistics.AmountStatsFragment;
import luluteam.bath.bathprojectas.fragment.statistics.CountStatisticFragment;
import luluteam.bath.bathprojectas.fragment.statistics.HumanFlowStatsFragment;
import luluteam.bath.bathprojectas.fragment.statistics.TimeStatisticFragment;

/**
 * Created by Administrator on 2018/3/18 0018.
 */

public class StatisticFragment extends BaseFragment {
    private TabLayout statistics_tablayout;
    private ViewPager statistics_viewpager;
    private FragmentManager fragmentManager;
    private List<Fragment> fragmentList = new ArrayList<>();
    private List<String> titleList = new ArrayList<>();

    private TimeStatisticFragment timeStatisticFragment;
    private CountStatisticFragment countStatisticFragment;
    private AmountStatsFragment amountStatsFragment;
    private HumanFlowStatsFragment humanFlowStatsFragment;

    private FloatingActionButton mFab;

    private boolean isFromCtrlFragment = false;

    public StatisticFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistic, container, false);
        initData();
        initUI(view);
        statistics_tablayout.getTabAt(isFromCtrlFragment ? statistics_tablayout.getTabCount() - 1 : 0).select();
        return view;
    }

    private void initData() {
        timeStatisticFragment = new TimeStatisticFragment();
        countStatisticFragment = new CountStatisticFragment();
        amountStatsFragment = new AmountStatsFragment();
        humanFlowStatsFragment = new HumanFlowStatsFragment();

        fragmentManager = getChildFragmentManager();
        fragmentList.add(timeStatisticFragment);
        fragmentList.add(countStatisticFragment);
        fragmentList.add(amountStatsFragment);
        fragmentList.add(humanFlowStatsFragment);

        titleList.add("时间统计");
        titleList.add("次数统计");
        titleList.add("用量统计");
        titleList.add("人流量统计");
    }

    private void initUI(View view) {
        statistics_tablayout = (TabLayout) view.findViewById(R.id.statistics_tab);
        statistics_viewpager = (ViewPager) view.findViewById(R.id.statistics_viewpager);
        //加上可见的fragment，另外保留另外两个不可见fragment不销毁
        statistics_viewpager.setOffscreenPageLimit(3);
        mFab = (FloatingActionButton) view.findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toTop();
            }
        });

        statistics_viewpager.setAdapter(new FragmentPagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return titleList.get(position);
            }
        });

        statistics_tablayout.setupWithViewPager(statistics_viewpager);
    }

    public void setSelect() {
        if (statistics_tablayout != null) {
            statistics_tablayout.getTabAt(statistics_tablayout.getTabCount() - 1).select();
        } else {
            isFromCtrlFragment = true;
        }
    }

    private void toTop() {
        switch (statistics_tablayout.getSelectedTabPosition()) {
            case 0:
                timeStatisticFragment.toTop();
                break;
            case 1:
                //预留
                countStatisticFragment.toTop();
                break;
            default:
        }
    }

}
