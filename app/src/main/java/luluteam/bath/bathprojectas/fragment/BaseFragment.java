package luluteam.bath.bathprojectas.fragment;


import android.support.v4.app.Fragment;

import luluteam.bath.bathprojectas.activity.BaseActivity;
import luluteam.bath.bathprojectas.model.ToiletInfo;

/**
 * Created by Guan on 2018/1/5.
 */

public class BaseFragment extends Fragment implements BaseActivity.FragmentCallback {
    @Override
    public void onStateChanged(Object object) {
        System.err.println("BaseFragment onStateChanged=" + object.toString());
    }

    @Override
    public void onReloadImage() {
        System.err.println("BaseFragment onReloadImage()");
    }

    @Override
    public void onNewToilet() {
        System.err.println("BaseFragment onNewToilet()");

    }

    public interface ControlCallback {
        boolean isControlled();
    }

    public interface InfoCallback {
        void onToiletItem(ToiletInfo.ToiletItem item);
    }
}
