package com.app.update.scheduler.eventhandler;

import com.app.update.scheduler.jamfpro.api.JssApi;

import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

public class JssApiResponseHandler implements EventHandler<WorkerStateEvent> {

	private JssApi jssApi;
	private Text actiontarget;
	private Button button;
	
	public JssApiResponseHandler(JssApi jssApi, Text actiontarget, Button button) {
		this.jssApi = jssApi;
		this.actiontarget = actiontarget;
		this.button = button;
	}
	
	@Override
	public void handle(WorkerStateEvent event) {
		switch (jssApi.getLastResponseCode()) {
		case 401:
			actiontarget.setText("Username and/or password not accepted.");
			break;
		case 0:
			actiontarget.setText("URL was not found.");
			break;
         case 200: 
            actiontarget.setText("Timeframe not selected or app updates are not enabled.");
            break;
		default:                       
			actiontarget.setText("Something really went wrong. Please file an issue on Github.");
			break;
		}
		
		button.setDisable(false);
	}
}
