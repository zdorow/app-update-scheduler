package com.app.update.scheduler.service;

import java.util.List;

import com.app.update.scheduler.eventhandler.JssApiResponseHandler;
import com.app.update.scheduler.jamfpro.api.JssApi;
import com.app.update.scheduler.option.AppUpdateSchedulerOption;
import com.app.update.scheduler.option.TimeFrame;
import com.app.update.scheduler.option.impl.EvenlySpreadSchedulerOption;
import com.app.update.scheduler.option.impl.TimeFrameSchedulerOption;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.text.Text;

public class TimeFrameSchedulerService extends Service<Boolean> {
	
	private Task<Boolean> scheduler;
	
	public TimeFrameSchedulerService(JssApi jssApi, List<Integer> appIdList, Text actiontarget, ComboBox<String> timeFrameStartOptions, ComboBox<String> timeFrameEndOptions, 
			AppUpdateSchedulerOption schedulerOption, ProgressBar progressBar) {
		
		switch (schedulerOption) {
		case EvenlySpread:
			scheduler = new EvenlySpreadSchedulerOption(jssApi, appIdList, actiontarget);
			break;
		case TimeInterval:
			TimeFrame timeFrameStart = TimeFrame.fromDisplayText(timeFrameStartOptions.getValue());
			TimeFrame timeFrameEnd = TimeFrame.fromDisplayText(timeFrameEndOptions.getValue());

			scheduler = new TimeFrameSchedulerOption(jssApi, appIdList, actiontarget, timeFrameStart, timeFrameEnd);
			break;
		default:
			break;
		}
		
		progressBar.progressProperty().bind(scheduler.progressProperty());
		
		scheduler.setOnSucceeded(ex -> {
			System.out.println("App Scheduling has completed.");
			
			actiontarget.setText("Done scheduling apps ");
		});
		
		scheduler.setOnFailed(new JssApiResponseHandler(jssApi.getLastResponseCode(), actiontarget));
	}

	@Override
	protected Task<Boolean> createTask() {
		return scheduler;
	}
}