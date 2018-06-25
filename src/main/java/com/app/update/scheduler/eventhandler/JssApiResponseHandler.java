package com.app.update.scheduler.eventhandler;

import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.text.Text;

public class JssApiResponseHandler implements EventHandler<WorkerStateEvent> {

	private Object jssApi;
	private Text actiontarget;

	
	public JssApiResponseHandler(Object jssApi, Text actiontarget) {
		this.jssApi = jssApi;
		this.actiontarget = actiontarget;
	}
	
	@Override
	public void handle(WorkerStateEvent event) {
            //Integer responseCode = 
		switch (0) {
		case 401:
			actiontarget.setText("Username and/or password not accepted.");
			break;
		case 0:
			actiontarget.setText("URL was not found.");
			break;
		default:
			actiontarget.setText("Something really went wrong. Please file an issue on Github1");
			break;
		}
	}
}
