package luluteam.bath.bathprojectas.view.dialog.datePickDialog;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Calendar;

import luluteam.bath.bathprojectas.R;

/**
 * Created by luluteam on 2017/12/1.
 */

public class DatePick extends LinearLayout {

    private WheelView mYearWV;
    private WheelView mMonthWV;
    private WheelView mDayWV;
    private WheelView mHourWV;
    private WheelView mMinuteWV;
    private WheelView mSecondWV;

    private LinearLayout layout_date;
    private LinearLayout layout_time;

    private int mYear;
    private int mMonth = 2;

    private String resultDate;

    private Context mContext;
    //获取系统时间信息
    private Calendar mCalendar = Calendar.getInstance();

    private int type = TYPE_ALL;
    //仅显示年月日
    public final static int TYPE_ONLYDATE = 1;
    //仅显示时分秒
    public final static int TYPE_ONLYTIME = 2;
    //全部显示
    public final static int TYPE_ALL = 3;

    public DatePick(Context context) {
        this(context, null);
    }

    public DatePick(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public DatePick(Context context, int type) {
        this(context);
        this.type = type;
    }


    private void init(Context context, AttributeSet attrs) {
        TypedArray attribute = context.obtainStyledAttributes(attrs, R.styleable.DatePick);
        type = attribute.getInt(R.styleable.DatePick_type, TYPE_ALL);
        attribute.recycle();
    }

    /**
     * 获得类型
     *
     * @return
     */
    public int getType() {
        return type;
    }
//
//    /**
//     * 设置类型
//     * @param type
//     */
//    public void setType(int type) {
//        this.type = type;
//        onFinishInflate();
//    }

    /**
     * 设置年月日时分秒WheelView的item显示数量
     *
     * @param number
     */
    public void setWheelViewNumber(int number) {
        mYearWV.setItemNumber(number);
        mMonthWV.setItemNumber(number);
        mDayWV.setItemNumber(number);
        mHourWV.setItemNumber(number);
        mMinuteWV.setItemNumber(number);
        mSecondWV.setItemNumber(number);
    }

    public String getResultDate() {
        String dateStr = mYearWV.getSelectedText() + "-" + mMonthWV.getSelectedText() + "-" + mDayWV.getSelectedText();
        String timeStr = mHourWV.getSelectedText() + ":" + mMinuteWV.getSelectedText() + ":" + mSecondWV.getSelectedText();

        switch (type) {
            case TYPE_ALL:
                resultDate = dateStr + "T" + timeStr;
                break;
            case TYPE_ONLYDATE:
                resultDate = dateStr;
                break;
            case TYPE_ONLYTIME:
                resultDate = timeStr;
                break;
            default:
                resultDate = "";
                break;

        }
        return resultDate;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mContext = getContext();

        //初始化布局界面
        LayoutInflater.from(mContext).inflate(R.layout.layout_datepick, this);
        mYearWV = (WheelView) findViewById(R.id.wv_year);
        mMonthWV = (WheelView) findViewById(R.id.wv_month);
        mDayWV = (WheelView) findViewById(R.id.wv_day);
        mHourWV = (WheelView) findViewById(R.id.wv_hour);
        mMinuteWV = (WheelView) findViewById(R.id.wv_minute);
        mSecondWV = (WheelView) findViewById(R.id.wv_second);
        layout_date = (LinearLayout) findViewById(R.id.layout_date);
        layout_time = (LinearLayout) findViewById(R.id.layout_time);

        switch (type) {
            case TYPE_ALL:
                layout_date.setVisibility(VISIBLE);
                layout_time.setVisibility(VISIBLE);
                break;
            case TYPE_ONLYDATE:
                layout_date.setVisibility(VISIBLE);
                layout_time.setVisibility(GONE);
                break;
            case TYPE_ONLYTIME:
                layout_date.setVisibility(GONE);
                layout_time.setVisibility(VISIBLE);
        }

        mYearWV.setOnSelectListener(new WheelView.OnSelectListener() {
            @Override
            public void endSelect(int id, String text) {
                mYear = id;
                mDayWV.setData(getDayData());
            }

            @Override
            public void selecting(int id, String text) {
                mYear = id;
            }
        });

        mMonthWV.setOnSelectListener(new WheelView.OnSelectListener() {
            @Override
            public void endSelect(int id, String text) {
                mMonth = id;
                mDayWV.setData(getDayData());
            }

            @Override
            public void selecting(int id, String text) {
                mMonth = id;
            }
        });

        setData();
    }


//    public void setOnResultListener(OnResultListener listener){
//        listener.onResult();
//    }
//
//    public interface OnResultListener{
//        void onResult();
//    }


//==============================私有函数，不向外暴露=============================================//

    /**
     * 设置初始的显示数据集
     */
    private void setData() {
        mYearWV.setData(getYearData(10));
        mMonthWV.setData(getMonthData());
        mDayWV.setData(getDayData());
        mHourWV.setData(getHourData());
        mMinuteWV.setData(getMinuteData());
        mSecondWV.setData(getSecondData());
        setDefaultData();
    }

    /**
     * 将当前时间设置为默认显示的item
     */
    private void setDefaultData() {
        mYearWV.setDefault(getCurrentDateIndex(Calendar.YEAR));
        mMonthWV.setDefault(getCurrentDateIndex(Calendar.MONTH));
        mDayWV.setDefault(getCurrentDateIndex(Calendar.DAY_OF_MONTH));
        mHourWV.setDefault(getCurrentDateIndex(Calendar.HOUR_OF_DAY));
        mMinuteWV.setDefault(getCurrentDateIndex(Calendar.MINUTE));
        mSecondWV.setDefault(getCurrentDateIndex(Calendar.SECOND));
    }

    private int getCurrentDateIndex(int typeOfWV) {
        int index = 0;
        switch (typeOfWV) {
            case Calendar.YEAR:
                index = mYearWV.getDataList().indexOf(int2String(mCalendar.get(typeOfWV)));
                break;
            case Calendar.MONTH:
                index = mMonthWV.getDataList().indexOf(int2String(mCalendar.get(typeOfWV) + 1));
                break;
            case Calendar.DAY_OF_MONTH:
                index = mDayWV.getDataList().indexOf(int2String(mCalendar.get(typeOfWV)));
                break;
            case Calendar.HOUR_OF_DAY:
                index = mHourWV.getDataList().indexOf(int2String(mCalendar.get(typeOfWV)));
                break;
            case Calendar.MINUTE:
                index = mMinuteWV.getDataList().indexOf(int2String(mCalendar.get(typeOfWV)));
                break;
            case Calendar.SECOND:
                index = mSecondWV.getDataList().indexOf(int2String(mCalendar.get(typeOfWV)));
                break;
        }
        return index;
    }

    private String int2String(int num) {
        String result = "";
        if (num < 10) {
            result = "0" + num;
        } else {
            result = num + "";
        }
        return result;
    }

    /**
     * 获得年份数据
     *
     * @param number 向上显示的年份数量
     * @return
     */
    private ArrayList<String> getYearData(int number) {
        ArrayList<String> result = new ArrayList<>();
        int nowYear = mCalendar.get(Calendar.YEAR);
        for (int i = number - 1; i >= 0; i--) {
            result.add((nowYear - i) + "");
        }
        return result;
    }

    /**
     * 判断是否为闰年
     *
     * @param yearStr
     * @return
     */
    private boolean isLeapYear(String yearStr) {
        int year = Integer.parseInt(yearStr);
        if (((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0)) {
            return true;
        }
        return false;
    }

    /**
     * 设置月份，天，时分秒
     *
     * @param list
     * @param start
     * @param end
     */
    private void setListData(ArrayList<String> list, int start, int end) {
        for (int i = start; i <= end; i++) {
            if (i < 10) {
                list.add("0" + i);
            } else {
                list.add("" + i);
            }
        }
    }

    /**
     * 得到月份的数据
     *
     * @return
     */
    private ArrayList<String> getMonthData() {
        ArrayList<String> result = new ArrayList<>();
        setListData(result, 1, 12);
        return result;
    }

    /**
     * 根据年份，月份来获得天数的数据
     *
     * @return
     */
    private ArrayList<String> getDayData() {
        ArrayList<String> result = new ArrayList<>();
        switch (mMonth + 1) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                setListData(result, 1, 31);
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                setListData(result, 1, 30);
                break;
            case 2:
                int dayNum = isLeapYear(mYearWV.getItemText(mYear)) ? 29 : 28;
                setListData(result, 1, dayNum);
                break;
            default:
                break;
        }
        return result;
    }

    /**
     * 获得小时数据
     *
     * @return
     */
    private ArrayList<String> getHourData() {
        ArrayList<String> result = new ArrayList<>();
        setListData(result, 0, 23);
        return result;
    }

    /**
     * 获得分钟数据
     *
     * @return
     */
    private ArrayList<String> getMinuteData() {
        ArrayList<String> result = new ArrayList<>();
        setListData(result, 0, 59);
        return result;
    }

    /**
     * 获得秒钟数据
     *
     * @return
     */
    private ArrayList<String> getSecondData() {
        ArrayList<String> result = new ArrayList<>();
        setListData(result, 0, 59);
        return result;
    }

}
