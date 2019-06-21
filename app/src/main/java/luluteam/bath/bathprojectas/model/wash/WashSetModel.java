package luluteam.bath.bathprojectas.model.wash;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

@Deprecated
public class WashSetModel extends BaseObservable {
    private int runTime;
    private int delayTime;
    private int id;

    public WashSetModel() {

    }

    public WashSetModel(int runTime, int delayTime) {
        this.runTime = runTime;
        this.delayTime = delayTime;
    }

    public WashSetModel(int runTime, int delayTime, int id) {
        this.runTime = runTime;
        this.delayTime = delayTime;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Bindable
    public int getRunTime() {
        return runTime;
    }

    public void setRunTime(int runTime) {
        this.runTime = runTime;
    }

    @Bindable
    public int getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(int delayTime) {
        this.delayTime = delayTime;
    }
}
