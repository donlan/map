/*
 *
 *   Copyright (C) 2017 author : 梁桂栋
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 *   Email me : stonelavender@hotmail.com
 *
 */

package dong.lan.mapeye.views.customsView;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ViewFlipper;

import java.util.Date;

import dong.lan.mapeye.R;


/**
 * Created by 梁桂栋 on 2016年09月02日 21:24.
 * Email:760625325@qq.com
 * GitHub: https://gitbub.com/donlan
 * description:
 */
public class DateTimePicker extends AlertDialog {

    private ViewFlipper switcher;
    private View view;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private Date date;

    private CallBack callBack;

    private int index = 0;


    public DateTimePicker(Context context, CallBack callBackListener) {
        super(context);
        this.callBack = callBackListener;
        date = new Date(System.currentTimeMillis());
        view = LayoutInflater.from(context).inflate(R.layout.date_time_picker, null);
        datePicker = (DatePicker) view.findViewById(R.id.datePicker);
        timePicker = (TimePicker) view.findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);
        datePicker.setMinDate(System.currentTimeMillis());
        datePicker.init(date.getYear() + 1900, date.getMonth(), date.getDate(), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {
                date.setYear(i - 1900);
                date.setMonth(i1);
                date.setDate(i2);
            }
        });
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                date.setHours(i);
                date.setMinutes(i1);
            }
        });
        TextView back = (TextView) view.findViewById(R.id.left);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateTimePicker.this.dismiss();
            }
        });
        TextView ok = (TextView) view.findViewById(R.id.right);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getTime(callBack);
                dismiss();
            }
        });
        switcher = (ViewFlipper) view.findViewById(R.id.switcher);
        RadioGroup dateCheck = (RadioGroup) view.findViewById(R.id.dateTimeCheck);
        dateCheck.setOnCheckedChangeListener(
                new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int i) {
                        if (i == R.id.dateCheck && index == 1) {
                            switcher.showPrevious();
                            index = 0;
                        } else if (i == R.id.timeCheck && index == 0) {
                            switcher.showNext();
                            index = 1;
                        }
                    }
                }
        );

        setView(view);
    }


    public DateTimePicker getTime(CallBack callBack) {
        callBack.onClose(date.getTime());
        return this;
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        view = null;
        datePicker = null;
        timePicker = null;
        switcher = null;
        date = null;
        callBack = null;
    }

    public interface CallBack {
        void onClose(long time);
    }
}
