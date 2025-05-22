package com.example.sparkchaindemo.entity;

/**
 * @author anjia
 */
import androidx.lifecycle.ViewModel;

import com.example.sparkchaindemo.fragment.SecondFragment;

import java.util.ArrayList;
import java.util.List;

public class ScheduleViewModel extends ViewModel {
    private List<SecondFragment.Schedule> scheduleList = new ArrayList<>();
    private boolean isDataLoaded = false;

    public List<SecondFragment.Schedule> getScheduleList() {
        return scheduleList;
    }

    public boolean isDataLoaded() {
        return isDataLoaded;
    }

    public void setDataLoaded(boolean dataLoaded) {
        isDataLoaded = dataLoaded;
    }

    // 添加日程的方法（可选）
    public void addSchedule(SecondFragment.Schedule schedule) {
        scheduleList.add(schedule);
    }
}
