package com.app.update.scheduler.service;

import java.util.List;

import com.app.update.scheduler.option.AppUpdateSchedulerOption;
import com.app.update.scheduler.applicationlistget.ApplicationListGet;
import com.app.update.scheduler.eventhandler.JssApiResponseHandler;
import com.app.update.scheduler.jamfpro.api.JssApi;
import java.util.logging.Logger;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.text.Text;

public class ApplicationListService extends Service<List<Integer>> {
    private static final Logger LOG = Logger.getLogger(ApplicationListService.class.getName());
	
	private final ApplicationListGet applicationListGet;
	
	public ApplicationListService(JssApi jssApi, Text actiontarget, ProgressBar progressBar, Button button,
			ComboBox<String> timeFrameStartOptions, ComboBox<String> timeFrameEndOptions, AppUpdateSchedulerOption schedulerOption) {
		this.applicationListGet = new ApplicationListGet(jssApi, actiontarget, progressBar);
		
		progressBar.progressProperty().bind(applicationListGet.progressProperty());
		
		//applicationListGet.onSucceededProperty().bind(this.onSucceededProperty());
		applicationListGet.setOnSucceeded(e -> {
			System.out.println("ApplicationListService has succeeded.");

			List<Integer> appIdList = applicationListGet.getValue();
			System.out.println(appIdList);
			
			new TimeFrameSchedulerService(jssApi, appIdList, actiontarget, timeFrameStartOptions, timeFrameEndOptions,
										schedulerOption, progressBar, button).start();
			appIdList.removeAll(appIdList);
			button.setDisable(false);
		});
		
		applicationListGet.setOnFailed(new JssApiResponseHandler(jssApi, actiontarget, button));
	}

	@Override
	protected Task<List<Integer>> createTask() {
		return applicationListGet;
	}

	public ApplicationListGet getApplicationListGet() {
		return applicationListGet;
	}
	
}
