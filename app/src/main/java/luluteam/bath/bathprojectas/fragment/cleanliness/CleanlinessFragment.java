package luluteam.bath.bathprojectas.fragment.cleanliness;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import luluteam.bath.bathprojectas.R;
import luluteam.bath.bathprojectas.fragment.BaseFragment;


public class CleanlinessFragment extends BaseFragment {

    private RecyclerView clearliness_RV;


    public CleanlinessFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cleanliness, container, false);
        clearliness_RV = (RecyclerView) view.findViewById(R.id.cleanliness_RV);
        return view;
    }


}
