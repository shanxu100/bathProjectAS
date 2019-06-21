package luluteam.bath.bathprojectas.fragment.setting;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import luluteam.bath.bathprojectas.R;

/**
 * Created by 123 on 2017/11/16.
 */
@Deprecated
public class ToiletManageFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bath_manage, container, false);
        return view;
    }
}
