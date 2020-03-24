package com.lianren.android.ui.dialog;

import android.app.Dialog;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.adapter.ArrayWheelAdapter;
import com.contrarywind.listener.OnItemSelectedListener;
import com.contrarywind.view.WheelView;
import com.lianren.android.R;
import com.lianren.android.base.BaseDialog;
import com.lianren.android.base.BaseDialogFragment;
import com.lianren.android.widget.invitetime.CalendarView;
import com.lianren.android.widget.invitetime.DateUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @package: com.lianren.android.ui.dialog
 * @user:xhkj
 * @date:2020/1/15
 * @description:
 **/
public class InviteTimeDialog {

    public static final class Builder
            extends BaseDialogFragment.Builder<Builder> implements View.OnClickListener {
        private boolean mAutoDismiss = true;
        private OnListener mListener;
        private CalendarView calendarDay;
        private WheelView wheelTime;
        private Button btnSubmit;
        private String s_time;
        private String e_time;
        private TextView tvTime;
        List<InviteDateBean> mList = new ArrayList<>();

        public Builder(FragmentActivity activity) {
            super(activity);
            setContentView(R.layout.dialog_invite_time);
            setAnimStyle(BaseDialog.AnimStyle.BOTTOM);
            setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
            setGravity(Gravity.BOTTOM);
            calendarDay = findViewById(R.id.calendar_day);
            wheelTime = findViewById(R.id.wheel_view);
            btnSubmit = findViewById(R.id.btnSubmit);
            btnSubmit.setOnClickListener(this);
            tvTime = findViewById(R.id.txt_time);
            calendarDay.setSTimeSelListener(new CalendarView.CalendarSTimeSelListener() {
                @Override
                public void onSTimeSelect(Date date) {
                    mList = new ArrayList<>();
                    String format = "yyyy/MM/dd HH:mm";
                    SimpleDateFormat sdf = new SimpleDateFormat(format);
                    Date dateObj1 = null;
                    Date dateObj2 = null;
                    try {
                        dateObj1 = sdf.parse(DateUtil.dateTostr(date, "yyyy/MM/dd") + " " + s_time);
                        dateObj2 = sdf.parse(DateUtil.dateTostr(date, "yyyy/MM/dd") + " " + e_time);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    long dif = dateObj1.getTime();
                    while (dif <= dateObj2.getTime()) {
                        Date slot = new Date(dif);
                        dif += 1800000;
                        InviteDateBean dateBean = new InviteDateBean();
                        dateBean.date = slot;
                        mList.add(dateBean);
                    }
                    wheelTime.setAdapter(new ArrayWheelAdapter(mList));// 设置显示数据
                    wheelTime.setCurrentItem(0);// 初始化时显示的数据

                    tvTime.setText(DateUtil.dateTostr(mList.get(wheelTime.getCurrentItem()).date, "MM月dd日 HH:mm"));
                }
            });
            wheelTime.setOnItemSelectedListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(int index) {
                    tvTime.setText(DateUtil.dateTostr(mList.get(wheelTime.getCurrentItem()).date, "MM月dd日 HH:mm"));
                }
            });
        }

        public Builder setListener(OnListener mListener) {
            this.mListener = mListener;
            return this;
        }

        public Builder setTimeLimit(String s_time, String e_time) {
            this.s_time = s_time;
            this.e_time = e_time;
            mList = new ArrayList<>();
            String format = "yyyy/MM/dd HH:mm";
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            Date dateObj1 = null;
            Date dateObj2 = null;
            try {
                dateObj1 = sdf.parse(DateUtil.dateTostr(new Date(), "yyyy/MM/dd") + " " + s_time);
                dateObj2 = sdf.parse(DateUtil.dateTostr(new Date(), "yyyy/MM/dd") + " " + e_time);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long dif = dateObj1.getTime();
            while (dif <= dateObj2.getTime()) {
                Date slot = new Date(dif);
                dif += 1800000;
                InviteDateBean dateBean = new InviteDateBean();
                dateBean.date = slot;
                mList.add(dateBean);
            }
            wheelTime.setAdapter(new ArrayWheelAdapter(mList));// 设置显示数据
            wheelTime.setCurrentItem(0);// 初始化时显示的数据
            tvTime.setText(DateUtil.dateTostr(mList.get(wheelTime.getCurrentItem()).date, "MM月dd日 HH:mm"));
            return this;
        }


        @Override
        public void onClick(View v) {
            if (mAutoDismiss) {
                dismiss();
            }
            if (v == btnSubmit) {
                if (mListener != null) {
                    mListener.onSelected(getDialog(), mList.get(wheelTime.getCurrentItem()).date);
                }
            }
        }

        @Override
        public BaseDialog show() {
            return super.show();
        }
    }

    public interface OnListener {

        /**
         * 选择条目时回调
         */
        void onSelected(Dialog dialog, Date date);

        /**
         * 点击取消时回调
         */
        void onCancel(Dialog dialog);
    }
}