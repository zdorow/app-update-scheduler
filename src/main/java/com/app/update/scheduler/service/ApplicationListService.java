package com.app.update.scheduler.service;

import java.util.List;

import com.app.update.scheduler.applicationlistget.ApplicationListGet;
import com.app.update.scheduler.jamfpro.api.JssApi;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressBar;
import javafx.scene.text.Text;

public class ApplicationListService extends Service<List<Integer>> {
	
	private ApplicationListGet applicationListGet;
	
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
