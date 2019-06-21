package luluteam.bath.bathprojectas.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import luluteam.bath.bathprojectas.R;
import luluteam.bath.bathprojectas.fragment.BaseFragment;
import luluteam.bath.bathprojectas.fragment.cleanliness.CleanlinessFragment;
import luluteam.bath.bathprojectas.view.TabItemViewCtrlFragment;

@Deprecated
public class CleanlinessActivity extends BaseActivity {

    private Toolbar include_toolbar;

    private TabLayout cleanliness_tablayout;
    private ViewPager cleanliness_viewpager;
    private List<BaseFragment> fragmentList = new ArrayList<>();
    private List<TabItemViewCtrlFragment> tabItemViewList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cleanliness);
        initUI();
    }

    private void initUI() {
        cleanliness_tablayout = (TabLayout) findViewById(R.id.cleanliness_tab);
        cleanliness_viewpager = (ViewPager) findViewById(R.id.container);
        include_toolbar = (Toolbar) findViewById(R.id.include_toolbar);
        include_toolbar.setTitle("厕所清洁度详情");
        this.setSupportActionBar(include_toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        fragmentList.add(new CleanlinessFragment());
        fragmentList.add(new CleanlinessFragment());
        fragmentList.add(new CleanlinessFragment());
        tabItemViewList.add(new TabItemViewCtrlFragment(this, R.drawable.tab_item_man_selector, 0));
        tabItemViewList.add(new TabItemViewCtrlFragment(this, R.drawable.tab_item_woman_selector, 0));
        tabItemViewList.add(new TabItemViewCtrlFragment(this, R.drawable.tab_item_third_selector, 0));
        cleanliness_viewpager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }
        });
        cleanliness_tablayout.setupWithViewPager(cleanliness_viewpager);
        cleanliness_tablayout.getTabAt(1).select();
        /**
         * MODE_FIXED:固定tabs，并同时显示所有的tabs。
         * MODE_SCROLLABLE：可滚动tabs，显示一部分tabs，在这个模式下能包含长标签和大量的tabs，最好用于用户不需要直接比较tabs。
         */
        cleanliness_tablayout.setTabMode(TabLayout.MODE_FIXED);
        //手动指定TabLayout中Item的View
        for (int i = 0; i < cleanliness_tablayout.getTabCount(); i++) {
            TabLayout.Tab tab = cleanliness_tablayout.getTabAt(i);
            tab.setCustomView(tabItemViewList.get(i));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
