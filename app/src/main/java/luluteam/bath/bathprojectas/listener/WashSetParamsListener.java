package luluteam.bath.bathprojectas.listener;

import java.util.List;

import luluteam.bath.bathprojectas.model.wash.WashParams;

public interface WashSetParamsListener {
    void onPitWashSetReceived(List<WashParams.Group> groups, int frameIndex);

    void onRoadWashSetReceived(List<WashParams.Group> groups, int frameIndex);
}
