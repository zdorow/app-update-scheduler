package com.app.update.scheduler.service;

import java.util.List;

import com.app.update.scheduler.applicationlistget.ApplicationListGet;
import com.app.update.scheduler.jamfpro.api.JssApi;
import java.util.logging.Logger;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressBar;
import javafx.scene.text.Text;

public class ApplicationListService extends Service<List<Integer>> {
    private static final Logger LOG = Logger.getLogger(ApplicationListService.class.getName());
	
	private final ApplicationListGet applicationListGet;
	
	public ApplicationListService(JssApi jssApi, Text actiontarget, ProgressBar progressBar) {
		this.applicationListGet = new ApplicationListGet(jssApi, actiontarget);
		
		progressBar.progressProperty().bind(applicationListGet.progressProperty());
		
		applicationListGet.onSucceededProperty().bind(this.onSucceededProperty());
	}

	@Override
	protected Task<List<Integer>> createTask() {
		return applicationListGet;
	}

	public ApplicationListGet getApplicationListGet() {
		return applicationListGet;
	}
	
}
