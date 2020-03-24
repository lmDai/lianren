package com.lianren.android.ui.dialog;

import com.contrarywind.interfaces.IPickerViewData;
import com.lianren.android.widget.invitetime.DateUtil;

import java.util.Date;

/**
 * @package: com.lianren.android.ui.dialog
 * @user:xhkj
 * @date:2020/1/21
 * @description:
 **/
public class InviteDateBean implements IPickerViewData {
    public Date date;

    @Override
    public String getPickerViewText() {
        return DateUtil.dateTostr(date, "HH:mm");
    }
}
