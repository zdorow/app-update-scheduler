package com.app.update.scheduler.option.impl;

import java.util.List;

import com.app.update.scheduler.jamfpro.api.JssApi;
import com.app.update.scheduler.jamfpro.api.JssApiException;
import com.app.update.scheduler.option.TimeFrame;

import javafx.concurrent.Task;
import javafx.scene.text.Text;

public class TimeFrameSchedulerOption extends Task<Boolean> {

    private static final String updateXml = "<mobile_device_application><general><itunes_sync_time>%d</itunes_sync_time></general></mobile_device_application>";

    private final JssApi jssApi;
    private final List<Integer> appIdList;
    private final Text actiontarget;
    private final TimeFrame timeFrameStart;
    private final TimeFrame timeFrameEnd;

    public TimeFrameSchedulerOption(JssApi jssApi, List<Integer> appIdList, Text actiontarget, TimeFrame timeFrameStart, TimeFrame timeFrameEnd) {
        this.jssApi = jssApi;
        this.appIdList = appIdList;
        this.actiontarget = actiontarget;
        this.timeFrameStart = timeFrameStart;
        this.timeFrameEnd = timeFrameEnd;
    }

    @Override
    protected Boolean call() throws Exception {

        actiontarget.setText("Calculating spread of application updates");

        try {
            int count = 0;
            double spreadBeforeMidnight, spreadAfterMidnight;
            double startTime = timeFrameStart.calculateNumberOfSecondsFromMidnight();
            double endTime = timeFrameEnd.calculateNumberOfSecondsFromMidnight();
            actiontarget.setText("Updating application update schedules");

            if (endTime < startTime) {
                // In this sequence we use the percentage of total time before midnight to allocate which apps 
                // will be updated before midnight. The percentage of time is based on the total time selected.
                System.out.println("Endtime less than start detected. Running compensation sequence");
                double midnight = 0.0;
                double timeUntilMidnight = 86400 - startTime;
                double calcualtePercentageList = timeUntilMidnight / (startTime + endTime);
                System.out.println("Percentage of list to be allocated before midnight" + calcualtePercentageList * 100);
                double percentListTotal = (Math.round(appIdList.size() * calcualtePercentageList));

                // This is where the two lists get broken up
                List<Integer> appIdListBeforeMidnight = appIdList.subList(0, (int) percentListTotal);
                List<Integer> appIdListAfterMidnight = appIdList.subList((int) percentListTotal, appIdList.size());

                //This is where the spread gets calculated.
                spreadBeforeMidnight = timeUntilMidnight / new Double(appIdListBeforeMidnight.size());
                spreadAfterMidnight = endTime / new Double(appIdListAfterMidnight.size());

                for (int id : appIdListBeforeMidnight) {
                    setSyncTime(id, startTime, count, spreadBeforeMidnight, appIdListBeforeMidnight,
                            "Setting iTunes sync time for apps that run before midnight: ");
                }
                count = 0;
                for (int id : appIdListAfterMidnight) {
                    setSyncTime(id, midnight, count, spreadAfterMidnight, appIdListAfterMidnight,
                            "Setting iTunes sync time for apps that run after midnight: ");
                }
            } else {
                double spread = endTime / new Double(appIdList.size());

                for (int id : appIdList) {
                    setSyncTime(id, startTime, count, spread, appIdList, "Setting iTunes sync time: ");
                }
            }
            updateProgress(1, 1);
            appIdList.clear();
        } catch (JssApiException e) {
            actiontarget.setText("There was an error while processing app updates.");
            return false;
        }
        return true;
    }

    public void setSyncTime(int id, double time, int count, double spread, List<Integer> idList, String messageText) throws JssApiException {
        jssApi.put("mobiledeviceapplications/id/" + id, String.format(updateXml, Math.round(time)));
        updateProgress(count, idList.size());
        count++;
        time += spread;
        Double percent = (double) count / idList.size() * 100;
        actiontarget.setText(messageText + percent.intValue() + "%");
    }
}
