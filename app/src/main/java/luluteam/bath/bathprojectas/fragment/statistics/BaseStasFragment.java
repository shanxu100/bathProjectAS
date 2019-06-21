package luluteam.bath.bathprojectas.fragment.statistics;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import java.util.List;

import luluteam.bath.bathprojectas.R;
import luluteam.bath.bathprojectas.activity.Main2Activity;
import luluteam.bath.bathprojectas.fragment.BaseFragment;
import luluteam.bath.bathprojectas.model.StatisticAnalysis.RecordInfoResult;
import luluteam.bath.bathprojectas.utils.ToastUtil;
import luluteam.bath.bathprojectas.view.dialog.LoadingDialog;
import luluteam.bath.bathprojectas.view.mpchart.listviewitems.MyLineChartView;

public abstract class BaseStasFragment extends BaseFragment implements IStatisticFragment {

    private ScrollView mScrollView;
    private SwipeRefreshLayout mSwipeRefresh;
    private StatisticHelper statisticHelper;


    protected String[] deviceTypes;
    protected String[] chartTitles;
    protected List<MyLineChartView> chartViews;
    protected String[] labels;
    protected int type;

    public BaseStasFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        initSpecial(view);
        initView(view);
        if (getActivity() instanceof Main2Activity) {
            ((Main2Activity) getActivity()).setChangeToiletTypeCallback(new IChangeToiletTypeCallback() {
                @Override
                public void onChanged() {
                    setAllChartView();
                }
            });
        }
        initData();
        return view;
    }

    protected void initView(View view) {
        statisticHelper = new StatisticHelper();
        mScrollView = (ScrollView) view.findViewById(R.id.scrollview);
        mSwipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);

        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setAllChartView();
                //5秒后自动关闭刷新
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mSwipeRefresh.isRefreshing()) {
                            mSwipeRefresh.setRefreshing(false);
                            ToastUtil.showShortToast(getActivity(), "数据加载超时，请重新下滑刷新！");
                        }
                    }
                }, 5000);
            }
        });
        setAllChartView();
    }

    private void setAllChartView() {
        LoadingDialog loadingDialog = new LoadingDialog(getActivity());
        loadingDialog.setLoadingText("获取数据");
        loadingDialog.show();
        for (int i = 0; i < chartViews.size(); i++) {
            setChartView(chartViews.get(i), chartViews.get(i).getDateType(), i);
        }
        loadingDialog.dismiss();
        mSwipeRefresh.setRefreshing(false);
    }

    private void setChartView(MyLineChartView view, String dateType, int index) {
        view.setRefreshTime();
        view.setTitle(chartTitles[index]);
        view.setVisibility(View.VISIBLE);
        statisticHelper.setLineChartView(deviceTypes[index], dateType, new StatisticHelper.ResultCallback() {
            @Override
            public void onResult(RecordInfoResult recordInfoResult) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        view.setDeviceType(deviceTypes[index]);
                        view.setChartData(statisticHelper.getLineData(recordInfoResult, labels, type));
                        view.setChartXAxisValue(recordInfoResult);
                    }
                });
            }
        });
    }

    protected abstract void initData();

    @LayoutRes
    protected abstract int getLayoutId();

    protected abstract void initSpecial(View view);


    @Override
    public void toTop() {
        mScrollView.fullScroll(ScrollView.FOCUS_UP);
    }
}
