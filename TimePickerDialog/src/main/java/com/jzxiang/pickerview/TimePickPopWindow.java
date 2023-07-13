package com.jzxiang.pickerview;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jzxiang.pickerview.config.PickerConfig;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.data.WheelCalendar;
import com.jzxiang.pickerview.listener.OnDateSetListener;

import java.util.Calendar;

/**
 * Author: PL
 * Date: 2023/7/13
 * Desc:
 */
public class TimePickPopWindow extends PopupWindow implements View.OnClickListener {
    private final Context mContext;
    private final LayoutInflater mInflater;
    private final PickerConfig mPickerConfig;
    private TimeWheel mTimeWheel;
    private long mCurrentMillSeconds;

    private static TimePickPopWindow create(Context context, PickerConfig pickerConfig) {
        TimePickPopWindow timePickPopWindow = new TimePickPopWindow(context, pickerConfig);
        return timePickPopWindow;
    }


    public TimePickPopWindow(Context context, PickerConfig pickerConfig) {
        super(context);
        this.mContext = context;
        mPickerConfig = pickerConfig == null ? new PickerConfig() : pickerConfig;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //设置View
        setContentView(initView());
        //设置宽与高
        setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
//        setAnimationStyle(R.style.MyPopupWindow);
        //设置背景只有设置了这个才可以点击外边和BACK消失
        setBackgroundDrawable(new ColorDrawable());
        setFocusable(true);
        setOutsideTouchable(true);
        setTouchable(true);
        setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 判断是不是点击了外部
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    return true;
                }
                return false;
            }
        });
    }

    private View initView() {
        View view = mInflater.inflate(R.layout.timepicker_layout_pop, null);
        TextView cancel = (TextView) view.findViewById(R.id.tv_cancel);
        cancel.setOnClickListener(this);
        TextView sure = (TextView) view.findViewById(R.id.tv_sure);
        sure.setOnClickListener(this);
        TextView reset = (TextView) view.findViewById(R.id.tv_reset);
        reset.setOnClickListener(this);
        TextView title = (TextView) view.findViewById(R.id.tv_title);
        View toolbar = view.findViewById(R.id.toolbar);

        title.setText(mPickerConfig.mTitleString);
        cancel.setText(mPickerConfig.mCancelString);
        sure.setText(mPickerConfig.mSureString);
        reset.setText(mPickerConfig.mResetString);

        title.setTextColor(mPickerConfig.mToolBarTVColor);
        cancel.setTextColor(mPickerConfig.mToolBarTVColor);
        sure.setTextColor(mPickerConfig.mToolBarTVColor);
        reset.setTextColor(mPickerConfig.mToolBarTVColor);

        toolbar.setPadding(mPickerConfig.mToolBarPadding, 0, mPickerConfig.mToolBarPadding, 0);
        toolbar.setBackgroundColor(mPickerConfig.mThemeColor);

        mTimeWheel = new TimeWheel(view, mPickerConfig);
        return view;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.tv_cancel) {
            dismiss();
        } else if (i == R.id.tv_sure) {
            sureClicked();
        } else if (i == R.id.tv_reset) {
            if (mPickerConfig.mCallBack != null) {
                mPickerConfig.mCallBack.onDateReSet();
            }
            dismiss();
        }
    }

    /*
     * @desc This method returns the current milliseconds. If current milliseconds is not set,
     *       this will return the system milliseconds.
     * @param none
     * @return long - the current milliseconds.
     */
    public long getCurrentMillSeconds() {
        if (mCurrentMillSeconds == 0)
            return System.currentTimeMillis();

        return mCurrentMillSeconds;
    }

    /*
     * @desc This method is called when onClick method is invoked by sure button. A Calendar instance is created and
     *       initialized.
     * @param none
     * @return none
     */
    void sureClicked() {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();

        calendar.set(Calendar.YEAR, mTimeWheel.getCurrentYear());
        calendar.set(Calendar.MONTH, mTimeWheel.getCurrentMonth() - 1);
        calendar.set(Calendar.DAY_OF_MONTH, mTimeWheel.getCurrentDay());
        calendar.set(Calendar.HOUR_OF_DAY, mTimeWheel.getCurrentHour());
        calendar.set(Calendar.MINUTE, mTimeWheel.getCurrentMinute());

        mCurrentMillSeconds = calendar.getTimeInMillis();
        if (mPickerConfig.mCallBack != null) {
            mPickerConfig.mCallBack.onDateSet(mCurrentMillSeconds);
        }
        dismiss();
    }

    public static class Builder {
        PickerConfig mPickerConfig;

        public Builder() {
            mPickerConfig = new PickerConfig();
        }

        public TimePickPopWindow.Builder setType(Type type) {
            mPickerConfig.mType = type;
            return this;
        }

        public TimePickPopWindow.Builder setThemeColor(int color) {
            mPickerConfig.mThemeColor = color;
            return this;
        }

        public TimePickPopWindow.Builder setCancelStringId(String left) {
            mPickerConfig.mCancelString = left;
            return this;
        }

        public TimePickPopWindow.Builder setSureStringId(String right) {
            mPickerConfig.mSureString = right;
            return this;
        }

        public TimePickPopWindow.Builder setResetStringId(String reset) {
            mPickerConfig.mResetString = reset;
            return this;
        }

        public TimePickPopWindow.Builder setTitleStringId(String title) {
            mPickerConfig.mTitleString = title;
            return this;
        }

        public TimePickPopWindow.Builder setToolBarTextColor(int color) {
            mPickerConfig.mToolBarTVColor = color;
            return this;
        }

        public TimePickPopWindow.Builder setToolBarPadding(int padding) {
            mPickerConfig.mToolBarPadding = padding;
            return this;
        }

        public TimePickPopWindow.Builder setWheelItemTextNormalColor(int color) {
            mPickerConfig.mWheelTVNormalColor = color;
            return this;
        }

        public TimePickPopWindow.Builder setWheelItemTextSelectorColor(int color) {
            mPickerConfig.mWheelTVSelectorColor = color;
            return this;
        }

        public TimePickPopWindow.Builder setWheelItemTextSize(int size) {
            mPickerConfig.mWheelTVSize = size;
            return this;
        }

        public TimePickPopWindow.Builder setCyclic(boolean cyclic) {
            mPickerConfig.cyclic = cyclic;
            return this;
        }

        public TimePickPopWindow.Builder setMinMillseconds(long millseconds) {
            mPickerConfig.mMinCalendar = new WheelCalendar(millseconds);
            return this;
        }

        public TimePickPopWindow.Builder setMaxMillseconds(long millseconds) {
            mPickerConfig.mMaxCalendar = new WheelCalendar(millseconds);
            return this;
        }

        public TimePickPopWindow.Builder setCurrentMillseconds(long millseconds) {
            mPickerConfig.mCurrentCalendar = new WheelCalendar(millseconds);
            return this;
        }

        public TimePickPopWindow.Builder setYearText(String year) {
            mPickerConfig.mYear = year;
            return this;
        }

        public TimePickPopWindow.Builder setMonthText(String month) {
            mPickerConfig.mMonth = month;
            return this;
        }

        public TimePickPopWindow.Builder setDayText(String day) {
            mPickerConfig.mDay = day;
            return this;
        }

        public TimePickPopWindow.Builder setHourText(String hour) {
            mPickerConfig.mHour = hour;
            return this;
        }

        public TimePickPopWindow.Builder setMinuteText(String minute) {
            mPickerConfig.mMinute = minute;
            return this;
        }

        public TimePickPopWindow.Builder setCallBack(OnDateSetListener listener) {
            mPickerConfig.mCallBack = listener;
            return this;
        }

        public TimePickPopWindow build(Context context) {
            return create(context, mPickerConfig);
        }

    }
}
