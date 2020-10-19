package com.example.memo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.memo.databinding.DialogTimeBinding;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class TimePickerFragment extends DialogFragment {
    private static final String ARG_DATE = "date";
    public static final String EXTRA_DATE = "extra_date";
    private DialogTimeBinding mDialogTimeBinding;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        mDialogTimeBinding = DialogTimeBinding.inflate(LayoutInflater.from(getActivity()));

        // 解析时间
        Date time = (Date) getArguments().getSerializable(ARG_DATE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        mDialogTimeBinding.dialogTimePicker.setHour(calendar.get(Calendar.HOUR_OF_DAY));
        mDialogTimeBinding.dialogTimePicker.setMinute(calendar.get(Calendar.MINUTE));
        mDialogTimeBinding.dialogTimePicker.setIs24HourView(true);
        mDialogTimeBinding.dialogTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                Toast.makeText(getActivity(), "时：" + hourOfDay + " 分：" + minute, Toast.LENGTH_SHORT).show();
            }
        });
        return new AlertDialog.Builder(getActivity())
                .setView(mDialogTimeBinding.getRoot())
                .setTitle("Time of Crime")
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH);
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    int hour = mDialogTimeBinding.dialogTimePicker.getHour();
                    int minute = mDialogTimeBinding.dialogTimePicker.getMinute();
                    Date date = new GregorianCalendar(year, month, day, hour, minute).getTime();
                    sendResult(Activity.RESULT_OK,date);
                })
                .create();
    }

    private void sendResult(int resultCode, Date date) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE, date);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }

    public static TimePickerFragment newInstance(Date date) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_DATE, date);
        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setCancelable(false);
        fragment.setArguments(bundle);
        return fragment;
    }
}
