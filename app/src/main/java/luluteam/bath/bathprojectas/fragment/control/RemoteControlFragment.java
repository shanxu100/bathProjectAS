package luluteam.bath.bathprojectas.fragment.control;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import luluteam.bath.bathprojectas.R;
import luluteam.bath.bathprojectas.adapter.ControlAdapter;
import luluteam.bath.bathprojectas.fragment.BaseFragment;
import luluteam.bath.bathprojectas.fragment.main.ControlFragment;
import luluteam.bath.bathprojectas.model.RemoteControl.Devices;


/**
 * 显示 男女残卫 单个设备的控制状态
 */
public class RemoteControlFragment extends BaseFragment {

    private static final String TAG = "RemoteControlFragment";

    private ListView devices_lv;
    private ControlAdapter adapter;
    private String usage = "";//厕所类型

    private OnIdelListener listener;

    public void setListener(OnIdelListener listener) {
        this.listener = listener;
    }

    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public ControlAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(ControlAdapter adapter) {
        this.adapter = adapter;
    }

    public RemoteControlFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_remote_control, container, false);
        devices_lv = (ListView) view.findViewById(R.id.devices_lv);

        devices_lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            int firstPosition = -1;
            int lastPosition = -1;
            boolean scrollUp = false;
            boolean scrollDown = false;
            int scrollTop = 0;
            int scrollBottom = 0;

            private boolean isSameRow(int item, int lastItem) {
                return item == lastItem;
            }

            private int getScrollTop(ListView listView) {
                if (listView != null) {
                    View firstChild = listView.getChildAt(0);
                    return firstChild == null ? -1 : firstChild.getTop();

                }
                return -1;
            }

            private int getScrollBottom(ListView listView) {
                if (listView != null) {
                    View lastChild = listView.getChildAt(listView.getChildCount() - 1);
                    return lastChild == null ? -1 : lastChild.getBottom();
                }
                return -1;
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //判断状态
                switch (scrollState) {
                    // 当不滚动时 // 是当屏幕停止滚动时
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        scrollDown = isSameRow(devices_lv.getFirstVisiblePosition(), firstPosition) && (scrollTop - getScrollTop(devices_lv) <= 0);
                        scrollUp = isSameRow(devices_lv.getLastVisiblePosition(), lastPosition);
                        Log.e(TAG, "currentBottom:" + getScrollBottom(devices_lv));
                        // 判断滚动到顶部
                        if (devices_lv.getFirstVisiblePosition() == 0 && scrollTop == 0 && scrollDown) {
                            //TODO 滑到顶部
                            if (listener != null) {
                                Log.e(TAG, "top");
                                listener.onTop();
                                return;
                            }
                        }
                        // 判断滑到底部
                        if (devices_lv.getLastVisiblePosition() == devices_lv.getCount() - 1 && scrollUp && scrollBottom == getScrollBottom(devices_lv)) {
                            //TODO 滑到底部
                            if (listener != null) {
                                Log.e(TAG, "bottom");
                                listener.onBottom();
                            }
                        }
                        firstPosition = devices_lv.getFirstVisiblePosition();
                        lastPosition = devices_lv.getLastVisiblePosition();
                        break;
                    // 滚动时
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                        scrollTop = getScrollTop(devices_lv);
                        scrollBottom = getScrollBottom(devices_lv);
                        Log.e(TAG, "scrollBottom:" + scrollBottom);
                        Log.e(TAG, "scrollTop:" + scrollTop);
                        Log.e(TAG, "lvBottom:" + devices_lv.getBottom());
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                        // 当用户由于之前划动屏幕并抬起手指，屏幕产生惯性滑动时，即滚动时
                        break;
                    default:
                        break;

                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        if (adapter != null) {
            devices_lv.setAdapter(adapter);
            System.err.println("usage=" + usage + " adapter is not null");
        } else {
            System.err.println("usage=" + usage + " adapter is null");
        }
        return view;
    }


    @Override
    public void onStateChanged(Object object) {
        //&& getActivity() instanceof ControlActivity
        if (adapter != null && object instanceof Devices) {
            try {
                //由于未加载，会出现空指针异常
                adapter.notifyDataChange((Devices) object, ((ControlFragment) getParentFragment()).isControlled());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public interface OnIdelListener {
        void onTop();

        void onBottom();
    }
}
