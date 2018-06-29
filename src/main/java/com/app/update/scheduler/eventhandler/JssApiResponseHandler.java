package com.app.update.scheduler.eventhandler;

import com.app.update.scheduler.jamfpro.api.JssApi;

import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.text.Text;
import javafx.scene.control.Button;
import java.util.List;

public class JssApiResponseHandler implements EventHandler<WorkerStateEvent> {

	private JssApi jssApi;
	private Text actiontarget;
	private Button button;
	private List<Integer> appIdList;
	
	public JssApiResponseHandler(JssApi jssApi, Text actiontarget, Button button) {
		this.jssApi = jssApi;
		this.actiontarget = actiontarget;
		this.button = button;
	}
	
	public JssApiResponseHandler(JssApi jssApi, Text actiontarget, Button button, List<Integer> appIdList) {
		this.jssApi = jssApi;
		this.actiontarget = actiontarget;
		this.button = button;
		this.appIdList = appIdList;
	}
	
	@Override
	public void handle(WorkerStateEvent event) {
		switch (jssApi.getLastResponseCode()) {
		case 401:
			actiontarget.setText("Username and/or password not accepted.");
			button.setDisable(false);
			break;
		case 0:
			actiontarget.setText("URL was not found.");
			button.setDisable(false);
			break;
         case 200: 
            actiontarget.setText("Timeframe not selected or app updates are not enabled.");
            button.setDisable(false);
            break;
		default:                       
			actiontarget.setText("Something really went wrong. Please file an issue on Github.");
			button.setDisable(false);
			break;
		}
	}
}
